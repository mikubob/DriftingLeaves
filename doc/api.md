# FeiTwnd Website API 接口文档

## 目录

- [概述](#概述)
- [通用说明](#通用说明)
- [限流说明](#限流说明)
- [响应格式](#响应格式)
- [管理端接口 (Admin)](#管理端接口-admin)
    - [管理员管理](#管理员管理)
    - [文章管理](#文章管理)
    - [文章分类管理](#文章分类管理)
    - [文章标签管理](#文章标签管理)
    - [文章评论管理](#文章评论管理)
    - [经历管理](#经历管理)
    - [技能管理](#技能管理)
    - [友情链接管理](#友情链接管理)
    - [社交媒体管理](#社交媒体管理)
    - [系统配置管理](#系统配置管理)
    - [个人信息管理](#个人信息管理)
    - [音乐管理](#音乐管理)
    - [留言管理](#留言管理)
    - [RSS 订阅管理](#rss-订阅管理)
    - [访客管理](#访客管理)
    - [浏览记录管理](#浏览记录管理)
    - [操作日志管理](#操作日志管理)
    - [统计数据](#统计数据)
    - [通用接口](#通用接口)
- [博客端接口 (Blog)](#博客端接口-blog)
    - [文章相关](#文章相关)
    - [文章分类](#文章分类-1)
    - [文章标签](#文章标签-1)
    - [文章评论](#文章评论-1)
    - [文章点赞](#文章点赞)
    - [留言](#留言-1)
    - [友情链接](#友情链接-1)
    - [音乐](#音乐-1)
    - [个人信息](#个人信息-1)
    - [系统配置](#系统配置-1)
    - [社交媒体](#社交媒体-1)
    - [RSS 订阅](#rss-订阅)
    - [RSS Feed](#rss-feed)
    - [站点地图](#站点地图)
    - [访客记录](#访客记录-1)
    - [统计数据](#统计数据-1)
    - [验证码](#验证码)
- [首页端接口 (Home)](#首页端接口-home)
    - [个人信息](#个人信息-2)
    - [社交媒体](#社交媒体-2)
    - [系统配置](#系统配置-2)
    - [访客记录](#访客记录-2)
- [简历端接口 (CV)](#简历端接口-cv)
    - [个人信息](#个人信息-3)
    - [经历](#经历-1)
    - [技能](#技能-1)
    - [访客记录](#访客记录-3)
- [公共接口](#公共接口)
    - [健康检查](#健康检查)

---

## 概述

本文档描述了 FeiTwnd Website 后端系统的所有 RESTful API 接口。系统采用前后端分离架构，提供博客、首页、简历等多个前端模块的接口支持。

**基础信息：**
- 基础路径：根据模块不同分为 `/admin`、`/blog`、`/home`、`/cv`、`/health`
- 数据格式：JSON
- 字符编码：UTF-8

---

## 通用说明

### 认证方式

管理端接口需要通过 JWT Token 进行认证，在请求头中携带：
```
Authorization: Bearer <your_jwt_token>
```

### 时间格式

- 请求和响应中的时间字段统一使用 ISO 8601 格式
- 日期格式：`yyyy-MM-dd`
- 日期时间格式：`yyyy-MM-dd HH:mm:ss`

### 分页参数

分页查询接口通常使用以下参数：
- `page`: 页码，从 1 开始
- `pageSize`: 每页显示数量

---

## 限流说明

系统对部分接口实施了限流保护，防止恶意请求：

| 接口路径 | 限流类型 | Tokens | 时间窗口 | 突发容量 |
|---------|---------|--------|---------|---------|
| POST /admin/admin/sendCode | IP | 5 | 60 秒 | 8 |
| POST /admin/admin/login | IP | 5 | 60 秒 | 8 |
| POST /blog/articleComment | IP | 5 | 60 秒 | 8 |
| PUT /blog/articleComment/edit | IP | 5 | 60 秒 | 8 |
| DELETE /blog/articleComment/{id} | IP | 5 | 60 秒 | 8 |
| GET /blog/article/search | IP | 10 | 60 秒 | 15 |
| POST /blog/articleLike/{articleId} | IP | 10 | 60 秒 | 15 |
| DELETE /blog/articleLike/{articleId} | IP | 10 | 60 秒 | 15 |
| POST /blog/message | IP | 5 | 60 秒 | 8 |
| PUT /blog/message/edit | IP | 5 | 60 秒 | 8 |
| DELETE /blog/message/{id} | IP | 5 | 60 秒 | 8 |
| POST /blog/rssSubscription | IP | 5 | 60 秒 | 8 |
| POST /blog/visitor/record | IP | 10 | 60 秒 | 15 |
| POST /cv/visitor/record | IP | 10 | 60 秒 | 15 |
| POST /home/visitor/record | IP | 10 | 60 秒 | 15 |

---

## 响应格式

所有接口统一使用以下响应格式：

### 成功响应

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 错误响应

```json
{
  "code": 400,
  "message": "错误信息",
  "data": null
}
```

### 分页响应

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 100,
    "page": 1,
    "pageSize": 10,
    "totalPages": 10
  }
}
```

---

## 管理端接口 (Admin)

### 管理员管理

**基础路径：** `/admin/admin`

#### 1. 发送验证码

- **接口路径：** `/admin/admin/sendCode`
- **请求方法：** POST
- **限流：** IP 限流，5 tokens/60 秒，突发容量 8
- **请求 Content-Type：** `application/json`

**请求参数：**

```json
{
  "username": "string"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 管理员用户名 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 2. 管理员登录

- **接口路径：** `/admin/admin/login`
- **请求方法：** POST
- **限流：** IP 限流，5 tokens/60 秒，突发容量 8

**请求参数：**

```json
{
  "username": "string",
  "password": "string",
  "code": "string"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |
| code | string | 是 | 验证码 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "string",
    "admin": {
      "id": 1,
      "username": "string",
      "nickname": "string",
      "email": "string"
    }
  }
}
```

---

#### 3. 获取管理员信息

- **接口路径：** `/admin/admin`
- **请求方法：** GET
- **认证：** 需要 JWT Token

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "string",
    "nickname": "string",
    "email": "string"
  }
}
```

---

#### 4. 管理员退出登录

- **接口路径：** `/admin/admin/logout`
- **请求方法：** POST

**请求参数：**

```json
{
  "token": "string"
}
```

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 5. 修改密码

- **接口路径：** `/admin/admin/changePassword`
- **请求方法：** PUT

**请求参数：**

```json
{
  "oldPassword": "string",
  "newPassword": "string"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| oldPassword | string | 是 | 旧密码 |
| newPassword | string | 是 | 新密码 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 6. 修改昵称

- **接口路径：** `/admin/admin/changeNickname`
- **请求方法：** PUT

**请求参数：**

```json
{
  "nickname": "string"
}
```

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 7. 换绑邮箱

- **接口路径：** `/admin/admin/changeEmail`
- **请求方法：** PUT

**请求参数：**

```json
{
  "email": "string",
  "code": "string"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| email | string | 是 | 新邮箱 |
| code | string | 是 | 验证码 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 文章管理

**基础路径：** `/admin/article`

#### 1. 分页查询文章

- **接口路径：** `/admin/article/page`
- **请求方法：** GET

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页数量，默认 10 |
| title | string | 否 | 文章标题（模糊搜索） |
| categoryId | long | 否 | 分类 ID |
| isPublished | int | 否 | 是否发布：0-草稿，1-已发布 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "string",
        "slug": "string",
        "summary": "string",
        "coverImage": "string",
        "categoryId": 1,
        "categoryName": "string",
        "isPublished": 1,
        "isTop": 0,
        "viewCount": 100,
        "createTime": "2024-01-01 00:00:00"
      }
    ],
    "total": 100,
    "page": 1,
    "pageSize": 10,
    "totalPages": 10
  }
}
```

---

#### 2. 根据 ID 获取文章详情

- **接口路径：** `/admin/article/{id}`
- **请求方法：** GET

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 文章 ID |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "string",
    "slug": "string",
    "summary": "string",
    "coverImage": "string",
    "contentMarkdown": "string",
    "contentHtml": "string",
    "categoryId": 1,
    "isPublished": 1,
    "isTop": 0,
    "viewCount": 100,
    "tagIds": [1, 2, 3],
    "createTime": "2024-01-01 00:00:00",
    "updateTime": "2024-01-01 00:00:00"
  }
}
```

---

#### 3. 创建文章

- **接口路径：** `/admin/article`
- **请求方法：** POST
- **操作日志：** 是

**请求参数：**

```json
{
  "title": "string",
  "slug": "string",
  "summary": "string",
  "coverImage": "string",
  "contentMarkdown": "string",
  "contentHtml": "string",
  "categoryId": 1,
  "isPublished": 1,
  "isTop": 0,
  "tagIds": [1, 2, 3]
}
```

| 字段 | 类型 | 必填 | 说明 | 约束 |
|------|------|------|------|------|
| title | string | 是 | 文章标题 | 最大 50 字 |
| slug | string | 是 | URL 标识 | 最大 50 字 |
| summary | string | 否 | 文章摘要 | - |
| coverImage | string | 否 | 封面图片 URL | - |
| contentMarkdown | string | 是 | Markdown 内容 | - |
| contentHtml | string | 否 | HTML 内容 | - |
| categoryId | long | 是 | 分类 ID | - |
| isPublished | int | 否 | 是否发布：0-否，1-是 | - |
| isTop | int | 否 | 是否置顶：0-否，1-是 | - |
| tagIds | array | 否 | 标签 ID 列表 | - |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 4. 更新文章

- **接口路径：** `/admin/article`
- **请求方法：** PUT
- **操作日志：** 是

**请求参数：** 同创建文章，需包含 `id` 字段

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 5. 批量删除文章

- **接口路径：** `/admin/article`
- **请求方法：** DELETE
- **操作日志：** 是

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | array | 是 | 文章 ID 列表 |

示例：`/admin/article?ids=1,2,3`

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 6. 发布/取消发布文章

- **接口路径：** `/admin/article/publish/{id}`
- **请求方法：** PUT
- **操作日志：** 是

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 文章 ID |

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| isPublished | int | 是 | 0-取消发布，1-发布 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 7. 置顶/取消置顶文章

- **接口路径：** `/admin/article/top/{id}`
- **请求方法：** PUT
- **操作日志：** 是

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 文章 ID |

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| isTop | int | 是 | 0-取消置顶，1-置顶 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 8. 文章搜索

- **接口路径：** `/admin/article/search`
- **请求方法：** GET

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 是 | 搜索关键词 |
| page | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页数量，默认 10 |

**响应：** 同分页查询

---

### 文章分类管理

**基础路径：** `/admin/articleCategory`

#### 1. 获取所有分类

- **接口路径：** `/admin/articleCategory`
- **请求方法：** GET

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "string",
      "slug": "string",
      "description": "string",
      "sort": 1
    }
  ]
}
```

---

#### 2. 添加分类

- **接口路径：** `/admin/articleCategory`
- **请求方法：** POST
- **操作日志：** 是

**请求参数：**

```json
{
  "name": "string",
  "slug": "string",
  "description": "string",
  "sort": 1
}
```

| 字段 | 类型 | 必填 | 说明 | 约束 |
|------|------|------|------|------|
| name | string | 是 | 分类名称 | 最大 20 字 |
| slug | string | 是 | URL 标识 | 最大 20 字 |
| description | string | 否 | 分类描述 | 最大 100 字 |
| sort | int | 否 | 排序，越小越靠前 | - |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 3. 更新分类

- **接口路径：** `/admin/articleCategory`
- **请求方法：** PUT
- **操作日志：** 是

**请求参数：** 同添加分类，需包含 `id` 字段

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 4. 批量删除分类

- **接口路径：** `/admin/articleCategory`
- **请求方法：** DELETE
- **操作日志：** 是

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | array | 是 | 分类 ID 列表 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 文章标签管理

**基础路径：** `/admin/article/tag`

#### 1. 获取所有标签

- **接口路径：** `/admin/article/tag`
- **请求方法：** GET

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "string",
      "slug": "string"
    }
  ]
}
```

---

#### 2. 添加标签

- **接口路径：** `/admin/article/tag`
- **请求方法：** POST
- **操作日志：** 是

**请求参数：**

```json
{
  "name": "string",
  "slug": "string"
}
```

| 字段 | 类型 | 必填 | 说明 | 约束 |
|------|------|------|------|------|
| name | string | 是 | 标签名称 | 最大 20 字 |
| slug | string | 是 | URL 标识 | 最大 30 字 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 3. 更新标签

- **接口路径：** `/admin/article/tag`
- **请求方法：** PUT
- **操作日志：** 是

**请求参数：** 同添加标签，需包含 `id` 字段

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 4. 批量删除标签

- **接口路径：** `/admin/article/tag`
- **请求方法：** DELETE
- **操作日志：** 是

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | array | 是 | 标签 ID 列表 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 文章评论管理

**基础路径：** `/admin/article/comment`

#### 1. 分页查询评论

- **接口路径：** `/admin/article/comment/page`
- **请求方法：** GET

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页数量，默认 10 |
| articleId | long | 否 | 文章 ID |
| isApproved | int | 否 | 是否审核：0-未审核，1-已审核 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "articleId": 1,
        "articleTitle": "string",
        "content": "string",
        "nickname": "string",
        "isApproved": 1,
        "createTime": "2024-01-01 00:00:00"
      }
    ],
    "total": 100,
    "page": 1,
    "pageSize": 10,
    "totalPages": 10
  }
}
```

---

#### 2. 根据文章 ID 查询评论

- **接口路径：** `/admin/article/comment/{articleId}`
- **请求方法：** GET

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| articleId | long | 是 | 文章 ID |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "articleId": 1,
      "content": "string",
      "nickname": "string",
      "isApproved": 1,
      "createTime": "2024-01-01 00:00:00"
    }
  ]
}
```

---

#### 3. 批量审核通过评论

- **接口路径：** `/admin/article/comment/approve`
- **请求方法：** PUT
- **操作日志：** 是

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | array | 是 | 评论 ID 列表 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 4. 批量删除评论

- **接口路径：** `/admin/article/comment`
- **请求方法：** DELETE
- **操作日志：** 是

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | array | 是 | 评论 ID 列表 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 5. 管理员回复评论

- **接口路径：** `/admin/article/comment/reply`
- **请求方法：** POST
- **操作日志：** 是

**请求参数：**

```json
{
  "parentId": 1,
  "rootId": 1,
  "content": "string",
  "parentNickname": "string"
}
```

| 字段 | 类型 | 必填 | 说明 | 约束 |
|------|------|------|------|------|
| parentId | long | 是 | 父评论 ID | - |
| rootId | long | 否 | 根评论 ID | - |
| content | string | 是 | 回复内容 | 最大 2000 字 |
| parentNickname | string | 否 | 父评论昵称 | 最大 15 字 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 经历管理

**基础路径：** `/admin/experience`

#### 1. 获取所有经历

- **接口路径：** `/admin/experience`
- **请求方法：** GET

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | int | 否 | 类型：0-教育，1-工作，2-项目 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "type": 0,
      "title": "string",
      "subtitle": "string",
      "logoUrl": "string",
      "content": "string",
      "startDate": "2024-01-01",
      "endDate": "2024-12-31",
      "isVisible": 1
    }
  ]
}
```

---

#### 2. 添加经历

- **接口路径：** `/admin/experience`
- **请求方法：** POST
- **操作日志：** 是

**请求参数：**

```json
{
  "type": 0,
  "title": "string",
  "subtitle": "string",
  "logoUrl": "string",
  "content": "string",
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "isVisible": 1
}
```

| 字段 | 类型 | 必填 | 说明 | 约束 |
|------|------|------|------|------|
| type | int | 是 | 类型：0-教育，1-工作，2-项目 | - |
| title | string | 是 | 标题 | 最大 50 字 |
| subtitle | string | 否 | 副标题 | 最大 100 字 |
| logoUrl | string | 否 | Logo URL | - |
| content | string | 是 | 内容 | - |
| startDate | date | 是 | 开始时间 | - |
| endDate | date | 否 | 结束时间 | - |
| isVisible | int | 否 | 是否可见：0-否，1-是 | - |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 3. 更新经历

- **接口路径：** `/admin/experience`
- **请求方法：** PUT
- **操作日志：** 是

**请求参数：** 同添加经历，需包含 `id` 字段

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 4. 批量删除经历

- **接口路径：** `/admin/experience`
- **请求方法：** DELETE
- **操作日志：** 是

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | array | 是 | 经历 ID 列表 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 技能管理

**基础路径：** `/admin/skill`

#### 1. 获取所有技能

- **接口路径：** `/admin/skill`
- **请求方法：** GET

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "string",
      "description": "string",
      "icon": "string",
      "sort": 1,
      "isVisible": 1
    }
  ]
}
```

---

#### 2. 添加技能

- **接口路径：** `/admin/skill`
- **请求方法：** POST
- **操作日志：** 是

**请求参数：**

```json
{
  "name": "string",
  "description": "string",
  "icon": "string",
  "sort": 1,
  "isVisible": 1
}
```

| 字段 | 类型 | 必填 | 说明 | 约束 |
|------|------|------|------|------|
| name | string | 是 | 技能名称 | 最大 20 字 |
| description | string | 否 | 技能描述 | 最大 255 字 |
| icon | string | 否 | 图标 URL | - |
| sort | int | 否 | 排序 | - |
| isVisible | int | 否 | 是否可见 | - |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 3. 更新技能

- **接口路径：** `/admin/skill`
- **请求方法：** PUT
- **操作日志：** 是

**请求参数：** 同添加技能，需包含 `id` 字段

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 4. 批量删除技能

- **接口路径：** `/admin/skill`
- **请求方法：** DELETE
- **操作日志：** 是

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | array | 是 | 技能 ID 列表 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 友情链接管理

**基础路径：** `/admin/friendLink`

#### 1. 获取所有友链

- **接口路径：** `/admin/friendLink`
- **请求方法：** GET

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "string",
      "url": "string",
      "avatarUrl": "string",
      "description": "string",
      "sort": 1,
      "isVisible": 1
    }
  ]
}
```

---

#### 2. 添加友链

- **接口路径：** `/admin/friendLink`
- **请求方法：** POST
- **操作日志：** 是

**请求参数：**

```json
{
  "name": "string",
  "url": "string",
  "avatarUrl": "string",
  "description": "string",
  "sort": 1,
  "isVisible": 1
}
```

| 字段 | 类型 | 必填 | 说明 | 约束 |
|------|------|------|------|------|
| name | string | 是 | 网站名称 | 最大 20 字 |
| url | string | 是 | 网站地址 | 最大 100 字 |
| avatarUrl | string | 否 | 头像 URL | - |
| description | string | 否 | 网站描述 | 最大 255 字 |
| sort | int | 否 | 排序 | - |
| isVisible | int | 否 | 是否可见 | - |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 3. 更新友链

- **接口路径：** `/admin/friendLink`
- **请求方法：** PUT
- **操作日志：** 是

**请求参数：** 同添加友链，需包含 `id` 字段

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 4. 批量删除友链

- **接口路径：** `/admin/friendLink`
- **请求方法：** DELETE
- **操作日志：** 是

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | array | 是 | 友链 ID 列表 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 社交媒体管理

**基础路径：** `/admin/socialMedia`

#### 1. 获取所有社交媒体

- **接口路径：** `/admin/socialMedia`
- **请求方法：** GET

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "string",
      "icon": "string",
      "link": "string",
      "sort": 1,
      "isVisible": 1
    }
  ]
}
```

---

#### 2. 添加社交媒体

- **接口路径：** `/admin/socialMedia`
- **请求方法：** POST
- **操作日志：** 是

**请求参数：**

```json
{
  "name": "string",
  "icon": "string",
  "link": "string",
  "sort": 1,
  "isVisible": 1
}
```

| 字段 | 类型 | 必填 | 说明 | 约束 |
|------|------|------|------|------|
| name | string | 是 | 名称 | 最大 20 字 |
| icon | string | 否 | 图标类名 | 最大 50 字 |
| link | string | 否 | 链接 | 最大 100 字 |
| sort | int | 否 | 排序 | - |
| isVisible | int | 否 | 是否可见 | - |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 3. 更新社交媒体

- **接口路径：** `/admin/socialMedia`
- **请求方法：** PUT
- **操作日志：** 是

**请求参数：** 同添加社交媒体，需包含 `id` 字段

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 4. 批量删除社交媒体

- **接口路径：** `/admin/socialMedia`
- **请求方法：** DELETE
- **操作日志：** 是

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | array | 是 | 社交媒体 ID 列表 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 系统配置管理

**基础路径：** `/admin/systemConfig`

#### 1. 获取所有配置

- **接口路径：** `/admin/systemConfig`
- **请求方法：** GET

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "configKey": "string",
      "configValue": "string",
      "configType": "string",
      "description": "string"
    }
  ]
}
```

---

#### 2. 根据配置键获取配置

- **接口路径：** `/admin/systemConfig/key/{configKey}`
- **请求方法：** GET

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| configKey | string | 是 | 配置键 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "configKey": "string",
    "configValue": "string",
    "configType": "string",
    "description": "string"
  }
}
```

---

#### 3. 根据 ID 获取配置

- **接口路径：** `/admin/systemConfig/{id}`
- **请求方法：** GET

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 配置 ID |

**响应：** 同上

---

#### 4. 添加配置

- **接口路径：** `/admin/systemConfig`
- **请求方法：** POST
- **操作日志：** 是

**请求参数：**

```json
{
  "configKey": "string",
  "configValue": "string",
  "configType": "string",
  "description": "string"
}
```

| 字段 | 类型 | 必填 | 说明 | 约束 |
|------|------|------|------|------|
| configKey | string | 是 | 配置键 | 最大 50 字 |
| configValue | string | 否 | 配置值 | - |
| configType | string | 否 | 配置类型 | 最大 20 字 |
| description | string | 否 | 配置描述 | 最大 255 字 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 5. 更新配置

- **接口路径：** `/admin/systemConfig`
- **请求方法：** PUT
- **操作日志：** 是

**请求参数：** 同添加配置，需包含 `id` 字段

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 6. 批量删除配置

- **接口路径：** `/admin/systemConfig`
- **请求方法：** DELETE
- **操作日志：** 是

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | array | 是 | 配置 ID 列表 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 个人信息管理

**基础路径：** `/admin/personalInfo`

#### 1. 获取个人信息

- **接口路径：** `/admin/personalInfo`
- **请求方法：** GET

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "nickname": "string",
    "tag": "string",
    "description": "string",
    "avatar": "string",
    "website": "string",
    "email": "string",
    "github": "string",
    "location": "string"
  }
}
```

---

#### 2. 更新个人信息

- **接口路径：** `/admin/personalInfo`
- **请求方法：** PUT
- **操作日志：** 是

**请求参数：**

```json
{
  "id": 1,
  "nickname": "string",
  "tag": "string",
  "description": "string",
  "avatar": "string",
  "website": "string",
  "email": "string",
  "github": "string",
  "location": "string"
}
```

| 字段 | 类型 | 必填 | 说明 | 约束 |
|------|------|------|------|------|
| id | long | 否 | 个人信息 ID | - |
| nickname | string | 是 | 昵称 | 最大 20 字 |
| tag | string | 是 | 标签 | 最大 30 字 |
| description | string | 否 | 个人简介 | 最大 50 字 |
| avatar | string | 否 | 头像 URL | - |
| website | string | 否 | 个人网站 | 最大 100 字 |
| email | string | 否 | 电子邮箱 | 最大 50 字 |
| github | string | 否 | GitHub | 最大 100 字 |
| location | string | 否 | 所在地 | 最大 50 字 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 音乐管理

**基础路径：** `/admin/music`

#### 1. 分页查询音乐

- **接口路径：** `/admin/music/page`
- **请求方法：** GET

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页数量，默认 10 |
| isVisible | int | 否 | 是否可见 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "string",
        "artist": "string",
        "duration": 180,
        "coverImage": "string",
        "musicUrl": "string",
        "lyricUrl": "string",
        "hasLyric": 1,
        "lyricType": "string",
        "sort": 1,
        "isVisible": 1
      }
    ],
    "total": 100,
    "page": 1,
    "pageSize": 10,
    "totalPages": 10
  }
}
```

---

#### 2. 根据 ID 获取音乐

- **接口路径：** `/admin/music/{id}`
- **请求方法：** GET

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 音乐 ID |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "string",
    "artist": "string",
    "duration": 180,
    "coverImage": "string",
    "musicUrl": "string",
    "lyricUrl": "string",
    "hasLyric": 1,
    "lyricType": "string",
    "sort": 1,
    "isVisible": 1
  }
}
```

---

#### 3. 添加音乐

- **接口路径：** `/admin/music`
- **请求方法：** POST
- **操作日志：** 是

**请求参数：**

```json
{
  "title": "string",
  "artist": "string",
  "duration": 180,
  "coverImage": "string",
  "musicUrl": "string",
  "lyricUrl": "string",
  "hasLyric": 1,
  "lyricType": "string",
  "sort": 1,
  "isVisible": 1
}
```

| 字段 | 类型 | 必填 | 说明 | 约束 |
|------|------|------|------|------|
| title | string | 是 | 音乐标题 | 最大 50 字 |
| artist | string | 否 | 作者 | 最大 50 字 |
| duration | int | 否 | 时长（秒） | - |
| coverImage | string | 否 | 封面 URL | - |
| musicUrl | string | 是 | 音频 URL | - |
| lyricUrl | string | 否 | 歌词 URL | - |
| hasLyric | int | 否 | 是否有歌词 | - |
| lyricType | string | 否 | 歌词类型 | 最大 10 字 |
| sort | int | 否 | 排序 | - |
| isVisible | int | 否 | 是否可见 | - |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 4. 更新音乐

- **接口路径：** `/admin/music`
- **请求方法：** PUT
- **操作日志：** 是

**请求参数：** 同添加音乐，需包含 `id` 字段

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 5. 批量删除音乐

- **接口路径：** `/admin/music`
- **请求方法：** DELETE
- **操作日志：** 是

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | array | 是 | 音乐 ID 列表 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 留言管理

**基础路径：** `/admin/message`

#### 1. 分页查询留言

- **接口路径：** `/admin/message/page`
- **请求方法：** GET

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页数量，默认 10 |
| isApproved | int | 否 | 是否审核 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "content": "string",
        "nickname": "string",
        "isApproved": 1,
        "createTime": "2024-01-01 00:00:00"
      }
    ],
    "total": 100,
    "page": 1,
    "pageSize": 10,
    "totalPages": 10
  }
}
```

---

#### 2. 批量审核通过留言

- **接口路径：** `/admin/message/approve`
- **请求方法：** PUT
- **操作日志：** 是

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | array | 是 | 留言 ID 列表 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 3. 批量删除留言

- **接口路径：** `/admin/message`
- **请求方法：** DELETE
- **操作日志：** 是

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | array | 是 | 留言 ID 列表 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 4. 管理员回复留言

- **接口路径：** `/admin/message/reply`
- **请求方法：** POST
- **操作日志：** 是

**请求参数：**

```json
{
  "parentId": 1,
  "rootId": 1,
  "content": "string",
  "parentNickname": "string"
}
```

| 字段 | 类型 | 必填 | 说明 | 约束 |
|------|------|------|------|------|
| parentId | long | 是 | 父留言 ID | - |
| rootId | long | 否 | 根留言 ID | - |
| content | string | 是 | 回复内容 | 最大 2000 字 |
| parentNickname | string | 否 | 父留言昵称 | 最大 15 字 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### RSS 订阅管理

**基础路径：** `/admin/rssSubscription`

#### 1. 分页查询订阅

- **接口路径：** `/admin/rssSubscription/page`
- **请求方法：** GET

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页数量，默认 10 |
| isActive | int | 否 | 是否激活 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "visitorId": 1,
        "nickname": "string",
        "email": "string",
        "isActive": 1,
        "subscribeTime": "2024-01-01 00:00:00"
      }
    ],
    "total": 100,
    "page": 1,
    "pageSize": 10,
    "totalPages": 10
  }
}
```

---

#### 2. 获取所有激活的订阅

- **接口路径：** `/admin/rssSubscription`
- **请求方法：** GET

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "visitorId": 1,
      "nickname": "string",
      "email": "string",
      "isActive": 1
    }
  ]
}
```

---

#### 3. 根据 ID 查询订阅

- **接口路径：** `/admin/rssSubscription/{id}`
- **请求方法：** GET

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 订阅 ID |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "visitorId": 1,
    "nickname": "string",
    "email": "string",
    "isActive": 1
  }
}
```

---

#### 4. 更新订阅

- **接口路径：** `/admin/rssSubscription`
- **请求方法：** PUT
- **操作日志：** 是

**请求参数：**

```json
{
  "id": 1,
  "visitorId": 1,
  "nickname": "string",
  "email": "string",
  "isActive": 1
}
```

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 5. 批量删除订阅

- **接口路径：** `/admin/rssSubscription`
- **请求方法：** DELETE
- **操作日志：** 是

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | array | 是 | 订阅 ID 列表 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 访客管理

**基础路径：** `/admin/visitor`

#### 1. 获取访客列表

- **接口路径：** `/admin/visitor/page`
- **请求方法：** GET

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页数量，默认 10 |
| isBlocked | int | 否 | 是否封禁 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "ip": "string",
        "userAgent": "string",
        "isBlocked": 0,
        "lastVisitTime": "2024-01-01 00:00:00"
      }
    ],
    "total": 100,
    "page": 1,
    "pageSize": 10,
    "totalPages": 10
  }
}
```

---

#### 2. 批量封禁访客

- **接口路径：** `/admin/visitor/block`
- **请求方法：** PUT
- **操作日志：** 是

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | array | 是 | 访客 ID 列表 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 3. 批量解封访客

- **接口路径：** `/admin/visitor/unblock`
- **请求方法：** PUT
- **操作日志：** 是

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | array | 是 | 访客 ID 列表 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 浏览记录管理

**基础路径：** `/admin/view`

#### 1. 获取浏览记录列表

- **接口路径：** `/admin/view/page`
- **请求方法：** GET

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页数量，默认 10 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "visitorId": 1,
        "pagePath": "string",
        "pageTitle": "string",
        "ip": "string",
        "visitTime": "2024-01-01 00:00:00"
      }
    ],
    "total": 100,
    "page": 1,
    "pageSize": 10,
    "totalPages": 10
  }
}
```

---

#### 2. 批量删除浏览记录

- **接口路径：** `/admin/view`
- **请求方法：** DELETE

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | array | 是 | 浏览记录 ID 列表 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 操作日志管理

**基础路径：** `/admin/operationLog`

#### 1. 分页查询操作日志

- **接口路径：** `/admin/operationLog/page`
- **请求方法：** GET

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页数量，默认 10 |
| operationType | string | 否 | 操作类型 |
| target | string | 否 | 操作目标 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "operationType": "INSERT",
        "target": "article",
        "targetId": 1,
        "operatorId": 1,
        "operatorName": "string",
        "operationTime": "2024-01-01 00:00:00"
      }
    ],
    "total": 100,
    "page": 1,
    "pageSize": 10,
    "totalPages": 10
  }
}
```

---

#### 2. 批量删除操作日志

- **接口路径：** `/admin/operationLog`
- **请求方法：** DELETE

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | array | 是 | 操作日志 ID 列表 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 统计数据

**基础路径：** `/admin/report`

#### 1. 浏览量统计

- **接口路径：** `/admin/report/viewStatistics`
- **请求方法：** GET

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| begin | date | 是 | 开始日期（yyyy-MM-dd） |
| end | date | 是 | 结束日期（yyyy-MM-dd） |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalViewCount": 10000,
    "dailyViewCounts": [
      {
        "date": "2024-01-01",
        "count": 100
      }
    ]
  }
}
```

---

#### 2. 访客统计

- **接口路径：** `/admin/report/visitorStatistics`
- **请求方法：** GET

**请求参数（Query）：** 同浏览量统计

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalVisitorCount": 5000,
    "dailyVisitorCounts": [
      {
        "date": "2024-01-01",
        "count": 50
      }
    ]
  }
}
```

---

#### 3. 访客省份分布统计

- **接口路径：** `/admin/report/provinceDistribution`
- **请求方法：** GET

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "provinceCounts": [
      {
        "province": "北京市",
        "count": 100
      }
    ]
  }
}
```

---

#### 4. 文章访问量排行前十

- **接口路径：** `/admin/report/articleViewTop10`
- **请求方法：** GET

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "articleViewCounts": [
      {
        "articleId": 1,
        "articleTitle": "string",
        "viewCount": 1000
      }
    ]
  }
}
```

---

#### 5. 获取总览数据

- **接口路径：** `/admin/report/overview`
- **请求方法：** GET

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalViewCount": 10000,
    "totalVisitorCount": 5000,
    "todayViewCount": 100,
    "todayVisitorCount": 50
  }
}
```

---

### 通用接口

**基础路径：** `/admin/common`

#### 文件上传

- **接口路径：** `/admin/common/upload`
- **请求方法：** POST
- **Content-Type：** `multipart/form-data`

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | file | 是 | 上传的文件 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": "https://example.com/uploads/file.jpg"
}
```

---

## 博客端接口 (Blog)

### 文章相关

**基础路径：** `/blog/article`

#### 1. 获取已发布文章列表

- **接口路径：** `/blog/article/page`
- **请求方法：** GET

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页数量，默认 10 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "string",
        "slug": "string",
        "summary": "string",
        "coverImage": "string",
        "categoryId": 1,
        "categoryName": "string",
        "viewCount": 100,
        "createTime": "2024-01-01 00:00:00"
      }
    ],
    "total": 100,
    "page": 1,
    "pageSize": 10,
    "totalPages": 10
  }
}
```

---

#### 2. 根据 slug 获取文章详情

- **接口路径：** `/blog/article/detail/{slug}`
- **请求方法：** GET

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| slug | string | 是 | 文章 URL 标识 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "string",
    "slug": "string",
    "summary": "string",
    "coverImage": "string",
    "contentMarkdown": "string",
    "contentHtml": "string",
    "categoryId": 1,
    "categoryName": "string",
    "viewCount": 100,
    "likeCount": 50,
    "commentCount": 30,
    "wordCount": 5000,
    "readingTime": 17,
    "publishTime": "2024-01-01 00:00:00",
    "updateTime": "2024-01-02 00:00:00",
    "tagNames": ["Java", "Spring", "MySQL"],
    "prevArticle": {
      "id": 1,
      "title": "string",
      "slug": "string"
    },
    "nextArticle": {
      "id": 2,
      "title": "string",
      "slug": "string"
    },
    "relatedArticles": [
      {
        "id": 3,
        "title": "string",
        "slug": "string"
      }
    ]
  }
}
```

---

#### 3. 根据分类 ID 获取文章列表

- **接口路径：** `/blog/article/category/{categoryId}`
- **请求方法：** GET

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| categoryId | long | 是 | 分类 ID |

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页数量，默认 10 |

**响应：** 同获取已发布文章列表

---

#### 4. 获取文章归档

- **接口路径：** `/blog/article/archive`
- **请求方法：** GET

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "year": 2024,
      "month": 1,
      "articles": [
        {
          "id": 1,
          "title": "string",
          "slug": "string",
          "createTime": "2024-01-01 00:00:00"
        }
      ]
    }
  ]
}
```

---

#### 5. 文章搜索

- **接口路径：** `/blog/article/search`
- **请求方法：** GET
- **限流：** IP 限流，10 tokens/60 秒，突发容量 15

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 是 | 搜索关键词 |
| page | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页数量，默认 10 |

**响应：** 同获取已发布文章列表

---

### 文章分类

**基础路径：** `/blog/articleCategory`

#### 获取可见分类

- **接口路径：** `/blog/articleCategory`
- **请求方法：** GET

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "string",
      "slug": "string",
      "description": "string",
      "sort": 1
    }
  ]
}
```

---

### 文章标签

**基础路径：** `/blog/article/tag`

#### 1. 获取可见标签

- **接口路径：** `/blog/article/tag`
- **请求方法：** GET

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "string",
      "slug": "string"
    }
  ]
}
```

---

#### 2. 根据标签 ID 获取文章列表

- **接口路径：** `/blog/article/tag/{tagId}`
- **请求方法：** GET

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| tagId | long | 是 | 标签 ID |

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页数量，默认 10 |

**响应：** 同获取已发布文章列表

---

### 文章评论

**基础路径：** `/blog/articleComment`

#### 1. 获取文章评论树

- **接口路径：** `/blog/articleComment/article/{articleId}`
- **请求方法：** GET

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| articleId | long | 是 | 文章 ID |

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| visitorId | long | 否 | 访客 ID（用于获取未审核评论） |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "articleId": 1,
      "content": "string",
      "nickname": "string",
      "createTime": "2024-01-01 00:00:00",
      "isApproved": 1,
      "replies": []
    }
  ]
}
```

---

#### 2. 提交评论

- **接口路径：** `/blog/articleComment`
- **请求方法：** POST
- **限流：** IP 限流，5 tokens/60 秒，突发容量 8

**请求参数：**

```json
{
  "articleId": 1,
  "rootId": 1,
  "parentId": 1,
  "parentNickname": "string",
  "content": "string",
  "visitorId": 1,
  "nickname": "string",
  "emailOrQq": "string",
  "isMarkdown": 1,
  "isSecret": 0,
  "isNotice": 1
}
```

| 字段 | 类型 | 必填 | 说明 | 约束 |
|------|------|------|------|------|
| articleId | long | 是 | 文章 ID | - |
| rootId | long | 否 | 根评论 ID | - |
| parentId | long | 否 | 父评论 ID | - |
| parentNickname | string | 否 | 父评论昵称 | 最大 15 字 |
| content | string | 是 | 评论内容 | 最大 2000 字 |
| visitorId | long | 是 | 访客 ID | - |
| nickname | string | 是 | 昵称 | 最大 15 字 |
| emailOrQq | string | 否 | 邮箱或 QQ | 最大 50 字 |
| isMarkdown | int | 否 | 是否使用 Markdown | - |
| isSecret | int | 否 | 是否匿名 | - |
| isNotice | int | 否 | 有回复是否通知 | - |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 3. 编辑评论

- **接口路径：** `/blog/articleComment/edit`
- **请求方法：** PUT
- **限流：** IP 限流，5 tokens/60 秒，突发容量 8

**请求参数：**

```json
{
  "id": 1,
  "visitorId": 1,
  "content": "string"
}
```

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 4. 删除评论

- **接口路径：** `/blog/articleComment/{id}`
- **请求方法：** DELETE
- **限流：** IP 限流，5 tokens/60 秒，突发容量 8

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 评论 ID |

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| visitorId | long | 是 | 访客 ID |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 文章点赞

**基础路径：** `/blog/articleLike`

#### 1. 点赞文章

- **接口路径：** `/blog/articleLike/{articleId}`
- **请求方法：** POST
- **限流：** IP 限流，10 tokens/60 秒，突发容量 15

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| articleId | long | 是 | 文章 ID |

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| visitorId | long | 是 | 访客 ID |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 2. 取消点赞

- **接口路径：** `/blog/articleLike/{articleId}`
- **请求方法：** DELETE
- **限流：** IP 限流，10 tokens/60 秒，突发容量 15

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| articleId | long | 是 | 文章 ID |

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| visitorId | long | 是 | 访客 ID |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 3. 检查是否已点赞

- **接口路径：** `/blog/articleLike/{articleId}`
- **请求方法：** GET

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| articleId | long | 是 | 文章 ID |

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| visitorId | long | 是 | 访客 ID |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

---

### 留言

**基础路径：** `/blog/message`

#### 1. 提交留言

- **接口路径：** `/blog/message`
- **请求方法：** POST
- **限流：** IP 限流，5 tokens/60 秒，突发容量 8

**请求参数：**

```json
{
  "content": "string",
  "rootId": 1,
  "parentId": 1,
  "parentNickname": "string",
  "visitorId": 1,
  "nickname": "string",
  "emailOrQq": "string",
  "isMarkdown": 1,
  "isSecret": 0,
  "isNotice": 1
}
```

| 字段 | 类型 | 必填 | 说明 | 约束 |
|------|------|------|------|------|
| content | string | 是 | 留言内容 | 最大 2000 字 |
| rootId | long | 否 | 根留言 ID | - |
| parentId | long | 否 | 父留言 ID | - |
| parentNickname | string | 否 | 父留言昵称 | 最大 15 字 |
| visitorId | long | 是 | 访客 ID | - |
| nickname | string | 是 | 昵称 | 最大 15 字 |
| emailOrQq | string | 否 | 邮箱或 QQ | 最大 50 字 |
| isMarkdown | int | 否 | 是否使用 Markdown | - |
| isSecret | int | 否 | 是否匿名 | - |
| isNotice | int | 否 | 有回复是否通知 | - |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 2. 获取留言树

- **接口路径：** `/blog/message`
- **请求方法：** GET

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| visitorId | long | 否 | 访客 ID（用于获取未审核留言） |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "content": "string",
      "nickname": "string",
      "createTime": "2024-01-01 00:00:00",
      "isApproved": 1,
      "replies": []
    }
  ]
}
```

---

#### 3. 编辑留言

- **接口路径：** `/blog/message/edit`
- **请求方法：** PUT
- **限流：** IP 限流，5 tokens/60 秒，突发容量 8

**请求参数：**

```json
{
  "id": 1,
  "visitorId": 1,
  "content": "string"
}
```

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 4. 删除留言

- **接口路径：** `/blog/message/{id}`
- **请求方法：** DELETE
- **限流：** IP 限流，5 tokens/60 秒，突发容量 8

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 留言 ID |

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| visitorId | long | 是 | 访客 ID |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 友情链接

**基础路径：** `/blog/friendLink`

#### 获取可见友链

- **接口路径：** `/blog/friendLink`
- **请求方法：** GET

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "string",
      "url": "string",
      "avatarUrl": "string",
      "description": "string"
    }
  ]
}
```

---

### 音乐

**基础路径：** `/blog/music`

#### 获取可见音乐

- **接口路径：** `/blog/music`
- **请求方法：** GET

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "title": "string",
      "artist": "string",
      "duration": 180,
      "coverImage": "string",
      "musicUrl": "string",
      "lyricUrl": "string",
      "hasLyric": 1,
      "lyricType": "string"
    }
  ]
}
```

---

### 个人信息

**基础路径：** `/blog/personalInfo`

#### 获取个人信息

- **接口路径：** `/blog/personalInfo`
- **请求方法：** GET

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "nickname": "string",
    "tag": "string",
    "description": "string",
    "avatar": "string",
    "website": "string",
    "email": "string",
    "github": "string",
    "location": "string"
  }
}
```

---

### 系统配置

**基础路径：** `/blog/systemConfig`

#### 根据配置键获取配置

- **接口路径：** `/blog/systemConfig/key/{configKey}`
- **请求方法：** GET

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| configKey | string | 是 | 配置键 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "configKey": "string",
    "configValue": "string",
    "configType": "string",
    "description": "string"
  }
}
```

