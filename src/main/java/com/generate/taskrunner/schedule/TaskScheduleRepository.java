package com.generate.taskrunner.schedule;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TaskScheduleRepository {
    TaskSchedule save(TaskSchedule schedule);

    Optional<TaskSchedule> findById(String id);

    List<TaskSchedule> findDue(Instant now);
}
