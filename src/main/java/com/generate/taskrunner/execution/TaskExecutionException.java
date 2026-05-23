package com.generate.taskrunner.execution;

public class TaskExecutionException extends RuntimeException {
    public TaskExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
