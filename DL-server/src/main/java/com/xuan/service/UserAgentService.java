package com.xuan.service;

/**
 * User-Agent 解析服务，一次解析并返回系统与浏览器信息。
 */
public interface UserAgentService {
    record Info(String os, String browser) {}
    /**
     * 一次解析请求的系统和浏览器名称。
     * @param userAgent 请求头，可为空
     * @return 解析结果，未知信息以 Unknown 表示
     */
    Info parse(String userAgent);
}
