package com.generate.taskrunner.api;

import com.generate.taskrunner.api.dto.CreateScheduleRequest;
import com.generate.taskrunner.api.dto.CreateTaskRequest;
import com.generate.taskrunner.api.dto.TaskLogResponse;
import com.generate.taskrunner.api.dto.TaskResponse;
import com.generate.taskrunner.api.dto.TaskRunResponse;
import com.generate.taskrunner.api.dto.TaskScheduleResponse;
import com.generate.taskrunner.domain.TaskDefinition;
import com.generate.taskrunner.domain.TaskLogEntry;
import com.generate.taskrunner.domain.TaskRun;
import com.generate.taskrunner.execution.TaskExecutorService;
import com.generate.taskrunner.repository.TaskDefinitionRepository;
import com.generate.taskrunner.repository.TaskRunRepository;
import com.generate.taskrunner.schedule.TaskSchedule;
import com.generate.taskrunner.schedule.TaskScheduleRepository;
import com.generate.taskrunner.schedule.TaskScheduleType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
public class TaskController {
    private final TaskDefinitionRepository taskDefinitionRepository;
    private final TaskRunRepository taskRunRepository;
    private final TaskExecutorService taskExecutorService;
    private final TaskScheduleRepository taskScheduleRepository;

    public TaskController(TaskDefinitionRepository taskDefinitionRepository,
                          TaskRunRepository taskRunRepository,
                          TaskExecutorService taskExecutorService,
                          TaskScheduleRepository taskScheduleRepository) {
        this.taskDefinitionRepository = taskDefinitionRepository;
        this.taskRunRepository = taskRunRepository;
        this.taskExecutorService = taskExecutorService;
        this.taskScheduleRepository = taskScheduleRepository;
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskResponse> createTask(@RequestBody CreateTaskRequest request) {
        if (isBlank(request.name()) || isBlank(request.command())) {
            return ResponseEntity.badRequest().build();
        }
        long timeoutSeconds = request.timeoutSeconds() == null ? 60 : request.timeoutSeconds();
        if (timeoutSeconds < 1 || timeoutSeconds > 3600) {
            return ResponseEntity.badRequest().build();
        }
        String workingDirectory = isBlank(request.workingDirectory()) ? null : request.workingDirectory();
        TaskDefinition taskDefinition = new TaskDefinition(
                UUID.randomUUID().toString(),
                request.name().trim(),
                request.command(),
                workingDirectory,
                Duration.ofSeconds(timeoutSeconds),
                Instant.now()
        );
        taskDefinitionRepository.save(taskDefinition);
        return ResponseEntity.created(URI.create("/tasks/" + taskDefinition.id())).body(toTaskResponse(taskDefinition));
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable String taskId) {
        return taskDefinitionRepository.findById(taskId)
                .map(taskDefinition -> ResponseEntity.ok(toTaskResponse(taskDefinition)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/tasks/{taskId}/runs")
    public ResponseEntity<TaskRunResponse> startRun(@PathVariable String taskId) {
        return taskDefinitionRepository.findById(taskId)
                .map(taskDefinition -> {
                    TaskRun taskRun = taskExecutorService.start(taskDefinition);
                    return ResponseEntity.accepted().body(toTaskRunResponse(taskRun));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/runs/{runId}")
    public ResponseEntity<TaskRunResponse> getRun(@PathVariable String runId) {
        return taskRunRepository.findById(runId)
                .map(taskRun -> ResponseEntity.ok(toTaskRunResponse(taskRun)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/runs/{runId}/logs")
    public ResponseEntity<TaskLogResponse> getRunLogs(@PathVariable String runId) {
        if (taskRunRepository.findById(runId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<TaskLogResponse.Entry> entries = taskRunRepository.findLogsByRunId(runId).stream()
                .map(this::toLogEntryResponse)
                .toList();
        return ResponseEntity.ok(new TaskLogResponse(runId, entries));
    }

    private TaskResponse toTaskResponse(TaskDefinition taskDefinition) {
        return new TaskResponse(
                taskDefinition.id(),
                taskDefinition.name(),
                taskDefinition.command(),
                taskDefinition.workingDirectory(),
                taskDefinition.timeout().toSeconds(),
                taskDefinition.createdAt()
        );
    }

    private TaskRunResponse toTaskRunResponse(TaskRun taskRun) {
        return new TaskRunResponse(
                taskRun.id(),
                taskRun.taskId(),
                taskRun.status(),
                taskRun.createdAt(),
                taskRun.startedAt(),
                taskRun.finishedAt(),
                taskRun.exitCode(),
                taskRun.errorMessage()
        );
    }

    private TaskLogResponse.Entry toLogEntryResponse(TaskLogEntry taskLogEntry) {
        return new TaskLogResponse.Entry(taskLogEntry.timestamp(), taskLogEntry.stream(), taskLogEntry.message());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
