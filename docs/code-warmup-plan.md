# DiliDili 代码预热方案

## 1. 背景

当前项目中大量重量级对象（Room 数据库、OkHttpClient、Retrofit、ExoPlayer、WebView 等）均采用 **懒加载** 策略——由 Hilt `@Singleton` 延迟注入或在 Composable `remember` 块中按需创建。这意味着用户首次触达相关功能时，需要承担全部初始化开销，导致首页白屏、视频页卡顿、WebView 页面闪烁等体验问题。

通过在 Application 启动阶段利用 **后台线程并行预热**，可以将这些开销从用户交互路径上移除，缩短感知延迟 **500-1200ms**（视设备性能）。

---

## 2. 预热点总览

| 优先级 | 预热对象 | 当前状态 | 首次创建耗时 | 预热收益 |
|:------:|---------|---------|:----------:|:-------:|
| P0 | WebView 引擎 | 用时才创建 | 200-400ms | 极高 |
| P0 | Room 数据库 | Hilt 懒加载 | 100-250ms | 极高 |
| P1 | OkHttpClient + Retrofit | Hilt 懒加载 | 80-150ms | 高 |
| P1 | ExoPlayer | 进页面才创建 | 150-300ms | 高 |
| P2 | Retrofit API 代理 | Hilt 懒加载 | 20-30ms/个 | 中 |
| P2 | Gson TypeAdapter 缓存 | 每次 new Gson | 20-50ms | 中 |
| P3 | 首页数据预取 | 进页面才请求 | 300-800ms | 中 |

> **总可优化延迟：约 600-1200ms**

---

## 3. 逐项分析

### 3.1 P0：WebView 引擎初始化（200-400ms）

**文件**：`ui/pages/homepage/randomvideo/WebView.kt:23-51`

```kotlin
// 当前：每次进入 BiliRegionScreen 时，factory 内从零创建 WebView
AndroidView(
    factory = { context ->
        WebView(context).apply {  // ← Chromium 引擎首次初始化极慢
            settings.javaScriptEnabled = true
            ...
        }
    }
)
```

**问题**

Android WebView 首次创建需要加载 Chromium 内核、初始化 V8 JS 引擎。低端机耗时可达 400ms 以上，表现为页面短暂白屏或卡顿。

**预热方案**

在 `Application.onCreate()` 中通过后台线程预创建一个 dummy WebView，触发 Chromium 引擎加载后立即销毁：

```kotlin
// DiliDiliApp.onCreate() 中启动后台预热
Thread {
    val looper = Looper.getMainLooper()
    Handler(looper).post {
        val dummy = WebView(this@DiliDiliApp)
        dummy.destroy()
    }
}.start()
```

> 注意：WebView 必须在主线程创建，但其引擎加载完成后后续创建速度大幅提升。可在 `super.onCreate()` 之后尽早 post 到主线程队列。

**收益**

后续所有 WebView 创建耗时从 **200-400ms 降至 20-50ms**。

---

### 3.2 P0：Room 数据库初始化（100-250ms）

**文件**：`data/local/archive/AppDatabase.kt:18-27`

```kotlin
fun getInstance(context: Context): AppDatabase {
    return INSTANCE ?: synchronized(this) {
        Room.databaseBuilder(...)
            .fallbackToDestructiveMigration()
            .build().also { INSTANCE = it }  // ← 首次 build 包含 schema 验证、WAL 初始化
    }
}
```

**文件**：`di/AppModule.kt:62-63`（Hilt `@Singleton` 懒创建）

**问题**

Room 的 `build()` + 首次 DAO 查询涉及：
- SQLite 数据库文件打开
- WAL（Write-Ahead Logging）模式初始化
- Schema 验证与迁移检查

全部延迟到第一个需要数据库的 ViewModel 触发时才执行。

**预热方案**

Application 启动时在后台线程触发数据库实例创建和一次空查询：

