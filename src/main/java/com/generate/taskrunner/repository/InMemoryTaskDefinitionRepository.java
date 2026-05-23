package com.generate.taskrunner.repository;

import com.generate.taskrunner.domain.TaskDefinition;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryTaskDefinitionRepository implements TaskDefinitionRepository {
    private final ConcurrentMap<String, TaskDefinition> taskDefinitions = new ConcurrentHashMap<>();

    @Override
    public TaskDefinition save(TaskDefinition taskDefinition) {
        taskDefinitions.put(taskDefinition.id(), taskDefinition);
        return taskDefinition;
    }

    @Override
    public Optional<TaskDefinition> findById(String id) {
        return Optional.ofNullable(taskDefinitions.get(id));
    }
}
