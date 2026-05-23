package com.generate.taskrunner.repository.mybatis;

import java.time.Instant;

public class TaskDefinitionRow {
    private String id;
    private String name;
    private String command;
    private String workingDirectory;
    private Long timeoutMillis;
    private Instant createdAt;

    public TaskDefinitionRow() {
    }

    public TaskDefinitionRow(String id, String name, String command, String workingDirectory, Long timeoutMillis, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.command = command;
        this.workingDirectory = workingDirectory;
        this.timeoutMillis = timeoutMillis;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCommand() {
        return command;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public Long getTimeoutMillis() {
        return timeoutMillis;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
