package com.generate.taskrunner.queue;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryTaskRunQueueTest {

    @Test
    void emptyQueueReturnsEmpty() {
        InMemoryTaskRunQueue queue = new InMemoryTaskRunQueue();

        assertThat(queue.dequeue()).isEmpty();
    }

    @Test
    void dequeuesRunIdsInFirstInFirstOutOrder() {
        InMemoryTaskRunQueue queue = new InMemoryTaskRunQueue();

        queue.enqueue("run-1");
        queue.enqueue("run-2");

        assertThat(queue.dequeue()).contains("run-1");
        assertThat(queue.dequeue()).contains("run-2");
        assertThat(queue.dequeue()).isEmpty();
    }

    @Test
    void rejectsRunIdsWhenCapacityIsFull() {
        InMemoryTaskRunQueue queue = new InMemoryTaskRunQueue(1);

        queue.enqueue("run-1");

        assertThatThrownBy(() -> queue.enqueue("run-2"))
                .isInstanceOf(TaskRunQueueFullException.class)
                .hasMessage("Task run queue is full");
        assertThat(queue.dequeue()).contains("run-1");
        assertThat(queue.dequeue()).isEmpty();
    }
}
