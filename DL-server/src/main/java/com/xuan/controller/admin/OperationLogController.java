package com.xuan.controller.admin;


import cn.hutool.db.PageResult;
import com.xuan.dto.OperationLogPageQueryDTO;
import com.xuan.result.Result;
import com.xuan.service.IOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端操作日志接口
 */
@Slf4j
@RestController("adminOperationLogController")
@RequestMapping("/admin/operationLog")
public class OperationLogController {

    @Autowired
    private IOperationLogService operationLogService;

    /**
     * 分页查询操作日志
     * @param operationLogPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> pageQuery(OperationLogPageQueryDTO operationLogPageQueryDTO) {
        log.info("分页查询操作日志,{}", operationLogPageQueryDTO);
        PageResult pageResult = operationLogService.pageQuery(operationLogPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除操作日志
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result batchDelete(@RequestParam List<Long> ids) {
        log.info("批量删除操作日志,{}", ids);
        operationLogService.batchDelete(ids);
        return Result.success();
    }
}
