package com.xuan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.constant.MessageConstant;
import com.xuan.constant.StatusConstant;
import com.xuan.dto.ArticleDTO;
import com.xuan.dto.ArticlePageQueryDTO;
import com.xuan.entity.Articles;
import com.xuan.exception.ArticleException;
import com.xuan.mapper.ArticleMapper;
import com.xuan.mapper.ArticleTagMapper;
import com.xuan.result.PageResult;
import com.xuan.service.IArticleService;
import com.xuan.service.IArticleTagService;
import com.xuan.utils.MarkdownUtil;
import com.xuan.vo.ArticleArchiveVO;
import com.xuan.vo.BlogArticleDetailVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 文章服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Articles> implements IArticleService {

    private final IArticleTagService articleTagService;

    //分钟阅读量：300字/分钟
    private final int VIEWS = 300;

    /**
     * 分页查询文章
     *
     * @param articlePageQueryDTO 文章分页查询参数
     * @return 分页结果
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

    // ===== 其他方法待实现 =====

    /**
     * 创建文章
     *
     * @param articleDTO 文章数据
     */
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "articleList", allEntries = true),
            @CacheEvict(value = "articleDetail", allEntries = true),
            @CacheEvict(value = "articleArchive", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    public void createArticle(ArticleDTO articleDTO) {
        Articles articles = BeanUtil.copyProperties(articleDTO, Articles.class);
        //1.判断前端是否进行了md->html的渲染，如果没有则后端进行转换
        if (StrUtil.isNotBlank(articles.getContentHtml())) {
            articles.setContentHtml(articleDTO.getContentHtml());
        } else {
            String rawContent = articleDTO.getContentMarkdown();
            String contentHtml = MarkdownUtil.isHtml(rawContent)
                    ? MarkdownUtil.sanitize(rawContent)
                    : MarkdownUtil.toHtml(rawContent);
            articles.setContentHtml(contentHtml);
        }

        //2.计算字数得阅读的时间
        String plainText = articleDTO.getContentMarkdown();
        long wordCount = countWords(plainText);
        long readingTime = Math.max(1, wordCount / VIEWS);//阅读时间,以每分钟阅读300字为例，最少为1分钟
        articles.setWordCount(wordCount);
        articles.setReadingTime(readingTime);

        //3.设置发布信息
        Integer isPublished = articleDTO.getIsPublished();
        if (isPublished != null && isPublished.equals(StatusConstant.ENABLE)) {
            articles.setPublishTime(LocalDateTime.now());
        }

        //4.初始化统计字段和默认状态
        articles.setViewCount(0L);
        articles.setLikeCount(0L);
        articles.setCommentCount(0L);
        if (articles.getIsTop() == null) {
            articles.setIsTop(StatusConstant.DISABLE);
        }

        //5.保存文章到数据库
        save(articles);

        //TODO 6.保存文章-标签关联
        if (articleDTO.getTagIds() != null && !articleDTO.getTagIds().isEmpty()) {
            articleTagService.deleteRelationsByArticleId(articleDTO.getId());
            if (!articleDTO.getTagIds().isEmpty()){
                articleTagService.batchInsertRelations(articleDTO.getId(),articleDTO.getTagIds());
            }
        }
    }


    /**
     * 根据 id 查询文章详情
     *
     * @param id 文章 id
     * @return 文章详情
     */
    @Override
    public Articles getArticleById(Long id) {
        Articles article = getById(id);
        if (article == null) {
            throw new ArticleException(MessageConstant.ARTICLE_NOT_FOUND);
        }
        //TODO 填充标签id列表，用于管理端编辑时回显
        List<Long> tagIds = articleTagService.getTagIdsByArticleId(id);
        article.setTagIds(tagIds);
        return article;
    }

    /**
     * 更新文章
     * @param articleDTO 文章数据
     */
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "articleList", allEntries = true),
            @CacheEvict(value = "articleDetail", allEntries = true),
            @CacheEvict(value = "articleArchive", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    public void updateArticle(ArticleDTO articleDTO) {
        // TODO: 实现更新文章逻辑
    }

    @Override
    public void batchDelete(List<Long> ids) {
        this.removeByIds(ids);
    }

    @Override
    public void publishOrCancel(Long id, Integer isPublished) {
        Articles articles = this.getArticleById(id);
        if (articles != null) {
            articles.setIsPublished(isPublished);
            this.updateById(articles);
        }
    }

    @Override
    public void toggleTop(Long id, Integer isTop) {
        Articles articles = this.getArticleById(id);
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


    //<==========私有辅助方法辅助==========>

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<Articles> buildQueryWrapper(ArticlePageQueryDTO dto) {
        LambdaQueryWrapper<Articles> wrapper = new LambdaQueryWrapper<>();

        // 标题模糊搜索
        if (StrUtil.isNotBlank(dto.getTitle())) {
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

        // 排序：先按置顶降序，再按创建时间降序
        wrapper.orderByDesc(Articles::getIsTop, Articles::getCreateTime);

        return wrapper;
    }

    /**
     * 统计字数（中文算1字，英文单词算1字）
     *
     * @param text 文本
     * @return 字数
     */
    private long countWords(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        // 去除Markdown语法符号
        String cleanText = text.replaceAll("[#*`>\\-\\[\\]()!|]", "");
        // 中文字符数
        long chineseCount = cleanText.chars()
                .filter(c -> Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN)
                .count();
        // 英文单词数
        String englishText = cleanText.replaceAll("[\\u4e00-\\u9fff]", " ");
        String[] words = englishText.trim().split("\\s+");
        long englishCount = 0;
        for (String word : words) {
            if (!word.isEmpty() && word.matches(".*[a-zA-Z0-9].*")) {
                englishCount++;
            }
        }
        return chineseCount + englishCount;
    }
}
