package com.generate.taskrunner.schedule;

import com.generate.taskrunner.domain.TaskDefinition;
import com.generate.taskrunner.execution.TaskExecutorService;
import com.generate.taskrunner.repository.TaskDefinitionRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@ConditionalOnProperty(name = "taskrunner.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class TaskScheduleWorker {
    private final TaskScheduleRepository scheduleRepository;
    private final TaskDefinitionRepository taskDefinitionRepository;
    private final TaskExecutorService taskExecutorService;

    public TaskScheduleWorker(TaskScheduleRepository scheduleRepository,
                              TaskDefinitionRepository taskDefinitionRepository,
                              TaskExecutorService taskExecutorService) {
        this.scheduleRepository = scheduleRepository;
        this.taskDefinitionRepository = taskDefinitionRepository;
        this.taskExecutorService = taskExecutorService;
    }

    @Scheduled(fixedDelayString = "${taskrunner.scheduler.poll-delay-ms:1000}")
    public void processDueSchedules() {
        Instant now = Instant.now();
        for (TaskSchedule schedule : scheduleRepository.findDue(now)) {
            taskDefinitionRepository.findById(schedule.taskId()).ifPresent(task -> runSchedule(schedule, task, now));
        }
    }

    private void runSchedule(TaskSchedule schedule, TaskDefinition task, Instant now) {
        taskExecutorService.start(task);
        scheduleRepository.save(new TaskSchedule(
                schedule.id(),
                schedule.taskId(),
                schedule.type(),
                schedule.cronExpression(),
                schedule.fixedDelay(),
                nextRunAt(schedule, now),
                schedule.createdAt()
        ));
    }

    private Instant nextRunAt(TaskSchedule schedule, Instant now) {
        if (schedule.type() == TaskScheduleType.FIXED_DELAY) {
            return now.plus(schedule.fixedDelay());
        }
        return now.plusSeconds(60);
    }
}
