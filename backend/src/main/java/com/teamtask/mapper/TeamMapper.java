package com.teamtask.mapper;

import com.teamtask.entity.Team;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface TeamMapper {

    @Insert("INSERT INTO team(team_name, description, leader_id, create_time, update_time) " +
            "VALUES(#{teamName}, #{description}, #{leaderId}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Team team);

    @Update("UPDATE team SET team_name=#{teamName}, description=#{description}, " +
            "update_time=NOW() WHERE id=#{id}")
    int update(Team team);

    @Delete("DELETE FROM team WHERE id=#{id}")
    int delete(Long id);

    @Select("SELECT * FROM team WHERE id=#{id}")
    Team findById(Long id);

    @Select("SELECT * FROM team WHERE leader_id=#{leaderId}")
    List<Team> findByLeaderId(Long leaderId);

    @Select("SELECT * FROM team WHERE team_name LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY create_time DESC")
    List<Team> searchByKeyword(String keyword);

    @Select("SELECT * FROM team ORDER BY create_time DESC")
    List<Team> findAll();
}