# ANR 采集与测试方案

针对本项目的五个阶段做 ANR 采集与测试，采用 **WatchDog** 与 **SIGQUIT 捕获** 两种方式。

---

## 一、五个阶段划分

| 阶段 | 含义 | 代码入口 / 关键路径 |
|------|------|---------------------|
| **1. App 启动** | Application、MainActivity、首帧 UI | `DiliDiliApp.onCreate()` → `MainActivity.onCreate()` → `setContent { TrunkFrame() }` |
| **2. 页面跳转** | 导航切换、进入/退出页面 | `RootNavHost`、`navigate(Routes.player(...))`、`popBackStack()`、`BottomBar` 切换 Tab |
| **3. 视频播放** | ExoPlayer 创建、prepare、播放、全屏 | `VideoPlayerScreen2` / `VideoPlayerScreen`、`ExoPlayer.Builder`、`setMediaItem`/`prepare`、全屏 Dialog |
| **4. 图片加载** | Coil 请求与解码、Compose 绘制 | `rememberAsyncImagePainter(...)`、`AsyncImage`（RecommendPage、BiliVideoScreen、RandomVideoPart 等） |
| **5. UI 滑动** | 列表/横向 Pager 滑动与重组 | `LazyColumn`、`HorizontalPager`（HomePage、VideoPlayerScreen2、AnimatePage、RelatedVideo、YingShiPage 等） |

---

## 二、方式一：WatchDog

### 原理

- 后台单独起一个线程，按固定间隔（如 2s）向主线程 `Handler` 投递一个“tick”任务。
- 若在**阈值时间**（如 4s）内主线程没有执行该任务，认为主线程可能卡死，触发一次“疑似 ANR”：
  - 打印主线程当前调用栈到 Logcat。
  - 将当前**阶段** + 主线程栈写入 app 私有目录下的文件（便于拉取分析）。

### 实现位置

- `com.example.dilidiliactivity.anr.AnrMonitor`：单例，负责 WatchDog 线程、主线程 tick、超时判定、写栈与阶段。
- `com.example.dilidiliactivity.anr.AnrStage`：五个阶段 + `IDLE` 的枚举，在入口处调用 `AnrMonitor.setStage(stage)` 打点。

### 使用方式

1. **Application 中**（建议在 `onCreate` 末尾）：
   - `AnrMonitor.setStageFile(File(filesDir, "anr_stage.txt"))`（可选，用于与 SIGQUIT 或 adb 对照）。
   - `AnrMonitor.start()` 启动 WatchDog。
2. 在五个阶段的**入口**调用 `AnrMonitor.setStage(AnrStage.xxx)`，阶段结束可设回 `AnrStage.IDLE`（可选）。
3. 调参（可选）：
   - `AnrMonitor.blockThresholdMs = 4000L`：主线程多少毫秒未响应则判定为卡顿（建议 3s–5s）。
   - `AnrMonitor.pollIntervalMs = 2000L`：WatchDog 轮询间隔。

### 输出

- **Logcat**：`AnrMonitor` 的 TAG，会打印 `ANR WatchDog: main thread blocked (stage=xxx)` 及主线程栈。
- **文件**：`getFilesDir()` 下 `anr_watchdog_{stage}_{timestamp}.txt`，内容为阶段 + 主线程栈；若设置了 `setStageFile`，还会持续写入当前阶段到 `anr_stage.txt`。

---

## 三、方式二：捕获 SIGQUIT 信号

### 背景

- 系统在判定 ANR 时会向进程发送 **SIGQUIT**，并拉取各线程栈（如生成 `/data/anr/traces.txt` 等）。
- 在进程内“捕获”SIGQUIT 需要 **Native 层**（JNI）：在信号处理函数里只能做**异步信号安全**的操作（如 `write()` 写文件），不能调用 malloc、JNI 等。

### 做法（二选一或组合）

#### 方案 A：不接 NDK，仅用 WatchDog + 阶段文件

- 不编译 Native，仅使用 WatchDog。
- 在 `AnrMonitor.setStageFile()` 指向的路径持续写入当前阶段。
- 真实 ANR 发生时，系统会自己 dump traces；你只需在发生 ANR 前后查看该阶段文件（或 WatchDog 生成的 `anr_watchdog_*.txt`），与系统 traces 时间戳对照，即可知道 ANR 发生在哪个阶段。

#### 方案 B：NDK 注册 SIGQUIT，仅写“发生 SIGQUIT”的标记

- 使用本项目中的可选 Native 模块（见下方“可选 Native 实现”）。
- 在 Native 中注册 SIGQUIT 处理函数，在**仅使用 async-signal-safe** 的前提下，向固定路径写一条“SIGQUIT 已发生 + 时间戳”的记录（例如 `anr_sigquit_received.txt`）。
- 分析时：结合系统生成的 traces + 该标记文件 + Java 写入的 `anr_stage.txt`，判断 ANR 发生的阶段与时机。

