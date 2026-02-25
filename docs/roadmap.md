# DiliDili 项目路线图

## 已完成

### 架构基础
- MVVM + Clean Architecture（UI → Domain → Data）
- Jetpack Compose 全栈 UI
- Hilt 依赖注入
- 三级缓存（内存 → Room → 网络）
- 类型安全导航（sealed class routes）
- Retrofit + OkHttp 统一网络层
- Timber 日志框架

### 性能优化（主线程阻塞 356ms → 22ms，降低 94%）
| 优化项 | 收益 |
|--------|------|
| WebView 引擎预热 | 253ms → 20ms（-92%） |
| OkHttp + Retrofit 预热 | 33ms → 0ms（-100%） |
| ExoPlayer 预热池 | 59ms → 2ms（-97%） |
| Retrofit API 代理预热 | 40-60ms → 0ms（-100%） |
| Room 数据库预热 | 移至后台线程 |
| Gson TypeAdapter 缓存 | 6ms → 5ms + 共享单例 |
| 首页数据预取 | ≤176ms 网络延迟预取 |

### 功能模块
| 模块 | 状态 |
|------|------|
| 首页视频列表（8 个分区 Tab） | ✅ 完成 |
| 视频播放器（ExoPlayer + DASH） | ✅ 完成 |
| WebView 集成 | ✅ 完成 |
| 本地视频播放 | ✅ 完成 |
| 底部导航（5 个 Tab） | ✅ 完成 |
| 搜索/发现 | 部分完成 |

### 工程化
- GitHub Actions CI（构建 + Lint + ktlint + 单元测试）
- .editorconfig 编辑器格式统一
- ktlint Kotlin 代码风格检查
- PR 模板 + CONTRIBUTING.md 开发规范
- 6 个单元测试类（Repository + 4 个 ViewModel）

---

## 接下来要做的事

### P0：稳定性与错误处理

- [ ] **统一错误处理机制**
  - 引入 `Result<T>` / `sealed class AppError` 包装类型
  - 替换散落的 try-catch，统一错误分类（网络/超时/解析/未知）
  - ViewModel 中使用结构化错误状态替代字符串

- [ ] **修复 VideoPlayerScreen TODO**
  - 实现弹幕/小窗功能（`onClick = { /* TODO: 弹幕/小窗 */ }`）

- [ ] **Room 数据库迁移策略**
  - 替换 `fallbackToDestructiveMigration()` 为正式 Migration
  - 避免正式版本升级时丢失用户数据

### P1：测试覆盖

- [ ] **Compose UI 测试**
  - 关键页面的交互测试（首页列表、播放器控件）
  - Navigation 路由跳转验证

- [ ] **数据层测试**
  - Room DAO 查询/事务测试
  - API 拦截器链测试
  - 缓存策略（三级缓存降级逻辑）验证

- [ ] **目标：单元测试从 6 个增加到 25+ 个，覆盖率 > 60%**

### P2：功能增强

- [ ] **Paging3 分页加载**
  - 替换手动分页逻辑，支持无限滚动
  - 集成加载状态指示器（Loading / Error / Empty）

- [ ] **网络状态监听**
  - 添加 `ConnectivityManager` 监听网络变化
  - 离线时展示缓存数据 + 过期标识
  - 恢复网络后自动刷新

- [ ] **搜索功能完善**
  - 本地 + API 搜索
  - 搜索建议和历史记录

- [ ] **图片缓存优化**
  - 配置 Coil 磁盘缓存大小和 LRU 策略
  - 可见区域缩略图预加载

### P3：架构演进

- [ ] **模块化拆分**
  ```
  :app (壳工程)
  ├── :feature-home     (首页、发现、排行)
  ├── :feature-player   (播放器、流媒体)
  ├── :feature-social   (关注、分享、评论)
  ├── :feature-profile  (用户、历史、设置)
  └── :core             (网络、数据库、DI、主题)
  ```
  - 改善增量编译速度
  - 降低模块间耦合

- [ ] **监控与分析**
  - 接入 Firebase Crashlytics 崩溃监控
  - 接入 Firebase Analytics 用户行为分析
  - 关键性能指标埋点（启动耗时、页面加载、帧率）

### P4：体验优化

- [ ] **ExoPlayer 生命周期管理**
  - 完善播放器池的回收机制，防止内存泄漏
  - 后台/前台切换时正确暂停/恢复

- [ ] **自适应码率**
  - 根据网络状况自动选择视频质量

- [ ] **视频下载/离线播放**
  - 本地缓存已观看视频
  - 下载队列管理

---

## 关键指标目标

| 指标 | 当前 | 目标 | 时间 |
|------|------|------|------|
| 启动耗时 | ~100ms | < 50ms | Q2 2026 |
| 首帧渲染 | ~98ms | < 40ms | Q2 2026 |
| 单元测试数 | 6 | 25+ | Q2 2026 |
| 测试覆盖率 | ~10% | 60%+ | Q4 2026 |
| 视频页加载 | ~35ms | < 20ms | Q2 2026 |
