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
  `grade` VARCHAR(20) COMMENT '年级',
  `major` VARCHAR(50) COMMENT '专业',
  `college` VARCHAR(100) COMMENT '学院',
  `class_name` VARCHAR(50) COMMENT '班级',
  `avatar` VARCHAR(255) COMMENT '头像',
  `last_avatar_update` DATETIME COMMENT '上次修改头像时间',
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
  KEY `idx_parent_id` (`parent_id`),
  UNIQUE KEY `uk_name_parent_id` (`name`, `parent_id`)
  -- 暂时注释外键约束，避免插入顶级分类时的错误
  -- CONSTRAINT `fk_category_parent` FOREIGN KEY (`parent_id`) REFERENCES `category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资料分类表';

-- 为分类表添加唯一键约束（如果不存在）
SET @exist = (SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = 'material_platform' AND table_name = 'category' AND index_name = 'uk_name_parent_id');
SET @sql = IF(@exist = 0, 'ALTER TABLE `category` ADD UNIQUE KEY `uk_name_parent_id` (`name`, `parent_id`)', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

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
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_material_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_material_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
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

-- 创建问题表
CREATE TABLE IF NOT EXISTS `question` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `title` VARCHAR(100) NOT NULL COMMENT '问题标题',
  `content` TEXT NOT NULL COMMENT '问题内容',
  `tags` VARCHAR(255) COMMENT '标签（逗号分隔）',
  `view_count` INT DEFAULT 0 COMMENT '浏览量',
  `answer_count` INT DEFAULT 0 COMMENT '回答数',
  `like_count` INT DEFAULT 0 COMMENT '点赞数',
  `is_featured` INT DEFAULT 0 COMMENT '是否精选：0否，1是',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_question_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问题表';

-- 创建积分表
CREATE TABLE IF NOT EXISTS `integral` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `total_points` INT DEFAULT 0 COMMENT '总积分',
  `upload_count` INT DEFAULT 0 COMMENT '上传次数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  CONSTRAINT `fk_integral_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分表';

-- 创建积分记录表
CREATE TABLE IF NOT EXISTS `points_record` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `type` INT DEFAULT 0 COMMENT '类型：0下载，1上传，2提问，3回答',
  `points` INT DEFAULT 0 COMMENT '积分变动',
  `related_id` BIGINT(20) COMMENT '关联ID（资料ID或问题ID）',
  `reason` VARCHAR(200) COMMENT '变动原因',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_points_record_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分记录表';

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
INSERT INTO `user` (`student_id`, `name`, `password`, `points`, `role`, `grade`, `major`, `college`, `avatar`) 
VALUES ('admin', '管理员', 'e10adc3949ba59abbe56e057f20f883e', 0, 1, '2021', '计算机科学与技术', '信息科学与工程学院', 'default.jpg')
ON DUPLICATE KEY UPDATE 
  `name` = VALUES(`name`),
  `password` = VALUES(`password`),
  `role` = VALUES(`role`),
  `grade` = VALUES(`grade`),
  `major` = VALUES(`major`),
  `college` = VALUES(`college`),
  `avatar` = VALUES(`avatar`);

-- 插入刘一用户账号（学号：2025241887，密码：123456，角色：0）
INSERT INTO `user` (`student_id`, `name`, `password`, `points`, `role`, `grade`, `major`, `college`, `avatar`) 
VALUES ('2025241887', '刘一', 'e10adc3949ba59abbe56e057f20f883e', 0, 0, '2025', '软件工程', '信息科学与工程学院', 'default.jpg')
ON DUPLICATE KEY UPDATE 
  `name` = VALUES(`name`),
  `password` = VALUES(`password`),
  `role` = VALUES(`role`),
  `grade` = VALUES(`grade`),
  `major` = VALUES(`major`),
  `college` = VALUES(`college`),
  `avatar` = VALUES(`avatar`);

-- 插入六二用户账号（学号：2025241888，密码：123456，角色：0）
INSERT INTO `user` (`student_id`, `name`, `password`, `points`, `role`, `grade`, `major`, `college`, `avatar`) 
VALUES ('2025241888', '六二', 'e10adc3949ba59abbe56e057f20f883e', 0, 0, '2025', '软件工程', '信息科学与工程学院', 'default.jpg');

-- 插入admin表管理员账号（用户名：admin，密码：123456）
INSERT INTO `admin` (`username`, `password`, `name`, `status`) VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', 1);

-- 先插入顶级分类
INSERT INTO `category` (`name`, `description`, `parent_id`, `sort`) 
VALUES 
('工作资料', '各类工作资料', 0, 1),
('学习资料', '各类学习资料', 0, 2),
('生活资料', '各类生活资料', 0, 3)
ON DUPLICATE KEY UPDATE 
  `description` = VALUES(`description`),
  `sort` = VALUES(`sort`);

