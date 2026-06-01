package com.teamtask.controller;

import com.teamtask.entity.Task;
import com.teamtask.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createTask(@RequestBody Task task) {
        Map<String, Object> result = new HashMap<>();
        try {
            Task newTask = taskService.createTask(task);
            result.put("success", true);
            result.put("message", "任务创建成功");
            result.put("data", newTask);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTask(@PathVariable Long id, @RequestBody Task task) {
        Map<String, Object> result = new HashMap<>();
        try {
            task.setId(id);
            Task updatedTask = taskService.updateTask(task);
            result.put("success", true);
            result.put("message", "任务更新成功");
            result.put("data", updatedTask);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTask(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            taskService.deleteTask(id);
            result.put("success", true);
            result.put("message", "任务删除成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTaskById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        Task task = taskService.getTaskById(id);
        if (task != null) {
            result.put("success", true);
            result.put("data", task);
        } else {
            result.put("success", false);
            result.put("message", "任务不存在");
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<Map<String, Object>> getTasksByTeamId(@PathVariable Long teamId) {
        Map<String, Object> result = new HashMap<>();
        List<Task> tasks = taskService.getTasksByTeamId(teamId);
        result.put("success", true);
        result.put("data", tasks);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/assignee/{assigneeId}")
    public ResponseEntity<Map<String, Object>> getTasksByAssigneeId(@PathVariable Long assigneeId) {
        Map<String, Object> result = new HashMap<>();
        List<Task> tasks = taskService.getTasksByAssigneeId(assigneeId);
        result.put("success", true);
        result.put("data", tasks);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<Map<String, Object>> getTasksByCreatorId(@PathVariable Long creatorId) {
        Map<String, Object> result = new HashMap<>();
        List<Task> tasks = taskService.getTasksByCreatorId(creatorId);
        result.put("success", true);
        result.put("data", tasks);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchTasks(@RequestParam String keyword) {
        Map<String, Object> result = new HashMap<>();
        List<Task> tasks = taskService.searchTasks(keyword);
        result.put("success", true);
        result.put("data", tasks);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getTasksByStatus(@PathVariable String status) {
        Map<String, Object> result = new HashMap<>();
        List<Task> tasks = taskService.getTasksByStatus(status);
        result.put("success", true);
        result.put("data", tasks);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTasks() {
        Map<String, Object> result = new HashMap<>();
        List<Task> tasks = taskService.getAllTasks();
        result.put("success", true);
        result.put("data", tasks);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateTaskStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Map<String, Object> result = new HashMap<>();
        try {
            Task updatedTask = taskService.updateTaskStatus(id, status);
            result.put("success", true);
            result.put("message", "状态更新成功");
            result.put("data", updatedTask);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PatchMapping("/{id}/claim")
    public ResponseEntity<Map<String, Object>> claimTask(
            @PathVariable Long id,
            @RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Task task = taskService.claimTask(id, userId);
            result.put("success", true);
            result.put("message", "任务领取成功");
            result.put("data", task);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}