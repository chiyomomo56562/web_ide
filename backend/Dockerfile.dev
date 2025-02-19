# 빌드를 먼저 해야한다.
FROM openjdk:21-jdk-slim-buster AS builder

WORKDIR /app

COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
COPY src/main ./src/main

RUN ./gradlew bootJar

#위에서 빌드된 걸 실행
FROM openjdk:21-slim-buster

WORKDIR /app

COPY --from=builder /app/build/libs/backend-*.jar app.jar

EXPOSE 5000

ENTRYPOINT [ "java", "-jar", "app.jar" ]