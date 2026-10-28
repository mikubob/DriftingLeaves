package com.xuan.task;

import com.xuan.service.CounterBatchWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * 计数同步协调组件，串联稳定批次、事务落库、Redis 确认及所有者锁释放。
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class CounterSyncCoordinator {
    private final StringRedisTemplate redis;
    private final CounterBatchWriter writer;

    // A stable pending batch survives process failure and lease expiry. New increments stay in the original hash.
    static final DefaultRedisScript<String> SNAPSHOT = new DefaultRedisScript<>("""
            local batch = redis.call('HGET',KEYS[2],ARGV[1])
            if batch then return batch end
            local delta = redis.call('HGET',KEYS[1],ARGV[1])
            if not delta or tonumber(delta) == 0 then return nil end
            batch = ARGV[2] .. '|' .. delta
            redis.call('HSET',KEYS[2],ARGV[1],batch)
            return batch
            """, String.class);
    static final DefaultRedisScript<Long> ACK = new DefaultRedisScript<>("""
            if redis.call('HGET',KEYS[2],ARGV[1]) ~= ARGV[2] then return 0 end
            local delta = string.match(ARGV[2], '|(.+)$')
            redis.call('HINCRBY',KEYS[1],ARGV[1],-tonumber(delta))
            redis.call('HDEL',KEYS[2],ARGV[1])
            return 1
            """, Long.class);
    static final DefaultRedisScript<Long> UNLOCK = new DefaultRedisScript<>("""
            if redis.call('GET',KEYS[1]) == ARGV[1] then return redis.call('DEL',KEYS[1]) end
            return 0
            """, Long.class);

    /**
     * 持有所有者锁遍历增量，复用未确认批次；单篇失败保留批次供下轮重试。
     * @param metric view 或 like
     * @param counterKey Redis 增量 Hash
     * @param lockKey 同步任务锁键
     */
    public void sync(String metric, String counterKey, String lockKey) {
        String owner = UUID.randomUUID().toString();
        if (!Boolean.TRUE.equals(redis.opsForValue().setIfAbsent(lockKey, owner, Duration.ofMinutes(4)))) return;
        String batchKey = counterKey + ":batches";
        try {
            var ids = new HashSet<>(redis.opsForHash().keys(counterKey));
            ids.addAll(redis.opsForHash().keys(batchKey));
            for (Object rawId : ids) {
                try {
                    String id = rawId.toString();
                    Long articleId = Long.valueOf(id);
                    String batch = redis.execute(SNAPSHOT, List.of(counterKey, batchKey), id, UUID.randomUUID().toString());
                    if (batch == null) continue;
                    int separator = batch.indexOf('|');
                    writer.apply(metric, articleId, batch.substring(0, separator), Long.parseLong(batch.substring(separator + 1)));
                    redis.execute(ACK, List.of(counterKey, batchKey), id, batch);
                } catch (Exception ex) {
                    log.error("统计同步失败: metric={}, articleId={}", metric, rawId, ex);
                }
            }
        } finally {
            redis.execute(UNLOCK, List.of(lockKey), owner);
        }
    }
}
