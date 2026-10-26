package com.xuan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 技能DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;

    /**
     * 技能名称
     */
    @NotBlank(message = "技能名称不能为空")
    @Size(max = 20, message = "技能名称不能超过20字")
    private String name;

    /**
     * 技能描述
     */
    @Size(max = 255, message = "技能描述不能超过255字")
    private String description;

    /**
     * 图标 URL，长度对应数据库 skills.icon VARCHAR(255)。
     */
    @Size(max = 255, message = "技能图标链接不能超过255个字符")
    private String icon;

    /**
     * 排序，越小越靠前
     */
    private Integer sort;

    /**
     * 是否可见
     */
    private Integer isVisible;
}
