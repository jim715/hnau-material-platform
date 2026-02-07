# 数据库初始化指南

## 问题分析
用户在使用MySQL Workbench执行SQL脚本时遇到编码错误：`'gbk' codec can't decode byte 0xae in position 5: illegal multibyte sequence`，这是因为Workbench默认使用GBK编码打开UTF-8编码的文件。

## 解决方案

### 方案1：使用Java工具类初始化数据库

1. **编译并运行DbInitUtil工具类**
   - 打开命令提示符，进入backend目录
   - 执行以下命令编译工具类：
     ```bash
     mvn compile
     ```
   - 执行以下命令运行工具类：
     ```bash
     mvn exec:java -Dexec.mainClass="com.hnau.platform.utils.DbInitUtil"
     ```

2. **工具类功能**
   - 自动加载MySQL驱动
   - 连接到MySQL服务器
   - 创建material_platform数据库（如果不存在）
   - 执行admin_schema.sql脚本
   - 输出执行过程和结果

3. **优势**
   - 无需配置环境变量
   - 避免编码问题
   - 一键完成初始化

### 方案2：使用完整路径执行MySQL命令

1. **打开命令提示符（管理员权限）**

2. **使用完整路径登录MySQL**
   ```bash
   "D:\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p
   ```
   输入密码：`root`

3. **创建数据库（如果不存在）**
   ```sql
   CREATE DATABASE IF NOT EXISTS material_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

4. **使用数据库**
   ```sql
   USE material_platform;
   ```

5. **执行SQL脚本**
   ```sql
   SOURCE "D:\code\hnau-material-platform\backend\src\main\resources\sql\admin_schema.sql";
   ```

### 方案3：修改MySQL Workbench编码设置

1. **打开MySQL Workbench**
2. **进入Edit -> Preferences -> SQL Editor**
3. **将"Default Character Set"改为"utf8mb4"**
4. **重启MySQL Workbench后再尝试执行脚本**

### 方案4：使用PowerShell执行SQL脚本

1. **打开PowerShell（管理员权限）**

2. **执行以下命令**
   ```powershell
   & "D:\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p material_platform < "D:\code\hnau-material-platform\backend\src\main\resources\sql\admin_schema.sql"
   ```
   输入密码：`root`

## 验证数据库初始化

无论使用哪种方案，初始化完成后可以通过以下步骤验证：

1. **登录MySQL**
   ```bash
   "D:\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p
   ```

2. **使用数据库**
   ```sql
   USE material_platform;
   ```

3. **检查用户表**
   ```sql
   SELECT * FROM user WHERE student_id = 'admin';
   ```
   应该看到管理员账号信息

4. **检查表结构**
   ```sql
   SHOW TABLES;
   DESCRIBE user;
   DESCRIBE material;
   DESCRIBE shield_keyword;
   ```

## 注意事项

- 确保MySQL服务正在运行（可在服务管理器中查看）
- 数据库连接配置必须与`application.yml`文件中的配置一致
- 如果遇到权限问题，请使用管理员权限运行命令提示符或PowerShell

## 启动后端服务

数据库初始化完成后，可以启动后端服务：

```bash
mvn spring-boot:run
```

服务将在`http://localhost:8080/api`上运行。