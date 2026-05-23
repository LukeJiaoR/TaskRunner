package com.generate.taskrunner.schedule;

import com.generate.taskrunner.schedule.mybatis.TaskScheduleMapper;
import com.generate.taskrunner.schedule.mybatis.TaskScheduleRow;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class PersistentTaskScheduleRepository implements TaskScheduleRepository {
    private final TaskScheduleMapper mapper;

    public PersistentTaskScheduleRepository(TaskScheduleMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public TaskSchedule save(TaskSchedule schedule) {
        mapper.upsert(toRow(schedule));
        return schedule;
    }

    @Override
    public Optional<TaskSchedule> findById(String id) {
        return mapper.findById(id).map(this::toDomain);
    }

    @Override
    public List<TaskSchedule> findDue(Instant now) {
        return mapper.findDue(now).stream()
                .map(this::toDomain)
                .toList();
    }

    private TaskScheduleRow toRow(TaskSchedule schedule) {
        return new TaskScheduleRow(
                schedule.id(),
                schedule.taskId(),
                schedule.type().name(),
                schedule.cronExpression(),
                schedule.fixedDelay() == null ? null : schedule.fixedDelay().toMillis(),
                schedule.nextRunAt(),
                schedule.createdAt()
        );
    }

    private TaskSchedule toDomain(TaskScheduleRow row) {
        return new TaskSchedule(
                row.getId(),
                row.getTaskId(),
                TaskScheduleType.valueOf(row.getType()),
                row.getCronExpression(),
                row.getFixedDelayMillis() == null ? null : Duration.ofMillis(row.getFixedDelayMillis()),
                row.getNextRunAt(),
                row.getCreatedAt()
        );
    }
}
