package com.generate.taskrunner.repository;

import com.generate.taskrunner.domain.TaskLogEntry;
import com.generate.taskrunner.domain.TaskRun;
import com.generate.taskrunner.domain.TaskRunStatus;
import com.generate.taskrunner.repository.mybatis.TaskLogRow;
import com.generate.taskrunner.repository.mybatis.TaskRunMapper;
import com.generate.taskrunner.repository.mybatis.TaskRunRow;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PersistentTaskRunRepository implements TaskRunRepository {
    private final TaskRunMapper taskRunMapper;

    public PersistentTaskRunRepository(TaskRunMapper taskRunMapper) {
        this.taskRunMapper = taskRunMapper;
    }

    @Override
    public TaskRun save(TaskRun taskRun) {
        taskRunMapper.upsert(toRow(taskRun));
        return taskRun;
    }

    @Override
    public Optional<TaskRun> findById(String id) {
        return taskRunMapper.findById(id).map(this::toDomain);
    }

    @Override
    public long countByStatus(TaskRunStatus status) {
        return taskRunMapper.countByStatus(status.name());
    }

    @Override
    public void appendLog(TaskLogEntry entry) {
        taskRunMapper.insertLog(new TaskLogRow(null, entry.runId(), entry.timestamp(), entry.stream(), entry.message()));
    }

    @Override
    public List<TaskLogEntry> findLogsByRunId(String runId) {
        return taskRunMapper.findLogsByRunId(runId).stream()
                .map(this::toDomain)
                .toList();
    }

    private TaskRunRow toRow(TaskRun taskRun) {
        return new TaskRunRow(
                taskRun.id(),
                taskRun.taskId(),
                taskRun.status().name(),
                taskRun.createdAt(),
                taskRun.startedAt(),
                taskRun.finishedAt(),
                taskRun.exitCode(),
                taskRun.errorMessage(),
                taskRun.attempt(),
                taskRun.nextAttemptAt()
        );
    }

    private TaskRun toDomain(TaskRunRow row) {
        return new TaskRun(
                row.getId(),
                row.getTaskId(),
                TaskRunStatus.valueOf(row.getStatus()),
                row.getCreatedAt(),
                row.getStartedAt(),
                row.getFinishedAt(),
                row.getExitCode(),
                row.getErrorMessage(),
                row.getAttempt(),
                row.getNextAttemptAt()
        );
    }

    private TaskLogEntry toDomain(TaskLogRow row) {
        return new TaskLogEntry(row.getRunId(), row.getTimestamp(), row.getStream(), row.getMessage());
    }
}
