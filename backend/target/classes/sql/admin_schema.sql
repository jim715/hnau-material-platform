-- 修改user表，添加role字段（0：普通用户，1：管理员）
ALTER TABLE `user` ADD COLUMN `role` INT DEFAULT 0 COMMENT '用户角色：0普通用户，1管理员';

-- 修改material表，添加audit_status和agri_tag字段
ALTER TABLE `material` ADD COLUMN `audit_status` INT DEFAULT 0 COMMENT '审核状态：0待审核，1已通过，2已驳回';
ALTER TABLE `material` ADD COLUMN `agri_tag` INT DEFAULT 0 COMMENT '农科精选标记：0否，1是';

-- 创建屏蔽关键词表
CREATE TABLE `shield_keyword` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `keyword` VARCHAR(50) NOT NULL COMMENT '屏蔽关键词',
  `shield_time` DATETIME NOT NULL COMMENT '屏蔽时间',
  `reason` VARCHAR(200) COMMENT '屏蔽原因',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_keyword` (`keyword`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='屏蔽关键词表';

-- 插入一个管理员账号（学号：admin，密码：123456，角色：1）
INSERT INTO `user` (`student_id`, `name`, `password`, `email`, `phone`, `points`, `role`) VALUES ('admin', '管理员', 'e10adc3949ba59abbe56e057f20f883e', 'admin@example.com', '13800138000', 0, 1);