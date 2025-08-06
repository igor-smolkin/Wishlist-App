FROM gradle:8.1.1-jdk17 AS build
COPY . /app
WORKDIR /app
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon

FROM openjdk:17-jdk-slim
COPY --from=build /app/build/libs/auth.jar auth.jar
ENTRYPOINT ["java", "-jar", "auth.jar"]