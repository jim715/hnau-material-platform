# MySQL 命令行执行 SQL 脚本指南（完整路径版）

## 问题分析
MySQL 命令未被识别为内部或外部命令，这是因为 MySQL 的 bin 目录未添加到系统环境变量中。

## 解决方案
使用完整路径执行 MySQL 命令，步骤如下：

### 1. 打开命令提示符（管理员权限）

### 2. 使用完整路径登录 MySQL
```bash
"D:\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p
```
输入密码：`root`

### 3. 创建数据库（如果不存在）
```sql
CREATE DATABASE IF NOT EXISTS material_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 4. 使用数据库
```sql
USE material_platform;
```

### 5. 执行 SQL 脚本
```sql
SOURCE "D:\code\hnau-material-platform\backend\src\main\resources\sql\admin_schema.sql";
```

### 6. 验证执行结果
执行以下命令检查表结构：
```sql
SHOW TABLES;
DESCRIBE user;
DESCRIBE material;
DESCRIBE shield_keyword;
```

## 验证管理员账号
```sql
SELECT * FROM user WHERE student_id = 'admin';
```

## 备选方案：直接执行完整命令
如果不想进入 MySQL 交互模式，可以使用以下命令一次性执行：

### 方法1：使用命令提示符
```bash
"D:\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p material_platform < "D:\code\hnau-material-platform\backend\src\main\resources\sql\admin_schema.sql"
```

### 方法2：使用 PowerShell
```powershell
& "D:\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p material_platform < "D:\code\hnau-material-platform\backend\src\main\resources\sql\admin_schema.sql"
```

## 注意事项
- 确保 MySQL 服务正在运行
- 路径中的空格需要使用双引号包围
- 输入密码时，命令行会提示输入，输入后按回车即可
- 如果出现权限问题，请使用管理员权限运行命令提示符

## 验证 MySQL 服务状态
```bash
net start | findstr MySQL
```
如果看到 `MySQL80` 服务正在运行，则表示 MySQL 服务正常。