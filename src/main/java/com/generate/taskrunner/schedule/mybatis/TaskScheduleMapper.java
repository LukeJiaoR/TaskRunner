package com.generate.taskrunner.schedule.mybatis;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Mapper
public interface TaskScheduleMapper {

    @Insert("""
            MERGE INTO task_schedules KEY(id)
            VALUES (#{id}, #{taskId}, #{type}, #{cronExpression}, #{fixedDelayMillis}, #{nextRunAt}, #{createdAt})
            """)
    void upsert(TaskScheduleRow row);

    @Select("""
            SELECT id, task_id, type, cron_expression, fixed_delay_millis, next_run_at, created_at
            FROM task_schedules
            WHERE id = #{id}
            """)
    @Results(id = "TaskScheduleRowResult", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "task_id", property = "taskId"),
            @Result(column = "type", property = "type"),
            @Result(column = "cron_expression", property = "cronExpression"),
            @Result(column = "fixed_delay_millis", property = "fixedDelayMillis"),
            @Result(column = "next_run_at", property = "nextRunAt"),
            @Result(column = "created_at", property = "createdAt")
    })
    Optional<TaskScheduleRow> findById(@Param("id") String id);

    @Select("""
            SELECT id, task_id, type, cron_expression, fixed_delay_millis, next_run_at, created_at
            FROM task_schedules
            WHERE next_run_at <= #{now}
            ORDER BY next_run_at ASC
            """)
    @Results(id = "TaskScheduleDueRowResult", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "task_id", property = "taskId"),
            @Result(column = "type", property = "type"),
            @Result(column = "cron_expression", property = "cronExpression"),
            @Result(column = "fixed_delay_millis", property = "fixedDelayMillis"),
            @Result(column = "next_run_at", property = "nextRunAt"),
            @Result(column = "created_at", property = "createdAt")
    })
    List<TaskScheduleRow> findDue(@Param("now") Instant now);
}
