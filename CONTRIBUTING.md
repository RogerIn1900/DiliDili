# Contributing Guide

## Branch Strategy

```
main          ← 稳定发布分支，保护分支
  └── develop ← 开发集成分支（可选）
       ├── feature/*  ← 新功能
       ├── fix/*       ← Bug 修复
       ├── perf/*      ← 性能优化
       └── refactor/*  ← 重构
```

### 分支命名规范

| 前缀 | 用途 | 示例 |
|------|------|------|
| `feature/` | 新功能开发 | `feature/video-player` |
| `fix/` | Bug 修复 | `fix/crash-on-launch` |
| `perf/` | 性能优化 | `perf/startup-warmup` |
| `refactor/` | 代码重构 | `refactor/network-layer` |
| `hotfix/` | 紧急线上修复 | `hotfix/api-timeout` |

## Commit Convention

使用 [Conventional Commits](https://www.conventionalcommits.org/) 规范：

```
<type>(<scope>): <description>

[optional body]
```

### Type

| Type | 说明 |
|------|------|
| `feat` | 新功能 |
| `fix` | Bug 修复 |
| `perf` | 性能优化 |
| `refactor` | 重构（不影响功能） |
| `docs` | 文档更新 |
| `test` | 测试相关 |
| `chore` | 构建/工具链 |
| `style` | 代码格式（不影响逻辑） |

### 示例

```
feat(player): 添加画中画模式支持
fix(home): 修复首页列表滚动卡顿
perf(warmup): 预热 WebView 引擎减少首次加载耗时
docs: 添加性能优化报告
```

## Development Workflow

1. 从 `main` 创建功能分支
   ```bash
   git checkout main
   git pull origin main
   git checkout -b feature/your-feature
   ```

2. 开发并提交
   ```bash
   git add <files>
   git commit -m "feat(scope): description"
   ```

3. 推送并创建 PR
   ```bash
   git push -u origin feature/your-feature
   # 在 GitHub 上创建 Pull Request → main
   ```

4. PR 要求
   - CI 检查全部通过（构建、Lint、测试）
   - 填写 PR 模板
   - 至少自测通过

## Code Style

- Kotlin 代码风格遵循 [Kotlin Official Style](https://kotlinlang.org/docs/coding-conventions.html)
- 使用 ktlint 自动检查：`./gradlew ktlintCheck`
- 自动格式化：`./gradlew ktlintFormat`
- 最大行宽：120 字符
