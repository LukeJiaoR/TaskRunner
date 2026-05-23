package com.generate.taskrunner.repository;

import com.generate.taskrunner.TestQueueConfiguration;
import com.generate.taskrunner.domain.TaskDefinition;
import com.generate.taskrunner.domain.TaskLogEntry;
import com.generate.taskrunner.domain.TaskRun;
import com.generate.taskrunner.domain.TaskRunStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestQueueConfiguration.class)
class PersistentTaskRepositoryTest {

    @Autowired
    private TaskDefinitionRepository taskDefinitionRepository;

    @Autowired
    private TaskRunRepository taskRunRepository;

    @Test
    void usesPersistentRepositoryBeans() {
        assertThat(taskDefinitionRepository).isInstanceOf(PersistentTaskDefinitionRepository.class);
        assertThat(taskRunRepository).isInstanceOf(PersistentTaskRunRepository.class);
    }

    @Test
    void savesAndFindsTaskDefinitionsById() {
        TaskDefinition task = new TaskDefinition(uniqueId("task"), "Say hello", "printf hello", null, Duration.ofSeconds(10), Instant.now());

        taskDefinitionRepository.save(task);

        assertThat(taskDefinitionRepository.findById(task.id())).contains(task);
        assertThat(taskDefinitionRepository.findById(uniqueId("missing"))).isEmpty();
    }

    @Test
    void savesAndUpdatesTaskRunsById() {
        TaskDefinition task = new TaskDefinition(uniqueId("task"), "Say hello", "printf hello", null, Duration.ofSeconds(10), Instant.now());
        taskDefinitionRepository.save(task);
        Instant createdAt = Instant.now();
        TaskRun queued = new TaskRun(uniqueId("run"), task.id(), TaskRunStatus.QUEUED, createdAt, null, null, null, null);
        TaskRun running = new TaskRun(queued.id(), task.id(), TaskRunStatus.RUNNING, createdAt, createdAt.plusSeconds(1), null, null, null);

        taskRunRepository.save(queued);
        taskRunRepository.save(running);

        assertThat(taskRunRepository.findById(queued.id())).contains(running);
        assertThat(taskRunRepository.findById(uniqueId("missing"))).isEmpty();
    }

    @Test
    void appendsAndReadsLogEntriesInInsertionOrder() {
        TaskDefinition task = new TaskDefinition(uniqueId("task"), "Say hello", "printf hello", null, Duration.ofSeconds(10), Instant.now());
        taskDefinitionRepository.save(task);
        TaskRun run = new TaskRun(uniqueId("run"), task.id(), TaskRunStatus.RUNNING, Instant.now(), Instant.now(), null, null, null);
        taskRunRepository.save(run);
        TaskLogEntry first = new TaskLogEntry(run.id(), Instant.now(), "stdout", "hello");
        TaskLogEntry second = new TaskLogEntry(run.id(), Instant.now().plusMillis(1), "stderr", "warning");

        taskRunRepository.appendLog(first);
        taskRunRepository.appendLog(second);

        assertThat(taskRunRepository.findLogsByRunId(run.id())).containsExactly(first, second);
        assertThat(taskRunRepository.findLogsByRunId(uniqueId("missing"))).isEqualTo(List.of());
    }

    private String uniqueId(String prefix) {
        return prefix + "-" + System.nanoTime();
    }
}
