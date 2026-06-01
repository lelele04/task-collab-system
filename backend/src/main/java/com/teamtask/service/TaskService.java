package com.teamtask.service;

import com.teamtask.entity.Task;
import com.teamtask.entity.User;
import com.teamtask.mapper.TaskMapper;
import com.teamtask.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationService notificationService;

    public Task createTask(Task task) {
        if (task.getTaskName() == null || task.getTaskName().trim().isEmpty()) {
            throw new RuntimeException("任务名称不能为空");
        }
        if (task.getDeadline() == null) {
            throw new RuntimeException("截止日期不能为空");
        }
        if (task.getStatus() == null) {
            task.setStatus("PENDING");
        }
        taskMapper.insert(task);
        return taskMapper.findById(task.getId());
    }

    public Task updateTask(Task task) {
        if (task.getTeamId() == null) {
            throw new RuntimeException("团队不能为空，请选择团队");
        }
        if (task.getAssigneeId() == null) {
            throw new RuntimeException("负责人不能为空，请选择负责人");
        }
        taskMapper.update(task);
        return taskMapper.findById(task.getId());
    }

    public void deleteTask(Long id) {
        taskMapper.delete(id);
    }

    public Task getTaskById(Long id) {
        return taskMapper.findById(id);
    }

    public List<Task> getTasksByTeamId(Long teamId) {
        return taskMapper.findByTeamId(teamId);
    }

    public List<Task> getTasksByAssigneeId(Long assigneeId) {
        return taskMapper.findByAssigneeId(assigneeId);
    }

    public List<Task> getTasksByCreatorId(Long creatorId) {
        return taskMapper.findByCreatorId(creatorId);
    }

    public List<Task> searchTasks(String keyword) {
        return taskMapper.searchByKeywordWithRelations(keyword);
    }

    public List<Task> getTasksByStatus(String status) {
        return taskMapper.findByStatus(status);
    }

    public List<Task> getAllTasks() {
        return taskMapper.findAllWithRelations();
    }

    public Task updateTaskStatus(Long taskId, String status) {
        Task task = taskMapper.findById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        if ("COMPLETED".equals(status)) {
            if (task.getTeamId() == null) {
                throw new RuntimeException("任务必须分配团队才能完成");
            }
            if (task.getAssigneeId() == null) {
                throw new RuntimeException("任务必须分配负责人才能完成");
            }
        }
        task.setStatus(status);
        taskMapper.update(task);
        return task;
    }

    public Task claimTask(Long taskId, Long userId) {
        Task task = taskMapper.findById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }

        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if ("ADMIN".equals(user.getRole())) {
            throw new RuntimeException("管理员不能领取任务");
        }

        if (task.getAssigneeId() != null) {
            throw new RuntimeException("该任务已被领取");
        }

        List<Task> claimedTasks = taskMapper.findByAssigneeId(userId);
        if (claimedTasks.size() >= 5) {
            throw new RuntimeException("每人最多只能领取5个任务");
        }

        task.setAssigneeId(userId);
        task.setStatus("IN_PROGRESS");
        taskMapper.update(task);

        Task updatedTask = taskMapper.findById(taskId);
        if (user != null) {
            notificationService.notifyTaskClaimed(taskId, updatedTask.getTaskName(), userId, user.getUsername());
        }

        return updatedTask;
    }

    public int getClaimedTaskCount(Long userId) {
        List<Task> claimedTasks = taskMapper.findByAssigneeId(userId);
        return claimedTasks.size();
    }
}