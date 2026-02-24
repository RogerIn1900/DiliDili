# 代码预热基线数据报告（优化前）

> 采集时间：2026-02-24 22:31 ~ 22:34
> 设备：Android 真机（PID 27569）
> 分支：`feature/code-prewarm` @ `182cea7`
> 采集方式：`adb logcat | Select-String "Warmup-Baseline"`

---

## 1. 启动链路时间线

```
22:31:45.167  Application.onCreate 开始
     │
     ├── +4ms    Application.onCreate 完成（Timber 初始化）
     │
     ├── +50ms   MainActivity.onCreate 开始（含 Hilt 注入）
     │   ├── +38ms   super.onCreate + enableEdgeToEdge
     │   └── +12ms   setContent
     ├── +100ms  MainActivity.onCreate 完成
     │
22:31:46.054  首页 Composable 渲染，触发 Hilt 懒加载 ──┐
     ├── +10ms   Room AppDatabase build                 │
     ├── +28ms   OkHttpClient build                     │ 43ms
     ├── +4ms    Retrofit build                         │
     └── +1ms    BilibiliApi proxy create ──────────────┘
```

**冷启动到首页框架渲染完成：~100ms**
**Hilt 懒加载对象级联创建：~43ms**

---

## 2. 各预热点首次耗时

### 2.1 WebView 引擎（P0）

| 次序 | 时间 | 耗时 | 说明 |
|:----:|------|-----:|------|
| 1 | 22:32:30.765 | **253ms** | 首次创建，加载 Chromium 内核 |
| 2 | 22:32:48.029 | 11ms | 引擎已加载 |
| 3 | 22:32:55.715 | 14ms | |
| 4 | 22:33:03.385 | 10ms | |
| 5 | 22:33:12.474 | 13ms | |
| 6 | 22:33:36.416 | 18ms | |
| 7-10 | 22:33:42.xxx | 8-10ms | 连续快速创建 |
| 11 | 22:34:25.772 | 15ms | |

**结论**：首次 253ms vs 后续平均 12ms，**预热可节省 ~240ms**。

### 2.2 Room 数据库（P0）

| 项目 | 耗时 |
|------|-----:|
| AppDatabase.build() | **10ms** |

**结论**：本设备上 Room 初始化较快，预热收益有限（~10ms）。

### 2.3 OkHttpClient + Retrofit + API 代理（P1）

| 对象 | 耗时 |
|------|-----:|
| OkHttpClient build | **28ms** |
| Retrofit build | 4ms |
| BilibiliApi proxy | 1ms |
| PopularVideoApi proxy | 未触发 |
| **合计** | **33ms+** |

**结论**：OkHttpClient 是网络层初始化的主要开销（28ms），预热可将首次 API 调用提前 ~33ms。

### 2.4 ExoPlayer（P1）

| 次序 | 位置 | 耗时 | 说明 |
|:----:|------|-----:|------|
| 1 | YingShiPage | **59ms** | 首次 build，解码器探测 |
| 2 | YingShiPage | 3ms | Activity 重建后复用 |
| 3 | YingShiPage | 3ms | |
| 4 | YingShiPage | 2ms | |
| 5 | YingShiPage | 3ms | |
| 6 | YingShiPage | 4ms | |

> Media3Player / DashPlayer 未触发（本次未进入在线视频播放页）。

**结论**：首次 59ms vs 后续平均 3ms，**预热可节省 ~56ms**。

### 2.5 Retrofit API 代理反射（P2）

| API 接口 | 耗时 |
|----------|-----:|
| BilibiliApi | 1ms |
| PopularVideoApi | 未触发 |

**结论**：耗时极低（1ms），预热收益可忽略。

### 2.6 Gson TypeAdapter 首次反序列化（P2）

| 类型 | 耗时 |
|------|-----:|
| Rights | 2ms |
| Owner | 1ms |
| Stat | 2ms |
| Dimension | 1ms |
| **合计** | **6ms** |

**结论**：总计 6ms，预热收益较低。

### 2.7 首页数据加载（P3）

| 次序 | 耗时 | 数据量 |
|:----:|-----:|:------:|
| 1 | **176ms** | 10 条 |
| 2 | 148ms | 10 条 |

