USE team_task_db;

INSERT INTO `task` (task_name, description, team_id, assignee_id, creator_id, status, priority, deadline) VALUES
('优化前端页面性能', '对前端页面进行性能优化，提升加载速度', 1, NULL, 1, 'PENDING', 'HIGH', DATE_ADD(NOW(), INTERVAL 14 DAY)),
('修复登录页面bug', '修复登录页面的兼容性问题', 1, NULL, 1, 'PENDING', 'MEDIUM', DATE_ADD(NOW(), INTERVAL 7 DAY)),
('开发用户权限模块', '实现用户角色权限管理功能', 2, NULL, 1, 'PENDING', 'HIGH', DATE_ADD(NOW(), INTERVAL 21 DAY)),
('编写API文档', '为所有API接口编写详细文档', 2, NULL, 2, 'PENDING', 'MEDIUM', DATE_ADD(NOW(), INTERVAL 10 DAY)),
('设计测试用例', '为核心功能设计测试用例', 3, NULL, 2, 'PENDING', 'MEDIUM', DATE_ADD(NOW(), INTERVAL 15 DAY)),
('部署测试环境', '搭建新的测试环境', 4, NULL, 1, 'PENDING', 'LOW', DATE_ADD(NOW(), INTERVAL 30 DAY)),
('设计移动端UI', '设计移动端适配的UI界面', 6, NULL, 2, 'PENDING', 'HIGH', DATE_ADD(NOW(), INTERVAL 20 DAY)),
('调研新技术方案', '调研微服务架构方案', 2, NULL, 1, 'PENDING', 'LOW', DATE_ADD(NOW(), INTERVAL 30 DAY)),
('整理技术文档', '整理并更新技术文档', 5, NULL, 2, 'PENDING', 'MEDIUM', DATE_ADD(NOW(), INTERVAL 12 DAY)),
('代码审查', '对核心模块进行代码审查', 1, NULL, 1, 'PENDING', 'MEDIUM', DATE_ADD(NOW(), INTERVAL 8 DAY));