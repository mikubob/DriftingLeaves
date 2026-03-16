package com.xuan.service.impl;

import com.xuan.service.ISitemapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 站点地图服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SitemapServiceImpl implements ISitemapService {

    @Override
    public String generateSitemap() {
        // TODO: 实现生成站点地图 XML 逻辑
        return null;
    }
}
