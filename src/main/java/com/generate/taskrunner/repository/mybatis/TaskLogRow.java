package com.generate.taskrunner.repository.mybatis;

import java.time.Instant;

public class TaskLogRow {
    private Long id;
    private String runId;
    private Instant timestamp;
    private String stream;
    private String message;

    public TaskLogRow() {
    }

    public TaskLogRow(Long id, String runId, Instant timestamp, String stream, String message) {
        this.id = id;
        this.runId = runId;
        this.timestamp = timestamp;
        this.stream = stream;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public String getRunId() {
        return runId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getStream() {
        return stream;
    }

    public String getMessage() {
        return message;
    }
}
