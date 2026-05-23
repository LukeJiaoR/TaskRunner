# TaskRunner 项目功能总结

## 项目概述

TaskRunner 是一个基于 Spring Boot 3.0.1 和 Java 17 的最小可用任务运行平台。当前版本提供 REST API，用于创建本地命令任务、启动队列化异步执行、查询运行状态，并读取执行日志。

当前实现使用 H2/MyBatis 持久化任务定义、运行记录和日志；任务运行通过队列创建 `QUEUED` 记录，再由 worker 消费并执行本地命令。Redis 队列实现已接入，测试环境使用内存队列避免依赖外部 Redis。尚未提供认证授权、GraphQL、SOAP 或生产级沙箱能力。

## 技术架构

### 核心框架

- **Spring Boot**: 3.0.1
- **Java**: 17
- **构建工具**: Gradle 7.6
- **包名**: `com.generate.taskrunner`

### 当前实际使用的能力

- **Spring MVC**: 提供 REST API。
- **H2 / MyBatis / JDBC**: 持久化任务定义、运行状态和执行日志。
- **Spring Data Redis**: 提供 Redis list 队列实现，用于运行 ID 入队和 worker 消费。
- **JUnit 5 / Spring Boot Test / MockMvc**: 覆盖 API、仓储、队列和执行器行为。
- **Java Process API**: 执行本地 shell 命令。
- **ConcurrentHashMap / CopyOnWriteArrayList / ConcurrentLinkedQueue**: 保留为内存仓储和内存队列的单元测试对照实现。

### 已引入但未作为当前阶段使用的依赖

`build.gradle` 中仍保留 GraphQL、SOAP 等依赖。这些属于后续阶段可选能力，当前核心任务运行流程不依赖它们。

## 当前项目结构

```text
src/main/java/com/generate/taskrunner/
├── TaskRunnerApplication.java
├── api/
│   ├── HealthController.java
│   ├── TaskController.java
│   └── dto/
│       ├── CreateTaskRequest.java
│       ├── TaskLogResponse.java
│       ├── TaskResponse.java
│       └── TaskRunResponse.java
├── domain/
│   ├── TaskDefinition.java
│   ├── TaskLogEntry.java
│   ├── TaskRun.java
│   └── TaskRunStatus.java
├── execution/
│   ├── ProcessTaskExecutorService.java
│   ├── QueuedTaskWorker.java
│   ├── TaskExecutionException.java
│   ├── TaskExecutorService.java
│   └── TaskProcessRunner.java
├── queue/
│   ├── InMemoryTaskRunQueue.java
│   ├── RedisTaskRunQueue.java
│   └── TaskRunQueue.java
├── repository/
│   ├── InMemoryTaskDefinitionRepository.java
│   ├── InMemoryTaskRunRepository.java
│   ├── PersistentTaskDefinitionRepository.java
│   ├── PersistentTaskRunRepository.java
│   ├── TaskDefinitionRepository.java
│   ├── TaskRunRepository.java
│   └── mybatis/
│       ├── TaskDefinitionMapper.java
│       ├── TaskDefinitionRow.java
│       ├── TaskLogRow.java
│       ├── TaskRunMapper.java
│       └── TaskRunRow.java
```

测试结构：

```text
src/test/java/com/generate/taskrunner/
├── TaskRunnerApplicationTests.java
├── api/
│   ├── HealthControllerTest.java
│   └── TaskControllerTest.java
├── execution/
│   ├── ProcessTaskExecutorServiceTest.java
│   ├── QueuedTaskWorkerTest.java
│   └── TaskProcessRunnerTest.java
├── queue/
│   └── InMemoryTaskRunQueueTest.java
├── repository/
│   ├── InMemoryTaskRepositoryTest.java
│   └── PersistentTaskRepositoryTest.java
```

## 已实现功能

### 1. 健康检查

`GET /health`

响应：

```json
{
  "status": "UP"
}
```

### 2. 创建任务定义

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

行为：

- `name` 不能为空。
- `command` 不能为空。
- `timeoutSeconds` 缺省为 60 秒。
- `timeoutSeconds` 必须在 1 到 3600 秒之间。
- 返回 `201 Created` 和任务详情。

### 3. 查询任务定义

`GET /tasks/{taskId}`

行为：

- 存在时返回任务详情。
- 不存在时返回 `404 Not Found`。

### 4. 启动任务运行

`POST /tasks/{taskId}/runs`

行为：

- 为任务创建一次运行实例。
- 将运行记录保存为 `QUEUED`，并把运行 ID 放入任务运行队列。
- worker 从队列消费运行 ID，加载任务定义后异步执行本地 shell 命令。
- 返回 `202 Accepted` 和运行 ID。
- 缺失任务返回 `404 Not Found`。

### 5. 查询运行状态

`GET /runs/{runId}`

运行状态包括：

