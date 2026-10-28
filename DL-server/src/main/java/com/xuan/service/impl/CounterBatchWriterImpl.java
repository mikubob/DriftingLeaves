package com.xuan.service.impl;

import com.xuan.service.CounterBatchWriter;

import com.xuan.mapper.ArticleMapper;
import com.xuan.mapper.CounterBatchMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 计数批次落库实现，依赖独立事务代理保证批次登记与计数同时提交或回滚。
 */
@Service
@RequiredArgsConstructor
public class CounterBatchWriterImpl implements CounterBatchWriter {
    private final CounterBatchMapper batches;
    private final ArticleMapper articles;

    @Transactional
    @Override
    public void apply(String metric, Long articleId, String batchId, long delta) {
        if (batches.claim(batchId, metric, articleId, delta) == 0) return;
        if ("like".equals(metric)) articles.addLikeCount(articleId, delta);
        else if ("view".equals(metric)) articles.addViewCount(articleId, delta);
        else throw new IllegalArgumentException("Unknown counter: " + metric);
    }
}
