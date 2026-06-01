package com.teamtask.mapper;

import com.teamtask.entity.Notification;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface NotificationMapper {

    @Insert("INSERT INTO notification(type, content, user_id, username, task_id, task_name, create_time, is_read) " +
            "VALUES(#{type}, #{content}, #{userId}, #{username}, #{taskId}, #{taskName}, NOW(), FALSE)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notification notification);

    @Select("SELECT * FROM notification ORDER BY create_time DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "type", column = "type"),
        @Result(property = "content", column = "content"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "username", column = "username"),
        @Result(property = "taskId", column = "task_id"),
        @Result(property = "taskName", column = "task_name"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "isRead", column = "is_read")
    })
    List<Notification> findAll();

    @Select("SELECT * FROM notification WHERE is_read = FALSE ORDER BY create_time DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "type", column = "type"),
        @Result(property = "content", column = "content"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "username", column = "username"),
        @Result(property = "taskId", column = "task_id"),
        @Result(property = "taskName", column = "task_name"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "isRead", column = "is_read")
    })
    List<Notification> findUnread();

    @Update("UPDATE notification SET is_read = TRUE WHERE id = #{id}")
    int markAsRead(Long id);

    @Delete("DELETE FROM notification WHERE id = #{id}")
    int delete(Long id);
}