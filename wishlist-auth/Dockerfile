FROM gradle:8.1.1-jdk17 AS build
COPY . /app
WORKDIR /app
RUN chmod +x gradlew && ./gradlew bootJar
RUN ll /app && ll /app/build && ll /app/build/libs

FROM openjdk:17-jdk-slim
COPY --from=build /app/build/libs/*.jar auth.jar
ENTRYPOINT ["java", "-jar", "auth.jar"]