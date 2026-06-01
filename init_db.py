import pymysql

# 数据库连接配置
config = {
    'host': 'localhost',
    'user': 'root',
    'password': '123456',
    'charset': 'utf8mb4'
}

try:
    # 连接数据库服务器
    conn = pymysql.connect(**config)
    cursor = conn.cursor()
    
    # 删除并重建数据库
    cursor.execute("DROP DATABASE IF EXISTS team_task_db")
    cursor.execute("CREATE DATABASE team_task_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    cursor.execute("USE team_task_db")
    
    # 创建用户表
    cursor.execute("""
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
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
    """)
    
    # 创建团队表
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS `team` (
            `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
            `team_name` VARCHAR(100) NOT NULL,
            `description` TEXT,
            `leader_id` BIGINT,
            `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
            `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            INDEX `idx_team_name` (`team_name`),
            INDEX `idx_leader_id` (`leader_id`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
    """)
    
    # 创建任务表
    cursor.execute("""
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
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
    """)
    
    # 创建团队申请表
    cursor.execute("""
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
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
    """)
    
    # 创建团队成员表
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS `team_member` (
            `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
            `team_id` BIGINT NOT NULL,
            `user_id` BIGINT NOT NULL,
            `join_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
            INDEX `idx_team_id` (`team_id`),
            INDEX `idx_user_id` (`user_id`),
            UNIQUE KEY `uk_team_user` (`team_id`, `user_id`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
    """)
    
    # 插入用户数据
    users = [
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
        ('zhengshi', 'zs123456', 'zhengshi@example.com', '13800138011', 'USER')
    ]
    cursor.executemany("INSERT INTO `user` (username, password, email, phone, role) VALUES (%s, %s, %s, %s, %s)", users)
    
    # 插入团队数据（管理员不能成为负责人，负责人必须是团队成员）
    teams = [
        ('前端开发组', '负责前端页面开发和维护', 3),    # user2 作为负责人
        ('后端开发组', '负责后端接口开发和维护', 5),    # lisi 作为负责人
        ('测试组', '负责功能测试和质量保证', 2),        # user1 作为负责人
        ('运维组', '负责服务器运维和部署', 8),          # sunqi 作为负责人
        ('产品组', '负责产品设计和需求分析', 2),        # user1 作为负责人
        ('设计组', '负责UI设计和用户体验优化', 2)       # user1 作为负责人
    ]
    cursor.executemany("INSERT INTO `team` (team_name, description, leader_id) VALUES (%s, %s, %s)", teams)
    
    # 插入团队成员数据（管理员不成为成员，每个成员最多加入2个组）
    team_members = [
        # 前端开发组 (3人)
        (1, 3),  # user2
        (1, 4),  # zhangsan
        (1, 9),  # qianba
        
        # 后端开发组 (3人)
        (2, 5),  # lisi
        (2, 6),  # wangwu
        (2, 10), # zhoujiu
        
        # 测试组 (3人)
        (3, 2),  # user1(负责人)
        (3, 7),  # zhaoliu
        (3, 11), # wujiu
        
        # 运维组 (3人)
        (4, 8),  # sunqi
        (4, 9),  # qianba
        (4, 12), # zhengshi
        
        # 产品组 (2人)
        (5, 2),  # user1(负责人)
        (5, 3),  # user2
        
        # 设计组 (2人)
        (6, 2),  # user1(负责人)
        (6, 12), # zhengshi
    ]
    cursor.executemany("INSERT INTO `team_member` (team_id, user_id) VALUES (%s, %s)", team_members)
    
    # 插入任务数据
    import datetime
    today = datetime.date.today()
    
    tasks = [
        ('完成用户登录模块', '实现用户注册、登录、找回密码等功能', 1, 3, 1, 'IN_PROGRESS', 'HIGH', today + datetime.timedelta(days=7)),
        ('开发任务列表页面', '实现任务的增删改查界面', 1, 4, 1, 'IN_PROGRESS', 'HIGH', today + datetime.timedelta(days=5)),
        ('完成团队管理API', '实现团队的创建、编辑、删除接口', 2, 5, 1, 'IN_PROGRESS', 'MEDIUM', today + datetime.timedelta(days=10)),
        ('开发任务审核功能', '实现任务提交审核流程', 2, 6, 1, 'PENDING', 'HIGH', today + datetime.timedelta(days=14)),
        ('编写单元测试用例', '为后端接口编写单元测试', 3, 7, 2, 'PENDING', 'MEDIUM', today + datetime.timedelta(days=21)),
        ('执行功能测试', '对已完成功能进行系统测试', 3, 3, 2, 'PENDING', 'MEDIUM', today + datetime.timedelta(days=12)),
        ('搭建CI/CD流水线', '配置自动化构建和部署流程', 4, 8, 1, 'IN_PROGRESS', 'HIGH', today + datetime.timedelta(days=15)),
        ('监控系统部署', '部署Prometheus和Grafana监控', 4, 5, 1, 'PENDING', 'MEDIUM', today + datetime.timedelta(days=20)),
        ('完成首页原型设计', '设计新版首页UI原型', 6, 6, 2, 'COMPLETED', 'HIGH', today - datetime.timedelta(days=5)),
        ('优化用户体验', '分析用户行为，优化交互流程', 6, 4, 2, 'PENDING_REVIEW', 'MEDIUM', today + datetime.timedelta(days=8)),
        ('数据库优化', '对核心查询进行索引优化', 2, 8, 1, 'PENDING_REVIEW', 'HIGH', today + datetime.timedelta(days=3)),
        ('完成支付模块对接', '对接第三方支付接口', 2, 7, 1, 'PENDING', 'HIGH', today + datetime.timedelta(days=30))
    ]
    cursor.executemany("INSERT INTO `task` (task_name, description, team_id, assignee_id, creator_id, status, priority, deadline) VALUES (%s, %s, %s, %s, %s, %s, %s, %s)", tasks)
    
    conn.commit()
    print("数据库初始化成功！")
    
    # 验证数据
    cursor.execute("SELECT team_name FROM team LIMIT 3")
    print("团队数据：", cursor.fetchall())
    
    cursor.execute("SELECT task_name FROM task LIMIT 3")
    print("任务数据：", cursor.fetchall())
    
except Exception as e:
    print(f"错误: {e}")
    conn.rollback()
finally:
    cursor.close()
    conn.close()