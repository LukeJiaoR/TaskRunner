package com.generate.taskrunner.repository;

import com.generate.taskrunner.domain.TaskDefinition;
import com.generate.taskrunner.domain.TaskLogEntry;
import com.generate.taskrunner.domain.TaskRun;
import com.generate.taskrunner.domain.TaskRunStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryTaskRepositoryTest {

    @Test
    void savesAndFindsTaskDefinitionsById() {
        TaskDefinitionRepository repository = new InMemoryTaskDefinitionRepository();
        TaskDefinition task = new TaskDefinition("task-1", "Say hello", "printf hello", null, Duration.ofSeconds(10), Instant.now());

        repository.save(task);

        assertThat(repository.findById("task-1")).contains(task);
        assertThat(repository.findById("missing")).isEmpty();
    }

    @Test
    void savesAndUpdatesTaskRunsById() {
        TaskRunRepository repository = new InMemoryTaskRunRepository();
        Instant createdAt = Instant.now();
        TaskRun queued = new TaskRun("run-1", "task-1", TaskRunStatus.QUEUED, createdAt, null, null, null, null);
        TaskRun running = new TaskRun("run-1", "task-1", TaskRunStatus.RUNNING, createdAt, createdAt.plusSeconds(1), null, null, null);

        repository.save(queued);
        repository.save(running);

        assertThat(repository.findById("run-1")).contains(running);
        assertThat(repository.findById("missing")).isEmpty();
    }

    @Test
    void appendsAndReadsLogEntriesInInsertionOrder() {
        TaskRunRepository repository = new InMemoryTaskRunRepository();
        TaskLogEntry first = new TaskLogEntry("run-1", Instant.now(), "stdout", "hello");
        TaskLogEntry second = new TaskLogEntry("run-1", Instant.now().plusMillis(1), "stderr", "warning");

        repository.appendLog(first);
        repository.appendLog(second);

        assertThat(repository.findLogsByRunId("run-1")).containsExactly(first, second);
        assertThat(repository.findLogsByRunId("missing")).isEqualTo(List.of());
    }
}
