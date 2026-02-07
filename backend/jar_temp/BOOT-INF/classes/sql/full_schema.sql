-- 完整的数据库初始化脚本
-- 创建所有必要的表结构

-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` VARCHAR(20) NOT NULL COMMENT '学号',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `points` INT DEFAULT 0 COMMENT '积分',
  `upload_count` INT DEFAULT 0 COMMENT '上传资料数',
  `download_count` INT DEFAULT 0 COMMENT '下载资料数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` INT DEFAULT 1 COMMENT '状态：0禁用，1启用',
  `role` INT DEFAULT 0 COMMENT '用户角色：0普通用户，1管理员',
  `email` VARCHAR(100) COMMENT '邮箱',
  `phone` VARCHAR(20) COMMENT '手机号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建分类表
CREATE TABLE IF NOT EXISTS `category` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `description` VARCHAR(200) COMMENT '分类描述',
  `parent_id` BIGINT(20) DEFAULT 0 COMMENT '父分类ID',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` INT DEFAULT 1 COMMENT '状态：0禁用，1启用',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资料分类表';

-- 创建资料表
CREATE TABLE IF NOT EXISTS `material` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(100) NOT NULL COMMENT '资料名称',
  `description` TEXT COMMENT '资料描述',
  `category_id` BIGINT(20) NOT NULL COMMENT '分类ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '上传用户ID',
  `file_path` VARCHAR(255) NOT NULL COMMENT '文件路径',
  `file_name` VARCHAR(100) NOT NULL COMMENT '文件名',
  `file_size` BIGINT(20) DEFAULT 0 COMMENT '文件大小（字节）',
  `download_count` INT DEFAULT 0 COMMENT '下载次数',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `points` INT DEFAULT 0 COMMENT '所需积分',
  `status` INT DEFAULT 1 COMMENT '状态：0禁用，1启用',
  `audit_status` INT DEFAULT 0 COMMENT '审核状态：0待审核，1已通过，2已驳回',
  `agri_tag` INT DEFAULT 0 COMMENT '农科精选标记：0否，1是',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资料表';

-- 创建屏蔽关键词表
CREATE TABLE IF NOT EXISTS `shield_keyword` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `keyword` VARCHAR(50) NOT NULL COMMENT '屏蔽关键词',
  `shield_time` DATETIME NOT NULL COMMENT '屏蔽时间',
  `reason` VARCHAR(200) COMMENT '屏蔽原因',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_keyword` (`keyword`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='屏蔽关键词表';

-- 创建管理员表
CREATE TABLE IF NOT EXISTS `admin` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `status` INT DEFAULT 1 COMMENT '状态：0禁用，1启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 插入一个管理员账号（学号：admin，密码：123456，角色：1）
INSERT INTO `user` (`student_id`, `name`, `password`, `email`, `phone`, `points`, `role`) 
VALUES ('admin', '管理员', 'e10adc3949ba59abbe56e057f20f883e', 'admin@example.com', '13800138000', 0, 1)
ON DUPLICATE KEY UPDATE 
  `name` = VALUES(`name`),
  `password` = VALUES(`password`),
  `email` = VALUES(`email`),
  `phone` = VALUES(`phone`),
  `role` = VALUES(`role`);

-- 插入admin表管理员账号（用户名：admin，密码：123456）
INSERT INTO `admin` (`username`, `password`, `name`, `status`) VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', 1);

-- 插入默认分类
INSERT INTO `category` (`name`, `description`, `parent_id`, `sort`) 
VALUES 
('学习资料', '各类学习资料', 0, 1),
('考试资源', '考试相关资源', 0, 2),
('课件讲义', '课程课件和讲义', 1, 1),
('习题答案', '习题和答案', 1, 2),
('历年真题', '历年考试真题', 2, 1),
('模拟试题', '模拟考试试题', 2, 2)
ON DUPLICATE KEY UPDATE 
  `description` = VALUES(`description`),
  `sort` = VALUES(`sort`);

-- 脚本执行完成
SELECT '数据库初始化完成！' AS message;
SELECT '管理员账号：admin，密码：123456' AS admin_info;