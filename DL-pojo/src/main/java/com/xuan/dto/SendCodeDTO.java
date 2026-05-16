package com.xuan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 发送验证码DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendCodeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // 用户名
    private String username;
}
