package com.xuan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.context.VisitorContext;
import com.xuan.dto.*;
import com.xuan.entity.ArticleComments;
import com.xuan.entity.Articles;
import com.xuan.exception.ValidationException;
import com.xuan.mapper.ArticleCommentMapper;
import com.xuan.mapper.ArticleMapper;
import com.xuan.properties.WebsiteProperties;
import com.xuan.result.PageResult;
import com.xuan.service.*;
import com.xuan.utils.CommitActions;
import com.xuan.utils.DiscussionUtil;
import com.xuan.utils.IpUtil;
import com.xuan.vo.ArticleCommentQueryVO;
import com.xuan.vo.ArticleCommentVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

/**
 * 文章评论服务实现，管理审核计数、线程可见性及经验证访客的内容操作。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleCommentServiceImpl extends ServiceImpl<ArticleCommentMapper, ArticleComments> implements IArticleCommentService {
    private final ArticleMapper articleMapper;
    private final UserAgentService userAgentService;
    private final AsyncEmailService asyncEmailService;
    private final WebsiteProperties websiteProperties;
    private final CaptchaService captchaService;

    @Override
    public PageResult pageQuery(ArticleCommentPageQueryDTO dto) {
        var page = baseMapper.pageQueryWithArticleTitle(new Page<ArticleComments>(dto.getPage(), dto.getPageSize()), dto);
        page.getRecords().forEach(c -> c.setContentHtml(DiscussionUtil.html(c.getContent(), c.getIsMarkdown())));
        return PageResult.fromIPage(page);
    }

    @Override
    public List<ArticleComments> getByArticleId(Long articleId) {
        List<ArticleComments> comments = list(new LambdaQueryWrapper<ArticleComments>()
                .eq(ArticleComments::getArticleId, articleId).orderByDesc(ArticleComments::getCreateTime));
        comments.forEach(c -> c.setContentHtml(DiscussionUtil.html(c.getContent(), c.getIsMarkdown())));
        return comments;
    }

    @Override
    @Transactional
    public void batchApprove(List<Long> ids) {
        // Serialize state transitions with edits/deletes on the same article, without rewriting its counters.
        for (Long id : ids.stream().distinct().sorted().toList()) {
            ArticleComments c = lockedComment(id);
            if (c == null) continue;
            boolean changed = lambdaUpdate().eq(ArticleComments::getId, id).eq(ArticleComments::getIsApproved, 0)
                    .set(ArticleComments::getIsApproved, 1).set(ArticleComments::getUpdateTime, java.time.LocalDateTime.now()).update();
            if (changed) {
                articleMapper.addCommentCount(c.getArticleId(), 1);
                CommitActions.afterCommit(() -> notifyParent(c));
            }
        }
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        for (Long id : ids.stream().distinct().sorted().toList()) {
            ArticleComments c = lockedComment(id);
            if (c == null) continue;
            var scope = new LambdaQueryWrapper<ArticleComments>().eq(ArticleComments::getId, id);
            if (isRoot(c)) scope.or().eq(ArticleComments::getRootId, id);
            else {
                ArticleComments root = baseMapper.lockById(c.getRootId());
                if (root != null) {
                    var replies = lambdaUpdate().eq(ArticleComments::getParentId, id).eq(ArticleComments::getRootId, root.getId())
                            .set(ArticleComments::getParentId, root.getId()).set(ArticleComments::getParentNickname, root.getNickname());
                    if (Integer.valueOf(1).equals(c.getIsSecret())) replies.set(ArticleComments::getIsSecret, 1);
                    replies.update();
                }
            }
            List<ArticleComments> deleting = list(scope.last("FOR UPDATE"));
            long approved = deleting.stream().filter(v -> Integer.valueOf(1).equals(v.getIsApproved())).count();
            if (!deleting.isEmpty()) removeByIds(deleting.stream().map(ArticleComments::getId).toList());
            if (approved > 0) articleMapper.addCommentCount(c.getArticleId(), -approved);
        }
    }

    @Override
    @Transactional
    public void adminReply(ArticleCommentReplyDTO dto, HttpServletRequest request) {
        ArticleComments c = BeanUtil.copyProperties(dto, ArticleComments.class);
        lockArticle(c.getArticleId());
        bindParent(c, true);
        c.setNickname(websiteProperties.getTitle());
        c.setIsAdminReply(1);
        c.setIsApproved(1);
        c.setIsEdited(0);
        prepare(c, request);
        save(c);
        articleMapper.addCommentCount(c.getArticleId(), 1);
        CommitActions.afterCommit(() -> notifyParent(c));
    }

    @Override
    public List<ArticleCommentVO> getCommentTree(Long articleId, Long ignoredVisitorId) {
        return getTreePage(articleId, 1, 10).getRecords();
    }

    @Override
    public PageResult<ArticleCommentVO> getTreePage(Long articleId, int page, int pageSize) {
        checkPage(page, pageSize);
        Articles article = articleMapper.selectById(articleId);
        if (article == null || !Integer.valueOf(1).equals(article.getIsPublished())) {
            throw new ValidationException("文章不存在或未发布");
        }
        var roots = visible(articleId).and(w -> w.isNull(ArticleComments::getRootId).or().eq(ArticleComments::getRootId, 0))
                .orderByAsc(ArticleComments::getCreateTime, ArticleComments::getId);
        Page<ArticleComments> result = page(new Page<>(page, pageSize), roots);
        Map<Long, ArticleCommentVO> map = new LinkedHashMap<>();
        for (ArticleComments c : result.getRecords()) {
            ArticleCommentVO vo = convert(c);
            vo.setChildren(new ArrayList<>());
            map.put(c.getId(), vo);
        }
        if (!map.isEmpty()) {
            List<ArticleComments> children = list(visible(articleId).in(ArticleComments::getRootId, map.keySet())
                    .orderByAsc(ArticleComments::getCreateTime, ArticleComments::getId));
            Map<Long, List<ArticleComments>> byParent = children.stream().filter(c -> c.getParentId() != null).collect(java.util.stream.Collectors.groupingBy(ArticleComments::getParentId));
            var queue = new ArrayDeque<Long>(map.keySet());
            var seen = new HashSet<Long>(map.keySet());
            while (!queue.isEmpty()) {
                for (ArticleComments child : byParent.getOrDefault(queue.remove(), List.of())) {
                    if (!seen.add(child.getId())) continue;
                    queue.add(child.getId());
                }
            }
            // Emit in the original query order after resolving ancestor visibility.
            for (ArticleComments child : children) {
                if (seen.contains(child.getId())) map.get(child.getRootId()).getChildren().add(convert(child));
            }
        }
        return PageResult.of(new ArrayList<>(map.values()), result.getTotal(), page, pageSize);
    }

    /**
     * 构造当前请求访客可见的评论条件，匿名访客仅能读取公开且已审核内容。
     */
    private LambdaQueryWrapper<ArticleComments> visible(Long articleId) {
        Long visitorId = VisitorContext.getId();
        return new LambdaQueryWrapper<ArticleComments>().eq(ArticleComments::getArticleId, articleId)
                .and(w -> {
                    w.eq(ArticleComments::getIsApproved, 1);
                    if (visitorId != null) w.or().eq(ArticleComments::getVisitorId, visitorId);
                }).and(w -> {
                    w.isNull(ArticleComments::getIsSecret).or().eq(ArticleComments::getIsSecret, 0);
                    if (visitorId != null) w.or().eq(ArticleComments::getVisitorId, visitorId);
                });
    }

    @Override
    @Transactional
    public void submitComment(ArticleCommentDTO dto, HttpServletRequest request) {
        Long visitorId = VisitorContext.requireId();
        DiscussionUtil.email(dto.getEmailOrQq());
        lockArticle(dto.getArticleId());
        ArticleComments c = BeanUtil.copyProperties(dto, ArticleComments.class);
        c.setVisitorId(visitorId);
        bindParent(c, false);
        captchaService.verify(dto.getCaptchaId(), dto.getCaptchaAnswer());
        c.setIsApproved(0);
        c.setIsAdminReply(0);
        c.setIsEdited(0);
        prepare(c, request);
        save(c);
    }

    @Override
    @Transactional
    public void editComment(ArticleCommentEditDTO dto) {
        Long visitorId = VisitorContext.requireId();
        ArticleComments c = lockedComment(dto.getId());
        requireOwner(c, visitorId);
        int markdown = Integer.valueOf(1).equals(dto.getIsMarkdown()) ? 1 : 0;
        lambdaUpdate().eq(ArticleComments::getId, c.getId()).set(ArticleComments::getContent, dto.getContent())
                .set(ArticleComments::getContentHtml, DiscussionUtil.html(dto.getContent(), markdown))
                .set(ArticleComments::getIsMarkdown, markdown).set(ArticleComments::getIsEdited, 1)
                .set(ArticleComments::getIsApproved, 0).set(ArticleComments::getUpdateTime, java.time.LocalDateTime.now()).update();
        if (Integer.valueOf(1).equals(c.getIsApproved())) articleMapper.addCommentCount(c.getArticleId(), -1);
    }

    @Override
    @Transactional
    public void visitorDeleteComment(Long id, Long ignoredVisitorId) {
        Long visitorId = VisitorContext.requireId();
        requireOwner(lockedComment(id), visitorId);
        batchDelete(List.of(id));
    }

    /**
     * 按文章、评论顺序加锁，串行化审核、编辑及删除。
     * @param id 目标 ID
     * @return 锁定的评论，不存在时为 null
     */
    private ArticleComments lockedComment(Long id) {
        ArticleComments c = getById(id);
        if (c == null) return null;
        articleMapper.lockById(c.getArticleId());
        return baseMapper.lockById(id);
    }

    /**
     * 锁定可评论的已发布文章。
     * @param id 文章 ID
     * @throws ValidationException 文章不存在或未发布
     */
    private void lockArticle(Long id) {
        Articles article = articleMapper.lockById(id);
        if (article == null || !Integer.valueOf(1).equals(article.getIsPublished())) {
            throw new ValidationException("文章不存在或未发布");
        }
    }

    /**
     * 锁定并验证回复关系；访客必须可见完整祖先链，管理员回复继承私密归属。
     * @param c 待保存的回复
     * @param admin 是否为管理员回复
     * @throws ValidationException 关系不一致、祖先缺失或目标不可见
     */
    private void bindParent(ArticleComments c, boolean admin) {
        if (c.getParentId() == null || c.getParentId() == 0) {
            if (c.getRootId() != null && c.getRootId() != 0) throw new ValidationException("回复关系不一致");
            c.setRootId(null); c.setParentId(null); c.setParentNickname(null);
            return;
        }
        ArticleComments parent = baseMapper.lockById(c.getParentId());
        if (parent == null || !Objects.equals(parent.getArticleId(), c.getArticleId())) {
            throw new ValidationException("回复目标不存在或不属于该文章");
        }
        Long rootId = isRoot(parent) ? parent.getId() : parent.getRootId();
        ArticleComments root = baseMapper.lockById(rootId);
        if (root == null || !isRoot(root) || !Objects.equals(root.getArticleId(), c.getArticleId())) {
            throw new ValidationException("回复根节点不存在");
        }
        if (c.getRootId() != null && c.getRootId() != 0 && !rootId.equals(c.getRootId())) {
            throw new ValidationException("回复根节点不一致");
        }
        ArticleComments ancestor = parent;
        Set<Long> seen = new HashSet<>();
        while (!rootId.equals(ancestor.getId())) {
            if (!seen.add(ancestor.getId()) || !rootId.equals(ancestor.getRootId())
                    || !Objects.equals(c.getArticleId(), ancestor.getArticleId())) {
                throw new ValidationException("回复关系不一致");
            }
            if (!admin) requireVisible(ancestor);
            if (Integer.valueOf(1).equals(ancestor.getIsSecret())) {
                c.setIsSecret(1);
                if (admin) c.setVisitorId(ancestor.getVisitorId());
            }
            ancestor = baseMapper.lockById(ancestor.getParentId());
            if (ancestor == null) throw new ValidationException("回复祖先不存在");
        }
        if (!admin) requireVisible(root);
        if (Integer.valueOf(1).equals(root.getIsSecret())) {
            c.setIsSecret(1);
            if (admin) c.setVisitorId(root.getVisitorId());
        }
        c.setRootId(rootId);
        c.setParentNickname(parent.getNickname());
    }

    /**
     * 要求当前访客可见回复祖先，避免通过已知 ID 回复隐藏内容。
     * @param c 已锁定的祖先节点
     * @throws ValidationException 节点未审核或私密且不属于当前访客
     */
    private void requireVisible(ArticleComments c) {
        boolean own = Objects.equals(c.getVisitorId(), VisitorContext.requireId());
        if ((!Integer.valueOf(1).equals(c.getIsApproved()) || Integer.valueOf(1).equals(c.getIsSecret())) && !own) {
            throw new ValidationException("回复目标不可见");
        }
    }

    /**
     * 校验内容属于当前访客，管理员回复不能由访客编辑或删除。
     * @param c 目标内容，可为 null
     * @param visitorId 已验证访客 ID
     * @throws ValidationException 内容不存在或无权修改
     */
    private void requireOwner(ArticleComments c, Long visitorId) {
        if (c == null || !visitorId.equals(c.getVisitorId()) || Integer.valueOf(1).equals(c.getIsAdminReply())) {
            throw new ValidationException("内容不存在或无权修改");
        }
    }

    /**
     * 安全渲染内容，并从请求补充地点与一次解析的设备信息。
     * @param c 待保存内容
     * @param request 当前请求，可为 null
     */
    private void prepare(ArticleComments c, HttpServletRequest request) {
        c.setContentHtml(DiscussionUtil.html(c.getContent(), c.getIsMarkdown()));
        if (request == null) return;
        c.setLocation(DiscussionUtil.location(IpUtil.getGeoInfo(IpUtil.getClientIp(request))));
        String ua = request.getHeader("User-Agent");
        UserAgentService.Info device = userAgentService.parse(ua);
        c.setUserAgentOs(device.os());
        c.setUserAgentBrowser(device.browser());
    }

    /**
     * 安全渲染历史内容并组装公开视图；联系方式不进入公开 VO。
     * @param c 数据库记录
     * @return 包含服务端归属标记的视图
     */
    private ArticleCommentVO convert(ArticleComments c) {
        ArticleCommentVO vo = BeanUtil.copyProperties(c, ArticleCommentVO.class);
        vo.setContentHtml(DiscussionUtil.html(c.getContent(), c.getIsMarkdown()));
        vo.setIsOwn(VisitorContext.getId() != null && VisitorContext.getId().equals(c.getVisitorId())
                && !Integer.valueOf(1).equals(c.getIsAdminReply()));
        return vo;
    }

    /**
     * 成功提交后通知开启邮件提醒的父节点作者，通知故障仅记录日志。
     * @param reply 已审核回复
     */
    private void notifyParent(ArticleComments reply) {
        if (reply.getParentId() == null) return;
        try {
            ArticleComments parent = getById(reply.getParentId());
            if (parent == null || !Integer.valueOf(1).equals(parent.getIsNotice())) return;
            String email = DiscussionUtil.email(parent.getEmailOrQq());
            if (email == null) return;
            asyncEmailService.sendReplyNotificationAsync(email, parent.getNickname(), parent.getContent(),
                    reply.getNickname(), reply.getContent(), "comment");
        } catch (Exception ex) {
            log.error("发送评论通知失败: parentId={}", reply.getParentId(), ex);
        }
    }

    private boolean isRoot(ArticleComments c) { return c.getRootId() == null || c.getRootId() == 0; }
    private void checkPage(int page, int size) {
        if (page < 1 || size < 1 || size > 50) throw new ValidationException("分页范围不正确，page>=1，pageSize=1..50");
    }
    @Override public Integer countTotal() { return Math.toIntExact(count()); }
    @Override public Integer countPending() { return Math.toIntExact(lambdaQuery().eq(ArticleComments::getIsApproved, 0).count()); }
}
