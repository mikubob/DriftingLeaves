package com.xuan.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 博客端文章详情VO（含HTML内容）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogArticleDetailVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String title;
    private String slug;
    private String summary;
    private String coverImage;
    private String contentHtml;
    private String contentMarkdown;
    private Long categoryId;
    private String categoryName;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private Long wordCount;
    private Long readingTime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 文章标签名称列表
    private List<String> tagNames;

    // 上一篇/下一篇导航
    private BlogArticleVO prevArticle;
    private BlogArticleVO nextArticle;

    // 相关文章推荐
    private List<BlogArticleVO> relatedArticles;
}
