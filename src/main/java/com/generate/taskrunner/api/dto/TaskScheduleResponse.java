package com.generate.taskrunner.api.dto;

import com.generate.taskrunner.schedule.TaskScheduleType;

import java.time.Instant;

public record TaskScheduleResponse(
        String id,
        String taskId,
        TaskScheduleType type,
        String cronExpression,
        Long fixedDelaySeconds,
        Instant nextRunAt,
        Instant createdAt
) {
}
