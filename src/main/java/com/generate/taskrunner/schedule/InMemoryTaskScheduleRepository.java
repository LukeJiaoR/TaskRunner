package com.generate.taskrunner.schedule;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryTaskScheduleRepository implements TaskScheduleRepository {
    private final ConcurrentMap<String, TaskSchedule> schedules = new ConcurrentHashMap<>();

    @Override
    public TaskSchedule save(TaskSchedule schedule) {
        schedules.put(schedule.id(), schedule);
        return schedule;
    }

    @Override
    public Optional<TaskSchedule> findById(String id) {
        return Optional.ofNullable(schedules.get(id));
    }

    @Override
    public List<TaskSchedule> findDue(Instant now) {
        return schedules.values().stream()
                .filter(schedule -> !schedule.nextRunAt().isAfter(now))
                .sorted(Comparator.comparing(TaskSchedule::nextRunAt))
                .toList();
    }
}
