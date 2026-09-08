# 验证记录

日期：2026-09-08。实现分支：`feat/resume-target-playback`，基线 `f04c244`。

## 已执行

- JDK 17，Gradle 8.13，Android SDK 36。
- `ANDROID_HOME=<sdk> ANDROID_SERIAL=emulator-5556 ./gradlew :app:testDebugUnitTest :app:assembleDebug :app:connectedDebugAndroidTest --console=plain`：成功。JUnit XML 汇总 **39 项单元测试，9 项设备测试，0 失败、0 跳过**。
- `ANDROID_HOME=<sdk> ./gradlew :app:lintDebug --no-daemon --console=plain`：成功。Lint **0 error、234 warning、16 hint**；未关闭检查或新增 baseline，历史警告尚未逐一整改。
- `ANDROID_HOME=<sdk> ANDROID_SERIAL=emulator-5556 ./gradlew :benchmark:connectedBenchmarkAndroidTest -Pandroid.testInstrumentationRunnerArguments.androidx.benchmark.suppressErrors=EMULATOR --console=plain`：成功，**2 项 Macrobenchmark，各 3 次采样**。此基线对应测量时 APK，不冒充最终 HEAD 的完整性能，详见 [性能记录](performance/README.md)。
- `python3 scripts/measure_first_frame.py --adb <sdk>/platform-tools/adb --serial emulator-5556 --apk app/build/outputs/apk/benchmark/app-benchmark.apk --output docs/performance/first-frame.json`：成功，3 次渲染首帧均有真实回调。
- CI YAML 经 `yaml.safe_load` 解析，首帧脚本经 Python AST 解析；`git diff --check` 通过。

设备为专用 ResumeTargetApi36 AVD，Android 16 / API 36。测试覆盖请求乱序、详情离线降级、播放恢复与释放、手势边界、HTTP 契约、Room 事务回滚与 2 → 3 迁移。

## 修复过的验证失败

- 清除不存在的 NavigationGraph Activity 注册后，Manifest Lint 通过。
- Release 变体 stickyHeader 缺少显式实验 API opt-in，补齐后编译通过。
- Macrobenchmark 最初缺少 self-instrumenting 配置，补齐后两项测试通过。
- 多变体任务一起运行时，Lint 引用到 KAPT 正在重建的 benchmark stub，发生 FileNotFound/FIR 缓存错误。最终按 Debug 回归、独立进程 Lint、Benchmark 分步验证通过，没有隐藏失败。
- 原有模拟器磁盘不足导致安装失败，改用专用模拟器后完成验证，没有删除用户原有应用或数据。

## 尚未验证

- 系统杀进程、强制停止、权限被撤销和不同 OEM 的全部组合。已验证 Activity 重建、后台停止/恢复及 SavedStateHandle 数据恢复逻辑。
- 物理设备性能、网络视频首帧、真实弱网及媒体离线下载。网络视频页面仍使用 WebView。
- 列表接口未确认分页游标契约，因此只实现快照刷新，没有声称 Paging 3 / RemoteMediator 已落地。
- GitHub Actions 远程结果尚无：推送预检因缺少 HTTPS 用户凭据失败。配置已提交，但不能写成“远程 CI 已绿”。

本地报告位于 `app/build/reports/tests`、`app/build/reports/androidTests`、`app/build/reports/lint-results-debug.html`；性能原始 JSON 已纳入文档，大型 Perfetto trace 留在构建输出目录。
