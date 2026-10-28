package com.xuan.controller.blog;

import com.xuan.annotation.RateLimit;
import com.xuan.result.Result;
import com.xuan.service.CaptchaService;
import com.xuan.vo.CaptchaVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 博客公共接口，向已验证访客签发服务端算术验证码。
 */
@RestController("blogCommonController")
@RequestMapping("/blog/common")
@RequiredArgsConstructor
public class CommonController {
    private final CaptchaService captchaService;

    /**
     * 为当前已验证访客生成五分钟、一次使用的算术挑战。
     * @return 仅包含题目及挑战 ID 的结果
     */
    @GetMapping("/captcha/generate")
    @RateLimit(type = RateLimit.Type.IP, tokens = 10, burstCapacity = 15, timeWindow = 60)
    public Result<CaptchaVO> generateCaptcha() {
        return Result.success(captchaService.generate());
    }
}
