package com.teamtask.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TeamApplication {
    private Long id;
    private Long teamId;
    private Long userId;
    private String status;
    private String reason;
    private LocalDateTime applyTime;
    private LocalDateTime approveTime;

    private String teamName;
    private String userName;
    
    private String applyType;
    private String description;
}