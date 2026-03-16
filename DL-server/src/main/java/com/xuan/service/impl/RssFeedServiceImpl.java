package com.xuan.service.impl;

import com.xuan.service.IRssFeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * RSS Feed 服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RssFeedServiceImpl implements IRssFeedService {

    @Override
    public String generateRssFeed() {
        // TODO: 实现生成 RSS Feed 逻辑
        return null;
    }
}
