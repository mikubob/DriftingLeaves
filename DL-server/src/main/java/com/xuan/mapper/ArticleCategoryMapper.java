package com.xuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuan.entity.ArticleCategories;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 文章分类持久层，提供关联统计及事务行锁查询。
 */
@Mapper
public interface ArticleCategoryMapper extends BaseMapper<ArticleCategories> {
    /**
     * 以行锁读取分类，调用方须处于数据库事务内。
     * @param id 分类 ID
     * @return 锁定的记录，不存在时为 null
     */
    @org.apache.ibatis.annotations.Select("SELECT * FROM article_categories WHERE id = #{id} FOR UPDATE")
    ArticleCategories lockById(Long id);


    /**
     * 获取所有分类及其已发布文章数（包括没有文章的分类）
     */
    List<ArticleCategories> getVisibleCategories();
}
