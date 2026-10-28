package com.xuan.service.impl;

import com.xuan.service.VerifyCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static com.xuan.constant.RedisConstant.*;

/**
 * 邮件验证码服务实现，原子预约与消费验证码，并维护独立冷却、尝试次数及锁定。
 */
@Service
@RequiredArgsConstructor
public class VerifyCodeServiceImpl implements VerifyCodeService {
    private final StringRedisTemplate redis;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final DefaultRedisScript<Long> RESERVE = new DefaultRedisScript<>("""
            if redis.call('EXISTS',KEYS[3]) == 1 or redis.call('EXISTS',KEYS[2]) == 1 then return 0 end
            redis.call('SET',KEYS[1],ARGV[2] .. '|' .. ARGV[1],'EX',300)
            redis.call('SET',KEYS[2],ARGV[2],'EX',60)
            return 1
            """, Long.class);
    private static final DefaultRedisScript<Long> CANCEL = new DefaultRedisScript<>("""
            local value=redis.call('GET',KEYS[1]); if value and string.sub(value,1,36) == ARGV[1] then redis.call('DEL',KEYS[1]) end
            if redis.call('GET',KEYS[2]) == ARGV[1] then redis.call('DEL',KEYS[2]) end
            return 1
            """, Long.class);
    private static final DefaultRedisScript<Long> VERIFY = new DefaultRedisScript<>("""
            if redis.call('EXISTS',KEYS[3]) == 1 then return 0 end
            local code = redis.call('GET',KEYS[1])
            if code and string.sub(code,38) == ARGV[1] then
                redis.call('DEL',KEYS[1],KEYS[4])
                return 1
            end
            local attempts = redis.call('INCR',KEYS[4])
            if attempts == 1 then redis.call('EXPIRE',KEYS[4],300) end
            if attempts >= 5 then redis.call('SET',KEYS[3],'1','EX',1800) end
            return 0
            """, Long.class);

    @Override public String generateCode() { return String.format("%06d", RANDOM.nextInt(1_000_000)); }
    @Override public String reserveCode(Long id, String code) {
        String owner = java.util.UUID.randomUUID().toString();
        return Long.valueOf(1).equals(redis.execute(RESERVE, keys(id), code, owner)) ? owner : null;
    }
    @Override public void cancelCode(Long id, String reservation) { redis.execute(CANCEL, keys(id), reservation); }
    @Override public boolean verifyCode(Long id, String code) {
        return Long.valueOf(1).equals(redis.execute(VERIFY, keys(id), code == null ? "" : code));
    }
    @Override public Long getRemainingCooldown(Long id) { return ttl(KEY_RATE_LIMIT_PREFIX + id, TimeUnit.SECONDS); }
    @Override public boolean isLocked(Long id) { return Boolean.TRUE.equals(redis.hasKey(KEY_LOCK_PREFIX + id)); }
    @Override public Long getLockRemainingMinutes(Long id) { return ttl(KEY_LOCK_PREFIX + id, TimeUnit.MINUTES); }
    @Override public boolean canAttempt(Long id) { return !isLocked(id); }
    @Override public Long getRemainingAttempts(Long id) {
        if (isLocked(id)) return 0L;
        String attempts = redis.opsForValue().get(KEY_ATTEMPT_COUNT_PREFIX + id);
        return Math.max(0L, 5L - (attempts == null ? 0L : Long.parseLong(attempts)));
    }
    private Long ttl(String key, TimeUnit unit) { Long ttl = redis.getExpire(key, unit); return ttl == null ? 0L : Math.max(0, ttl); }
    /**
     * 按固定 Lua 参数顺序组装验证码、冷却、锁定及失败计数键。
     * @param id 管理员 ID
     * @return 同一次校验使用的四个 Redis 键
     */
    private List<String> keys(Long id) {
        return List.of(KEY_VERIFY_CODE_PREFIX + "v2:" + id, KEY_RATE_LIMIT_PREFIX + id,
                KEY_LOCK_PREFIX + id, KEY_ATTEMPT_COUNT_PREFIX + id);
    }
}
