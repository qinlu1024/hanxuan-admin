# ===== 构建阶段：Maven 编译打包 =====
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build

# 配置阿里云 Maven 镜像，加速依赖下载
RUN mkdir -p /root/.m2 && printf '%s\n' \
  '<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0">' \
  '  <mirrors>' \
  '    <mirror>' \
  '      <id>aliyun</id>' \
  '      <mirrorOf>central</mirrorOf>' \
  '      <name>Aliyun Maven</name>' \
  '      <url>https://maven.aliyun.com/repository/public</url>' \
  '    </mirror>' \
  '  </mirrors>' \
  '</settings>' > /root/.m2/settings.xml

# 先拷贝 pom，利用 Docker 层缓存依赖
COPY pom.xml .
RUN mvn -B dependency:go-offline

# 拷贝源码并打包（跳过测试）
COPY src ./src
RUN mvn -B clean package -DskipTests

# ===== 运行阶段：精简 JRE =====
FROM eclipse-temurin:17-jre
WORKDIR /app

# 时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY --from=builder /build/target/*.jar app.jar

# 上传文件目录
RUN mkdir -p /app/upload
VOLUME /app/upload

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