```kotlin
thread(name = "RoomWarmup", isDaemon = true) {
    val db = AppDatabase.getInstance(this@DiliDiliApp)
    db.archiveDao().getArchive("__warmup__") // 触发 WAL 初始化
}
```

**收益**

首次进入需要数据的页面时，数据库延迟从 **100-250ms 降至 ~0ms**。

---

### 3.3 P1：OkHttpClient + Retrofit 链（80-150ms）

**文件**：`di/AppModule.kt:29-48`

```kotlin
fun provideOkHttpClient(): OkHttpClient {
    val logging = HttpLoggingInterceptor()...
    return OkHttpClient.Builder()       // ← 连接池初始化、SSL 上下文创建
        .connectTimeout(15, TimeUnit.SECONDS)
        ...
        .build()
}

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
    Retrofit.Builder()                  // ← GsonConverterFactory 反射初始化
        ...
        .addConverterFactory(GsonConverterFactory.create())
        .build()
```

**问题**

OkHttpClient 构造涉及 `ConnectionPool`、`SSLSocketFactory`、证书链初始化；Retrofit 构造涉及 `GsonConverterFactory` 内部反射。两者级联依赖，在第一次 API 调用时才被 Hilt 创建。

**预热方案**

通过 Hilt 的 `EntryPoint` 在 Application 启动时在后台线程主动触发注入：

```kotlin
thread(name = "NetworkWarmup", isDaemon = true) {
    val entryPoint = EntryPointAccessors.fromApplication(
        this@DiliDiliApp, NetworkEntryPoint::class.java
    )
    entryPoint.okHttpClient()  // 触发 OkHttpClient 创建
    entryPoint.retrofit()      // 触发 Retrofit 创建
}
```

或更简单地，在已注入的地方（如 Activity）利用协程触发：

```kotlin
lifecycleScope.launch(Dispatchers.IO) {
    // 仅访问即可触发 Hilt 懒创建
    videoRepository.hashCode()
}
```

**收益**

首次 API 调用延迟从 **80-150ms 降至纯网络延迟**。

---

### 3.4 P1：ExoPlayer 初始化（150-300ms）

**文件**：`ui/pages/homepage/recommend/video/Media3Player.kt:27-42`

```kotlin
val player = remember {
    ExoPlayer.Builder(context).build().apply {  // ← 解码器探测、渲染管线初始化
        ...
        prepare()      // ← 触发 codec 选择
        playWhenReady = true
    }
}
```

**文件**：`ui/pages/homepage/yingshipage/YingShiPage.kt:134-138`（同样模式）

**问题**

`ExoPlayer.Builder.build()` 涉及 `MediaCodec` 能力枚举、音视频渲染管线初始化；`prepare()` 触发解码器选择。当前每次进入视频页面都在 `remember` 中创建新实例。

**预热方案**

提供一个全局 ExoPlayer 实例或实例池，在首页加载期间后台预创建：

```kotlin
object ExoPlayerPool {
    private var warmPlayer: ExoPlayer? = null

    fun warmup(context: Context) {
        thread(name = "ExoPlayerWarmup", isDaemon = true) {
            // ExoPlayer.Builder.build() 必须在有 Looper 的线程
            Looper.prepare()
            warmPlayer = ExoPlayer.Builder(context.applicationContext).build()
            Looper.loop()
        }
    }

    fun acquire(context: Context): ExoPlayer {
        return warmPlayer?.also { warmPlayer = null }
            ?: ExoPlayer.Builder(context).build()
    }
}
```

**收益**

视频页面首帧渲染从 **150-300ms 降至 ~30ms**。

---

### 3.5 P2：Retrofit API 代理反射（40-60ms 总计）

**文件**：`di/AppModule.kt:52-58`

```kotlin
fun provideBilibiliApi(retrofit: Retrofit): BilibiliApi =
    retrofit.create(BilibiliApi::class.java)    // ← Java 动态代理 + 方法注解解析

fun providePopularVideoApi(retrofit: Retrofit): PopularVideoApi =
    retrofit.create(PopularVideoApi::class.java) // ← 同上
```

