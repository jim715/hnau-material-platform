-- 插入一个测试用户，设置班级信息
INSERT INTO `user` (`student_id`, `name`, `password`, `points`, `role`, `grade`, `major`, `college`, `class_name`, `avatar`)
VALUES ('2025241889', '王五', 'e10adc3949ba59abbe56e057f20f883e', 5, 0, '2025', '软件工程', '软件学院', '软件2501', 'default.jpg')
ON DUPLICATE KEY UPDATE
  `name` = VALUES(`name`),
  `grade` = VALUES(`grade`),
  `major` = VALUES(`major`),
  `college` = VALUES(`college`),
  `class_name` = VALUES(`class_name`),
  `password` = VALUES(`password`);

-- 查询测试用户信息
SELECT * FROM `user` WHERE `student_id` = '2025241889';

-- 脚本执行完成
SELECT '测试用户创建完成！' AS message;