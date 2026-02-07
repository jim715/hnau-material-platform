# 使用Java 17基础镜像
FROM openjdk:17-jdk-slim

# 维护者信息
MAINTAINER hnau

# 设置工作目录
WORKDIR /app

# 复制编译后的jar文件到容器中
COPY target/material-platform-1.0-SNAPSHOT.jar app.jar

# 复制启动脚本到容器中
COPY start.sh start.sh

# 添加执行权限
RUN chmod +x start.sh

# 暴露端口
EXPOSE 8082

# 运行启动脚本
CMD ["./start.sh"]