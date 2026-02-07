# MySQL 命令行执行 SQL 脚本指南

## 问题分析
MySQL Workbench 出现编码错误：`'gbk' codec can't decode byte 0xae in position 5: illegal multibyte sequence`，这是因为 Workbench 默认使用 GBK 编码打开 UTF-8 编码的文件。

## 解决方案
使用 MySQL 命令行工具执行 SQL 脚本，步骤如下：

### 1. 打开命令提示符（管理员权限）

### 2. 登录 MySQL
```bash
mysql -u root -p
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

## 注意事项
- 确保 MySQL 服务正在运行
- 路径中的反斜杠需要使用双引号包围
- 如果出现权限问题，请使用管理员权限运行命令提示符

## 备选方案
如果仍然遇到编码问题，可以尝试以下方法：

### 方法1：使用 PowerShell 执行
```powershell
mysql -u root -p material_platform < "D:\code\hnau-material-platform\backend\src\main\resources\sql\admin_schema.sql"
```

### 方法2：修改 MySQL Workbench 编码设置
1. 打开 MySQL Workbench
2. 进入 Edit -> Preferences -> SQL Editor
3. 将 "Default Character Set" 改为 "utf8mb4"
4. 重启 MySQL Workbench 后再尝试执行脚本