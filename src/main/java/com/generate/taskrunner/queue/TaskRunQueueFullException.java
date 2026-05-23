package com.generate.taskrunner.queue;

public class TaskRunQueueFullException extends RuntimeException {
    public TaskRunQueueFullException() {
        super("Task run queue is full");
    }
}
