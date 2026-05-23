package com.generate.taskrunner.repository.mybatis;

import java.time.Instant;

public class TaskRunRow {
    private String id;
    private String taskId;
    private String status;
    private Instant createdAt;
    private Instant startedAt;
    private Instant finishedAt;
    private Integer exitCode;
    private String errorMessage;
    private int attempt;
    private Instant nextAttemptAt;

    public TaskRunRow() {
    }

    public TaskRunRow(String id, String taskId, String status, Instant createdAt, Instant startedAt, Instant finishedAt, Integer exitCode, String errorMessage, int attempt, Instant nextAttemptAt) {
        this.id = id;
        this.taskId = taskId;
        this.status = status;
        this.createdAt = createdAt;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.exitCode = exitCode;
        this.errorMessage = errorMessage;
        this.attempt = attempt;
        this.nextAttemptAt = nextAttemptAt;
    }

    public String getId() {
        return id;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public Integer getExitCode() {
        return exitCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getAttempt() {
        return attempt;
    }

    public Instant getNextAttemptAt() {
        return nextAttemptAt;
    }
}