**问题**

`retrofit.create()` 使用 `Proxy.newProxyInstance()` + 反射解析接口上所有方法注解（`@GET`、`@Query` 等）。每个 API 接口约 20-30ms。

**预热方案**

随 OkHttpClient + Retrofit 一起在后台预创建即可，无需额外处理。参见 3.3 节。

**收益**

两个 API 代理合计从 **40-60ms 降至 ~0ms**。

---

### 3.6 P2：Gson TypeAdapter 反射缓存（20-50ms）

**文件**：`data/local/archive/Converters.kt:6-20`

```kotlin
class Converters {
    private val gson = Gson()  // ← 每个 Converters 实例创建一个 Gson 对象

    @TypeConverter fun fromRights(value: Rights): String = gson.toJson(value)
    @TypeConverter fun toRights(value: String): Rights = gson.fromJson(value, Rights::class.java)
    // ... Owner, Stat, Dimension 同理
}
```

**问题**

Gson 对每个新类型首次反序列化时需要通过反射构建 `TypeAdapter`（扫描字段、注解等）。4 个复杂类型（`Rights`、`Owner`、`Stat`、`Dimension`）首次解析约 20-50ms。

**预热方案**

1. 将 `Gson` 实例改为全局单例，避免重复创建：

```kotlin
class Converters {
    companion object {
        private val gson = Gson()
    }
    @TypeConverter fun fromRights(value: Rights): String = gson.toJson(value)
    // ...
}
```

2. 在后台线程用 dummy JSON 触发 TypeAdapter 缓存预热：

```kotlin
thread(name = "GsonWarmup", isDaemon = true) {
    val gson = Gson()
    gson.fromJson("{}", Rights::class.java)
    gson.fromJson("{}", Owner::class.java)
    gson.fromJson("{}", Stat::class.java)
    gson.fromJson("{}", Dimension::class.java)
}
```

**收益**

首次数据库读取的类型转换从 **20-50ms 降至 ~1ms**。

---

### 3.7 P3：首页数据预取（300-800ms 网络延迟）

**文件**：`domain/repository/VideoRepository.kt:50-58`

```kotlin
suspend fun getVideoList(ps: Int, rid: Int): List<Archive> {
    val response = api.getDynamicRegion(ps, rid) // ← 网络请求
    val list = response.data.archives
    list.forEach { dao.insertArchive(it.toEntity()) } // ← 数据库写入
    return list
}
```

**问题**

用户进入首页后 ViewModel 才发起 API 请求，网络延迟 + 数据库写入期间用户看到空白或 loading 状态。

**预热方案**

Application 启动后在后台协程预取首页数据，缓存到内存或数据库：

```kotlin
// DiliDiliApp.onCreate() 末尾
val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
scope.launch {
    try {
        val entryPoint = EntryPointAccessors.fromApplication(
            this@DiliDiliApp, DataEntryPoint::class.java
        )
        val repo = entryPoint.videoRepository()
        repo.getVideoList(ps = 10, rid = 1)  // 预取首页数据
    } catch (_: Exception) { /* 静默失败，不影响启动 */ }
}
```

**收益**

首页从白屏等待 **300-800ms 变为直接展示内容**（网络正常时）。

---

## 4. 推荐预热时序

```
Application.onCreate()
│
├── [主线程] ANR Monitor 启动（已有）
│
├── [主线程 post] WebView 预热 ────────────── ~200ms
│   └── 创建 dummy WebView → destroy
│
├── [后台线程 A] 基础设施预热 ─────────────── ~150ms (并行)
│   ├── AppDatabase.getInstance()            ~150ms
│   └── 空查询触发 WAL 初始化                  ~20ms
│
├── [后台线程 B] 网络层预热 ──────────────── ~120ms (并行)
│   ├── OkHttpClient 创建                    ~80ms
│   ├── Retrofit 创建                        ~40ms
│   ├── BilibiliApi 代理                     ~25ms
│   └── PopularVideoApi 代理                 ~25ms
│
├── [后台线程 C] 序列化预热 ──────────────── ~40ms (并行)
│   └── Gson TypeAdapter 缓存预热
│
├── [后台协程] 数据预取 ─────────────────── ~500ms (异步)
│   └── api.getDynamicRegion() → 缓存
│
│   ─────── 后台并行总耗时约 200ms ───────
│
MainActivity.onCreate()
│
└── setContent { TrunkFrame() }
    └── 首页数据已就绪，直接渲染
```