> PopularVideoViewModel 未触发（本次未进入热门视频页）。

**结论**：首次加载 176ms（含网络 + 数据库写入），预取可让首页直接展示数据。

---

## 3. 汇总排序

按首次耗时从高到低排列：

| 排名 | 预热点 | 首次耗时 | 后续耗时 | 可节省 | 优化优先级 |
|:----:|--------|--------:|--------:|-------:|:---------:|
| 1 | WebView 引擎 | **253ms** | 12ms | **~240ms** | **P0** |
| 2 | 首页数据预取 | **176ms** | - | **~176ms** | **P1** |
| 3 | ExoPlayer | **59ms** | 3ms | **~56ms** | **P1** |
| 4 | OkHttpClient + Retrofit | **33ms** | - | **~33ms** | **P2** |
| 5 | Room 数据库 | **10ms** | - | **~10ms** | **P3** |
| 6 | Gson TypeAdapter | **6ms** | - | **~6ms** | **P3** |
| 7 | API 代理反射 | **1ms** | - | **~1ms** | **P3** |
| | **可优化总计** | | | **~522ms** | |

### 耗时占比

```
WebView 引擎     ████████████████████████████████████████████████  46%
首页数据预取     ██████████████████████████████████               34%
ExoPlayer        ███████████                                      11%
OkHttp+Retrofit  ██████                                            6%
Room 数据库      ██                                                 2%
Gson TypeAdapter █                                                  1%
API 代理反射                                                       <1%
```

---

## 4. 未采集到的打点

以下日志点在本次运行中未触发，需进入对应页面后补充采集：

| 打点 | 原因 |
|------|------|
| `PopularVideoApi proxy create` | 未进入热门视频页，Hilt 未注入 |
| `PopularVideoVM.loadPopularVideo` | 同上 |
| `ExoPlayer build+prepare (Media3Player)` | 未进入在线视频播放页 |
| `ExoPlayer build+prepare (DashPlayer)` | 未进入 Dash 播放页 |

---

## 5. 附录：原始日志数据

