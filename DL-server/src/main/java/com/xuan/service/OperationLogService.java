package com.xuan.service;

import cn.hutool.db.PageResult;
import com.xuan.dto.OperationLogPageQueryDTO;
import com.xuan.entity.OperationLogs;

import java.util.List;

public interface OperationLogService {
    /**
     * 保存操作日志
     * @param operationLogs
     */
    void save(OperationLogs operationLogs);

    /**
     * 分页查询操作日志
     * @param operationLogPageQueryDTO
     * @return
     */
    PageResult pageQuery(OperationLogPageQueryDTO operationLogPageQueryDTO);

    /**
     * 批量删除操作日志
     * @param ids
     */
    void batchDelete(List<Long> ids);
}
