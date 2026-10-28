package com.xuan.controller.blog;

import com.xuan.annotation.RateLimit;
import com.xuan.dto.RssSubscriptionDTO;
import com.xuan.result.Result;
import com.xuan.service.IRssSubscriptionService;
import com.xuan.vo.RssSubscriptionStatusVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 博客端 RSS 订阅接口
 */
@Slf4j
@RestController("blogRssSubscriptionController")
@RequestMapping("/blog/rssSubscription")
@RequiredArgsConstructor
public class RssSubscriptionController {

    private final IRssSubscriptionService rssSubscriptionService;

    /**
     * 更新当前访客的订阅；换邮箱需重新通过邮件确认。
     * @param dto 订阅资料
     * @return 操作结果
     */
    @PutMapping
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8, timeWindow = 60)
    public Result editSubscription(@Valid @RequestBody RssSubscriptionDTO dto) {
        rssSubscriptionService.addSubscription(dto);
        return Result.success();
    }

    /**
     * 由发起订阅的浏览器确认邮箱并绑定订阅。
     * @param token 邮件确认票据
     * @return 确认结果
     */
    @PostMapping("/confirm")
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8, timeWindow = 60)
    public Result confirm(@RequestParam String token) {
        rssSubscriptionService.confirm(token);
        return Result.success();
    }

    /**
     * 添加RSS订阅
     * @param rssSubscriptionDTO
     * @return
     */
    @PostMapping
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8,
            timeWindow = 60, message = "操作过于频繁，请稍后再试")
    public Result addSubscription(@Valid @RequestBody RssSubscriptionDTO rssSubscriptionDTO) {
        log.info("添加RSS订阅,{}", rssSubscriptionDTO);
        rssSubscriptionService.addSubscription(rssSubscriptionDTO);
        return Result.success();
    }

    /**
     * 凭签名退订链接取消对应订阅，重复调用幂等。
     * @param token 绑定邮箱和确认版本的退订凭据
     * @return 退订结果
     */
    @PutMapping("/unsubscribe")
    public Result unsubscribe(@RequestParam String token) {
        log.info("取消RSS订阅");
        rssSubscriptionService.unsubscribe(token);
        return Result.success();
    }

    /**
     * 检查访客订阅状态（返回订阅详情）
     */
    @GetMapping("/check")
    public Result<RssSubscriptionStatusVO> checkSubscription(@RequestParam(required = false) Long visitorId) {
        log.info("检查订阅状态: visitorId={}", visitorId);
        RssSubscriptionStatusVO status = rssSubscriptionService.getSubscriptionStatus(visitorId);
        return Result.success(status);
    }
}
