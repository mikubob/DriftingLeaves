---
title: 默认模块
language_tabs:
  - shell: Shell
  - http: HTTP
  - javascript: JavaScript
  - ruby: Ruby
  - python: Python
  - php: PHP
  - java: Java
  - go: Go
toc_footers: []
includes: []
search: true
code_clipboard: true
highlight_theme: darkula
headingLevel: 2
generator: "@tarslib/widdershins v4.0.30"

---

# 默认模块

Base URLs:

# Authentication

# 健康检查接口

## GET 健康检查

GET /health

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": "string"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultString](#schemaresultstring)|

# 管理端浏览记录接口

## GET 获取浏览记录列表

GET /admin/view/page

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|page|query|integer| 否 |页码|
|pageSize|query|integer| 否 |每页显示数量|
|pagePath|query|string| 否 |页面路径|
|referer|query|string| 否 |来源URL|
|visitorId|query|integer(int64)| 否 |访客ID|

> 返回示例

> 200 Response

```json
{
  "type": "object",
  "properties": {
    "code": {
      "type": "integer",
      "description": "编码：1成功，0和其它数字为失败"
    },
    "msg": {
      "type": "string",
      "description": "错误信息"
    },
    "data": {
      "type": "object",
      "properties": {
        "page": {
          "type": "integer",
          "description": "当前页码",
          "format": "int64"
        },
        "pageSize": {
          "type": "integer",
          "description": "每页显示数量",
          "format": "int64"
        },
        "total": {
          "type": "integer",
          "description": "总记录数",
          "format": "int64"
        },
        "totalPages": {
          "type": "integer",
          "description": "总页数",
          "format": "int64"
        },
        "records": {
          "type": "array",
          "items": {
            "$ref": "#/components/schemas/1"
          },
          "description": "数据列表"
        }
      },
      "description": "数据"
    }
  },
  "description": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultPageResult](#schemaresultpageresult)|

## DELETE 批量删除浏览记录

DELETE /admin/view

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|ids|query|array[integer]| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

# 管理端管理员接口

## POST 发送验证码

POST /admin/admin/sendCode

> Body 请求参数

```json
{
  "username": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[SendCodeDTO](#schemasendcodedto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## POST 管理员登录

POST /admin/admin/login

> Body 请求参数

```json
{
  "username": "string",
  "password": "string",
  "code": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[AdminLoginDTO](#schemaadminlogindto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "id": 0,
    "token": "string"
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultAdminLoginVO](#schemaresultadminloginvo)|

## GET 获取管理员信息

GET /admin/admin

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "id": 0,
    "nickname": "string",
    "email": "string"
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultAdminVO](#schemaresultadminvo)|

## POST 管理员退出登录

POST /admin/admin/logout

> Body 请求参数

```json
{
  "id": 0,
  "token": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[AdminLogoutDTO](#schemaadminlogoutdto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## PUT 管理员修改密码

PUT /admin/admin/changePassword

> Body 请求参数

```json
{
  "oldPassword": "string",
  "newPassword": "string",
  "confirmNewPassword": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[AdminChangePasswordDTO](#schemaadminchangepassworddto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## PUT 管理员更改昵称

PUT /admin/admin/changeNickname

> Body 请求参数

```json
{
  "nickname": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[AdminChangeNicknameDTO](#schemaadminchangenicknamedto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## PUT 管理员换绑邮箱

PUT /admin/admin/changeEmail

> Body 请求参数

```json
{
  "email": "user@example.com",
  "code": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[AdminChangeEmailDTO](#schemaadminchangeemaildto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

# 管理端音乐接口

## GET 分页查询音乐列表

GET /admin/music/page

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|page|query|integer| 否 |页码|
|pageSize|query|integer| 否 |每页显示数量|
|title|query|string| 否 |音乐标题|
|artist|query|string| 否 |作者|
|isVisible|query|integer| 否 |是否可见|

> 返回示例

> 200 Response

```json
{
  "type": "object",
  "properties": {
    "code": {
      "type": "integer",
      "description": "编码：1成功，0和其它数字为失败"
    },
    "msg": {
      "type": "string",
      "description": "错误信息"
    },
    "data": {
      "type": "object",
      "properties": {
        "page": {
          "type": "integer",
          "description": "当前页码",
          "format": "int64"
        },
        "pageSize": {
          "type": "integer",
          "description": "每页显示数量",
          "format": "int64"
        },
        "total": {
          "type": "integer",
          "description": "总记录数",
          "format": "int64"
        },
        "totalPages": {
          "type": "integer",
          "description": "总页数",
          "format": "int64"
        },
        "records": {
          "type": "array",
          "items": {
            "$ref": "#/components/schemas/1"
          },
          "description": "数据列表"
        }
      },
      "description": "数据"
    }
  },
  "description": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultPageResult](#schemaresultpageresult)|

## GET 根据ID查询音乐

GET /admin/music/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "createTime": "string",
    "updateTime": "string",
    "id": 0,
    "title": "string",
    "artist": "string",
    "duration": 0,
    "coverImage": "string",
    "musicUrl": "string",
    "lyricUrl": "string",
    "hasLyric": 0,
    "lyricType": "string",
    "sort": 0,
    "isVisible": 0
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultMusic](#schemaresultmusic)|

## POST 添加音乐

POST /admin/music

> Body 请求参数

```json
{
  "id": 0,
  "title": "string",
  "artist": "string",
  "duration": 0,
  "coverImage": "string",
  "musicUrl": "string",
  "lyricUrl": "string",
  "hasLyric": 0,
  "lyricType": "string",
  "sort": 0,
  "isVisible": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[MusicDTO](#schemamusicdto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## PUT 更新音乐

PUT /admin/music

> Body 请求参数

```json
{
  "id": 0,
  "title": "string",
  "artist": "string",
  "duration": 0,
  "coverImage": "string",
  "musicUrl": "string",
  "lyricUrl": "string",
  "hasLyric": 0,
  "lyricType": "string",
  "sort": 0,
  "isVisible": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[MusicDTO](#schemamusicdto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## DELETE 批量删除音乐

DELETE /admin/music

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|ids|query|array[integer]| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

# 管理端技能接口

## GET 获取所有技能信息

GET /admin/skill

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "createTime": "string",
      "updateTime": "string",
      "id": 0,
      "name": "string",
      "description": "string",
      "icon": "string",
      "sort": 0,
      "isVisible": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultListSkills](#schemaresultlistskills)|

## POST 添加技能信息

POST /admin/skill

> Body 请求参数

```json
{
  "id": 0,
  "name": "string",
  "description": "string",
  "icon": "string",
  "sort": 0,
  "isVisible": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[SkillDTO](#schemaskilldto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## DELETE 批量删除技能信息

DELETE /admin/skill

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|ids|query|array[integer]| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": "string"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultString](#schemaresultstring)|

## PUT 修改技能信息

PUT /admin/skill

> Body 请求参数

```json
{
  "id": 0,
  "name": "string",
  "description": "string",
  "icon": "string",
  "sort": 0,
  "isVisible": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[SkillDTO](#schemaskilldto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

# 管理端通用接口

## POST 文件上传

POST /admin/common/upload

> Body 请求参数

```yaml
file: string

```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|object| 否 |none|
|» file|body|string(binary)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

# 管理端统计相关接口

## GET 浏览量统计

GET /admin/report/viewStatistics

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|begin|query|string| 是 |none|
|end|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "dateList": "string",
    "viewCountList": "string"
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultViewReportVO](#schemaresultviewreportvo)|

## GET 访客统计

GET /admin/report/visitorStatistics

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|begin|query|string| 是 |none|
|end|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "dateList": "string",
    "newVisitorCountList": "string",
    "totalVisitorCountList": "string"
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultVisitorReportVO](#schemaresultvisitorreportvo)|

## GET 访客省份分布统计

GET /admin/report/provinceDistribution

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "provinceList": "string",
    "countList": "string"
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultProvinceVisitorVO](#schemaresultprovincevisitorvo)|

## GET 文章访问量排行前十

GET /admin/report/articleViewTop10

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "titleList": [
      "string"
    ],
    "viewCountList": [
      0
    ]
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultArticleViewTop10VO](#schemaresultarticleviewtop10vo)|

## GET 获取总览数据（总访问量、总访客）

GET /admin/report/overview

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "totalViewCount": 0,
    "totalVisitorCount": 0,
    "todayViewCount": 0,
    "todayNewVisitorCount": 0,
    "totalArticleCount": 0,
    "totalCommentCount": 0,
    "totalMessageCount": 0,
    "pendingCommentCount": 0,
    "pendingMessageCount": 0
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultAdminOverviewVO](#schemaresultadminoverviewvo)|

# 管理端文章接口

## GET 分页条件查询文章列表

GET /admin/article/page

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|page|query|integer| 否 |页码|
|pageSize|query|integer| 否 |每页显示数量|
|title|query|string| 否 |文章标题（模糊搜索）|
|categoryId|query|integer(int64)| 否 |分类ID|
|isPublished|query|integer| 否 |是否发布,0-草稿，1-已发布|

> 返回示例

> 200 Response

```json
{
  "type": "object",
  "properties": {
    "code": {
      "type": "integer",
      "description": "编码：1成功，0和其它数字为失败"
    },
    "msg": {
      "type": "string",
      "description": "错误信息"
    },
    "data": {
      "type": "object",
      "properties": {
        "page": {
          "type": "integer",
          "description": "当前页码",
          "format": "int64"
        },
        "pageSize": {
          "type": "integer",
          "description": "每页显示数量",
          "format": "int64"
        },
        "total": {
          "type": "integer",
          "description": "总记录数",
          "format": "int64"
        },
        "totalPages": {
          "type": "integer",
          "description": "总页数",
          "format": "int64"
        },
        "records": {
          "type": "array",
          "items": {
            "$ref": "#/components/schemas/1"
          },
          "description": "数据列表"
        }
      },
      "description": "数据"
    }
  },
  "description": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultPageResult](#schemaresultpageresult)|

## GET 根据 ID 获取文章详情

GET /admin/article/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "createTime": "string",
    "updateTime": "string",
    "id": 0,
    "title": "string",
    "slug": "string",
    "summary": "string",
    "coverImage": "string",
    "contentMarkdown": "string",
    "contentHtml": "string",
    "categoryId": 0,
    "viewCount": 0,
    "likeCount": 0,
    "commentCount": 0,
    "wordCount": 0,
    "readingTime": 0,
    "isPublished": 0,
    "isTop": 0,
    "publishTime": "string",
    "publishYear": 0,
    "publishMonth": 0,
    "publishDay": 0,
    "publishDate": "string",
    "tagIds": [
      0
    ]
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultArticles](#schemaresultarticles)|

## POST 创建文章

POST /admin/article

> Body 请求参数

```json
{
  "id": 0,
  "title": "string",
  "slug": "string",
  "summary": "string",
  "coverImage": "string",
  "contentMarkdown": "string",
  "contentHtml": "string",
  "categoryId": 0,
  "isPublished": 0,
  "isTop": 0,
  "tagIds": [
    0
  ]
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[ArticleDTO](#schemaarticledto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## PUT 更新文章

PUT /admin/article

> Body 请求参数

```json
{
  "id": 0,
  "title": "string",
  "slug": "string",
  "summary": "string",
  "coverImage": "string",
  "contentMarkdown": "string",
  "contentHtml": "string",
  "categoryId": 0,
  "isPublished": 0,
  "isTop": 0,
  "tagIds": [
    0
  ]
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[ArticleDTO](#schemaarticledto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## DELETE 批量删除文章

DELETE /admin/article

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|ids|query|array[integer]| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## PUT 发布/取消发布文章

PUT /admin/article/publish/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer| 是 |none|
|isPublished|query|integer| 是 |0-取消发布，1-发布|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## PUT 置顶/取消置顶文章

PUT /admin/article/top/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer| 是 |none|
|isTop|query|integer| 是 |0-取消置顶，1-置顶|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## GET 文章搜索（全文本搜索：标题、摘要、内容）

GET /admin/article/search

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|keyword|query|string| 是 |none|
|page|query|integer| 是 |none|
|pageSize|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "type": "object",
  "properties": {
    "code": {
      "type": "integer",
      "description": "编码：1成功，0和其它数字为失败"
    },
    "msg": {
      "type": "string",
      "description": "错误信息"
    },
    "data": {
      "type": "object",
      "properties": {
        "page": {
          "type": "integer",
          "description": "当前页码",
          "format": "int64"
        },
        "pageSize": {
          "type": "integer",
          "description": "每页显示数量",
          "format": "int64"
        },
        "total": {
          "type": "integer",
          "description": "总记录数",
          "format": "int64"
        },
        "totalPages": {
          "type": "integer",
          "description": "总页数",
          "format": "int64"
        },
        "records": {
          "type": "array",
          "items": {
            "$ref": "#/components/schemas/1"
          },
          "description": "数据列表"
        }
      },
      "description": "数据"
    }
  },
  "description": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultPageResult](#schemaresultpageresult)|

# 管理端留言接口

## GET 分页条件查询留言

GET /admin/message/page

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|page|query|integer| 否 |none|
|pageSize|query|integer| 否 |none|
|isApproved|query|integer| 否 |是否审核通过，0-否，1-是|
|startTime|query|string| 否 |开始时间|
|endTime|query|string| 否 |结束时间|

> 返回示例

> 200 Response

```json
{
  "type": "object",
  "properties": {
    "code": {
      "type": "integer",
      "description": "编码：1成功，0和其它数字为失败"
    },
    "msg": {
      "type": "string",
      "description": "错误信息"
    },
    "data": {
      "type": "object",
      "properties": {
        "page": {
          "type": "integer",
          "description": "当前页码",
          "format": "int64"
        },
        "pageSize": {
          "type": "integer",
          "description": "每页显示数量",
          "format": "int64"
        },
        "total": {
          "type": "integer",
          "description": "总记录数",
          "format": "int64"
        },
        "totalPages": {
          "type": "integer",
          "description": "总页数",
          "format": "int64"
        },
        "records": {
          "type": "array",
          "items": {
            "$ref": "#/components/schemas/1"
          },
          "description": "数据列表"
        }
      },
      "description": "数据"
    }
  },
  "description": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultPageResult](#schemaresultpageresult)|

## PUT 批量审核通过留言

PUT /admin/message/approve

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|ids|query|array[integer]| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": "string"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultString](#schemaresultstring)|

## DELETE 批量删除留言

DELETE /admin/message

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|ids|query|array[integer]| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": "string"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultString](#schemaresultstring)|

## POST 管理员回复留言

POST /admin/message/reply

> Body 请求参数

```json
{
  "parentId": 0,
  "rootId": 0,
  "parentNickname": "string",
  "content": "string",
  "isMarkdown": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[MessageReplyDTO](#schemamessagereplydto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": "string"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultString](#schemaresultstring)|

# 管理端访客接口

## GET 获取访客列表

GET /admin/visitor/page

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|page|query|integer| 否 |页码|
|pageSize|query|integer| 否 |每页显示数量|
|country|query|string| 否 |国家|
|province|query|string| 否 |省份|
|city|query|string| 否 |城市|
|status|query|integer| 否 |状态,是否被封禁 0正常 1封禁|

> 返回示例

> 200 Response

```json
{
  "type": "object",
  "properties": {
    "code": {
      "type": "integer",
      "description": "编码：1成功，0和其它数字为失败"
    },
    "msg": {
      "type": "string",
      "description": "错误信息"
    },
    "data": {
      "type": "object",
      "properties": {
        "page": {
          "type": "integer",
          "description": "当前页码",
          "format": "int64"
        },
        "pageSize": {
          "type": "integer",
          "description": "每页显示数量",
          "format": "int64"
        },
        "total": {
          "type": "integer",
          "description": "总记录数",
          "format": "int64"
        },
        "totalPages": {
          "type": "integer",
          "description": "总页数",
          "format": "int64"
        },
        "records": {
          "type": "array",
          "items": {
            "$ref": "#/components/schemas/1"
          },
          "description": "数据列表"
        }
      },
      "description": "数据"
    }
  },
  "description": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultPageResult](#schemaresultpageresult)|

## PUT 批量封禁访客

PUT /admin/visitor/block

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|ids|query|array[integer]| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## PUT 批量解封访客

PUT /admin/visitor/unblock

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|ids|query|array[integer]| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": "string"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultString](#schemaresultstring)|

## DELETE 批量删除访客

DELETE /admin/visitor

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|ids|query|array[integer]| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": "string"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultString](#schemaresultstring)|

# 管理端文章标签接口

## GET 获取所有标签

GET /admin/article/tag

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "createTime": "string",
      "updateTime": "string",
      "id": 0,
      "name": "string",
      "slug": "string",
      "articleCount": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultListArticleTags](#schemaresultlistarticletags)|

## POST 添加标签

POST /admin/article/tag

> Body 请求参数

```json
{
  "id": 0,
  "name": "string",
  "slug": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[ArticleTagDTO](#schemaarticletagdto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## PUT 修改标签

PUT /admin/article/tag

> Body 请求参数

```json
{
  "id": 0,
  "name": "string",
  "slug": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[ArticleTagDTO](#schemaarticletagdto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## DELETE 批量删除标签

DELETE /admin/article/tag

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|ids|query|array[integer]| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

# 管理端经历接口

## GET 根据分类获取经历信息

GET /admin/experience

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|type|query|integer| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "createTime": "string",
      "updateTime": "string",
      "id": 0,
      "type": 0,
      "title": "string",
      "subtitle": "string",
      "logoUrl": "string",
      "content": "string",
      "startDate": "string",
      "endDate": "string",
      "isVisible": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultListExperiences](#schemaresultlistexperiences)|

## POST 添加经历信息

POST /admin/experience

> Body 请求参数

```json
{
  "id": 0,
  "type": 0,
  "title": "string",
  "subtitle": "string",
  "logoUrl": "string",
  "content": "string",
  "startDate": "string",
  "endDate": "string",
  "isVisible": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[ExperienceDTO](#schemaexperiencedto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## PUT 修改经历信息

PUT /admin/experience

> Body 请求参数

```json
{
  "id": 0,
  "type": 0,
  "title": "string",
  "subtitle": "string",
  "logoUrl": "string",
  "content": "string",
  "startDate": "string",
  "endDate": "string",
  "isVisible": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[ExperienceDTO](#schemaexperiencedto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## DELETE 批量删除经历信息

DELETE /admin/experience

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|ids|query|array[integer]| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

# 管理端友链接口

## GET 获取所有友情链接信息

GET /admin/friendLink

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "createTime": "string",
      "updateTime": "string",
      "id": 0,
      "name": "string",
      "url": "string",
      "avatarUrl": "string",
      "description": "string",
      "sort": 0,
      "isVisible": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultListFriendLinks](#schemaresultlistfriendlinks)|

## POST 添加友情链接信息

POST /admin/friendLink

> Body 请求参数

```json
{
  "id": 0,
  "name": "string",
  "url": "string",
  "avatarUrl": "string",
  "description": "string",
  "sort": 0,
  "isVisible": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[FriendLinkDTO](#schemafriendlinkdto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## DELETE 批量删除友情链接信息

DELETE /admin/friendLink

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|ids|query|array[integer]| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## PUT 修改友情链接信息

PUT /admin/friendLink

> Body 请求参数

```json
{
  "id": 0,
  "name": "string",
  "url": "string",
  "avatarUrl": "string",
  "description": "string",
  "sort": 0,
  "isVisible": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[FriendLinkDTO](#schemafriendlinkdto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

# 管理端社交媒体接口

## GET 获取所有社交媒体信息

GET /admin/socialMedia

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "createTime": "string",
      "updateTime": "string",
      "id": 0,
      "name": "string",
      "icon": "string",
      "link": "string",
      "sort": 0,
      "isVisible": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultListSocialMedia](#schemaresultlistsocialmedia)|

## POST 添加社交媒体信息

POST /admin/socialMedia

> Body 请求参数

```json
{
  "id": 0,
  "name": "string",
  "icon": "string",
  "link": "string",
  "sort": 0,
  "isVisible": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[SocialMediaDTO](#schemasocialmediadto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## DELETE 批量删除社交媒体信息

DELETE /admin/socialMedia

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|ids|query|array[integer]| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## PUT 修改社交媒体信息

PUT /admin/socialMedia

> Body 请求参数

```json
{
  "id": 0,
  "name": "string",
  "icon": "string",
  "link": "string",
  "sort": 0,
  "isVisible": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[SocialMediaDTO](#schemasocialmediadto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

# 管理端操作日志接口

## GET 分页查询操作日志

GET /admin/operationLog/page

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|page|query|integer| 否 |页码|
|pageSize|query|integer| 否 |每页显示数量|
|adminId|query|integer(int64)| 否 |管理员ID|
|operationType|query|string| 否 |操作类型|
|operationTarget|query|string| 否 |操作对象|
|startTime|query|string| 否 |开始时间|
|endTime|query|string| 否 |结束时间|

> 返回示例

> 200 Response

```json
{
  "type": "object",
  "properties": {
    "code": {
      "type": "integer",
      "description": "编码：1成功，0和其它数字为失败"
    },
    "msg": {
      "type": "string",
      "description": "错误信息"
    },
    "data": {
      "type": "object",
      "properties": {
        "page": {
          "type": "integer",
          "description": "当前页码",
          "format": "int64"
        },
        "pageSize": {
          "type": "integer",
          "description": "每页显示数量",
          "format": "int64"
        },
        "total": {
          "type": "integer",
          "description": "总记录数",
          "format": "int64"
        },
        "totalPages": {
          "type": "integer",
          "description": "总页数",
          "format": "int64"
        },
        "records": {
          "type": "array",
          "items": {
            "$ref": "#/components/schemas/1"
          },
          "description": "数据列表"
        }
      },
      "description": "数据"
    }
  },
  "description": "分页查询结果"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultPageResult](#schemaresultpageresult)|

## DELETE 批量删除操作日志

DELETE /admin/operationLog

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|ids|query|array[integer]| 是 |要删除的操作日志 ID 列表|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

# 管理端个人信息接口

## GET 获取个人信息

GET /admin/personalInfo

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "createTime": "string",
    "updateTime": "string",
    "id": 0,
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

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultPersonalInfo](#schemaresultpersonalinfo)|

## PUT 更新个人信息

PUT /admin/personalInfo

> Body 请求参数

```json
{
  "id": 0,
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

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[PersonalInfoDTO](#schemapersonalinfodto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

# 管理端系统配置接口

## GET 获取所有系统配置

GET /admin/systemConfig

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "createTime": "string",
      "updateTime": "string",
      "id": 0,
      "configKey": "string",
      "configValue": "string",
      "configType": "string",
      "description": "string"
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultListSystemConfig](#schemaresultlistsystemconfig)|

## POST 添加系统配置

POST /admin/systemConfig

> Body 请求参数

```json
{
  "id": 0,
  "configKey": "string",
  "configValue": "string",
  "configType": "string",
  "description": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[SystemConfigDTO](#schemasystemconfigdto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## PUT 更新系统配置

PUT /admin/systemConfig

> Body 请求参数

```json
{
  "id": 0,
  "configKey": "string",
  "configValue": "string",
  "configType": "string",
  "description": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[SystemConfigDTO](#schemasystemconfigdto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## DELETE 批量删除系统配置

DELETE /admin/systemConfig

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|ids|query|array[integer]| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## GET 根据配置键获取配置

GET /admin/systemConfig/key/{configKey}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|configKey|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "createTime": "string",
    "updateTime": "string",
    "id": 0,
    "configKey": "string",
    "configValue": "string",
    "configType": "string",
    "description": "string"
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultSystemConfig](#schemaresultsystemconfig)|

## GET 根据ID获取配置

GET /admin/systemConfig/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "createTime": "string",
    "updateTime": "string",
    "id": 0,
    "configKey": "string",
    "configValue": "string",
    "configType": "string",
    "description": "string"
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultSystemConfig](#schemaresultsystemconfig)|

# 管理端文章评论接口

## GET 分页条件查询评论（时间、是否审核）

GET /admin/article/comment/page

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|page|query|integer| 否 |页码|
|pageSize|query|integer| 否 |每页显示数量|
|articleId|query|integer(int64)| 否 |文章ID|
|isApproved|query|integer| 否 |是否审核通过，0-否，1-是|
|startTime|query|string| 否 |开始时间|
|endTime|query|string| 否 |结束时间|

> 返回示例

> 200 Response

```json
{
  "type": "object",
  "properties": {
    "code": {
      "type": "integer",
      "description": "编码：1成功，0和其它数字为失败"
    },
    "msg": {
      "type": "string",
      "description": "错误信息"
    },
    "data": {
      "type": "object",
      "properties": {
        "page": {
          "type": "integer",
          "description": "当前页码",
          "format": "int64"
        },
        "pageSize": {
          "type": "integer",
          "description": "每页显示数量",
          "format": "int64"
        },
        "total": {
          "type": "integer",
          "description": "总记录数",
          "format": "int64"
        },
        "totalPages": {
          "type": "integer",
          "description": "总页数",
          "format": "int64"
        },
        "records": {
          "type": "array",
          "items": {
            "$ref": "#/components/schemas/1"
          },
          "description": "数据列表"
        }
      },
      "description": "数据"
    }
  },
  "description": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultPageResult](#schemaresultpageresult)|

## GET 根据文章ID查询评论

GET /admin/article/comment/{articleId}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|articleId|path|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "createTime": "string",
      "updateTime": "string",
      "id": 0,
      "articleId": 0,
      "rootId": 0,
      "parentId": 0,
      "parentNickname": "string",
      "content": "string",
      "contentHtml": "string",
      "visitorId": 0,
      "nickname": "string",
      "emailOrQq": "string",
      "location": "string",
      "userAgentOs": "string",
      "userAgentBrowser": "string",
      "isApproved": 0,
      "isMarkdown": 0,
      "isSecret": 0,
      "isNotice": 0,
      "isEdited": 0,
      "isAdminReply": 0,
      "articleTitle": "string"
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultListArticleComments](#schemaresultlistarticlecomments)|

## PUT 批量审核通过评论

PUT /admin/article/comment/approve

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|ids|query|array[integer]| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": "string"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultString](#schemaresultstring)|

## DELETE 批量删除评论

DELETE /admin/article/comment

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|ids|query|array[integer]| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": "string"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultString](#schemaresultstring)|

## POST 管理员回复评论

POST /admin/article/comment/reply

> Body 请求参数

```json
{
  "articleId": 0,
  "parentId": 0,
  "rootId": 0,
  "parentNickname": "string",
  "content": "string",
  "isMarkdown": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[ArticleCommentReplyDTO](#schemaarticlecommentreplydto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": "string"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultString](#schemaresultstring)|

# 管理端文章分类接口

## GET 获取所有文章分类

GET /admin/articleCategory

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "createTime": "string",
      "updateTime": "string",
      "id": 0,
      "name": "string",
      "slug": "string",
      "description": "string",
      "sort": 0,
      "articleCount": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultListArticleCategories](#schemaresultlistarticlecategories)|

## POST 添加文章分类

POST /admin/articleCategory

> Body 请求参数

```json
{
  "id": 0,
  "name": "string",
  "slug": "string",
  "description": "string",
  "sort": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[ArticleCategoryDTO](#schemaarticlecategorydto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## PUT 更新文章分类

PUT /admin/articleCategory

> Body 请求参数

```json
{
  "id": 0,
  "name": "string",
  "slug": "string",
  "description": "string",
  "sort": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[ArticleCategoryDTO](#schemaarticlecategorydto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## DELETE 批量删除文章分类

DELETE /admin/articleCategory

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|ids|query|array[integer]| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

# 管理端 RSS 订阅接口

## GET 分页查询RSS订阅列表

GET /admin/rssSubscription/page

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|page|query|integer| 否 |页码|
|pageSize|query|integer| 否 |每页显示数量|
|email|query|string| 否 |邮箱|
|isActive|query|integer| 否 |是否激活，0-否，1-是|

> 返回示例

> 200 Response

```json
{
  "type": "object",
  "properties": {
    "code": {
      "type": "integer",
      "description": "编码：1成功，0和其它数字为失败"
    },
    "msg": {
      "type": "string",
      "description": "错误信息"
    },
    "data": {
      "type": "object",
      "properties": {
        "page": {
          "type": "integer",
          "description": "当前页码",
          "format": "int64"
        },
        "pageSize": {
          "type": "integer",
          "description": "每页显示数量",
          "format": "int64"
        },
        "total": {
          "type": "integer",
          "description": "总记录数",
          "format": "int64"
        },
        "totalPages": {
          "type": "integer",
          "description": "总页数",
          "format": "int64"
        },
        "records": {
          "type": "array",
          "items": {
            "$ref": "#/components/schemas/1"
          },
          "description": "数据列表"
        }
      },
      "description": "数据"
    }
  },
  "description": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultPageResult](#schemaresultpageresult)|

## GET 获取所有激活的订阅

GET /admin/rssSubscription

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "id": 0,
      "visitorId": 0,
      "nickname": "string",
      "email": "string",
      "isActive": 0,
      "subscribeTime": "string",
      "unSubscribeTime": "string"
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultListRssSubscriptions](#schemaresultlistrsssubscriptions)|

## PUT 更新RSS订阅

PUT /admin/rssSubscription

> Body 请求参数

```json
{
  "id": 0,
  "visitorId": 0,
  "nickname": "string",
  "email": "string",
  "isActive": 0,
  "subscribeTime": "string",
  "unSubscribeTime": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[RssSubscriptions](#schemarsssubscriptions)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## DELETE 批量删除RSS订阅

DELETE /admin/rssSubscription

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|ids|query|array[integer]| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## GET 根据ID查询RSS订阅

GET /admin/rssSubscription/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "id": 0,
    "visitorId": 0,
    "nickname": "string",
    "email": "string",
    "isActive": 0,
    "subscribeTime": "string",
    "unSubscribeTime": "string"
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultRssSubscriptions](#schemaresultrsssubscriptions)|

# 首页端访客接口

## POST 记录访客访问信息

POST /home/visitor/record

> Body 请求参数

```json
{
  "pagePath": "string",
  "pageTitle": "string",
  "referer": "string",
  "screen": "string",
  "timezone": "string",
  "language": "string",
  "platform": "string",
  "cookiesEnabled": true,
  "deviceMemory": 0,
  "hardwareConcurrency": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[VisitorRecordDTO](#schemavisitorrecorddto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "visitorFingerprint": "string",
    "sessionId": "string",
    "visitorId": 0,
    "isNewVisitor": true
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultVisitorRecordVO](#schemaresultvisitorrecordvo)|

# 首页端社交媒体接口

## GET 获取可见社交媒体信息

GET /home/socialMedia

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "id": 0,
      "name": "string",
      "icon": "string",
      "link": "string",
      "sort": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultListSocialMediaVO](#schemaresultlistsocialmediavo)|

# 首页端个人信息接口

## GET 获取个人信息

GET /home/personalInfo

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "id": 0,
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

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultPersonalInfoVO](#schemaresultpersonalinfovo)|

# SystemConfigController

## GET 根据配置键获取配置

GET /home/systemConfig/key/{configKey}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|configKey|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "createTime": "string",
    "updateTime": "string",
    "id": 0,
    "configKey": "string",
    "configValue": "string",
    "configType": "string",
    "description": "string"
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultSystemConfig](#schemaresultsystemconfig)|

## GET 根据配置键获取配置

GET /blog/systemConfig/key/{configKey}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|configKey|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "createTime": "string",
    "updateTime": "string",
    "id": 0,
    "configKey": "string",
    "configValue": "string",
    "configType": "string",
    "description": "string"
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultSystemConfig](#schemaresultsystemconfig)|

# 博客端音乐接口

## GET 获取所有可见的音乐

GET /blog/music

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "id": 0,
      "title": "string",
      "artist": "string",
      "duration": 0,
      "coverImage": "string",
      "musicUrl": "string",
      "lyricUrl": "string",
      "hasLyric": 0,
      "lyricType": "string"
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultListMusicVO](#schemaresultlistmusicvo)|

# 博客端公共接口

## GET 生成算术验证码

GET /blog/common/captcha/generate

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "captchaId": "string",
    "question": "string",
    "result": 0
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultCaptchaVO](#schemaresultcaptchavo)|

# 博客端统计相关接口

## GET 获取博客统计数据

GET /blog/report

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "viewTotalCount": 0,
    "viewTodayCount": 0,
    "visitorTotalCount": 0,
    "categoryTotalCount": 0,
    "tagTotalCount": 0,
    "articleTotalCount": 0
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultBlogReportVO](#schemaresultblogreportvo)|

# 博客端文章接口

## GET 获取已发布文章列表（分页）

GET /blog/article/page

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|page|query|integer| 是 |none|
|pageSize|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "page": 0,
    "pageSize": 0,
    "total": 0,
    "totalPages": 0,
    "records": [
      {
        "id": 0,
        "title": "string",
        "slug": "string",
        "summary": "string",
        "coverImage": "string",
        "categoryId": 0,
        "categoryName": "string",
        "viewCount": 0,
        "likeCount": 0,
        "commentCount": 0,
        "wordCount": 0,
        "readingTime": 0,
        "isTop": 0,
        "publishTime": "string"
      }
    ]
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultPageResultBlogArticleVO](#schemaresultpageresultblogarticlevo)|

## GET 根据slug获取文章详情（浏览量+1）

GET /blog/article/detail/{slug}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|slug|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "id": 0,
    "title": "string",
    "slug": "string",
    "summary": "string",
    "coverImage": "string",
    "contentHtml": "string",
    "contentMarkdown": "string",
    "categoryId": 0,
    "categoryName": "string",
    "viewCount": 0,
    "likeCount": 0,
    "commentCount": 0,
    "wordCount": 0,
    "readingTime": 0,
    "publishTime": "string",
    "updateTime": "string",
    "tagNames": [
      "string"
    ],
    "prevArticle": {
      "id": 0,
      "title": "string",
      "slug": "string",
      "summary": "string",
      "coverImage": "string",
      "categoryId": 0,
      "categoryName": "string",
      "viewCount": 0,
      "likeCount": 0,
      "commentCount": 0,
      "wordCount": 0,
      "readingTime": 0,
      "isTop": 0,
      "publishTime": "string"
    },
    "nextArticle": {
      "id": 0,
      "title": "string",
      "slug": "string",
      "summary": "string",
      "coverImage": "string",
      "categoryId": 0,
      "categoryName": "string",
      "viewCount": 0,
      "likeCount": 0,
      "commentCount": 0,
      "wordCount": 0,
      "readingTime": 0,
      "isTop": 0,
      "publishTime": "string"
    },
    "relatedArticles": [
      {
        "id": 0,
        "title": "string",
        "slug": "string",
        "summary": "string",
        "coverImage": "string",
        "categoryId": 0,
        "categoryName": "string",
        "viewCount": 0,
        "likeCount": 0,
        "commentCount": 0,
        "wordCount": 0,
        "readingTime": 0,
        "isTop": 0,
        "publishTime": "string"
      }
    ]
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultBlogArticleDetailVO](#schemaresultblogarticledetailvo)|

## GET 根据分类ID获取文章列表（分页）

GET /blog/article/category/{categoryId}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|categoryId|path|integer| 是 |none|
|page|query|integer| 是 |none|
|pageSize|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "page": 0,
    "pageSize": 0,
    "total": 0,
    "totalPages": 0,
    "records": [
      {
        "id": 0,
        "title": "string",
        "slug": "string",
        "summary": "string",
        "coverImage": "string",
        "categoryId": 0,
        "categoryName": "string",
        "viewCount": 0,
        "likeCount": 0,
        "commentCount": 0,
        "wordCount": 0,
        "readingTime": 0,
        "isTop": 0,
        "publishTime": "string"
      }
    ]
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultPageResultBlogArticleVO](#schemaresultpageresultblogarticlevo)|

## GET 获取文章归档（按年月分组）

GET /blog/article/archive

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "year": 0,
      "month": 0,
      "articles": [
        {
          "id": 0,
          "title": "string",
          "slug": "string",
          "publishDay": 0,
          "publishTime": "string"
        }
      ]
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultListArticleArchiveVO](#schemaresultlistarticlearchivevo)|

## GET 文章搜索（仅已发布）

GET /blog/article/search

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|keyword|query|string| 是 |none|
|page|query|integer| 是 |none|
|pageSize|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "page": 0,
    "pageSize": 0,
    "total": 0,
    "totalPages": 0,
    "records": [
      {
        "id": 0,
        "title": "string",
        "slug": "string",
        "summary": "string",
        "coverImage": "string",
        "categoryId": 0,
        "categoryName": "string",
        "viewCount": 0,
        "likeCount": 0,
        "commentCount": 0,
        "wordCount": 0,
        "readingTime": 0,
        "isTop": 0,
        "publishTime": "string"
      }
    ]
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultPageResultBlogArticleVO](#schemaresultpageresultblogarticlevo)|

# 博客端留言接口

## POST 访客提交留言

POST /blog/message

> Body 请求参数

```json
{
  "content": "string",
  "rootId": 0,
  "parentId": 0,
  "parentNickname": "string",
  "visitorId": 0,
  "nickname": "string",
  "emailOrQq": "string",
  "isMarkdown": 0,
  "isSecret": 0,
  "isNotice": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[MessageDTO](#schemamessagedto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": "string"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultString](#schemaresultstring)|

## GET 获取留言列表（树形结构，含当前访客的未审核留言）

GET /blog/message

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|visitorId|query|integer| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "id": 0,
      "rootId": 0,
      "parentId": 0,
      "parentNickname": "string",
      "content": "string",
      "contentHtml": "string",
      "isMarkdown": 0,
      "visitorId": 0,
      "nickname": "string",
      "emailOrQq": "string",
      "location": "string",
      "userAgentOs": "string",
      "userAgentBrowser": "string",
      "isApproved": 0,
      "isSecret": 0,
      "isAdminReply": 0,
      "createTime": "string",
      "children": [
        {
          "id": 0,
          "rootId": 0,
          "parentId": 0,
          "parentNickname": "string",
          "content": "string",
          "contentHtml": "string",
          "isMarkdown": 0,
          "visitorId": 0,
          "nickname": "string",
          "emailOrQq": "string",
          "location": "string",
          "userAgentOs": "string",
          "userAgentBrowser": "string",
          "isApproved": 0,
          "isSecret": 0,
          "isAdminReply": 0,
          "createTime": "string",
          "children": [
            {}
          ]
        }
      ]
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultListMessageVO](#schemaresultlistmessagevo)|

## PUT 访客编辑留言

PUT /blog/message/edit

> Body 请求参数

```json
{
  "id": 0,
  "visitorId": 0,
  "content": "string",
  "isMarkdown": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[MessageEditDTO](#schemamessageeditdto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": "string"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultString](#schemaresultstring)|

## DELETE 访客删除留言

DELETE /blog/message/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer| 是 |none|
|visitorId|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": "string"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultString](#schemaresultstring)|

# 博客端 RSS Feed 接口

## GET 生成 RSS 2.0 Feed XML

GET /blog/rss

> 返回示例

> 200 Response

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|string|

# 博客端 Sitemap 接口

## GET 动态生成站点地图 XML

GET /blog/sitemap.xml

> 返回示例

> 200 Response

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|string|

# 博客端访客接口

## POST 记录访客访问信息

POST /blog/visitor/record

> Body 请求参数

```json
{
  "pagePath": "string",
  "pageTitle": "string",
  "referer": "string",
  "screen": "string",
  "timezone": "string",
  "language": "string",
  "platform": "string",
  "cookiesEnabled": true,
  "deviceMemory": 0,
  "hardwareConcurrency": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[VisitorRecordDTO](#schemavisitorrecorddto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "visitorFingerprint": "string",
    "sessionId": "string",
    "visitorId": 0,
    "isNewVisitor": true
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultVisitorRecordVO](#schemaresultvisitorrecordvo)|

# 博客端文章标签接口

## GET 获取有已发布文章的标签列表

GET /blog/article/tag

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "createTime": "string",
      "updateTime": "string",
      "id": 0,
      "name": "string",
      "slug": "string",
      "articleCount": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultListArticleTags](#schemaresultlistarticletags)|

## GET 根据标签ID获取已发布文章列表

GET /blog/article/tag/{tagId}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|tagId|path|integer| 是 |none|
|page|query|integer| 是 |none|
|pageSize|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "type": "object",
  "properties": {
    "code": {
      "type": "integer",
      "description": "编码：1成功，0和其它数字为失败"
    },
    "msg": {
      "type": "string",
      "description": "错误信息"
    },
    "data": {
      "type": "object",
      "properties": {
        "page": {
          "type": "integer",
          "description": "当前页码",
          "format": "int64"
        },
        "pageSize": {
          "type": "integer",
          "description": "每页显示数量",
          "format": "int64"
        },
        "total": {
          "type": "integer",
          "description": "总记录数",
          "format": "int64"
        },
        "totalPages": {
          "type": "integer",
          "description": "总页数",
          "format": "int64"
        },
        "records": {
          "type": "array",
          "items": {
            "$ref": "#/components/schemas/1"
          },
          "description": "数据列表"
        }
      },
      "description": "数据"
    }
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultPageResult](#schemaresultpageresult)|

# 博客端友链接口

## GET 获取可见友情链接

GET /blog/friendLink

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "id": 0,
      "name": "string",
      "url": "string",
      "avatarUrl": "string",
      "description": "string",
      "sort": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultListFriendLinkVO](#schemaresultlistfriendlinkvo)|

# 博客端文章点赞接口

## POST 点赞文章

POST /blog/articleLike/{articleId}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|articleId|path|integer| 是 |none|
|visitorId|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": "string"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultString](#schemaresultstring)|

## DELETE 取消点赞

DELETE /blog/articleLike/{articleId}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|articleId|path|integer| 是 |none|
|visitorId|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": "string"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultString](#schemaresultstring)|

## GET 检查是否已点赞

GET /blog/articleLike/{articleId}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|articleId|path|integer| 是 |none|
|visitorId|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": true
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultBoolean](#schemaresultboolean)|

# 博客端个人信息接口

## GET 获取个人信息

GET /blog/personalInfo

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "id": 0,
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

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultPersonalInfoVO](#schemaresultpersonalinfovo)|

# 博客端文章评论接口

## GET 根据文章ID获取评论列表（树形结构，含当前访客的未审核评论）

GET /blog/articleComment/article/{articleId}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|articleId|path|integer| 是 |none|
|visitorId|query|integer| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "id": 0,
      "articleId": 0,
      "rootId": 0,
      "parentId": 0,
      "parentNickname": "string",
      "content": "string",
      "contentHtml": "string",
      "isMarkdown": 0,
      "visitorId": 0,
      "nickname": "string",
      "emailOrQq": "string",
      "location": "string",
      "userAgentOs": "string",
      "userAgentBrowser": "string",
      "isApproved": 0,
      "isSecret": 0,
      "isAdminReply": 0,
      "createTime": "string",
      "children": [
        {
          "id": 0,
          "articleId": 0,
          "rootId": 0,
          "parentId": 0,
          "parentNickname": "string",
          "content": "string",
          "contentHtml": "string",
          "isMarkdown": 0,
          "visitorId": 0,
          "nickname": "string",
          "emailOrQq": "string",
          "location": "string",
          "userAgentOs": "string",
          "userAgentBrowser": "string",
          "isApproved": 0,
          "isSecret": 0,
          "isAdminReply": 0,
          "createTime": "string",
          "children": [
            {}
          ]
        }
      ]
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultListArticleCommentVO](#schemaresultlistarticlecommentvo)|

## POST 提交评论（添加评论/回复评论）

POST /blog/articleComment

> Body 请求参数

```json
{
  "articleId": 0,
  "rootId": 0,
  "parentId": 0,
  "parentNickname": "string",
  "content": "string",
  "visitorId": 0,
  "nickname": "string",
  "emailOrQq": "string",
  "isMarkdown": 0,
  "isSecret": 0,
  "isNotice": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[ArticleCommentDTO](#schemaarticlecommentdto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": "string"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultString](#schemaresultstring)|

## PUT 访客编辑评论

PUT /blog/articleComment/edit

> Body 请求参数

```json
{
  "id": 0,
  "visitorId": 0,
  "content": "string",
  "isMarkdown": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[ArticleCommentEditDTO](#schemaarticlecommenteditdto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": "string"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultString](#schemaresultstring)|

## DELETE 访客删除评论

DELETE /blog/articleComment/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer| 是 |none|
|visitorId|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": "string"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultString](#schemaresultstring)|

# 博客端文章分类接口

## GET 获取所有可见文章分类（有已发布文章的分类）

GET /blog/articleCategory

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "createTime": "string",
      "updateTime": "string",
      "id": 0,
      "name": "string",
      "slug": "string",
      "description": "string",
      "sort": 0,
      "articleCount": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultListArticleCategories](#schemaresultlistarticlecategories)|

# 博客端 RSS 订阅接口

## POST 添加RSS订阅

POST /blog/rssSubscription

> Body 请求参数

```json
{
  "visitorId": 0,
  "nickname": "string",
  "email": "user@example.com"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[RssSubscriptionDTO](#schemarsssubscriptiondto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## PUT 取消RSS订阅（访客端）

PUT /blog/rssSubscription/unsubscribe

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|email|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[Result](#schemaresult)|

## GET 检查访客订阅状态（返回订阅详情）

GET /blog/rssSubscription/check

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|visitorId|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "subscribed": true,
    "nickname": "string",
    "email": "string"
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultRssSubscriptionStatusVO](#schemaresultrsssubscriptionstatusvo)|

# 简历端技能接口

## GET 获取技能信息

GET /cv/skill

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "id": 0,
      "name": "string",
      "description": "string",
      "icon": "string",
      "sort": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultListSkillVO](#schemaresultlistskillvo)|

# 简历端访客接口

## POST 记录访客访问信息

POST /cv/visitor/record

> Body 请求参数

```json
{
  "pagePath": "string",
  "pageTitle": "string",
  "referer": "string",
  "screen": "string",
  "timezone": "string",
  "language": "string",
  "platform": "string",
  "cookiesEnabled": true,
  "deviceMemory": 0,
  "hardwareConcurrency": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[VisitorRecordDTO](#schemavisitorrecorddto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "visitorFingerprint": "string",
    "sessionId": "string",
    "visitorId": 0,
    "isNewVisitor": true
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultVisitorRecordVO](#schemaresultvisitorrecordvo)|

# 简历端经历接口

## GET 获取全部经历信息

GET /cv/experience

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "id": 0,
      "type": 0,
      "title": "string",
      "subtitle": "string",
      "logoUrl": "string",
      "content": "string",
      "startDate": "string",
      "endDate": "string"
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultListExperienceVO](#schemaresultlistexperiencevo)|

# 简历端个人信息接口

## GET 获取个人信息

GET /cv/personalInfo

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "id": 0,
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

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResultPersonalInfoVO](#schemaresultpersonalinfovo)|

# 数据模型

<h2 id="tocS_ResultString">ResultString</h2>

<a id="schemaresultstring"></a>
<a id="schema_ResultString"></a>
<a id="tocSresultstring"></a>
<a id="tocsresultstring"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|string|false|none||数据|

<h2 id="tocS_"></h2>

<a id="schema"></a>
<a id="schema_"></a>
<a id="tocS"></a>
<a id="tocs"></a>

```json
{}

```

### 属性

*None*

<h2 id="tocS_PageResult">PageResult</h2>

<a id="schemapageresult"></a>
<a id="schema_PageResult"></a>
<a id="tocSpageresult"></a>
<a id="tocspageresult"></a>

```json
{
  "type": "object",
  "properties": {
    "page": {
      "type": "integer",
      "description": "当前页码",
      "format": "int64"
    },
    "pageSize": {
      "type": "integer",
      "description": "每页显示数量",
      "format": "int64"
    },
    "total": {
      "type": "integer",
      "description": "总记录数",
      "format": "int64"
    },
    "totalPages": {
      "type": "integer",
      "description": "总页数",
      "format": "int64"
    },
    "records": {
      "type": "array",
      "items": {
        "$ref": "#/components/schemas/1"
      },
      "description": "数据列表"
    }
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|page|integer(int64)|false|none||当前页码|
|pageSize|integer(int64)|false|none||每页显示数量|
|total|integer(int64)|false|none||总记录数|
|totalPages|integer(int64)|false|none||总页数|
|records|[[1](#schema1)]|false|none||数据列表|

<h2 id="tocS_ResultPageResult">ResultPageResult</h2>

<a id="schemaresultpageresult"></a>
<a id="schema_ResultPageResult"></a>
<a id="tocSresultpageresult"></a>
<a id="tocsresultpageresult"></a>

```json
{
  "type": "object",
  "properties": {
    "code": {
      "type": "integer",
      "description": "编码：1成功，0和其它数字为失败"
    },
    "msg": {
      "type": "string",
      "description": "错误信息"
    },
    "data": {
      "type": "object",
      "properties": {
        "page": {
          "type": "integer",
          "description": "当前页码",
          "format": "int64"
        },
        "pageSize": {
          "type": "integer",
          "description": "每页显示数量",
          "format": "int64"
        },
        "total": {
          "type": "integer",
          "description": "总记录数",
          "format": "int64"
        },
        "totalPages": {
          "type": "integer",
          "description": "总页数",
          "format": "int64"
        },
        "records": {
          "type": "array",
          "items": {
            "$ref": "#/components/schemas/1"
          },
          "description": "数据列表"
        }
      },
      "description": "数据"
    }
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[PageResult](#schemapageresult)|false|none||数据|

<h2 id="tocS_Result">Result</h2>

<a id="schemaresult"></a>
<a id="schema_Result"></a>
<a id="tocSresult"></a>
<a id="tocsresult"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|object|false|none||数据|

<h2 id="tocS_SendCodeDTO">SendCodeDTO</h2>

<a id="schemasendcodedto"></a>
<a id="schema_SendCodeDTO"></a>
<a id="tocSsendcodedto"></a>
<a id="tocssendcodedto"></a>

```json
{
  "username": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|username|string|false|none||none|

<h2 id="tocS_AdminLoginVO">AdminLoginVO</h2>

<a id="schemaadminloginvo"></a>
<a id="schema_AdminLoginVO"></a>
<a id="tocSadminloginvo"></a>
<a id="tocsadminloginvo"></a>

```json
{
  "id": 0,
  "token": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|token|string|false|none||none|

<h2 id="tocS_ResultAdminLoginVO">ResultAdminLoginVO</h2>

<a id="schemaresultadminloginvo"></a>
<a id="schema_ResultAdminLoginVO"></a>
<a id="tocSresultadminloginvo"></a>
<a id="tocsresultadminloginvo"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "id": 0,
    "token": "string"
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[AdminLoginVO](#schemaadminloginvo)|false|none||数据|

<h2 id="tocS_AdminLoginDTO">AdminLoginDTO</h2>

<a id="schemaadminlogindto"></a>
<a id="schema_AdminLoginDTO"></a>
<a id="tocSadminlogindto"></a>
<a id="tocsadminlogindto"></a>

```json
{
  "username": "string",
  "password": "string",
  "code": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|username|string|true|none||none|
|password|string|true|none||none|
|code|string|true|none||none|

<h2 id="tocS_AdminVO">AdminVO</h2>

<a id="schemaadminvo"></a>
<a id="schema_AdminVO"></a>
<a id="tocSadminvo"></a>
<a id="tocsadminvo"></a>

```json
{
  "id": 0,
  "nickname": "string",
  "email": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|nickname|string|false|none||none|
|email|string|false|none||none|

<h2 id="tocS_ResultAdminVO">ResultAdminVO</h2>

<a id="schemaresultadminvo"></a>
<a id="schema_ResultAdminVO"></a>
<a id="tocSresultadminvo"></a>
<a id="tocsresultadminvo"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "id": 0,
    "nickname": "string",
    "email": "string"
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[AdminVO](#schemaadminvo)|false|none||数据|

<h2 id="tocS_AdminLogoutDTO">AdminLogoutDTO</h2>

<a id="schemaadminlogoutdto"></a>
<a id="schema_AdminLogoutDTO"></a>
<a id="tocSadminlogoutdto"></a>
<a id="tocsadminlogoutdto"></a>

```json
{
  "id": 0,
  "token": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|token|string|false|none||none|

<h2 id="tocS_AdminChangePasswordDTO">AdminChangePasswordDTO</h2>

<a id="schemaadminchangepassworddto"></a>
<a id="schema_AdminChangePasswordDTO"></a>
<a id="tocSadminchangepassworddto"></a>
<a id="tocsadminchangepassworddto"></a>

```json
{
  "oldPassword": "string",
  "newPassword": "string",
  "confirmNewPassword": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|oldPassword|string|true|none||none|
|newPassword|string|true|none||none|
|confirmNewPassword|string|true|none||none|

<h2 id="tocS_AdminChangeNicknameDTO">AdminChangeNicknameDTO</h2>

<a id="schemaadminchangenicknamedto"></a>
<a id="schema_AdminChangeNicknameDTO"></a>
<a id="tocSadminchangenicknamedto"></a>
<a id="tocsadminchangenicknamedto"></a>

```json
{
  "nickname": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|nickname|string|true|none||none|

<h2 id="tocS_AdminChangeEmailDTO">AdminChangeEmailDTO</h2>

<a id="schemaadminchangeemaildto"></a>
<a id="schema_AdminChangeEmailDTO"></a>
<a id="tocSadminchangeemaildto"></a>
<a id="tocsadminchangeemaildto"></a>

```json
{
  "email": "user@example.com",
  "code": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|email|string(email)|true|none||none|
|code|string|true|none||none|

<h2 id="tocS_Music">Music</h2>

<a id="schemamusic"></a>
<a id="schema_Music"></a>
<a id="tocSmusic"></a>
<a id="tocsmusic"></a>

```json
{
  "createTime": "string",
  "updateTime": "string",
  "id": 0,
  "title": "string",
  "artist": "string",
  "duration": 0,
  "coverImage": "string",
  "musicUrl": "string",
  "lyricUrl": "string",
  "hasLyric": 0,
  "lyricType": "string",
  "sort": 0,
  "isVisible": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|createTime|string|false|none||创建时间|
|updateTime|string|false|none||更新时间|
|id|integer(int64)|false|none||none|
|title|string|false|none||音乐标题|
|artist|string|false|none||作者|
|duration|integer|false|none||时长，单位：秒|
|coverImage|string|false|none||封面图片 url|
|musicUrl|string|false|none||音频文件 url|
|lyricUrl|string|false|none||歌词文件 url|
|hasLyric|integer|false|none||是否有歌词，0-否，1-是|
|lyricType|string|false|none||歌词类型,lrc,json,txt|
|sort|integer|false|none||排序，越小越靠前|
|isVisible|integer|false|none||是否可见|

<h2 id="tocS_ResultMusic">ResultMusic</h2>

<a id="schemaresultmusic"></a>
<a id="schema_ResultMusic"></a>
<a id="tocSresultmusic"></a>
<a id="tocsresultmusic"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "createTime": "string",
    "updateTime": "string",
    "id": 0,
    "title": "string",
    "artist": "string",
    "duration": 0,
    "coverImage": "string",
    "musicUrl": "string",
    "lyricUrl": "string",
    "hasLyric": 0,
    "lyricType": "string",
    "sort": 0,
    "isVisible": 0
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[Music](#schemamusic)|false|none||数据|

<h2 id="tocS_MusicDTO">MusicDTO</h2>

<a id="schemamusicdto"></a>
<a id="schema_MusicDTO"></a>
<a id="tocSmusicdto"></a>
<a id="tocsmusicdto"></a>

```json
{
  "id": 0,
  "title": "string",
  "artist": "string",
  "duration": 0,
  "coverImage": "string",
  "musicUrl": "string",
  "lyricUrl": "string",
  "hasLyric": 0,
  "lyricType": "string",
  "sort": 0,
  "isVisible": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|title|string|true|none||音乐标题|
|artist|string|false|none||作者|
|duration|integer|false|none||时长，单位：秒|
|coverImage|string|false|none||封面图片url|
|musicUrl|string|true|none||音频文件url|
|lyricUrl|string|false|none||歌词文件url|
|hasLyric|integer|false|none||是否有歌词，0-否，1-是|
|lyricType|string|false|none||歌词类型,lrc,json,txt|
|sort|integer|false|none||排序，越小越靠前|
|isVisible|integer|false|none||是否可见|

<h2 id="tocS_Skills">Skills</h2>

<a id="schemaskills"></a>
<a id="schema_Skills"></a>
<a id="tocSskills"></a>
<a id="tocsskills"></a>

```json
{
  "createTime": "string",
  "updateTime": "string",
  "id": 0,
  "name": "string",
  "description": "string",
  "icon": "string",
  "sort": 0,
  "isVisible": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|createTime|string|false|none||创建时间|
|updateTime|string|false|none||更新时间|
|id|integer(int64)|false|none||none|
|name|string|false|none||技能名称|
|description|string|false|none||技能描述|
|icon|string|false|none||图标 url|
|sort|integer|false|none||排序，越小越靠前|
|isVisible|integer|false|none||是否可见|

<h2 id="tocS_ResultListSkills">ResultListSkills</h2>

<a id="schemaresultlistskills"></a>
<a id="schema_ResultListSkills"></a>
<a id="tocSresultlistskills"></a>
<a id="tocsresultlistskills"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "createTime": "string",
      "updateTime": "string",
      "id": 0,
      "name": "string",
      "description": "string",
      "icon": "string",
      "sort": 0,
      "isVisible": 0
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[[Skills](#schemaskills)]|false|none||数据|

<h2 id="tocS_SkillDTO">SkillDTO</h2>

<a id="schemaskilldto"></a>
<a id="schema_SkillDTO"></a>
<a id="tocSskilldto"></a>
<a id="tocsskilldto"></a>

```json
{
  "id": 0,
  "name": "string",
  "description": "string",
  "icon": "string",
  "sort": 0,
  "isVisible": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|name|string|true|none||技能名称|
|description|string|false|none||技能描述|
|icon|string|false|none||图标url|
|sort|integer|false|none||排序，越小越靠前|
|isVisible|integer|false|none||是否可见|

<h2 id="tocS_ViewReportVO">ViewReportVO</h2>

<a id="schemaviewreportvo"></a>
<a id="schema_ViewReportVO"></a>
<a id="tocSviewreportvo"></a>
<a id="tocsviewreportvo"></a>

```json
{
  "dateList": "string",
  "viewCountList": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|dateList|string|false|none||日期，以逗号分隔，例如：2025-01-01,2025-01-02|
|viewCountList|string|false|none||浏览量，以逗号分隔，例如：120,350,200|

<h2 id="tocS_ResultViewReportVO">ResultViewReportVO</h2>

<a id="schemaresultviewreportvo"></a>
<a id="schema_ResultViewReportVO"></a>
<a id="tocSresultviewreportvo"></a>
<a id="tocsresultviewreportvo"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "dateList": "string",
    "viewCountList": "string"
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[ViewReportVO](#schemaviewreportvo)|false|none||数据|

<h2 id="tocS_VisitorReportVO">VisitorReportVO</h2>

<a id="schemavisitorreportvo"></a>
<a id="schema_VisitorReportVO"></a>
<a id="tocSvisitorreportvo"></a>
<a id="tocsvisitorreportvo"></a>

```json
{
  "dateList": "string",
  "newVisitorCountList": "string",
  "totalVisitorCountList": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|dateList|string|false|none||日期，以逗号分隔，例如：2025-01-01,2025-01-02|
|newVisitorCountList|string|false|none||新增访客数，以逗号分隔，例如：5,12,8|
|totalVisitorCountList|string|false|none||累计访客数，以逗号分隔，例如：5,17,25|

<h2 id="tocS_ResultVisitorReportVO">ResultVisitorReportVO</h2>

<a id="schemaresultvisitorreportvo"></a>
<a id="schema_ResultVisitorReportVO"></a>
<a id="tocSresultvisitorreportvo"></a>
<a id="tocsresultvisitorreportvo"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "dateList": "string",
    "newVisitorCountList": "string",
    "totalVisitorCountList": "string"
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[VisitorReportVO](#schemavisitorreportvo)|false|none||数据|

<h2 id="tocS_ProvinceVisitorVO">ProvinceVisitorVO</h2>

<a id="schemaprovincevisitorvo"></a>
<a id="schema_ProvinceVisitorVO"></a>
<a id="tocSprovincevisitorvo"></a>
<a id="tocsprovincevisitorvo"></a>

```json
{
  "provinceList": "string",
  "countList": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|provinceList|string|false|none||省份，以逗号分隔，例如：广东,北京,浙江|
|countList|string|false|none||对应省份的访客数，以逗号分隔，例如：120,85,60|

<h2 id="tocS_ResultProvinceVisitorVO">ResultProvinceVisitorVO</h2>

<a id="schemaresultprovincevisitorvo"></a>
<a id="schema_ResultProvinceVisitorVO"></a>
<a id="tocSresultprovincevisitorvo"></a>
<a id="tocsresultprovincevisitorvo"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "provinceList": "string",
    "countList": "string"
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[ProvinceVisitorVO](#schemaprovincevisitorvo)|false|none||数据|

<h2 id="tocS_ArticleViewTop10VO">ArticleViewTop10VO</h2>

<a id="schemaarticleviewtop10vo"></a>
<a id="schema_ArticleViewTop10VO"></a>
<a id="tocSarticleviewtop10vo"></a>
<a id="tocsarticleviewtop10vo"></a>

```json
{
  "titleList": [
    "string"
  ],
  "viewCountList": [
    0
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|titleList|[string]|false|none||文章标题列表|
|viewCountList|[integer]|false|none||对应文章的浏览量列表|

<h2 id="tocS_ResultArticleViewTop10VO">ResultArticleViewTop10VO</h2>

<a id="schemaresultarticleviewtop10vo"></a>
<a id="schema_ResultArticleViewTop10VO"></a>
<a id="tocSresultarticleviewtop10vo"></a>
<a id="tocsresultarticleviewtop10vo"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "titleList": [
      "string"
    ],
    "viewCountList": [
      0
    ]
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[ArticleViewTop10VO](#schemaarticleviewtop10vo)|false|none||数据|

<h2 id="tocS_AdminOverviewVO">AdminOverviewVO</h2>

<a id="schemaadminoverviewvo"></a>
<a id="schema_AdminOverviewVO"></a>
<a id="tocSadminoverviewvo"></a>
<a id="tocsadminoverviewvo"></a>

```json
{
  "totalViewCount": 0,
  "totalVisitorCount": 0,
  "todayViewCount": 0,
  "todayNewVisitorCount": 0,
  "totalArticleCount": 0,
  "totalCommentCount": 0,
  "totalMessageCount": 0,
  "pendingCommentCount": 0,
  "pendingMessageCount": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|totalViewCount|integer|false|none||总浏览量|
|totalVisitorCount|integer|false|none||总访客数|
|todayViewCount|integer|false|none||今日浏览量|
|todayNewVisitorCount|integer|false|none||今日新增访客数|
|totalArticleCount|integer|false|none||总文章数|
|totalCommentCount|integer|false|none||总评论数|
|totalMessageCount|integer|false|none||总留言数|
|pendingCommentCount|integer|false|none||待审核评论数|
|pendingMessageCount|integer|false|none||待审核留言数|

<h2 id="tocS_ResultAdminOverviewVO">ResultAdminOverviewVO</h2>

<a id="schemaresultadminoverviewvo"></a>
<a id="schema_ResultAdminOverviewVO"></a>
<a id="tocSresultadminoverviewvo"></a>
<a id="tocsresultadminoverviewvo"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "totalViewCount": 0,
    "totalVisitorCount": 0,
    "todayViewCount": 0,
    "todayNewVisitorCount": 0,
    "totalArticleCount": 0,
    "totalCommentCount": 0,
    "totalMessageCount": 0,
    "pendingCommentCount": 0,
    "pendingMessageCount": 0
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[AdminOverviewVO](#schemaadminoverviewvo)|false|none||数据|

<h2 id="tocS_Articles">Articles</h2>

<a id="schemaarticles"></a>
<a id="schema_Articles"></a>
<a id="tocSarticles"></a>
<a id="tocsarticles"></a>

```json
{
  "createTime": "string",
  "updateTime": "string",
  "id": 0,
  "title": "string",
  "slug": "string",
  "summary": "string",
  "coverImage": "string",
  "contentMarkdown": "string",
  "contentHtml": "string",
  "categoryId": 0,
  "viewCount": 0,
  "likeCount": 0,
  "commentCount": 0,
  "wordCount": 0,
  "readingTime": 0,
  "isPublished": 0,
  "isTop": 0,
  "publishTime": "string",
  "publishYear": 0,
  "publishMonth": 0,
  "publishDay": 0,
  "publishDate": "string",
  "tagIds": [
    0
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|createTime|string|false|none||创建时间|
|updateTime|string|false|none||更新时间|
|id|integer(int64)|false|none||none|
|title|string|false|none||文章标题|
|slug|string|false|none||URL 标识|
|summary|string|false|none||文章摘要|
|coverImage|string|false|none||封面图片 url|
|contentMarkdown|string|false|none||Markdown 内容|
|contentHtml|string|false|none||转换后的 HTML 内容|
|categoryId|integer(int64)|false|none||分类 ID|
|viewCount|integer(int64)|false|none||浏览次数|
|likeCount|integer(int64)|false|none||点赞次数|
|commentCount|integer(int64)|false|none||评论数|
|wordCount|integer(int64)|false|none||字数统计|
|readingTime|integer(int64)|false|none||预计阅读时间，单位：分钟|
|isPublished|integer|false|none||是否发布，0-否，1-是|
|isTop|integer|false|none||是否置顶，0-否，1-是|
|publishTime|string|false|none||发布时间|
|publishYear|integer|false|none||发布年份（数据库生成列，不可更新）|
|publishMonth|integer|false|none||发布月份（数据库生成列，不可更新）|
|publishDay|integer|false|none||发布日期（数据库生成列，不可更新）|
|publishDate|string|false|none||发布日期（去掉时间，数据库生成列，不可更新）|
|tagIds|[integer]|false|none||标签 ID 列表（非数据库字段，管理端返回时填充）|

<h2 id="tocS_ResultArticles">ResultArticles</h2>

<a id="schemaresultarticles"></a>
<a id="schema_ResultArticles"></a>
<a id="tocSresultarticles"></a>
<a id="tocsresultarticles"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "createTime": "string",
    "updateTime": "string",
    "id": 0,
    "title": "string",
    "slug": "string",
    "summary": "string",
    "coverImage": "string",
    "contentMarkdown": "string",
    "contentHtml": "string",
    "categoryId": 0,
    "viewCount": 0,
    "likeCount": 0,
    "commentCount": 0,
    "wordCount": 0,
    "readingTime": 0,
    "isPublished": 0,
    "isTop": 0,
    "publishTime": "string",
    "publishYear": 0,
    "publishMonth": 0,
    "publishDay": 0,
    "publishDate": "string",
    "tagIds": [
      0
    ]
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[Articles](#schemaarticles)|false|none||数据|

<h2 id="tocS_ArticleDTO">ArticleDTO</h2>

<a id="schemaarticledto"></a>
<a id="schema_ArticleDTO"></a>
<a id="tocSarticledto"></a>
<a id="tocsarticledto"></a>

```json
{
  "id": 0,
  "title": "string",
  "slug": "string",
  "summary": "string",
  "coverImage": "string",
  "contentMarkdown": "string",
  "contentHtml": "string",
  "categoryId": 0,
  "isPublished": 0,
  "isTop": 0,
  "tagIds": [
    0
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||文章ID（更新时使用）|
|title|string|true|none||文章标题|
|slug|string|true|none||URL标识|
|summary|string|false|none||文章摘要|
|coverImage|string|false|none||封面图片url|
|contentMarkdown|string|true|none||Markdown内容|
|contentHtml|string|false|none||前端编辑器渲染的HTML内容（可选，若提供则直接使用，不再后端转换）|
|categoryId|integer(int64)|true|none||分类ID|
|isPublished|integer|false|none||是否发布,0-否（草稿），1-是|
|isTop|integer|false|none||是否置顶,0-否，1-是|
|tagIds|[integer]|false|none||标签ID列表|

<h2 id="tocS_MessageReplyDTO">MessageReplyDTO</h2>

<a id="schemamessagereplydto"></a>
<a id="schema_MessageReplyDTO"></a>
<a id="tocSmessagereplydto"></a>
<a id="tocsmessagereplydto"></a>

```json
{
  "parentId": 0,
  "rootId": 0,
  "parentNickname": "string",
  "content": "string",
  "isMarkdown": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|parentId|integer(int64)|true|none||父留言ID|
|rootId|integer(int64)|false|none||根留言ID|
|parentNickname|string|false|none||父留言昵称|
|content|string|true|none||回复内容|
|isMarkdown|integer|false|none||是否使用markdown，0-否，1-是|

<h2 id="tocS_ArticleTags">ArticleTags</h2>

<a id="schemaarticletags"></a>
<a id="schema_ArticleTags"></a>
<a id="tocSarticletags"></a>
<a id="tocsarticletags"></a>

```json
{
  "createTime": "string",
  "updateTime": "string",
  "id": 0,
  "name": "string",
  "slug": "string",
  "articleCount": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|createTime|string|false|none||创建时间|
|updateTime|string|false|none||更新时间|
|id|integer(int64)|false|none||none|
|name|string|false|none||标签名称|
|slug|string|false|none||URL 标识|
|articleCount|integer|false|none||文章数量（非数据库字段，查询时计算）|

<h2 id="tocS_ResultListArticleTags">ResultListArticleTags</h2>

<a id="schemaresultlistarticletags"></a>
<a id="schema_ResultListArticleTags"></a>
<a id="tocSresultlistarticletags"></a>
<a id="tocsresultlistarticletags"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "createTime": "string",
      "updateTime": "string",
      "id": 0,
      "name": "string",
      "slug": "string",
      "articleCount": 0
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[[ArticleTags](#schemaarticletags)]|false|none||数据|

<h2 id="tocS_ArticleTagDTO">ArticleTagDTO</h2>

<a id="schemaarticletagdto"></a>
<a id="schema_ArticleTagDTO"></a>
<a id="tocSarticletagdto"></a>
<a id="tocsarticletagdto"></a>

```json
{
  "id": 0,
  "name": "string",
  "slug": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|name|string|true|none||标签名称|
|slug|string|true|none||URL标识|

<h2 id="tocS_Experiences">Experiences</h2>

<a id="schemaexperiences"></a>
<a id="schema_Experiences"></a>
<a id="tocSexperiences"></a>
<a id="tocsexperiences"></a>

```json
{
  "createTime": "string",
  "updateTime": "string",
  "id": 0,
  "type": 0,
  "title": "string",
  "subtitle": "string",
  "logoUrl": "string",
  "content": "string",
  "startDate": "string",
  "endDate": "string",
  "isVisible": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|createTime|string|false|none||创建时间|
|updateTime|string|false|none||更新时间|
|id|integer(int64)|false|none||none|
|type|integer|false|none||类型，0-教育经历，1-实习及工作经历，2-项目经历|
|title|string|false|none||标题，公司名/学校名/项目名|
|subtitle|string|false|none||副标题，职位/专业/项目角色|
|logoUrl|string|false|none||logo|
|content|string|false|none||内容|
|startDate|string|false|none||开始时间|
|endDate|string|false|none||结束时间|
|isVisible|integer|false|none||是否可见|

<h2 id="tocS_ResultListExperiences">ResultListExperiences</h2>

<a id="schemaresultlistexperiences"></a>
<a id="schema_ResultListExperiences"></a>
<a id="tocSresultlistexperiences"></a>
<a id="tocsresultlistexperiences"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "createTime": "string",
      "updateTime": "string",
      "id": 0,
      "type": 0,
      "title": "string",
      "subtitle": "string",
      "logoUrl": "string",
      "content": "string",
      "startDate": "string",
      "endDate": "string",
      "isVisible": 0
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[[Experiences](#schemaexperiences)]|false|none||数据|

<h2 id="tocS_ExperienceDTO">ExperienceDTO</h2>

<a id="schemaexperiencedto"></a>
<a id="schema_ExperienceDTO"></a>
<a id="tocSexperiencedto"></a>
<a id="tocsexperiencedto"></a>

```json
{
  "id": 0,
  "type": 0,
  "title": "string",
  "subtitle": "string",
  "logoUrl": "string",
  "content": "string",
  "startDate": "string",
  "endDate": "string",
  "isVisible": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|type|integer|true|none||类型，0-教育经历，1-实习及工作经历,2-项目经历|
|title|string|true|none||标题,公司名/学校名/项目名|
|subtitle|string|false|none||副标题,职位/专业/项目角色|
|logoUrl|string|false|none||logo|
|content|string|true|none||内容|
|startDate|string|true|none||开始时间|
|endDate|string|false|none||结束时间|
|isVisible|integer|false|none||是否可见|

<h2 id="tocS_FriendLinks">FriendLinks</h2>

<a id="schemafriendlinks"></a>
<a id="schema_FriendLinks"></a>
<a id="tocSfriendlinks"></a>
<a id="tocsfriendlinks"></a>

```json
{
  "createTime": "string",
  "updateTime": "string",
  "id": 0,
  "name": "string",
  "url": "string",
  "avatarUrl": "string",
  "description": "string",
  "sort": 0,
  "isVisible": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|createTime|string|false|none||创建时间|
|updateTime|string|false|none||更新时间|
|id|integer(int64)|false|none||none|
|name|string|false|none||网站名称|
|url|string|false|none||网站地址|
|avatarUrl|string|false|none||头像 url|
|description|string|false|none||网站描述|
|sort|integer|false|none||排序，越小越靠前|
|isVisible|integer|false|none||是否可见|

<h2 id="tocS_ResultListFriendLinks">ResultListFriendLinks</h2>

<a id="schemaresultlistfriendlinks"></a>
<a id="schema_ResultListFriendLinks"></a>
<a id="tocSresultlistfriendlinks"></a>
<a id="tocsresultlistfriendlinks"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "createTime": "string",
      "updateTime": "string",
      "id": 0,
      "name": "string",
      "url": "string",
      "avatarUrl": "string",
      "description": "string",
      "sort": 0,
      "isVisible": 0
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[[FriendLinks](#schemafriendlinks)]|false|none||数据|

<h2 id="tocS_FriendLinkDTO">FriendLinkDTO</h2>

<a id="schemafriendlinkdto"></a>
<a id="schema_FriendLinkDTO"></a>
<a id="tocSfriendlinkdto"></a>
<a id="tocsfriendlinkdto"></a>

```json
{
  "id": 0,
  "name": "string",
  "url": "string",
  "avatarUrl": "string",
  "description": "string",
  "sort": 0,
  "isVisible": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|name|string|true|none||网站名称|
|url|string|true|none||网站地址|
|avatarUrl|string|false|none||头像url|
|description|string|false|none||网站描述|
|sort|integer|false|none||排序，越小越靠前|
|isVisible|integer|false|none||是否可见|

<h2 id="tocS_SocialMedia">SocialMedia</h2>

<a id="schemasocialmedia"></a>
<a id="schema_SocialMedia"></a>
<a id="tocSsocialmedia"></a>
<a id="tocssocialmedia"></a>

```json
{
  "createTime": "string",
  "updateTime": "string",
  "id": 0,
  "name": "string",
  "icon": "string",
  "link": "string",
  "sort": 0,
  "isVisible": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|createTime|string|false|none||创建时间|
|updateTime|string|false|none||更新时间|
|id|integer(int64)|false|none||none|
|name|string|false|none||名称|
|icon|string|false|none||图标类名|
|link|string|false|none||链接|
|sort|integer|false|none||排序，越小越靠前|
|isVisible|integer|false|none||是否可见|

<h2 id="tocS_ResultListSocialMedia">ResultListSocialMedia</h2>

<a id="schemaresultlistsocialmedia"></a>
<a id="schema_ResultListSocialMedia"></a>
<a id="tocSresultlistsocialmedia"></a>
<a id="tocsresultlistsocialmedia"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "createTime": "string",
      "updateTime": "string",
      "id": 0,
      "name": "string",
      "icon": "string",
      "link": "string",
      "sort": 0,
      "isVisible": 0
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[[SocialMedia](#schemasocialmedia)]|false|none||数据|

<h2 id="tocS_SocialMediaDTO">SocialMediaDTO</h2>

<a id="schemasocialmediadto"></a>
<a id="schema_SocialMediaDTO"></a>
<a id="tocSsocialmediadto"></a>
<a id="tocssocialmediadto"></a>

```json
{
  "id": 0,
  "name": "string",
  "icon": "string",
  "link": "string",
  "sort": 0,
  "isVisible": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|name|string|true|none||名称|
|icon|string|false|none||图标类名|
|link|string|false|none||链接|
|sort|integer|false|none||排序，越小越靠前|
|isVisible|integer|false|none||是否可见|

<h2 id="tocS_PersonalInfo">PersonalInfo</h2>

<a id="schemapersonalinfo"></a>
<a id="schema_PersonalInfo"></a>
<a id="tocSpersonalinfo"></a>
<a id="tocspersonalinfo"></a>

```json
{
  "createTime": "string",
  "updateTime": "string",
  "id": 0,
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

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|createTime|string|false|none||创建时间|
|updateTime|string|false|none||更新时间|
|id|integer(int64)|false|none||none|
|nickname|string|false|none||昵称|
|tag|string|false|none||标签|
|description|string|false|none||个人简介|
|avatar|string|false|none||头像 url|
|website|string|false|none||个人网站|
|email|string|false|none||电子邮箱|
|github|string|false|none||GitHub|
|location|string|false|none||所在地|

<h2 id="tocS_ResultPersonalInfo">ResultPersonalInfo</h2>

<a id="schemaresultpersonalinfo"></a>
<a id="schema_ResultPersonalInfo"></a>
<a id="tocSresultpersonalinfo"></a>
<a id="tocsresultpersonalinfo"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "createTime": "string",
    "updateTime": "string",
    "id": 0,
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

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[PersonalInfo](#schemapersonalinfo)|false|none||数据|

<h2 id="tocS_PersonalInfoDTO">PersonalInfoDTO</h2>

<a id="schemapersonalinfodto"></a>
<a id="schema_PersonalInfoDTO"></a>
<a id="tocSpersonalinfodto"></a>
<a id="tocspersonalinfodto"></a>

```json
{
  "id": 0,
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

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|nickname|string|true|none||昵称|
|tag|string|true|none||标签|
|description|string|false|none||个人简介|
|avatar|string|false|none||头像url|
|website|string|false|none||个人网站|
|email|string|false|none||电子邮箱|
|github|string|false|none||GitHub|
|location|string|false|none||所在地|

<h2 id="tocS_SystemConfig">SystemConfig</h2>

<a id="schemasystemconfig"></a>
<a id="schema_SystemConfig"></a>
<a id="tocSsystemconfig"></a>
<a id="tocssystemconfig"></a>

```json
{
  "createTime": "string",
  "updateTime": "string",
  "id": 0,
  "configKey": "string",
  "configValue": "string",
  "configType": "string",
  "description": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|createTime|string|false|none||创建时间|
|updateTime|string|false|none||更新时间|
|id|integer(int64)|false|none||none|
|configKey|string|false|none||配置键|
|configValue|string|false|none||配置值|
|configType|string|false|none||配置类型|
|description|string|false|none||配置描述|

<h2 id="tocS_ResultListSystemConfig">ResultListSystemConfig</h2>

<a id="schemaresultlistsystemconfig"></a>
<a id="schema_ResultListSystemConfig"></a>
<a id="tocSresultlistsystemconfig"></a>
<a id="tocsresultlistsystemconfig"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "createTime": "string",
      "updateTime": "string",
      "id": 0,
      "configKey": "string",
      "configValue": "string",
      "configType": "string",
      "description": "string"
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[[SystemConfig](#schemasystemconfig)]|false|none||数据|

<h2 id="tocS_ResultSystemConfig">ResultSystemConfig</h2>

<a id="schemaresultsystemconfig"></a>
<a id="schema_ResultSystemConfig"></a>
<a id="tocSresultsystemconfig"></a>
<a id="tocsresultsystemconfig"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "createTime": "string",
    "updateTime": "string",
    "id": 0,
    "configKey": "string",
    "configValue": "string",
    "configType": "string",
    "description": "string"
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[SystemConfig](#schemasystemconfig)|false|none||数据|

<h2 id="tocS_SystemConfigDTO">SystemConfigDTO</h2>

<a id="schemasystemconfigdto"></a>
<a id="schema_SystemConfigDTO"></a>
<a id="tocSsystemconfigdto"></a>
<a id="tocssystemconfigdto"></a>

```json
{
  "id": 0,
  "configKey": "string",
  "configValue": "string",
  "configType": "string",
  "description": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|configKey|string|true|none||配置键|
|configValue|string|false|none||配置值|
|configType|string|false|none||配置类型|
|description|string|false|none||配置描述|

<h2 id="tocS_ArticleComments">ArticleComments</h2>

<a id="schemaarticlecomments"></a>
<a id="schema_ArticleComments"></a>
<a id="tocSarticlecomments"></a>
<a id="tocsarticlecomments"></a>

```json
{
  "createTime": "string",
  "updateTime": "string",
  "id": 0,
  "articleId": 0,
  "rootId": 0,
  "parentId": 0,
  "parentNickname": "string",
  "content": "string",
  "contentHtml": "string",
  "visitorId": 0,
  "nickname": "string",
  "emailOrQq": "string",
  "location": "string",
  "userAgentOs": "string",
  "userAgentBrowser": "string",
  "isApproved": 0,
  "isMarkdown": 0,
  "isSecret": 0,
  "isNotice": 0,
  "isEdited": 0,
  "isAdminReply": 0,
  "articleTitle": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|createTime|string|false|none||创建时间|
|updateTime|string|false|none||更新时间|
|id|integer(int64)|false|none||none|
|articleId|integer(int64)|false|none||文章 ID|
|rootId|integer(int64)|false|none||根评论 ID,null 是一级评论|
|parentId|integer(int64)|false|none||父评论 ID,null 是一级评论|
|parentNickname|string|false|none||父评论昵称|
|content|string|false|none||评论内容|
|contentHtml|string|false|none||转换后的 HTML 内容|
|visitorId|integer(int64)|false|none||访客 ID|
|nickname|string|false|none||昵称|
|emailOrQq|string|false|none||邮箱或 qq|
|location|string|false|none||地址|
|userAgentOs|string|false|none||操作系统名称|
|userAgentBrowser|string|false|none||浏览器名称|
|isApproved|integer|false|none||是否审核通过，0-否，1-是|
|isMarkdown|integer|false|none||是否使用 markdown，0-否，1-是|
|isSecret|integer|false|none||是否匿名，0-否，1-是|
|isNotice|integer|false|none||有回复是否通知，0-否，1-是|
|isEdited|integer|false|none||是否编辑过，0-否，1-是|
|isAdminReply|integer|false|none||是否为管理员回复，0-否，1-是|
|articleTitle|string|false|none||文章标题（非数据库字段，关联查询时填充）|

<h2 id="tocS_ResultListArticleComments">ResultListArticleComments</h2>

<a id="schemaresultlistarticlecomments"></a>
<a id="schema_ResultListArticleComments"></a>
<a id="tocSresultlistarticlecomments"></a>
<a id="tocsresultlistarticlecomments"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "createTime": "string",
      "updateTime": "string",
      "id": 0,
      "articleId": 0,
      "rootId": 0,
      "parentId": 0,
      "parentNickname": "string",
      "content": "string",
      "contentHtml": "string",
      "visitorId": 0,
      "nickname": "string",
      "emailOrQq": "string",
      "location": "string",
      "userAgentOs": "string",
      "userAgentBrowser": "string",
      "isApproved": 0,
      "isMarkdown": 0,
      "isSecret": 0,
      "isNotice": 0,
      "isEdited": 0,
      "isAdminReply": 0,
      "articleTitle": "string"
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[[ArticleComments](#schemaarticlecomments)]|false|none||数据|

<h2 id="tocS_ArticleCommentReplyDTO">ArticleCommentReplyDTO</h2>

<a id="schemaarticlecommentreplydto"></a>
<a id="schema_ArticleCommentReplyDTO"></a>
<a id="tocSarticlecommentreplydto"></a>
<a id="tocsarticlecommentreplydto"></a>

```json
{
  "articleId": 0,
  "parentId": 0,
  "rootId": 0,
  "parentNickname": "string",
  "content": "string",
  "isMarkdown": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|articleId|integer(int64)|true|none||文章ID|
|parentId|integer(int64)|true|none||父评论ID|
|rootId|integer(int64)|false|none||根评论ID|
|parentNickname|string|false|none||父评论昵称|
|content|string|true|none||回复内容|
|isMarkdown|integer|false|none||是否使用markdown，0-否，1-是|

<h2 id="tocS_ArticleCategories">ArticleCategories</h2>

<a id="schemaarticlecategories"></a>
<a id="schema_ArticleCategories"></a>
<a id="tocSarticlecategories"></a>
<a id="tocsarticlecategories"></a>

```json
{
  "createTime": "string",
  "updateTime": "string",
  "id": 0,
  "name": "string",
  "slug": "string",
  "description": "string",
  "sort": 0,
  "articleCount": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|createTime|string|false|none||创建时间|
|updateTime|string|false|none||更新时间|
|id|integer(int64)|false|none||none|
|name|string|false|none||分类名称|
|slug|string|false|none||URL 标识|
|description|string|false|none||分类描述|
|sort|integer|false|none||排序，越小越靠前|
|articleCount|integer|false|none||文章数量（非数据库字段，查询时计算）|

<h2 id="tocS_ResultListArticleCategories">ResultListArticleCategories</h2>

<a id="schemaresultlistarticlecategories"></a>
<a id="schema_ResultListArticleCategories"></a>
<a id="tocSresultlistarticlecategories"></a>
<a id="tocsresultlistarticlecategories"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "createTime": "string",
      "updateTime": "string",
      "id": 0,
      "name": "string",
      "slug": "string",
      "description": "string",
      "sort": 0,
      "articleCount": 0
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[[ArticleCategories](#schemaarticlecategories)]|false|none||数据|

<h2 id="tocS_ArticleCategoryDTO">ArticleCategoryDTO</h2>

<a id="schemaarticlecategorydto"></a>
<a id="schema_ArticleCategoryDTO"></a>
<a id="tocSarticlecategorydto"></a>
<a id="tocsarticlecategorydto"></a>

```json
{
  "id": 0,
  "name": "string",
  "slug": "string",
  "description": "string",
  "sort": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|name|string|true|none||分类名称|
|slug|string|true|none||URL标识|
|description|string|false|none||分类描述|
|sort|integer|false|none||排序，越小越靠前|

<h2 id="tocS_RssSubscriptions">RssSubscriptions</h2>

<a id="schemarsssubscriptions"></a>
<a id="schema_RssSubscriptions"></a>
<a id="tocSrsssubscriptions"></a>
<a id="tocsrsssubscriptions"></a>

```json
{
  "id": 0,
  "visitorId": 0,
  "nickname": "string",
  "email": "string",
  "isActive": 0,
  "subscribeTime": "string",
  "unSubscribeTime": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|visitorId|integer(int64)|false|none||访客ID|
|nickname|string|false|none||昵称|
|email|string|false|none||邮箱|
|isActive|integer|false|none||是否激活，0-否，1-是|
|subscribeTime|string|false|none||订阅时间|
|unSubscribeTime|string|false|none||取消订阅时间|

<h2 id="tocS_ResultListRssSubscriptions">ResultListRssSubscriptions</h2>

<a id="schemaresultlistrsssubscriptions"></a>
<a id="schema_ResultListRssSubscriptions"></a>
<a id="tocSresultlistrsssubscriptions"></a>
<a id="tocsresultlistrsssubscriptions"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "id": 0,
      "visitorId": 0,
      "nickname": "string",
      "email": "string",
      "isActive": 0,
      "subscribeTime": "string",
      "unSubscribeTime": "string"
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[[RssSubscriptions](#schemarsssubscriptions)]|false|none||数据|

<h2 id="tocS_ResultRssSubscriptions">ResultRssSubscriptions</h2>

<a id="schemaresultrsssubscriptions"></a>
<a id="schema_ResultRssSubscriptions"></a>
<a id="tocSresultrsssubscriptions"></a>
<a id="tocsresultrsssubscriptions"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "id": 0,
    "visitorId": 0,
    "nickname": "string",
    "email": "string",
    "isActive": 0,
    "subscribeTime": "string",
    "unSubscribeTime": "string"
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[RssSubscriptions](#schemarsssubscriptions)|false|none||数据|

<h2 id="tocS_VisitorRecordVO">VisitorRecordVO</h2>

<a id="schemavisitorrecordvo"></a>
<a id="schema_VisitorRecordVO"></a>
<a id="tocSvisitorrecordvo"></a>
<a id="tocsvisitorrecordvo"></a>

```json
{
  "visitorFingerprint": "string",
  "sessionId": "string",
  "visitorId": 0,
  "isNewVisitor": true
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|visitorFingerprint|string|false|none||设备指纹|
|sessionId|string|false|none||当前会话ID|
|visitorId|integer(int64)|false|none||访客在数据库中的ID|
|isNewVisitor|boolean|false|none||是否是新访客|

<h2 id="tocS_ResultVisitorRecordVO">ResultVisitorRecordVO</h2>

<a id="schemaresultvisitorrecordvo"></a>
<a id="schema_ResultVisitorRecordVO"></a>
<a id="tocSresultvisitorrecordvo"></a>
<a id="tocsresultvisitorrecordvo"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "visitorFingerprint": "string",
    "sessionId": "string",
    "visitorId": 0,
    "isNewVisitor": true
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[VisitorRecordVO](#schemavisitorrecordvo)|false|none||数据|

<h2 id="tocS_VisitorRecordDTO">VisitorRecordDTO</h2>

<a id="schemavisitorrecorddto"></a>
<a id="schema_VisitorRecordDTO"></a>
<a id="tocSvisitorrecorddto"></a>
<a id="tocsvisitorrecorddto"></a>

```json
{
  "pagePath": "string",
  "pageTitle": "string",
  "referer": "string",
  "screen": "string",
  "timezone": "string",
  "language": "string",
  "platform": "string",
  "cookiesEnabled": true,
  "deviceMemory": 0,
  "hardwareConcurrency": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|pagePath|string|true|none||访问路径|
|pageTitle|string|false|none||页面标题|
|referer|string|false|none||来源页面|
|screen|string|false|none||屏幕分辨率 "1920x1080"|
|timezone|string|false|none||时区 "Asia/Shanghai"|
|language|string|false|none||语言 "zh-CN"|
|platform|string|false|none||平台 "Win32"|
|cookiesEnabled|boolean|false|none||是否支持Cookie|
|deviceMemory|integer|false|none||设备内存|
|hardwareConcurrency|integer|false|none||CPU核心数|

<h2 id="tocS_SocialMediaVO">SocialMediaVO</h2>

<a id="schemasocialmediavo"></a>
<a id="schema_SocialMediaVO"></a>
<a id="tocSsocialmediavo"></a>
<a id="tocssocialmediavo"></a>

```json
{
  "id": 0,
  "name": "string",
  "icon": "string",
  "link": "string",
  "sort": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|name|string|false|none||名称|
|icon|string|false|none||图标类名|
|link|string|false|none||链接|
|sort|integer|false|none||排序，越小越靠前|

<h2 id="tocS_ResultListSocialMediaVO">ResultListSocialMediaVO</h2>

<a id="schemaresultlistsocialmediavo"></a>
<a id="schema_ResultListSocialMediaVO"></a>
<a id="tocSresultlistsocialmediavo"></a>
<a id="tocsresultlistsocialmediavo"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "id": 0,
      "name": "string",
      "icon": "string",
      "link": "string",
      "sort": 0
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[[SocialMediaVO](#schemasocialmediavo)]|false|none||数据|

<h2 id="tocS_PersonalInfoVO">PersonalInfoVO</h2>

<a id="schemapersonalinfovo"></a>
<a id="schema_PersonalInfoVO"></a>
<a id="tocSpersonalinfovo"></a>
<a id="tocspersonalinfovo"></a>

```json
{
  "id": 0,
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

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|nickname|string|false|none||昵称|
|tag|string|false|none||标签|
|description|string|false|none||个人简介|
|avatar|string|false|none||头像url|
|website|string|false|none||个人网站|
|email|string|false|none||电子邮箱|
|github|string|false|none||GitHub|
|location|string|false|none||所在地|

<h2 id="tocS_ResultPersonalInfoVO">ResultPersonalInfoVO</h2>

<a id="schemaresultpersonalinfovo"></a>
<a id="schema_ResultPersonalInfoVO"></a>
<a id="tocSresultpersonalinfovo"></a>
<a id="tocsresultpersonalinfovo"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "id": 0,
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

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[PersonalInfoVO](#schemapersonalinfovo)|false|none||数据|

<h2 id="tocS_MusicVO">MusicVO</h2>

<a id="schemamusicvo"></a>
<a id="schema_MusicVO"></a>
<a id="tocSmusicvo"></a>
<a id="tocsmusicvo"></a>

```json
{
  "id": 0,
  "title": "string",
  "artist": "string",
  "duration": 0,
  "coverImage": "string",
  "musicUrl": "string",
  "lyricUrl": "string",
  "hasLyric": 0,
  "lyricType": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|title|string|false|none||音乐标题|
|artist|string|false|none||作者|
|duration|integer|false|none||时长，单位：秒|
|coverImage|string|false|none||封面图片url|
|musicUrl|string|false|none||音频文件url|
|lyricUrl|string|false|none||歌词文件url|
|hasLyric|integer|false|none||是否有歌词，0-否，1-是|
|lyricType|string|false|none||歌词类型,lrc,json,txt|

<h2 id="tocS_ResultListMusicVO">ResultListMusicVO</h2>

<a id="schemaresultlistmusicvo"></a>
<a id="schema_ResultListMusicVO"></a>
<a id="tocSresultlistmusicvo"></a>
<a id="tocsresultlistmusicvo"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "id": 0,
      "title": "string",
      "artist": "string",
      "duration": 0,
      "coverImage": "string",
      "musicUrl": "string",
      "lyricUrl": "string",
      "hasLyric": 0,
      "lyricType": "string"
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[[MusicVO](#schemamusicvo)]|false|none||数据|

<h2 id="tocS_CaptchaVO">CaptchaVO</h2>

<a id="schemacaptchavo"></a>
<a id="schema_CaptchaVO"></a>
<a id="tocScaptchavo"></a>
<a id="tocscaptchavo"></a>

```json
{
  "captchaId": "string",
  "question": "string",
  "result": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|captchaId|string|false|none||验证码ID|
|question|string|false|none||算术题目，如 "3 + 5 = ?"|
|result|integer|false|none||正确答案|

<h2 id="tocS_ResultCaptchaVO">ResultCaptchaVO</h2>

<a id="schemaresultcaptchavo"></a>
<a id="schema_ResultCaptchaVO"></a>
<a id="tocSresultcaptchavo"></a>
<a id="tocsresultcaptchavo"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "captchaId": "string",
    "question": "string",
    "result": 0
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[CaptchaVO](#schemacaptchavo)|false|none||数据|

<h2 id="tocS_BlogReportVO">BlogReportVO</h2>

<a id="schemablogreportvo"></a>
<a id="schema_BlogReportVO"></a>
<a id="tocSblogreportvo"></a>
<a id="tocsblogreportvo"></a>

```json
{
  "viewTotalCount": 0,
  "viewTodayCount": 0,
  "visitorTotalCount": 0,
  "categoryTotalCount": 0,
  "tagTotalCount": 0,
  "articleTotalCount": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|viewTotalCount|integer|false|none||总浏览量|
|viewTodayCount|integer|false|none||今日浏览量|
|visitorTotalCount|integer|false|none||总访客数|
|categoryTotalCount|integer|false|none||总文章分类数|
|tagTotalCount|integer|false|none||总文章标签数|
|articleTotalCount|integer|false|none||总文章数|

<h2 id="tocS_ResultBlogReportVO">ResultBlogReportVO</h2>

<a id="schemaresultblogreportvo"></a>
<a id="schema_ResultBlogReportVO"></a>
<a id="tocSresultblogreportvo"></a>
<a id="tocsresultblogreportvo"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "viewTotalCount": 0,
    "viewTodayCount": 0,
    "visitorTotalCount": 0,
    "categoryTotalCount": 0,
    "tagTotalCount": 0,
    "articleTotalCount": 0
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[BlogReportVO](#schemablogreportvo)|false|none||数据|

<h2 id="tocS_BlogArticleVO">BlogArticleVO</h2>

<a id="schemablogarticlevo"></a>
<a id="schema_BlogArticleVO"></a>
<a id="tocSblogarticlevo"></a>
<a id="tocsblogarticlevo"></a>

```json
{
  "id": 0,
  "title": "string",
  "slug": "string",
  "summary": "string",
  "coverImage": "string",
  "categoryId": 0,
  "categoryName": "string",
  "viewCount": 0,
  "likeCount": 0,
  "commentCount": 0,
  "wordCount": 0,
  "readingTime": 0,
  "isTop": 0,
  "publishTime": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|title|string|false|none||none|
|slug|string|false|none||none|
|summary|string|false|none||none|
|coverImage|string|false|none||none|
|categoryId|integer(int64)|false|none||none|
|categoryName|string|false|none||none|
|viewCount|integer(int64)|false|none||none|
|likeCount|integer(int64)|false|none||none|
|commentCount|integer(int64)|false|none||none|
|wordCount|integer(int64)|false|none||none|
|readingTime|integer(int64)|false|none||none|
|isTop|integer|false|none||none|
|publishTime|string|false|none||none|

<h2 id="tocS_PageResultBlogArticleVO">PageResultBlogArticleVO</h2>

<a id="schemapageresultblogarticlevo"></a>
<a id="schema_PageResultBlogArticleVO"></a>
<a id="tocSpageresultblogarticlevo"></a>
<a id="tocspageresultblogarticlevo"></a>

```json
{
  "page": 0,
  "pageSize": 0,
  "total": 0,
  "totalPages": 0,
  "records": [
    {
      "id": 0,
      "title": "string",
      "slug": "string",
      "summary": "string",
      "coverImage": "string",
      "categoryId": 0,
      "categoryName": "string",
      "viewCount": 0,
      "likeCount": 0,
      "commentCount": 0,
      "wordCount": 0,
      "readingTime": 0,
      "isTop": 0,
      "publishTime": "string"
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|page|integer(int64)|false|none||当前页码|
|pageSize|integer(int64)|false|none||每页显示数量|
|total|integer(int64)|false|none||总记录数|
|totalPages|integer(int64)|false|none||总页数|
|records|[[BlogArticleVO](#schemablogarticlevo)]|false|none||数据列表|

<h2 id="tocS_ResultPageResultBlogArticleVO">ResultPageResultBlogArticleVO</h2>

<a id="schemaresultpageresultblogarticlevo"></a>
<a id="schema_ResultPageResultBlogArticleVO"></a>
<a id="tocSresultpageresultblogarticlevo"></a>
<a id="tocsresultpageresultblogarticlevo"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "page": 0,
    "pageSize": 0,
    "total": 0,
    "totalPages": 0,
    "records": [
      {
        "id": 0,
        "title": "string",
        "slug": "string",
        "summary": "string",
        "coverImage": "string",
        "categoryId": 0,
        "categoryName": "string",
        "viewCount": 0,
        "likeCount": 0,
        "commentCount": 0,
        "wordCount": 0,
        "readingTime": 0,
        "isTop": 0,
        "publishTime": "string"
      }
    ]
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[PageResultBlogArticleVO](#schemapageresultblogarticlevo)|false|none||数据|

<h2 id="tocS_BlogArticleDetailVO">BlogArticleDetailVO</h2>

<a id="schemablogarticledetailvo"></a>
<a id="schema_BlogArticleDetailVO"></a>
<a id="tocSblogarticledetailvo"></a>
<a id="tocsblogarticledetailvo"></a>

```json
{
  "id": 0,
  "title": "string",
  "slug": "string",
  "summary": "string",
  "coverImage": "string",
  "contentHtml": "string",
  "contentMarkdown": "string",
  "categoryId": 0,
  "categoryName": "string",
  "viewCount": 0,
  "likeCount": 0,
  "commentCount": 0,
  "wordCount": 0,
  "readingTime": 0,
  "publishTime": "string",
  "updateTime": "string",
  "tagNames": [
    "string"
  ],
  "prevArticle": {
    "id": 0,
    "title": "string",
    "slug": "string",
    "summary": "string",
    "coverImage": "string",
    "categoryId": 0,
    "categoryName": "string",
    "viewCount": 0,
    "likeCount": 0,
    "commentCount": 0,
    "wordCount": 0,
    "readingTime": 0,
    "isTop": 0,
    "publishTime": "string"
  },
  "nextArticle": {
    "id": 0,
    "title": "string",
    "slug": "string",
    "summary": "string",
    "coverImage": "string",
    "categoryId": 0,
    "categoryName": "string",
    "viewCount": 0,
    "likeCount": 0,
    "commentCount": 0,
    "wordCount": 0,
    "readingTime": 0,
    "isTop": 0,
    "publishTime": "string"
  },
  "relatedArticles": [
    {
      "id": 0,
      "title": "string",
      "slug": "string",
      "summary": "string",
      "coverImage": "string",
      "categoryId": 0,
      "categoryName": "string",
      "viewCount": 0,
      "likeCount": 0,
      "commentCount": 0,
      "wordCount": 0,
      "readingTime": 0,
      "isTop": 0,
      "publishTime": "string"
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|title|string|false|none||none|
|slug|string|false|none||none|
|summary|string|false|none||none|
|coverImage|string|false|none||none|
|contentHtml|string|false|none||none|
|contentMarkdown|string|false|none||none|
|categoryId|integer(int64)|false|none||none|
|categoryName|string|false|none||none|
|viewCount|integer(int64)|false|none||none|
|likeCount|integer(int64)|false|none||none|
|commentCount|integer(int64)|false|none||none|
|wordCount|integer(int64)|false|none||none|
|readingTime|integer(int64)|false|none||none|
|publishTime|string|false|none||none|
|updateTime|string|false|none||none|
|tagNames|[string]|false|none||文章标签名称列表|
|prevArticle|[BlogArticleVO](#schemablogarticlevo)|false|none||上一篇/下一篇导航|
|nextArticle|[BlogArticleVO](#schemablogarticlevo)|false|none||none|
|relatedArticles|[[BlogArticleVO](#schemablogarticlevo)]|false|none||相关文章推荐|

<h2 id="tocS_ResultBlogArticleDetailVO">ResultBlogArticleDetailVO</h2>

<a id="schemaresultblogarticledetailvo"></a>
<a id="schema_ResultBlogArticleDetailVO"></a>
<a id="tocSresultblogarticledetailvo"></a>
<a id="tocsresultblogarticledetailvo"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "id": 0,
    "title": "string",
    "slug": "string",
    "summary": "string",
    "coverImage": "string",
    "contentHtml": "string",
    "contentMarkdown": "string",
    "categoryId": 0,
    "categoryName": "string",
    "viewCount": 0,
    "likeCount": 0,
    "commentCount": 0,
    "wordCount": 0,
    "readingTime": 0,
    "publishTime": "string",
    "updateTime": "string",
    "tagNames": [
      "string"
    ],
    "prevArticle": {
      "id": 0,
      "title": "string",
      "slug": "string",
      "summary": "string",
      "coverImage": "string",
      "categoryId": 0,
      "categoryName": "string",
      "viewCount": 0,
      "likeCount": 0,
      "commentCount": 0,
      "wordCount": 0,
      "readingTime": 0,
      "isTop": 0,
      "publishTime": "string"
    },
    "nextArticle": {
      "id": 0,
      "title": "string",
      "slug": "string",
      "summary": "string",
      "coverImage": "string",
      "categoryId": 0,
      "categoryName": "string",
      "viewCount": 0,
      "likeCount": 0,
      "commentCount": 0,
      "wordCount": 0,
      "readingTime": 0,
      "isTop": 0,
      "publishTime": "string"
    },
    "relatedArticles": [
      {
        "id": 0,
        "title": "string",
        "slug": "string",
        "summary": "string",
        "coverImage": "string",
        "categoryId": 0,
        "categoryName": "string",
        "viewCount": 0,
        "likeCount": 0,
        "commentCount": 0,
        "wordCount": 0,
        "readingTime": 0,
        "isTop": 0,
        "publishTime": "string"
      }
    ]
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[BlogArticleDetailVO](#schemablogarticledetailvo)|false|none||数据|

<h2 id="tocS_ArticleArchiveItemVO">ArticleArchiveItemVO</h2>

<a id="schemaarticlearchiveitemvo"></a>
<a id="schema_ArticleArchiveItemVO"></a>
<a id="tocSarticlearchiveitemvo"></a>
<a id="tocsarticlearchiveitemvo"></a>

```json
{
  "id": 0,
  "title": "string",
  "slug": "string",
  "publishDay": 0,
  "publishTime": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|title|string|false|none||none|
|slug|string|false|none||none|
|publishDay|integer|false|none||none|
|publishTime|string|false|none||none|

<h2 id="tocS_ArticleArchiveVO">ArticleArchiveVO</h2>

<a id="schemaarticlearchivevo"></a>
<a id="schema_ArticleArchiveVO"></a>
<a id="tocSarticlearchivevo"></a>
<a id="tocsarticlearchivevo"></a>

```json
{
  "year": 0,
  "month": 0,
  "articles": [
    {
      "id": 0,
      "title": "string",
      "slug": "string",
      "publishDay": 0,
      "publishTime": "string"
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|year|integer|false|none||none|
|month|integer|false|none||none|
|articles|[[ArticleArchiveItemVO](#schemaarticlearchiveitemvo)]|false|none||none|

<h2 id="tocS_ResultListArticleArchiveVO">ResultListArticleArchiveVO</h2>

<a id="schemaresultlistarticlearchivevo"></a>
<a id="schema_ResultListArticleArchiveVO"></a>
<a id="tocSresultlistarticlearchivevo"></a>
<a id="tocsresultlistarticlearchivevo"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "year": 0,
      "month": 0,
      "articles": [
        {
          "id": 0,
          "title": "string",
          "slug": "string",
          "publishDay": 0,
          "publishTime": "string"
        }
      ]
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[[ArticleArchiveVO](#schemaarticlearchivevo)]|false|none||数据|

<h2 id="tocS_MessageDTO">MessageDTO</h2>

<a id="schemamessagedto"></a>
<a id="schema_MessageDTO"></a>
<a id="tocSmessagedto"></a>
<a id="tocsmessagedto"></a>

```json
{
  "content": "string",
  "rootId": 0,
  "parentId": 0,
  "parentNickname": "string",
  "visitorId": 0,
  "nickname": "string",
  "emailOrQq": "string",
  "isMarkdown": 0,
  "isSecret": 0,
  "isNotice": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|content|string|true|none||留言内容|
|rootId|integer(int64)|false|none||根留言ID,null是一级留言|
|parentId|integer(int64)|false|none||父留言ID,null是一级留言|
|parentNickname|string|false|none||父留言昵称|
|visitorId|integer(int64)|true|none||访客ID|
|nickname|string|true|none||昵称|
|emailOrQq|string|false|none||邮箱或qq|
|isMarkdown|integer|false|none||是否使用markdown，0-否，1-是|
|isSecret|integer|false|none||是否匿名，0-否，1-是|
|isNotice|integer|false|none||有回复是否通知，0-否，1-是|

<h2 id="tocS_MessageVO">MessageVO</h2>

<a id="schemamessagevo"></a>
<a id="schema_MessageVO"></a>
<a id="tocSmessagevo"></a>
<a id="tocsmessagevo"></a>

```json
{
  "id": 0,
  "rootId": 0,
  "parentId": 0,
  "parentNickname": "string",
  "content": "string",
  "contentHtml": "string",
  "isMarkdown": 0,
  "visitorId": 0,
  "nickname": "string",
  "emailOrQq": "string",
  "location": "string",
  "userAgentOs": "string",
  "userAgentBrowser": "string",
  "isApproved": 0,
  "isSecret": 0,
  "isAdminReply": 0,
  "createTime": "string",
  "children": [
    {
      "id": 0,
      "rootId": 0,
      "parentId": 0,
      "parentNickname": "string",
      "content": "string",
      "contentHtml": "string",
      "isMarkdown": 0,
      "visitorId": 0,
      "nickname": "string",
      "emailOrQq": "string",
      "location": "string",
      "userAgentOs": "string",
      "userAgentBrowser": "string",
      "isApproved": 0,
      "isSecret": 0,
      "isAdminReply": 0,
      "createTime": "string",
      "children": [
        {
          "id": 0,
          "rootId": 0,
          "parentId": 0,
          "parentNickname": "string",
          "content": "string",
          "contentHtml": "string",
          "isMarkdown": 0,
          "visitorId": 0,
          "nickname": "string",
          "emailOrQq": "string",
          "location": "string",
          "userAgentOs": "string",
          "userAgentBrowser": "string",
          "isApproved": 0,
          "isSecret": 0,
          "isAdminReply": 0,
          "createTime": "string",
          "children": [
            {}
          ]
        }
      ]
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|rootId|integer(int64)|false|none||none|
|parentId|integer(int64)|false|none||none|
|parentNickname|string|false|none||none|
|content|string|false|none||none|
|contentHtml|string|false|none||none|
|isMarkdown|integer|false|none||none|
|visitorId|integer(int64)|false|none||none|
|nickname|string|false|none||none|
|emailOrQq|string|false|none||none|
|location|string|false|none||none|
|userAgentOs|string|false|none||none|
|userAgentBrowser|string|false|none||none|
|isApproved|integer|false|none||none|
|isSecret|integer|false|none||none|
|isAdminReply|integer|false|none||none|
|createTime|string|false|none||none|
|children|[[MessageVO](#schemamessagevo)]|false|none||子留言列表（仅根留言有值）|

<h2 id="tocS_ResultListMessageVO">ResultListMessageVO</h2>

<a id="schemaresultlistmessagevo"></a>
<a id="schema_ResultListMessageVO"></a>
<a id="tocSresultlistmessagevo"></a>
<a id="tocsresultlistmessagevo"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "id": 0,
      "rootId": 0,
      "parentId": 0,
      "parentNickname": "string",
      "content": "string",
      "contentHtml": "string",
      "isMarkdown": 0,
      "visitorId": 0,
      "nickname": "string",
      "emailOrQq": "string",
      "location": "string",
      "userAgentOs": "string",
      "userAgentBrowser": "string",
      "isApproved": 0,
      "isSecret": 0,
      "isAdminReply": 0,
      "createTime": "string",
      "children": [
        {
          "id": 0,
          "rootId": 0,
          "parentId": 0,
          "parentNickname": "string",
          "content": "string",
          "contentHtml": "string",
          "isMarkdown": 0,
          "visitorId": 0,
          "nickname": "string",
          "emailOrQq": "string",
          "location": "string",
          "userAgentOs": "string",
          "userAgentBrowser": "string",
          "isApproved": 0,
          "isSecret": 0,
          "isAdminReply": 0,
          "createTime": "string",
          "children": [
            {}
          ]
        }
      ]
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[[MessageVO](#schemamessagevo)]|false|none||数据|

<h2 id="tocS_MessageEditDTO">MessageEditDTO</h2>

<a id="schemamessageeditdto"></a>
<a id="schema_MessageEditDTO"></a>
<a id="tocSmessageeditdto"></a>
<a id="tocsmessageeditdto"></a>

```json
{
  "id": 0,
  "visitorId": 0,
  "content": "string",
  "isMarkdown": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|true|none||留言ID|
|visitorId|integer(int64)|true|none||访客ID（用于验证身份）|
|content|string|true|none||编辑后的内容|
|isMarkdown|integer|false|none||是否使用markdown|

<h2 id="tocS_FriendLinkVO">FriendLinkVO</h2>

<a id="schemafriendlinkvo"></a>
<a id="schema_FriendLinkVO"></a>
<a id="tocSfriendlinkvo"></a>
<a id="tocsfriendlinkvo"></a>

```json
{
  "id": 0,
  "name": "string",
  "url": "string",
  "avatarUrl": "string",
  "description": "string",
  "sort": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|name|string|false|none||网站名称|
|url|string|false|none||网站地址|
|avatarUrl|string|false|none||头像url|
|description|string|false|none||网站描述|
|sort|integer|false|none||排序|

<h2 id="tocS_ResultListFriendLinkVO">ResultListFriendLinkVO</h2>

<a id="schemaresultlistfriendlinkvo"></a>
<a id="schema_ResultListFriendLinkVO"></a>
<a id="tocSresultlistfriendlinkvo"></a>
<a id="tocsresultlistfriendlinkvo"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "id": 0,
      "name": "string",
      "url": "string",
      "avatarUrl": "string",
      "description": "string",
      "sort": 0
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[[FriendLinkVO](#schemafriendlinkvo)]|false|none||数据|

<h2 id="tocS_ResultBoolean">ResultBoolean</h2>

<a id="schemaresultboolean"></a>
<a id="schema_ResultBoolean"></a>
<a id="tocSresultboolean"></a>
<a id="tocsresultboolean"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": true
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|boolean|false|none||数据|

<h2 id="tocS_ArticleCommentVO">ArticleCommentVO</h2>

<a id="schemaarticlecommentvo"></a>
<a id="schema_ArticleCommentVO"></a>
<a id="tocSarticlecommentvo"></a>
<a id="tocsarticlecommentvo"></a>

```json
{
  "id": 0,
  "articleId": 0,
  "rootId": 0,
  "parentId": 0,
  "parentNickname": "string",
  "content": "string",
  "contentHtml": "string",
  "isMarkdown": 0,
  "visitorId": 0,
  "nickname": "string",
  "emailOrQq": "string",
  "location": "string",
  "userAgentOs": "string",
  "userAgentBrowser": "string",
  "isApproved": 0,
  "isSecret": 0,
  "isAdminReply": 0,
  "createTime": "string",
  "children": [
    {
      "id": 0,
      "articleId": 0,
      "rootId": 0,
      "parentId": 0,
      "parentNickname": "string",
      "content": "string",
      "contentHtml": "string",
      "isMarkdown": 0,
      "visitorId": 0,
      "nickname": "string",
      "emailOrQq": "string",
      "location": "string",
      "userAgentOs": "string",
      "userAgentBrowser": "string",
      "isApproved": 0,
      "isSecret": 0,
      "isAdminReply": 0,
      "createTime": "string",
      "children": [
        {
          "id": 0,
          "articleId": 0,
          "rootId": 0,
          "parentId": 0,
          "parentNickname": "string",
          "content": "string",
          "contentHtml": "string",
          "isMarkdown": 0,
          "visitorId": 0,
          "nickname": "string",
          "emailOrQq": "string",
          "location": "string",
          "userAgentOs": "string",
          "userAgentBrowser": "string",
          "isApproved": 0,
          "isSecret": 0,
          "isAdminReply": 0,
          "createTime": "string",
          "children": [
            {}
          ]
        }
      ]
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|articleId|integer(int64)|false|none||none|
|rootId|integer(int64)|false|none||none|
|parentId|integer(int64)|false|none||none|
|parentNickname|string|false|none||none|
|content|string|false|none||none|
|contentHtml|string|false|none||none|
|isMarkdown|integer|false|none||none|
|visitorId|integer(int64)|false|none||none|
|nickname|string|false|none||none|
|emailOrQq|string|false|none||none|
|location|string|false|none||none|
|userAgentOs|string|false|none||none|
|userAgentBrowser|string|false|none||none|
|isApproved|integer|false|none||none|
|isSecret|integer|false|none||none|
|isAdminReply|integer|false|none||none|
|createTime|string|false|none||none|
|children|[[ArticleCommentVO](#schemaarticlecommentvo)]|false|none||子评论列表（仅根评论有值）|

<h2 id="tocS_ResultListArticleCommentVO">ResultListArticleCommentVO</h2>

<a id="schemaresultlistarticlecommentvo"></a>
<a id="schema_ResultListArticleCommentVO"></a>
<a id="tocSresultlistarticlecommentvo"></a>
<a id="tocsresultlistarticlecommentvo"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "id": 0,
      "articleId": 0,
      "rootId": 0,
      "parentId": 0,
      "parentNickname": "string",
      "content": "string",
      "contentHtml": "string",
      "isMarkdown": 0,
      "visitorId": 0,
      "nickname": "string",
      "emailOrQq": "string",
      "location": "string",
      "userAgentOs": "string",
      "userAgentBrowser": "string",
      "isApproved": 0,
      "isSecret": 0,
      "isAdminReply": 0,
      "createTime": "string",
      "children": [
        {
          "id": 0,
          "articleId": 0,
          "rootId": 0,
          "parentId": 0,
          "parentNickname": "string",
          "content": "string",
          "contentHtml": "string",
          "isMarkdown": 0,
          "visitorId": 0,
          "nickname": "string",
          "emailOrQq": "string",
          "location": "string",
          "userAgentOs": "string",
          "userAgentBrowser": "string",
          "isApproved": 0,
          "isSecret": 0,
          "isAdminReply": 0,
          "createTime": "string",
          "children": [
            {}
          ]
        }
      ]
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[[ArticleCommentVO](#schemaarticlecommentvo)]|false|none||数据|

<h2 id="tocS_ArticleCommentDTO">ArticleCommentDTO</h2>

<a id="schemaarticlecommentdto"></a>
<a id="schema_ArticleCommentDTO"></a>
<a id="tocSarticlecommentdto"></a>
<a id="tocsarticlecommentdto"></a>

```json
{
  "articleId": 0,
  "rootId": 0,
  "parentId": 0,
  "parentNickname": "string",
  "content": "string",
  "visitorId": 0,
  "nickname": "string",
  "emailOrQq": "string",
  "isMarkdown": 0,
  "isSecret": 0,
  "isNotice": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|articleId|integer(int64)|true|none||none|
|rootId|integer(int64)|false|none||none|
|parentId|integer(int64)|false|none||none|
|parentNickname|string|false|none||none|
|content|string|true|none||none|
|visitorId|integer(int64)|true|none||none|
|nickname|string|true|none||none|
|emailOrQq|string|false|none||none|
|isMarkdown|integer|false|none||none|
|isSecret|integer|false|none||none|
|isNotice|integer|false|none||none|

<h2 id="tocS_ArticleCommentEditDTO">ArticleCommentEditDTO</h2>

<a id="schemaarticlecommenteditdto"></a>
<a id="schema_ArticleCommentEditDTO"></a>
<a id="tocSarticlecommenteditdto"></a>
<a id="tocsarticlecommenteditdto"></a>

```json
{
  "id": 0,
  "visitorId": 0,
  "content": "string",
  "isMarkdown": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|true|none||评论ID|
|visitorId|integer(int64)|true|none||访客ID（用于验证身份）|
|content|string|true|none||编辑后的内容|
|isMarkdown|integer|false|none||是否使用markdown|

<h2 id="tocS_RssSubscriptionDTO">RssSubscriptionDTO</h2>

<a id="schemarsssubscriptiondto"></a>
<a id="schema_RssSubscriptionDTO"></a>
<a id="tocSrsssubscriptiondto"></a>
<a id="tocsrsssubscriptiondto"></a>

```json
{
  "visitorId": 0,
  "nickname": "string",
  "email": "user@example.com"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|visitorId|integer(int64)|true|none||访客ID|
|nickname|string|false|none||昵称|
|email|string(email)|true|none||邮箱|

<h2 id="tocS_RssSubscriptionStatusVO">RssSubscriptionStatusVO</h2>

<a id="schemarsssubscriptionstatusvo"></a>
<a id="schema_RssSubscriptionStatusVO"></a>
<a id="tocSrsssubscriptionstatusvo"></a>
<a id="tocsrsssubscriptionstatusvo"></a>

```json
{
  "subscribed": true,
  "nickname": "string",
  "email": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|subscribed|boolean|false|none||是否已订阅|
|nickname|string|false|none||订阅时使用的昵称|
|email|string|false|none||订阅时使用的邮箱|

<h2 id="tocS_ResultRssSubscriptionStatusVO">ResultRssSubscriptionStatusVO</h2>

<a id="schemaresultrsssubscriptionstatusvo"></a>
<a id="schema_ResultRssSubscriptionStatusVO"></a>
<a id="tocSresultrsssubscriptionstatusvo"></a>
<a id="tocsresultrsssubscriptionstatusvo"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "subscribed": true,
    "nickname": "string",
    "email": "string"
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[RssSubscriptionStatusVO](#schemarsssubscriptionstatusvo)|false|none||数据|

<h2 id="tocS_SkillVO">SkillVO</h2>

<a id="schemaskillvo"></a>
<a id="schema_SkillVO"></a>
<a id="tocSskillvo"></a>
<a id="tocsskillvo"></a>

```json
{
  "id": 0,
  "name": "string",
  "description": "string",
  "icon": "string",
  "sort": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|name|string|false|none||技能名称|
|description|string|false|none||技能描述|
|icon|string|false|none||图标url|
|sort|integer|false|none||排序，越小越靠前|

<h2 id="tocS_ResultListSkillVO">ResultListSkillVO</h2>

<a id="schemaresultlistskillvo"></a>
<a id="schema_ResultListSkillVO"></a>
<a id="tocSresultlistskillvo"></a>
<a id="tocsresultlistskillvo"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "id": 0,
      "name": "string",
      "description": "string",
      "icon": "string",
      "sort": 0
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[[SkillVO](#schemaskillvo)]|false|none||数据|

<h2 id="tocS_ExperienceVO">ExperienceVO</h2>

<a id="schemaexperiencevo"></a>
<a id="schema_ExperienceVO"></a>
<a id="tocSexperiencevo"></a>
<a id="tocsexperiencevo"></a>

```json
{
  "id": 0,
  "type": 0,
  "title": "string",
  "subtitle": "string",
  "logoUrl": "string",
  "content": "string",
  "startDate": "string",
  "endDate": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|type|integer|false|none||类型，0-教育经历，1-实习及工作经历,2-项目经历|
|title|string|false|none||标题,公司名/学校名/项目名|
|subtitle|string|false|none||副标题,职位/专业/项目角色|
|logoUrl|string|false|none||logo|
|content|string|false|none||内容|
|startDate|string|false|none||开始时间|
|endDate|string|false|none||结束时间|

<h2 id="tocS_ResultListExperienceVO">ResultListExperienceVO</h2>

<a id="schemaresultlistexperiencevo"></a>
<a id="schema_ResultListExperienceVO"></a>
<a id="tocSresultlistexperiencevo"></a>
<a id="tocsresultlistexperiencevo"></a>

```json
{
  "code": 0,
  "msg": "string",
  "data": [
    {
      "id": 0,
      "type": 0,
      "title": "string",
      "subtitle": "string",
      "logoUrl": "string",
      "content": "string",
      "startDate": "string",
      "endDate": "string"
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||编码：1成功，0和其它数字为失败|
|msg|string|false|none||错误信息|
|data|[[ExperienceVO](#schemaexperiencevo)]|false|none||数据|

