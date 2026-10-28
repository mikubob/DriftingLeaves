package com.xuan.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xuan.dto.AdminChangeEmailDTO;
import com.xuan.dto.AdminChangeNicknameDTO;
import com.xuan.dto.AdminChangePasswordDTO;
import com.xuan.dto.AdminLoginDTO;
import com.xuan.entity.Admin;
import com.xuan.vo.AdminLoginVO;
import com.xuan.vo.AdminVO;

/**
 * 管理员账户服务，负责验证码、登录、会话及个人账户变更。
 */
public interface IAdminService extends IService<Admin> {
    /**
     * 向当前登录管理员的绑定邮箱发送换绑验证码。
     * @throws com.xuan.exception.AccountNotFoundException 当前账户不存在或不是管理员
     */
    void sendChangeEmailCode();

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
    AdminLoginVO login(AdminLoginDTO adminLoginDTO, String ip);

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
    void changePassword(AdminChangePasswordDTO adminChangePasswordDTO);

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
