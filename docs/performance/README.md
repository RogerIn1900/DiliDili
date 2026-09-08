# 固定素材性能基线

采样日期：2026-09-08。设备为专用 `ResumeTargetApi36` AVD，Android 16 / API 36、arm64、4 vCPU、约 2 GB RAM、SwiftShader 软件渲染。APK 为非 debuggable 的 benchmark 变体，开启 profileable；Macrobenchmark 使用 `CompilationMode.None()`，每项 3 次。宿主负载和温度未作严格控制。

这是测量流程验证，不是性能验收或前后对比。尤其滚动结果包含明显超时帧，不能据此声称“流畅 60 fps”或“降低卡顿比例”。

| 指标 | 实测结果 | 原始数据 |
|---|---:|---|
| 演示列表冷启动 TTID 中位数 | 1629.46 ms | [Macrobenchmark JSON](macrobenchmark.json) |
| 演示列表滚动帧 CPU 耗时 P50 / P95 | 38.81 / 101.65 ms | [Macrobenchmark JSON](macrobenchmark.json) |
| 帧超时 P50 / P95 | 33.48 / 114.51 ms | [Macrobenchmark JSON](macrobenchmark.json) |
| 设置媒体到渲染首帧 | 175、144、136 ms，中位数 144 ms | [首帧 JSON](first-frame.json) |

冷启动和列表滚动指 `PlaybackDemoActivity` 的 60 项固定演示列表，首帧指 320×180、24 fps、6 秒 H264 无音轨素材。它们不能代表首页网络请求、WebView 在线播放或真实用户视频集合。

原始基线 APK SHA-256：`510bcb2533c4af825f307ef95bfc18210425e1e7ee278d4482ab84ddf774b865`。测量时工作区标记为 `5d5d587-dirty`；对应测量实现随后单独提交为 `2261f92`。此后详情页接线修改不属于本次采样的 APK；没有把数据冒充最终 HEAD 的完整应用性能。

```sh
ANDROID_SERIAL=<device> ./gradlew :benchmark:connectedBenchmarkAndroidTest -Pandroid.testInstrumentationRunnerArguments.androidx.benchmark.suppressErrors=EMULATOR
python3 scripts/measure_first_frame.py --apk app/build/outputs/apk/benchmark/app-benchmark.apk --serial <dedicated-device> --output docs/performance/first-frame.json
```

物理设备测量应删除 `suppressErrors=EMULATOR` 参数。脚本安装并记录被测 APK 哈希，固定三次冷进程选择，缺少渲染回调时直接失败，不用超时值伪装成功。原始 Perfetto traces 保留在本地 `benchmark/build/outputs/connected_android_test_additional_output/`，不将大型 trace 文件纳入 Git。

[官方指标定义](https://developer.android.com/topic/performance/benchmarking/macrobenchmark-metrics)。
