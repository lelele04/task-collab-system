package com.teamtask.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Team {
    private Long id;
    private String teamName;
    private String description;
    private Long leaderId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<User> members;
    private User leader;
    private String leaderName;
}