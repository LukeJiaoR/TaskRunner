package com.generate.taskrunner.execution;

import com.generate.taskrunner.domain.TaskDefinition;
import com.generate.taskrunner.domain.TaskRun;
import com.generate.taskrunner.domain.TaskRunStatus;
import com.generate.taskrunner.queue.InMemoryTaskRunQueue;
import com.generate.taskrunner.repository.InMemoryTaskRunRepository;
import com.generate.taskrunner.repository.TaskRunRepository;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessTaskExecutorServiceTest {

    @Test
    void startCreatesQueuedRunAndEnqueuesRunIdWithoutExecuting() throws Exception {
        TaskRunRepository repository = new InMemoryTaskRunRepository();
        InMemoryTaskRunQueue queue = new InMemoryTaskRunQueue();
        TaskExecutorService executor = new ProcessTaskExecutorService(repository, queue);
        TaskDefinition task = new TaskDefinition("task-1", "Say hello", "printf hello", null, Duration.ofSeconds(5), Instant.now());

        TaskRun started = executor.start(task);

        assertThat(started.status()).isEqualTo(TaskRunStatus.QUEUED);
        assertThat(repository.findById(started.id())).contains(started);
        assertThat(queue.dequeue()).contains(started.id());
        Thread.sleep(100);
        assertThat(repository.findById(started.id())).contains(started);
        assertThat(repository.findLogsByRunId(started.id())).isEmpty();
    }
}
