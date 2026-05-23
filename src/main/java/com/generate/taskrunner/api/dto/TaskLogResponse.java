package com.generate.taskrunner.api.dto;

import java.time.Instant;
import java.util.List;

public record TaskLogResponse(
        String runId,
        List<Entry> entries
) {
    public record Entry(
            Instant timestamp,
            String stream,
            String message
    ) {
    }
}
