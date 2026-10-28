package com.xuan.service;

/**
 * 文章计数落库服务，在同一数据库事务内登记批次并累加计数。
 */
public interface CounterBatchWriter {
    /**
     * 应用稳定批次，已提交的批次直接跳过；数据库失败回滚批次登记和计数。
     * @param metric 计数类型，view 或 like
     * @param articleId 文章 ID
     * @param batchId 全局唯一的稳定批次 ID
     * @param delta 本批增量，取消点赞时可为负数
     * @throws IllegalArgumentException 未知计数类型
     */
    void apply(String metric, Long articleId, String batchId, long delta);
}
