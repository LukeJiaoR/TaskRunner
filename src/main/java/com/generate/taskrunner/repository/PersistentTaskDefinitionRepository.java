package com.generate.taskrunner.repository;

import com.generate.taskrunner.domain.TaskDefinition;
import com.generate.taskrunner.repository.mybatis.TaskDefinitionMapper;
import com.generate.taskrunner.repository.mybatis.TaskDefinitionRow;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
public class PersistentTaskDefinitionRepository implements TaskDefinitionRepository {
    private final TaskDefinitionMapper taskDefinitionMapper;

    public PersistentTaskDefinitionRepository(TaskDefinitionMapper taskDefinitionMapper) {
        this.taskDefinitionMapper = taskDefinitionMapper;
    }

    @Override
    public TaskDefinition save(TaskDefinition taskDefinition) {
        taskDefinitionMapper.upsert(toRow(taskDefinition));
        return taskDefinition;
    }

    @Override
    public Optional<TaskDefinition> findById(String id) {
        return taskDefinitionMapper.findById(id).map(this::toDomain);
    }

    private TaskDefinitionRow toRow(TaskDefinition taskDefinition) {
        return new TaskDefinitionRow(
                taskDefinition.id(),
                taskDefinition.name(),
                taskDefinition.command(),
                taskDefinition.workingDirectory(),
                taskDefinition.timeout().toMillis(),
                taskDefinition.createdAt()
        );
    }

    private TaskDefinition toDomain(TaskDefinitionRow row) {
        return new TaskDefinition(
                row.getId(),
                row.getName(),
                row.getCommand(),
                row.getWorkingDirectory(),
                Duration.ofMillis(row.getTimeoutMillis()),
                row.getCreatedAt()
        );
    }
}
