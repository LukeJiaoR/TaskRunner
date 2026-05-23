package com.generate.taskrunner.api;

import com.generate.taskrunner.TestQueueConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestQueueConfiguration.class)
class TaskScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createsFixedDelayScheduleForTask() throws Exception {
        String taskId = createTask("Say hello", "printf hello");

        mockMvc.perform(post("/tasks/{taskId}/schedules", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"FIXED_DELAY\",\"fixedDelaySeconds\":30}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.taskId").value(taskId))
                .andExpect(jsonPath("$.type").value("FIXED_DELAY"))
                .andExpect(jsonPath("$.fixedDelaySeconds").value(30))
                .andExpect(jsonPath("$.nextRunAt").isNotEmpty());
    }

    private String createTask(String name, String command) throws Exception {
        MvcResult result = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\",\"command\":\"" + command + "\",\"timeoutSeconds\":10}"))
                .andExpect(status().isCreated())
                .andReturn();
        return extractString(result, "id");
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
