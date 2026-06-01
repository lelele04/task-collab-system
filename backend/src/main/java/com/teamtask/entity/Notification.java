package com.teamtask.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Notification {
    private Long id;
    private String type;
    private String content;
    private Long userId;
    private String username;
    private Long taskId;
    private String taskName;
    private LocalDateTime createTime;
    private Boolean isRead;
}