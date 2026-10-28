package com.xuan.task;

import com.xuan.constant.RedisConstant;
import com.xuan.task.CounterSyncCoordinator;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 文章点赞计数定时任务，触发共享同步协调组件。
 */
@Component
@RequiredArgsConstructor
public class LikeCountSyncTask {
    private final CounterSyncCoordinator counters;

    /**
     * 定期同步点赞增量，批次去重和失败重试由协调组件负责。
     */
    @Scheduled(fixedRate = 5 * 60 * 1000, initialDelay = 2 * 60 * 1000)
    public void syncLikeCountToMySQL() {
        counters.sync("like", RedisConstant.ARTICLE_LIKE_COUNT, RedisConstant.LOCK_LIKE_COUNT_SYNC);
    }
}
