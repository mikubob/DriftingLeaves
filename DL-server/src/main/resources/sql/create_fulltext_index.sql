-- ====================================
-- 文章表全文索引创建脚本
-- ====================================
-- 说明：为 articles 表添加全文索引以支持高效的全文搜索
-- 要求：MySQL 5.6+ (InnoDB 引擎支持 FULLTEXT 索引)
-- ====================================

-- 1. 为 title, summary, content_markdown 字段创建联合全文索引
-- 用于支持文章搜索功能（MATCH ... AGAINST 查询）
ALTER TABLE articles 
ADD FULLTEXT INDEX ft_article_search (title, summary, content_markdown)
WITH PARSER ngram;

-- ====================================
-- 可选：单独为各个字段创建全文索引
-- ====================================
-- 如果只需要搜索标题，可以创建单独的索引
-- ALTER TABLE articles ADD FULLTEXT INDEX ft_title (title) WITH PARSER ngram;

-- 如果只需要搜索摘要，可以创建单独的索引
-- ALTER TABLE articles ADD FULLTEXT INDEX ft_summary (summary) WITH PARSER ngram;

-- 如果只需要搜索内容，可以创建单独的索引
-- ALTER TABLE articles ADD FULLTEXT INDEX ft_content (content_markdown) WITH PARSER ngram;

-- ====================================
-- 全文索引使用说明
-- ====================================
-- 1. 基本搜索（布尔模式）：
--    SELECT * FROM articles 
--    WHERE MATCH(title, summary, content_markdown) AGAINST('关键词' IN BOOLEAN MODE);
--
-- 2. 自然语言模式（默认，按相关性排序）：
--    SELECT * FROM articles 
--    WHERE MATCH(title, summary, content_markdown) AGAINST('关键词');
--
-- 3. 布尔模式支持的操作符：
--    + 必须包含：'+数据库 +优化'
--    - 必须不包含：'+数据库 -MySQL'
--    * 通配符：'数据*'
--    "" 精确匹配：'"数据库优化"'
--
-- ====================================
-- 注意事项
-- ====================================
-- 1. 全文索引会占用额外的磁盘空间
-- 2. 插入、更新、删除操作会变慢（需要维护索引）
-- 3. 对于短文本（< 3 个字符），ngram 解析器可能无法有效索引
-- 4. 如果表数据量很大，创建索引可能需要较长时间
-- ====================================

-- ====================================
-- 删除全文索引（如需要）
-- ====================================
-- ALTER TABLE articles DROP INDEX ft_article_search;
