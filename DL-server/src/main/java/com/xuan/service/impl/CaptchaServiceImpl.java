package com.xuan.service.impl;

import com.xuan.service.CaptchaService;

import com.xuan.context.VisitorContext;
import com.xuan.exception.ValidationException;
import com.xuan.vo.CaptchaVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * 访客验证码服务实现，使用 Redis 保存限时挑战并原子消费答案。
 */
@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {
    private final StringRedisTemplate redis;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final DefaultRedisScript<String> CONSUME = new DefaultRedisScript<>(
            "local v=redis.call('GET',KEYS[1]); redis.call('DEL',KEYS[1]); return v", String.class);

    @Override
    public CaptchaVO generate() {
        Long visitorId = VisitorContext.requireId();
        int a = RANDOM.nextInt(9) + 1, b = RANDOM.nextInt(9) + 1;
        String id = UUID.randomUUID().toString();
        redis.opsForValue().set(key(visitorId, id), String.valueOf(a + b), Duration.ofMinutes(5));
        return CaptchaVO.builder().captchaId(id).question(a + " + " + b + " = ?").build();
    }

    @Override
    public void verify(String id, String answer) {
        Long visitorId = VisitorContext.requireId();
        if (id == null || id.length() > 64) throw new ValidationException("验证码无效，请刷新");
        String expected = redis.execute(CONSUME, List.of(key(visitorId, id)));
        if (expected == null || answer == null || !answer.matches("[0-9]{1,3}") || !expected.equals(answer)) {
            throw new ValidationException("验证码错误或已过期，请刷新");
        }
    }

    private String key(Long visitorId, String id) { return "captcha:" + visitorId + ":" + id; }
}
