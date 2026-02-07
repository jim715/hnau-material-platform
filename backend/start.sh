#!/bin/bash

# 启动脚本

# 设置环境变量
export JAVA_OPTS="-Xms512m -Xmx1024m -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m"

# 启动应用
echo "Starting Material Platform Backend..."
echo "Java Options: $JAVA_OPTS"

# 运行jar文件
java $JAVA_OPTS -jar app.jar
