# Maven 安装指南

## 1. 下载 Maven

从官方网站下载最新版本的 Maven：
[https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)

选择 `apache-maven-3.9.4-bin.zip`（或最新版本）下载。

## 2. 解压 Maven

将下载的 zip 文件解压到本地目录，例如：
`D:\Program Files\Apache\maven\apache-maven-3.9.4`

## 3. 配置环境变量

### 3.1 新增 MAVEN_HOME 环境变量

1. 右键点击「此电脑」→「属性」→「高级系统设置」→「环境变量」
2. 在「系统变量」中点击「新建」
3. 变量名：`MAVEN_HOME`
4. 变量值：`D:\Program Files\Apache\maven\apache-maven-3.9.4`（根据实际解压路径修改）

### 3.2 修改 Path 环境变量

1. 在「系统变量」中找到并选中 `Path` 变量，点击「编辑」
2. 点击「新建」，添加：`%MAVEN_HOME%\bin`
3. 点击「确定」保存所有设置

## 4. 验证安装

打开命令提示符（Win+R → 输入 `cmd` → 回车），执行以下命令：

```bash
mvn -version
```

如果显示 Maven 版本信息，则安装成功。

## 5. 启动后端服务

安装成功后，在后端目录执行以下命令启动服务：

```bash
mvn spring-boot:run
```

## 6. 注意事项

- 确保已安装 JDK 1.8 或更高版本
- 环境变量配置后需要重新打开命令提示符才能生效
- 如果遇到端口占用问题，可以修改 `application.properties` 文件中的端口配置