package com.xuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.dto.SkillDTO;
import com.xuan.entity.Skills;
import com.xuan.mapper.SkillMapper;
import com.xuan.service.ISkillService;
import com.xuan.vo.SkillVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 技能服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SkillServiceImpl extends ServiceImpl<SkillMapper, Skills> implements ISkillService {

    @Override
    public List<Skills> getAllSkill() {
        // TODO: 实现获取所有技能信息逻辑
        return null;
    }

    @Override
    public void addSkill(SkillDTO skillDTO) {
        // TODO: 实现添加技能逻辑
    }

    @Override
    public void batchDelete(List<Long> ids) {
        // TODO: 实现批量删除技能逻辑
    }

    @Override
    public void updateSkill(SkillDTO skillDTO) {
        // TODO: 实现修改技能逻辑
    }

    @Override
    public List<SkillVO> getSkillVO() {
        // TODO: 实现简历端获取技能信息逻辑
        return null;
    }
}
