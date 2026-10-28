package com.xuan.service;

/**
 * 访客凭据服务，以独立用途的签名令牌证明访客身份。
 */
public interface VisitorTokenService {
    /**
     * 为服务端已建立的访客签发一年有效的凭据。
     * @param visitorId 数据库中的访客 ID
     * @return 签名访客令牌
     */
    String issue(Long visitorId);

    /**
     * 校验签名、有效期及访客用途；访客存在性和封禁由拦截器继续检查。
     * @param token 请求携带的访客令牌
     * @return 经验证的访客 ID
     * @throws com.xuan.exception.TokenException 凭据无效或过期
     */
    Long verify(String token);
}
