package com.xuan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.constant.MessageConstant;
import com.xuan.dto.ArticleCategoryDTO;
import com.xuan.entity.ArticleCategories;
import com.xuan.entity.Articles;
import com.xuan.exception.ArticleCategoryException;
import com.xuan.mapper.ArticleCategoryMapper;
import com.xuan.service.IArticleCategoryService;
import com.xuan.service.IArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 文章分类服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleCategoryServiceImpl extends ServiceImpl<ArticleCategoryMapper, ArticleCategories> implements IArticleCategoryService {

    private final IArticleService articleService;

    /**
     * 获取所有文章分类
     *
     * @return 文章分类列表
     */
    @Override
    public List<ArticleCategories> listAll() {
        LambdaQueryWrapper<ArticleCategories> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(ArticleCategories::getSort)
                .orderByDesc(ArticleCategories::getId);
        return list(wrapper);
    }

    /**
     * 添加文章分类
     *
     * @param articleCategoryDTO 文章分类数据
     */
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "articleCategories", allEntries = true),
            @CacheEvict(value = "articleList", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    public void addCategory(ArticleCategoryDTO articleCategoryDTO) {
        ArticleCategories articleCategories = BeanUtil.copyProperties(articleCategoryDTO, ArticleCategories.class);
        save(articleCategories);
    }

    /**
     * 更新文章分类
     *
     * @param articleCategoryDTO 文章分类数据
     */
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "articleCategories", allEntries = true),
            @CacheEvict(value = "articleList", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    public void updateCategory(ArticleCategoryDTO articleCategoryDTO) {
        ArticleCategories articleCategories = getById(articleCategoryDTO.getId());
        if (articleCategories == null) {
            throw new ArticleCategoryException(MessageConstant.CATEGORY_NOT_FOUND);
        }
        
        BeanUtil.copyProperties(articleCategoryDTO, articleCategories);
        updateById(articleCategories);
    }

    /**
     * 批量删除文章分类
     *
     * @param ids 文章分类 id 列表
     */
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "articleCategories", allEntries = true),
            @CacheEvict(value = "articleList", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    public void batchDelete(List<Long> ids) {
        // 检查分类下是否有关联文章
        for (Long id : ids) {
            long count = articleService.count(new LambdaQueryWrapper<Articles>()
                    .eq(Articles::getCategoryId, id));
            if (count > 0) {
                throw new ArticleCategoryException(MessageConstant.CATEGORY_HAS_ARTICLES);
            }
        }
        removeByIds(ids);
    }

    /**
     * 获取所有有已发布文章的可见分类
     *
     * @return 可见分类列表
     */
    @Override
    @Cacheable(value = "articleCategories", key = "'visible'")
    public List<ArticleCategories> getVisibleCategories() {
        return baseMapper.getVisibleCategories();
    }
}
