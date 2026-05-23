package com.generate.taskrunner.domain;

import java.time.Instant;

public record TaskRun(
        String id,
        String taskId,
        TaskRunStatus status,
        Instant createdAt,
        Instant startedAt,
        Instant finishedAt,
        Integer exitCode,
        String errorMessage,
        int attempt,
        Instant nextAttemptAt
) {
    public TaskRun(String id,
                   String taskId,
                   TaskRunStatus status,
                   Instant createdAt,
                   Instant startedAt,
                   Instant finishedAt,
                   Integer exitCode,
                   String errorMessage) {
        this(id, taskId, status, createdAt, startedAt, finishedAt, exitCode, errorMessage, 0, null);
    }
}
