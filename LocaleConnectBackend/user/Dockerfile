FROM openjdk:17-jdk-slim

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} user-service.jar

ENTRYPOINT ["java", "-jar", "/user-service.jar"]

EXPOSE 8084