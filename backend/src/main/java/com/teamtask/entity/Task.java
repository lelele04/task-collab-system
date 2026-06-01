package com.teamtask.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Task {
    private Long id;
    private String taskName;
    private String description;
    private Long teamId;
    private Long assigneeId;
    private Long creatorId;
    private String status;
    private String priority;
    private LocalDateTime deadline;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    private String teamName;
    private String assigneeName;
    private String creatorName;
}