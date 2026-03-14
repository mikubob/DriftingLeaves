package com.xuan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xuan.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * 管理员
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("admin")
@EqualsAndHashCode(callSuper = true)
public class Admin extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    // 用户名
    private String username;

    // 加密后的密码
    private String password;

    // 盐值
    private String salt;

    // 昵称
    private String nickname;

    // 电子邮箱
    private String email;

    // 角色 1-管理员 0-游客
    private Integer role;
}
