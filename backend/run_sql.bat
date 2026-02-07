@echo off

rem 设置MySQL安装路径
set "MYSQL_HOME=D:\MySQL\MySQL Server 8.0"

rem MySQL执行文件路径
set "MYSQL_EXE=%MYSQL_HOME%\bin\mysql.exe"

rem 检查MySQL执行文件是否存在
if not exist "%MYSQL_EXE%" (
    echo 错误：找不到MySQL执行文件！
    echo 请检查MySQL安装路径是否正确：%MYSQL_HOME%
    pause
    exit /b 1
)

rem 执行SQL脚本
echo 正在执行数据库初始化脚本...
echo 请输入MySQL root用户密码：
"%MYSQL_EXE%" -u root -p < "%~dp0complete_db_init.sql"

rem 检查执行结果
if %errorlevel% equ 0 (
    echo 数据库初始化脚本执行成功！
    echo 管理员账号：admin，密码：123456
    echo 请使用以下命令启动后端服务：
    echo mvn spring-boot:run
) else (
    echo 数据库初始化脚本执行失败！
    echo 请检查MySQL连接和权限是否正确。
)

pause