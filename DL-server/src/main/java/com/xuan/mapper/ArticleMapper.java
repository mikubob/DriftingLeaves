package com.xuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xuan.entity.Articles;
import com.xuan.vo.ArticleArchiveItemVO;
import com.xuan.vo.ArticleVO;
import com.xuan.vo.BlogArticleDetailVO;
import com.xuan.vo.BlogArticleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Articles> {
    
    /**
     * 全文搜索文章（使用 MySQL FULLTEXT 索引）
     * @param page 分页对象
     * @param keyword 搜索关键词
     * @return 分页结果
     */
    IPage<ArticleVO> searchWithFullText(IPage<ArticleVO> page, @Param("keyword") String keyword);
    
    /**
     * 获取已发布文章分页列表
     * @param page 分页对象
     * @return 分页结果
     */
    IPage<BlogArticleVO> getPublishedPage(IPage<BlogArticleVO> page);
    
    /**
     * 根据 slug 获取文章详情
     * @param slug 文章 slug
     * @return 文章详情 VO
     */
    BlogArticleDetailVO getBySlug(@Param("slug") String slug);
    
    /**
     * 获取上一篇文章（按发布时间）
     * @param id 当前文章 ID
     * @return 上一篇文章 VO
     */
    BlogArticleVO getPrevArticle(@Param("id") Long id);
    
    /**
     * 获取下一篇文章（按发布时间）
     * @param id 当前文章 ID
     * @return 下一篇文章 VO
     */
    BlogArticleVO getNextArticle(@Param("id") Long id);
    
    /**
     * 获取相关文章推荐（同分类，排除当前文章）
     * @param articleId 当前文章 ID
     * @param categoryId 分类 ID
     * @return 相关文章列表
     */
    List<BlogArticleVO> getRelatedArticles(@Param("articleId") Long articleId, @Param("categoryId") Long categoryId);
    
    /**
     * 获取文章归档列表
     * @return 文章归档列表
     */
    List<ArticleArchiveItemVO> getArchiveList();
    
    /**
     * 博客端文章搜索（仅获取已发布文章）
     * @param keyword 搜索关键词
     * @return 分页结果
     */
    IPage<BlogArticleVO> searchPublished(IPage<BlogArticleVO> page, @Param("keyword") String keyword);
}
