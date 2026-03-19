package com.xuan.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class IpUtil {

    private static Searcher searcher;

    // 静态加载 ip2region 数据库
    static {
        try (InputStream inputStream = IpUtil.class.getClassLoader().getResourceAsStream("ip2region.xdb")) {
            if (inputStream != null) {
                byte[] bytes = inputStream.readAllBytes();
                searcher = Searcher.newWithBuffer(bytes);
            } else {
                log.error("无法找到 ip2region.xdb 文件");
            }
        } catch (Exception e) {
            log.error("ip2region 初始化失败", e);
        }
    }

    /**
     * 获取真实客户端 IP (融合了 CDN 支持)
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) return "unknown";

        // 1. CDN 专用头 (优先级最高)
        String ip = request.getHeader("CF-Connecting-IP");
        if (isInvalid(ip)) ip = request.getHeader("True-Client-IP");
        if (isInvalid(ip)) ip = request.getHeader("Ali-CDN-Real-IP");
        if (isInvalid(ip)) ip = request.getHeader("X-Real-IP");

        // 2. 标准代理头
        if (isInvalid(ip)) ip = request.getHeader("X-Forwarded-For");
        if (isInvalid(ip)) ip = request.getHeader("Proxy-Client-IP");
        if (isInvalid(ip)) ip = request.getHeader("WL-Proxy-Client-IP");

        // 3. 直连 IP
        if (isInvalid(ip)) ip = request.getRemoteAddr();

        // 4. 处理多级代理 (取第一个)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        // 5. 本地回环处理 (可选，视业务需求而定，通常直接返回即可)
        if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            // 这里不再尝试获取本机网卡 IP，避免在容器环境中获取错误 IP
            return "127.0.0.1";
        }

        return ip;
    }



    /**
     * 获取地理位置 (离线高性能版)
     */
    public static String getIpLocation(String ip) {
        if (isInvalid(ip) || "127.0.0.1".equals(ip)) return "本地";
        if (searcher == null) return "未知";

        try {
            String region = searcher.search(ip);
            if (region != null) {
                String[] parts = region.split("\\|");
                StringBuilder sb = new StringBuilder();
                // 格式: 国家|区域|省份|城市|ISP
                if (parts.length > 2 && !"0".equals(parts[2])) sb.append(parts[2]);
                if (parts.length > 3 && !"0".equals(parts[3])) {
                    if (sb.length() > 0) sb.append(" ");
                    sb.append(parts[3]);
                }
                return sb.length() > 0 ? sb.toString() : "未知";
            }
        } catch (Exception e) {
            log.error("IP 地理位置解析失败", e);
        }
        return "未知";
    }

    /**
     * 获取地理位置信息
     * 返回包含 country、province 和 city 的 Map
     *
     * @param ip IP 地址
     * @return 包含 country、province 和 city 的 Map
     */
    public static Map<String, String> getGeoInfo(String ip) {
        if (isInvalid(ip) || "127.0.0.1".equals(ip)) {
            return createEmptyResult();
        }
        
        if (searcher == null) {
            return createEmptyResult();
        }

        try {
            String region = searcher.search(ip);
            if (region != null) {
                String[] parts = region.split("\\|");
                // 格式：国家|区域|省份|城市|ISP
                String country = parts.length > 0 && !"0".equals(parts[0]) ? parts[0] : "";
                String province = parts.length > 2 && !"0".equals(parts[2]) ? parts[2] : "";
                String city = parts.length > 3 && !"0".equals(parts[3]) ? parts[3] : "";
                
                Map<String, String> result = new HashMap<>();
                result.put("country", country);
                result.put("province", province);
                result.put("city", city);
                return result;
            }
        } catch (Exception e) {
            log.error("IP 地理位置解析失败", e);
        }
        return createEmptyResult();
    }

    // 提取公共方法避免重复代码
    private static Map<String, String> createEmptyResult() {
        Map<String, String> result = new HashMap<>();
        result.put("country", "");
        result.put("province", "");
        result.put("city", "");
        return result;
    }

    private static boolean isInvalid(String ip) {
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            return true;
        }
        // 简单 IPv4 验证
        if (!ip.matches("^\\d{1,3}(\\.\\d{1,3}){3}$") &&
            !ip.matches("^([0-9a-fA-F]{0,4}:){2,7}[0-9a-fA-F]{0,4}$")) {
            return true;
        }
        return false;
    }

    /**
     * 关闭 ip2region searcher 资源
     */
    public static void close() {
        if (searcher != null) {
            try {
                searcher.close();
            } catch (Exception e) {
                log.error("关闭 ip2region searcher 失败", e);
            }
        }
    }
}