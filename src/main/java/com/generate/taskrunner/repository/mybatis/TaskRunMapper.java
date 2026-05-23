package com.generate.taskrunner.repository.mybatis;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface TaskRunMapper {

    @Insert("""
            MERGE INTO task_runs KEY(id)
            VALUES (#{id}, #{taskId}, #{status}, #{createdAt}, #{startedAt}, #{finishedAt}, #{exitCode}, #{errorMessage}, #{attempt}, #{nextAttemptAt})
            """)
    void upsert(TaskRunRow row);

    @Select("""
            SELECT id, task_id, status, created_at, started_at, finished_at, exit_code, error_message, attempt, next_attempt_at
            FROM task_runs
            WHERE id = #{id}
            """)
    @Results(id = "TaskRunRowResult", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "task_id", property = "taskId"),
            @Result(column = "status", property = "status"),
            @Result(column = "created_at", property = "createdAt"),
            @Result(column = "started_at", property = "startedAt"),
            @Result(column = "finished_at", property = "finishedAt"),
            @Result(column = "exit_code", property = "exitCode"),
            @Result(column = "error_message", property = "errorMessage"),
            @Result(column = "attempt", property = "attempt"),
            @Result(column = "next_attempt_at", property = "nextAttemptAt")
    })
    Optional<TaskRunRow> findById(@Param("id") String id);

    @Select("""
            SELECT COUNT(*)
            FROM task_runs
            WHERE status = #{status}
            """)
    long countByStatus(@Param("status") String status);

    @Insert("""
            INSERT INTO task_run_logs (run_id, timestamp, stream, message)
            VALUES (#{runId}, #{timestamp}, #{stream}, #{message})
            """)
    void insertLog(TaskLogRow row);

    @Select("""
            SELECT id, run_id, timestamp, stream, message
            FROM task_run_logs
            WHERE run_id = #{runId}
            ORDER BY id ASC
            """)
    @Results(id = "TaskLogRowResult", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "run_id", property = "runId"),
            @Result(column = "timestamp", property = "timestamp"),
            @Result(column = "stream", property = "stream"),
            @Result(column = "message", property = "message")
    })
    List<TaskLogRow> findLogsByRunId(@Param("runId") String runId);
}
