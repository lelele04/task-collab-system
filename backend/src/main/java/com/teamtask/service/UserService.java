package com.teamtask.service;

import com.teamtask.entity.User;
import com.teamtask.mapper.UserMapper;
import com.teamtask.dto.LoginDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationService notificationService;

    public User register(User user) {
        User existUser = userMapper.findByUsername(user.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        user.setRole("USER");
        userMapper.insert(user);

        notificationService.notifyUserRegister(user.getId(), user.getUsername());

        return user;
    }

    public String login(LoginDTO loginDTO) {
        User user = userMapper.findByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!user.getPassword().equals(loginDTO.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return generateToken(user);
    }

    private String generateToken(User user) {
        return "token_" + user.getId() + "_" + System.currentTimeMillis();
    }

    public User getUserById(Long id) {
        return userMapper.findById(id);
    }

    public User getUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public List<User> searchUsers(String keyword) {
        return userMapper.searchByKeyword(keyword);
    }

    public List<User> getAllUsers() {
        return userMapper.findAll();
    }

    public User updateUser(User user) {
        userMapper.update(user);
        return userMapper.findById(user.getId());
    }

    public void deleteUser(Long id) {
        userMapper.delete(id);
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.findById(userId);
        if (!user.getPassword().equals(oldPassword)) {
            throw new RuntimeException("原密码错误");
        }
        userMapper.updatePassword(userId, newPassword);
    }
}