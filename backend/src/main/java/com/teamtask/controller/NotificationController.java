package com.teamtask.controller;

import com.teamtask.entity.Notification;
import com.teamtask.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllNotifications() {
        Map<String, Object> result = new HashMap<>();
        List<Notification> notifications = notificationService.getAllNotifications();
        result.put("success", true);
        result.put("data", notifications);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/unread")
    public ResponseEntity<Map<String, Object>> getUnreadNotifications() {
        Map<String, Object> result = new HashMap<>();
        List<Notification> notifications = notificationService.getUnreadNotifications();
        result.put("success", true);
        result.put("data", notifications);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        notificationService.markAsRead(id);
        result.put("success", true);
        result.put("message", "标记已读成功");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteNotification(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        notificationService.deleteNotification(id);
        result.put("success", true);
        result.put("message", "删除成功");
        return ResponseEntity.ok(result);
    }
}