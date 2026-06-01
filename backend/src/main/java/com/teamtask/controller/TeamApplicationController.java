package com.teamtask.controller;

import com.teamtask.entity.TeamApplication;
import com.teamtask.service.TeamApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/team-applications")
@CrossOrigin(origins = "*")
public class TeamApplicationController {

    @Autowired
    private TeamApplicationService applicationService;

    @PostMapping("/apply")
    public ResponseEntity<Map<String, Object>> apply(
            @RequestParam Long teamId,
            @RequestParam Long userId,
            @RequestParam(required = false) String reason) {
        Map<String, Object> result = new HashMap<>();
        try {
            TeamApplication application = applicationService.apply(teamId, userId, reason, "JOIN", null);
            result.put("success", true);
            result.put("message", "申请提交成功，请等待管理员审批");
            result.put("data", application);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/apply-create")
    public ResponseEntity<Map<String, Object>> applyCreateTeam(
            @RequestParam Long userId,
            @RequestParam String teamName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String reason) {
        Map<String, Object> result = new HashMap<>();
        try {
            TeamApplication application = applicationService.apply(null, userId, reason, "CREATE", teamName + "|" + (description != null ? description : ""));
            result.put("success", true);
            result.put("message", "创建团队申请提交成功，请等待管理员审批");
            result.put("data", application);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Map<String, Object>> approve(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            TeamApplication application = applicationService.approve(id);
            result.put("success", true);
            result.put("message", "审批通过");
            result.put("data", application);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> reject(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            TeamApplication application = applicationService.reject(id);
            result.put("success", true);
            result.put("message", "已拒绝申请");
            result.put("data", application);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<Map<String, Object>> getPendingApplications() {
        Map<String, Object> result = new HashMap<>();
        List<TeamApplication> applications = applicationService.getPendingApplications();
        result.put("success", true);
        result.put("data", applications);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<Map<String, Object>> getApplicationsByTeamId(@PathVariable Long teamId) {
        Map<String, Object> result = new HashMap<>();
        List<TeamApplication> applications = applicationService.getByTeamId(teamId);
        result.put("success", true);
        result.put("data", applications);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getApplicationsByUserId(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        List<TeamApplication> applications = applicationService.getByUserId(userId);
        result.put("success", true);
        result.put("data", applications);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        TeamApplication application = applicationService.getById(id);
        if (application != null) {
            result.put("success", true);
            result.put("data", application);
        } else {
            result.put("success", false);
            result.put("message", "申请不存在");
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllApplications() {
        Map<String, Object> result = new HashMap<>();
        List<TeamApplication> applications = applicationService.getAllApplications();
        result.put("success", true);
        result.put("data", applications);
        return ResponseEntity.ok(result);
    }
}