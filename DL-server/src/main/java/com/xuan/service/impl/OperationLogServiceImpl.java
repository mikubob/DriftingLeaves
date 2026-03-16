package com.xuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.dto.OperationLogPageQueryDTO;
import com.xuan.entity.OperationLogs;
import com.xuan.mapper.OperationLogMapper;
import com.xuan.result.PageResult;
import com.xuan.service.IOperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 操作日志服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLogs> implements IOperationLogService {

    @Override
    public void saveLog(OperationLogs operationLogs) {
        // TODO: 实现保存操作日志逻辑
    }

    @Override
    public PageResult pageQuery(OperationLogPageQueryDTO operationLogPageQueryDTO) {
        // TODO: 实现分页查询操作日志逻辑
        return null;
    }

    @Override
    public void batchDelete(List<Long> ids) {
        // TODO: 实现批量删除操作日志逻辑
    }
}
