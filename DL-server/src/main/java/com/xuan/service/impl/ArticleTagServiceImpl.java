package com.xuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.dto.ArticleTagDTO;
import com.xuan.entity.ArticleTags;
import com.xuan.mapper.ArticleTagMapper;
import com.xuan.service.IArticleTagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文章标签服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTags> implements IArticleTagService {

    @Override
    public List<ArticleTags> listAll() {
        //TODO 实现获取所有标签逻辑
        return null;
    }

    @Override
    public void addTag(ArticleTagDTO articleTagDTO) {
        //TODO 实现添加标签逻辑
    }

    @Override
    public void updateTag(ArticleTagDTO articleTagDTO) {
        //TODO 实现修改标签逻辑
    }

    @Override
    public void batchDelete(List<Long> ids) {
        //TODO 实现批量删除标签逻辑
    }

    @Override
    public List<ArticleTags> getVisibleTags() {
        //TODO 实现获取有已发布文章的标签列表逻辑
        return null;
    }

    @Override
    public List<Long> getTagIdsByArticleId(Long id) {
        //TODO 实现根据文章 id 获取标签 id 逻辑
        return null;
    }

    @Override
    public void deleteRelationsByArticleId(Long id) {
        //TODO 实现根据文章 id 删除标签关联逻辑
    }

    @Override
    public void batchInsertRelations(Long id, List<Long> tagIds) {
        //TODO 实现批量插入标签关联逻辑
    }
}
