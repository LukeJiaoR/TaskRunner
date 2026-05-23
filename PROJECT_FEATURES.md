# TaskRunner Project Features / 项目功能说明

> This document summarizes the current public-facing capabilities and architecture of TaskRunner.
> For installation and quick start, see `README.md`.
>
> 本文总结 TaskRunner 当前对外可见的能力与架构。
> 安装与快速开始请参见 `README.md`。

## English

### Overview

TaskRunner is a lightweight task runner built with Spring Boot 3.0.1 and Java 17. It lets users define local shell-command tasks, queue asynchronous runs, inspect status and logs, and keep execution history in a small relational data model.

The public surface of the application is REST-based. The runtime is built around a queue worker and a command runner, with persistence handled through H2 and MyBatis.

### What it does

- Create task definitions with a name, command, working directory, and timeout.
- Start asynchronous runs for a task definition.
- Track run state from `QUEUED` to `RUNNING` and then to `SUCCEEDED`, `FAILED`, or `TIMED_OUT`.
- Read execution logs for each run, including `stdout`, `stderr`, and system messages.
- Persist task definitions, task runs, schedules, and logs.
- Use Redis for queueing in the runtime path, while keeping in-memory implementations for tests.
- Maintain scheduling data and process due schedules in a background worker.

### REST API surface

TaskRunner currently exposes these endpoints:

- `GET /health`
- `POST /tasks`
- `GET /tasks/{taskId}`
- `POST /tasks/{taskId}/runs`
- `GET /runs/{runId}`
- `GET /runs/{runId}/logs`

### Architecture at a glance

```text
api/         HTTP controllers and request/response DTOs

domain/      Task, run, log, and schedule domain models

execution/   Queue consumer and local command runner

queue/       Queue abstraction plus Redis and in-memory implementations

repository/  Persistence abstraction plus H2/MyBatis implementations

schedule/    Schedule model, repository, and worker
```

### Runtime flow

1. A client creates a task definition through the API.
2. Starting a run creates a `QUEUED` record and pushes the run ID into the queue.
3. `QueuedTaskWorker` polls the queue, checks concurrency and retry rules, and reloads the task definition.
4. `TaskProcessRunner` executes the command through the local shell, captures output, and updates the run record.
5. Logs are written as the command runs and are later returned by the run-log API.
6. The schedule worker periodically scans due schedules and triggers the same execution path in the background.

### Storage model

The database schema is defined in `src/main/resources/schema.sql` and currently includes these tables:

- `task_definitions`
- `task_runs`
- `task_run_logs`
- `task_schedules`

The persisted run states are:

- `QUEUED`
- `RUNNING`
- `SUCCEEDED`
- `FAILED`
- `TIMED_OUT`

### Configuration

Most runtime knobs are supplied through Spring properties with sensible defaults in code. The main ones are:

- `taskrunner.worker.enabled`
- `taskrunner.worker.poll-delay-ms`
- `taskrunner.worker.max-concurrent-runs`
- `taskrunner.worker.max-attempts`
- `taskrunner.worker.retry-delay-ms`
- `taskrunner.scheduler.enabled`
- `taskrunner.scheduler.poll-delay-ms`
- `taskrunner.queue.redis-key`

`src/main/resources/application.properties` is intentionally minimal; the code provides the default behavior.

### Testing and development

The project uses:

- JUnit 5
- Spring Boot Test
- MockMvc for HTTP-layer tests
- In-memory queue and repository implementations for isolated tests

Test coverage currently focuses on:

- application startup
- health check behavior
- task creation and lookup
- run creation, status lookup, and logs
- persistence behavior
- queue FIFO behavior and bounded capacity
- command execution success, failure, timeout, and retry handling

### Scope notes

- REST and worker-based task execution are the current public-facing features.
- Scheduling exists as background infrastructure backed by persistence and a worker; public API exposure can evolve later.
- Additional dependencies in `build.gradle` are kept for future expansion and are not part of the current public feature surface.

