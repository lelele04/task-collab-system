package com.teamtask.service;

import com.teamtask.entity.Notification;
import com.teamtask.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    public void notifyUserRegister(Long userId, String username) {
        Notification notification = new Notification();
        notification.setType("USER_REGISTER");
        notification.setContent("新用户注册: " + username + " (ID: " + userId + ")");
        notification.setUserId(userId);
        notification.setUsername(username);
        notificationMapper.insert(notification);
    }

    public void notifyTaskClaimed(Long taskId, String taskName, Long userId, String username) {
        Notification notification = new Notification();
        notification.setType("TASK_CLAIMED");
        notification.setContent("任务被领取: " + taskName + " 被 " + username + " 领取");
        notification.setTaskId(taskId);
        notification.setTaskName(taskName);
        notification.setUserId(userId);
        notification.setUsername(username);
        notificationMapper.insert(notification);
    }

    public List<Notification> getAllNotifications() {
        return notificationMapper.findAll();
    }

    public List<Notification> getUnreadNotifications() {
        return notificationMapper.findUnread();
    }

    public void markAsRead(Long notificationId) {
        notificationMapper.markAsRead(notificationId);
    }

    public void deleteNotification(Long notificationId) {
        notificationMapper.delete(notificationId);
    }
}