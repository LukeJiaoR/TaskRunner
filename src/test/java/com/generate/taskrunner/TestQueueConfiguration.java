package com.generate.taskrunner;

import com.generate.taskrunner.queue.InMemoryTaskRunQueue;
import com.generate.taskrunner.queue.TaskRunQueue;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestQueueConfiguration {

    @Bean
    @Primary
    TaskRunQueue taskRunQueue() {
        return new InMemoryTaskRunQueue();
    }
}
