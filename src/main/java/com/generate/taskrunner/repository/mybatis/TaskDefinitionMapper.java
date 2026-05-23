package com.generate.taskrunner.repository.mybatis;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface TaskDefinitionMapper {

    @Insert("""
            MERGE INTO task_definitions KEY(id)
            VALUES (#{id}, #{name}, #{command}, #{workingDirectory}, #{timeoutMillis}, #{createdAt})
            """)
    void upsert(TaskDefinitionRow row);

    @Select("""
            SELECT id, name, command, working_directory, timeout_millis, created_at
            FROM task_definitions
            WHERE id = #{id}
            """)
    @Results(id = "TaskDefinitionRowResult", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "name", property = "name"),
            @Result(column = "command", property = "command"),
            @Result(column = "working_directory", property = "workingDirectory"),
            @Result(column = "timeout_millis", property = "timeoutMillis"),
            @Result(column = "created_at", property = "createdAt")
    })
    Optional<TaskDefinitionRow> findById(@Param("id") String id);
}
