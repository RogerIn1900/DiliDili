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

## 学习目标总结
- 熟悉 **现代 Compose 架构**  
- 掌握 **依赖与插件的集中管理**  
- 提升 **构建与运行时性能**  
- 练习 **网络、数据库、状态流与依赖注入** 的综合运用  