---

### 社交媒体

**基础路径：** `/blog/socialMedia`（注：实际代码中博客端没有单独的社交媒体接口，使用首页端接口）

---

### RSS 订阅

**基础路径：** `/blog/rssSubscription`

#### 1. 添加订阅

- **接口路径：** `/blog/rssSubscription`
- **请求方法：** POST
- **限流：** IP 限流，5 tokens/60 秒，突发容量 8

**请求参数：**

```json
{
  "visitorId": 1,
  "nickname": "string",
  "email": "string"
}
```

| 字段 | 类型 | 必填 | 说明 | 约束 |
|------|------|------|------|------|
| visitorId | long | 是 | 访客 ID | - |
| nickname | string | 否 | 昵称 | 最大 15 字 |
| email | string | 是 | 邮箱 | 有效邮箱格式 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 2. 取消订阅

- **接口路径：** `/blog/rssSubscription/unsubscribe`
- **请求方法：** PUT

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| email | string | 是 | 邮箱 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 3. 检查订阅状态

- **接口路径：** `/blog/rssSubscription/check`
- **请求方法：** GET

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| visitorId | long | 是 | 访客 ID |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "isSubscribed": true,
    "subscription": {
      "id": 1,
      "email": "string",
      "subscribeTime": "2024-01-01 00:00:00"
    }
  }
}
```

---

### RSS Feed

**基础路径：** `/blog/rss`

#### 获取 RSS Feed

- **接口路径：** `/blog/rss`
- **请求方法：** GET
- **Content-Type：** `application/xml; charset=UTF-8`
- **缓存：** 启用缓存

**响应：** RSS 2.0 XML 格式

```xml
<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0">
  <channel>
    <title>FeiTwnd Blog</title>
    <link>https://example.com</link>
    <description>FeiTwnd's Blog</description>
    <item>
      <title>文章标题</title>
      <link>https://example.com/article/slug</link>
      <description>文章摘要</description>
      <pubDate>Mon, 01 Jan 2024 00:00:00 GMT</pubDate>
    </item>
  </channel>
