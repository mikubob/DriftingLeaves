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
 * 访客提交留言DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // 留言内容
    @NotBlank(message = "留言内容不能为空")
    @Size(max = 2000, message = "留言内容不能超过2000字")
    private String content;

    // 根留言ID,null是一级留言
    private Long rootId;

    // 父留言ID,null是一级留言
    private Long parentId;

    // 父留言昵称
    @Size(max = 15, message = "父留言昵称不能超过15字")
    private String parentNickname;

    // 访客ID
    private Long visitorId;

    // 昵称
    @NotBlank(message = "昵称不能为空")
    @Size(max = 15, message = "昵称不能超过15字")
    private String nickname;

    // 邮箱或qq
    @Size(max = 50, message = "邮箱或QQ号不能超过50字")
    private String emailOrQq;

    @NotBlank(message = "请先获取验证码")
    private String captchaId;
    @NotBlank(message = "请输入验证码")
    private String captchaAnswer;

    // 是否使用markdown，0-否，1-是
    @jakarta.validation.constraints.Min(0)
    @jakarta.validation.constraints.Max(1)
    private Integer isMarkdown;

    // 是否私密（悄悄话），0-否，1-是
    @jakarta.validation.constraints.Min(0)
    @jakarta.validation.constraints.Max(1)
    private Integer isSecret;

    // 有回复是否通知，0-否，1-是
    @jakarta.validation.constraints.Min(0)
    @jakarta.validation.constraints.Max(1)
    private Integer isNotice;
}