- `QUEUED`
- `RUNNING`
- `SUCCEEDED`
- `FAILED`
- `TIMED_OUT`

状态流转：

```text
QUEUED -> RUNNING -> SUCCEEDED
QUEUED -> RUNNING -> FAILED
QUEUED -> RUNNING -> TIMED_OUT
```

### 6. 查询运行日志

`GET /runs/{runId}/logs`

日志条目字段：

- `timestamp`
- `stream`: `stdout`、`stderr` 或 `system`
- `message`

### 7. H2/MyBatis 持久化

运行时默认使用 H2/MyBatis 保存：

- 任务定义：`task_definitions`
- 运行记录：`task_runs`
- 运行日志：`task_run_logs`

REST API 行为保持不变；仓储接口仍是 `TaskDefinitionRepository` 和 `TaskRunRepository`。内存仓储不再作为默认 Spring Bean，只保留给直接实例化的单元测试使用。

### 8. 队列化执行

`TaskExecutorService` 创建运行记录后只负责入队；`QueuedTaskWorker` 从队列取出运行 ID，重新加载 `TaskRun` 和 `TaskDefinition`，再交给 `TaskProcessRunner` 执行本地命令。

队列实现：

- 运行时默认使用 `RedisTaskRunQueue`，基于 Redis list 保存待执行运行 ID。
- 测试和局部单元验证使用 `InMemoryTaskRunQueue`，不要求本机启动 Redis。
- `InMemoryTaskRunQueue` 支持有界容量，队列已满时抛出 `TaskRunQueueFullException`。
- worker 通过 `taskrunner.worker.max-concurrent-runs` 限制同时处于 `RUNNING` 的任务数量，达到上限时把运行 ID 放回队列等待后续轮询。
- worker 通过 `taskrunner.worker.max-attempts` 和 `taskrunner.worker.retry-delay-ms` 控制失败/超时运行的最大尝试次数和重试延迟。

## 运行方式

### 启动应用

```bash
./gradlew bootRun
```

默认服务地址：`http://localhost:8080`

### 运行测试

```bash
./gradlew test
```

## API 快速示例

创建任务：

```bash
curl -s -X POST http://localhost:8080/tasks \
  -H 'Content-Type: application/json' \
  -d '{"name":"Say hello","command":"printf hello","timeoutSeconds":10}'
```

启动运行：

```bash
curl -s -X POST http://localhost:8080/tasks/<taskId>/runs
```

查询运行状态：

```bash
curl -s http://localhost:8080/runs/<runId>
```

查询运行日志：

```bash
curl -s http://localhost:8080/runs/<runId>/logs
```

## 测试覆盖

当前测试覆盖：

- Spring 应用上下文加载。
- `/health` 健康检查。
- 任务创建、参数校验、任务查询。
- 任务运行启动、状态查询、日志查询。
- 缺失任务和缺失运行的 `404` 行为。
- 持久化仓储保存、更新、日志顺序读取。
- 内存仓储保存、更新、日志顺序读取。
- 队列抽象、内存队列 FIFO 行为和有界容量行为。
- 本地命令执行成功、失败退出码、超时处理。
- 队列 worker 消费运行 ID、缺失任务失败处理、非排队运行跳过、并发上限控制和失败重试。

## Roadmap

### Phase 1: MVP Core Platform（已完成）

- 创建任务定义。
- 异步启动本地命令任务。
- 查询任务和运行状态。
- 捕获 stdout/stderr/system 日志。
- 使用内存仓储支撑端到端流程。

### Phase 2: Persistence and Operational Safety（部分完成）

- 已完成：使用 H2/MyBatis 持久化任务定义、运行记录和日志。
- 已完成：运行时仓储切换为持久化实现，REST API 行为保持不变。
- 待完成：日志保留策略。
- 待完成：取消运行能力。
- 待完成：Spring Actuator 或等价监控端点。

### Phase 3: Scheduling and Queueing（部分完成）

- 已完成：运行创建与命令执行解耦，运行 ID 先入队再由 worker 消费执行。
- 已完成：提供 Redis list 队列实现。
- 已完成：提供内存队列用于测试，默认测试套件不依赖外部 Redis。
- 已完成：提供内存队列有界容量和 worker 并发上限控制。
- 已完成：提供失败/超时重试策略、最大尝试次数和延迟退避配置。
- 待完成：cron/fixed-delay 调度。

### Phase 4: Product Hardening

- 增加认证授权或 API Key。
- 增加命令执行沙箱和权限隔离。
- 增加结构化日志、指标和 API 文档。
- 在 API 稳定后再考虑 UI。

## 安全说明

当前 MVP 会执行调用方提交的本地 shell 命令，只适合本地可信环境。不要在公网或不可信网络中暴露该服务；如果需要部署到共享环境，必须先增加认证、授权、命令白名单或沙箱隔离。
