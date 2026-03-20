package com.xuan.service.impl;

import com.xuan.service.UserAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAgentServiceImpl implements UserAgentService {

    @Override
    public String getOsName(String userAgent) {
        // TODO: 实现解析操作系统名称逻辑
        return null;
    }

    @Override
    public String getBrowserName(String userAgent) {
        // TODO: 实现解析浏览器名称逻辑
        return null;
    }
}
