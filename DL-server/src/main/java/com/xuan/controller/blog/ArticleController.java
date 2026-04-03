package com.xuan.controller.blog;


import com.xuan.annotation.RateLimit;
import com.xuan.constant.RedisConstant;
import com.xuan.result.PageResult;
import com.xuan.result.Result;
import com.xuan.service.IArticleService;
import com.xuan.vo.ArticleArchiveVO;
import com.xuan.vo.BlogArticleDetailVO;
import com.xuan.vo.BlogArticleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 博客端文章接口
 */
@RestController("blogArticleController")
@RequestMapping("/blog/article")
@Slf4j
@RequiredArgsConstructor
public class ArticleController {

    private final IArticleService articleService;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 获取已发布文章列表（分页）
     */
    @GetMapping("/page")
    public Result<PageResult<BlogArticleVO>> getPublishedPage(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int pageSize) {
        log.info("博客端获取已发布文章列表: page={}, pageSize={}", page, pageSize);
        PageResult<BlogArticleVO> pageResult = articleService.getPublishedPage(page, pageSize);
        
        for (BlogArticleVO article : pageResult.getRecords()) {
            mergeRedisCount(article);
        }
        
        return Result.success(pageResult);
    }

    /**
     * 根据slug获取文章详情（浏览量+1）
     */
    @GetMapping("/detail/{slug}")
    public Result<BlogArticleDetailVO> getBySlug(@PathVariable String slug) {
        log.info("博客端获取文章详情: slug={}", slug);
        BlogArticleDetailVO articleDetail = articleService.getBySlug(slug);
        
        articleService.incrementViewCount(articleDetail.getId());
        
        mergeRedisCount(articleDetail);
        
        return Result.success(articleDetail);
    }

    /**
     * 根据分类ID获取文章列表（分页）
     */
    @GetMapping("/category/{categoryId}")
    public Result<PageResult<BlogArticleVO>> getByCategory(@PathVariable Long categoryId,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int pageSize) {
        log.info("博客端根据分类获取文章列表: categoryId={}, page={}, pageSize={}", categoryId, page, pageSize);
        PageResult<BlogArticleVO> pageResult = articleService.getPublishedByCategoryId(categoryId, page, pageSize);
        
        for (BlogArticleVO article : pageResult.getRecords()) {
            mergeRedisCount(article);
        }
        
        return Result.success(pageResult);
    }

    /**
     * 获取文章归档（按年月分组）
     */
    @GetMapping("/archive")
    public Result<List<ArticleArchiveVO>> getArchive() {
        log.info("博客端获取文章归档");
        List<ArticleArchiveVO> archiveList = articleService.getArchive();
        return Result.success(archiveList);
    }

    /**
     * 文章搜索（仅已发布）
     */
    @GetMapping("/search")
    @RateLimit(type = RateLimit.Type.IP, tokens = 10, burstCapacity = 15,
            timeWindow = 60, message = "搜索过于频繁，请稍后再试")
    public Result<PageResult<BlogArticleVO>> search(@RequestParam String keyword,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int pageSize) {
        log.info("博客端文章搜索: keyword={}", keyword);
        PageResult<BlogArticleVO> pageResult = articleService.searchPublished(keyword, page, pageSize);
        
        for (BlogArticleVO article : pageResult.getRecords()) {
            mergeRedisCount(article);
        }
        
        return Result.success(pageResult);
    }

    /**
     * 合并 Redis 中的浏览量和点赞数增量
     */
    private void mergeRedisCount(BlogArticleVO article) {
        if (article == null || article.getId() == null) {
            return;
        }
        String articleIdStr = article.getId().toString();
        
        Object pendingView = stringRedisTemplate.opsForHash().get(RedisConstant.ARTICLE_VIEW_COUNT, articleIdStr);
        if (pendingView != null) {
            long pendingViewCount = Long.parseLong(pendingView.toString());
            article.setViewCount(article.getViewCount() + pendingViewCount);
        }
        
        Object pendingLike = stringRedisTemplate.opsForHash().get(RedisConstant.ARTICLE_LIKE_COUNT, articleIdStr);
        if (pendingLike != null) {
            long pendingLikeCount = Long.parseLong(pendingLike.toString());
            article.setLikeCount(article.getLikeCount() + pendingLikeCount);
        }
    }

    /**
     * 合并 Redis 中的浏览量和点赞数增量（详情页）
     */
    private void mergeRedisCount(BlogArticleDetailVO article) {
        if (article == null || article.getId() == null) {
            return;
        }
        String articleIdStr = article.getId().toString();
        
        Object pendingView = stringRedisTemplate.opsForHash().get(RedisConstant.ARTICLE_VIEW_COUNT, articleIdStr);
        if (pendingView != null) {
            long pendingViewCount = Long.parseLong(pendingView.toString());
            article.setViewCount(article.getViewCount() + pendingViewCount);
        }
        
        Object pendingLike = stringRedisTemplate.opsForHash().get(RedisConstant.ARTICLE_LIKE_COUNT, articleIdStr);
        if (pendingLike != null) {
            long pendingLikeCount = Long.parseLong(pendingLike.toString());
            article.setLikeCount(article.getLikeCount() + pendingLikeCount);
        }
    }
}
