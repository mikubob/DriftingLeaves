package com.xuan.service;

import com.xuan.annotation.OperationLog;
import org.aspectj.lang.JoinPoint;

/**
 * 异步操作审计服务，显式接收请求线程确定的管理员身份。
 */
public interface SaveLogAsyncService {

    /**
     * 异步持久化操作审计，序列化前裁剪并脱敏，身份不从异步线程上下文读取。
     * @param joinPoint 操作方法及实参
     * @param result 操作返回值
     * @param error 操作异常，成功时为 null
     * @param operationLog 操作日志注解
     * @param adminId 请求线程捕获的管理员 ID
     */
    void saveLogAsync(JoinPoint joinPoint, Object result, Throwable error,
                      OperationLog operationLog, Long adminId);
}
