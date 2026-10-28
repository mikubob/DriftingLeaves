package com.xuan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.annotation.EvictArticleCaches;
import com.xuan.constant.MessageConstant;
import com.xuan.constant.RedisConstant;
import com.xuan.constant.StatusConstant;
import com.xuan.dto.ArticleDTO;
import com.xuan.dto.ArticlePageQueryDTO;
import com.xuan.dto.ArticleTitleViewCountDTO;
import com.xuan.entity.ArticleComments;
import com.xuan.entity.ArticleLikes;
import com.xuan.entity.ArticleTags;
import com.xuan.entity.Articles;
import com.xuan.entity.RssSubscriptions;
import com.xuan.exception.ArticleException;
import com.xuan.exception.ValidationException;
import com.xuan.mapper.ArticleCategoryMapper;
import com.xuan.mapper.ArticleCommentMapper;
import com.xuan.mapper.ArticleCountReader;
import com.xuan.mapper.ArticleLikeMapper;
import com.xuan.mapper.ArticleMapper;
import com.xuan.properties.WebsiteProperties;
import com.xuan.result.PageResult;
import com.xuan.service.AsyncEmailService;
import com.xuan.service.IArticleService;
import com.xuan.service.IArticleTagService;
import com.xuan.service.IRssSubscriptionService;
import com.xuan.utils.CommitActions;
import com.xuan.utils.MarkdownUtil;
import com.xuan.vo.ArticleArchiveItemVO;
import com.xuan.vo.ArticleArchiveVO;
import com.xuan.vo.ArticleVO;
import com.xuan.vo.BlogArticleDetailVO;
import com.xuan.vo.BlogArticleVO;
import com.xuan.vo.HotArticleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 文章服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Articles> implements IArticleService {

    private final IArticleTagService articleTagService;
    private final IRssSubscriptionService rssSubscriptionService;
    private final WebsiteProperties websiteProperties;
    private final AsyncEmailService asyncEmailService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ArticleCategoryMapper categoryMapper;
    private final ArticleCommentMapper commentMapper;
    private final ArticleLikeMapper likeMapper;
    private final ArticleCountReader countReader;

    //分钟阅读量：300字/分钟
    private static final int VIEWS = 300;

    /**
     * 分页查询文章
     *
     * @param articlePageQueryDTO 文章分页查询参数
     * @return 分页结果
     */
    @Override
    public PageResult<ArticleVO> pageQuery(ArticlePageQueryDTO articlePageQueryDTO) {
        // 构建管理端文章列表分页对象，列表不返回正文内容，避免管理列表接口传输过重。
        Page<ArticleVO> page = new Page<>(articlePageQueryDTO.getPage(), articlePageQueryDTO.getPageSize());

        // 管理端列表需要展示分类名称，因此通过自定义 SQL 关联分类表补齐 categoryName。
        IPage<ArticleVO> articlePage = baseMapper.pageQueryWithCategory(page, articlePageQueryDTO);

        // 转换为统一分页响应结构，保持前端现有 records / total 读取方式不变。
        return PageResult.fromIPage(articlePage);
    }

    /**
     * 创建文章、初始化统计及关联标签，首次发布成功提交后发送订阅通知。
     * @param articleDTO 文章数据，客户端 ID 被忽略
     * @return 数据库生成的文章 ID
     */
    @Override
    @Transactional
    @EvictArticleCaches
    public Long createArticle(ArticleDTO articleDTO) {
        articleDTO.setId(null);
        validateRelations(articleDTO);
        Articles articles = BeanUtil.copyProperties(articleDTO, Articles.class);

        boolean firstPublishNow = StatusConstant.ENABLE.equals(articleDTO.getIsPublished());

        //1.判断前端是否进行了md->html的渲染，如果没有则后端进行转换
        if (StrUtil.isNotBlank(articles.getContentHtml())) {
            articles.setContentHtml(MarkdownUtil.sanitize(articleDTO.getContentHtml()));
        } else {
            String rawContent = articleDTO.getContentMarkdown();
            String contentHtml = MarkdownUtil.isHtml(rawContent)
                    ? MarkdownUtil.sanitize(rawContent)
                    : MarkdownUtil.toHtml(rawContent);
            articles.setContentHtml(contentHtml);
        }

        //2.计算字数得阅读的时间
        String plainText = articleDTO.getContentMarkdown();
        long wordCount = countWords(plainText);
        long readingTime = Math.max(1, wordCount / VIEWS);
        articles.setWordCount(wordCount);
        articles.setReadingTime(readingTime);

        //3.设置发布信息
        if (firstPublishNow) {
            articles.setPublishTime(LocalDateTime.now());
        }

        //4.初始化统计字段和默认状态
        articles.setViewCount(0L);
        articles.setLikeCount(0L);
        articles.setCommentCount(0L);
        if (articles.getIsTop() == null) {
            articles.setIsTop(StatusConstant.DISABLE);
        }

        //5.保存文章到数据库
        save(articles);

        //6.保存文章-标签关联
        if (articleDTO.getTagIds() != null && !articleDTO.getTagIds().isEmpty()) {
            articleTagService.batchInsertRelations(articles.getId(), articleDTO.getTagIds());
        }

        //7.仅首次发布时通知RSS订阅者
        if (firstPublishNow) {
            CommitActions.afterCommit(() -> notifyRssSubscribers(articles));
        }
        return articles.getId();
    }


    /**
     * 根据 id 查询文章详情
     *
     * @param id 文章 id
     * @return 文章详情
     */
    @Override
    public Articles getArticleById(Long id) {
        Articles article = getById(id);
        if (article == null) {
            throw new ArticleException(MessageConstant.ARTICLE_NOT_FOUND);
        }
        //填充标签id列表，用于管理端编辑时回显
        List<Long> tagIds = articleTagService.getTagIdsByArticleId(id);
        article.setTagIds(tagIds);
        return article;
    }

    /**
     * 更新文章
     *
     * @param articleDTO 文章数据
     */
    @Override
    @Transactional
    @EvictArticleCaches
    public void updateArticle(ArticleDTO articleDTO) {
        if (articleDTO.getId() == null || articleDTO.getId() <= 0) throw new ValidationException("更新ID无效");
        validateRelations(articleDTO);
        Articles articles = baseMapper.lockById(articleDTO.getId());
        if (articles == null) {
            throw new ArticleException(MessageConstant.ARTICLE_NOT_FOUND);
        }

        boolean firstPublishNow = articles.getPublishTime() == null
                && StatusConstant.ENABLE.equals(articleDTO.getIsPublished());

        BeanUtil.copyProperties(articleDTO, articles);

        //1.如果文章状态由草稿变为发布，且没有发布时间，则设置发布时间
        if (firstPublishNow) {
            articles.setPublishTime(LocalDateTime.now());
        }

        //2.如果markdown有内容更新，重新生成html并且计算字数
        if (StrUtil.isNotBlank(articleDTO.getContentMarkdown())) {
            // 2.1.优先查看前端是否渲染了html，渲染了直接使用前端渲染
            if (StrUtil.isNotBlank(articleDTO.getContentHtml())) {
                articles.setContentHtml(MarkdownUtil.sanitize(articleDTO.getContentHtml()));
            } else {
                //2.2.否则使用markdown转换
                String rawContent = articleDTO.getContentMarkdown();
                String contentHtml = MarkdownUtil.isHtml(rawContent)
                        ? MarkdownUtil.sanitize(rawContent)
                        : MarkdownUtil.toHtml(rawContent);
                articles.setContentHtml(contentHtml);
            }

            //2.3.重新计算字数和阅读时间
            long wordCount = countWords(articleDTO.getContentMarkdown());
            long readingTime = Math.max(1, wordCount / VIEWS);
            articles.setWordCount(wordCount);
            articles.setReadingTime(readingTime);
        }

        //4.更新文章
        articles.setViewCount(null);
        articles.setLikeCount(null);
        articles.setCommentCount(null);
        if (!updateById(articles)) throw new ValidationException("更新目标不存在");

        //5.更新文章-标签关联
        if (articleDTO.getTagIds() != null){
            articleTagService.deleteRelationsByArticleId(articleDTO.getId());
            if (!articleDTO.getTagIds().isEmpty()) {
                articleTagService.batchInsertRelations(articleDTO.getId(), articleDTO.getTagIds());
            }
        }

        //6.仅首次发布时通知RSS订阅者
        if (firstPublishNow) {
            CommitActions.afterCommit(() -> notifyRssSubscribers(articles));
        }
    }

    /**
     * 批量删除文章
     * @param ids 文章id列表
     */
    @Override
    @Transactional
    @EvictArticleCaches
    public void batchDelete(List<Long> ids) {
        //1.批量删除文章-标签关联
        articleTagService.batchDeleteRelationsByArticleIds(ids);
        for (Long id : ids.stream().distinct().sorted().toList()) {
            baseMapper.lockById(id);
            commentMapper.delete(new LambdaQueryWrapper<ArticleComments>().eq(ArticleComments::getArticleId, id));
            likeMapper.delete(new LambdaQueryWrapper<ArticleLikes>().eq(ArticleLikes::getArticleId, id));
        }
        removeByIds(ids);
        CommitActions.afterCommit(() -> {
            for (Long id : ids) {
                redisTemplate.delete(RedisConstant.ARTICLE_LIKE_USER_SET + id);
                redisTemplate.opsForHash().delete(RedisConstant.ARTICLE_VIEW_COUNT, id.toString());
                redisTemplate.opsForHash().delete(RedisConstant.ARTICLE_LIKE_COUNT, id.toString());
            }
        });
    }

    /**
     * 锁定文章并切换发布状态，串行判断首次发布时间，成功提交后仅发送一次首次发布通知。
     * @param id 文章 ID
     * @param isPublished 1 发布，0 取消发布
     */
    @Override
    @Transactional
    @EvictArticleCaches
    public void publishOrCancel(Long id, Integer isPublished) {
        if (isPublished == null || (isPublished != 0 && isPublished != 1)) throw new ValidationException("发布状态无效");
        //1.查询文章是否存在
        Articles articles = baseMapper.lockById(id);
        if (articles == null) {
            throw new ArticleException(MessageConstant.ARTICLE_NOT_FOUND);
        }

        boolean firstPublishNow = StatusConstant.ENABLE.equals(isPublished) && articles.getPublishTime() == null;

        //2.设置发布状态
        Articles updateArticle = Articles.builder()
                .id(id)
                .isPublished(isPublished)
                .build();

        //3.如果为首次发布，则设置发布时间
        if (firstPublishNow) {
            updateArticle.setPublishTime(LocalDateTime.now());
        }

        //4.更新文章状态
        updateById(updateArticle);

        //5.仅首次发布时通知RSS订阅者
        if (firstPublishNow) {
            CommitActions.afterCommit(() -> notifyRssSubscribers(articles));
        }
    }

    /**
     * 置顶/取消置顶文章
     * @param id 文章id
     * @param isTop 是否置顶
     */
    @Override
    @EvictArticleCaches
    public void toggleTop(Long id, Integer isTop) {
        if (isTop == null || (isTop != 0 && isTop != 1)) throw new ValidationException("置顶状态无效");
        //1.根据id获取当前文章
        Articles articles = getById(id);
        if (articles == null) {
            throw new ArticleException(MessageConstant.ARTICLE_NOT_FOUND);
        }
        //2.更新文章置顶状态
        updateById(Articles.builder()
                .id(id)
                .isTop(isTop)
                .build());

    }

    /**
     * 文章搜索
     * @param keyword 关键字
     * @param page 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @Override
    public PageResult<ArticleVO> search(String keyword, int page, int pageSize) {
        // 1.创建分页对象
        Page<ArticleVO> mpPage = new Page<>(page, pageSize);

        // 2.使用全文索引搜索（通过 Mapper XML）
        IPage<ArticleVO> resultPage = baseMapper.searchWithFullText(mpPage, keyword);

        // 3.使用 PageResult.fromIPage() 构建分页结果
        return PageResult.fromIPage(resultPage);
    }


    /**
     * 获取已发布文章分页
     * @param page 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @Override
    public PageResult<BlogArticleVO> getPublishedPage(int page, int pageSize) {
        // 1. 创建分页对象
        Page<BlogArticleVO> mpPage = new Page<>(page, pageSize);

        // 2. 查询已发布文章（使用自定义 SQL）
        IPage<BlogArticleVO> resultPage = baseMapper.getPublishedPage(mpPage);

        // 3. 返回分页结果
        countReader.merge(resultPage.getRecords());
        return PageResult.fromIPage(resultPage);
    }

    /**
     * 获取文章详情
     * @param slug 文章slug
     * @return 文章详情
     */
    @Override
    public BlogArticleDetailVO getBySlug(String slug) {
        //1.根据slug获取文章详情
        BlogArticleDetailVO articleDetail = baseMapper.getBySlug(slug);
        if (articleDetail == null) {
            throw new ArticleException(MessageConstant.ARTICLE_NOT_FOUND);
        }

        //2.填充标签名称列表
        List<ArticleTags> tags = articleTagService.getTagByArticleId(articleDetail.getId());
        if (tags != null && !tags.isEmpty()){
            articleDetail.setTagNames(tags.stream()
                    .map(ArticleTags::getName)
                    .toList());
        }

        //3.填充上一篇/下一篇导航
        articleDetail.setPrevArticle(baseMapper.getPrevArticle(articleDetail.getId()));
        articleDetail.setNextArticle(baseMapper.getNextArticle(articleDetail.getId()));

        //4.填充相关文章推荐（同分类，排除当前文章，最多 6 篇）
        if (articleDetail.getCategoryId() != null){
            articleDetail.setRelatedArticles(
                    baseMapper.getRelatedArticles(articleDetail.getId(), articleDetail.getCategoryId())
            );
        }
        articleDetail.setContentHtml(MarkdownUtil.sanitize(articleDetail.getContentHtml()));
        countReader.merge(articleDetail);
        return articleDetail;
    }

    /**
     * 文章浏览量+1（写入Redis，定期写入mysql）
     * @param articleId 文章ID
     */
    @Override
    public void incrementViewCount(Long articleId) {
        redisTemplate.opsForHash().increment(RedisConstant.ARTICLE_VIEW_COUNT, articleId.toString(), 1);
    }

    /**
     * 根据分类 ID 获取文章分页
     * @param categoryId 分类 ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @Override
    public PageResult<BlogArticleVO> getPublishedByCategoryId(Long categoryId, int page, int pageSize) {
        //1.创建分页对象
        Page<BlogArticleVO> mpPage = new Page<>(page, pageSize);

        //2.执行分页查询（使用自定义 SQL）
        IPage<BlogArticleVO> resultPage = baseMapper.getPublishedByCategoryId(mpPage, categoryId);

        //3.返回分页结果
        countReader.merge(resultPage.getRecords());
        return PageResult.fromIPage(resultPage);
    }

    /**
     * 获取文章归档（按年月分组）
     * @return 文章归档列表
     */
    @Override
    @Cacheable(value = "articleArchive", key = "'all'")
    public List<ArticleArchiveVO> getArchive() {
        // 1. 查询所有已发布文章
        List<ArticleArchiveItemVO> allArticles = baseMapper.getArchiveList();

        // 2. 按年月分组
        Map<String, ArticleArchiveVO> archiveMap = new LinkedHashMap<>();

        for (ArticleArchiveItemVO item : allArticles) {
            if (item.getPublishTime() == null) {
                continue;
            }

            int year = item.getPublishTime().getYear();
            int month = item.getPublishTime().getMonthValue();
            String key = year + "-" + month;

            ArticleArchiveVO archiveVO = archiveMap.computeIfAbsent(key, k ->
                    ArticleArchiveVO.builder()
                            .year(year)
                            .month(month)
                            .articles(new ArrayList<>())
                            .build()
            );
            archiveVO.getArticles().add(item);
        }

        // 3. 返回归档列表
        return new ArrayList<>(archiveMap.values());
    }

    /**
     * 博客端文章搜索（仅获取已发布文章）
     * @param keyword 关键字
     * @param page 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @Override
    public PageResult<BlogArticleVO> searchPublished(String keyword, int page, int pageSize) {
        Page<BlogArticleVO> mpPage = new Page<>(page, pageSize);
        IPage<BlogArticleVO> resultPage = baseMapper.searchPublished(mpPage, keyword);
        countReader.merge(resultPage.getRecords());
        return PageResult.fromIPage(resultPage);
    }

    /**
     * 根据标签 ID 获取文章分页
     * @param tagId 标签 ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @Override
    public PageResult<BlogArticleVO> getPublishedByTagId(Long tagId, int page, int pageSize) {
        //1.创建分页对象
        Page<BlogArticleVO> mpPage = new Page<>(page, pageSize);

        //2.执行分页查询（使用自定义 SQL）
        IPage<BlogArticleVO> resultPage = baseMapper.getPublishedByTagId(mpPage, tagId);

        //3.返回分页结果
        countReader.merge(resultPage.getRecords());
        return PageResult.fromIPage(resultPage);
    }


    /**
     * 获取已发布文章总数
     * @return 已发布文章总数
     */
    @Override
    public Integer countPublished() {
        LambdaQueryWrapper<Articles> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(Articles::getIsPublished, StatusConstant.ENABLE);
        return Math.toIntExact(count(wrapper));
    }

    /**
     * 获取文章浏览量top10
     * @return 文章浏览量top10
     */
    @Override
    public List<ArticleTitleViewCountDTO> getViewTop10() {
        return baseMapper.getViewTop10();
    }


    /**
     * 获取本月热门文章点赞榜（前 5 篇）
     * @return 本月点赞数最高的已发布文章列表
     */
    @Override
    public List<HotArticleVO> getMonthHotArticlesByLike() {
        LocalDateTime begin = LocalDateTime.now().withDayOfMonth(1).toLocalDate().atStartOfDay();
        List<HotArticleVO> articles = baseMapper.getMonthHotArticlesByLike(begin, begin.plusMonths(1));
        countReader.merge(articles);
        articles.forEach(a -> a.setHotValue(a.getLikeCount()));
        return articles;
    }

    /**
     * 获取本月热门文章浏览榜（前 5 篇）
     * @return 本月浏览量最高的已发布文章列表
     */
    @Override
    public List<HotArticleVO> getMonthHotArticlesByView() {
        LocalDateTime begin = LocalDateTime.now().withDayOfMonth(1).toLocalDate().atStartOfDay();
        List<HotArticleVO> articles = baseMapper.getMonthHotArticlesByView(begin, begin.plusMonths(1));
        countReader.merge(articles);
        articles.forEach(a -> a.setHotValue(a.getViewCount()));
        return articles;
    }

    /**
     * 获取全站热门文章点赞榜（前 5 篇）
     * @return 全站总点赞数最高的已发布文章列表
     */
    @Override
    public List<HotArticleVO> getSiteHotArticlesByLike() {
        List<HotArticleVO> articles = baseMapper.getSiteHotArticlesByLike();
        countReader.merge(articles);
        articles.forEach(a -> a.setHotValue(a.getLikeCount()));
        return articles;
    }

    /**
     * 获取全站热门文章浏览榜（前 5 篇）
     * @return 全站总浏览量最高的已发布文章列表
     */
    @Override
    public List<HotArticleVO> getSiteHotArticlesByView() {
        List<HotArticleVO> articles = baseMapper.getSiteHotArticlesByView();
        countReader.merge(articles);
        articles.forEach(a -> a.setHotValue(a.getViewCount()));
        return articles;
    }

    /**
     * 校验并锁定文章分类及标签，防止保存期间关联目标被并发删除。
     * @param dto 待保存文章
     * @throws com.xuan.exception.ValidationException 关联目标缺失或标签列表含空项
     */
    private void validateRelations(ArticleDTO dto) {
        if (dto.getCategoryId() == null || categoryMapper.lockById(dto.getCategoryId()) == null) {
            throw new ValidationException("文章分类不存在");
        }
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()
                && (dto.getTagIds().stream().anyMatch(Objects::isNull)
                || articleTagService.list(new LambdaQueryWrapper<ArticleTags>().in(ArticleTags::getId, dto.getTagIds()).orderByAsc(ArticleTags::getId).last("FOR UPDATE")).size()
                != dto.getTagIds().stream().distinct().count())) {
            throw new ValidationException("文章标签不存在");
        }
    }

    /**
     * 统计字数（中文算1字，英文单词算1字）
     *
     * @param text 文本
     * @return 字数
     */
    private long countWords(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        // 去除Markdown语法符号
        String cleanText = text.replaceAll("[#*`>\\-\\[\\]()!|]", "");
        // 中文字符数
        long chineseCount = cleanText.chars()
                .filter(c -> Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN)
                .count();
        // 英文单词数
        String englishText = cleanText.replaceAll("[\\u4e00-\\u9fff]", " ");
        String[] words = englishText.trim().split("\\s+");
        long englishCount = 0;
        for (String word : words) {
            if (!word.isEmpty() && word.matches(".*[a-zA-Z0-9].*")) {
                englishCount++;
            }
        }
        return chineseCount + englishCount;
    }


    /**
     * 通知 RSS 订阅者新文章发布
     */
    private void notifyRssSubscribers(Articles article) {
        try {
            //1. 获取所有激活的订阅者
            List<RssSubscriptions> subscribers = rssSubscriptionService.getAllActiveSubscriptions();
            if (subscribers == null || subscribers.isEmpty()) {
                return;
            }
            String blogUrl = websiteProperties.getBlog();
            if (blogUrl == null || blogUrl.isBlank()) {
                log.warn("未配置 dl.website.blog，跳过发送新文章通知邮件");
                return;
            }
            blogUrl = blogUrl.trim();
            if (!blogUrl.startsWith("http://") && !blogUrl.startsWith("https://")) {
                blogUrl = "https://" + blogUrl;
            }
            if (blogUrl.endsWith("/")) {
                blogUrl = blogUrl.substring(0, blogUrl.length() - 1);
            }
            String articleUrl = blogUrl + "/article/" + article.getSlug();
            //2. 发送邮件
            for (RssSubscriptions subscriber : subscribers) {
                asyncEmailService.sendNewArticleNotificationAsync(
                        subscriber.getEmail(),
                        subscriber.getNickname() != null ? subscriber.getNickname() : "订阅者",
                        article.getTitle(),
                        article.getSummary(),
                        articleUrl,
                        blogUrl + "/?rssUnsubscribe=" + rssSubscriptionService.unsubscribeToken(subscriber)
                );
            }
            log.info("已向 {} 个RSS订阅者发送新文章通知: title={}", subscribers.size(), article.getTitle());
        } catch (Exception e) {
            log.error("通知RSS订阅者异常: title={}, ex={}", article.getTitle(), e.getMessage());
        }
    }
}
