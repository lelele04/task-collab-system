USE team_task_db;

INSERT INTO `task` (task_name, description, team_id, assignee_id, creator_id, status, priority, deadline) VALUES
('编写项目文档', '编写项目技术文档和API文档', 3, 2, 1, 'IN_PROGRESS', 'MEDIUM', DATE_ADD(NOW(), INTERVAL 10 DAY)),
('测试用例评审', '评审测试用例的完整性和覆盖率', 3, 2, 2, 'IN_PROGRESS', 'LOW', DATE_ADD(NOW(), INTERVAL 5 DAY)),
('整理需求文档', '整理和归档产品需求文档', 5, 2, 2, 'PENDING_REVIEW', 'MEDIUM', DATE_ADD(NOW(), INTERVAL 3 DAY)),
('设计系统架构图', '设计系统整体架构和模块划分', 5, 2, 1, 'COMPLETED', 'HIGH', DATE_ADD(NOW(), INTERVAL -3 DAY));