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
 * 文章归档中的单篇文章
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleArchiveItemVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String title;
    private String slug;
    private Integer publishDay;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;
}
