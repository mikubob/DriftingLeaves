package com.xuan.aspect;


import com.xuan.annotation.OperationLog;
import com.xuan.service.SaveLogAsyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 由 Spring 管理的操作日志切面，用于记录管理员操作。
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class OperationLogAspect {

    private final SaveLogAsyncService saveLogAsyncService;

    /**
     * 定义切入点
     */
    @Pointcut("@annotation(com.xuan.annotation.OperationLog)")
    public void operationLogPointCut() {
    }

    /**
     * 执行目标方法，并读取方法上的操作日志注解以异步保存执行结果。
     *
     * @param joinPoint 目标方法及其调用参数
     * @return 目标方法返回值
     * @throws Throwable 目标方法抛出的异常，记录后继续向调用方抛出
     */
    @Around("operationLogPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        Throwable error = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            error = e;
            throw e;
        } finally {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            OperationLog operationLog = method.getAnnotation(OperationLog.class);

            if (operationLog != null) {
                saveLogAsyncService.saveLogAsync(joinPoint, result, error, operationLog, com.xuan.context.BaseContext.getCurrentId());
            }
        }
    }
}
