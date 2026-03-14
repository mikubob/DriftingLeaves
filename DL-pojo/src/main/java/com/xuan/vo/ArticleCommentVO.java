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
 * 文章评论VO（树形结构）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCommentVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long articleId;
    private Long rootId;
    private Long parentId;
    private String parentNickname;
    private String content;
    private String contentHtml;
    private Integer isMarkdown;
    private Long visitorId;
    private String nickname;
    private String emailOrQq;
    private String location;
    private String userAgentOs;
    private String userAgentBrowser;
    private Integer isApproved;
    private Integer isSecret;
    private Integer isAdminReply;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /**
     * 子评论列表（仅根评论有值）
     */
    private List<ArticleCommentVO> children;
}