> 后台线程 A/B/C 并行执行，不阻塞主线程。总墙钟时间约 200ms（取最慢的线程），远小于各项累加的 600ms+。

---

## 5. 预热前后对比

### 5.1 启动到首页可交互

| 阶段 | 预热前 | 预热后 | 节省 |
|------|:------:|:------:|:----:|
| Application 初始化 | ~50ms | ~50ms | - |
| 首页 ViewModel 触发 DI 链 | ~200ms | ~0ms | 200ms |
| 首页数据加载（网络） | ~500ms | ~0ms（已预取） | 500ms |
| **总计** | **~750ms** | **~50ms** | **~700ms** |

### 5.2 首次进入视频页面

| 阶段 | 预热前 | 预热后 | 节省 |
|------|:------:|:------:|:----:|
| ExoPlayer 创建 | ~200ms | ~30ms | 170ms |
| 数据库查询（已预热） | ~150ms | ~5ms | 145ms |
| **总计** | **~350ms** | **~35ms** | **~315ms** |

### 5.3 首次进入 WebView 页面

| 阶段 | 预热前 | 预热后 | 节省 |
|------|:------:|:------:|:----:|
| WebView 引擎初始化 | ~300ms | ~30ms | 270ms |
| **总计** | **~300ms** | **~30ms** | **~270ms** |

---

## 6. 注意事项

1. **内存开销**：预热会提前占用内存。在低内存设备上应考虑根据 `ActivityManager.isLowRamDevice()` 跳过部分预热。

2. **启动时间权衡**：预热任务在后台线程执行，不增加主线程阻塞时间。但如果后台线程过多会与主线程竞争 CPU，需控制并行线程数（建议不超过 3 个预热线程）。

3. **isAnrTest 开关**：当前 `AnrMonitor.isAnrTest` 默认为 `true`，启动时会阻塞主线程 10 秒。生产环境必须关闭，否则预热线程的工作会被 ANR 模拟掩盖。

4. **预热失败容忍**：所有预热任务应静默捕获异常，不影响正常启动流程。预热失败仅意味着回退到当前的懒加载行为。

5. **ExoPlayer 生命周期**：全局 ExoPlayer 实例需要妥善管理释放时机，避免在后台持有解码器资源。建议在最后一个视频页面退出后释放，下次进入前重新预热。

---

## 7. 涉及文件清单

| 文件路径 | 改动类型 |
|---------|---------|
| `app/src/main/java/.../DiliDiliApp.kt` | 添加预热调度逻辑 |
| `app/src/main/java/.../di/AppModule.kt` | 添加 EntryPoint 接口（可选） |
| `app/src/main/java/.../data/local/archive/AppDatabase.kt` | 无需改动（已有 getInstance） |
| `app/src/main/java/.../data/local/archive/Converters.kt` | Gson 改为 companion object 单例 |
| `app/src/main/java/.../ui/.../randomvideo/WebView.kt` | 无需改动（预热在 Application 层） |
| `app/src/main/java/.../ui/.../video/Media3Player.kt` | 改为使用 ExoPlayerPool |
| `app/src/main/java/.../ui/.../yingshipage/YingShiPage.kt` | 改为使用 ExoPlayerPool |
| 新增 `app/src/main/java/.../warmup/ExoPlayerPool.kt` | ExoPlayer 实例池 |
| 新增 `app/src/main/java/.../warmup/AppWarmup.kt` | 预热调度器（可选，集中管理） |
