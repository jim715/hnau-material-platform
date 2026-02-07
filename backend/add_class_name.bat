@echo off
set /p password=请输入MySQL根密码: 
mysql -u root -p%password% material_platform -e "ALTER TABLE `user` ADD COLUMN `class_name` VARCHAR(50) COMMENT '班级';"
if %errorlevel% equ 0 (
    echo class_name字段添加成功！
) else (
    echo class_name字段添加失败，请检查错误信息。
)
pause