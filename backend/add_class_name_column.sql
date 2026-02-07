-- 添加class_name字段到user表
ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `class_name` VARCHAR(50) COMMENT '班级';

-- 脚本执行完成
SELECT 'class_name字段添加完成！' AS message;