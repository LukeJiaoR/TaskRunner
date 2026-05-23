package com.generate.taskrunner.schedule.mybatis;

import java.time.Instant;

public class TaskScheduleRow {
    private String id;
    private String taskId;
    private String type;
    private String cronExpression;
    private Long fixedDelayMillis;
    private Instant nextRunAt;
    private Instant createdAt;

    public TaskScheduleRow() {
    }

    public TaskScheduleRow(String id, String taskId, String type, String cronExpression, Long fixedDelayMillis, Instant nextRunAt, Instant createdAt) {
        this.id = id;
        this.taskId = taskId;
        this.type = type;
        this.cronExpression = cronExpression;
        this.fixedDelayMillis = fixedDelayMillis;
        this.nextRunAt = nextRunAt;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getType() {
        return type;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public Long getFixedDelayMillis() {
        return fixedDelayMillis;
    }

    public Instant getNextRunAt() {
        return nextRunAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
