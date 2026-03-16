package com.xuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.dto.PersonalInfoDTO;
import com.xuan.entity.PersonalInfo;
import com.xuan.mapper.PersonalInfoMapper;
import com.xuan.service.IPersonalInfoService;
import com.xuan.vo.PersonalInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 个人信息服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PersonalInfoServiceImpl extends ServiceImpl<PersonalInfoMapper, PersonalInfo> implements IPersonalInfoService {

    @Override
    public PersonalInfo getAllPersonalInfo() {
        // TODO: 实现获取个人信息逻辑
        return null;
    }

    @Override
    public void updatePersonalInfo(PersonalInfoDTO personalInfoDTO) {
        // TODO: 实现更新个人信息逻辑
    }

    @Override
    public PersonalInfoVO getPersonalInfo() {
        // TODO: 实现其他端获取个人信息逻辑
        return null;
    }
}
