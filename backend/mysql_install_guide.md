# MySQL 安装指南

## 1. 下载 MySQL

从官方网站下载最新版本的 MySQL Installer：
[https://dev.mysql.com/downloads/installer/](https://dev.mysql.com/downloads/installer/)

选择 "MySQL Installer for Windows" 下载。

## 2. 安装 MySQL

1. 双击下载的安装文件，启动安装向导
2. 选择 "Developer Default" 安装类型（包含 MySQL Server、Workbench 等必要组件）
3. 点击 "Next" 继续，系统会检查并安装必要的依赖项
4. 点击 "Execute" 开始安装所选组件
5. 安装完成后，点击 "Next" 进入配置界面

## 3. 配置 MySQL

1. 在 "Type and Networking" 页面：
   - 选择 "Development Machine" 配置类型
   - 保持默认端口 3306
   - 点击 "Next"

2. 在 "Authentication Method" 页面：
   - 选择 "Use Legacy Authentication Method (Retain MySQL 5.x Compatibility)"
   - 点击 "Next"

3. 在 "Accounts and Roles" 页面：
   - 设置 root 用户密码为 "root"（与项目配置一致）
   - 点击 "Next"

4. 在 "Windows Service" 页面：
   - 保持默认设置，将 MySQL 配置为 Windows 服务
   - 点击 "Next"

5. 在 "Apply Configuration" 页面：
   - 点击 "Execute" 应用所有配置
   - 配置完成后，点击 "Finish"

## 4. 验证 MySQL 安装

1. 打开 MySQL Workbench
2. 连接到本地 MySQL 服务器（默认连接）
3. 输入 root 密码 "root" 登录
4. 如果成功连接，则 MySQL 安装配置完成

## 5. 创建数据库

1. 在 MySQL Workbench 中，点击 "Create a new schema in the connected server"
2. 数据库名称设置为 "material_platform"
3. 点击 "Apply" 创建数据库

## 6. 执行数据库脚本

1. 在 MySQL Workbench 中，打开 "File" → "Run SQL Script..."
2. 选择项目中的 `src/main/resources/sql/admin_schema.sql` 文件
3. 点击 "Run" 执行脚本
4. 脚本执行完成后，数据库表结构和管理员账号会被创建

## 7. 注意事项

- 确保 MySQL 服务正在运行（可在服务管理器中查看）
- 数据库连接配置必须与 `application.yml` 文件中的配置一致
- 如果遇到端口占用问题，可以修改 MySQL 配置或项目配置文件中的端口设置