# Skills 使用指南

## 目录
1. [Skills 概述](#skills-概述)
2. [已安装的 Skills 列表](#已安装的-skills-列表)
3. [核心 Skills 使用方法](#核心-skills-使用方法)
4. [使用示例](#使用示例)
5. [配置说明](#配置说明)
6. [常见问题](#常见问题)

---

## Skills 概述

Skills 是 Claude Code 的能力扩展系统，允许 AI Agent 通过结构化的 SKILL.md 文件来学习和执行特定的工作流程。Skills 相当于 AI Agent 的"插件系统"，无需重新训练模型即可扩展功能边界。

### Skills 的优势
- **模块化**: 每个 Skill 都有独立的功能和工具集
- **可复用**: 一次创建，多次回用
- **可共享**: 可以在不同项目间共享
- **结构化**: 基于 Markdown 格式，易于理解和维护

---

## 已安装的 Skills 列表

### Superpowers Skills (13个)
位于: `~/.claude/skills/superpowers/skills/`

| Skill 名称 | 功能描述 |
|-----------|---------|
| **brainstorming** | 头脑风暴和需求澄清 - 在做任何创造性工作前必须使用 |
| **systematic-debugging** | 系统化调试 - 遇到任何 bug 或问题时使用 |
| **writing-plans** | 编写计划 - 将设计文档转化为可执行的任务计划 |
| **executing-plans** | 执行计划 - 按照计划逐步实现功能 |
| **test-driven-development** | 测试驱动开发 - TDD 开发方法论 |
| **subagent-driven-development** | 子代理驱动开发 - 并行分发任务给子代理 |
| **dispatching-parallel-agents** | 并行代理分发 - 管理多个子代理并行工作 |
| **requesting-code-review** | 请求代码审查 - 发起代码审查流程 |
| **receiving-code-review** | 接收代码审查 - 响应审查反馈 |
| **finishing-a-development-branch** | 完成开发分支 - 合并和发布代码 |
| **verification-before-completion** | 完成前验证 - 最终检查和测试 |
| **using-git-worktrees** | 使用 Git Worktrees - 高效管理多分支 |
| **writing-skills** | 编写 Skills - 创建和优化 Skills |
| **using-superpowers** | Superpowers 使用指南 |

### Anthropic 官方 Skills (16个)
位于: `~/.claude/skills/anthropic-skills/skills/`

| Skill 名称 | 功能描述 |
|-----------|---------|
| **skill-creator** | 元技能 - 创建新的 Skills |
| **claude-api** | Claude API 使用指南 - 各语言 SDK |
| **canvas-design** | Canvas 设计 - Web Canvas 绑定 |
| **brand-guidelines** | 品牌指南 - 设计系统规范 |
| **docx** | Word 文档处理 - 创建和编辑 Word 文件 |
| **pdf** | PDF 处理 - 生成和操作 PDF |
| **pptx** | PowerPoint 处理 - 创建演示文稿 |
| **xlsx** | Excel 处理 - 创建和编辑电子表格 |
| **algorithmic-art** | 算法艺术 - 编程生成艺术作品 |
| **doc-coauthoring** | 文档协作 - 多人协作编写文档 |
| **frontend-design** | 前端设计 - Web 界面设计 |
| **internal-comms** | 内部沟通 - 团队协作沟通 |
| **mcp-builder** | MCP 构建器 - Model Context Protocol |
| **slack-gif-creator** | Slack GIF 创建 - 快速制作 GIF |
| **theme-factory** | 主题工厂 - 创建设计主题 |
| **web-artifacts-builder** | Web 产物构建器 - 生成 Web 内容 |
| **webapp-testing** | Web 应用测试 - 测试 Web 应用 |

---

## 核心 Skills 使用方法

### 1. brainstorming (头脑风暴)

**触发条件**: 当你想要创建新功能、构建组件、添加功能或修改行为时。

**使用场景**:
- 开发新功能前
- 设计新组件时
- 规划项目结构时
- 任何创造性工作开始前

**使用方式**:
在 Claude Code 中直接描述你的想法，例如：
```
我想给项目添加一个用户反馈功能
```

Claude 会自动触发 brainstorming skill，引导你完成以下流程：
1. 探索项目上下文
2. 提出澄清问题（一次一个）
3. 提出 2-3 个方案并比较权衡
4. 展示设计方案
5. 编写设计文档
6. 转换为实施计划

**重要原则**:
> 🚫 **硬性规则**: 在展示设计并获得用户批准之前，不要调用任何实现技能、编写任何代码或采取任何实施行动。

---

### 2. systematic-debugging (系统化调试)

**触发条件**: 遇到任何 bug、测试失败或意外行为时。

**使用场景**:
- 测试失败时
- 生产环境 bug
- 意外行为
- 性能问题
- 构建失败
- 集成问题

**使用方式**:
当遇到问题时，直接描述问题：
```
登录功能报错了，错误信息是"undefined is not a function"
```

Claude 会自动触发 systematic-debugging skill，按照四个阶段进行：

**Phase 1: 根因调查** (必须首先完成)
- 仔细阅读错误信息
- 稳定复现问题
- 检查最近的变更
- 收集多组件系统证据

**Phase 2: 假设形成**
- 基于证据形成假设

**Phase 3: 验证假设**
- 测试假设是否正确

**Phase 4: 实施修复**
- 验证根因后进行修复

**重要原则**:
> 🚫 **铁律**: 在完成根因调查之前，不要尝试任何修复。

---

### 3. writing-plans (编写计划)

**触发条件**: brainstorming 完成后，需要将设计转化为具体实施计划时。

**使用场景**:
- brainstorming 完成后
- 需要将复杂任务分解为可执行步骤时
- 规划项目里程碑时

**使用方式**:
brainstorming 完成后，Claude 会自动调用此技能，或者你可以手动触发：
```
帮我把设计方案转化为实施计划
```

**输出内容**:
- 结构化的任务清单
- 任务依赖关系
- 建议的执行顺序
- 预计完成时间

---

### 4. test-driven-development (测试驱动开发)

**触发条件**: 需要使用 TDD 方法开发功能时。

**使用方式**:
```
我想用 TDD 的方式来实现这个功能
```

**工作流程**:
1. 编写失败的测试
2. 编写最小代码使测试通过
3. 重构代码
4. 重复

---

### 5. skill-creator (技能创建器)

**触发条件**: 需要创建新的 Skill 或优化现有 Skill 时。

**使用场景**:
- 从零创建新 Skill
- 编辑和优化现有 Skill
- 运行评估测试 Skill 性能
- 优化 Skill 描述以提高触发准确性

**使用方式**:
```
我想创建一个用于自动生成 API 文档的 Skill
```

**创建流程**:
1. 捕获意图 - 确定 Skill 应实现什么
2. 访谈研究 - 了解边界情况、输入/输出格式
3. 编写 SKILL.md - 编写 Skill 定义文件
4. 创建测试用例 - 验证 Skill 有效性
5. 迭代优化 - 根据反馈改进

---

## 使用示例

### 示例 1: 开发新功能

**用户**: "我想给项目添加一个任务管理功能"

**Claude 响应** (触发 brainstorming):
```
我来帮你头脑风暴一下这个任务管理功能。
首先让我了解一下项目的当前状态...
[探索项目上下文]
```

**后续流程**:
1. Claude 询问澄清问题（一次一个）
2. 提出 2-3 个方案
3. 用户选择方案
4. Claude 编写设计文档
5. 自动调用 writing-plans 转为实施计划
6. 使用 subagent-driven-development 并行开发

### 示例 2: 遇到 Bug

**用户**: "提交按钮点击后没有反应，控制台报错 'Cannot read property value of null'"

**Claude 响应** (触发 systematic-debugging):
```
我来系统性地调试这个问题。
首先让我收集更多信息...
```

**后续流程**:
1. 复现问题
2. 检查相关代码
3. 追踪根因
4. 找到问题根源
5. 修复并验证

### 示例 3: 创建自定义 Skill

**用户**: "我想创建一个自动化代码生成 Skill"

**Claude 响应** (触发 skill-creator):
```
我来帮你创建这个 Skill。
首先需要了解几个问题...
```

**后续流程**:
1. 了解具体需求
2. 设计 Skill 结构
3. 编写 SKILL.md
4. 创建测试用例
5. 迭代优化

---

## 配置说明

### Skills 存储位置

**项目目录** (源代码):
```
/Users/luke/WorkSpace/TaskRunner/superpowers/       # Superpowers 源码
/Users/luke/WorkSpace/TaskRunner/anthropic-skills/ # Anthropic 官方 Skills 源码
```

**Claude Code 全局目录** (符号链接):
```
~/.claude/skills/  # 所有 Skills 的符号链接
```

**Trae IDE 项目目录** (已配置):
```
/Users/luke/WorkSpace/TaskRunner/.trae-cn/skills/  # 本项目可用的 Skills
```

### Trae IDE 配置

**✅ 已完成配置**: Skills 已复制到项目的 `.trae-cn/skills/` 目录。

**Trae 支持的 Skills 列表** (29个):

| Superpowers Skills | Anthropic 官方 Skills |
|-------------------|----------------------|
| brainstorming | skill-creator |
| systematic-debugging | claude-api |
| writing-plans | canvas-design |
| executing-plans | brand-guidelines |
| test-driven-development | docx |
| subagent-driven-development | pdf |
| dispatching-parallel-agents | pptx |
| requesting-code-review | xlsx |
| receiving-code-review | algorithmic-art |
| finishing-a-development-branch | doc-coauthoring |
| verification-before-completion | frontend-design |
| using-git-worktrees | internal-comms |
| using-superpowers | mcp-builder |
| writing-skills | slack-gif-creator |
| | theme-factory |
| | web-artifacts-builder |
| | webapp-testing |

**在 Trae IDE 中使用**:
1. 打开 Trae IDE
2. 打开项目: `/Users/luke/WorkSpace/TaskRunner`
3. Skills 已自动加载，可以在对话中直接使用

**使用示例**:
```
在 Trae 中输入: "我想添加用户反馈功能"
→ Trae 会自动触发 brainstorming skill
```

### 更新 Skills

如果需要更新 Skills，进入对应的源码目录执行 git pull：

```bash
# 更新 Superpowers
cd /Users/luke/WorkSpace/TaskRunner/superpowers
git pull

# 更新 Anthropic Skills
cd /Users/luke/WorkSpace/TaskRunner/anthropic-skills
git pull

# 同步到 Trae 项目目录
cp -r /Users/luke/WorkSpace/TaskRunner/superpowers/skills/* /Users/luke/WorkSpace/TaskRunner/.trae-cn/skills/
cp -r /Users/luke/WorkSpace/TaskRunner/anthropic-skills/skills/* /Users/luke/WorkSpace/TaskRunner/.trae-cn/skills/
```

### 查看所有可用 Skills

在 Claude Code 中，输入 `/` 可以看到所有可用的命令和 Skills。

### Skill 触发机制

Skills 通过以下方式触发：

1. **自动触发**: Claude 根据上下文自动判断
2. **手动触发**: 用户明确请求
3. **命令触发**: 使用 `/skill-name` 命令

---

## 常见问题

### Q: 如何知道哪个 Skill 会被触发？

A: 当你描述一个任务时，Claude 会分析你的意图并自动选择合适的 Skill。你也可以明确要求使用某个 Skill，例如："请使用 brainstorming 来帮我规划这个功能"。

### Q: 可以同时使用多个 Skills 吗？

A: 可以。复杂的任务通常需要多个 Skills 协同工作。例如，开发新功能会经历：brainstorming → writing-plans → executing-plans → requesting-code-review → receiving-code-review → finishing-a-development-branch。

### Q: 如何创建自定义 Skill？

A: 使用 skill-creator Skill。Claude 会引导你完成创建过程，包括：
- 定义 Skill 的目的和触发条件
- 编写 SKILL.md 文件
- 创建测试用例
- 迭代优化

### Q: Skills 对所有项目都适用吗？

A: Superpowers 的 Skills 是通用设计，适用于各种项目。Anthropic 官方 Skills 则更偏向特定领域（如文档处理、API 使用等）。

### Q: 如果 Skill 的行为不符合预期怎么办？

A: 可以使用 skill-creator 来优化和调整 Skill 的行为，或者修改 SKILL.md 文件中的描述和指令。

### Q: Skills 支持热更新吗？

A: 修改 SKILL.md 文件后，Claude Code 会自动重新加载该 Skill，无需重启。

---

## 最佳实践

### 1. 开发新功能
**推荐流程**: brainstorming → writing-plans → executing-plans → verification-before-completion

### 2. 修复 Bug
**推荐流程**: systematic-debugging → 修复 → verification-before-completion

### 3. 代码审查
**推荐流程**: requesting-code-review → receiving-code-review → finishing-a-development-branch

### 4. 创建新 Skill
**推荐流程**: skill-creator → 测试 → 迭代优化

---

## 总结

Skills 系统让 Claude Code 拥有了强大的扩展能力。通过合理使用 Skills，你可以：

- ✅ 更高效地完成复杂任务
- ✅ 保证工作质量（通过系统化的流程）
- ✅ 减少遗漏和错误
- ✅ 建立可复用的工作模式

建议从 brainstorming 和 systematic-debugging 开始使用，这是最常用的两个 Skills。
