package com.generate.taskrunner;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestQueueConfiguration.class)
class TaskRunnerApplicationTests {

    @Test
    void contextLoads() {
    }

}
