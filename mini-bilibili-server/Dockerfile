# syntax=docker/dockerfile:1.7

FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml ./
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

RUN groupadd --system spring && useradd --system --gid spring spring

COPY --from=builder --chown=spring:spring /app/target/*.jar app.jar

USER spring:spring

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -Duser.timezone=Asia/Shanghai"

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
