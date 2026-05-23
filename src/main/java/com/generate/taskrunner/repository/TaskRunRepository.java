package com.generate.taskrunner.repository;

import com.generate.taskrunner.domain.TaskLogEntry;
import com.generate.taskrunner.domain.TaskRun;
import com.generate.taskrunner.domain.TaskRunStatus;

import java.util.List;
import java.util.Optional;

public interface TaskRunRepository {
    TaskRun save(TaskRun taskRun);

    Optional<TaskRun> findById(String id);

    long countByStatus(TaskRunStatus status);

    void appendLog(TaskLogEntry entry);

    List<TaskLogEntry> findLogsByRunId(String runId);
}
