# Skills 清单

## 目录
1. [Superpowers Skills](#superpowers-skills-14个)
2. [Anthropic 官方 Skills](#anthropic-官方-skills-17个)
3. [快速索引](#快速索引)

---

## Superpowers Skills (14个)

Superpowers 是一套完整的 AI 软件开发生命周期框架，强制 AI 遵循工程化的开发流程。

| Skill 名称 | 功能描述 |
|-----------|---------|
| **brainstorming** | 🧠 **头脑风暴** - 在做任何创造性工作前必须使用。引导用户澄清需求、探索方案、编写设计文档。**硬性规则：不设计不许写代码。** |
| **systematic-debugging** | 🐛 **系统化调试** - 遇到任何 bug、测试失败或意外行为时使用。按四个阶段（根因调查→假设形成→验证→修复）系统化解决问题。**铁律：找到根因前不许修复。** |
| **writing-plans** | 📋 **编写计划** - 将设计文档转化为可执行的任务清单。分解任务、规划顺序、建立依赖关系。 |
| **executing-plans** | 🚀 **执行计划** - 按照计划逐步实现功能。读取 PLAN.md 并分批次执行任务。 |
| **test-driven-development** | 🧪 **测试驱动开发** - 使用 TDD 方法开发。先写失败测试 → 写最小代码通过测试 → 重构 → 重复。 |
| **subagent-driven-development** | 👥 **子代理驱动开发** - 并行分发任务给多个子代理，提高开发效率。需要配合 dispatching-parallel-agents 使用。 |
| **dispatching-parallel-agents** | 🔀 **并行代理分发** - 管理多个子代理并行工作。协调任务分配、结果汇总、进度跟踪。 |
| **requesting-code-review** | 👀 **请求代码审查** - 发起代码审查流程。准备审查材料、选择审查者、跟踪审查状态。 |
| **receiving-code-review** | ✅ **接收代码审查** - 响应审查反馈。根据审查意见修改代码、与审查者沟通、迭代改进。 |
| **finishing-a-development-branch** | 🎯 **完成开发分支** - 合并和发布代码。执行最终检查、创建 PR、解决冲突、准备发布。 |
| **verification-before-completion** | 🔍 **完成前验证** - 最终检查和测试。验证所有功能正常、测试通过、文档完整。 |
| **using-git-worktrees** | 🌳 **使用 Git Worktrees** - 高效管理多分支。使用 git worktree 并行开发多个功能，避免频繁切换分支。 |
| **writing-skills** | 🛠️ **编写 Skills** - 创建和优化 Skills。定义 Skill 结构、编写 SKILL.md、创建测试用例、迭代优化。 |
| **using-superpowers** | 📖 **Superpowers 使用指南** - 介绍 Superpowers 框架的使用方法和最佳实践。 |

---

## Anthropic 官方 Skills (17个)

Anthropic 官方提供的专业领域 Skills，涵盖文档处理、设计、API开发等多个方面。

### 文档与内容处理 (6个)

| Skill 名称 | 功能描述 |
|-----------|---------|
| **docx** | 📄 **Word 文档处理** - 创建、编辑、格式化 Word 文档。支持添加图片、表格、样式、目录等复杂元素。 |
| **pdf** | 📑 **PDF 处理** - 生成和操作 PDF 文件。合并、分割、旋转页面、添加水印、表单填写等。 |
| **pptx** | 📊 **PowerPoint 处理** - 创建和编辑演示文稿。添加幻灯片、图表、动画效果、演讲者备注。 |
| **xlsx** | 📈 **Excel 处理** - 创建和编辑电子表格。公式计算、数据透视表、图表、条件格式化。 |
| **doc-coauthoring** | 🤝 **文档协作** - 多人协作编写文档。协调多人编辑、跟踪变更、合并冲突。 |
| **internal-comms** | 💬 **内部沟通** - 团队协作沟通。编写状态报告、更新通知、会议记录等内部文档。 |

### 设计与可视化 (5个)

| Skill 名称 | 功能描述 |
|-----------|---------|
| **canvas-design** | 🎨 **Canvas 设计** - 使用 HTML5 Canvas 绑定创建图形和可视化内容。支持动画、交互式图形、像素操作。 |
| **brand-guidelines** | 🎨 **品牌指南** - 应用品牌设计系统规范。确保设计符合品牌标准（颜色、字体、Logo 使用）。 |
| **frontend-design** | 💻 **前端设计** - 设计 Web 界面和 UI 组件。创建 React 组件、HTML/CSS 布局、响应式设计。 |
| **theme-factory** | 🎨 **主题工厂** - 创建设计主题和风格系统。生成配色方案、字体组合、组件样式。 |
| **algorithmic-art** | 🎨 **算法艺术** - 使用代码生成艺术作品。p5.js 绑定、随机性、粒子系统、流场。 |

### 开发工具 (4个)

| Skill 名称 | 功能描述 |
|-----------|---------|
| **skill-creator** | 🏗️ **技能创建器** - 创建新的 Skills。从零构建 Skill、编辑优化现有 Skill、测试评估性能。 |
| **claude-api** | 🔌 **Claude API 使用** - 各语言 SDK 使用指南。Python、JavaScript、Go、Java、PHP、Ruby 等。包含 Managed Agents 开发。 |
| **mcp-builder** | 🔧 **MCP 构建器** - Model Context Protocol 服务器开发。构建 MCP 服务器连接外部服务（Slack、GitHub等）。 |
| **webapp-testing** | 🧪 **Web 应用测试** - 测试 Web 应用功能。使用 Playwright 进行端到端测试、UI 测试、API 测试。 |

### 其他工具 (2个)

| Skill 名称 | 功能描述 |
|-----------|---------|
| **slack-gif-creator** | 🎬 **Slack GIF 创建** - 快速制作优化的 GIF。适用于 Slack 聊天的动画表情、反应 GIF。 |
| **web-artifacts-builder** | 🌐 **Web 产物构建器** - 生成复杂的 Web HTML 产物。React 组件、多组件界面、带状态管理的 Web 应用。 |

---

## 快速索引

### 按用途分类

#### 🏗️ 开发流程类
| Skill | 说明 |
|-------|------|
| brainstorming | 需求澄清 → 设计 |
| writing-plans | 设计 → 任务计划 |
| executing-plans | 执行任务 |
| test-driven-development | TDD 开发 |
| subagent-driven-development | 并行开发 |
| verification-before-completion | 完成前验证 |

#### 🐛 问题解决类
| Skill | 说明 |
|-------|------|
| systematic-debugging | 系统化调试 |
| requesting-code-review | 代码审查 |
| receiving-code-review | 响应审查 |
| finishing-a-development-branch | 分支完成 |

#### 📄 文档处理类
| Skill | 说明 |
|-------|------|
| docx | Word 文档 |
| pdf | PDF 文件 |
| pptx | PowerPoint |
| xlsx | Excel 表格 |
| doc-coauthoring | 文档协作 |
| internal-comms | 内部沟通 |

#### 🎨 设计类
| Skill | 说明 |
|-------|------|
| canvas-design | Canvas 绘图 |
| brand-guidelines | 品牌规范 |
| frontend-design | 前端 UI |
| theme-factory | 主题创建 |
| algorithmic-art | 算法艺术 |

#### 🔧 工具类
| Skill | 说明 |
|-------|------|
| skill-creator | 创建 Skill |
| claude-api | Claude API |
| mcp-builder | MCP 服务器 |
| webapp-testing | Web 测试 |
| slack-gif-creator | GIF 制作 |
| web-artifacts-builder | Web 产物 |

#### 🔀 Git/版本控制
| Skill | 说明 |
|-------|------|
| using-git-worktrees | Git Worktrees |
| writing-skills | Skill 编写 |
| using-superpowers | Superpowers |

### 按使用频率推荐

#### 🌟 日常开发必用 (推荐优先学习)
1. **brainstorming** - 开发任何功能前必用
2. **systematic-debugging** - 遇到问题时必用
3. **test-driven-development** - 提升代码质量
4. **verification-before-completion** - 保证交付质量

#### 🔧 特定场景使用
| 场景 | 推荐 Skill |
|------|-----------|
| 并行开发多个功能 | subagent-driven-development + dispatching-parallel-agents |
| 代码审查流程 | requesting-code-review + receiving-code-review |
| 多分支管理 | using-git-worktrees |
| 处理 Office 文档 | docx / pptx / xlsx |
| 前端界面开发 | frontend-design + webapp-testing |
| 创建新 Skill | skill-creator + writing-skills |
| 构建外部集成 | mcp-builder + claude-api |

---

## 使用建议

### 新手入门路线
1. 先学习 **brainstorming** 和 **systematic-debugging**（最常用）
2. 了解 **test-driven-development** 提升代码质量
3. 学习 **skill-creator** 创建自定义技能

### 完整开发流程示例
```
需求 → brainstorming (澄清) → writing-plans (计划) →
executing-plans (执行) → test-driven-development (测试) →
requesting-code-review (审查) → receiving-code-review (修改) →
verification-before-completion (验收) → finishing-a-development-branch (发布)
```

### 快速参考
- 需要创意工作？→ 用 **brainstorming**
- 遇到 bug？→ 用 **systematic-debugging**
- 写测试？→ 用 **test-driven-development**
- 处理文档？→ 用 **docx/pdf/pptx/xlsx**
- 设计界面？→ 用 **frontend-design**
- 创建工具？→ 用 **skill-creator**
