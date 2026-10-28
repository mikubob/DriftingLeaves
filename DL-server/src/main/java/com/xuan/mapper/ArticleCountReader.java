package com.xuan.mapper;

import com.xuan.constant.RedisConstant;
import com.xuan.vo.BlogArticleVO;
import com.xuan.vo.BlogArticleDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 文章展示计数读取组件，批量读取 Redis 待同步增量并合并到数据库查询结果。
 */
@Component
@RequiredArgsConstructor
public class ArticleCountReader {
    private final StringRedisTemplate redis;

    /**
     * 将待同步的浏览和点赞增量批量合入文章列表；每次只合并新查询的数据库结果。
     * @param articles 文章列表，可为空；点赞展示值最低为零
     */
    public void merge(List<? extends BlogArticleVO> articles) {
        if (articles == null || articles.isEmpty()) return;
        List<Long> ids = articles.stream().map(BlogArticleVO::getId).toList();
        Map<Long, long[]> pending = pending(ids);
        for (BlogArticleVO article : articles) {
            long[] delta = pending.get(article.getId());
            article.setViewCount(value(article.getViewCount()) + delta[0]);
            article.setLikeCount(Math.max(0, value(article.getLikeCount()) + delta[1]));
        }
    }

    /**
     * 将待同步增量合入文章详情，调用前必须已从数据库读取基数。
     * @param article 非空文章详情
     */
    public void merge(BlogArticleDetailVO article) {
        long[] delta = pending(List.of(article.getId())).get(article.getId());
        article.setViewCount(value(article.getViewCount()) + delta[0]);
        article.setLikeCount(Math.max(0, value(article.getLikeCount()) + delta[1]));
    }

    /**
     * 批量读取两类 Hash 增量，缺失字段按零处理。
     * @param ids 文章 ID 列表
     * @return 以文章 ID 索引的浏览、点赞增量数组
     */
    private Map<Long, long[]> pending(List<Long> ids) {
        List<Object> fields = new ArrayList<>(ids.stream().map(Object::toString).toList());
        List<Object> views = redis.opsForHash().multiGet(RedisConstant.ARTICLE_VIEW_COUNT, fields);
        List<Object> likes = redis.opsForHash().multiGet(RedisConstant.ARTICLE_LIKE_COUNT, fields);
        Map<Long, long[]> result = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) result.put(ids.get(i), new long[]{number(views.get(i)), number(likes.get(i))});
        return result;
    }

    private long number(Object value) { return value == null ? 0 : Long.parseLong(value.toString()); }
    private long value(Long value) { return value == null ? 0 : value; }
}
