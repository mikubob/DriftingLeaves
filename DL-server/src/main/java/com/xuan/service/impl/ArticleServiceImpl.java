package com.xuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.dto.ArticleDTO;
import com.xuan.dto.ArticlePageQueryDTO;
import com.xuan.entity.Articles;
import com.xuan.mapper.ArticleMapper;
import com.xuan.result.PageResult;
import com.xuan.service.IArticleService;
import com.xuan.vo.ArticleArchiveVO;
import com.xuan.vo.BlogArticleDetailVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

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
    private LambdaQueryWrapper<Articles> buildQueryWrapper(ArticlePageQueryDTO dto) {
        LambdaQueryWrapper<Articles> wrapper = new LambdaQueryWrapper<>();
        
        // 标题模糊搜索
        if (StringUtils.hasText(dto.getTitle())) {
            wrapper.like(Articles::getTitle, dto.getTitle());
        }
        
        // 分类 ID 精确匹配
        if (dto.getCategoryId() != null) {
            wrapper.eq(Articles::getCategoryId, dto.getCategoryId());
        }
        
        // 发布状态匹配
        if (dto.getIsPublished() != null) {
            wrapper.eq(Articles::getIsPublished, dto.getIsPublished());
        }
        
        // 默认按创建时间降序
        wrapper.orderByDesc(Articles::getCreateTime);
        
        return wrapper;
    }

    // ===== 其他方法待实现 =====
    
    @Override
    public void createArticle(ArticleDTO articleDTO) {
        // TODO: 实现创建文章逻辑
    }

    @Override
    public Articles getById(Long id) {
        return super.getById(id);
    }

    @Override
    public void updateArticle(ArticleDTO articleDTO) {
        // TODO: 实现更新文章逻辑
    }

    @Override
    public void batchDelete(List<Long> ids) {
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
        LambdaQueryWrapper<Articles> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.like(Articles::getTitle, keyword).or().like(Articles::getContentMarkdown, keyword));
        wrapper.orderByDesc(Articles::getCreateTime);
        
        IPage<Articles> resultPage = this.page(mpPage, wrapper);
        return PageResult.fromIPage(resultPage);
    }

    @Override
    public PageResult<Articles> getPublishedPage(int page, int pageSize) {
        Page<Articles> mpPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Articles> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Articles::getIsPublished, 1);
        wrapper.orderByDesc(Articles::getIsTop, Articles::getCreateTime);
        
        IPage<Articles> resultPage = this.page(mpPage, wrapper);
        return PageResult.fromIPage(resultPage);
    }

    @Override
    public BlogArticleDetailVO getBySlug(String slug) {
        LambdaQueryWrapper<Articles> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Articles::getSlug, slug);
        Articles articles = this.getOne(wrapper);
        
        if (articles == null) {
            throw new RuntimeException("文章不存在");
        }
        
        // TODO: 转换为 BlogArticleDetailVO
        BlogArticleDetailVO vo = new BlogArticleDetailVO();
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
        LambdaQueryWrapper<Articles> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Articles::getCategoryId, categoryId);
        wrapper.eq(Articles::getIsPublished, 1);
        wrapper.orderByDesc(Articles::getIsTop, Articles::getCreateTime);
        
        IPage<Articles> resultPage = this.page(mpPage, wrapper);
        return PageResult.fromIPage(resultPage);
    }

    @Override
    public List<ArticleArchiveVO> getArchive() {
        // TODO: 实现文章归档逻辑
        return Collections.emptyList();
    }

    @Override
    public PageResult<Articles> searchPublished(String keyword, int page, int pageSize) {
        Page<Articles> mpPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Articles> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Articles::getIsPublished, 1);
        wrapper.and(w -> w.like(Articles::getTitle, keyword).or().like(Articles::getContentMarkdown, keyword));
        wrapper.orderByDesc(Articles::getIsTop, Articles::getCreateTime);
        
        IPage<Articles> resultPage = this.page(mpPage, wrapper);
        return PageResult.fromIPage(resultPage);
    }

    @Override
    public PageResult<Articles> getPublishedByTagId(Long tagId, int page, int pageSize) {
        // TODO: 需要根据标签 ID 查询文章（涉及关联表）
        return PageResult.empty();
    }
}
