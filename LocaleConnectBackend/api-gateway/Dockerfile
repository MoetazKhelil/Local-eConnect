FROM openjdk:17-jdk-slim

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} api-gateway.jar

ENTRYPOINT ["java", "-jar", "/api-gateway.jar"]

EXPOSE 8080