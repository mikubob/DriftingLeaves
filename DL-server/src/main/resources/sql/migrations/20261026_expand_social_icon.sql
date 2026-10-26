-- 将社交图标长度扩展为与 skills.icon 相同的 255 个字符。
-- 已有数据库执行此脚本；新安装使用更新后的 DriftingLeaves.sql。
-- 请先选择博客数据库。扩展字段不会删除或截断原有图标，可重复执行。
ALTER TABLE social_media
    MODIFY COLUMN icon VARCHAR(255) NULL COMMENT '图标URL';
