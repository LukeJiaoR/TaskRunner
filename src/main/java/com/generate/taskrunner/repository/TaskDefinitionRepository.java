package com.generate.taskrunner.repository;

import com.generate.taskrunner.domain.TaskDefinition;

import java.util.Optional;

public interface TaskDefinitionRepository {
    TaskDefinition save(TaskDefinition taskDefinition);

    Optional<TaskDefinition> findById(String id);
}
