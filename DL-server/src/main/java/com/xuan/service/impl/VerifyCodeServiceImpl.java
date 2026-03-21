package com.xuan.service.impl;

import com.xuan.service.VerifyCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.xuan.constant.RedisConstant.KEY_ATTEMPT_COUNT;
import static com.xuan.constant.RedisConstant.KEY_LOCK;
import static com.xuan.constant.RedisConstant.KEY_RATE_LIMIT;
import static com.xuan.constant.RedisConstant.KEY_VERIFY_CODE;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerifyCodeServiceImpl implements VerifyCodeService {

    private final RedisTemplate<String,Object> redisTemplate;


    // 时间常量
    private static final int RATE_LIMIT_SECONDS = 60; // 发送频率限制60秒
    private static final int CODE_TTL_MINUTES = 5;    // 验证码有效期5分钟
    private static final int MAX_ATTEMPTS = 5;        // 最大尝试次数
    private static final int LOCK_MINUTES = 30;       // 锁定30分钟


    /**
     * 生成验证码
     * @return 验证码
     */
    @Override
    public String generateCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1_000_000));// 生成6位数字验证码
    }

    /**
     * 保存验证码
     * @param code 验证码
     */
    @Override
    public void saveCode(String code) {
        // 保存验证码
        redisTemplate.opsForValue().set(KEY_VERIFY_CODE, code, CODE_TTL_MINUTES, TimeUnit.MINUTES);
        // 设置发送频率限制
        redisTemplate.opsForValue().set(KEY_RATE_LIMIT, "1", RATE_LIMIT_SECONDS, TimeUnit.SECONDS);
        // 重置尝试计数和锁定状态
        redisTemplate.delete(KEY_ATTEMPT_COUNT);
        redisTemplate.delete(KEY_LOCK);
    }

    /**
     * 判断是否可以发送验证码
     * @return 是否可以发送验证码
     */
    @Override
    public boolean canSendCode() {
        return redisTemplate.opsForValue().get(KEY_RATE_LIMIT) == null;
    }

    /**
     * 获取剩余冷却时间(秒)
     * @return 剩余冷却时间
     */
    @Override
    public Long getRemainingCooldown() {
        Long ttl = redisTemplate.getExpire(KEY_RATE_LIMIT, TimeUnit.SECONDS);
        return ttl != null ? Math.max(ttl, 0) : 0;
    }

    /**
     * 判断是否被锁定
     * @return 是否被锁定
     */
    @Override
    public boolean isLocked() {
        return Boolean.TRUE.equals(redisTemplate.hasKey(KEY_LOCK));
    }

    /**
     * 获取锁定剩余时间(分钟)
     * @return 锁定剩余时间
     */
    @Override
    public Long getLockRemainingMinutes() {
        Long ttl = redisTemplate.getExpire(KEY_LOCK, TimeUnit.MINUTES);
        return ttl != null ? Math.max(ttl, 0) : 0;
    }

    /**
     * 判断是否允许尝试
     * @return 是否允许尝试
     */
    @Override
    public boolean canAttempt() {
        // 检查是否被锁定
        return !isLocked();
    }

    /**
     * 验证验证码
     * @param code 验证码
     * @return 验证结果
     */
    @Override
    public boolean verifyCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        // 检查是否被锁定
        if (isLocked()) {
            return false;
        }

        // 检查验证码是否存在
        String savedCode = (String) redisTemplate.opsForValue().get(KEY_VERIFY_CODE);
        if (savedCode == null) {
            // 验证失败，记录尝试
            recordFailedAttempt();
            return false;
        }

        // 验证
        boolean success = savedCode.equals(code.trim());

        if (success) {
            // 验证成功
            clearAll(); // 清空所有验证相关数据
            return true;
        } else {
            // 验证失败，记录尝试
            recordFailedAttempt();
            return false;
        }
    }

    /**
     * 获取剩余尝试次数
     * @return 剩余尝试次数
     */
    @Override
    public Long getRemainingAttempts() {
        if (isLocked()) {
            return 0L;
        }
        Long attemptCount = getAttemptCount();
        return Math.max(MAX_ATTEMPTS - attemptCount, 0);
    }

    // 记录失败尝试
    private void recordFailedAttempt() {
        // 增加失败计数
        Long attemptCount = redisTemplate.opsForValue().increment(KEY_ATTEMPT_COUNT, 1L);
        if (attemptCount == null) {
            attemptCount = 1L;
        }

        // 如果是第一次失败，设置过期时间
        if (attemptCount == 1) {
            long codeTtl = redisTemplate.getExpire(KEY_VERIFY_CODE, TimeUnit.SECONDS);
            if (codeTtl > 0) {
                redisTemplate.expire(KEY_ATTEMPT_COUNT, codeTtl, TimeUnit.SECONDS);
            }
        }

        // 达到最大尝试次数，锁定
        if (attemptCount >= MAX_ATTEMPTS) {
            redisTemplate.opsForValue().set(KEY_LOCK, "1", LOCK_MINUTES, TimeUnit.MINUTES);
        }
    }

    // 重置状态
    public void clearAll() {
        redisTemplate.delete(KEY_VERIFY_CODE);
        redisTemplate.delete(KEY_RATE_LIMIT);
        redisTemplate.delete(KEY_ATTEMPT_COUNT);
        redisTemplate.delete(KEY_LOCK);
    }

    // 获取当前尝试次数
    public Long getAttemptCount() {
        try {
            Object value = redisTemplate.opsForValue().get(KEY_ATTEMPT_COUNT);
            if (value == null) {
                return 0L;
            }
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
            return 0L;
        } catch (Exception e) {
            return 0L;
        }
    }
}
