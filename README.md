# TaskRunner

A lightweight task runner built with Spring Boot 3.0.1 and Java 17. It exposes REST APIs to create task definitions, start task runs, inspect run status and logs, and manage task schedules.

## English

### Overview

TaskRunner is a local command execution platform for defining tasks, queueing runs, and tracking execution history. It persists task definitions, runs, schedules, and logs with H2 and MyBatis, and executes commands through Java's Process API.

The application currently provides:

- Health check endpoint
- Create and fetch task definitions
- Start task runs for a task definition
- Fetch run status
- Fetch execution logs
- Create and inspect task schedules
- In-memory implementations for tests and local development
- Redis-backed queue implementation for asynchronous run processing

### Features

- **Task definitions**: store command, working directory, and timeout.
- **Task runs**: queue execution, track `QUEUED`, `RUNNING`, `SUCCEEDED`, and `FAILED` states, and record exit codes and errors.
- **Execution logs**: read stdout/stderr entries for each run.
- **Schedules**: create cron-based or fixed-delay schedules for a task.
- **Persistence**: H2 schema and MyBatis mappers for task definitions, runs, schedules, and logs.
- **Queueing**: Redis queue implementation for run dispatch, plus in-memory queue support for tests.

### Tech Stack

- Java 17
- Spring Boot 3.0.1
- Spring Web / Spring MVC
- Spring Scheduling
- Spring Data Redis
- MyBatis
- H2 Database
- Gradle 7.6
- JUnit 5 + Spring Boot Test

### API

#### Health

`GET /health`

Response:

```json
{ "status": "UP" }
```

#### Create a task

`POST /tasks`

Request:

```json
{
  "name": "Say hello",
  "command": "printf hello",
  "workingDirectory": null,
  "timeoutSeconds": 10
}
```

Response fields:

- `id`
- `name`
- `command`
- `workingDirectory`
- `timeoutSeconds`
- `createdAt`

#### Get a task

`GET /tasks/{taskId}`

#### Start a run

`POST /tasks/{taskId}/runs`

Creates a queued run for the task and returns the run record.

#### Get a run

`GET /runs/{runId}`

#### Get run logs

`GET /runs/{runId}/logs`

Returns ordered log entries with timestamp, stream, and message.

#### Scheduling support

Task scheduling is implemented in the background worker and persisted in the `task_schedules` table.

The scheduler currently supports:

- `CRON`
- `FIXED_DELAY`

Use `CreateScheduleRequest` and `TaskScheduleResponse` as the request/response shapes for schedule-related code paths in the application.

### Project Structure

```text
src/main/java/com/generate/taskrunner/
├── TaskRunnerApplication.java
├── api/
│   ├── HealthController.java
│   ├── TaskController.java
│   └── dto/
├── domain/
├── execution/
├── queue/
├── repository/
└── schedule/
```

```text
src/test/java/com/generate/taskrunner/
├── api/
├── execution/
├── queue/
├── repository/
└── schedule/
```

### Configuration

Main configuration lives in `src/main/resources/application.properties`.

The database schema is defined in `src/main/resources/schema.sql`.

### Build and Run

#### Prerequisites

- Java 17
- A Unix-like shell or Windows terminal
- Optional: Redis if you want to exercise the Redis queue implementation

#### Build

```bash
./gradlew build
```

#### Run tests

```bash
./gradlew test
```

#### Run the application

```bash
./gradlew bootRun
```

The app starts on the default Spring Boot port unless overridden in configuration.

### Notes

- `HELP.md` is the default Spring initializr guide and is kept for reference.
- `PROJECT_FEATURES.md` documents the current implementation in more detail.
- Some dependencies in `build.gradle` are present for future expansion, but the current core flow is the REST task runner described above.

### Contributing

1. Create a feature branch.
2. Make changes with tests where appropriate.
3. Run `./gradlew test`.
4. Open a pull request with a clear summary and validation notes.

---

## 中文

### 项目简介