</rss>
```

---

### 站点地图

**基础路径：** `/blog/sitemap.xml`

#### 获取站点地图

- **接口路径：** `/blog/sitemap.xml`
- **请求方法：** GET
- **Content-Type：** `application/xml; charset=UTF-8`
- **缓存：** 启用缓存

**响应：** Sitemap XML 格式

```xml
<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
  <url>
    <loc>https://example.com</loc>
    <lastmod>2024-01-01</lastmod>
    <changefreq>daily</changefreq>
    <priority>1.0</priority>
  </url>
</urlset>
```

---

### 访客记录

**基础路径：** `/blog/visitor`

#### 记录访客访问

- **接口路径：** `/blog/visitor/record`
- **请求方法：** POST
- **限流：** IP 限流，10 tokens/60 秒，突发容量 15

**请求参数：**

```json
{
  "pagePath": "string",
  "pageTitle": "string",
  "referer": "string",
  "screen": "1920x1080",
  "timezone": "Asia/Shanghai",
  "language": "zh-CN",
  "platform": "Win32",
  "cookiesEnabled": true,
  "deviceMemory": 8,
  "hardwareConcurrency": 8
}
```

| 字段 | 类型 | 必填 | 说明 | 约束 |
|------|------|------|------|------|
| pagePath | string | 是 | 访问路径 | 最大 500 字 |
| pageTitle | string | 否 | 页面标题 | 最大 200 字 |
| referer | string | 否 | 来源页面 | 最大 500 字 |
| screen | string | 否 | 屏幕分辨率 | - |
| timezone | string | 否 | 时区 | - |
| language | string | 否 | 语言 | - |
| platform | string | 否 | 平台 | - |
| cookiesEnabled | boolean | 否 | 是否支持 Cookie | - |
| deviceMemory | int | 否 | 设备内存 | - |
| hardwareConcurrency | int | 否 | CPU 核心数 | - |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "visitorId": 1,
    "ip": "string",
    "region": "string",
    "isp": "string"
  }
}
```

