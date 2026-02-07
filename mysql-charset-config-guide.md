# MySQL字符集配置指南（中文显示问题永久解决方案）

## 问题分析

在我们的系统中，中文用户信息显示为问号（???）的问题，经过分析，主要是由于MySQL服务器的字符集设置与应用程序期望的字符集不匹配导致的。

具体表现为：
- MySQL服务器的`character_set_client`、`character_set_connection`和`character_set_results`设置为`gbk`
- 而我们的数据库和表使用的是`utf8mb4`字符集
- 这种不匹配导致中文在传输和存储过程中发生编码转换错误，最终显示为问号

## 永久解决方案

### 步骤1：修改MySQL配置文件

1. **找到MySQL配置文件**
   - Windows系统：通常位于 `D:\MySQL\MySQL Server 8.0\my.ini`
   - Linux系统：通常位于 `/etc/my.cnf` 或 `/etc/mysql/my.cnf`

2. **编辑配置文件**
   - 以管理员身份打开配置文件
   - 在 `[client]` 部分添加：
     ```ini
     [client]
     default-character-set=utf8mb4
     ```
   - 在 `[mysql]` 部分添加：
     ```ini
     [mysql]
     default-character-set=utf8mb4
     ```
   - 在 `[mysqld]` 部分添加：
     ```ini
     [mysqld]
     character-set-server=utf8mb4
     collation-server=utf8mb4_unicode_ci
     init-connect='SET NAMES utf8mb4'
     ```

3. **保存配置文件**

4. **重启MySQL服务**
   - Windows系统：在服务管理器中重启MySQL服务
   - Linux系统：执行 `sudo systemctl restart mysql`

### 步骤2：验证字符集设置

1. **登录MySQL服务器**
   ```bash
   mysql -u root -p
   ```

2. **执行字符集检查命令**
   ```sql
   SHOW VARIABLES LIKE 'character%';
   SHOW VARIABLES LIKE 'collation%';
   ```

3. **验证结果**
   确保所有字符集相关的变量都显示为 `utf8mb4` 或相关值：
   - `character_set_client` = `utf8mb4`
   - `character_set_connection` = `utf8mb4`
   - `character_set_database` = `utf8mb4`
   - `character_set_results` = `utf8mb4`
   - `character_set_server` = `utf8mb4`
   - `collation_connection` = `utf8mb4_unicode_ci`
   - `collation_database` = `utf8mb4_unicode_ci`
   - `collation_server` = `utf8mb4_unicode_ci`

### 步骤3：验证数据库和表结构

1. **检查数据库字符集**
   ```sql
   SELECT schema_name, default_character_set_name, default_collation_name
   FROM information_schema.schemata
   WHERE schema_name = 'material_platform';
   ```

2. **检查表字符集**
   ```sql
   SELECT table_name, table_collation
   FROM information_schema.tables
   WHERE table_schema = 'material_platform';
   ```

3. **检查列字符集**
   ```sql
   SELECT column_name, character_set_name, collation_name
   FROM information_schema.columns
   WHERE table_schema = 'material_platform' AND table_name = 'user';
   ```

## 应用程序配置验证

### 后端配置

确保 `application.yml` 文件中的数据库连接字符串包含正确的字符编码参数：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/material_platform?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai&useSSL=false&characterSetResults=utf8mb4
```

### 前端配置

确保前端请求设置了正确的字符编码：

```javascript
// 在axios配置中
const api = axios.create({
  baseURL: 'http://localhost:8082',
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
});
```

## 测试方法

1. **重启应用程序**
   - 重启后端服务
   - 重启前端服务

2. **测试中文显示**
   - 登录系统
   - 访问用户管理界面
   - 查看用户列表中的中文姓名、学院、专业等信息
   - 上传包含中文信息的Excel文件
   - 验证新用户的中文信息是否正确显示

3. **测试数据持久化**
   - 新增用户，输入中文姓名
   - 退出系统并重新登录
   - 验证用户信息是否仍然正确显示

## 故障排除

### 如果问题仍然存在

1. **检查数据库连接**
   - 确保应用程序使用的数据库连接参数正确
   - 验证连接池配置是否正确

2. **检查应用程序代码**
   - 确保所有字符串处理都使用UTF-8编码
   - 检查是否有硬编码的字符编码设置

3. **检查网络传输**
   - 确保Web服务器（如Tomcat）使用UTF-8编码
   - 检查HTTP响应头是否包含正确的字符编码设置

4. **检查客户端设置**
   - 确保浏览器使用UTF-8编码（通常默认设置）
   - 清除浏览器缓存后重试

## 最佳实践

1. **统一字符编码**
   - 整个应用栈（前端、后端、数据库）都使用UTF-8编码
   - 避免在不同层级使用不同的字符编码

2. **提前配置**
   - 在系统初始化时就正确配置字符编码
   - 避免在系统运行后修改字符编码设置

3. **定期检查**
   - 定期验证字符编码设置是否正确
   - 特别是在系统升级或迁移后

4. **测试覆盖**
   - 在测试用例中包含中文和其他非ASCII字符的测试
   - 确保数据的完整生命周期（创建、存储、读取、显示）都能正确处理中文

## 总结

通过以上步骤，我们可以永久解决MySQL数据库中文显示为问号的问题，确保系统能够正确处理和显示中文用户信息。

这种配置不仅解决了当前的问题，也为系统的长期稳定运行奠定了基础，避免了未来可能出现的字符编码相关问题。