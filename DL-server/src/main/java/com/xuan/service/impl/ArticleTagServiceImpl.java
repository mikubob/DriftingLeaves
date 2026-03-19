package com.xuan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.dto.ArticleTagDTO;
import com.xuan.entity.ArticleTags;
import com.xuan.mapper.ArticleTagMapper;
import com.xuan.service.IArticleTagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 文章标签服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTags> implements IArticleTagService {

    /**
     * 获取所有标签
     * @return 标签列表
     */
    @Override
    @Cacheable(value = "articleTags", key = "'all'")
    public List<ArticleTags> listAll() {
        //1.构建查询条件
        LambdaQueryWrapper<ArticleTags> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(ArticleTags::getId);

        //2.执行查询获取所有标签
        List<ArticleTags> list = list(queryWrapper);
        return list != null ? list : Collections.emptyList();
    }

    /**
     * 添加标签
     * @param articleTagDTO 新增标签数据
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "articleTags", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    public void addTag(ArticleTagDTO articleTagDTO) {
        save(BeanUtil.copyProperties(articleTagDTO, ArticleTags.class));
    }

    /**
     * 修改标签
     * @param articleTagDTO 修改的标签数据
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "articleTags", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    public void updateTag(ArticleTagDTO articleTagDTO) {
        //1.拷贝更新的数据
        ArticleTags articleTags = BeanUtil.copyProperties(articleTagDTO, ArticleTags.class);

        //2.更新标签
        updateById(articleTags);
    }

    /**
     * 批量删除标签
     * @param ids 标签 id 列表
     */
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "articleTags", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    public void batchDeleteTags(List<Long> ids) {
        removeBatchByIds(ids);
    }

    /**
     * 获取可见的标签
     * @return 标签列表
     */
    @Override
    @Cacheable(value = "articleTags", key = "'visible'")
    public List<ArticleTags> getVisibleTags() {
        List<ArticleTags> list = baseMapper.getVisibleTags();
        return list != null ? list : Collections.emptyList();
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

    @Override
    public void batchDeleteRelationsByArticleIds(List<Long> ids) {
        //TODO 实现批量删除标签关联逻辑
    }

    @Override
    public List<ArticleTags> getTagByArticleId(Long id) {
        //TODO 实现根据文章 id 获取标签逻辑
        return null;
    }
}
