package com.xuan.context;

import com.xuan.exception.TokenException;

/**
 * 当前请求的已验证访客上下文，由拦截器建立并在请求结束时清理。
 */
public final class VisitorContext {
    private static final ThreadLocal<Long> ID = new ThreadLocal<>();
    private VisitorContext() {}

    /**
     * 读取当前请求的访客身份。
     * @return 访客 ID，匿名请求为 null
     */
    public static Long getId() { return ID.get(); }
    /**
     * 保存拦截器已验证的访客身份。
     * @param id 已通过凭据及封禁检查的访客 ID
     */
    public static void setId(Long id) { ID.set(id); }
    /**
     * 清理请求线程中的访客身份，避免线程复用时串用凭据。
     */
    public static void clear() { ID.remove(); }
    /**
     * 要求当前请求具有已验证访客身份。
     * @return 访客 ID
     * @throws TokenException 未建立访客会话
     */
    public static Long requireId() {
        Long id = getId();
        if (id == null) throw new TokenException("请先建立访客会话");
        return id;
    }
}
