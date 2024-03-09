FROM openjdk:17-jdk-slim

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} chat-service.jar

ENTRYPOINT ["java", "-jar", "/chat-service.jar"]

EXPOSE 8003