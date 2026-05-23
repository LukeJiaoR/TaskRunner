package com.generate.taskrunner.execution;

import com.generate.taskrunner.domain.TaskDefinition;
import com.generate.taskrunner.domain.TaskRun;

public interface TaskExecutorService {
    TaskRun start(TaskDefinition taskDefinition);
}
