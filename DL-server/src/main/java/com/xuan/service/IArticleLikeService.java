package com.xuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuan.entity.ArticleLikes;

/**
 * 文章点赞服务，使用当前请求的已验证访客身份维护点赞状态。
 */
public interface IArticleLikeService extends IService<ArticleLikes> {

    /**
     * 为已发布文章点赞，重复点赞不增加计数。
     * @param articleId 文章 ID
     * @param visitorId 仅兼容旧参数，实际身份取自 VisitorContext
     * @throws com.xuan.exception.TokenException 缺少访客身份
     */
    void likeArticle(Long articleId, Long visitorId);

    /**
     * 取消当前访客的点赞，重复取消不重复扣减。
     * @param articleId 文章 ID
     * @param visitorId 仅兼容旧参数，实际身份取自 VisitorContext
     * @throws com.xuan.exception.TokenException 缺少访客身份
     */
    void unlikeArticle(Long articleId, Long visitorId);

    /**
     * 读取当前访客是否已点赞。
     * @param articleId 文章 ID
     * @param visitorId 仅兼容旧参数，实际身份取自 VisitorContext
     * @return 当前访客是否在点赞集合中
     * @throws com.xuan.exception.TokenException 缺少访客身份
     */
    boolean hasLiked(Long articleId, Long visitorId);
}
