package com.teamtask.mapper;

import com.teamtask.entity.TeamApplication;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface TeamApplicationMapper {

    @Insert("INSERT INTO team_application(team_id, user_id, status, reason, apply_time, apply_type, description) " +
            "VALUES(#{teamId}, #{userId}, #{status}, #{reason}, NOW(), #{applyType}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TeamApplication application);

    @Update("UPDATE team_application SET status=#{status}, approve_time=NOW() WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Select("SELECT * FROM team_application WHERE team_id=#{teamId} AND user_id=#{userId}")
    TeamApplication findByTeamAndUser(@Param("teamId") Long teamId, @Param("userId") Long userId);

    @Select("SELECT a.id, a.team_id, a.user_id, a.status, a.reason, a.apply_time, a.approve_time, " +
            "t.team_name, u.username " +
            "FROM team_application a " +
            "LEFT JOIN team t ON a.team_id = t.id " +
            "LEFT JOIN `user` u ON a.user_id = u.id " +
            "WHERE a.team_id = #{teamId} " +
            "ORDER BY a.apply_time DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "teamId", column = "team_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "status", column = "status"),
        @Result(property = "reason", column = "reason"),
        @Result(property = "applyTime", column = "apply_time"),
        @Result(property = "approveTime", column = "approve_time"),
        @Result(property = "teamName", column = "team_name"),
        @Result(property = "userName", column = "username")
    })
    List<TeamApplication> findByTeamId(Long teamId);

    @Select("SELECT a.id, a.team_id, a.user_id, a.status, a.reason, a.apply_time, a.approve_time, " +
            "t.team_name, u.username " +
            "FROM team_application a " +
            "LEFT JOIN team t ON a.team_id = t.id " +
            "LEFT JOIN `user` u ON a.user_id = u.id " +
            "WHERE a.user_id = #{userId} " +
            "ORDER BY a.apply_time DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "teamId", column = "team_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "status", column = "status"),
        @Result(property = "reason", column = "reason"),
        @Result(property = "applyTime", column = "apply_time"),
        @Result(property = "approveTime", column = "approve_time"),
        @Result(property = "teamName", column = "team_name"),
        @Result(property = "userName", column = "username")
    })
    List<TeamApplication> findByUserId(Long userId);

    @Select("SELECT a.id, a.team_id, a.user_id, a.status, a.reason, a.apply_time, a.approve_time, a.apply_type, a.description, " +
            "IF(a.apply_type = 'CREATE', SUBSTRING_INDEX(a.description, '|', 1), t.team_name) as team_name, u.username " +
            "FROM team_application a " +
            "LEFT JOIN team t ON a.team_id = t.id " +
            "LEFT JOIN `user` u ON a.user_id = u.id " +
            "ORDER BY a.apply_time DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "teamId", column = "team_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "status", column = "status"),
        @Result(property = "reason", column = "reason"),
        @Result(property = "applyTime", column = "apply_time"),
        @Result(property = "approveTime", column = "approve_time"),
        @Result(property = "applyType", column = "apply_type"),
        @Result(property = "description", column = "description"),
        @Result(property = "teamName", column = "team_name"),
        @Result(property = "userName", column = "username")
    })
    List<TeamApplication> findAll();

    @Select("SELECT a.id, a.team_id, a.user_id, a.status, a.reason, a.apply_time, a.approve_time, a.apply_type, a.description, " +
            "IF(a.apply_type = 'CREATE', SUBSTRING_INDEX(a.description, '|', 1), t.team_name) as team_name, u.username " +
            "FROM team_application a " +
            "LEFT JOIN team t ON a.team_id = t.id " +
            "LEFT JOIN `user` u ON a.user_id = u.id " +
            "WHERE a.status = #{status} " +
            "ORDER BY a.apply_time DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "teamId", column = "team_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "status", column = "status"),
        @Result(property = "reason", column = "reason"),
        @Result(property = "applyTime", column = "apply_time"),
        @Result(property = "approveTime", column = "approve_time"),
        @Result(property = "applyType", column = "apply_type"),
        @Result(property = "description", column = "description"),
        @Result(property = "teamName", column = "team_name"),
        @Result(property = "userName", column = "username")
    })
    List<TeamApplication> findByStatus(String status);

    @Select("SELECT a.id, a.team_id, a.user_id, a.status, a.reason, a.apply_time, a.approve_time, " +
            "t.team_name, u.username " +
            "FROM team_application a " +
            "LEFT JOIN team t ON a.team_id = t.id " +
            "LEFT JOIN `user` u ON a.user_id = u.id " +
            "WHERE a.id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "teamId", column = "team_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "status", column = "status"),
        @Result(property = "reason", column = "reason"),
        @Result(property = "applyTime", column = "apply_time"),
        @Result(property = "approveTime", column = "approve_time"),
        @Result(property = "teamName", column = "team_name"),
        @Result(property = "userName", column = "username")
    })
    TeamApplication findById(Long id);
}