package com.xuan.task;

import com.xuan.constant.RedisConstant;
import com.xuan.task.CounterSyncCoordinator;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 文章浏览计数定时任务，触发共享同步协调组件。
 */
@Component
@RequiredArgsConstructor
public class ViewCountSyncTask {
    private final CounterSyncCoordinator counters;

    /**
     * 定期同步浏览增量，批次去重和失败重试由协调组件负责。
     */
    @Scheduled(fixedRate = 5 * 60 * 1000, initialDelay = 1 * 60 * 1000)
    public void syncViewCountToMySQL() {
        counters.sync("view", RedisConstant.ARTICLE_VIEW_COUNT, RedisConstant.LOCK_VIEW_COUNT_SYNC);
    }
}