TaskRunner 是一个基于 Spring Boot 3.0.1 和 Java 17 的轻量级任务运行平台。它提供 REST API，用于创建任务、启动执行、查看运行状态和日志，以及管理定时调度。

当前项目支持：

- 健康检查接口
- 创建和查询任务定义
- 为任务启动一次运行
- 查询运行状态
- 查询执行日志
- 创建和查看任务调度
- 测试和本地开发可用的内存实现
- 支持异步任务分发的 Redis 队列实现

### 功能说明

- **任务定义**：保存命令、工作目录和超时时间。
- **任务运行**：将执行放入队列，跟踪 `QUEUED`、`RUNNING`、`SUCCEEDED`、`FAILED` 状态，并记录退出码与错误信息。
- **执行日志**：按时间读取每次运行的 stdout/stderr 日志。
- **任务调度**：支持基于 cron 或固定延迟的调度。
- **持久化**：使用 H2 + MyBatis 持久化任务定义、运行记录、调度和日志。
- **队列**：提供 Redis 队列用于运行分发，也提供内存队列用于测试。

### 技术栈

- Java 17
- Spring Boot 3.0.1
- Spring Web / Spring MVC
- Spring Scheduling
- Spring Data Redis
- MyBatis
- H2 Database
- Gradle 7.6
- JUnit 5 + Spring Boot Test

### API

#### 健康检查

`GET /health`

响应：

```json
{ "status": "UP" }
```

#### 创建任务

`POST /tasks`

请求示例：

```json
{
  "name": "Say hello",
  "command": "printf hello",
  "workingDirectory": null,
  "timeoutSeconds": 10
}
```

返回字段：

- `id`
- `name`
- `command`
- `workingDirectory`
- `timeoutSeconds`
- `createdAt`

#### 查询任务

`GET /tasks/{taskId}`

#### 启动运行

`POST /tasks/{taskId}/runs`

会为该任务创建一个排队中的运行记录并返回运行对象。

#### 查询运行

`GET /runs/{runId}`

#### 查询运行日志

`GET /runs/{runId}/logs`

按时间顺序返回日志条目，包含时间戳、流类型和消息内容。

#### 调度支持

任务调度由后台 worker 处理，并持久化到 `task_schedules` 表中。

当前支持的调度类型：

- `CRON`
- `FIXED_DELAY`

应用内的调度相关请求/响应结构由 `CreateScheduleRequest` 和 `TaskScheduleResponse` 定义。

### 项目结构

```text
src/main/java/com/generate/taskrunner/
├── TaskRunnerApplication.java
├── api/
│   ├── HealthController.java
│   ├── TaskController.java
│   └── dto/
├── domain/
├── execution/
├── queue/
├── repository/
└── schedule/
```

```text
src/test/java/com/generate/taskrunner/
├── api/
├── execution/
├── queue/
├── repository/
└── schedule/
```

### 配置说明

主要配置位于 `src/main/resources/application.properties`。

数据库结构定义在 `src/main/resources/schema.sql`。

### 构建与运行

#### 前置要求

- Java 17
- 类 Unix Shell 或 Windows 终端
- 可选：如果需要体验 Redis 队列实现，准备 Redis 服务

#### 构建项目

```bash
./gradlew build
```

#### 运行测试

```bash
./gradlew test
```

#### 启动应用

```bash
./gradlew bootRun
```

如果没有在配置中修改，应用会使用 Spring Boot 默认端口启动。

### 说明

- `HELP.md` 是 Spring Initializr 默认生成的入门说明，保留作参考。
- `PROJECT_FEATURES.md` 对当前实现做了更详细的功能总结。
- `build.gradle` 中保留了一些未来扩展依赖，但当前核心流程就是上面描述的 REST 任务运行平台。

### 参与贡献

1. 新建 feature 分支。
2. 修改代码并尽量补充测试。
3. 运行 `./gradlew test`。
4. 提交 PR，并说明变更内容和验证方式。
