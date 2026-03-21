# ---- Build Stage ----
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# 复制 Maven 配置（使用阿里云镜像加速）
COPY settings.xml /root/.m2/settings.xml

# 先复制 pom.xml 利用 Docker 缓存下载依赖
COPY pom.xml .

# 使用 Docker BuildKit 缓存加速 Maven 依赖下载
RUN --mount=type=cache,target=/root/.m2/repository \
    mvn dependency:go-offline -B

# 复制源码并构建
COPY src ./src
RUN --mount=type=cache,target=/root/.m2/repository \
    mvn package -DskipTests -B

# ---- Runtime Stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app

# 设置环境变量
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# 健康检查 - 检测 8080 端口是否可用
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/ 2>/dev/null || wget -qO- http://localhost:8080/ 2>/dev/null || exit 1

# 使用 JAVA_OPTS 环境变量启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
