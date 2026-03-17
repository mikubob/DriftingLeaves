package com.xuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xuan.entity.Articles;
import com.xuan.vo.ArticleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ArticleMapper extends BaseMapper<Articles> {
    
    /**
     * 全文搜索文章（使用 MySQL FULLTEXT 索引）
     * @param page 分页对象
     * @param keyword 搜索关键词
     * @return 分页结果
     */
    IPage<ArticleVO> searchWithFullText(IPage<ArticleVO> page, @Param("keyword") String keyword);
}
