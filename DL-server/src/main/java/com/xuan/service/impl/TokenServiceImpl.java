package com.xuan.service.impl;

import com.xuan.constant.JwtClaimsConstant;
import com.xuan.constant.RedisConstant;
import com.xuan.properties.JwtProperties;
import com.xuan.service.TokenService;
import com.xuan.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Token 服务实现类
 * 核心逻辑：JWT + Redis Set 实现多端登录与令牌管理
 *
 * @author Xuan
 */
@Slf4j
@Service
@RequiredArgsConstructor // Lombok 注解：自动生成包含 final 字段的构造器，推荐用于依赖注入
public class TokenServiceImpl implements TokenService {

    /**
     * 单用户最大 Token 数量限制
     * 防止恶意用户通过大量登录耗尽 Redis 内存
     */
    private static final int MAX_TOKEN_COUNT = 10;

    /**
     * Redis 模板
     * 泛型 <String, Object> 表示 Key 为字符串，Value 为对象（此处实际存储字符串 Token）
     */
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * JWT 配置属性
     * 包含密钥、过期时间等
     */
    private final JwtProperties jwtProperties;

    /**
     * 创建 JWT 令牌并存储到 Redis
     *
     * @param userId 用户 ID
     * @param role   用户角色
     * @return 生成的 JWT 令牌字符串
     */
    @Override
    public String createAndStoreToken(Long userId, Integer role) {
        // 1. 构建 JWT 的 Claims (载荷)
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.ADMIN_ID, userId);
        claims.put(JwtClaimsConstant.ADMIN_ROLE, role);

        // 2. 生成 JWT 字符串
        // 注意：JwtUtil 内部会根据 ttl 设置 JWT 自身的 exp 过期时间
        String token = JwtUtil.createJWT(
                jwtProperties.getSecretKey(),
                jwtProperties.getTtl(), // 单位：毫秒 (例如 172800000)
                claims
        );

        // 3. 构建 Redis Key
        // 格式示例：token:active:1001
        String tokenKey = RedisConstant.TOKEN_PREFIX + userId;

        // 4. 检查 Token 数量限制
        // 防止恶意用户通过大量登录耗尽 Redis 内存
        Long tokenCount = redisTemplate.opsForSet().size(tokenKey);
        if (tokenCount != null && tokenCount >= MAX_TOKEN_COUNT) {
            log.warn("用户 [{}] Token 数量超过限制: {}，拒绝创建新 Token", userId, tokenCount);
            throw new IllegalStateException("Token 数量已达上限，请先登出其他设备");
        }

        // 获取过期时间 (单位：毫秒)
        long ttlMillis = jwtProperties.getTtl();

        // 5. 将 Token 存入 Redis Set 并设置过期时间
        // 【关键点】使用 RedisCallback 保证以下两个操作的原子性：
        //    a. 将 token 加入集合 (允许同一用户多端登录)
        //    b. 设置整个 Key 的过期时间
        // 如果分两步执行，可能在 add 成功后、expire 执行前宕机，导致永不过期的脏数据
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            // 转换为 byte[] 以避免 Spring 默认的序列化开销，提高性能
            byte[] keyBytes = tokenKey.getBytes(StandardCharsets.UTF_8);
            byte[] tokenBytes = token.getBytes(StandardCharsets.UTF_8);

            // 将 token 添加到 Set 集合中
            // 如果 token 已存在，不会重复添加 (Set 特性)
            connection.sAdd(keyBytes, tokenBytes);

            // 【重要修复】设置过期时间
            // connection.expire(key, seconds) -> 单位是秒
            // connection.pExpire(key, milliseconds) -> 单位是毫秒
            // 由于配置文件 ttl 是毫秒 (172800000)，必须使用 pExpire！
            // 如果误用 expire，过期时间会变成 1.72亿秒 (约 5.5 年)
            connection.pExpire(keyBytes, ttlMillis);

            return null;
        });

        log.info("用户 [{}] 生成新 Token，Redis Key: {}, 过期时间: {}ms", userId, tokenKey, ttlMillis);
        return token;
    }

    /**
     * 验证 Token 有效性
     *
     * @param userId 用户 ID
     * @param token  待验证的 Token
     * @return true: 有效 (存在于 Redis 且未被登出); false: 无效
     */
    @Override
    public boolean isValidToken(Long userId, String token) {
        // 空值校验：防止 NPE
        if (userId == null || token == null || token.isEmpty()) {
            log.warn("Token 验证失败：参数无效 - userId: {}, token: {}", userId, token == null ? "null" : "empty");
            return false;
        }

        String tokenKey = RedisConstant.TOKEN_PREFIX + userId;

        // 1. 检查 Redis 中是否存在该 Token
        // 这一步主要校验：用户是否主动登出 (logout) 或 管理员强制踢人
        // 注意：这里不校验 JWT 的签名和时间戳，通常建议在拦截器中先校验 JWT 自身有效性，再调用此方法
        Boolean isMember = redisTemplate.opsForSet().isMember(tokenKey, token);

        // 处理可能的 null 值，确保返回布尔值
        boolean isValid = Boolean.TRUE.equals(isMember);

        if (!isValid) {
            log.warn("Token 验证失败：用户 [{}] 的 Token 不在 Redis 白名单中 (可能已登出或过期)", userId);
        }

        return isValid;
    }

    /**
     * 单端登出 (退出当前设备)
     * 仅删除指定的 Token，其他设备的 Token 依然有效
     *
     * @param userId 用户 ID
     * @param token  要失效的 Token
     */
    @Override
    public void logout(Long userId, String token) {
        String tokenKey = RedisConstant.TOKEN_PREFIX + userId;
        
        // 从 Set 中移除指定的 token
        Long removedCount = redisTemplate.opsForSet().remove(tokenKey, token);
        
        if (removedCount != null && removedCount > 0) {
            log.info("用户 [{}] 单端登出成功，移除 Token", userId);
        } else {
            log.warn("用户 [{}] 登出失败：Redis 中未找到该 Token", userId);
        }
    }

    /**
     * 全端登出 (退出所有设备)
     * 直接删除整个 Key，该用户所有设备的 Token 立即失效
     *
     * @param userId 用户 ID
     */
    @Override
    public void logoutAll(Long userId) {
        String tokenKey = RedisConstant.TOKEN_PREFIX + userId;
        
        Boolean deleted = redisTemplate.delete(tokenKey);
        
        if (Boolean.TRUE.equals(deleted)) {
            log.info("用户 [{}] 全端登出成功，所有 Token 已清除", userId);
        } else {
            log.warn("用户 [{}] 全端登出：Redis 中未找到对应的 Key", userId);
        }
    }
}