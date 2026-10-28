package com.xuan.interceptor;

import com.xuan.context.VisitorContext;
import com.xuan.entity.Visitors;
import com.xuan.exception.TokenException;
import com.xuan.mapper.VisitorMapper;
import com.xuan.service.BlockService;
import com.xuan.service.VisitorTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 可选访客凭据拦截器，校验身份、存在性和封禁，并管理请求上下文。
 */
@Component
@RequiredArgsConstructor
public class VisitorTokenInterceptor implements HandlerInterceptor {
    private final VisitorTokenService tokens;
    private final VisitorMapper visitors;
    private final BlockService blocks;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        VisitorContext.clear();
        String token = request.getHeader("X-Visitor-Token");
        if (token == null || token.isBlank()) return true;
        Long id = tokens.verify(token);
        Visitors visitor = visitors.selectById(id);
        if (visitor == null) throw new TokenException("访客会话已失效，请重新建立会话");
        blocks.checkIfBlocked(visitor.getFingerprint());
        VisitorContext.setId(id);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        VisitorContext.clear();
    }
}
