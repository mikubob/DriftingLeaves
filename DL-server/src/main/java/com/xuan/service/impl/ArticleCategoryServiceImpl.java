package com.xuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.dto.ArticleCategoryDTO;
import com.xuan.entity.ArticleCategories;
import com.xuan.mapper.ArticleCategoryMapper;
import com.xuan.service.IArticleCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文章分类服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleCategoryServiceImpl extends ServiceImpl<ArticleCategoryMapper, ArticleCategories> implements IArticleCategoryService {

    @Override
    public List<ArticleCategories> listAll() {
        // TODO: 实现获取所有文章分类逻辑
        return null;
    }

    @Override
    public void addCategory(ArticleCategoryDTO articleCategoryDTO) {
        // TODO: 实现添加文章分类逻辑
    }

    @Override
    public void updateCategory(ArticleCategoryDTO articleCategoryDTO) {
        // TODO: 实现更新文章分类逻辑
    }

    @Override
    public void batchDelete(List<Long> ids) {
        // TODO: 实现批量删除文章分类逻辑
    }

    @Override
    public List<ArticleCategories> getVisibleCategories() {
        // TODO: 实现获取所有有已发布文章的可见分类逻辑
        return null;
    }
}
