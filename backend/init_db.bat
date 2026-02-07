@echo off
chcp 65001 > nul

:: MySQL安装路径
set "MYSQL_HOME=D:\MySQL\MySQL Server 8.0"

:: 数据库信息
set "DB_NAME=material_platform"
set "DB_USER=root"
set "DB_PASS=123456"

:: SQL脚本路径
set "SQL_FILE=%~dp0src\main\resources\sql\admin_schema.sql"

echo 正在初始化数据库...
echo.

:: 检查MySQL是否存在
if not exist "%MYSQL_HOME%\bin\mysql.exe" (
    echo 错误：找不到MySQL执行文件，请检查MYSQL_HOME路径是否正确！
    pause
    exit /b 1
)

:: 检查SQL脚本是否存在
if not exist "%SQL_FILE%" (
    echo 错误：找不到SQL脚本文件，请检查路径是否正确！
    pause
    exit /b 1
)

:: 创建数据库
echo 创建数据库 %DB_NAME%...
"%MYSQL_HOME%\bin\mysql.exe" -u "%DB_USER%" -p"%DB_PASS%" -e "CREATE DATABASE IF NOT EXISTS %DB_NAME% DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

if %errorlevel% equ 0 (
    echo 数据库创建成功！
) else (
    echo 数据库创建失败，请检查MySQL服务是否运行以及用户名密码是否正确！
    pause
    exit /b 1
)

echo.
echo 正在执行SQL脚本...
echo.

:: 执行SQL脚本
"%MYSQL_HOME%\bin\mysql.exe" -u "%DB_USER%" -p"%DB_PASS%" "%DB_NAME%" < "%SQL_FILE%"

if %errorlevel% equ 0 (
    echo SQL脚本执行成功！
    echo.
    echo 数据库初始化完成！
    echo 您可以使用以下命令启动后端服务：
    echo mvn spring-boot:run
) else (
    echo SQL脚本执行失败，请检查脚本内容是否正确！
    pause
    exit /b 1
)

echo.
pause