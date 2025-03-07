# 빌드를 먼저 해야한다.
FROM openjdk:21-jdk-slim-buster 
# AS builder

WORKDIR /app

COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
#COPY src/main ./src/main

# RUN ./gradlew bootJar
EXPOSE 5000
#밑에 RUN, CMD는 운영환경에선 삭제
RUN chmod +x gradlew

CMD ["./gradlew", "bootRun"]
#위에서 빌드된 걸 실행
#이건 운영서버용 설정
# FROM openjdk:21-slim-buster

# WORKDIR /app

# COPY --from=builder /app/build/libs/backend-*.jar app.jar

# EXPOSE 5000

# ENTRYPOINT [ "java", "-jar", "app.jar" ]