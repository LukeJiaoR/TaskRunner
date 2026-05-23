package com.generate.taskrunner.execution;

import com.generate.taskrunner.domain.TaskDefinition;
import com.generate.taskrunner.domain.TaskRun;
import com.generate.taskrunner.domain.TaskRunStatus;
import com.generate.taskrunner.queue.TaskRunQueue;
import com.generate.taskrunner.repository.TaskRunRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class ProcessTaskExecutorService implements TaskExecutorService {
    private final TaskRunRepository taskRunRepository;
    private final TaskRunQueue taskRunQueue;

    public ProcessTaskExecutorService(TaskRunRepository taskRunRepository, TaskRunQueue taskRunQueue) {
        this.taskRunRepository = taskRunRepository;
        this.taskRunQueue = taskRunQueue;
    }

    @Override
    public TaskRun start(TaskDefinition taskDefinition) {
        TaskRun queued = new TaskRun(UUID.randomUUID().toString(), taskDefinition.id(), TaskRunStatus.QUEUED, Instant.now(), null, null, null, null);
        taskRunRepository.save(queued);
        taskRunQueue.enqueue(queued.id());
        return queued;
    }
}
