package com.xuan.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * 【安全扫描日志过滤器配置类】
 *
 * 核心功能：
 * 1. 拦截常见的自动化漏洞扫描、敏感文件探测请求（如 .env, .git, wp-admin 等）。
 * 2. 对恶意请求直接返回 HTTP 404 (Not Found)，避免暴露服务器真实信息（如 403 Forbidden 会暴露路径存在）。
 * 3. 将拦截记录写入独立的日志文件（通过 Logger 名称 "SECURITY_SCAN" 区分），防止污染业务主日志。
 * 4. 对合法请求正常放行，并自动将客户端 IP 注入 MDC (Mapped Diagnostic Context)，便于后续业务日志追踪来源。
 *
 * @author Xuan
 */
@Configuration
public class SecurityLogConfig {

    public static final Logger SECURITY_SCAN_LOG = LoggerFactory.getLogger("SECURITY_SCAN");

    private static final Set<String> ALLOWED_PREFIXES = Set.of(
            "/blog/",      // 博客模块
            "/admin/",     // 管理后台
            "/home/",      // 首页相关
            "/cv/",        // 简历页面
            "/health",     // 健康检查接口 (通常用于 K8s 探针)
            "/ws",         // WebSocket 路径
            "/actuator/",  // [建议添加] Spring Boot 监控端点
            "/druid/"      // [建议添加] Druid 监控控制台
    );

    private static final Set<String> SCAN_EXTENSIONS = Set.of(
            ".env",         // 环境变量文件 (极高危)
            ".php",         // 尝试探测 PHP 环境
            ".bak", ".old", ".save", // 备份文件
            ".swp",         // Vim 编辑临时文件
            ".config", ".ini", ".properties", // 配置文件
            ".yml", ".yaml", // Kubernetes 或 Spring 配置
            ".sql",         // 数据库脚本
            ".tar.gz", ".zip", ".rar", // 压缩包
            ".git", ".svn"  // 版本控制目录 (虽通常是目录，但也常作为后缀出现在 URL 末尾)
    );

    private static final Set<String> SCAN_SEGMENTS = Set.of(

            "/.git", "/.svn", "/.htaccess", "/.htpasswd", "/.env",
            "/.aws", "/.docker",

            "/wp-admin", "/wp-login", "/wp-content", "/wp-includes", "/wordpress",

            "/phpmyadmin", "/phpinfo", "/info.php", "/config.php",
            "/debug", "/console",
            "/manager/html", // Tomcat Manager
            "/solr",         // Solr 搜索
            "/jenkins",      // Jenkins CI/CD
            "/struts",       // Struts2 漏洞探测
            "/cgi-bin"   // 传统 CGI 脚本



    );

    /**
     * 注册安全扫描日志过滤器，对可疑路径进行记录与拦截。
     * @return 安全扫描过滤器
     */
    @Bean
    public OncePerRequestFilter securityScanLogFilter() {
        return new OncePerRequestFilter() {

            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain)
                    throws IOException, ServletException {

                String path = request.getRequestURI(); // 获取 URI 路径 (不包含查询参数)
                String ip = getClientIp(request);      // 获取客户端真实 IP (处理代理情况)





                if (isAllowedPath(path)) {

                    org.slf4j.MDC.put("ip", ip);
                    try {
                        chain.doFilter(request, response); // 放行请求
                    } finally {

                        org.slf4j.MDC.remove("ip");
                    }
                    return; // 结束处理
                }




                if (isScanningRequest(path)) {


                    SECURITY_SCAN_LOG.warn("SCAN_ATTEMPT: {} {} from {} UA: {}",
                            request.getMethod(),
                            path,
                            ip,
                            request.getHeader("User-Agent"));




                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.setContentType("text/plain");
                    response.getWriter().write("Not Found");
                    response.flushBuffer(); // 强制立即发送响应，终止后续处理
                    return;
                }





                org.slf4j.MDC.put("ip", ip);
                try {
                    chain.doFilter(request, response);
                } finally {
                    org.slf4j.MDC.remove("ip");
                }
            }

                    private boolean isAllowedPath(String path) {

                if ("/".equals(path)) {
                    return true;
                }


                for (String prefix : ALLOWED_PREFIXES) {
                    if (path.startsWith(prefix)) {
                        return true;
                    }
                }
                return false;
            }

                    private boolean isScanningRequest(String path) {

                String lower = path.toLowerCase();



                int lastDotIndex = lower.lastIndexOf('.');
                if (lastDotIndex > 0) {








                    String ext = lower.substring(lastDotIndex);
                    if (SCAN_EXTENSIONS.contains(ext)) {
                        return true;
                    }
                }







                for (String seg : SCAN_SEGMENTS) {
                    if (lower.contains(seg)) {
                        return true;
                    }
                }

                return false;
            }

                    private String getClientIp(HttpServletRequest request) {
                return com.xuan.utils.IpUtil.getClientIp(request);
            }
        };
    }
}