# 构建阶段
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# 拷贝加速配置
COPY settings.xml /usr/share/maven/ref/settings.xml
COPY pom.xml .
# 预下载依赖 (使用 -s 指定配置)
RUN mvn -s /usr/share/maven/ref/settings.xml dependency:go-offline
COPY src ./src
# 打包
RUN mvn -s /usr/share/maven/ref/settings.xml package -DskipTests

# 运行阶段
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
