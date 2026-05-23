package com.generate.taskrunner.execution;

import com.generate.taskrunner.domain.TaskRun;
import com.generate.taskrunner.domain.TaskRunStatus;
import com.generate.taskrunner.queue.TaskRunQueue;
import com.generate.taskrunner.repository.TaskDefinitionRepository;
import com.generate.taskrunner.repository.TaskRunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@ConditionalOnProperty(name = "taskrunner.worker.enabled", havingValue = "true", matchIfMissing = true)
public class QueuedTaskWorker {
    private final TaskRunQueue taskRunQueue;
    private final TaskRunRepository taskRunRepository;
    private final TaskDefinitionRepository taskDefinitionRepository;
    private final TaskProcessRunner taskProcessRunner;
    private final int maxConcurrentRuns;
    private final int maxAttempts;
    private final Duration retryDelay;

    public QueuedTaskWorker(TaskRunQueue taskRunQueue,
                            TaskRunRepository taskRunRepository,
                            TaskDefinitionRepository taskDefinitionRepository,
                            TaskProcessRunner taskProcessRunner) {
        this(taskRunQueue, taskRunRepository, taskDefinitionRepository, taskProcessRunner, 2, 1, Duration.ZERO);
    }

    public QueuedTaskWorker(TaskRunQueue taskRunQueue,
                            TaskRunRepository taskRunRepository,
                            TaskDefinitionRepository taskDefinitionRepository,
                            TaskProcessRunner taskProcessRunner,
                            int maxConcurrentRuns) {
        this(taskRunQueue, taskRunRepository, taskDefinitionRepository, taskProcessRunner, maxConcurrentRuns, 1, Duration.ZERO);
    }

    public QueuedTaskWorker(TaskRunQueue taskRunQueue,
                            TaskRunRepository taskRunRepository,
                            TaskDefinitionRepository taskDefinitionRepository,
                            TaskProcessRunner taskProcessRunner,
                            int maxConcurrentRuns,
                            int maxAttempts) {
        this(taskRunQueue, taskRunRepository, taskDefinitionRepository, taskProcessRunner, maxConcurrentRuns, maxAttempts, Duration.ZERO);
    }

    @Autowired
    public QueuedTaskWorker(TaskRunQueue taskRunQueue,
                            TaskRunRepository taskRunRepository,
                            TaskDefinitionRepository taskDefinitionRepository,
                            TaskProcessRunner taskProcessRunner,
                            @Value("${taskrunner.worker.max-concurrent-runs:2}") int maxConcurrentRuns,
                            @Value("${taskrunner.worker.max-attempts:1}") int maxAttempts,
                            @Value("${taskrunner.worker.retry-delay-ms:0}") long retryDelayMillis) {
        this(taskRunQueue, taskRunRepository, taskDefinitionRepository, taskProcessRunner, maxConcurrentRuns, maxAttempts, Duration.ofMillis(retryDelayMillis));
    }

    private QueuedTaskWorker(TaskRunQueue taskRunQueue,
                             TaskRunRepository taskRunRepository,
                             TaskDefinitionRepository taskDefinitionRepository,
                             TaskProcessRunner taskProcessRunner,
                             int maxConcurrentRuns,
                             int maxAttempts,
                             Duration retryDelay) {
        this.taskRunQueue = taskRunQueue;
        this.taskRunRepository = taskRunRepository;
        this.taskDefinitionRepository = taskDefinitionRepository;
        this.taskProcessRunner = taskProcessRunner;
        this.maxConcurrentRuns = maxConcurrentRuns;
        this.maxAttempts = maxAttempts;
        this.retryDelay = retryDelay;
    }

    @Scheduled(fixedDelayString = "${taskrunner.worker.poll-delay-ms:250}")
    public void processNext() {
        taskRunQueue.dequeue().ifPresent(this::processRunId);
    }

    private void processRunId(String runId) {
        taskRunRepository.findById(runId).ifPresent(run -> {
            if (run.status() != TaskRunStatus.QUEUED) {
                return;
            }
            if (run.nextAttemptAt() != null && run.nextAttemptAt().isAfter(Instant.now())) {
                taskRunQueue.enqueue(runId);
                return;
            }
            if (taskRunRepository.countByStatus(TaskRunStatus.RUNNING) >= maxConcurrentRuns) {
                taskRunQueue.enqueue(runId);
                return;
            }
            taskDefinitionRepository.findById(run.taskId())
                    .ifPresentOrElse(taskDefinition -> runTask(taskDefinition, run), () -> failMissingTask(run));
        });
    }

    private void runTask(com.generate.taskrunner.domain.TaskDefinition taskDefinition, TaskRun run) {
        taskProcessRunner.run(taskDefinition, run);
        taskRunRepository.findById(run.id()).ifPresent(this::retryIfEligible);
    }

    private void retryIfEligible(TaskRun run) {
        if ((run.status() == TaskRunStatus.FAILED || run.status() == TaskRunStatus.TIMED_OUT) && run.attempt() < maxAttempts) {
            Instant nextAttemptAt = Instant.now().plus(retryDelay.multipliedBy(run.attempt()));
            taskRunRepository.save(new TaskRun(run.id(), run.taskId(), TaskRunStatus.QUEUED, run.createdAt(), run.startedAt(), run.finishedAt(), run.exitCode(), run.errorMessage(), run.attempt(), nextAttemptAt));
            taskRunQueue.enqueue(run.id());
        }
    }

    private void failMissingTask(TaskRun run) {
        String message = "Task definition not found: " + run.taskId();
        taskRunRepository.save(new TaskRun(run.id(), run.taskId(), TaskRunStatus.FAILED, run.createdAt(), run.startedAt(), Instant.now(), null, message, run.attempt(), null));
    }
}
