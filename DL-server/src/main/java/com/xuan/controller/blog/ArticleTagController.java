package com.xuan.controller.blog;


import cn.hutool.db.PageResult;
import com.xuan.entity.ArticleTags;
import com.xuan.result.Result;
import com.xuan.service.ArticleService;
import com.xuan.service.ArticleTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 博客端文章标签接口
 */
@Slf4j
@RestController("blogArticleTagController")
@RequestMapping("/blog/article/tag")
public class ArticleTagController {

    @Autowired
    private ArticleTagService articleTagService;

    @Autowired
    private ArticleService articleService;

    /**
     * 获取有已发布文章的标签列表
     */
    @GetMapping
    public Result<List<ArticleTags>> getVisibleTags() {
        List<ArticleTags> list = articleTagService.getVisibleTags();
        return Result.success(list);
    }

    /**
     * 根据标签ID获取已发布文章列表
     */
    @GetMapping("/{tagId}")
    public Result<PageResult> getPublishedByTagId(@PathVariable Long tagId,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int pageSize) {
        log.info("博客端根据标签获取文章列表: tagId={}", tagId);
        PageResult pageResult = articleService.getPublishedByTagId(tagId, page, pageSize);
        return Result.success(pageResult);
    }
}
