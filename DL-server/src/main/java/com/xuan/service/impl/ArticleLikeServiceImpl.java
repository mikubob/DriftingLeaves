package com.xuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.constant.RedisConstant;
import com.xuan.context.VisitorContext;
import com.xuan.entity.ArticleLikes;
import com.xuan.entity.Articles;
import com.xuan.exception.ValidationException;
import com.xuan.mapper.ArticleLikeMapper;
import com.xuan.mapper.ArticleMapper;
import com.xuan.service.IArticleLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 文章点赞服务实现，原子维护当前访客的点赞成员及待同步计数。
 */
@Service
@RequiredArgsConstructor
public class ArticleLikeServiceImpl extends ServiceImpl<ArticleLikeMapper, ArticleLikes> implements IArticleLikeService {
    private final StringRedisTemplate stringRedisTemplate;
    private final ArticleMapper articles;
    private static final DefaultRedisScript<Long> CHANGE = new DefaultRedisScript<>("""
            local changed
            if ARGV[3] == '1' then
                changed = redis.call('ZADD',KEYS[1],'NX',ARGV[4],ARGV[1])
            else
                changed = redis.call('ZREM',KEYS[1],ARGV[1])
            end
            if changed == 1 then redis.call('HINCRBY',KEYS[2],ARGV[2],ARGV[3]) end
            return changed
            """, Long.class);

    @Override
    @Transactional
    public void likeArticle(Long articleId, Long ignoredVisitorId) { change(articleId, 1); }

    @Override
    @Transactional
    public void unlikeArticle(Long articleId, Long ignoredVisitorId) { change(articleId, -1); }

    /**
     * 锁定已发布文章，并原子修改当前访客的点赞成员和增量。
     * @param articleId 文章 ID
     * @param delta 点赞为 1，取消为 -1
     * @throws ValidationException 文章不存在或未发布
     */
    private void change(Long articleId, int delta) {
        Long visitorId = VisitorContext.requireId();
        Articles article = articles.lockById(articleId);
        if (article == null || !Integer.valueOf(1).equals(article.getIsPublished())) throw new ValidationException("文章不存在或未发布");
        stringRedisTemplate.execute(CHANGE, List.of(RedisConstant.ARTICLE_LIKE_USER_SET + articleId, RedisConstant.ARTICLE_LIKE_COUNT),
                visitorId.toString(), articleId.toString(), String.valueOf(delta), String.valueOf(System.currentTimeMillis()));
    }

    @Override
    public boolean hasLiked(Long articleId, Long ignoredVisitorId) {
        Long visitorId = VisitorContext.requireId();
        return stringRedisTemplate.opsForZSet().score(RedisConstant.ARTICLE_LIKE_USER_SET + articleId, visitorId.toString()) != null;
    }
}
