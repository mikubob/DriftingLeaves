package com.xuan.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xuan.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("articles")
@EqualsAndHashCode(callSuper = true)
public class Articles extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    // 文章标题
    private String title;

    // URL 标识
    private String slug;

    // 文章摘要
    private String summary;

    // 封面图片 url
    private String coverImage;

    // Markdown 内容
    private String contentMarkdown;

    // 转换后的 HTML 内容
    private String contentHtml;

    // 分类 ID
    private Long categoryId;

    // 浏览次数
    private Long viewCount;

    // 点赞次数
    private Long likeCount;

    // 评论数
    private Long commentCount;

    // 字数统计
    private Long wordCount;

    // 预计阅读时间，单位：分钟
    private Long readingTime;

    // 是否发布，0-否，1-是
    private Integer isPublished;

    // 是否置顶，0-否，1-是
    private Integer isTop;

    // 发布时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    // 发布年份
    private Integer publishYear;

    // 发布月份
    private Integer publishMonth;

    // 发布日期
    private Integer publishDay;

    // 发布日期（去掉时间）
    private LocalDate publishDate;

    // 标签 ID 列表（非数据库字段，管理端返回时填充）
    @TableField(exist = false)
    private List<Long> tagIds;
}
