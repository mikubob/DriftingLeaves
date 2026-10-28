package com.xuan.annotation;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import java.lang.annotation.*;

/**
 * 文章及分类、标签变更后的关联静态缓存失效注解，事务提交后执行驱逐。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Caching(evict = {
        @CacheEvict(value = "articleCategories", allEntries = true),
        @CacheEvict(value = "articleTags", allEntries = true),
        @CacheEvict(value = "articleArchive", allEntries = true),
        @CacheEvict(value = "blogReport", allEntries = true),
        @CacheEvict(value = "rssFeed", allEntries = true),
        @CacheEvict(value = "sitemap", allEntries = true)
})
public @interface EvictArticleCaches {}
