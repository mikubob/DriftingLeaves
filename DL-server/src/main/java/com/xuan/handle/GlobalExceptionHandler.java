package com.xuan.handle;

import com.xuan.constant.ConstraintErrorMessage;
import com.xuan.constant.MessageConstant;
import com.xuan.exception.BaseException;
import com.xuan.exception.BlockedException;
import com.xuan.exception.GuestReadOnlyException;
import com.xuan.exception.RateLimitException;
import com.xuan.exception.TokenException;
import com.xuan.exception.ValidationException;
import com.xuan.result.Result;
import com.xuan.utils.SqlConstraintExceptionParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 将 JSON、参数类型及绑定格式错误转换为 HTTP 400。
     * @param ex 请求解析异常
     * @return 不含原始请求值的错误结果
     */
    @ExceptionHandler({HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result badRequest(Exception ex) { return Result.error("请求参数格式不正确"); }

    /**
     * 将短期限流转换为 HTTP 429，并保留客户端重试提示。
     * @param ex 限流异常
     * @return 含 Retry-After 的错误响应
     */
    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<Result> rateLimit(RateLimitException ex) {
        return ResponseEntity.status(429).header("Retry-After", "60").body(Result.error(ex.getMessage()));
    }

    /**
     * 将业务参数校验失败转换为 HTTP 400。
     * @param ex 业务校验异常
     * @return 校验错误结果
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result validation(ValidationException ex) { return Result.error(ex.getMessage()); }


    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("业务异常：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 将凭据无效转换为 HTTP 401。
     * @param ex 令牌异常
     * @return 认证失败结果
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result exceptionHandler(TokenException ex){
        log.error("令牌异常：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 将实际封禁转换为 HTTP 403。
     * @param ex 封禁异常
     * @return 禁止访问结果
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result exceptionHandler(BlockedException ex){
        log.error("封禁异常：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 将演示游客的受限操作转换为 HTTP 403。
     * @param ex 游客权限异常
     * @return 禁止操作结果
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result exceptionHandler(GuestReadOnlyException ex){
        log.error("游客只读异常：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获参数校验异常（@Valid校验失败）
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result exceptionHandler(MethodArgumentNotValidException ex){
        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + "：" + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.error("参数校验异常：{}", errorMsg);
        return Result.error("提交内容不完整或格式不正确，请检查：" + errorMsg);
    }

    /**
     * 将数据库约束冲突转换为现有业务错误结果。
     * @param ex SQL 完整性约束异常
     * @return 约束对应的错误提示
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error("SQL 完整性约束异常：{}", ex.getMessage());
        
        SqlConstraintExceptionParser.ConstraintInfo info = 
            SqlConstraintExceptionParser.parse(ex);
        
        if (info != null) {
            String errorMessage = ConstraintErrorMessage.buildMessage(
                info.getConstraintName(), 
                info.getDuplicateValue()
            );
            return Result.error(errorMessage);
        }
        
        return Result.error("数据已存在或被其他记录引用，请检查后重试");
    }

    /**
     * 请求方法不支持异常
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result exceptionHandler(HttpRequestMethodNotSupportedException ex){
        log.error("请求方法不支持：{}", ex.getMessage());
        return Result.error("当前操作暂不支持，请刷新页面后重试");
    }

    /**
     * 缺少请求参数异常
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result exceptionHandler(MissingServletRequestParameterException ex){
        log.error("缺少请求参数：{}", ex.getMessage());
        return Result.error("请求信息不完整，请检查 " + ex.getParameterName() + " 后重试");
    }

    /**
     * 请求路径不存在异常
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result exceptionHandler(NoHandlerFoundException ex){
        log.warn("请求路径不存在：{} {}", ex.getHttpMethod(), ex.getRequestURL());
        return Result.error("请求地址不存在，请刷新页面后重试");
    }

    /**
     * 静态资源不存在异常（如 favicon.ico）
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result exceptionHandler(NoResourceFoundException ex){
        log.debug("静态资源不存在：{}", ex.getResourcePath());
        return Result.error("资源不存在或已被删除，请刷新页面后重试");
    }

    /**
     * 文件上传大小超限异常
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result exceptionHandler(MaxUploadSizeExceededException ex){
        log.error("文件上传大小超限：{}", ex.getMessage());
        return Result.error("上传文件过大，请压缩后重新上传");
    }

    /**
     * 兜底异常处理
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result exceptionHandler(Exception ex){
        log.error("未知异常：", ex);
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

}