## 中文

### 概述

TaskRunner 是一个基于 Spring Boot 3.0.1 和 Java 17 的轻量级任务运行平台。它允许用户定义本地 shell 命令任务、异步排队执行、查看运行状态与日志，并使用一个简单的关系型数据模型保存执行历史。

这个应用当前对外的主要形态是 REST API。运行时由队列 worker 和命令执行器组成，持久化则通过 H2 和 MyBatis 完成。

### 当前能力

- 创建任务定义，包含名称、命令、工作目录和超时时间。
- 为任务定义启动异步运行。
- 跟踪运行状态，从 `QUEUED` 到 `RUNNING`，再到 `SUCCEEDED`、`FAILED` 或 `TIMED_OUT`。
- 读取每次运行的执行日志，包括 `stdout`、`stderr` 和系统消息。
- 持久化任务定义、运行记录、调度信息和日志。
- 运行时使用 Redis 做队列分发，同时保留内存实现用于测试。
- 保存调度数据，并通过后台 worker 处理到期的调度。

### REST API

当前公开的接口包括：

- `GET /health`
- `POST /tasks`
- `GET /tasks/{taskId}`
- `POST /tasks/{taskId}/runs`
- `GET /runs/{runId}`
- `GET /runs/{runId}/logs`

### 架构概览

```text
api/         HTTP 控制器以及请求/响应 DTO

domain/      任务、运行、日志和调度的领域模型

execution/   队列消费者与本地命令执行器

queue/       队列抽象，以及 Redis / 内存实现

repository/  持久化抽象，以及 H2 / MyBatis 实现

schedule/    调度模型、仓储和 worker
```

### 运行流程

1. 客户端先通过 API 创建任务定义。
2. 启动运行时会先创建一条 `QUEUED` 记录，并把运行 ID 放入队列。
3. `QueuedTaskWorker` 轮询队列，检查并发与重试规则，然后重新加载任务定义。
4. `TaskProcessRunner` 通过本地 shell 执行命令，采集输出，并更新运行记录。
5. 日志会在命令执行过程中持续写入，后续可通过运行日志接口读取。
6. 调度 worker 会定期扫描到期调度，并在后台走同一条执行路径。

### 存储模型

数据库结构定义在 `src/main/resources/schema.sql`，当前包含这些表：

- `task_definitions`
- `task_runs`
- `task_run_logs`
- `task_schedules`

持久化的运行状态包括：

- `QUEUED`
- `RUNNING`
- `SUCCEEDED`
- `FAILED`
- `TIMED_OUT`

### 配置说明

大部分运行参数都通过 Spring 配置属性提供，并在代码里设置了合理默认值。主要包括：

- `taskrunner.worker.enabled`
- `taskrunner.worker.poll-delay-ms`
- `taskrunner.worker.max-concurrent-runs`
- `taskrunner.worker.max-attempts`
- `taskrunner.worker.retry-delay-ms`
- `taskrunner.scheduler.enabled`
- `taskrunner.scheduler.poll-delay-ms`
- `taskrunner.queue.redis-key`

`src/main/resources/application.properties` 保持得很轻量，默认行为主要由代码提供。

### 测试与开发

项目使用：

- JUnit 5
- Spring Boot Test
- MockMvc 做 HTTP 层测试
- 内存队列与内存仓储做隔离测试

当前测试主要覆盖：

- 应用启动
- 健康检查
- 任务创建与查询
- 运行创建、状态查询与日志查询
- 持久化行为
- 队列 FIFO 行为和有界容量
- 命令执行成功、失败、超时和重试处理

### 范围说明

- 目前对外可见的核心能力是 REST 接口和 worker 驱动的任务执行。
- 调度能力已作为后台基础设施存在，依赖持久化和 worker；后续可以再决定是否公开为 API。
- `build.gradle` 里保留了一些未来扩展依赖，但它们不属于当前公开的功能面。
