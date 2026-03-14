package com.xuan.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 博客端文章列表VO（不含文章内容）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogArticleVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String title;
    private String slug;
    private String summary;
    private String coverImage;
    private Long categoryId;
    private String categoryName;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private Long wordCount;
    private Long readingTime;
    private Integer isTop;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;
}
