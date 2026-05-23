package com.generate.taskrunner.queue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@ConditionalOnMissingBean(TaskRunQueue.class)
public class RedisTaskRunQueue implements TaskRunQueue {
    private final StringRedisTemplate stringRedisTemplate;
    private final String queueName;

    public RedisTaskRunQueue(StringRedisTemplate stringRedisTemplate,
                             @Value("${taskrunner.queue.redis-key:taskrunner:runs}") String queueName) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.queueName = queueName;
    }

    @Override
    public void enqueue(String runId) {
        stringRedisTemplate.opsForList().rightPush(queueName, runId);
    }

    @Override
    public Optional<String> dequeue() {
        return Optional.ofNullable(stringRedisTemplate.opsForList().leftPop(queueName));
    }
}
