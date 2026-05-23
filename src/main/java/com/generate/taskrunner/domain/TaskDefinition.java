package com.generate.taskrunner.domain;

import java.time.Duration;
import java.time.Instant;

public record TaskDefinition(
        String id,
        String name,
        String command,
        String workingDirectory,
        Duration timeout,
        Instant createdAt
) {
}
