# 用 OpenJDK 11 作为基础镜像
FROM openjdk:11-jre-slim

# 设置工作目录
WORKDIR /app

# 复制 backend 目录下打包好的 jar 包到容器里
COPY backend/target/*.jar app.jar

# 容器启动命令，运行 jar 包
ENTRYPOINT ["java", "-jar", "app.jar"]
