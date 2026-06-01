package com.teamtask.service;

import com.teamtask.entity.Team;
import com.teamtask.entity.User;
import com.teamtask.mapper.TeamMapper;
import com.teamtask.mapper.TeamMemberMapper;
import com.teamtask.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class TeamService {

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private TeamMemberMapper teamMemberMapper;

    @Autowired
    private UserMapper userMapper;

    public Team createTeam(Team team) {
        teamMapper.insert(team);
        teamMemberMapper.insert(team.getId(), team.getLeaderId());
        return teamMapper.findById(team.getId());
    }

    public Team updateTeam(Team team) {
        teamMapper.update(team);
        return teamMapper.findById(team.getId());
    }

    public void deleteTeam(Long id) {
        teamMapper.delete(id);
    }

    public Team getTeamById(Long id) {
        Team team = teamMapper.findById(id);
        if (team != null) {
            team.setMembers(getTeamMembers(id));
            team.setLeader(getLeader(team.getLeaderId()));
            team.setLeaderName(getLeaderName(team.getLeaderId()));
        }
        return team;
    }

    public List<Team> getTeamsByLeaderId(Long leaderId) {
        List<Team> teams = teamMapper.findByLeaderId(leaderId);
        teams.forEach(team -> {
            team.setMembers(getTeamMembers(team.getId()));
            team.setLeader(getLeader(team.getLeaderId()));
        });
        return teams;
    }

    public List<Team> searchTeams(String keyword) {
        List<Team> teams = teamMapper.searchByKeyword(keyword);
        teams.forEach(team -> {
            team.setMembers(getTeamMembers(team.getId()));
            team.setLeader(getLeader(team.getLeaderId()));
        });
        return teams;
    }

    public List<Team> getAllTeams() {
        List<Team> teams = teamMapper.findAll();
        teams.forEach(team -> {
            team.setMembers(getTeamMembers(team.getId()));
            team.setLeader(getLeader(team.getLeaderId()));
            team.setLeaderName(getLeaderName(team.getLeaderId()));
        });
        return teams;
    }

    public void addMember(Long teamId, Long userId) {
        if (teamMemberMapper.exists(teamId, userId) > 0) {
            throw new RuntimeException("您已加入该团队");
        }
        teamMemberMapper.insert(teamId, userId);
    }

    public void removeMember(Long teamId, Long userId) {
        teamMemberMapper.delete(teamId, userId);
    }

    private List<User> getTeamMembers(Long teamId) {
        List<Long> userIds = teamMemberMapper.findUserIdsByTeamId(teamId);
        List<User> members = new ArrayList<>();
        for (Long userId : userIds) {
            User user = userMapper.findById(userId);
            if (user != null) {
                members.add(user);
            }
        }
        return members;
    }

    private User getLeader(Long leaderId) {
        if (leaderId == null) {
            return null;
        }
        return userMapper.findById(leaderId);
    }

    private String getLeaderName(Long leaderId) {
        if (leaderId == null) {
            return "未知";
        }
        User leader = userMapper.findById(leaderId);
        return leader != null ? leader.getUsername() : "未知";
    }
}