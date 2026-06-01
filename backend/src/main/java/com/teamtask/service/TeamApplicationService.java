package com.teamtask.service;

import com.teamtask.entity.Team;
import com.teamtask.entity.TeamApplication;
import com.teamtask.mapper.TeamApplicationMapper;
import com.teamtask.mapper.TeamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TeamApplicationService {

    @Autowired
    private TeamApplicationMapper applicationMapper;
    
    @Autowired
    private TeamMapper teamMapper;

    public TeamApplication apply(Long teamId, Long userId, String reason, String applyType, String description) {
        if ("JOIN".equals(applyType)) {
            TeamApplication existing = applicationMapper.findByTeamAndUser(teamId, userId);
            if (existing != null && "PENDING".equals(existing.getStatus())) {
                throw new RuntimeException("您已经申请过该团队，请等待管理员审批");
            }
        }
        
        TeamApplication application = new TeamApplication();
        application.setTeamId(teamId);
        application.setUserId(userId);
        application.setStatus("PENDING");
        application.setReason(reason);
        application.setApplyType(applyType);
        application.setDescription(description);
        applicationMapper.insert(application);
        return application;
    }

    public TeamApplication approve(Long applicationId) {
        TeamApplication application = applicationMapper.findById(applicationId);
        if (application == null) {
            throw new RuntimeException("申请不存在");
        }
        
        if ("CREATE".equals(application.getApplyType())) {
            String[] parts = application.getDescription().split("\\|", 2);
            String teamName = parts[0];
            String teamDesc = parts.length > 1 ? parts[1] : "";
            
            Team team = new Team();
            team.setTeamName(teamName);
            team.setDescription(teamDesc);
            team.setLeaderId(application.getUserId());
            teamMapper.insert(team);
            
            application.setTeamId(team.getId());
        }
        
        applicationMapper.updateStatus(applicationId, "APPROVED");
        return applicationMapper.findById(applicationId);
    }

    public TeamApplication reject(Long applicationId) {
        TeamApplication application = applicationMapper.findById(applicationId);
        if (application == null) {
            throw new RuntimeException("申请不存在");
        }
        applicationMapper.updateStatus(applicationId, "REJECTED");
        return applicationMapper.findById(applicationId);
    }

    public TeamApplication getById(Long id) {
        return applicationMapper.findById(id);
    }

    public List<TeamApplication> getByTeamId(Long teamId) {
        return applicationMapper.findByTeamId(teamId);
    }

    public List<TeamApplication> getByUserId(Long userId) {
        return applicationMapper.findByUserId(userId);
    }

    public List<TeamApplication> getPendingApplications() {
        return applicationMapper.findByStatus("PENDING");
    }

    public List<TeamApplication> getAllApplications() {
        return applicationMapper.findAll();
    }
}