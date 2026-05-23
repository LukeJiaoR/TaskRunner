package com.generate.taskrunner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TaskRunnerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskRunnerApplication.class, args);
    }

}
