package com.generate.taskrunner.api;

import com.generate.taskrunner.TestQueueConfiguration;
import com.generate.taskrunner.domain.TaskRunStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestQueueConfiguration.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createsAndReadsTask() throws Exception {
        String taskId = createTask("Say hello", "printf hello");

        mockMvc.perform(get("/tasks/{taskId}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.name").value("Say hello"))
                .andExpect(jsonPath("$.command").value("printf hello"))
                .andExpect(jsonPath("$.timeoutSeconds").value(10));
    }

    @Test
    void blankTaskNameReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"  \",\"command\":\"printf hello\",\"timeoutSeconds\":10}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void missingTaskReturnsNotFound() throws Exception {
        mockMvc.perform(get("/tasks/missing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void startsRunAndReadsStatusAndLogs() throws Exception {
        String taskId = createTask("Say hello", "printf hello");

        MvcResult runResult = mockMvc.perform(post("/tasks/{taskId}/runs", taskId))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.taskId").value(taskId))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();
        String runId = extractString(runResult, "id");

        awaitTerminalRun(runId, TaskRunStatus.SUCCEEDED);

        mockMvc.perform(get("/runs/{runId}/logs", runId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.runId").value(runId))
                .andExpect(jsonPath("$.entries[0].stream").value("stdout"))
                .andExpect(jsonPath("$.entries[0].message").value("hello"));
    }

    @Test
    void missingRunReturnsNotFound() throws Exception {
        mockMvc.perform(get("/runs/missing"))
                .andExpect(status().isNotFound());
    }

    private String createTask(String name, String command) throws Exception {
        MvcResult result = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\",\"command\":\"" + command + "\",\"timeoutSeconds\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(name))
                .andReturn();
        return extractString(result, "id");
    }

    private void awaitTerminalRun(String runId, TaskRunStatus expectedStatus) throws Exception {
        long deadline = System.currentTimeMillis() + 5000;
        while (System.currentTimeMillis() < deadline) {
            MvcResult result = mockMvc.perform(get("/runs/{runId}", runId))
                    .andExpect(status().isOk())
                    .andReturn();
            String status = extractString(result, "status");
            if (status.equals(expectedStatus.name())) {
                return;
            }
            Thread.sleep(25);
        }
        throw new AssertionError("Run did not reach status " + expectedStatus);
    }

    private String extractString(MvcResult result, String fieldName) throws Exception {
        String body = result.getResponse().getContentAsString();
        String needle = "\"" + fieldName + "\":\"";
        int start = body.indexOf(needle);
        assertThat(start).isGreaterThanOrEqualTo(0);
        int valueStart = start + needle.length();
        int valueEnd = body.indexOf('"', valueStart);
        return body.substring(valueStart, valueEnd);
    }
}
