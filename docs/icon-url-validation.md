# 图标链接校验

社交媒体和技能图标统一使用数据库字段长度 255：

| 场景 | 数据库字段 | 最大字符数 |
| --- | --- | --- |
| 社交媒体 | `social_media.icon VARCHAR(255)` | 255 |
| 技能 | `skills.icon VARCHAR(255)` | 255 |

社交图标原字段为 `VARCHAR(50)`，需要升级已有数据库。新安装直接使用更新后的建表脚本。

管理端保存前分别检查上述长度，超限时显示具体提示并保留原输入。后端新增、修改接口使用相同限制，防止绕过前端后发生数据库写入错误；空图标仍可保存。上传返回的图标链接也经过同一校验。

HTTP 400 等响应优先显示后端提供的错误原因；无 HTTP 响应时才显示网络错误。

已有数据库部署前，在后端项目根目录通过 MySQL 客户端选择博客数据库并执行：

```sql
-- 先确认当前连接选择的是需要升级的博客数据库。
SELECT DATABASE();
SOURCE DL-server/src/main/resources/sql/migrations/20261026_expand_social_icon.sql;

-- 社交图标和技能图标都应显示 varchar(255)。
SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME IN ('social_media', 'skills')
  AND COLUMN_NAME = 'icon';
```

也可在数据库管理工具中执行该文件。脚本只扩展 `social_media.icon`，保留已有记录，可以重复执行。不要重新创建已有业务表。

上线顺序：保留数据库备份，在低峰期执行升级脚本并确认字段长度，再发布后端和管理端。MySQL 可能重建该表，执行时会获取表的元数据锁。

发布后分别新增和编辑社交媒体、技能，使用 51 至 255 字符的图标链接确认可以保存并原样读回。256 字符时应提示超限，原输入保持不变。

后端回归测试：

```powershell
mvn -pl DL-server -am test '-Dtest=ProfileIconValidationTest' '-Dsurefire.failIfNoSpecifiedTests=false'
```