---

### 统计数据

**基础路径：** `/blog/report`

#### 获取博客统计数据

- **接口路径：** `/blog/report`
- **请求方法：** GET

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "articleCount": 100,
    "categoryCount": 10,
    "tagCount": 50,
    "viewCount": 10000,
    "musicCount": 20
  }
}
```

---

### 验证码

**基础路径：** `/blog/common`

#### 生成验证码

- **接口路径：** `/blog/common/captcha/generate`
- **请求方法：** GET

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "captchaId": "captcha_1234567890_123",
    "question": "5 + 3 = ?",
    "result": 8
  }
}
```

---

## 首页端接口 (Home)

### 个人信息

**基础路径：** `/home/personalInfo`

#### 获取个人信息

- **接口路径：** `/home/personalInfo`
- **请求方法：** GET

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "nickname": "string",
    "tag": "string",
    "description": "string",
    "avatar": "string",
    "website": "string",
    "email": "string",
    "github": "string",
    "location": "string"
  }
}
```

---

### 社交媒体

**基础路径：** `/home/socialMedia`

#### 获取可见社交媒体

- **接口路径：** `/home/socialMedia`
- **请求方法：** GET

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "string",
      "icon": "string",
      "link": "string"
    }
  ]
}
```

---

### 系统配置

**基础路径：** `/home/systemConfig`

