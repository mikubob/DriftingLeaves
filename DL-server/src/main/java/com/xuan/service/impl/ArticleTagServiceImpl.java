package com.xuan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.annotation.EvictArticleCaches;
import com.xuan.dto.ArticleTagDTO;
import com.xuan.entity.ArticleTagRelations;
import com.xuan.entity.ArticleTags;
import com.xuan.exception.ValidationException;
import com.xuan.mapper.ArticleTagMapper;
import com.xuan.service.IArticleTagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 文章标签服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTags> implements IArticleTagService {

    /**
     * 获取所有标签
     * 
     * @return 标签列表
     */
    @Override
    @Cacheable(value = "articleTags", key = "'all'")
    public List<ArticleTags> listAll() {
        // 1. 构建查询条件
        LambdaQueryWrapper<ArticleTags> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(ArticleTags::getId);

        // 2. 执行查询获取所有标签
        List<ArticleTags> list = list(queryWrapper);
        return list != null ? list : Collections.emptyList();
    }

    /**
     * 添加标签
     * 
     * @param articleTagDTO 新增标签数据
     */
    @Override
    @EvictArticleCaches
    public void addTag(ArticleTagDTO articleTagDTO) {
        articleTagDTO.setId(null);
        // 1. 转换数据并保存标签
        save(BeanUtil.copyProperties(articleTagDTO, ArticleTags.class));
    }

    /**
     * 修改标签
     * 
     * @param articleTagDTO 修改的标签数据
     */
    @Override
    @EvictArticleCaches
    public void updateTag(ArticleTagDTO articleTagDTO) {
        if (articleTagDTO.getId() == null || articleTagDTO.getId() <= 0) throw new ValidationException("更新ID不能为空且必须为正数");
        // 1. 拷贝更新的数据
        ArticleTags articleTags = BeanUtil.copyProperties(articleTagDTO, ArticleTags.class);

        // 2. 更新标签
        if (!updateById(articleTags)) throw new ValidationException("更新目标不存在");
    }

    /**
     * 批量删除标签
     * 
     * @param ids 标签 id 列表
     */
    @Override
    @Transactional
    @EvictArticleCaches
    public void batchDeleteTags(List<Long> ids) {
        list(new LambdaQueryWrapper<ArticleTags>().in(ArticleTags::getId, ids).orderByAsc(ArticleTags::getId).last("FOR UPDATE"));
        // 1. 先删除关联关系中涉及这些标签的记录
        baseMapper.deleteRelationsByTagIds(ids);

        // 2. 批量删除标签
        removeBatchByIds(ids);
    }

    /**
     * 获取可见的标签
     * 
     * @return 标签列表
     */
    @Override
    @Cacheable(value = "articleTags", key = "'visible'")
    public List<ArticleTags> getVisibleTags() {
        // 1. 查询可见的标签
        List<ArticleTags> list = baseMapper.getVisibleTags();
        return list != null ? list : Collections.emptyList();
    }

    /**
     * 根据文章 id 获取标签 id
     * 
     * @param id 文章 id
     * @return 标签 id 列表
     */
    @Override
    public List<Long> getTagIdsByArticleId(Long id) {
        // 1. 根据文章 id 查询标签 id 列表
        List<Long> tagIds = baseMapper.getTagIdsByArticleId(id);
        return tagIds != null ? tagIds : Collections.emptyList();
    }

    /**
     * 删除文章标签关系
     * 
     * @param id 文章 id
     */
    @Override
    public void deleteRelationsByArticleId(Long id) {
        // 1. 删除文章标签关系
        baseMapper.deleteRelationsByArticleId(id);
    }

    /**
     * 批量插入文章标签关系
     * 
     * @param articleId 文章 id
     * @param tagIds 标签 id 列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInsertRelations(Long articleId, List<Long> tagIds) {
        // 1. 检查标签 id 列表是否为空
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        
        if (tagIds.stream().anyMatch(Objects::isNull)
                || list(new LambdaQueryWrapper<ArticleTags>().in(ArticleTags::getId, tagIds).orderByAsc(ArticleTags::getId).last("FOR UPDATE")).size() != tagIds.stream().distinct().count()) {
            throw new ValidationException("文章标签不存在");
        }
        // 2. 构建文章标签关系列表
        List<ArticleTagRelations> relations = tagIds.stream().distinct()
                .map(tagId -> ArticleTagRelations.builder()
                        .articleId(articleId)
                        .tagId(tagId)
                        .build())
                .toList();
        
        // 3. 批量插入文章标签关系
        baseMapper.batchInsertRelations(relations);
    }

    /**
     * 批量删除文章标签关系
     * 
     * @param ids 文章 id 列表
     */
    @Override
    public void batchDeleteRelationsByArticleIds(List<Long> ids) {
        // 1. 检查文章 id 列表是否为空
        if (ids == null || ids.isEmpty()) {
            return;
        }
        
        // 2. 批量删除文章标签关系
        baseMapper.batchDeleteRelationsByArticleIds(ids);
    }

    /**
     * 根据文章 id 获取标签
     * 
     * @param id 文章 id
     * @return 标签列表
     */
    @Override
    public List<ArticleTags> getTagByArticleId(Long id) {
        // 1. 根据文章 id 查询标签列表
        List<ArticleTags> tags = baseMapper.getTagByArticleId(id);
        return tags != null ? tags : Collections.emptyList();
    }

    /**
     * 获取文章总数
     * @return 文章总数
     */
    @Override
    public Integer countTotal() {
        return Math.toIntExact(count());
    }
}
