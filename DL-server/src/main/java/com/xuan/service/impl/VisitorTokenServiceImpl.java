package com.xuan.service.impl;

import com.xuan.service.VisitorTokenService;

import com.xuan.exception.TokenException;
import com.xuan.properties.JwtProperties;
import com.xuan.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Map;

/**
 * 访客凭据服务实现，使用独立用途声明签发并校验 JWT。
 */
@Service
@RequiredArgsConstructor
public class VisitorTokenServiceImpl implements VisitorTokenService {
    private final JwtProperties properties;

    @Override
    public String issue(Long visitorId) {
        return JwtUtil.createJWT(properties.getSecretKey(), Duration.ofDays(365).toMillis(),
                Map.of("purpose", "visitor", "visitorId", visitorId));
    }

    @Override
    public Long verify(String token) {
        try {
            Claims claims = JwtUtil.parseJWT(properties.getSecretKey(), token);
            if (!"visitor".equals(claims.get("purpose"))) throw new IllegalArgumentException();
            return Long.valueOf(claims.get("visitorId").toString());
        } catch (Exception ex) {
            throw new TokenException("访客会话已失效，请重新建立会话");
        }
    }
}
