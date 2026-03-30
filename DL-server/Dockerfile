# ============================================
# 阶段一：构建阶段
# 使用 Maven 镜像进行项目构建
# ============================================
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

# 设置工作目录
WORKDIR /build

# 复制 Maven 配置文件和项目文件
# 首先复制 pom 文件，利用 Docker 缓存机制加速构建
COPY pom.xml .
COPY DL-common/pom.xml DL-common/
COPY DL-pojo/pom.xml DL-pojo/
COPY DL-server/pom.xml DL-server/

# 下载依赖（这一层会被缓存，除非 pom.xml 发生变化）
RUN mvn dependency:go-offline -B

# 复制源代码
COPY DL-common/ DL-common/
COPY DL-pojo/ DL-pojo/
COPY DL-server/ DL-server/

# 构建项目，跳过测试以加快构建速度
RUN mvn clean package -DskipTests -B

# ============================================
# 阶段二：运行阶段
# 使用精简的 JRE 镜像作为运行环境
# ============================================
FROM eclipse-temurin:21-jre-alpine

# 设置维护者信息
LABEL maintainer="xuan"
LABEL description="DriftingLeaves Blog Server"
LABEL version="1.0.0"

# 设置时区为上海时区（中国标准时间）
# 安装 tzdata 包并配置时区
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    apk del tzdata

# 创建非 root 用户运行应用（安全最佳实践）
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# 设置工作目录
WORKDIR /app

# 从构建阶段复制打包好的 jar 文件
COPY --from=builder /build/DL-server/target/DL-server-1.0-SNAPSHOT.jar app.jar

# 创建日志目录
RUN mkdir -p /app/logs && chown -R appuser:appgroup /app

# 切换到非 root 用户
USER appuser

# 暴露应用端口（根据 application.yml 配置为 5922）
EXPOSE 5922

# 设置 JVM 参数（针对 4GB 内存服务器优化）
# -Xms/-Xmx: 堆内存 256-384MB，给系统留足余量
# -XX:MaxMetaspaceSize: 限制元空间大小，防止内存泄漏
# -XX:+UseContainerSupport: 容器环境感知（Java 21 默认开启）
# -XX:MaxRAMPercentage: 可选，按容器内存百分比分配
ENV JAVA_OPTS="-Xms256m -Xmx384m \
    -XX:MaxMetaspaceSize=128m \
    -XX:CompressedClassSpaceSize=64m \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=200 \
    -XX:+UseStringDeduplication \
    -XX:+ExitOnOutOfMemoryError \
    -Djava.security.egd=file:/dev/./urandom \
    -Duser.timezone=Asia/Shanghai \
    -Dfile.encoding=UTF-8"

# 健康检查配置
# 每 30 秒检查一次应用健康状态
# 超时时间 10 秒，启动等待 60 秒
# 连续 3 次失败则标记为不健康
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:5922/health || exit 1

# 启动应用
# 使用 exec 形式，使 Java 进程成为 PID 1，正确接收信号
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
