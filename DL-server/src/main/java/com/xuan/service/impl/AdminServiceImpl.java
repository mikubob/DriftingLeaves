package com.xuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.constant.MessageConstant;
import com.xuan.constant.StatusConstant;
import com.xuan.context.BaseContext;
import com.xuan.dto.AdminChangeEmailDTO;
import com.xuan.dto.AdminChangeNicknameDTO;
import com.xuan.dto.AdminChangePasswordDTO;
import com.xuan.dto.AdminLoginDTO;
import com.xuan.dto.AdminLogoutDTO;
import com.xuan.entity.Admin;
import com.xuan.exception.AccountNotFoundException;
import com.xuan.exception.PasswordErrorException;
import com.xuan.exception.VerifyCodeCoolDownException;
import com.xuan.exception.VerifyCodeErrorException;
import com.xuan.exception.VerifyCodeLockException;
import com.xuan.exception.VisitorSendCodeException;
import com.xuan.mapper.AdminMapper;
import com.xuan.properties.VisitorProperties;
import com.xuan.service.EmailService;
import com.xuan.service.EncryptPasswordService;
import com.xuan.service.IAdminService;
import com.xuan.service.TokenService;
import com.xuan.service.VerifyCodeService;
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

    private final VerifyCodeService verifyCodeService;
    private final EmailService emailService;
    private final TokenService tokenService;
    private final VisitorProperties visitorProperties;
    private final EncryptPasswordService encryptPasswordService;

    /**
     * 发送验证码
     *
     * @param username 用户名
     */
    @Override
    public void sendVerifyCode(String username) {
        // 1. 验证用户是否存在
        Admin admin = getByUsername(username);
        if (admin == null) {
            // 账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 2. 游客无须邮箱验证码
        if (admin.getRole().equals(StatusConstant.DISABLE)) {
            throw new VisitorSendCodeException(MessageConstant.VISITOR_VERIFY_CODE_ERROR
                    + visitorProperties.getVerifyCode());
        }

        // 3. 检查是否可以发送验证码
        if (!verifyCodeService.canSendCode()) {
            Long cooldown = verifyCodeService.getRemainingCooldown();
            throw new VerifyCodeCoolDownException("验证码冷却中，请等待" + cooldown + "秒后重试");
        }

        // 4. 生成并保存验证码
        String code = verifyCodeService.generateCode();
        String email = admin.getEmail();

        verifyCodeService.saveCode(code);

        // 5. 发送验证码
        emailService.sendVerifyCode(email, code);
        log.info("发送验证码成功：username={}, email={}", username, email);
    }

    /**
     * 管理员登录
     *
     * @param adminLoginDTO 登录参数
     * @return 登录 VO
     */
    @Override
    public AdminLoginVO login(AdminLoginDTO adminLoginDTO) throws Exception {
        String username = adminLoginDTO.getUsername();
        String password = adminLoginDTO.getPassword();

        // 1. 验证用户是否存在
        Admin admin = getByUsername(username);
        if (admin == null) {
            // 账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 2. 对密码进行加密
        String hashedPassword = encryptPasswordService.hashPassword(password, admin.getSalt());

        // 3. 验证密码是否正确
        if (!hashedPassword.equals(admin.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        // 4. 区分游客和管理员校验验证码
        if (admin.getRole() == StatusConstant.ENABLE) {
            // 管理员需要校验邮箱验证码

            // 检查是否可以校验验证码
            if (!verifyCodeService.canAttempt()) {
                Long lockRemainingMinutes = verifyCodeService.getLockRemainingMinutes();
                throw new VerifyCodeLockException(MessageConstant.VERIFY_CODE_LOCK + lockRemainingMinutes + "分钟");
            }

            // 校验验证码是否正确
            boolean isValid = verifyCodeService.verifyCode(adminLoginDTO.getCode());
            if (!isValid) {
                Long remainingAttempts = verifyCodeService.getRemainingAttempts();
                throw new VerifyCodeErrorException(MessageConstant.VERIFY_CODE_ERROR
                        + ",还可以试" + remainingAttempts + "次");
            }

        } else {
            // 游客直接校验固定验证码
            if (!adminLoginDTO.getCode().equals(visitorProperties.getVerifyCode())) {
                throw new VerifyCodeErrorException(MessageConstant.VERIFY_CODE_ERROR
                        + ",请输入:" + visitorProperties.getVerifyCode());
            }
        }

        // 5. 生成并存储 token
        String token = tokenService.createAndStoreToken(admin.getId(), admin.getRole());

        log.info("管理员登录成功：username={}, id={}, role={}", username, admin.getId(), admin.getRole());

        return AdminLoginVO.builder()
                .id(admin.getId())
                .token(token)
                .build();
    }

    /**
     * 获取管理员信息
     *
     * @return 管理员信息 VO
     */
    @Override
    public AdminVO getAdminById() {
        // 1. 获取当前登录的管理员 ID
        Long adminId = BaseContext.getCurrentId();

        // 2. 查询管理员信息
        Admin admin = getById(adminId);
        if (admin == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 3. 构造管理员信息
        return AdminVO.builder()
                .id(adminId)
                .nickname(admin.getNickname())
                .email(admin.getEmail())
                .build();
    }

    /**
     * 管理员退出登录
     *
     * @param adminLogoutDTO 退出登录参数
     */
    @Override
    public void logout(AdminLogoutDTO adminLogoutDTO) {
        // 删除 Redis 中的 token
        tokenService.logout(adminLogoutDTO.getId(), adminLogoutDTO.getToken());
        log.info("管理员退出登录成功：id={}", adminLogoutDTO.getId());
    }

    /**
     * 管理员修改密码
     *
     * @param adminChangePasswordDTO 修改密码参数
     */
    @Override
    public void changePassword(AdminChangePasswordDTO adminChangePasswordDTO) throws Exception {
        // 1. 获取当前管理员
        Long adminId = BaseContext.getCurrentId();
        Admin admin = getById(adminId);
        if (admin == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 2. 验证两次输入的新密码是否一致
        if (!adminChangePasswordDTO.getNewPassword().equals(adminChangePasswordDTO.getConfirmNewPassword())) {
            throw new PasswordErrorException(MessageConstant.NEW_PASSWORD_NOT_MATCH);
        }

        // 3. 验证旧密码是否正确
        String hashedOldPassword = encryptPasswordService.hashPassword(
                adminChangePasswordDTO.getOldPassword(), admin.getSalt());
        if (!hashedOldPassword.equals(admin.getPassword())) {
            throw new PasswordErrorException(MessageConstant.OLD_PASSWORD_ERROR);
        }

        // 4. 获取加密后的新密码
        String hashedNewPassword = encryptPasswordService.hashPassword(
                adminChangePasswordDTO.getNewPassword(), admin.getSalt());

        // 5. 验证新密码是否与旧密码一致
        if (hashedNewPassword.equals(admin.getPassword())) {
            throw new PasswordErrorException(MessageConstant.NEW_PASSWORD_NOT_CHANGE);
        }

        // 6. 更新管理员密码
        Admin updateAdmin = Admin.builder()
                .id(adminId)
                .password(hashedNewPassword)
                .build();
        updateById(updateAdmin);

        // 7. 登出所有设备
        tokenService.logoutAll(adminId);

        log.info("管理员修改密码成功：id={}", adminId);
    }

    /**
     * 管理员更改昵称
     *
     * @param adminChangeNicknameDTO 更改昵称参数
     */
    @Override
    public void changeNickname(AdminChangeNicknameDTO adminChangeNicknameDTO) {
        // 1. 获取当前管理员
        Long adminId = BaseContext.getCurrentId();
        Admin admin = getById(adminId);
        if (admin == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 2. 更新昵称
        Admin updateAdmin = Admin.builder()
                .id(adminId)
                .nickname(adminChangeNicknameDTO.getNickname())
                .build();
        updateById(updateAdmin);

        log.info("管理员更改昵称成功：id={}, nickname={}", adminId, adminChangeNicknameDTO.getNickname());
    }

    /**
     * 管理员换绑邮箱
     *
     * @param adminChangeEmailDTO 换绑邮箱参数
     */
    @Override
    public void changeEmail(AdminChangeEmailDTO adminChangeEmailDTO) {
        // 1. 获取当前管理员
        Long adminId = BaseContext.getCurrentId();
        Admin admin = getById(adminId);
        if (admin == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 2. 校验邮箱验证码

        // 检查是否可以校验验证码
        if (!verifyCodeService.canAttempt()) {
            Long lockRemainingMinutes = verifyCodeService.getLockRemainingMinutes();
            throw new VerifyCodeLockException(MessageConstant.VERIFY_CODE_LOCK + lockRemainingMinutes + "分钟");
        }

        // 校验验证码是否正确
        boolean isValid = verifyCodeService.verifyCode(adminChangeEmailDTO.getCode());
        if (!isValid) {
            Long remainingAttempts = verifyCodeService.getRemainingAttempts();
            throw new VerifyCodeErrorException(MessageConstant.VERIFY_CODE_ERROR
                    + ",还可以试" + remainingAttempts + "次");
        }

        // 3. 更新邮箱
        Admin updateAdmin = Admin.builder()
                .id(adminId)
                .email(adminChangeEmailDTO.getEmail())
                .build();
        updateById(updateAdmin);

        log.info("管理员换绑邮箱成功：id={}, email={}", adminId, adminChangeEmailDTO.getEmail());
    }

    // <========== 私有辅助方法 ==========>

    /**
     * 根据用户名查询管理员
     *
     * @param username 用户名
     * @return 管理员实体
     */
    private Admin getByUsername(String username) {
        return lambdaQuery()
                .eq(Admin::getUsername, username)
                .one();
    }
}
