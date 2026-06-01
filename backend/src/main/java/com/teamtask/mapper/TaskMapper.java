package com.teamtask.mapper;

import com.teamtask.entity.Task;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface TaskMapper {

    @Insert("INSERT INTO task(task_name, description, team_id, assignee_id, creator_id, " +
            "status, priority, deadline, create_time, update_time) " +
            "VALUES(#{taskName}, #{description}, #{teamId}, #{assigneeId}, #{creatorId}, " +
            "#{status}, #{priority}, #{deadline}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Task task);

    @Update("UPDATE task SET task_name=#{taskName}, description=#{description}, " +
            "team_id=#{teamId}, assignee_id=#{assigneeId}, status=#{status}, " +
            "priority=#{priority}, deadline=#{deadline}, update_time=NOW() WHERE id=#{id}")
    int update(Task task);

    @Delete("DELETE FROM task WHERE id=#{id}")
    int delete(Long id);

    @Select("SELECT * FROM task WHERE id=#{id}")
    Task findById(Long id);

    @Select("SELECT * FROM task WHERE team_id=#{teamId} ORDER BY create_time DESC")
    List<Task> findByTeamId(Long teamId);

    @Select("SELECT * FROM task WHERE assignee_id=#{assigneeId} ORDER BY create_time DESC")
    List<Task> findByAssigneeId(Long assigneeId);

    @Select("SELECT * FROM task WHERE creator_id=#{creatorId} ORDER BY create_time DESC")
    List<Task> findByCreatorId(Long creatorId);

    @Select("SELECT * FROM task WHERE task_name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR description LIKE CONCAT('%', #{keyword}, '%') ORDER BY create_time DESC")
    List<Task> searchByKeyword(String keyword);

    @Select("SELECT t.id, t.task_name, t.description, t.team_id, t.assignee_id, t.creator_id, " +
            "t.status, t.priority, t.deadline, t.create_time, t.update_time, " +
            "tm.team_name, u1.username as assignee_name, u2.username as creator_name " +
            "FROM task t " +
            "LEFT JOIN team tm ON t.team_id = tm.id " +
            "LEFT JOIN `user` u1 ON t.assignee_id = u1.id " +
            "LEFT JOIN `user` u2 ON t.creator_id = u2.id " +
            "WHERE t.task_name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR t.description LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY t.create_time DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "taskName", column = "task_name"),
        @Result(property = "description", column = "description"),
        @Result(property = "teamId", column = "team_id"),
        @Result(property = "assigneeId", column = "assignee_id"),
        @Result(property = "creatorId", column = "creator_id"),
        @Result(property = "status", column = "status"),
        @Result(property = "priority", column = "priority"),
        @Result(property = "deadline", column = "deadline"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "teamName", column = "team_name"),
        @Result(property = "assigneeName", column = "assignee_name"),
        @Result(property = "creatorName", column = "creator_name")
    })
    List<Task> searchByKeywordWithRelations(String keyword);

    @Select("SELECT * FROM task WHERE status=#{status} ORDER BY create_time DESC")
    List<Task> findByStatus(String status);

    @Select("SELECT * FROM task ORDER BY create_time DESC")
    List<Task> findAll();

    @Select("SELECT t.id, t.task_name, t.description, t.team_id, t.assignee_id, t.creator_id, " +
            "t.status, t.priority, t.deadline, t.create_time, t.update_time, " +
            "tm.team_name, u1.username as assignee_name, u2.username as creator_name " +
            "FROM task t " +
            "LEFT JOIN team tm ON t.team_id = tm.id " +
            "LEFT JOIN `user` u1 ON t.assignee_id = u1.id " +
            "LEFT JOIN `user` u2 ON t.creator_id = u2.id " +
            "ORDER BY t.create_time DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "taskName", column = "task_name"),
        @Result(property = "description", column = "description"),
        @Result(property = "teamId", column = "team_id"),
        @Result(property = "assigneeId", column = "assignee_id"),
        @Result(property = "creatorId", column = "creator_id"),
        @Result(property = "status", column = "status"),
        @Result(property = "priority", column = "priority"),
        @Result(property = "deadline", column = "deadline"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "teamName", column = "team_name"),
        @Result(property = "assigneeName", column = "assignee_name"),
        @Result(property = "creatorName", column = "creator_name")
    })
    List<Task> findAllWithRelations();
}