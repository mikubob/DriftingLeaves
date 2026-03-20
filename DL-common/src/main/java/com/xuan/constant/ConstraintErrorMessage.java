package com.xuan.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * SQL 约束错误消息配置
 */
public class ConstraintErrorMessage {
    
    private static final Map<String, String> CONSTRAINT_MESSAGE_MAP = new HashMap<>();
    
    static {
        CONSTRAINT_MESSAGE_MAP.put("username", "用户名 %s 已存在");
        CONSTRAINT_MESSAGE_MAP.put("user", "用户 %s 已存在");
        
        CONSTRAINT_MESSAGE_MAP.put("email", "邮箱 %s 已存在");
        CONSTRAINT_MESSAGE_MAP.put("phone", "手机号 %s 已存在");
        CONSTRAINT_MESSAGE_MAP.put("mobile", "手机号 %s 已存在");
        
        CONSTRAINT_MESSAGE_MAP.put("title", "标题 %s 已存在");
        CONSTRAINT_MESSAGE_MAP.put("name", "名称 %s 已存在");
        CONSTRAINT_MESSAGE_MAP.put("slug", "别名 %s 已存在");
        
        CONSTRAINT_MESSAGE_MAP.put("code", "编码 %s 已存在");
    }
    
    /**
     * 根据约束名称获取错误消息模板
     * 
     * @param constraintName 约束名称
     * @return 错误消息模板，如果未找到返回默认模板
     */
    public static String getMessageTemplate(String constraintName) {
        if (constraintName == null) {
            return "%s 已存在";
        }
        
        String template = CONSTRAINT_MESSAGE_MAP.get(constraintName.toLowerCase());
        if (template != null) {
            return template;
        }
        
        for (Map.Entry<String, String> entry : CONSTRAINT_MESSAGE_MAP.entrySet()) {
            if (constraintName.toLowerCase().contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        return "%s 已存在";
    }
    
    /**
     * 构建完整的错误消息
     * 
     * @param constraintName 约束名称
     * @param duplicateValue 重复的值
     * @return 完整的错误消息
     */
    public static String buildMessage(String constraintName, String duplicateValue) {
        String template = getMessageTemplate(constraintName);
        return String.format(template, duplicateValue);
    }
}
