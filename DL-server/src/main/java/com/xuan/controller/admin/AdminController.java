package com.xuan.controller.admin;

import com.xuan.annotation.RateLimit;
import com.xuan.dto.AdminChangeEmailDTO;
import com.xuan.dto.AdminChangeNicknameDTO;
import com.xuan.dto.AdminChangePasswordDTO;
import com.xuan.dto.AdminLoginDTO;
import com.xuan.dto.AdminLogoutDTO;
import com.xuan.dto.SendCodeDTO;
import com.xuan.properties.JwtProperties;
import com.xuan.result.Result;
import com.xuan.service.IAdminService;
import com.xuan.utils.IpUtil;
import com.xuan.vo.AdminLoginVO;
import com.xuan.vo.AdminVO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 管理端管理员接口
 */
@RestController
@RequestMapping("/admin/admin")
@Slf4j
@RequiredArgsConstructor
public class AdminController {


    private final IAdminService adminService;
    private final JwtProperties jwtProperties;

    /**
     * 发送验证码
     */
    @PostMapping("/sendCode")
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8,
            timeWindow = 60, message = "操作过于频繁，请稍后再试")
    public Result sendCode(@RequestBody SendCodeDTO sendCodeDTO) {
        log.info("发送验证码, {}", sendCodeDTO);
        adminService.sendVerifyCode(sendCodeDTO.getUsername());
        return Result.success();
    }

    /**
     * 管理员登录
     * 登录成功后 Token 写入 HttpOnly Cookie，前端 JS 无法读取
     */
    @PostMapping("/login")
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8,
            timeWindow = 60, message = "操作过于频繁，请稍后再试")
    public ResponseEntity<Result<AdminLoginVO>> adminLogin(@Valid @RequestBody AdminLoginDTO adminLoginDTO,
                                                           HttpServletRequest request,
                                                           HttpServletResponse response) throws Exception {
        log.info("管理员登录：{}", adminLoginDTO);
        String ip = IpUtil.getClientIp(request);
        AdminLoginVO adminLoginVO = adminService.login(adminLoginDTO, ip);

        // 将 Token 写入 HttpOnly Cookie，避免前端 JS 读取
        if (adminLoginVO.getToken() != null) {
            addTokenCookie(response, adminLoginVO.getToken(), jwtProperties.getTtl() / 1000, request.isSecure());
        }

        // 返回给前端的 VO 不包含 Token
        AdminLoginVO safeVO = AdminLoginVO.builder()
                .id(adminLoginVO.getId())
                .build();
        return ResponseEntity.ok(Result.success(safeVO));
    }

    /**
     * 获取管理员信息
     */
    @GetMapping
    public Result<AdminVO> getAdminInfo() {
        AdminVO adminVO = adminService.getAdminById();
        return Result.success(adminVO);
    }

    /**
     * 管理员退出登录
     */
    @PostMapping("/logout")
    public Result logout(@RequestBody AdminLogoutDTO adminLogoutDTO, HttpServletRequest request, HttpServletResponse response) {
        log.info("管理员退出登录：{}", adminLogoutDTO);
        String token = getTokenFromRequest(request);
        adminService.logout(adminLogoutDTO.getId(), token);
        clearTokenCookie(response, request.isSecure());
        return Result.success();
    }

    /**
     * 管理员修改密码
     */
    @PutMapping("/changePassword")
    public Result changePassword(@Valid @RequestBody AdminChangePasswordDTO adminChangePasswordDTO) throws Exception {
        log.info("管理员修改密码：{}", adminChangePasswordDTO);
        adminService.changePassword(adminChangePasswordDTO);
        return Result.success();
    }

    /**
     * 管理员更改昵称
     */
    @PutMapping("/changeNickname")
    public Result changeNickname(@Valid @RequestBody AdminChangeNicknameDTO adminChangeNicknameDTO) {
        log.info("管理员更改昵称：{}", adminChangeNicknameDTO);
        adminService.changeNickname(adminChangeNicknameDTO);
        return Result.success();
    }

    /**
     * 管理员换绑邮箱
     */
    @PutMapping("/changeEmail")
    public Result changeEmail(@Valid @RequestBody AdminChangeEmailDTO adminChangeEmailDTO) {
        log.info("管理员换绑邮箱：{}", adminChangeEmailDTO);
        adminService.changeEmail(adminChangeEmailDTO);
        return Result.success();
    }

    /**
     * 从请求 Cookie 中读取 Token（自动 URL 解码）
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (jwtProperties.getCookieName().equals(cookie.getName())) {
                return URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    /**
     * 将 Token 写入 HttpOnly Cookie
     */
    private void addTokenCookie(HttpServletResponse response, String token, long maxAgeSeconds, boolean secure) {
        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        String secureFlag = secure ? "; Secure" : "";
        String cookieValue = String.format(
                "%s=%s; Max-Age=%d; Path=/; HttpOnly%s; SameSite=Strict",
                jwtProperties.getCookieName(), encodedToken, maxAgeSeconds, secureFlag
        );
        response.addHeader("Set-Cookie", cookieValue);
    }

    /**
     * 清除 Token Cookie
     */
    private void clearTokenCookie(HttpServletResponse response, boolean secure) {
        String secureFlag = secure ? "; Secure" : "";
        String cookieValue = String.format(
                "%s=; Max-Age=0; Path=/; HttpOnly%s; SameSite=Strict",
                jwtProperties.getCookieName(), secureFlag
        );
        response.addHeader("Set-Cookie", cookieValue);
    }
}
