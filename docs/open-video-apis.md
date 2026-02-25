# 开源/免费视频信息 API 调研

> 调研日期：2026-02-25
> 目的：寻找可直接获取视频信息（标题、缩略图、播放量、流地址等）的公开 API
> 当前项目已使用：Bilibili 公开 API

---

## 目录

1. [无需 API Key，直接可用](#1-无需-api-key直接可用)
   - 1.1 Invidious API（YouTube 数据）
   - 1.2 Piped API（YouTube 数据）
   - 1.3 Bilibili 公开 API（项目已使用）
2. [需要 API Key（有免费额度）](#2-需要-api-key有免费额度)
   - 2.1 YouTube Data API v3
   - 2.2 Dailymotion API
   - 2.3 Vimeo API
3. [对比汇总](#3-对比汇总)
4. [与本项目的适配分析](#4-与本项目的适配分析)

---

## 1. 无需 API Key，直接可用

### 1.1 Invidious API（YouTube 数据）

开源的 YouTube 替代前端，提供完整的 JSON API，**无需任何认证**。

- **GitHub**：https://github.com/iv-org/invidious
- **API 文档**：https://docs.invidious.io/api/
- **公共实例**：`https://vid.puffyan.us`、`https://invidious.snopyta.org` 等
- **协议**：AGPL-3.0

#### 核心端点

| 端点 | 方法 | 功能 | 参数 |
|------|:----:|------|------|
| `/api/v1/videos/:id` | GET | 视频详情 | `region`（ISO 3166 国家码） |
| `/api/v1/trending` | GET | 热门视频 | `region`、`type`（music/gaming/movies） |
| `/api/v1/popular` | GET | 流行视频 | — |
| `/api/v1/search` | GET | 搜索视频 | `q`、`page`、`sort`、`type`、`date`、`duration` |
| `/api/v1/search/suggestions` | GET | 搜索建议 | `q` |
| `/api/v1/comments/:id` | GET | 视频评论 | `sort`（top/new）、`source`（youtube/reddit） |
| `/api/v1/captions/:id` | GET | 视频字幕 | `label`（语言）、WebVTT 格式 |
| `/api/v1/playlists/:plid` | GET | 播放列表 | `page` |
| `/api/v1/stats` | GET | 实例状态 | — |

#### 返回数据示例（`/api/v1/videos/:id`）

```json
{
  "title": "视频标题",
  "videoId": "dQw4w9WgXcQ",
  "videoThumbnails": [
    { "quality": "maxres", "url": "https://...", "width": 1280, "height": 720 }
  ],
  "description": "视频描述",
  "viewCount": 1234567,
  "likeCount": 12345,
  "lengthSeconds": 212,
  "author": "频道名",
  "authorId": "UCxxxxxx",
  "published": 1609459200,
  "adaptiveFormats": [
    { "url": "https://...", "type": "video/mp4", "quality": "1080p" },
    { "url": "https://...", "type": "audio/mp4", "quality": "medium" }
  ]
}
```

#### 优缺点

| 优点 | 缺点 |
|------|------|
| 完全免费，无需注册 | 依赖公共实例，可能不稳定 |
| 接口丰富（视频/搜索/评论/字幕） | 公共实例可能有速率限制 |
| 支持音视频流分离地址 | 可自建但需要服务器资源 |
| 所有端点支持 `&hl=LANGUAGE` 多语言 | YouTube 可能封锁实例 IP |

---

### 1.2 Piped API（YouTube 数据）

另一个开源 YouTube 前端，基于 **NewPipeExtractor** 提取数据，**无需认证**。

- **GitHub**：https://github.com/TeamPiped/Piped （9.8k stars）
- **API 文档**：https://docs.piped.video/docs/api-documentation/
- **API 规范**：https://github.com/TeamPiped/OpenAPI
- **公共实例**：`https://pipedapi.kavin.rocks`
- **协议**：AGPL-3.0

#### 核心端点

| 端点 | 方法 | 功能 | 参数 |
|------|:----:|------|------|
| `/streams/:videoId` | GET | 视频详情（含音视频流） | — |
| `/trending` | GET | 地区热门视频 | `region`（国家码） |
| `/search` | GET | 搜索视频 | `q`、`filter` |
| `/suggestions` | GET | 搜索建议 | `query` |
| `/comments/:videoId` | GET | 视频评论 | — |
| `/nextpage/comments/:videoId` | GET | 评论分页 | `nextpage` |
| `/channel/:channelId` | GET | 频道信息和视频列表 | — |
| `/c/:name` | GET | 按频道名获取信息 | — |
| `/user/:name` | GET | 按用户名获取频道信息 | — |
| `/nextpage/channel/:channelId` | GET | 频道视频分页 | `nextpage` |
| `/playlists/:playlistId` | GET | 播放列表 | — |
| `/sponsors/:videoId` | GET | 赞助商片段（SponsorBlock） | — |

#### 返回数据示例（`/streams/:videoId`）

```json
{
  "title": "视频标题",
  "description": "视频描述",
  "uploadDate": "2024-01-01",
  "uploader": "频道名",
  "uploaderUrl": "/channel/UCxxxxxx",
  "uploaderAvatar": "https://...",
  "thumbnailUrl": "https://...",
  "views": 1234567,
  "likes": 12345,
  "duration": 212,
  "audioStreams": [
    { "url": "https://...", "format": "M4A", "quality": "128 kbps", "mimeType": "audio/mp4" }
  ],
  "videoStreams": [
    { "url": "https://...", "format": "MPEG-4", "quality": "1080p", "mimeType": "video/mp4" }
  ],
  "relatedStreams": [
    { "url": "/watch?v=xxx", "title": "相关视频", "thumbnail": "https://...", "views": 999 }
  ],
  "subtitles": [
    { "url": "https://...", "mimeType": "text/vtt", "name": "English", "code": "en" }
  ]
}
```

#### 优缺点

| 优点 | 缺点 |
|------|------|
| 完全免费，无需注册 | 依赖公共实例 |
| 音视频流**分离返回**，天然适配 DASH 播放 | 实例可能被 YouTube 限流 |
| 包含相关推荐视频 | 不如 Invidious 接口全面（无 captions 独立端点） |
| 设计支持数千并发 | 部分实例可能下线 |
| 内置 SponsorBlock 集成 | |

---

### 1.3 Bilibili 公开 API（项目已使用）

Bilibili 部分接口无需认证即可调用，本项目已在使用。

- **Base URL**：`https://api.bilibili.com`

#### 项目中已使用的端点

| 端点 | 功能 | 项目中的调用点 |
|------|------|--------------|
| `GET /x/web-interface/dynamic/region?ps=&rid=` | 分区最新视频 | `BilibiliApi.getDynamicRegion()` |
| `GET /x/web-interface/popular/precious` | 每周必看 | `BilibiliApi.getPopularPrecious()` |
| `GET /x/player/playurl?bvid=&cid=&qn=` | 播放地址 | `BilibiliApi.getPlayUrl()` |
| `GET /x/relation/stat?vmid=` | 用户粉丝数 | `BilibiliApi.getFollowers()` |
| `GET /x/web-interface/view/detail/related?bvid=` | 相关推荐 | `BilibiliApi.getRelatedVideo()` |

#### 其他可用的公开端点

| 端点 | 功能 |
|------|------|
| `GET /x/web-interface/popular?ps=&pn=` | 热门视频（分页） |
| `GET /x/web-interface/view?bvid=` | 视频详情（标题/播放量/UP主等） |
| `GET /x/web-interface/ranking/v2?rid=&type=` | 排行榜 |
| `GET /x/web-interface/search/type?keyword=&search_type=video` | 搜索视频 |
| `GET /x/v2/reply?type=1&oid=` | 视频评论 |
| `GET /x/player/videoshot?bvid=&cid=` | 视频缩略图时间轴 |

#### 优缺点

| 优点 | 缺点 |
|------|------|
| 无需认证，直接调用 | 非官方文档，接口可能随时变动 |
| 返回数据丰富（完整视频元信息） | 部分接口需要 Cookie 才能获取高清流 |
| 中文内容，贴近目标用户 | API 文档集合项目已被律师函下架（2026-01） |
| 项目已有完整适配代码 | 存在反爬机制（频率限制、风控） |

---

## 2. 需要 API Key（有免费额度）

### 2.1 YouTube Data API v3

Google 官方提供的 YouTube 数据接口。

- **文档**：https://developers.google.com/youtube/v3
- **认证**：OAuth 2.0 / API Key
- **免费额度**：10,000 units/天

#### 核心端点

| 端点 | 功能 | 配额消耗 |
|------|------|:--------:|
| `GET /youtube/v3/videos?id=` | 视频详情 | 1 unit |
| `GET /youtube/v3/search?q=` | 搜索 | 100 units |
| `GET /youtube/v3/channels?id=` | 频道信息 | 1 unit |
| `GET /youtube/v3/commentThreads?videoId=` | 评论 | 1 unit |
| `GET /youtube/v3/playlists?channelId=` | 播放列表 | 1 unit |
| `GET /youtube/v3/videoCategories` | 视频分类 | 1 unit |

#### 优缺点

| 优点 | 缺点 |
|------|------|
| 官方接口，最稳定可靠 | 需要 Google Cloud 注册 |
| 数据最全面、最准确 | 搜索消耗高（100 units/次），每日上限易耗尽 |
| 完善的文档和 SDK | **不返回视频流地址**（仅元信息） |
| 不会被封锁 | 需要处理 OAuth 认证流程 |

---

### 2.2 Dailymotion API

欧洲视频平台，提供完整的数据 API。

- **文档**：https://developers.dailymotion.com/api/
- **认证**：OAuth 2.0
- **免费额度**：有

#### 核心端点

| 端点 | 功能 |
|------|------|
| `GET /video/:id` | 视频详情（标题、描述、时长、缩略图） |
| `GET /videos?search=` | 搜索视频 |
| `GET /videos?sort=trending` | 热门视频 |
| `GET /channel/:id/videos` | 频道视频列表 |

#### 优缺点

| 优点 | 缺点 |
|------|------|
| 官方 API，稳定 | 需要 OAuth 认证 |
| 有免费额度 | 内容以欧美为主，中文内容少 |
| 返回嵌入播放器 URL | 用户群体较小 |

---

### 2.3 Vimeo API

面向专业创作者的视频平台。

- **文档**：https://developer.vimeo.com/api/reference
- **认证**：OAuth 2.0 / Personal Access Token
- **免费额度**：有（Basic 账户）

#### 核心端点

| 端点 | 功能 |
|------|------|
| `GET /videos/:id` | 视频详情 |
| `GET /videos?query=` | 搜索视频 |
| `GET /channels/:id/videos` | 频道视频 |
| `GET /categories/:category/videos` | 分类视频 |

#### 优缺点

| 优点 | 缺点 |
|------|------|
| 高质量内容，视频画质好 | 需要注册获取 Token |
| API 设计规范（RESTful） | 免费额度有限（500 次/小时） |
| 完善的文档 | 内容偏专业/艺术向，大众娱乐内容少 |

---

## 3. 对比汇总

| API | 认证 | 视频详情 | 搜索 | 热门/趋势 | 视频流地址 | 评论 | 字幕 | 稳定性 |
|-----|:----:|:-------:|:----:|:---------:|:---------:|:----:|:----:|:------:|
| **Invidious** | 无 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | 中 |
| **Piped** | 无 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | 中 |
| **Bilibili** | 无 | ✅ | ✅ | ✅ | ✅* | ✅ | ❌ | 中 |
| **YouTube v3** | API Key | ✅ | ✅ | ✅ | ❌ | ✅ | ✅ | 高 |
| **Dailymotion** | OAuth | ✅ | ✅ | ✅ | 嵌入 | ❌ | ❌ | 高 |
| **Vimeo** | OAuth | ✅ | ✅ | ❌ | ❌ | ✅ | ❌ | 高 |

> \* Bilibili 播放地址部分清晰度需要 Cookie 认证

### 关键能力对比

```
                  详情  搜索  热门  流地址  免认证  中文内容
Invidious          ✅    ✅    ✅    ✅      ✅      ❌
Piped              ✅    ✅    ✅    ✅      ✅      ❌
Bilibili           ✅    ✅    ✅    ✅      ✅      ✅
YouTube v3         ✅    ✅    ✅    ❌      ❌      ❌
Dailymotion        ✅    ✅    ✅    ❌      ❌      ❌
Vimeo              ✅    ✅    ❌    ❌      ❌      ❌
```

---

## 4. 与本项目的适配分析

### 4.1 当前架构

项目使用 Retrofit + Gson 请求 Bilibili API，数据流：

```
BilibiliApi (Retrofit) → VideoRepository → ViewModel → Composable UI
```

### 4.2 扩展建议

若要集成新的视频源，推荐优先级：

| 优先级 | API | 理由 |
|:------:|-----|------|
| 1 | **Piped** | 无需 Key；音视频流分离返回，天然适配项目中 `DashPlayer` 的 `MergingMediaSource`；相关推荐随视频详情一起返回 |
| 2 | **Invidious** | 无需 Key；接口更全面（独立的字幕、搜索建议端点）；多实例可做 fallback |
| 3 | **Bilibili 扩展** | 补充未使用的端点（热门、排行榜、搜索），丰富现有内容 |

### 4.3 集成 Piped/Invidious 的改动评估

```
新增文件：
├── data/remote/api/PipedApi.kt          -- Retrofit 接口定义
├── data/remote/model/PipedVideo.kt      -- 数据模型
├── domain/repository/PipedRepository.kt -- 数据仓库
└── di/PipedModule.kt                    -- Hilt DI 模块（独立 Retrofit 实例）

修改文件：
├── ViewModel 层：增加多数据源切换逻辑
└── UI 层：视频来源标识
```

### 4.4 注意事项

1. **Invidious/Piped 实例稳定性**：公共实例可能下线或被限流，建议内置多个实例地址并支持自动 fallback
2. **Bilibili API 风险**：非官方接口随时可能变更，API 文档集合项目（SocialSisterYi/bilibili-API-collect）已于 2026-01-30 因律师函被归档
3. **混合数据源**：不同 API 返回的数据结构不同，需要在 Repository 层做统一映射
4. **网络访问**：Invidious/Piped 的公共实例部署在海外，国内访问可能需要代理

---

## 5. 参考链接

| 资源 | 地址 |
|------|------|
| Invidious GitHub | https://github.com/iv-org/invidious |
| Invidious API 文档 | https://docs.invidious.io/api/ |
| Piped GitHub | https://github.com/TeamPiped/Piped |
| Piped API 文档 | https://docs.piped.video/docs/api-documentation/ |
| Piped OpenAPI 规范 | https://github.com/TeamPiped/OpenAPI |
| YouTube Data API v3 | https://developers.google.com/youtube/v3 |
| Dailymotion API | https://developers.dailymotion.com/api/ |
| Vimeo API | https://developer.vimeo.com/api/reference |
| Public APIs 合集 | https://github.com/public-apis/public-apis |
| RSSHub（多平台聚合） | https://github.com/DIYgod/RSSHub |
