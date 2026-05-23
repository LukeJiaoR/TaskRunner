package com.generate.taskrunner.repository;

import com.generate.taskrunner.domain.TaskLogEntry;
import com.generate.taskrunner.domain.TaskRun;
import com.generate.taskrunner.domain.TaskRunStatus;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryTaskRunRepository implements TaskRunRepository {
    private final ConcurrentMap<String, TaskRun> taskRuns = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, CopyOnWriteArrayList<TaskLogEntry>> logs = new ConcurrentHashMap<>();

    @Override
    public TaskRun save(TaskRun taskRun) {
        taskRuns.put(taskRun.id(), taskRun);
        return taskRun;
    }

    @Override
    public Optional<TaskRun> findById(String id) {
        return Optional.ofNullable(taskRuns.get(id));
    }

    @Override
    public long countByStatus(TaskRunStatus status) {
        return taskRuns.values().stream()
                .filter(taskRun -> taskRun.status() == status)
                .count();
    }

    @Override
    public void appendLog(TaskLogEntry entry) {
        logs.computeIfAbsent(entry.runId(), ignored -> new CopyOnWriteArrayList<>()).add(entry);
    }

    @Override
    public List<TaskLogEntry> findLogsByRunId(String runId) {
        return List.copyOf(logs.getOrDefault(runId, new CopyOnWriteArrayList<>()));
    }
}
