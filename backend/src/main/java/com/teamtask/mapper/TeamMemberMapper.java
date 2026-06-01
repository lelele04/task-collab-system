package com.teamtask.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface TeamMemberMapper {

    @Insert("INSERT INTO team_member(team_id, user_id) VALUES(#{teamId}, #{userId})")
    int insert(Long teamId, Long userId);

    @Delete("DELETE FROM team_member WHERE team_id = #{teamId} AND user_id = #{userId}")
    int delete(Long teamId, Long userId);

    @Select("SELECT user_id FROM team_member WHERE team_id = #{teamId}")
    List<Long> findUserIdsByTeamId(Long teamId);

    @Select("SELECT team_id FROM team_member WHERE user_id = #{userId}")
    List<Long> findTeamIdsByUserId(Long userId);

    @Select("SELECT COUNT(*) FROM team_member WHERE team_id = #{teamId} AND user_id = #{userId}")
    int exists(Long teamId, Long userId);
}