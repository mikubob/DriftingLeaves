package com.xuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.dto.AdminChangeEmailDTO;
import com.xuan.dto.AdminChangeNicknameDTO;
import com.xuan.dto.AdminChangePasswordDTO;
import com.xuan.dto.AdminLoginDTO;
import com.xuan.dto.AdminLogoutDTO;
import com.xuan.entity.Admin;
import com.xuan.mapper.AdminMapper;
import com.xuan.service.IAdminService;
import com.xuan.vo.AdminLoginVO;
import com.xuan.vo.AdminVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 管理员服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {

    @Override
    public void sendVerifyCode(String username) {
        // TODO: 实现发送验证码逻辑
    }

    @Override
    public AdminLoginVO login(AdminLoginDTO adminLoginDTO) throws Exception {
        // TODO: 实现管理员登录逻辑
        return null;
    }

    @Override
    public AdminVO getAdminById() {
        // TODO: 实现获取管理员信息逻辑
        return null;
    }

    @Override
    public void logout(AdminLogoutDTO adminLogoutDTO) {
        // TODO: 实现管理员退出登录逻辑
    }

    @Override
    public void changePassword(AdminChangePasswordDTO adminChangePasswordDTO) throws Exception {
        // TODO: 实现管理员修改密码逻辑
    }

    @Override
    public void changeNickname(AdminChangeNicknameDTO adminChangeNicknameDTO) {
        // TODO: 实现管理员更改昵称逻辑
    }

    @Override
    public void changeEmail(AdminChangeEmailDTO adminChangeEmailDTO) {
        // TODO: 实现管理员换绑邮箱逻辑
    }
}
