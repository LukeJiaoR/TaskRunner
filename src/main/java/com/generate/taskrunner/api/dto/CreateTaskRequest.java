package com.generate.taskrunner.api.dto;

public record CreateTaskRequest(
        String name,
        String command,
        String workingDirectory,
        Long timeoutSeconds
) {
}
