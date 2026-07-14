package com.xuan.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xuan.dto.AdminChangeEmailDTO;
import com.xuan.dto.AdminChangeNicknameDTO;
import com.xuan.dto.AdminChangePasswordDTO;
import com.xuan.dto.AdminLoginDTO;
import com.xuan.entity.Admin;
import com.xuan.vo.AdminLoginVO;
import com.xuan.vo.AdminVO;

public interface IAdminService extends IService<Admin> {

    /**
     * 发送验证码
     */
    void sendVerifyCode(String username);

    /**
     * 管理员登录
     * @param adminLoginDTO 登录参数
     * @param ip 客户端 IP
     * @return 登录 VO
     */
    AdminLoginVO login(AdminLoginDTO adminLoginDTO, String ip) throws Exception;

    /**
     * 获取管理员信息
     * @return
     */
    AdminVO getAdminById();

    /**
     * 管理员退出登录
     * @param adminId 管理员 ID
     * @param token 当前登录令牌
     */
    void logout(Long adminId, String token);

    /**
     * 管理员修改密码
     * @param adminChangePasswordDTO
     */
    void changePassword(AdminChangePasswordDTO adminChangePasswordDTO) throws Exception;

    /**
     * 管理员更改昵称
     * @param adminChangeNicknameDTO
     */
    void changeNickname(AdminChangeNicknameDTO adminChangeNicknameDTO);

    /**
     * 管理员换绑邮箱
     * @param adminChangeEmailDTO
     */
    void changeEmail(AdminChangeEmailDTO adminChangeEmailDTO);
}
