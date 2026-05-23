package com.generate.taskrunner.api.dto;

import com.generate.taskrunner.domain.TaskRunStatus;

import java.time.Instant;

public record TaskRunResponse(
        String id,
        String taskId,
        TaskRunStatus status,
        Instant createdAt,
        Instant startedAt,
        Instant finishedAt,
        Integer exitCode,
        String errorMessage
) {
}
