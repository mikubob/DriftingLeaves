package com.xuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.dto.SocialMediaDTO;
import com.xuan.entity.SocialMedia;
import com.xuan.mapper.SocialMediaMapper;
import com.xuan.service.ISocialMediaService;
import com.xuan.vo.SocialMediaVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 社交媒体服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SocialMediaServiceImpl extends ServiceImpl<SocialMediaMapper, SocialMedia> implements ISocialMediaService {

    @Override
    public List<SocialMediaVO> getVisibleSocialMedia() {
        // TODO: 实现获取可见社交媒体信息逻辑
        return null;
    }

    @Override
    public List<SocialMedia> getAllSocialMedia() {
        // TODO: 实现获取所有社交媒体信息逻辑
        return null;
    }

    @Override
    public void addSocialMedia(SocialMediaDTO socialMediaDTO) {
        // TODO: 实现添加社交媒体信息逻辑
    }

    @Override
    public void batchDelete(List<Long> ids) {
        // TODO: 实现批量删除社交媒体逻辑
    }

    @Override
    public void updateSocialMedia(SocialMediaDTO socialMediaDTO) {
        // TODO: 实现修改社交媒体信息逻辑
    }
}
