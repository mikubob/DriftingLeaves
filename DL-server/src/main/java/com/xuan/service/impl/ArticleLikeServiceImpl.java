package com.xuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.constant.RedisConstant;
import com.xuan.entity.ArticleLikes;
import com.xuan.mapper.ArticleLikeMapper;
import com.xuan.service.IArticleLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 文章点赞服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleLikeServiceImpl extends ServiceImpl<ArticleLikeMapper, ArticleLikes> implements IArticleLikeService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 点赞文章
     *
     * @param articleId 文章 ID
     * @param visitorId 访客 ID
     */
    @Override
    @Transactional
    public void likeArticle(Long articleId, Long visitorId) {
        String userSetKey = RedisConstant.ARTICLE_LIKE_USER_SET + articleId;

        //1.使用 Redis Set 实现幂等性，已点赞则直接返回
        Long added = redisTemplate.opsForSet().add(userSetKey, visitorId);
        if (added == null || added == 0) {
            return;
        }

        //2.Redis Hash 存储点赞数增量
        redisTemplate.opsForHash().increment(RedisConstant.ARTICLE_LIKE_COUNT, articleId, 1);

        //3.保存点赞记录到 MySQL
        saveLikeRecord(articleId, visitorId);
    }

    /**
     * 取消点赞
     *
     * @param articleId 文章 ID
     * @param visitorId 访客 ID
     */
    @Override
    @Transactional
    public void unlikeArticle(Long articleId, Long visitorId) {
        String userSetKey = RedisConstant.ARTICLE_LIKE_USER_SET + articleId;

        //1.从 Redis Set 移除用户，未点赞则直接返回
        Long removed = redisTemplate.opsForSet().remove(userSetKey, visitorId);
        if (removed == null || removed == 0) {
            return;
        }

        //2.Redis Hash 点赞数减 1
        redisTemplate.opsForHash().increment(RedisConstant.ARTICLE_LIKE_COUNT, articleId, -1);

        //3.删除 MySQL 中的点赞记录
        deleteLikeRecord(articleId, visitorId);
    }

    /**
     * 检查是否已点赞（优先从 Redis 读取）
     *
     * @param articleId 文章 ID
     * @param visitorId 访客 ID
     * @return 是否已点赞
     */
    @Override
    public boolean hasLiked(Long articleId, Long visitorId) {
        String userSetKey = RedisConstant.ARTICLE_LIKE_USER_SET + articleId;

        //1.优先从 Redis Set 查询
        Boolean isMember = redisTemplate.opsForSet().isMember(userSetKey, visitorId);
        if (Boolean.TRUE.equals(isMember)) {
            return true;
        }

        //2.Redis 未命中，查询 MySQL
        LambdaQueryWrapper<ArticleLikes> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleLikes::getArticleId, articleId)
                .eq(ArticleLikes::getVisitorId, visitorId);
        boolean exists = this.count(wrapper) > 0;

        //3.回写 Redis 缓存
        if (exists) {
            redisTemplate.opsForSet().add(userSetKey, visitorId);
        }

        return exists;
    }

    /**
     * 获取文章点赞数（Redis 增量 + MySQL 基础值）
     *
     * @param articleId 文章 ID
     * @param mysqlLikeCount MySQL 中存储的点赞数
     * @return 总点赞数
     */
    @Override
    public long getLikeCount(Long articleId, long mysqlLikeCount) {
        //1.从 Redis Hash 获取增量
        Object increment = redisTemplate.opsForHash().get(RedisConstant.ARTICLE_LIKE_COUNT, articleId);
        if (increment == null) {
            return mysqlLikeCount;
        }

        //2.计算总点赞数，确保非负
        long redisIncrement = ((Number) increment).longValue();
        return Math.max(0, mysqlLikeCount + redisIncrement);
    }

    /**
     * 保存点赞记录到 MySQL
     *
     * @param articleId 文章 ID
     * @param visitorId 访客 ID
     */
    private void saveLikeRecord(Long articleId, Long visitorId) {
        ArticleLikes articleLikes = ArticleLikes.builder()
                .articleId(articleId)
                .visitorId(visitorId)
                .likeTime(LocalDateTime.now())
                .build();
        this.save(articleLikes);
    }

    /**
     * 删除点赞记录
     *
     * @param articleId 文章 ID
     * @param visitorId 访客 ID
     */
    private void deleteLikeRecord(Long articleId, Long visitorId) {
        LambdaQueryWrapper<ArticleLikes> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleLikes::getArticleId, articleId)
                .eq(ArticleLikes::getVisitorId, visitorId);
        this.remove(wrapper);
    }
}
