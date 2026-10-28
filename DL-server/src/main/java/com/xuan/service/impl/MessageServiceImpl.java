package com.xuan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.context.VisitorContext;
import com.xuan.dto.*;
import com.xuan.entity.Messages;
import com.xuan.exception.ValidationException;
import com.xuan.mapper.MessageMapper;
import com.xuan.properties.WebsiteProperties;
import com.xuan.result.PageResult;
import com.xuan.service.*;
import com.xuan.utils.CommitActions;
import com.xuan.utils.DiscussionUtil;
import com.xuan.utils.IpUtil;
import com.xuan.vo.MessageQueryVO;
import com.xuan.vo.MessageVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

/**
 * 留言服务实现，管理审核、线程可见性及经验证访客的内容操作。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Messages> implements IMessageService {
    private final UserAgentService userAgentService;
    private final AsyncEmailService asyncEmailService;
    private final WebsiteProperties websiteProperties;
    private final CaptchaService captchaService;

    @Override
    public PageResult pageQuery(MessagePageQueryDTO dto) {
        var query = new LambdaQueryWrapper<Messages>()
                .eq(dto.getIsApproved() != null, Messages::getIsApproved, dto.getIsApproved())
                .ge(dto.getStartTime() != null, Messages::getCreateTime, dto.getStartTime())
                .le(dto.getEndTime() != null, Messages::getCreateTime, dto.getEndTime())
                .orderByDesc(Messages::getCreateTime);
        Page<Messages> result = page(new Page<>(dto.getPage(), dto.getPageSize()), query);
        List<MessageQueryVO> records = result.getRecords().stream().map(c -> {
            MessageQueryVO vo = BeanUtil.copyProperties(c, MessageQueryVO.class);
            vo.setContentHtml(DiscussionUtil.html(c.getContent(), c.getIsMarkdown()));
            return vo;
        }).toList();
        return PageResult.of(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional
    public void batchApprove(List<Long> ids) {
        for (Long id : ids.stream().distinct().sorted().toList()) {
            Messages c = lockedComment(id);
            if (c == null) continue;
            boolean changed = lambdaUpdate().eq(Messages::getId, id).eq(Messages::getIsApproved, 0)
                    .set(Messages::getIsApproved, 1).set(Messages::getUpdateTime, java.time.LocalDateTime.now()).update();
            if (changed) {
                CommitActions.afterCommit(() -> notifyParent(c));
            }
        }
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        for (Long id : ids.stream().distinct().sorted().toList()) {
            Messages c = lockedComment(id);
            if (c == null) continue;
            var scope = new LambdaQueryWrapper<Messages>().eq(Messages::getId, id);
            if (isRoot(c)) scope.or().eq(Messages::getRootId, id);
            else {
                Messages root = baseMapper.lockById(c.getRootId());
                if (root != null) {
                    var replies = lambdaUpdate().eq(Messages::getParentId, id).eq(Messages::getRootId, root.getId())
                            .set(Messages::getParentId, root.getId()).set(Messages::getParentNickname, root.getNickname());
                    if (Integer.valueOf(1).equals(c.getIsSecret())) replies.set(Messages::getIsSecret, 1);
                    replies.update();
                }
            }
            remove(scope);
        }
    }

    @Override
    @Transactional
    public void adminReply(MessageReplyDTO dto, HttpServletRequest request) {
        Messages c = BeanUtil.copyProperties(dto, Messages.class);
        bindParent(c, true);
        c.setNickname(websiteProperties.getTitle());
        c.setIsAdminReply(1);
        c.setIsApproved(1);
        c.setIsEdited(0);
        prepare(c, request);
        save(c);
        CommitActions.afterCommit(() -> notifyParent(c));
    }

    @Override
    public List<MessageVO> getMessageTree(Long ignoredVisitorId) {
        return getTreePage(1, 10).getRecords();
    }

    @Override
    public PageResult<MessageVO> getTreePage(int page, int pageSize) {
        checkPage(page, pageSize);
        var roots = visible().and(w -> w.isNull(Messages::getRootId).or().eq(Messages::getRootId, 0))
                .orderByAsc(Messages::getCreateTime, Messages::getId);
        Page<Messages> result = page(new Page<>(page, pageSize), roots);
        Map<Long, MessageVO> map = new LinkedHashMap<>();
        for (Messages c : result.getRecords()) {
            MessageVO vo = convert(c);
            vo.setChildren(new ArrayList<>());
            map.put(c.getId(), vo);
        }
        if (!map.isEmpty()) {
            List<Messages> children = list(visible().in(Messages::getRootId, map.keySet())
                    .orderByAsc(Messages::getCreateTime, Messages::getId));
            Map<Long, List<Messages>> byParent = children.stream().filter(c -> c.getParentId() != null).collect(java.util.stream.Collectors.groupingBy(Messages::getParentId));
            var queue = new ArrayDeque<Long>(map.keySet());
            var seen = new HashSet<Long>(map.keySet());
            while (!queue.isEmpty()) {
                for (Messages child : byParent.getOrDefault(queue.remove(), List.of())) {
                    if (!seen.add(child.getId())) continue;
                    queue.add(child.getId());
                }
            }
            // Emit in the original query order after resolving ancestor visibility.
            for (Messages child : children) {
                if (seen.contains(child.getId())) map.get(child.getRootId()).getChildren().add(convert(child));
            }
        }
        return PageResult.of(new ArrayList<>(map.values()), result.getTotal(), page, pageSize);
    }

    /**
     * 构造当前请求访客可见的留言条件，匿名访客仅能读取公开且已审核内容。
     */
    private LambdaQueryWrapper<Messages> visible() {
        Long visitorId = VisitorContext.getId();
        return new LambdaQueryWrapper<Messages>()
                .and(w -> {
                    w.eq(Messages::getIsApproved, 1);
                    if (visitorId != null) w.or().eq(Messages::getVisitorId, visitorId);
                }).and(w -> {
                    w.isNull(Messages::getIsSecret).or().eq(Messages::getIsSecret, 0);
                    if (visitorId != null) w.or().eq(Messages::getVisitorId, visitorId);
                });
    }

    @Override
    @Transactional
    public void submitMessage(MessageDTO dto, HttpServletRequest request) {
        Long visitorId = VisitorContext.requireId();
        DiscussionUtil.email(dto.getEmailOrQq());
        Messages c = BeanUtil.copyProperties(dto, Messages.class);
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
    public void editMessage(MessageEditDTO dto) {
        Long visitorId = VisitorContext.requireId();
        Messages c = lockedComment(dto.getId());
        requireOwner(c, visitorId);
        int markdown = Integer.valueOf(1).equals(dto.getIsMarkdown()) ? 1 : 0;
        lambdaUpdate().eq(Messages::getId, c.getId()).set(Messages::getContent, dto.getContent())
                .set(Messages::getContentHtml, DiscussionUtil.html(dto.getContent(), markdown))
                .set(Messages::getIsMarkdown, markdown).set(Messages::getIsEdited, 1)
                .set(Messages::getIsApproved, 0).set(Messages::getUpdateTime, java.time.LocalDateTime.now()).update();
    }

    @Override
    @Transactional
    public void visitorDeleteMessage(Long id, Long ignoredVisitorId) {
        Long visitorId = VisitorContext.requireId();
        requireOwner(lockedComment(id), visitorId);
        batchDelete(List.of(id));
    }

    /**
     * 按根留言、目标留言顺序加锁，串行化审核、编辑及删除。
     * @param id 目标 ID
     * @return 锁定的留言，不存在时为 null
     */
    private Messages lockedComment(Long id) {
        Messages c = getById(id);
        if (c == null) return null;
        baseMapper.lockById(isRoot(c) ? c.getId() : c.getRootId());
        return baseMapper.lockById(id);
    }

    /**
     * 锁定并验证回复关系；访客必须可见完整祖先链，管理员回复继承私密归属。
     * @param c 待保存的回复
     * @param admin 是否为管理员回复
     * @throws ValidationException 关系不一致、祖先缺失或目标不可见
     */
    private void bindParent(Messages c, boolean admin) {
        if (c.getParentId() == null || c.getParentId() == 0) {
            if (c.getRootId() != null && c.getRootId() != 0) throw new ValidationException("回复关系不一致");
            c.setRootId(null); c.setParentId(null); c.setParentNickname(null);
            return;
        }
        Messages parent = getById(c.getParentId());
        if (parent == null) {
            throw new ValidationException("回复目标不存在或不属于该文章");
        }
        Long rootId = isRoot(parent) ? parent.getId() : parent.getRootId();
        Messages root = baseMapper.lockById(rootId);
        parent = baseMapper.lockById(c.getParentId());
        if (parent == null) throw new ValidationException("回复目标不存在");
        if (root == null || !isRoot(root)) {
            throw new ValidationException("回复根节点不存在");
        }
        if (c.getRootId() != null && c.getRootId() != 0 && !rootId.equals(c.getRootId())) {
            throw new ValidationException("回复根节点不一致");
        }
        Messages ancestor = parent;
        Set<Long> seen = new HashSet<>();
        while (!rootId.equals(ancestor.getId())) {
            if (!seen.add(ancestor.getId()) || !rootId.equals(ancestor.getRootId())) {
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
    private void requireVisible(Messages c) {
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
    private void requireOwner(Messages c, Long visitorId) {
        if (c == null || !visitorId.equals(c.getVisitorId()) || Integer.valueOf(1).equals(c.getIsAdminReply())) {
            throw new ValidationException("内容不存在或无权修改");
        }
    }

    /**
     * 安全渲染内容，并从请求补充地点与一次解析的设备信息。
     * @param c 待保存内容
     * @param request 当前请求，可为 null
     */
    private void prepare(Messages c, HttpServletRequest request) {
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
    private MessageVO convert(Messages c) {
        MessageVO vo = BeanUtil.copyProperties(c, MessageVO.class);
        vo.setContentHtml(DiscussionUtil.html(c.getContent(), c.getIsMarkdown()));
        vo.setIsOwn(VisitorContext.getId() != null && VisitorContext.getId().equals(c.getVisitorId())
                && !Integer.valueOf(1).equals(c.getIsAdminReply()));
        return vo;
    }

    /**
     * 成功提交后通知开启邮件提醒的父节点作者，通知故障仅记录日志。
     * @param reply 已审核回复
     */
    private void notifyParent(Messages reply) {
        if (reply.getParentId() == null) return;
        try {
            Messages parent = getById(reply.getParentId());
            if (parent == null || !Integer.valueOf(1).equals(parent.getIsNotice())) return;
            String email = DiscussionUtil.email(parent.getEmailOrQq());
            if (email == null) return;
            asyncEmailService.sendReplyNotificationAsync(email, parent.getNickname(), parent.getContent(),
                    reply.getNickname(), reply.getContent(), "message");
        } catch (Exception ex) {
            log.error("发送留言通知失败: parentId={}", reply.getParentId(), ex);
        }
    }

    private boolean isRoot(Messages c) { return c.getRootId() == null || c.getRootId() == 0; }
    private void checkPage(int page, int size) {
        if (page < 1 || size < 1 || size > 50) throw new ValidationException("分页范围不正确，page>=1，pageSize=1..50");
    }
    @Override public Integer countTotal() { return Math.toIntExact(count()); }
    @Override public Integer countPending() { return Math.toIntExact(lambdaQuery().eq(Messages::getIsApproved, 0).count()); }
}
