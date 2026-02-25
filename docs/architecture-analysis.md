# DiliDili 项目框架结构分析

> 分析日期：2026-02-25
> 分支：`feature/code-prewarm`
> 项目规模：85 个 Kotlin 文件，437KB 源代码

---

## 目录

1. [项目概述](#1-项目概述)
2. [目录结构](#2-目录结构)
3. [架构模式](#3-架构模式)
4. [技术栈](#4-技术栈)
5. [导航结构](#5-导航结构)
6. [数据流向](#6-数据流向)
7. [依赖注入](#7-依赖注入)
8. [网络层](#8-网络层)
9. [本地存储](#9-本地存储)
10. [页面与功能模块](#10-页面与功能模块)
11. [预热与性能优化](#11-预热与性能优化)
12. [构建配置](#12-构建配置)
13. [架构优劣分析](#13-架构优劣分析)

---

## 1. 项目概述

DiliDili 是一个仿 B 站视频播放模块的 Android 学习项目，采用现代 Android 开发最佳实践，核心技术包括 Jetpack Compose、Hilt、Room、Retrofit、Media3 ExoPlayer。

| 维度 | 数据 |
|------|------|
| 编译 SDK | 36 |
| 最低 SDK | 24 (Android 7.0) |
| 语言 | Kotlin 2.2.0 |
| UI 框架 | Jetpack Compose 1.6.0 |
| 架构模式 | MVVM + Clean Architecture |

---

## 2. 目录结构

```
app/src/main/java/com/example/dilidiliactivity/
│
├── DiliDiliApp.kt                          # @HiltAndroidApp 入口，启动预热
├── MainActivity.kt                         # 主 Activity，EdgeToEdge + Compose
│
├── data/                                   # ── 数据层 ──
│   ├── local/
│   │   ├── archive/                        # Room 数据库核心
│   │   │   ├── AppDatabase.kt             # RoomDatabase 单例
│   │   │   ├── ArchiveEntity.kt           # 数据库实体（40+ 字段）
│   │   │   ├── Archive.kt                 # 业务域模型 + 子模型（Rights/Owner/Stat/Dimension）
│   │   │   ├── ArchiveDao.kt             # DAO 接口（insert/query）
│   │   │   └── Converters.kt             # Room TypeConverter（Gson 序列化）
│   │   │
│   │   ├── advertisement/                 # 广告数据模型
│   │   ├── CommentData/                   # 评论数据模型
│   │   ├── DetailsPageData/               # 详情页数据模型
│   │   ├── HomePageData/                  # 首页 Tab 数据模型
│   │   ├── MediaResponse/                 # 媒体响应模型
│   │   ├── PopularPreciousResponse/       # 每周必看响应模型
│   │   ├── PopularVideoData/              # 热门视频数据模型
│   │   ├── RandomVideoData/               # 动态区视频数据模型
│   │   ├── RelatedVideos/                 # 相关视频数据模型
│   │   └── VideoPlayerData/               # 播放器数据模型
│   │       ├── VideoData.kt              # DASH 流信息
│   │       ├── PlayUrlResponse.kt        # 播放 URL 响应
│   │       ├── FansResponse.kt           # 粉丝数据
│   │       └── VideoIntroUiModel.kt      # 视频简介 UI 模型
│   │
│   ├── mapper/                            # 数据映射层
│   │   ├── ArchiveMapper.kt              # Entity ↔ Domain 转换
│   │   └── VideoDetailMapper.kt          # 视频详情映射
│   │
│   └── remote/api/                        # 远程 API 接口
│       ├── BilibiliApi.kt                # 主 API（动态区/播放URL/粉丝/相关视频）
│       └── PopularVideoApi.kt            # 热门视频 API
│
├── domain/                                # ── 领域层 ──
│   └── repository/
│       └── VideoRepository.kt            # 核心仓库（内存缓存→数据库→网络）
│
├── di/                                    # ── 依赖注入 ──
│   └── AppModule.kt                      # Hilt 全局单例配置
│
├── ui/                                    # ── 表现层 ──
│   ├── theme/
│   │   ├── Color.kt                      # 颜色定义
│   │   ├── Type.kt                       # 字体定义
│   │   └── Theme.kt                      # Material3 主题
│   │
│   ├── common/                            # 通用组件
│   │   ├── animatePart/
│   │   │   └── Like.kt                  # 点赞动画
│   │   ├── bottombars/
│   │   │   ├── BottomBar.kt             # 底部导航栏
│   │   │   └── GlobalPageState.kt       # 全局页面状态
│   │   ├── topbars/
│   │   │   ├── HomeTopBar.kt            # 首页顶栏
│   │   │   └── ShopTopBar.kt            # 商城顶栏
│   │   └── myView/
│   │       └── VideoTimelineView.kt     # 视频时间线自定义 View
│   │
│   ├── navigation/                        # 导航系统
│   │   ├── Screen.kt                     # 主路由常量（sealed class）
│   │   ├── TrunkScreen.kt               # 根路由常量
│   │   ├── NavigationGraph.kt           # 二级导航图（5 个主页面）
│   │   ├── NavigationMain.kt            # 根导航图（RootNavHost）
│   │   └── trunkframe/
│   │       ├── TrunkFrame.kt            # 应用根框架
│   │       └── MainFrame.kt             # 带底部导航的主框架
│   │
│   └── pages/                             # 页面模块
│       ├── homepage/                      # 首页模块
│       │   ├── HomePage.kt              # 首页主容器（Tab 切换）
│       │   ├── animatepage/             # 动画区
│       │   │   ├── AnimatePage.kt
│       │   │   └── AnimateVideoViewModel.kt
│       │   ├── popularvideo/            # 热门视频
│       │   │   ├── PopularVideo.kt
│       │   │   └── PopularVideoViewModel.kt
│       │   ├── randomvideo/             # 随机视频（WebView）
│       │   │   ├── RandomVideo.kt
│       │   │   ├── RandomVideoViewModel.kt
│       │   │   └── WebView.kt
│       │   ├── recommend/               # 推荐区
│       │   │   ├── RecommendPage.kt
│       │   │   ├── RandomVideoPart.kt
│       │   │   └── video/
│       │   │       ├── BiliVideoScreen.kt
│       │   │       ├── Media3Player.kt       # ExoPlayer 集成
│       │   │       └── VideoUrlExtractor.kt
│       │   ├── videoplayerpage/          # 视频播放页
│       │   │   ├── VideoPlayerScreen.kt      # 网络视频
│       │   │   ├── VideoPlayerScreen2.kt     # 本地视频
│       │   │   ├── VideoPlayerViewModel.kt
│       │   │   ├── SharedVideoViewModel.kt   # 跨页面共享
│       │   │   ├── VideoPlayerWithCustomTopBar.kt
│       │   │   ├── CommentCard.kt
│       │   │   └── TestVideo.kt
│       │   ├── relatedvideo/            # 相关视频
│       │   │   ├── RelatedVideo.kt
│       │   │   └── RelatedVideoViewModel.kt
│       │   ├── yingshipage/             # 影视区
│       │   │   └── YingShiPage.kt
│       │   └── zuixinpage/              # 最新
│       │       └── ZuixinPage.kt
│       │
│       ├── friendspage/                  # 朋友页
│       │   └── FriendsPage.kt
│       ├── messagepage/                  # 消息页
│       │   └── MessagePage.kt
│       ├── minepage/                     # 我的页
│       │   ├── MinePage.kt
│       │   └── FullScreenPage.kt
│       ├── popularpreciouspage/          # 每周必看
│       │   ├── PopularPreciousPage.kt
│       │   └── PopularPreciousViewModel.kt
│       ├── prepage/                      # 启动页
│       │   ├── AdScreen.kt
│       │   └── SpashScreen.kt
│       └── publishpage/                  # 发布页
│           └── PublishPage.kt
│
└── warmup/                               # ── 预热优化 ──
    ├── AppWarmup.kt                      # 统一调度器
    ├── WebViewWarmup.kt                  # WebView 引擎预热
    ├── ExoPlayerPool.kt                  # ExoPlayer 实例池
    ├── RoomWarmup.kt                     # Room 数据库预热
    ├── GsonWarmup.kt                     # Gson TypeAdapter 预热
    ├── NetworkWarmup.kt                  # 网络层预热（Hilt EntryPoint）
    └── DataPrefetch.kt                   # 首页数据预取
```

---

## 3. 架构模式

### 3.1 MVVM + Clean Architecture（三层架构）

```
┌─────────────────────────────────────────┐
│  UI Layer（表现层）                      │
│  ├── Composable Functions (页面/组件)     │  ← Jetpack Compose 声明式 UI
│  ├── ViewModel (状态管理)                 │  ← StateFlow + Coroutines
│  └── Navigation (路由导航)               │  ← Compose Navigation
├─────────────────────────────────────────┤
│  Domain Layer（领域层）                   │
│  ├── Repository (业务逻辑)               │  ← 三级缓存策略
│  └── Models (业务模型)                   │  ← Archive, Owner, Stat 等
├─────────────────────────────────────────┤
│  Data Layer（数据层）                     │
│  ├── Remote (Retrofit API)              │  ← BilibiliApi, PopularVideoApi
│  ├── Local (Room Database)              │  ← AppDatabase, ArchiveDao
│  └── Mapper (数据转换)                   │  ← Entity ↔ Domain 映射
└─────────────────────────────────────────┘
```

### 3.2 数据流方向

```
单向数据流（Unidirectional Data Flow）

用户操作 → ViewModel → Repository → [缓存/数据库/网络]
                ↓
        StateFlow.emit(新状态)
                ↓
        Compose Recomposition → UI 更新
```

### 3.3 设计特点

| 特点 | 说明 |
|------|------|
| 强分离 | UI → Domain → Data 严格分层，互不直接依赖 |
| 单一职责 | 每个 ViewModel 只管理一个页面的状态 |
| 依赖注入 | Hilt 管理所有对象的创建和生命周期 |
| 响应式 | StateFlow 驱动 UI 自动更新 |
| 三级缓存 | 内存缓存 → 数据库 → 网络（逐级降速） |

---

## 4. 技术栈

### 4.1 核心框架

| 技术 | 版本 | 用途 |
|------|------|------|
| Kotlin | 2.2.0 | 主语言 |
| Jetpack Compose | 1.6.0 | 声明式 UI |
| Material3 | 1.3.0 | 设计组件库 |
| Coroutines | 1.8.0 | 异步并发 |
| Flow / StateFlow | — | 响应式数据流 |

### 4.2 依赖注入与网络

| 技术 | 版本 | 用途 |
|------|------|------|
| Hilt | 2.56.1 | 依赖注入容器 |
| Retrofit | 2.11.0 | REST API 客户端 |
| OkHttp | 4.12.0 | HTTP 拦截器链 |
| Gson | — | JSON 序列化/反序列化 |

### 4.3 数据存储

| 技术 | 版本 | 用途 |
|------|------|------|
| Room | 2.7.2 | 本地 SQLite ORM |
| Room KTX | 2.7.2 | Coroutines 扩展 |

### 4.4 多媒体

| 技术 | 版本 | 用途 |
|------|------|------|
| Media3 ExoPlayer | 1.1.1 | 视频播放引擎 |
| Media3 UI | 1.1.1 | 播放器控件 |
| jsoup | 1.16.1 | HTML 解析 |

### 4.5 UI 与工具

| 技术 | 版本 | 用途 |
|------|------|------|
| Compose Navigation | — | 页面路由 |
| Accompanist Navigation Animation | 0.32.0 | 导航转场动画 |
| Coil | 2.4.0 | 图片加载 |
| Timber | 5.0.1 | 日志框架 |
| kotlinx-serialization | 1.6.3 | Kotlin 序列化 |

---

## 5. 导航结构

### 5.1 三层导航架构

```
TrunkFrame（应用根框架）
│
└── RootNavHost（根导航，带滑动转场动画）
    │
    ├── MainFrame（主框架 = 底部导航栏 + 内容区）
    │   │
    │   └── NavigationGraph（5 个底部 Tab 页面）
    │       ├── HomePage       ← 首页（内含多个子 Tab）
    │       ├── FriendsPage    ← 朋友
    │       ├── PublishPage    ← 发布
    │       ├── MessagePage    ← 消息
    │       └── MinePage       ← 我的
    │
    ├── VideoPlayerScreen      ← 网络视频播放页（覆盖全屏）
    ├── VideoPlayerScreen2     ← 本地视频播放页
    └── FullScreenPage         ← 全屏模式
```

### 5.2 路由定义

**主路由（Screen.kt）**：
```kotlin
sealed class Screen(val route: String, val destination: String) {
    object HomePage      : Screen("HomePage", "首页")
    object FriendsPage   : Screen("FriendsPage", "朋友")
    object PublishPage   : Screen("PublishPage", "发布")
    object MessagePage   : Screen("MessagePage", "消息")
    object MinePage      : Screen("MinePage", "我的")
}
```

**详情页路由（Routes）**：
```kotlin
object Routes {
    const val PLAYER       = "player/{videoId}"
    const val PLAYER_LOCAL = "playerLocal/{videoId}"
    const val FULL_SCREEN  = "FullScreenPage"
}
```

### 5.3 导航转场

- 进入：`slideInHorizontally`（从右向左滑入）
- 退出：`slideOutHorizontally`（向左滑出）
- 参数传递：`backStackEntry.arguments?.getString("videoId")`

---

## 6. 数据流向

### 6.1 三级缓存策略

```
VideoRepository.getVideoDetail(videoId)
    │
    ├─① 内存缓存（mutableMapOf<String, Archive>）
    │   ├─ 命中 → 直接返回                        ⏱ ~0ms
    │   └─ 未命中 ↓
    │
    ├─② 本地数据库（ArchiveDao.getArchive）
    │   ├─ 命中 → 写入内存缓存 → 返回               ⏱ ~5ms
    │   └─ 未命中 ↓
    │
    └─③ 网络请求（BilibiliApi）
        ├─ Retrofit 调用
        ├─ Gson 反序列化
        ├─ 写入数据库（ArchiveDao.insertArchive）
        ├─ 写入内存缓存
        └─ 返回                                    ⏱ ~176ms
```

### 6.2 首页加载完整链路

```
用户进入首页
    ↓
AnimatePage → LaunchedEffect(Unit) { viewModel.loadVideos() }
    ↓
AnimateVideoViewModel.loadVideos(ps=10, rid=1)
    ↓ viewModelScope.launch
VideoRepository.getVideoList(ps, rid)
    ↓
BilibiliApi.getDynamicRegion(10, 1)    ← Retrofit 网络请求
    ↓
response.data.archives: List<Archive>
    ↓
dao.insertArchive() × N               ← Room 批量写入
    ↓
return List<Archive>
    ↓
_allVideos.value = videos              ← StateFlow 更新
    ↓
Compose Recomposition                  ← UI 自动重绘
    ↓
用户看到视频列表
```

### 6.3 视频播放完整链路

```
用户点击视频卡片
    ↓
navController.navigate("player/$bvid")
    ↓
VideoPlayerScreen(videoId = bvid)
    ↓
VideoPlayerViewModel.loadVideo(videoId)
    ├─ repository.getVideoDetail(videoId)    ← 三级缓存查询
    └─ repository.getPlayUrl(cid, bvid)      ← 获取播放地址
        ↓
BilibiliApi.getPlayUrl(cid, bvid, qn=80)
    ↓
PlayUrlResponse.data.durl[0].url
    ↓
ExoPlayerPool.acquire(context)               ← 从预热池获取播放器
    ↓
player.setMediaItem(url) → prepare() → play
    ↓
用户看到视频播放
```

---

## 7. 依赖注入

### 7.1 Hilt 全局配置（AppModule.kt）

```
AppModule (@Module @InstallIn(SingletonComponent))
│
├── provideOkHttpClient()          → OkHttpClient @Singleton
│   └── 超时: 15s, 拦截器: HttpLoggingInterceptor(BASIC)
│
├── provideRetrofit(okHttp)        → Retrofit @Singleton
│   └── baseUrl: https://api.bilibili.com/
│   └── converter: GsonConverterFactory
│
├── provideBilibiliApi(retrofit)   → BilibiliApi @Singleton
├── providePopularVideoApi(retrofit) → PopularVideoApi @Singleton
│
├── provideAppDatabase(context)    → AppDatabase @Singleton
├── provideArchiveDao(db)          → ArchiveDao @Singleton
│
└── provideVideoRepository(dao, api) → VideoRepository @Singleton
```

### 7.2 依赖关系图

```
OkHttpClient ──→ Retrofit ──→ BilibiliApi ──┐
                           ──→ PopularVideoApi │
                                              ├──→ VideoRepository
AppDatabase ──→ ArchiveDao ───────────────────┘
```

### 7.3 ViewModel 注入

```kotlin
@HiltViewModel
class AnimateVideoViewModel @Inject constructor(
    private val repo: VideoRepository    // Hilt 自动注入
) : ViewModel()

// Composable 中使用
val viewModel: AnimateVideoViewModel = hiltViewModel()
```

### 7.4 预热模块的 EntryPoint 模式

```kotlin
// 在非 Hilt 管理的类中访问 Hilt 单例
@EntryPoint
@InstallIn(SingletonComponent::class)
interface NetworkEntryPoint {
    fun okHttpClient(): OkHttpClient
    fun retrofit(): Retrofit
    fun bilibiliApi(): BilibiliApi
    fun popularVideoApi(): PopularVideoApi
}

// 使用方式
val ep = EntryPointAccessors.fromApplication(context, NetworkEntryPoint::class.java)
ep.okHttpClient()  // 触发单例创建
```

---

## 8. 网络层

### 8.1 BilibiliApi 接口定义

| 方法 | 端点 | 参数 | 返回 |
|------|------|------|------|
| `getDynamicRegion` | `GET /x/web-interface/dynamic/region` | `ps`（页大小）, `rid`（分区 ID） | `DynamicRegionResponse` |
| `getPlayUrl` | `GET /x/player/playurl` | `cid`, `bvid`, `qn`(默认80=1080p) | `Response<PlayUrlResponse>` |
| `getPopularPrecious` | `GET /x/web-interface/popular/precious` | — | `PopularPreciousResponse` |
| `getFollowers` | `GET /x/relation/stat` | `vmid`（用户 ID） | `FansResponse` |
| `getRelatedVideo` | `GET /x/web-interface/archive/related` | `aid`, `bvid` | `RelatedVideosResponse` |

### 8.2 PopularVideoApi 接口定义

| 方法 | 端点 | 返回 |
|------|------|------|
| `getPopularVideo` | `GET /x/web-interface/popular/precious` | `PopularVideoData` |

### 8.3 网络配置

| 配置项 | 值 |
|--------|-----|
| Base URL | `https://api.bilibili.com/` |
| 连接超时 | 15 秒 |
| 读取超时 | 15 秒 |
| 写入超时 | 15 秒 |
| 日志级别 | BASIC |
| 序列化 | Gson（GsonConverterFactory） |

---

## 9. 本地存储

### 9.1 Room 数据库配置

```kotlin
@Database(
    entities = [ArchiveEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase()
```

- 数据库名：`archive_table`
- 版本：2
- 迁移策略：`fallbackToDestructiveMigration()`（开发模式，版本变更时清空数据）
- 单例模式：双重检查锁（Double-checked Locking）

### 9.2 ArchiveEntity 主要字段

| 字段 | 类型 | 说明 |
|------|------|------|
| `bvid` | String (PK) | 视频 BV 号（主键） |
| `aid` | Long | 视频 AID |
| `cid` | Long | 分 P CID |
| `title` | String | 标题 |
| `pic` | String | 封面 URL |
| `desc` | String | 描述 |
| `duration` | Int | 时长（秒） |
| `owner` | Owner | 发布者（TypeConverter） |
| `stat` | Stat | 统计数据（TypeConverter） |
| `rights` | Rights | 权限信息（TypeConverter） |
| `dimension` | Dimension | 分辨率（TypeConverter） |
| ... | ... | 共 40+ 字段 |

### 9.3 DAO 操作

```kotlin
@Dao
interface ArchiveDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArchive(archive: ArchiveEntity)

    @Query("SELECT * FROM archive_table WHERE bvid = :bvid")
    suspend fun getArchive(bvid: String): ArchiveEntity?

    @Query("SELECT * FROM archive_table WHERE bvid = :bvid LIMIT 1")
    suspend fun getArchiveByBvid(bvid: String): ArchiveEntity?
}
```

### 9.4 TypeConverter（复杂类型序列化）

`Converters.kt` 使用 `GsonWarmup.sharedGson`（全局共享单例）对 4 个复杂类型进行 JSON 序列化/反序列化：

| 类型 | 说明 |
|------|------|
| `Rights` | 视频权限标志（是否允许下载、弹幕等） |
| `Owner` | 发布者信息（mid、name、face） |
| `Stat` | 统计数据（播放、弹幕、点赞、投币等） |
| `Dimension` | 视频分辨率（width、height、rotate） |

### 9.5 数据映射

```
ArchiveEntity (数据库层)  ←→  Archive (业务层)
     │                              │
     │  .toDomain()                 │  .toEntity()
     └──────────────────────────────┘
```

- `ArchiveEntity.toDomain()` — 数据库读取后转为业务模型
- `Archive.toEntity()` — 网络数据写入数据库前转为实体

---

## 10. 页面与功能模块

### 10.1 页面层级

```
底部导航（5 个 Tab）
├── 首页 (HomePage)
│   ├── 动画区 (AnimatePage)         ── 视频列表 + 下拉刷新
│   ├── 推荐区 (RecommendPage)       ── 推荐视频 + WebView
│   ├── 影视区 (YingShiPage)         ── 内置/本地视频播放
│   └── 最新 (ZuixinPage)           ── 最新视频
│
├── 朋友 (FriendsPage)
├── 发布 (PublishPage)
├── 消息 (MessagePage)
└── 我的 (MinePage)

独立页面（从首页跳转）
├── 视频播放 (VideoPlayerScreen)      ── 网络视频 + 评论 + 相关推荐
├── 本地播放 (VideoPlayerScreen2)     ── 本地视频播放
├── 每周必看 (PopularPreciousPage)    ── 精选视频列表
└── 全屏播放 (FullScreenPage)
```

### 10.2 ViewModel 清单

| ViewModel | 管理页面 | 数据操作 | 数据源 |
|-----------|---------|---------|--------|
| `AnimateVideoViewModel` | 动画区 | 加载/刷新视频列表 | BilibiliApi + Room |
| `PopularVideoViewModel` | 热门视频 | 加载热门视频 | PopularVideoApi |
| `RandomVideoViewModel` | 随机视频 | 构建 WebView URL | BilibiliApi |
| `VideoPlayerViewModel` | 播放页 | 加载视频详情 + 播放 URL | Room + BilibiliApi |
| `SharedVideoViewModel` | 跨页面 | 共享当前视频信息 | 内存状态 |
| `RelatedVideoViewModel` | 相关视频 | 加载推荐列表 | BilibiliApi + Room |
| `PopularPreciousViewModel` | 每周必看 | 加载精选视频 | BilibiliApi + Room |

### 10.3 多媒体播放

项目中有三种视频播放方式：

| 方式 | 组件 | 使用场景 |
|------|------|---------|
| **Media3Player** | ExoPlayer + MergingMediaSource | 网络视频（音视频分离 DASH） |
| **DashPlayer** | ExoPlayer + MergingMediaSource | 网络视频（简化版） |
| **YingShiPage ExoPlayer** | ExoPlayer（单源） | 内置/本地视频播放 |
| **WebView** | Android WebView | B 站嵌入式播放器 iframe |

---

## 11. 预热与性能优化

### 11.1 预热调度时序

```
Application.onCreate()  [T=0ms]
│
├── AppWarmup.start(context)
│   │
│   ├── [主线程 post] WebViewWarmup
│   │   └── 创建 dummy WebView → destroy → Chromium 引擎已加载
│   │       基线: 253ms → 优化后: 20ms (-92%)
│   │
│   ├── [主线程 post] ExoPlayerPool
│   │   └── ExoPlayer.Builder.build() → 存入池中
│   │       基线: 59ms → 优化后: 2ms (-97%)
│   │
│   ├── [后台线程 Warmup-Infra]  ─────── 并行 ───────┐
│   │   ├── RoomWarmup: getInstance + runInTransaction │
│   │   │   基线: 10ms (主线程) → 移至后台            │
│   │   └── GsonWarmup: dummy fromJson × 4 类型       │
│   │       基线: 6ms → 优化后: 5ms                    │
│   │                                                  │
│   └── [后台线程 Warmup-Network] ─────── 并行 ───────┘
│       ├── NetworkWarmup: OkHttp + Retrofit + API 代理
│       │   基线: 33ms (主线程) → 移至后台
│       └── DataPrefetch: 首页数据预取（协程）
│           基线: 176ms → 用户进首页时数据已就绪
│
└── MainActivity.onCreate()  [T≈50ms]
    └── setContent { TrunkFrame() }
```

### 11.2 优化效果汇总

| 预热点 | 优化前 | 优化后 | 节省 | 降幅 |
|--------|------:|------:|-----:|-----:|
| WebView 引擎 | 253ms (主线程) | 20ms (post) | 233ms | 92% |
| ExoPlayer | 59ms (主线程) | 2ms (池获取) | 57ms | 97% |
| OkHttp+Retrofit | 33ms (主线程) | 0ms (后台) | 33ms | 100%* |
| Room 数据库 | 10ms (主线程) | 0ms (后台) | 10ms | 100%* |
| Gson TypeAdapter | 6ms (后台) | 5ms (后台) | 1ms | 17% |
| 首页数据预取 | 176ms (用户等待) | 0ms (已预取) | ≤176ms | ≤100% |
| **主线程阻塞合计** | **356ms** | **~22ms** | **~334ms** | |

> \* 100% 指主线程阻塞降为 0ms（耗时移至后台线程）

---

## 12. 构建配置

### 12.1 核心配置

```kotlin
android {
    compileSdk = 36
    defaultConfig {
        minSdk = 24
        targetSdk = 36
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }
    buildFeatures { compose = true }
}
```

### 12.2 AndroidManifest 关键配置

```xml
<uses-permission android:name="android.permission.INTERNET" />

<application
    android:name=".DiliDiliApp"
    android:usesCleartextTraffic="true">
    <activity android:name=".MainActivity" android:exported="true">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
</application>
```

---

## 13. 架构优劣分析

### 13.1 优势

| 维度 | 说明 |
|------|------|
| **分层清晰** | UI → Domain → Data 严格分离，各层职责单一 |
| **响应式** | StateFlow + Coroutines 实现单向数据流，UI 自动响应状态变化 |
| **类型安全** | Kotlin + Hilt + sealed class 路由，编译期发现错误 |
| **性能优化** | 三级缓存 + 7 项预热，冷启动快，交互无卡顿 |
| **模块独立** | 各功能模块高度解耦，ViewModel 独立管理各自页面状态 |
| **最小侵入** | 预热系统不修改核心业务代码，仅在入口和消费端少量改动 |
| **失败安全** | 所有预热模块独立 try-catch，失败不影响正常流程 |

### 13.2 可改进方向

| 维度 | 现状 | 建议 |
|------|------|------|
| **分页加载** | 手动管理分页逻辑 | 引入 Paging3，支持无限滚动和边界回调 |
| **错误处理** | 各 ViewModel 独立 try-catch | 封装统一 Result/UiState 包装类 |
| **离线模式** | 仅缓存已浏览的视频 | Repository 层增加"优先本地"策略 |
| **测试覆盖** | 有少量 Repository/ViewModel 单元测试 | 补充 UI 测试（Compose Testing） |
| **多模块** | 单模块 app | 按功能拆分 feature module，加速编译 |
| **图片缓存** | Coil 默认策略 | 配置磁盘缓存大小和预加载策略 |
| **数据库迁移** | `fallbackToDestructiveMigration` | 生产环境应使用正式迁移策略 |

### 13.3 关键文件索引

| 功能 | 文件 |
|------|------|
| 应用入口 | `DiliDiliApp.kt` |
| 主 Activity | `MainActivity.kt` |
| 依赖注入 | `di/AppModule.kt` |
| 核心仓库 | `domain/repository/VideoRepository.kt` |
| API 接口 | `data/remote/api/BilibiliApi.kt` |
| 数据库 | `data/local/archive/AppDatabase.kt` |
| 数据实体 | `data/local/archive/ArchiveEntity.kt` |
| 业务模型 | `data/local/archive/Archive.kt` |
| 路由定义 | `ui/navigation/Screen.kt` |
| 根导航 | `ui/navigation/NavigationMain.kt` |
| 首页 | `ui/pages/homepage/HomePage.kt` |
| 视频播放 | `ui/pages/homepage/videoplayerpage/VideoPlayerScreen.kt` |
| ExoPlayer | `ui/pages/homepage/recommend/video/Media3Player.kt` |
| 预热调度 | `warmup/AppWarmup.kt` |
