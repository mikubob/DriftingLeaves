package com.xuan.mapper;

import org.apache.ibatis.annotations.*;

/**
 * 计数批次去重持久层，批次登记与文章计数由业务服务在同一事务内提交。
 */
@Mapper
public interface CounterBatchMapper {
    /**
     * 登记尚未应用的稳定批次，调用方必须与文章更新共用事务。
     * @param batchId 批次 ID
     * @param metric view 或 like
     * @param articleId 文章 ID
     * @param delta 本批增量
     * @return 1 表示首次登记，0 表示批次已存在
     */
    @Insert("INSERT IGNORE INTO counter_sync_batches(batch_id,metric,article_id,delta) VALUES(#{batchId},#{metric},#{articleId},#{delta})")
    int claim(@Param("batchId") String batchId, @Param("metric") String metric,
              @Param("articleId") Long articleId, @Param("delta") long delta);
}
