package com.generate.taskrunner.domain;

import java.time.Instant;

public record TaskLogEntry(
        String runId,
        Instant timestamp,
        String stream,
        String message
) {
}
