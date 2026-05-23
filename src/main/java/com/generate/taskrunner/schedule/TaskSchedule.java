package com.generate.taskrunner.schedule;

import java.time.Duration;
import java.time.Instant;

public record TaskSchedule(
        String id,
        String taskId,
        TaskScheduleType type,
        String cronExpression,
        Duration fixedDelay,
        Instant nextRunAt,
        Instant createdAt
) {
}
