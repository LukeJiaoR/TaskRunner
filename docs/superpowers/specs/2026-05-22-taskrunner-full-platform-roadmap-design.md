# TaskRunner Full Platform Roadmap Design

## Context

TaskRunner is now an MVP Spring Boot task-running platform with REST endpoints for task creation, async local command execution, run status, and logs. The current MVP uses in-memory repositories and executes tasks directly from the API-triggered execution service.

The next goal is to evolve it into a fuller platform while keeping each milestone independently shippable and testable. The accepted roadmap is: persistence first, then Redis queueing, then authentication, then GraphQL, then SOAP.

## Goals

- Preserve the existing REST API behavior while improving internals.
- Add durable storage for task definitions, runs, and logs.
- Decouple run creation from execution through Redis queueing.
- Protect command execution APIs with authentication.
- Add GraphQL and SOAP as secondary API surfaces that reuse the same business services.
- Keep each phase small enough to verify with automated tests and manual API checks.

## Non-Goals

- No UI in this roadmap.
- No distributed worker deployment automation.
- No full multi-user account system in the first auth phase.
- No broad SOAP parity with every REST feature; SOAP is for compatibility use cases.
- No command sandboxing in the persistence phase; sandboxing belongs with production hardening after auth.

## Target Architecture

```text
REST / GraphQL / SOAP / Auth
          ↓
   Task Application Service
          ↓
 Repository + Queue + Worker
          ↓
 H2/MyBatis + Redis + Local Process
```

The application service should become the central boundary for task operations. REST, GraphQL, and SOAP should call the same service instead of duplicating business logic.

## Phase 2: H2/MyBatis Persistence

Replace in-memory repository beans with persistent implementations while keeping repository interfaces stable.

### Data Model

- `task_definitions`
  - `id`
  - `name`
  - `command`
  - `working_directory`
  - `timeout_millis`
  - `created_at`
- `task_runs`
  - `id`
  - `task_id`
  - `status`
  - `created_at`
  - `started_at`
  - `finished_at`
  - `exit_code`
  - `error_message`
- `task_run_logs`
  - `id`
  - `run_id`
  - `timestamp`
  - `stream`
  - `message`

### Behavior

- Existing REST endpoints continue to work.
- A newly created task is stored in H2.
- A run status update is stored in H2.
- Log entries are stored and returned in insertion order.
- Tests should verify persistence through MyBatis-backed repositories.

## Phase 3: Redis Queue

Decouple run creation from process execution.

### Behavior

- Starting a run creates a `QUEUED` run in the database.
- The run id is pushed to Redis.
- A worker consumes run ids and executes them.
- Worker updates status and logs through the same persistent repositories.
- Concurrency should be bounded.

### Boundaries

- Redis is an execution queue, not the source of truth.
- H2/MyBatis remains the source of truth for task and run state.
- If Redis is unavailable, run creation should fail clearly rather than silently losing work.

## Phase 4: API Key Authentication

Protect task APIs before exposing more API surfaces.

### Behavior

- `/health` remains public.
- REST task/run/log endpoints require an API key.
- GraphQL and SOAP must use the same auth rule when they are added.
- API key is configured through application properties or environment.

### Response Rules

- Missing API key: `401 Unauthorized`.
- Wrong API key: `401 Unauthorized`.
- Correct API key: request proceeds.

## Phase 5: GraphQL API

Add GraphQL as a flexible query/mutation layer.

### Schema Scope

Queries:
- task by id
- run by id
- logs by run id

Mutations:
- create task
- start run

### Design Rule

GraphQL resolvers must call the same application service used by REST. They must not access repositories directly except through the service boundary.

## Phase 6: SOAP API

Add SOAP for legacy integration.

### Scope

SOAP operations:
- create task
- start run
- get run status

SOAP does not need to expose every log query or admin capability in the first version.

### Design Rule

SOAP endpoints must call the same application service used by REST and GraphQL.

## Testing Strategy

- Continue TDD for every phase.
- Keep existing REST tests as regression coverage.
- Add repository integration tests for MyBatis persistence.
- Add queue/worker tests for Redis behavior.
- Add security tests for unauthorized and authorized requests.
- Add GraphQL tests for queries and mutations.
- Add SOAP endpoint tests for compatibility operations.

## Documentation Updates

After each phase, update `PROJECT_FEATURES.md` with actual completed capabilities and keep future capabilities clearly marked as roadmap items.

## Safety Notes

TaskRunner executes user-provided shell commands. Until authentication and sandboxing are implemented, the service must remain local/trusted only. After authentication, deployment beyond localhost still requires command sandboxing, allowlists, or worker isolation before production exposure.
