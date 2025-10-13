# DiliDili

**学习模仿 B 站视频播放模块和整体 App 架构**

---

## 项目简介
DiliDili 是一个 **仿 B 站视频播放板块的练习项目**，主要目标是学习和实践 **现代 Android 架构与优化手段**。  

---

## 技术栈与特性

### 构建 & 依赖管理
- 使用 **Kotlin DSL + Version Catalog** 管理依赖与插件，实现 **单一真相源**，减少冲突  
- Gradle 并行与缓存、构建性能优化  
- **KSP** 支持  

### 架构 & 数据流
- 全 **Jetpack Compose** 架构  
- **Hilt** 依赖注入，支持 ViewModel 注入与 `SavedStateHandle` 协作  
- **Room** 数据库：索引、事务、Paging3 集成  
- **Retrofit + OkHttp**：拦截器链、超时/重试/缓存策略  
- **Coroutines + Flow/StateFlow**：端到端响应式数据流、单向数据流、状态提升  
- **kotlinx-serialization**：多态、默认值处理  

### 优化
- **Baseline Profiles**  
- **R8**：资源/代码压缩、`keep` 规则与可读性平衡  
- **Compose BOM** 管理版本  
- **Compose 可重组优化**：稳定键、不可变状态  
- **Network Security Config** 配置  

---

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

