# DiliDili

Kotlin / Jetpack Compose 视频浏览、本地播放与离线列表实践。

本分支将优化落实为可测试的播放会话、Room 事务刷新和固定素材性能测量。实现与验证入口见 [工程说明](docs/resume-target.md)。

## 已实现

- Media3 本地播放：统一播放会话管理创建、前后台释放、媒体选择、进度及暂停状态保存；页面内嵌和全屏复用实例。
- 可测试手势：方向锁定、进度边界、取消不提交 Seek、临时倍速恢复。
- Room 列表快照：数据库作为读取来源，原子替换分区排序，去重、失败保留旧数据和刷新合并。
- Retrofit 请求契约、乱序请求隔离、Room 事务/迁移与 Compose 生命周期回归测试。
- 独立 Macrobenchmark 模块及视频首帧采样脚本。

## 能力边界

网络播放页面仍使用 WebView；离线能力指列表数据，不包含视频下载。当前列表接口未确认分页游标契约，因此没有接入 Paging 3 / RemoteMediator。没有声称 Baseline Profile 收益、ANR 降幅或启动提升比例。Room 使用 KAPT，构建依赖仍有历史重复声明。

## 构建与验证

需要 JDK 17、Android SDK 36，并设置 `ANDROID_HOME`。

```sh
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
ANDROID_SERIAL=<test-device> ./gradlew :app:connectedDebugAndroidTest
```

启动 App 后，首页的“本地播放体验”可进入不依赖网络接口的固定素材演示。

## 基本功能展示

### 热门视频列表
<img width="360" alt="DiliDili阶段一01" src="https://github.com/user-attachments/assets/396f27c2-e11f-4bb0-9d57-94e3e053ba03" />

### ▶视频播放页面
<img width="360" alt="DiliDili阶段一02" src="https://github.com/user-attachments/assets/0a638568-de40-4c08-8e90-0e3cce44dd2f" />

### 按钮动态点击  
https://github.com/user-attachments/assets/4ef44e44-8955-4c46-b1b0-dd6e43eaa4e5  

### 视频播放演示  
https://github.com/user-attachments/assets/ccb3b5fd-e89a-4073-acee-4975ad48b112  

---

# 项目结构特点

##  架构模式
- **单模块项目**：所有代码在 `:app` 模块内  
- **分层架构**：`data → domain → ui` 三层分离  
- **MVVM 模式**：`ViewModel + StateFlow` 管理状态  

---

## 包结构
- **原有业务代码**：`com.example.dilidiliactivity.*`  
- **新增核心代码**：`com.example.dilidili.core.*`  
- **分层清晰**：数据层、领域层、表现层分离  

---

## 功能模块
- **首页功能**：`homePage/` 包含多个子功能  
- **视频播放**：`VideoPlayerPage/` 完整的播放功能  
- **导航系统**：`navigation/` 统一的路由管理  
- **通用组件**：`common/` 可复用的 UI 组件  

---

##  技术栈
- **UI 框架**：Jetpack Compose  
- **依赖注入**：Hilt  
- **数据库**：Room  
- **网络**：Retrofit + OkHttp  
- **视频播放**：Media3 ExoPlayer  
- **图片加载**：Coil  

---

## 总结
本目录结构展示了一个 **Android 单模块项目**，采用了 **现代 Android 开发的最佳实践**，代码组织清晰，便于维护和扩展。

---

## 学习目标总结
- 熟悉 **现代 Compose 架构**  
- 掌握 **依赖与插件的集中管理**  
- 提升 **构建与运行时性能**  
- 练习 **网络、数据库、状态流与依赖注入** 的综合运用  

