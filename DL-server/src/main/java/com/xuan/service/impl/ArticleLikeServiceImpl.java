package com.xuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.entity.ArticleLikes;
import com.xuan.entity.Articles;
import com.xuan.mapper.ArticleLikeMapper;
import com.xuan.mapper.ArticleMapper;
import com.xuan.service.IArticleLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final ArticleMapper articleMapper;

    /**
     * 点赞文章
     *
     * @param articleId 文章 ID
     * @param visitorId 访客 ID
     */
    @Override
    @Transactional
    public void likeArticle(Long articleId, Long visitorId) {
        LambdaQueryWrapper<ArticleLikes> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleLikes::getArticleId, articleId)
                .eq(ArticleLikes::getVisitorId, visitorId);

        if (this.count(wrapper) > 0) {
            return;
        }

        ArticleLikes articleLikes = ArticleLikes.builder()
                .articleId(articleId)
                .visitorId(visitorId)
                .likeTime(LocalDateTime.now())
                .build();
        this.save(articleLikes);

        LambdaUpdateWrapper<Articles> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Articles::getId, articleId)
                .setSql("like_count = like_count + 1");
        articleMapper.update(null, updateWrapper);
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
        LambdaQueryWrapper<ArticleLikes> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleLikes::getArticleId, articleId)
                .eq(ArticleLikes::getVisitorId, visitorId);

        if (this.count(wrapper) == 0) {
            return;
        }

        this.remove(wrapper);

        LambdaUpdateWrapper<Articles> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Articles::getId, articleId)
                .setSql("like_count = like_count - 1");
        articleMapper.update(null, updateWrapper);
    }

    /**
     * 检查是否已点赞
     *
     * @param articleId 文章 ID
     * @param visitorId 访客 ID
     * @return 是否已点赞
     */
    @Override
    public boolean hasLiked(Long articleId, Long visitorId) {
        LambdaQueryWrapper<ArticleLikes> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleLikes::getArticleId, articleId)
                .eq(ArticleLikes::getVisitorId, visitorId);
        return count(wrapper) > 0;
    }
}
