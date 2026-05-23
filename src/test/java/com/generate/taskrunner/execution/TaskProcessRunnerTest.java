package com.generate.taskrunner.execution;

import com.generate.taskrunner.domain.TaskDefinition;
import com.generate.taskrunner.domain.TaskRun;
import com.generate.taskrunner.domain.TaskRunStatus;
import com.generate.taskrunner.repository.InMemoryTaskRunRepository;
import com.generate.taskrunner.repository.TaskRunRepository;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class TaskProcessRunnerTest {

    @Test
    void successfulCommandFinishesWithCapturedStdout() throws Exception {
        TaskRunRepository repository = new InMemoryTaskRunRepository();
        TaskProcessRunner runner = new TaskProcessRunner(repository);
        TaskDefinition task = new TaskDefinition("task-1", "Say hello", "printf hello", null, Duration.ofSeconds(5), Instant.now());
        TaskRun queued = queuedRun(task);

        runner.run(task, queued);

        TaskRun finished = repository.findById(queued.id()).orElseThrow();
        assertThat(finished.status()).isEqualTo(TaskRunStatus.SUCCEEDED);
        assertThat(finished.exitCode()).isEqualTo(0);
        assertThat(repository.findLogsByRunId(queued.id()))
                .anySatisfy(entry -> {
                    assertThat(entry.stream()).isEqualTo("stdout");
                    assertThat(entry.message()).isEqualTo("hello");
                });
    }

    @Test
    void failingCommandFinishesWithExitCode() {
        TaskRunRepository repository = new InMemoryTaskRunRepository();
        TaskProcessRunner runner = new TaskProcessRunner(repository);
        TaskDefinition task = new TaskDefinition("task-1", "Fail", "sh -c 'exit 7'", null, Duration.ofSeconds(5), Instant.now());
        TaskRun queued = queuedRun(task);

        runner.run(task, queued);

        TaskRun finished = repository.findById(queued.id()).orElseThrow();
        assertThat(finished.status()).isEqualTo(TaskRunStatus.FAILED);
        assertThat(finished.exitCode()).isEqualTo(7);
    }

    @Test
    void commandExceedingTimeoutIsTimedOut() {
        TaskRunRepository repository = new InMemoryTaskRunRepository();
        TaskProcessRunner runner = new TaskProcessRunner(repository);
        TaskDefinition task = new TaskDefinition("task-1", "Sleep", "sleep 2", null, Duration.ofMillis(100), Instant.now());
        TaskRun queued = queuedRun(task);

        runner.run(task, queued);

        TaskRun finished = repository.findById(queued.id()).orElseThrow();
        assertThat(finished.status()).isEqualTo(TaskRunStatus.TIMED_OUT);
        assertThat(finished.errorMessage()).isEqualTo("Task timed out after 100ms");
        assertThat(repository.findLogsByRunId(queued.id()))
                .anySatisfy(entry -> {
                    assertThat(entry.stream()).isEqualTo("system");
                    assertThat(entry.message()).contains("timed out");
                });
    }

    private TaskRun queuedRun(TaskDefinition task) {
        TaskRun queued = new TaskRun("run-1", task.id(), TaskRunStatus.QUEUED, Instant.now(), null, null, null, null);
        return queued;
    }
}
