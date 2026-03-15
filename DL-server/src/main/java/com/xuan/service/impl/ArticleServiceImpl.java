package com.xuan.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.dto.ArticlePageQueryDTO;
import com.xuan.entity.Articles;
import com.xuan.mapper.ArticleMapper;
import com.xuan.result.PageResult;
import com.xuan.service.IArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 文章服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Articles> implements IArticleService {

    /**
     * 分页条件查询文章列表（含草稿）
     * 使用 MyBatis-Plus 分页插件
     */
    @Override
    public PageResult<Articles> pageQuery(ArticlePageQueryDTO articlePageQueryDTO) {
        // 构建 MP 分页对象
        Page<Articles> page = new Page<>(articlePageQueryDTO.getPage(), articlePageQueryDTO.getPageSize());
        
        // 构建查询条件
        IPage<Articles> articlePage = this.page(page, buildQueryWrapper(articlePageQueryDTO));
        
        // 转换为自定义的 PageResult
        return PageResult.fromIPage(articlePage);
    }

    /**
     * 构建查询条件
     */
    private com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Articles> buildQueryWrapper(ArticlePageQueryDTO dto) {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Articles> wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        
        // 标题模糊搜索
        if (StringUtils.hasText(dto.getTitle())) {
            wrapper.like("title", dto.getTitle());
        }
        
        // 分类 ID 精确匹配
        if (dto.getCategoryId() != null) {
            wrapper.eq("category_id", dto.getCategoryId());
        }
        
        // 发布状态匹配
        if (dto.getIsPublished() != null) {
            wrapper.eq("is_published", dto.getIsPublished());
        }
        
        // 默认按创建时间降序
        wrapper.orderByDesc("create_time");
        
        return wrapper;
    }

    // ===== 其他方法待实现 =====
    
    @Override
    public void createArticle(com.xuan.dto.ArticleDTO articleDTO) {
        // TODO: 实现创建文章逻辑
    }

    @Override
    public Articles getById(Long id) {
        return super.getById(id);
    }

    @Override
    public void updateArticle(com.xuan.dto.ArticleDTO articleDTO) {
        // TODO: 实现更新文章逻辑
    }

    @Override
    public void batchDelete(java.util.List<Long> ids) {
        this.removeByIds(ids);
    }

    @Override
    public void publishOrCancel(Long id, Integer isPublished) {
        Articles articles = this.getById(id);
        if (articles != null) {
            articles.setIsPublished(isPublished);
            this.updateById(articles);
        }
    }

    @Override
    public void toggleTop(Long id, Integer isTop) {
        Articles articles = this.getById(id);
        if (articles != null) {
            articles.setIsTop(isTop);
            this.updateById(articles);
        }
    }

    @Override
    public PageResult<Articles> search(String keyword, int page, int pageSize) {
        Page<Articles> mpPage = new Page<>(page, pageSize);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Articles> wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.and(w -> w.like("title", keyword).or().like("content_markdown", keyword));
        wrapper.orderByDesc("create_time");
        
        IPage<Articles> resultPage = this.page(mpPage, wrapper);
        return PageResult.fromIPage(resultPage);
    }

    @Override
    public PageResult<Articles> getPublishedPage(int page, int pageSize) {
        Page<Articles> mpPage = new Page<>(page, pageSize);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Articles> wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("is_published", 1);
        wrapper.orderByDesc("is_top", "create_time");
        
        IPage<Articles> resultPage = this.page(mpPage, wrapper);
        return PageResult.fromIPage(resultPage);
    }

    @Override
    public com.xuan.vo.BlogArticleDetailVO getBySlug(String slug) {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Articles> wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("slug", slug);
        Articles articles = this.getOne(wrapper);
        
        if (articles == null) {
            throw new RuntimeException("文章不存在");
        }
        
        // TODO: 转换为 BlogArticleDetailVO
        com.xuan.vo.BlogArticleDetailVO vo = new com.xuan.vo.BlogArticleDetailVO();
        // 设置属性...
        return vo;
    }

    @Override
    public void incrementViewCount(Long articleId) {
        // TODO: 实现浏览量增加逻辑（Redis）
    }

    @Override
    public PageResult<Articles> getPublishedByCategoryId(Long categoryId, int page, int pageSize) {
        Page<Articles> mpPage = new Page<>(page, pageSize);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Articles> wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("category_id", categoryId);
        wrapper.eq("is_published", 1);
        wrapper.orderByDesc("is_top", "create_time");
        
        IPage<Articles> resultPage = this.page(mpPage, wrapper);
        return PageResult.fromIPage(resultPage);
    }

    @Override
    public java.util.List<com.xuan.vo.ArticleArchiveVO> getArchive() {
        // TODO: 实现文章归档逻辑
        return java.util.Collections.emptyList();
    }

    @Override
    public PageResult<Articles> searchPublished(String keyword, int page, int pageSize) {
        Page<Articles> mpPage = new Page<>(page, pageSize);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Articles> wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("is_published", 1);
        wrapper.and(w -> w.like("title", keyword).or().like("content_markdown", keyword));
        wrapper.orderByDesc("is_top", "create_time");
        
        IPage<Articles> resultPage = this.page(mpPage, wrapper);
        return PageResult.fromIPage(resultPage);
    }

    @Override
    public PageResult<Articles> getPublishedByTagId(Long tagId, int page, int pageSize) {
        // TODO: 需要根据标签 ID 查询文章（涉及关联表）
        return PageResult.empty();
    }
}