#### 根据配置键获取配置

- **接口路径：** `/home/systemConfig/key/{configKey}`
- **请求方法：** GET

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| configKey | string | 是 | 配置键 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "configKey": "string",
    "configValue": "string",
    "configType": "string",
    "description": "string"
  }
}
```

---

### 访客记录

**基础路径：** `/home/visitor`

#### 记录访客访问

- **接口路径：** `/home/visitor/record`
- **请求方法：** POST
- **限流：** IP 限流，10 tokens/60 秒，突发容量 15

**请求参数：** 同博客端访客记录

**响应：** 同博客端访客记录

---

## 简历端接口 (CV)

### 个人信息

**基础路径：** `/cv/personalInfo`

#### 获取个人信息

- **接口路径：** `/cv/personalInfo`
- **请求方法：** GET

**响应：** 同首页端个人信息

---

### 经历

**基础路径：** `/cv/experience`

#### 获取全部经历

- **接口路径：** `/cv/experience`
- **请求方法：** GET

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "type": 0,
      "typeLabel": "教育经历",
      "title": "string",
      "subtitle": "string",
      "logoUrl": "string",
      "content": "string",
      "startDate": "2024-01-01",
      "endDate": "2024-12-31"
    }
  ]
}
```

---

### 技能

**基础路径：** `/cv/skill`

