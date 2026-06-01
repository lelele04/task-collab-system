CREATE DATABASE IF NOT EXISTS team_task_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE team_task_db;

CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100),
  `phone` VARCHAR(20),
  `role` VARCHAR(20) DEFAULT 'USER',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_username` (`username`),
  INDEX `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `team` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `team_name` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `leader_id` BIGINT,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_team_name` (`team_name`),
  INDEX `idx_leader_id` (`leader_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `task` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `task_name` VARCHAR(200) NOT NULL,
  `description` TEXT,
  `team_id` BIGINT,
  `assignee_id` BIGINT,
  `creator_id` BIGINT,
  `status` VARCHAR(20) DEFAULT 'PENDING',
  `priority` VARCHAR(20) DEFAULT 'MEDIUM',
  `deadline` DATETIME,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_task_name` (`task_name`),
  INDEX `idx_team_id` (`team_id`),
  INDEX `idx_assignee_id` (`assignee_id`),
  INDEX `idx_creator_id` (`creator_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `team_application` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `team_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `status` VARCHAR(20) DEFAULT 'PENDING',
  `reason` TEXT,
  `apply_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `approve_time` DATETIME,
  `apply_type` VARCHAR(20) DEFAULT 'JOIN',
  `description` TEXT,
  INDEX `idx_team_id` (`team_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `team_member` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `team_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `join_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_team_id` (`team_id`),
  INDEX `idx_user_id` (`user_id`),
  UNIQUE KEY `uk_team_user` (`team_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `notification` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `type` VARCHAR(50) NOT NULL,
  `content` TEXT NOT NULL,
  `user_id` BIGINT,
  `username` VARCHAR(50),
  `task_id` BIGINT,
  `task_name` VARCHAR(200),
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_read` BOOLEAN DEFAULT FALSE,
  INDEX `idx_type` (`type`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_task_id` (`task_id`),
  INDEX `idx_is_read` (`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `user` (username, password, email, phone, role) VALUES
('admin', 'admin123', 'admin@example.com', '13800138000', 'ADMIN'),
('user1', 'user123', 'user1@example.com', '13800138001', 'USER'),
('user2', 'user123', 'user2@example.com', '13800138002', 'USER'),
('zhangsan', 'zs123456', 'zhangsan@example.com', '13800138003', 'USER'),
('lisi', 'ls123456', 'lisi@example.com', '13800138004', 'USER'),
('wangwu', 'ww123456', 'wangwu@example.com', '13800138005', 'USER'),
('zhaoliu', 'zl123456', 'zhaoliu@example.com', '13800138006', 'USER'),
('sunqi', 'sq123456', 'sunqi@example.com', '13800138007', 'USER'),
('qianba', 'qb123456', 'qianba@example.com', '13800138008', 'USER'),
('zhoujiu', 'zj123456', 'zhoujiu@example.com', '13800138009', 'USER'),
('wujiu', 'wj123456', 'wujiu@example.com', '13800138010', 'USER'),
('zhengshi', 'zs123456', 'zhengshi@example.com', '13800138011', 'USER');

INSERT INTO `team` (team_name, description, leader_id) VALUES
('前端开发组', '负责前端页面开发和维护', 3),
('后端开发组', '负责后端接口开发和维护', 5),
('测试组', '负责功能测试和质量保证', 2),
('运维组', '负责服务器运维和部署', 8),
('产品组', '负责产品设计和需求分析', 2),
('设计组', '负责UI设计和用户体验优化', 2);

INSERT INTO `team_member` (team_id, user_id) VALUES
-- 前端开发组
(1, 3), (1, 4), (1, 9),
-- 后端开发组
(2, 5), (2, 6), (2, 10),
-- 测试组
(3, 2), (3, 7), (3, 11),
-- 运维组
(4, 8), (4, 9), (4, 12),
-- 产品组
(5, 2), (5, 3),
-- 设计组
(6, 2), (6, 12);

INSERT INTO `task` (task_name, description, team_id, assignee_id, creator_id, status, priority, deadline) VALUES
('完成用户登录模块', '实现用户注册、登录、找回密码等功能', 1, 3, 1, 'IN_PROGRESS', 'HIGH', DATE_ADD(NOW(), INTERVAL 7 DAY)),
('开发任务列表页面', '实现任务的增删改查界面', 1, 4, 1, 'IN_PROGRESS', 'HIGH', DATE_ADD(NOW(), INTERVAL 5 DAY)),
('完成团队管理API', '实现团队的创建、编辑、删除接口', 2, 5, 1, 'IN_PROGRESS', 'MEDIUM', DATE_ADD(NOW(), INTERVAL 10 DAY)),
('开发任务审核功能', '实现任务提交审核流程', 2, 6, 1, 'PENDING', 'HIGH', DATE_ADD(NOW(), INTERVAL 14 DAY)),
('编写单元测试用例', '为后端接口编写单元测试', 3, 7, 2, 'PENDING', 'MEDIUM', DATE_ADD(NOW(), INTERVAL 21 DAY)),
('执行功能测试', '对已完成功能进行系统测试', 3, 3, 2, 'PENDING', 'MEDIUM', DATE_ADD(NOW(), INTERVAL 12 DAY)),
('搭建CI/CD流水线', '配置自动化构建和部署流程', 4, 8, 1, 'IN_PROGRESS', 'HIGH', DATE_ADD(NOW(), INTERVAL 15 DAY)),
('监控系统部署', '部署Prometheus和Grafana监控', 4, 5, 1, 'PENDING', 'MEDIUM', DATE_ADD(NOW(), INTERVAL 20 DAY)),
('完成首页原型设计', '设计新版首页UI原型', 6, 6, 2, 'COMPLETED', 'HIGH', DATE_ADD(NOW(), INTERVAL -5 DAY)),
('优化用户体验', '分析用户行为，优化交互流程', 6, 4, 2, 'PENDING_REVIEW', 'MEDIUM', DATE_ADD(NOW(), INTERVAL 8 DAY)),
('数据库优化', '对核心查询进行索引优化', 2, 8, 1, 'PENDING_REVIEW', 'HIGH', DATE_ADD(NOW(), INTERVAL 3 DAY)),
('完成支付模块对接', '对接第三方支付接口', 2, 7, 1, 'PENDING', 'HIGH', DATE_ADD(NOW(), INTERVAL 30 DAY)),
-- user1 的任务
('编写项目文档', '编写项目技术文档和API文档', 3, 2, 1, 'IN_PROGRESS', 'MEDIUM', DATE_ADD(NOW(), INTERVAL 10 DAY)),
('测试用例评审', '评审测试用例的完整性和覆盖率', 3, 2, 2, 'IN_PROGRESS', 'LOW', DATE_ADD(NOW(), INTERVAL 5 DAY)),
('整理需求文档', '整理和归档产品需求文档', 5, 2, 2, 'PENDING_REVIEW', 'MEDIUM', DATE_ADD(NOW(), INTERVAL 3 DAY)),
('设计系统架构图', '设计系统整体架构和模块划分', 5, 2, 1, 'COMPLETED', 'HIGH', DATE_ADD(NOW(), INTERVAL -3 DAY));