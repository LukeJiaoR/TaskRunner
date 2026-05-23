package com.generate.taskrunner.execution;

import com.generate.taskrunner.domain.TaskDefinition;
import com.generate.taskrunner.domain.TaskRun;
import com.generate.taskrunner.domain.TaskRunStatus;
import com.generate.taskrunner.queue.InMemoryTaskRunQueue;
import com.generate.taskrunner.repository.InMemoryTaskDefinitionRepository;
import com.generate.taskrunner.repository.InMemoryTaskRunRepository;
import com.generate.taskrunner.repository.TaskDefinitionRepository;
import com.generate.taskrunner.repository.TaskRunRepository;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class QueuedTaskWorkerTest {

    @Test
    void queuedRunIdIsConsumedAndExecuted() {
        TaskDefinitionRepository taskRepository = new InMemoryTaskDefinitionRepository();
        TaskRunRepository runRepository = new InMemoryTaskRunRepository();
        InMemoryTaskRunQueue queue = new InMemoryTaskRunQueue();
        QueuedTaskWorker worker = new QueuedTaskWorker(queue, runRepository, taskRepository, new TaskProcessRunner(runRepository));
        TaskDefinition task = new TaskDefinition("task-1", "Say hello", "printf hello", null, Duration.ofSeconds(5), Instant.now());
        TaskRun run = new TaskRun("run-1", task.id(), TaskRunStatus.QUEUED, Instant.now(), null, null, null, null);
        taskRepository.save(task);
        runRepository.save(run);
        queue.enqueue(run.id());

        worker.processNext();

        TaskRun finished = runRepository.findById(run.id()).orElseThrow();
        assertThat(finished.status()).isEqualTo(TaskRunStatus.SUCCEEDED);
        assertThat(queue.dequeue()).isEmpty();
        assertThat(runRepository.findLogsByRunId(run.id()))
                .anySatisfy(entry -> assertThat(entry.message()).isEqualTo("hello"));
    }

    @Test
    void missingTaskDefinitionMarksRunFailed() {
        TaskDefinitionRepository taskRepository = new InMemoryTaskDefinitionRepository();
        TaskRunRepository runRepository = new InMemoryTaskRunRepository();
        InMemoryTaskRunQueue queue = new InMemoryTaskRunQueue();
        QueuedTaskWorker worker = new QueuedTaskWorker(queue, runRepository, taskRepository, new TaskProcessRunner(runRepository));
        TaskRun run = new TaskRun("run-1", "missing-task", TaskRunStatus.QUEUED, Instant.now(), null, null, null, null);
        runRepository.save(run);
        queue.enqueue(run.id());

        worker.processNext();

        TaskRun failed = runRepository.findById(run.id()).orElseThrow();
        assertThat(failed.status()).isEqualTo(TaskRunStatus.FAILED);
        assertThat(failed.errorMessage()).isEqualTo("Task definition not found: missing-task");
    }

    @Test
    void nonQueuedRunIsSkipped() {
        TaskDefinitionRepository taskRepository = new InMemoryTaskDefinitionRepository();
        TaskRunRepository runRepository = new InMemoryTaskRunRepository();
        InMemoryTaskRunQueue queue = new InMemoryTaskRunQueue();
        QueuedTaskWorker worker = new QueuedTaskWorker(queue, runRepository, taskRepository, new TaskProcessRunner(runRepository));
        TaskDefinition task = new TaskDefinition("task-1", "Say hello", "printf hello", null, Duration.ofSeconds(5), Instant.now());
        TaskRun run = new TaskRun("run-1", task.id(), TaskRunStatus.SUCCEEDED, Instant.now(), Instant.now(), Instant.now(), 0, null);
        taskRepository.save(task);
        runRepository.save(run);
        queue.enqueue(run.id());

        worker.processNext();

        assertThat(runRepository.findById(run.id())).contains(run);
        assertThat(runRepository.findLogsByRunId(run.id())).isEmpty();
    }

    @Test
    void maxConcurrentRunsPreventsStartingMoreWork() {
        TaskDefinitionRepository taskRepository = new InMemoryTaskDefinitionRepository();
        TaskRunRepository runRepository = new InMemoryTaskRunRepository();
        InMemoryTaskRunQueue queue = new InMemoryTaskRunQueue();
        AtomicInteger executions = new AtomicInteger();
        TaskProcessRunner runner = new TaskProcessRunner(runRepository) {
            @Override
            public void run(TaskDefinition taskDefinition, TaskRun queued) {
                executions.incrementAndGet();
            }
        };
        QueuedTaskWorker worker = new QueuedTaskWorker(queue, runRepository, taskRepository, runner, 1);
        TaskDefinition task = new TaskDefinition("task-1", "Say hello", "printf hello", null, Duration.ofSeconds(5), Instant.now());
        TaskRun running = new TaskRun("run-1", task.id(), TaskRunStatus.RUNNING, Instant.now(), Instant.now(), null, null, null);
        TaskRun queued = new TaskRun("run-2", task.id(), TaskRunStatus.QUEUED, Instant.now(), null, null, null, null);
        taskRepository.save(task);
        runRepository.save(running);
        runRepository.save(queued);
        queue.enqueue(queued.id());

        worker.processNext();

        assertThat(executions).hasValue(0);
        assertThat(queue.dequeue()).contains(queued.id());
        assertThat(runRepository.findById(queued.id())).contains(queued);
    }

    @Test
    void failedRunIsRequeuedUntilMaxAttempts() {
        TaskDefinitionRepository taskRepository = new InMemoryTaskDefinitionRepository();
        TaskRunRepository runRepository = new InMemoryTaskRunRepository();
        InMemoryTaskRunQueue queue = new InMemoryTaskRunQueue();
        QueuedTaskWorker worker = new QueuedTaskWorker(queue, runRepository, taskRepository, new TaskProcessRunner(runRepository), 2, 3);
        TaskDefinition task = new TaskDefinition("task-1", "Fail", "exit 7", null, Duration.ofSeconds(5), Instant.now());
        TaskRun run = new TaskRun("run-1", task.id(), TaskRunStatus.QUEUED, Instant.now(), null, null, null, null);
        taskRepository.save(task);
        runRepository.save(run);
        queue.enqueue(run.id());

        worker.processNext();

        TaskRun retried = runRepository.findById(run.id()).orElseThrow();
        assertThat(retried.status()).isEqualTo(TaskRunStatus.QUEUED);
        assertThat(retried.attempt()).isEqualTo(1);
        assertThat(retried.nextAttemptAt()).isAfterOrEqualTo(retried.finishedAt());
        assertThat(queue.dequeue()).contains(run.id());
        queue.enqueue(run.id());

        worker.processNext();
        TaskRun retriedAgain = runRepository.findById(run.id()).orElseThrow();
        assertThat(retriedAgain.status()).isEqualTo(TaskRunStatus.QUEUED);
        assertThat(retriedAgain.attempt()).isEqualTo(2);
        assertThat(queue.dequeue()).contains(run.id());
        queue.enqueue(run.id());
        worker.processNext();

        TaskRun failed = runRepository.findById(run.id()).orElseThrow();
        assertThat(failed.status()).isEqualTo(TaskRunStatus.FAILED);
        assertThat(failed.attempt()).isEqualTo(3);
        assertThat(queue.dequeue()).isEmpty();
    }
}
