package com.generate.taskrunner.execution;

import com.generate.taskrunner.domain.TaskDefinition;
import com.generate.taskrunner.domain.TaskLogEntry;
import com.generate.taskrunner.domain.TaskRun;
import com.generate.taskrunner.domain.TaskRunStatus;
import com.generate.taskrunner.repository.TaskRunRepository;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
public class TaskProcessRunner {
    private final TaskRunRepository taskRunRepository;

    public TaskProcessRunner(TaskRunRepository taskRunRepository) {
        this.taskRunRepository = taskRunRepository;
    }

    public void run(TaskDefinition taskDefinition, TaskRun queued) {
        Instant startedAt = Instant.now();
        int attempt = queued.attempt() + 1;
        taskRunRepository.save(new TaskRun(queued.id(), queued.taskId(), TaskRunStatus.RUNNING, queued.createdAt(), startedAt, null, null, null, attempt, null));

        Process process = null;
        try {
            ProcessBuilder builder = new ProcessBuilder("sh", "-c", taskDefinition.command());
            if (taskDefinition.workingDirectory() != null && !taskDefinition.workingDirectory().isBlank()) {
                builder.directory(new File(taskDefinition.workingDirectory()));
            }
            process = builder.start();
            Process runningProcess = process;
            Thread stdout = streamLogs(queued.id(), "stdout", runningProcess.inputReader(StandardCharsets.UTF_8));
            Thread stderr = streamLogs(queued.id(), "stderr", runningProcess.errorReader(StandardCharsets.UTF_8));
            Duration timeout = taskDefinition.timeout();
            boolean finished = runningProcess.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
            if (!finished) {
                runningProcess.destroyForcibly();
                stdout.join(1000);
                stderr.join(1000);
                String message = "Task timed out after " + timeout.toMillis() + "ms";
                taskRunRepository.appendLog(new TaskLogEntry(queued.id(), Instant.now(), "system", message));
                taskRunRepository.save(new TaskRun(queued.id(), queued.taskId(), TaskRunStatus.TIMED_OUT, queued.createdAt(), startedAt, Instant.now(), null, message, attempt, null));
                return;
            }
            stdout.join(1000);
            stderr.join(1000);
            int exitCode = runningProcess.exitValue();
            TaskRunStatus status = exitCode == 0 ? TaskRunStatus.SUCCEEDED : TaskRunStatus.FAILED;
            taskRunRepository.save(new TaskRun(queued.id(), queued.taskId(), status, queued.createdAt(), startedAt, Instant.now(), exitCode, null, attempt, null));
        } catch (IOException e) {
            String message = e.getMessage();
            taskRunRepository.save(new TaskRun(queued.id(), queued.taskId(), TaskRunStatus.FAILED, queued.createdAt(), startedAt, Instant.now(), null, message, attempt, null));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            if (process != null) {
                process.destroyForcibly();
            }
            String message = "Task execution interrupted";
            taskRunRepository.save(new TaskRun(queued.id(), queued.taskId(), TaskRunStatus.FAILED, queued.createdAt(), startedAt, Instant.now(), null, message, attempt, null));
        }
    }

    private Thread streamLogs(String runId, String stream, BufferedReader reader) {
        Thread thread = new Thread(() -> {
            try (reader) {
                String line;
                while ((line = reader.readLine()) != null) {
                    taskRunRepository.appendLog(new TaskLogEntry(runId, Instant.now(), stream, line));
                }
            } catch (IOException e) {
                taskRunRepository.appendLog(new TaskLogEntry(runId, Instant.now(), "system", e.getMessage()));
            }
        });
        thread.start();
        return thread;
    }
}
