package com.xuan.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OperationLogPageQueryDTO {

    // 页码
    private int page;

    // 每页显示数量
    private int pageSize;

    // 管理员ID
    private Long adminId;

    // 操作类型
    private String operationType;

    // 操作对象
    private String operationTarget;

    // 开始时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    // 结束时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
