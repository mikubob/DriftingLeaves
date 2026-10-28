package com.xuan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 博客端访客提交文章评论DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCommentDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // 文章ID
    @NotNull(message = "文章ID不能为空")
    private Long articleId;

    // 根评论ID
    private Long rootId;

    // 父评论ID
    private Long parentId;

    // 父评论昵称
    @Size(max = 15, message = "父评论昵称不能超过15字")
    private String parentNickname;

    // 评论内容
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 2000, message = "评论内容不能超过2000字")
    private String content;

    // 访客ID
    private Long visitorId;

    // 昵称
    @NotBlank(message = "昵称不能为空")
    @Size(max = 15, message = "昵称不能超过15字")
    private String nickname;

    // 邮箱或QQ号
    @Size(max = 50, message = "邮箱或QQ号不能超过50字")
    private String emailOrQq;

    @NotBlank(message = "请先获取验证码")
    private String captchaId;
    @NotBlank(message = "请输入验证码")
    private String captchaAnswer;

    // 是否使用markdown
    @jakarta.validation.constraints.Min(0)
    @jakarta.validation.constraints.Max(1)
    private Integer isMarkdown;

    // 是否私密（悄悄话）
    @jakarta.validation.constraints.Min(0)
    @jakarta.validation.constraints.Max(1)
    private Integer isSecret;

    // 有回复是否通知
    @jakarta.validation.constraints.Min(0)
    @jakarta.validation.constraints.Max(1)
    private Integer isNotice;
}
