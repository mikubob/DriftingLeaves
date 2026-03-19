package com.xuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.dto.ExperienceDTO;
import com.xuan.entity.Experiences;
import com.xuan.mapper.ExperienceMapper;
import com.xuan.service.IExperienceService;
import com.xuan.vo.ExperienceVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 经历服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperienceServiceImpl extends ServiceImpl<ExperienceMapper, Experiences> implements IExperienceService {

    /**
     * 根据类型获取经历信息
     * @param type 经历类型
     * @return 经历信息
     */
    @Override
    public List<Experiences> getExperience(Integer type) {
        // TODO: 实现根据类型获取经历信息逻辑
        return null;
    }

    /**
     * 添加经历信息
     * @param experienceDTO 经验信息
     */
    @Override
    public void addExperience(ExperienceDTO experienceDTO) {
        // TODO: 实现添加经历信息逻辑
    }

    /**
     * 修改经历信息
     * @param experienceDTO 经验信息
     */
    @Override
    public void updateExperience(ExperienceDTO experienceDTO) {
        // TODO: 实现修改经历信息逻辑
    }

    /**
     * 批量删除
     * @param ids 经验id列表
     */
    @Override
    public void batchDelete(List<Long> ids) {
        // TODO: 实现批量删除经历逻辑
    }

    /**
     * 获取全部
     * @return 全部
     */
    @Override
    public List<ExperienceVO> getAllExperience() {
        // TODO: 实现 cv 端获取全部经历信息逻辑
        return null;
    }
}
