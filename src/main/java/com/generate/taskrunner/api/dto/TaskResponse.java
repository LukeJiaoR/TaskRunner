package com.generate.taskrunner.api.dto;

import java.time.Instant;

public record TaskResponse(
        String id,
        String name,
        String command,
        String workingDirectory,
        long timeoutSeconds,
        Instant createdAt
) {
}
