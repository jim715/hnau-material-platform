-- 更新测试用户的班级信息
UPDATE `user` SET `class_name` = '软件2501' WHERE `student_id` = '2025241887';
UPDATE `user` SET `class_name` = '软件2502' WHERE `student_id` = '2025241888';
UPDATE `user` SET `class_name` = '软件2503' WHERE `student_id` = '2025241777';

-- 查询更新后的用户信息
SELECT student_id, name, class_name FROM `user` WHERE student_id IN ('2025241777', '2025241887', '2025241888');

-- 脚本执行完成
SELECT '用户班级信息更新完成！' AS message;