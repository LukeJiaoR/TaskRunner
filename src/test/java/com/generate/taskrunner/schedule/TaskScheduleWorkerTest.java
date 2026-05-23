package com.generate.taskrunner.schedule;

import com.generate.taskrunner.domain.TaskRunStatus;
import com.generate.taskrunner.execution.ProcessTaskExecutorService;
import com.generate.taskrunner.queue.InMemoryTaskRunQueue;
import com.generate.taskrunner.repository.InMemoryTaskDefinitionRepository;
import com.generate.taskrunner.repository.InMemoryTaskRunRepository;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class TaskScheduleWorkerTest {

    @Test
    void enqueuesRunForDueFixedDelayScheduleAndAdvancesNextRunAt() {
        InMemoryTaskDefinitionRepository taskRepository = new InMemoryTaskDefinitionRepository();
        InMemoryTaskRunRepository runRepository = new InMemoryTaskRunRepository();
        InMemoryTaskRunQueue queue = new InMemoryTaskRunQueue();
        InMemoryTaskScheduleRepository scheduleRepository = new InMemoryTaskScheduleRepository();
        ProcessTaskExecutorService executor = new ProcessTaskExecutorService(runRepository, queue);
        TaskScheduleWorker worker = new TaskScheduleWorker(scheduleRepository, taskRepository, executor);
        var task = taskRepository.save(new com.generate.taskrunner.domain.TaskDefinition("task-1", "Say hello", "printf hello", null, Duration.ofSeconds(5), Instant.now()));
        TaskSchedule schedule = scheduleRepository.save(new TaskSchedule("schedule-1", task.id(), TaskScheduleType.FIXED_DELAY, null, Duration.ofSeconds(30), Instant.now().minusSeconds(1), Instant.now()));

        worker.processDueSchedules();

        String runId = queue.dequeue().orElseThrow();
        assertThat(runRepository.findById(runId).orElseThrow().status()).isEqualTo(TaskRunStatus.QUEUED);
        assertThat(scheduleRepository.findById(schedule.id()).orElseThrow().nextRunAt()).isAfter(schedule.nextRunAt());
    }
}