-- 再插入子分类
INSERT INTO `category` (`name`, `description`, `parent_id`, `sort`) 
VALUES 
('考试资源', '考试相关资源', 2, 1),
('课件讲义', '课程课件和讲义', 2, 2),
('习题答案', '习题和答案', 2, 3),
('历年真题', '历年考试真题', 2, 4),
('模拟试题', '模拟考试试题', 2, 5)
ON DUPLICATE KEY UPDATE 
  `description` = VALUES(`description`),
  `sort` = VALUES(`sort`);

-- 创建标签表
CREATE TABLE IF NOT EXISTS `tag` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
  `use_count` INT DEFAULT 0 COMMENT '使用次数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- 添加标签表的status列（如果不存在）
SET @exist = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = 'material_platform' AND table_name = 'tag' AND column_name = 'status');
SET @sql = IF(@exist = 0, 'ALTER TABLE `tag` ADD COLUMN `status` INT DEFAULT 1 COMMENT ''状态：0禁用，1启用''', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 创建资料标签关联表
CREATE TABLE IF NOT EXISTS `material_tag` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `material_id` BIGINT(20) NOT NULL COMMENT '资料ID',
  `tag_id` BIGINT(20) NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`id`),
  KEY `idx_material_id` (`material_id`),
  KEY `idx_tag_id` (`tag_id`),
  CONSTRAINT `fk_material_tag_material` FOREIGN KEY (`material_id`) REFERENCES `material` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_material_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资料标签关联表';

-- 插入默认标签
INSERT INTO `tag` (`name`, `use_count`) 
VALUES 
('考试', 0),
('课件', 0),
('习题', 0),
('真题', 0),
('模拟题', 0),
('答案', 0),
('讲义', 0),
('笔记', 0),
('复习', 0),
('资料', 0)
ON DUPLICATE KEY UPDATE 
  `use_count` = VALUES(`use_count`);

-- 创建搜索日志表
CREATE TABLE IF NOT EXISTS `search_log` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `keyword` VARCHAR(100) NOT NULL COMMENT '搜索关键词',
  `user_id` BIGINT(20) COMMENT '用户ID',
  `ip_address` VARCHAR(50) COMMENT 'IP地址',
  `search_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '搜索时间',
  `result_count` INT DEFAULT 0 COMMENT '搜索结果数量',
  `is_valid` BOOLEAN DEFAULT TRUE COMMENT '是否有效（防刷榜标记）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_keyword` (`keyword`),
  CONSTRAINT `fk_search_log_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索日志表';

-- 创建评论表
CREATE TABLE IF NOT EXISTS `comment` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `rel_type` INT DEFAULT 1 COMMENT '关联类型（1：资料，2：问题）',
  `rel_id` BIGINT(20) NOT NULL COMMENT '关联ID',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `like_count` INT DEFAULT 0 COMMENT '点赞数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_rel_type_rel_id` (`rel_type`, `rel_id`),
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 创建用户行为表
CREATE TABLE IF NOT EXISTS `user_behavior` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `behavior_type` INT NOT NULL COMMENT '行为类型（1：点赞，2：收藏）',
  `rel_id` BIGINT(20) NOT NULL COMMENT '关联ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_behavior_type_rel_id` (`behavior_type`, `rel_id`),
  CONSTRAINT `fk_user_behavior_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为表';

-- 创建下载记录表
CREATE TABLE IF NOT EXISTS `download_record` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `material_id` BIGINT(20) NOT NULL COMMENT '资料ID',
  `points` INT DEFAULT 0 COMMENT '消耗积分',
  `download_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '下载时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_material_id` (`material_id`),
  CONSTRAINT `fk_download_record_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_download_record_material` FOREIGN KEY (`material_id`) REFERENCES `material` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='下载记录表';

-- 创建上传记录表
CREATE TABLE IF NOT EXISTS `upload_record` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `material_id` BIGINT(20) NOT NULL COMMENT '资料ID',
  `points` INT DEFAULT 0 COMMENT '获得积分',
  `upload_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_material_id` (`material_id`),
  CONSTRAINT `fk_upload_record_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_upload_record_material` FOREIGN KEY (`material_id`) REFERENCES `material` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='上传记录表';

-- 创建密码修改记录表
CREATE TABLE IF NOT EXISTS `password_record` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `modify_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `ip_address` VARCHAR(50) COMMENT '修改IP',
  `device` VARCHAR(255) COMMENT '设备信息',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_password_record_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='密码修改记录表';

-- 脚本执行完成
SELECT '数据库初始化完成！' AS message;
SELECT '管理员账号：admin，密码：123456' AS admin_info;