#### 获取技能列表

- **接口路径：** `/cv/skill`
- **请求方法：** GET

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "string",
      "description": "string",
      "icon": "string"
    }
  ]
}
```

---

### 访客记录

**基础路径：** `/cv/visitor`

#### 记录访客访问

- **接口路径：** `/cv/visitor/record`
- **请求方法：** POST
- **限流：** IP 限流，10 tokens/60 秒，突发容量 15

**请求参数：** 同博客端访客记录

**响应：** 同博客端访客记录

---

## 公共接口

### 健康检查

**基础路径：** `/health`

#### 健康检查

- **接口路径：** `/health`
- **请求方法：** GET

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": "Server is running"
}
```

---

## 附录

### 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 429 | 请求过于频繁 |
| 500 | 服务器内部错误 |

### 数据字典

#### 经历类型（type）

| 值 | 说明 |
|----|------|
| 0 | 教育经历 |
| 1 | 实习及工作经历 |
| 2 | 项目经历 |

#### 是否类型字段

对于 `isPublished`、`isTop`、`isVisible`、`isSecret` 等布尔类型字段：

| 值 | 说明 |
|----|------|
| 0 | 否 |
| 1 | 是 |

#### 操作类型（operationType）

| 值 | 说明 |
|----|------|
| INSERT | 插入操作 |
| UPDATE | 更新操作 |
| DELETE | 删除操作 |
| OTHER | 其他操作 |

---

**文档版本：** 1.0  
**最后更新：** 2026-03-15
