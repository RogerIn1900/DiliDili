# 预热点 2-7 优化报告：全量预热实现

> 优化项：剩余 6 个预热点一次性实现
> 日期：2026-02-25
> 分支：`feature/code-prewarm`
> 前置：`warmup-1-webview-report.md`（WebView 引擎预热，已完成）

---

## 1. 优化内容总览

本次一次性实现了计划中剩余的全部 6 个预热点，并引入统一的 `AppWarmup` 调度器集中管理所有预热任务。

| # | 预热点 | 基线耗时 | 预热方式 | 预期收益 |
|:-:|--------|--------:|---------|---------|
| 2 | 首页数据预取 | 176ms | 后台协程预取 API 数据 | 首页直接展示 |
| 3 | ExoPlayer | 59ms | 主线程 post 预创建实例池 | 首次 build ~0ms |
| 4 | OkHttpClient + Retrofit | 33ms | 后台线程触发 Hilt 单例 | 首次 API 调用 ~0ms |
| 5 | Room 数据库 | 10ms | 后台线程 getInstance + 空事务 | DB 延迟 ~0ms |
| 6 | Gson TypeAdapter | 6ms | 共享单例 + dummy 反序列化 | 首次解析 ~0ms |
| 7 | API 代理反射 | ~1ms | 随网络层一起触发 | ~0ms |

---

## 2. 架构设计

### 2.1 统一调度器 `AppWarmup`

```
Application.onCreate()
│
├── AppWarmup.start(context)
│   │
│   ├── [主线程 post] WebViewWarmup        ── Chromium 引擎加载
│   ├── [主线程 post] ExoPlayerPool        ── 解码器探测 + 渲染管线
│   │
│   ├── [后台线程 Warmup-Infra]
│   │   ├── RoomWarmup                     ── DB 实例 + WAL 初始化
│   │   └── GsonWarmup                     ── TypeAdapter 反射缓存
│   │
│   └── [后台线程 Warmup-Network]
│       ├── NetworkWarmup                  ── OkHttp + Retrofit + API 代理
│       └── DataPrefetch                   ── 首页数据预取（依赖网络层）
│
└── Timber.d("AppWarmup scheduled in: Xms")
```

**设计要点**：
- 主线程任务用 `Handler.post`，排到 `onCreate` 之后执行
- 后台线程 A（基础设施）和 B（网络层）并行执行
- 数据预取串行在网络层之后（依赖 Retrofit 已创建）
- 所有预热失败静默捕获，不影响正常启动

### 2.2 Hilt EntryPoint 模式

网络层和数据预取通过 `@EntryPoint` 接口访问 Hilt 管理的单例，无需修改现有 DI 模块：

```kotlin
@EntryPoint
@InstallIn(SingletonComponent::class)
interface NetworkEntryPoint {
    fun okHttpClient(): OkHttpClient
    fun retrofit(): Retrofit
    fun bilibiliApi(): BilibiliApi
    fun popularVideoApi(): PopularVideoApi
}
```

### 2.3 ExoPlayer 实例池

```kotlin
object ExoPlayerPool {
    @Volatile
    private var warmPlayer: ExoPlayer? = null

    fun warmup(context: Context)           // post 到主线程预创建
    fun acquire(context: Context): ExoPlayer  // 取走预热实例或新建
}
```

- 池中仅维护 1 个实例（首次进入视频页最关键）
- `acquire()` 取走后池为空，后续调用正常 `new`
- ExoPlayer 必须在主线程创建（Android 限制）

### 2.4 Gson 共享单例

将 `Converters` 中每次 `new Gson()` 改为引用 `GsonWarmup.sharedGson`，预热时通过 dummy JSON 触发 4 个类型的 `TypeAdapter` 构建。

---

## 3. 修改文件清单

| 文件 | 变更类型 | 说明 |
|------|---------|------|
| `warmup/AppWarmup.kt` | **新增** | 统一预热调度器 |
| `warmup/NetworkWarmup.kt` | **新增** | OkHttp + Retrofit + API 代理预热 |
| `warmup/RoomWarmup.kt` | **新增** | Room 数据库预热 |
| `warmup/GsonWarmup.kt` | **新增** | Gson TypeAdapter 缓存预热 + 共享实例 |
| `warmup/ExoPlayerPool.kt` | **新增** | ExoPlayer 实例池 |
| `warmup/DataPrefetch.kt` | **新增** | 首页数据预取 |
| `DiliDiliApp.kt` | **修改** | 用 `AppWarmup.start()` 替换单独的 `WebViewWarmup` 调用 |
| `data/local/archive/Converters.kt` | **修改** | Gson 改为引用 `GsonWarmup.sharedGson` |
| `ui/.../video/Media3Player.kt` | **修改** | ExoPlayer 创建改为 `ExoPlayerPool.acquire()` |
| `ui/.../yingshipage/YingShiPage.kt` | **修改** | ExoPlayer 创建改为 `ExoPlayerPool.acquire()` |

---

## 4. 预期效果

### 4.1 完整预热前后对比

| 场景 | 预热前总延迟 | 预热后总延迟 | 节省 |
|------|----------:|----------:|-----:|
| 启动 → 首页可交互 | ~750ms | ~50ms | **~700ms** |
| 首次进入视频页面 | ~350ms | ~35ms | **~315ms** |
| 首次进入 WebView 页面 | ~300ms | ~30ms | **~270ms** |

### 4.2 各预热点预期收益

| 预热点 | 基线耗时 | 预热后 | 节省 |
|--------|--------:|------:|-----:|
| WebView 引擎 | 253ms | 20ms | **233ms** (已验证) |
| 首页数据预取 | 176ms | ~0ms | **~176ms** |
| ExoPlayer | 59ms | ~5ms | **~54ms** |
| OkHttpClient + Retrofit | 33ms | ~0ms | **~33ms** |
| Room 数据库 | 10ms | ~0ms | **~10ms** |
| Gson TypeAdapter | 6ms | ~0ms | **~6ms** |
| API 代理反射 | 1ms | ~0ms | **~1ms** |
| **合计** | **538ms** | **~25ms** | **~513ms** |

### 4.3 启动开销

预热任务本身的调度开销极小（`AppWarmup.start()` 仅创建 2 个后台线程 + 2 个 `Handler.post`），预计 < 5ms。实际预热工作在后台并行执行，不阻塞主线程。

---

## 5. 注意事项

1. **首次运行需实机验证**：以上为基于基线数据的预期值，需实机 logcat 采集 `[Warmup]` 标签日志确认
2. **ExoPlayer 内存**：预热池持有一个未使用的 ExoPlayer 实例，约占 2-5MB 内存，在 `acquire()` 后即释放
3. **数据预取失败容忍**：网络不可用时预取静默失败，用户进入首页后正常走 ViewModel 加载路径
4. **线程数控制**：仅使用 2 个后台线程（Warmup-Infra + Warmup-Network），不会与主线程竞争 CPU

---

## 6. 下一步

- [ ] 实机部署，采集 `[Warmup]` 日志验证各预热点实际耗时
- [ ] 对比启动链路各阶段耗时变化
- [ ] 确认无 ANR、内存泄漏等副作用
- [ ] 考虑低内存设备跳过部分预热（`ActivityManager.isLowRamDevice()`）