```
02-24 22:31:45.175 27569 27569 D DiliDiliApp: [Warmup-Baseline] Application.onCreate done: 4ms
02-24 22:31:45.217 27569 27569 D MainActivity: [Warmup-Baseline] MainActivity.onCreate start (since Application.onCreate: 50ms)
02-24 22:31:45.255 27569 27569 D MainActivity: [Warmup-Baseline] MainActivity before setContent: 38ms
02-24 22:31:45.268 27569 27569 D MainActivity: [Warmup-Baseline] MainActivity.onCreate done: 50ms (total since app: 100ms)
02-24 22:31:46.054 27569 27569 D AppDatabase$Companion: [Warmup-Baseline] Room AppDatabase build: 10ms
02-24 22:31:46.090 27569 27569 D AppModule: [Warmup-Baseline] OkHttpClient build: 28ms
02-24 22:31:46.095 27569 27569 D AppModule: [Warmup-Baseline] Retrofit build: 4ms
02-24 22:31:46.096 27569 27569 D AppModule: [Warmup-Baseline] BilibiliApi proxy create: 1ms
02-24 22:32:30.765 27569 27569 D WebViewKt: [Warmup-Baseline] WebView create + configure: 253ms
02-24 22:32:31.065 27569 27678 D Converters: [Warmup-Baseline] Gson first deserialize Rights: 2ms
02-24 22:32:31.067 27569 27678 D Converters: [Warmup-Baseline] Gson first deserialize Owner: 1ms
02-24 22:32:31.070 27569 27678 D Converters: [Warmup-Baseline] Gson first deserialize Stat: 2ms
02-24 22:32:31.072 27569 27678 D Converters: [Warmup-Baseline] Gson first deserialize Dimension: 1ms
02-24 22:32:48.029 27569 27569 D WebViewKt: [Warmup-Baseline] WebView create + configure: 11ms
02-24 22:32:55.715 27569 27569 D WebViewKt: [Warmup-Baseline] WebView create + configure: 14ms
02-24 22:33:03.385 27569 27569 D WebViewKt: [Warmup-Baseline] WebView create + configure: 10ms
02-24 22:33:08.093 27569 27569 D AnimateVideoViewModel$loadVideos: [Warmup-Baseline] AnimateVideoVM.loadVideos (network+db): 176ms, count=10
02-24 22:33:12.474 27569 27569 D WebViewKt: [Warmup-Baseline] WebView create + configure: 13ms
02-24 22:33:30.782 27569 27569 D AnimateVideoViewModel$loadVideos: [Warmup-Baseline] AnimateVideoVM.loadVideos (network+db): 148ms, count=10
02-24 22:33:36.416 27569 27569 D WebViewKt: [Warmup-Baseline] WebView create + configure: 18ms
02-24 22:33:42.006 27569 27569 D WebViewKt: [Warmup-Baseline] WebView create + configure: 10ms
02-24 22:33:42.030 27569 27569 D WebViewKt: [Warmup-Baseline] WebView create + configure: 9ms
02-24 22:33:42.055 27569 27569 D WebViewKt: [Warmup-Baseline] WebView create + configure: 8ms
02-24 22:33:42.077 27569 27569 D WebViewKt: [Warmup-Baseline] WebView create + configure: 9ms
02-24 22:33:49.165 27569 27569 D YingShiPageKt: [Warmup-Baseline] ExoPlayer.Builder.build (YingShiPage): 59ms
02-24 22:34:08.239 27569 27569 D MainActivity: [Warmup-Baseline] MainActivity.onCreate start (since Application.onCreate: 143072ms)
02-24 22:34:08.250 27569 27569 D MainActivity: [Warmup-Baseline] MainActivity before setContent: 11ms
02-24 22:34:08.251 27569 27569 D MainActivity: [Warmup-Baseline] MainActivity.onCreate done: 11ms (total since app: 143083ms)
02-24 22:34:08.423 27569 27569 D YingShiPageKt: [Warmup-Baseline] ExoPlayer.Builder.build (YingShiPage): 3ms
02-24 22:34:11.780 27569 27569 D MainActivity: [Warmup-Baseline] MainActivity.onCreate start (since Application.onCreate: 146612ms)
02-24 22:34:11.786 27569 27569 D MainActivity: [Warmup-Baseline] MainActivity before setContent: 7ms
02-24 22:34:11.787 27569 27569 D MainActivity: [Warmup-Baseline] MainActivity.onCreate done: 8ms (total since app: 146620ms)
02-24 22:34:11.957 27569 27569 D YingShiPageKt: [Warmup-Baseline] ExoPlayer.Builder.build (YingShiPage): 3ms
02-24 22:34:15.223 27569 27569 D MainActivity: [Warmup-Baseline] MainActivity.onCreate start (since Application.onCreate: 150056ms)
02-24 22:34:15.231 27569 27569 D MainActivity: [Warmup-Baseline] MainActivity before setContent: 7ms
02-24 22:34:15.231 27569 27569 D MainActivity: [Warmup-Baseline] MainActivity.onCreate done: 8ms (total since app: 150064ms)
02-24 22:34:15.360 27569 27569 D YingShiPageKt: [Warmup-Baseline] ExoPlayer.Builder.build (YingShiPage): 2ms
02-24 22:34:17.479 27569 27569 D MainActivity: [Warmup-Baseline] MainActivity.onCreate start (since Application.onCreate: 152312ms)
02-24 22:34:17.486 27569 27569 D MainActivity: [Warmup-Baseline] MainActivity before setContent: 7ms
02-24 22:34:17.487 27569 27569 D MainActivity: [Warmup-Baseline] MainActivity.onCreate done: 8ms (total since app: 152320ms)
02-24 22:34:17.628 27569 27569 D YingShiPageKt: [Warmup-Baseline] ExoPlayer.Builder.build (YingShiPage): 3ms
02-24 22:34:23.185 27569 27569 D YingShiPageKt: [Warmup-Baseline] ExoPlayer.Builder.build (YingShiPage): 4ms
02-24 22:34:25.772 27569 27569 D WebViewKt: [Warmup-Baseline] WebView create + configure: 15ms
```
