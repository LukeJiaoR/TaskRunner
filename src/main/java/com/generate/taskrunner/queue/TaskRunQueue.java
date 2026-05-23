package com.generate.taskrunner.queue;

import java.util.Optional;

public interface TaskRunQueue {
    void enqueue(String runId);

    Optional<String> dequeue();
}
