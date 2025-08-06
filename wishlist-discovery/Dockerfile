FROM gradle:8.1.1-jdk17 AS build
COPY . /app
WORKDIR /app
RUN chmod +x ../gradlew && ../gradlew :service-a:bootJar

FROM openjdk:17-jdk-slim
COPY --from=build /app/build/libs/discovery-service.jar discovery-service.jar
ENTRYPOINT ["java", "-jar", "discovery-service.jar"]