package com.xuan.utils;

import com.xuan.exception.ValidationException;
import org.springframework.web.util.HtmlUtils;
import java.util.Map;

/**
 * 评论和留言共用的安全内容渲染、联系方式校验及地点组装工具。
 */
public final class DiscussionUtil {
    private DiscussionUtil() {}

    /**
     * 按内容格式安全生成 HTML，普通文本转义并保留换行。
     * @param content 原始内容，可为 null
     * @param markdown 1 表示 Markdown，其余按普通文本处理
     * @return 清理后的 HTML，空内容返回空字符串
     */
    public static String html(String content, Integer markdown) {
        if (content == null) return "";
        return Integer.valueOf(1).equals(markdown) ? MarkdownUtil.toHtml(content)
                : HtmlUtils.htmlEscape(content).replace("\r\n", "\n").replace("\n", "<br>");
    }

    /**
     * 校验邮箱或 5..12 位 QQ 号，并转换为可发送的邮箱。
     * @param contact 用户填写的联系方式
     * @return 邮箱地址，未填写时为 null
     * @throws ValidationException 联系方式格式错误
     */
    public static String email(String contact) {
        if (contact == null || contact.isBlank()) return null;
        String value = contact.trim();
        if (value.matches("^[1-9]\\d{4,11}$")) return value + "@qq.com";
        if (value.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) return value;
        throw new ValidationException("邮箱或QQ号格式不正确");
    }

    /**
     * 将 IP 地理信息合并为展示地点，省市相同时不重复。
     * @param geo IP 工具返回的非空地理信息
     * @return 地点，无省市信息时为 null
     */
    public static String location(Map<String, String> geo) {
        String province = geo.getOrDefault("province", "");
        String city = geo.getOrDefault("city", "");
        if (province.isEmpty() && city.isEmpty()) return null;
        return province.equals(city) ? province : (province + "-" + city).replaceAll("^-|-$", "");
    }
}
