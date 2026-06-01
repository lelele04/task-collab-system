package com.teamtask.controller;

import com.teamtask.entity.Team;
import com.teamtask.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teams")
@CrossOrigin(origins = "*")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createTeam(@RequestBody Team team) {
        Map<String, Object> result = new HashMap<>();
        try {
            Team newTeam = teamService.createTeam(team);
            result.put("success", true);
            result.put("message", "团队创建成功");
            result.put("data", newTeam);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTeam(@PathVariable Long id, @RequestBody Team team) {
        Map<String, Object> result = new HashMap<>();
        try {
            team.setId(id);
            Team updatedTeam = teamService.updateTeam(team);
            result.put("success", true);
            result.put("message", "团队更新成功");
            result.put("data", updatedTeam);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTeam(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            teamService.deleteTeam(id);
            result.put("success", true);
            result.put("message", "团队删除成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTeamById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        Team team = teamService.getTeamById(id);
        if (team != null) {
            result.put("success", true);
            result.put("data", team);
        } else {
            result.put("success", false);
            result.put("message", "团队不存在");
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/leader/{leaderId}")
    public ResponseEntity<Map<String, Object>> getTeamsByLeaderId(@PathVariable Long leaderId) {
        Map<String, Object> result = new HashMap<>();
        List<Team> teams = teamService.getTeamsByLeaderId(leaderId);
        result.put("success", true);
        result.put("data", teams);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchTeams(@RequestParam String keyword) {
        Map<String, Object> result = new HashMap<>();
        List<Team> teams = teamService.searchTeams(keyword);
        result.put("success", true);
        result.put("data", teams);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTeams() {
        Map<String, Object> result = new HashMap<>();
        List<Team> teams = teamService.getAllTeams();
        result.put("success", true);
        result.put("data", teams);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/join")
    public ResponseEntity<Map<String, Object>> joinTeam(@RequestParam Long teamId, @RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            teamService.addMember(teamId, userId);
            result.put("success", true);
            result.put("message", "加入团队成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}