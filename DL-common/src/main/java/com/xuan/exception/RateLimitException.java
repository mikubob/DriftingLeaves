package com.xuan.exception;

/**
 * 短期限流异常，由异常处理器返回 HTTP 429。
 */
public class RateLimitException extends BaseException {
    public RateLimitException(String message) { super(message); }
}
