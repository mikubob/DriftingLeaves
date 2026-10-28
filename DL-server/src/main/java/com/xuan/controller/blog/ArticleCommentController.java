package com.xuan.controller.blog;


import com.xuan.annotation.RateLimit;
import com.xuan.dto.ArticleCommentDTO;
import com.xuan.dto.ArticleCommentEditDTO;
import com.xuan.result.Result;
import com.xuan.service.IArticleCommentService;
import com.xuan.vo.ArticleCommentVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 博客端文章评论接口
 */
@RestController("blogArticleCommentController")
@RequestMapping("/blog/articleComment")
@Slf4j
@RequiredArgsConstructor
public class ArticleCommentController {

    private final IArticleCommentService articleCommentService;

    /**
     * 按可见根节点分页返回评论及其可见回复。
     * @param articleId 已发布文章 ID
     * @param page 页码，从 1 开始
     * @param pageSize 每页根节点数，1..50
     * @return 当前请求访客可见的评论分页
     */
    @GetMapping("/article/{articleId}/page")
    public Result<com.xuan.result.PageResult<ArticleCommentVO>> getTreePage(@PathVariable Long articleId, @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(articleCommentService.getTreePage(articleId, page, pageSize));
    }

    /**
     * 根据文章ID获取评论列表（树形结构，含当前访客的未审核评论）
     */
    @GetMapping("/article/{articleId}")
    public Result<List<ArticleCommentVO>> getCommentTree(@PathVariable Long articleId,
                                                         @RequestParam(required = false) Long visitorId) {
        log.info("博客端获取文章评论树: articleId={}, visitorId={}", articleId, visitorId);
        List<ArticleCommentVO> commentTree = articleCommentService.getCommentTree(articleId, visitorId);
        return Result.success(commentTree);
    }

    /**
     * 提交评论（添加评论/回复评论）
     */
    @PostMapping
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8,
              timeWindow = 60, message = "评论过于频繁，请稍后再试")
    public Result<String> submitComment(@Valid @RequestBody ArticleCommentDTO articleCommentDTO,
                                        HttpServletRequest request) {
        log.info("访客提交内容");
        articleCommentService.submitComment(articleCommentDTO, request);
        return Result.success();
    }

    /**
     * 访客编辑评论
     */
    @PutMapping("/edit")
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8,
              timeWindow = 60, message = "操作过于频繁，请稍后再试")
    public Result<String> editComment(@Valid @RequestBody ArticleCommentEditDTO editDTO) {
        log.info("访客编辑评论: {}", editDTO);
        articleCommentService.editComment(editDTO);
        return Result.success();
    }

    /**
     * 访客删除评论
     */
    @DeleteMapping("/{id}")
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8,
              timeWindow = 60, message = "操作过于频繁，请稍后再试")
    public Result<String> deleteComment(@PathVariable Long id, @RequestParam(required = false) Long visitorId) {
        log.info("访客删除评论: id={}, visitorId={}", id, visitorId);
        articleCommentService.visitorDeleteComment(id, visitorId);
        return Result.success();
    }
}
