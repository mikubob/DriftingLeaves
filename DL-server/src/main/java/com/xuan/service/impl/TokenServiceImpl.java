package com.xuan.service.impl;

import com.xuan.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    @Override
    public String createAndStoreToken(Long userId, Integer role) {
        // TODO: 实现创建并存储token逻辑
        return null;
    }

    @Override
    public boolean isValidToken(Long userId, String token) {
        // TODO: 实现验证token有效性逻辑
        return false;
    }

    @Override
    public void logout(Long userId, String token) {
        // TODO: 实现退出登录删除token逻辑
    }

    @Override
    public void logoutAll(Long userId) {
        // TODO: 实现退出登录删除所有token逻辑
    }
}
