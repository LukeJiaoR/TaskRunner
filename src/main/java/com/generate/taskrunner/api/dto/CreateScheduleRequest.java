package com.generate.taskrunner.api.dto;

public record CreateScheduleRequest(
        String type,
        String cronExpression,
        Long fixedDelaySeconds
) {
}
