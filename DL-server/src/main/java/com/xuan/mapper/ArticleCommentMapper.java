package com.xuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuan.dto.ArticleCommentPageQueryDTO;
import com.xuan.entity.ArticleComments;
import com.xuan.vo.ArticleCommentQueryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 文章评论持久层，提供管理端分页及评论行锁查询。
 */
@Mapper
public interface ArticleCommentMapper extends BaseMapper<ArticleComments> {
    /**
     * 以行锁读取评论，调用方须处于数据库事务内。
     * @param id 评论 ID
     * @return 锁定的记录，不存在时为 null
     */
    @org.apache.ibatis.annotations.Select("SELECT * FROM article_comments WHERE id = #{id} FOR UPDATE")
    ArticleComments lockById(Long id);


    /**
     * 关联文章标题查询管理端评论分页。
     * @param page 分页信息
     * @param dto 筛选条件
     * @return 含联系方式及文章标题的管理端分页
     */
    IPage<ArticleCommentQueryVO> pageQueryWithArticleTitle(Page<ArticleComments> page, @Param("dto") ArticleCommentPageQueryDTO dto);
}
