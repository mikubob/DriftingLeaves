package com.xuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.dto.MusicDTO;
import com.xuan.dto.MusicPageQueryDTO;
import com.xuan.entity.Music;
import com.xuan.mapper.MusicMapper;
import com.xuan.result.PageResult;
import com.xuan.service.IMusicService;
import com.xuan.vo.MusicVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 音乐服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MusicServiceImpl extends ServiceImpl<MusicMapper, Music> implements IMusicService {

    @Override
    public void addMusic(MusicDTO musicDTO) {
        //TODO 实现添加音乐逻辑
    }

    @Override
    public PageResult pageQuery(MusicPageQueryDTO musicPageQueryDTO) {
        //TODO 实现分页查询音乐列表逻辑
        return null;
    }

    @Override
    public void updateMusic(MusicDTO musicDTO) {
        //TODO 实现更新音乐逻辑
    }

    @Override
    public void batchDelete(List<Long> ids) {
        //TODO 实现批量删除音乐逻辑
    }

    @Override
    public Music getById(Long id) {
        //TODO 实现根据 ID 查询音乐逻辑
        return null;
    }

    @Override
    public List<MusicVO> getAllVisibleMusic() {
        //TODO 实现获取所有可见的音乐逻辑
        return null;
    }
}
