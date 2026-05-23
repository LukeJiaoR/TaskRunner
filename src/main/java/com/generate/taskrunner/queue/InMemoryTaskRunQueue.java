package com.generate.taskrunner.queue;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class InMemoryTaskRunQueue implements TaskRunQueue {
    private final BlockingQueue<String> runIds;

    public InMemoryTaskRunQueue() {
        this.runIds = new LinkedBlockingQueue<>();
    }

    public InMemoryTaskRunQueue(int capacity) {
        this.runIds = new ArrayBlockingQueue<>(capacity);
    }

    @Override
    public void enqueue(String runId) {
        if (!runIds.offer(runId)) {
            throw new TaskRunQueueFullException();
        }
    }

    @Override
    public Optional<String> dequeue() {
        return Optional.ofNullable(runIds.poll());
    }
}