### 可选 Native 实现（方案 B）

- 代码位置：`app/src/main/cpp/`（`anr_sigquit.c` + `CMakeLists.txt`），Java 封装见 `AnrSigquitNative`。
- 在 Application 中调用 `AnrSigquitNative.install(filesDir)` 后，Native 注册 SIGQUIT handler；当系统发送 SIGQUIT 时，handler 内仅做 async-signal-safe 的 `write("SIGQUIT\n")` 到 `{filesDir}/anr_sigquit_received.txt`，然后恢复默认 handler 并 `raise(SIGQUIT)`，保证系统 ANR 流程仍会执行。
- 阶段信息以 Java 侧 `AnrMonitor.setStageFile()` 写入的 `anr_stage.txt` 为准，与 SIGQUIT 发生时间对照即可。
- 若不使用 NDK：删除 `app/build.gradle.kts` 中的 `externalNativeBuild { cmake { ... } }`，并删除或忽略 `app/src/main/cpp/`，`AnrSigquitNative.install()` 会静默失败，仅用 WatchDog 即可。

---

## 四、五个阶段的打点位置（建议）

| 阶段 | 建议打点位置 |
|------|----------------|
| **App 启动** | `DiliDiliApp.onCreate()` 首行 `setStage(APP_START)`；`MainActivity.onCreate()` 中 `setContent` 前可再设一次；首屏绘制完成后可设回 `IDLE`（可选）。 |
| **页面跳转** | 在 `RootNavHost` 的每个 `composable(...)` 的 Composable 内部首行 `setStage(NAVIGATION)`；或对 `NavController.navigate` / `popBackStack` 做 AOP/包装，在跳转前后设 `NAVIGATION`。 |
| **视频播放** | `VideoPlayerScreen2` / `VideoPlayerScreen` 的 Composable 入口 `setStage(VIDEO_PLAY)`；ExoPlayer `prepare()` 前后、全屏 Dialog 显示前后可保持或再设一次。 |
| **图片加载** | 在使用 `rememberAsyncImagePainter` 的 Composable 内（如 RecommendPage、BiliVideoScreen），在调用前或外层 `LaunchedEffect` 中 `setStage(IMAGE_LOAD)`；若有多处，可只在列表首项或关键大图处打点以减少噪音。 |
| **UI 滑动** | 在 `LazyColumn` / `HorizontalPager` 所在 Composable 内，在列表或 Pager 的 `content` 块入口设 `setStage(UI_SCROLL)`；或通过 `Modifier.scrollable` / 滑动监听在滑动开始设为 `UI_SCROLL`，结束设回 `IDLE`。 |

以上打点已在代码中接入（见各文件的 `AnrMonitor.setStage(...)` 调用）。

---

## 五、测试建议

1. **单独压测每个阶段**  
   - 启动：反复冷启 app，观察 WatchDog 是否在 `app_start` 阶段触发。  
   - 跳转：反复进入/退出播放页、Tab 切换，观察 `navigation`。  
   - 播放：反复进入播放页、切全屏、 Seek，观察 `video_play`。  
   - 图片：在推荐/列表页快速滑动或大量加载图片，观察 `image_load`。  
   - 滑动：在首页、播放页简介等 LazyColumn/HorizontalPager 快速滑动，观察 `ui_scroll`。

2. **人为制造主线程卡顿**  
   在某一阶段内加 `Thread.sleep(5000)` 或重计算，验证 WatchDog 能否在该阶段触发并 dump 出正确栈和阶段。

3. **结合系统 ANR**  
   若使用方案 B，可故意触发一次系统 ANR（如主线程 sleep 10s），然后取：  
   - `/data/anr/` 下 traces、  
   - `anr_sigquit_received.txt`、  
   - `anr_stage.txt` / `anr_watchdog_*.txt`  
   做时间线与阶段对照。

4. **Release 注意**  
   ANR 采集仅建议在 **debug 或内测包** 开启；Release 可通过 `BuildConfig.DEBUG` 或配置开关控制 `AnrMonitor.enabled` 与 `AnrMonitor.start()`，避免长期开 WatchDog 影响体验。

---

## 六、小结

- **WatchDog**：纯 Java/Kotlin，易集成，能直接得到“主线程卡顿时的阶段 + 栈”，适合开发与测试阶段使用。  
- **SIGQUIT 捕获**：需 NDK，用于在系统发 SIGQUIT 时打标记，与系统 traces 和阶段文件对照，适合需要与系统 ANR 行为对齐时的补充手段。  
- 五个阶段通过 `AnrStage` 在各自入口打点，两种方式都会利用“当前阶段”做输出或写文件，便于按阶段分析 ANR。
