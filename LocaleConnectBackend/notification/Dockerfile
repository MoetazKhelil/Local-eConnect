FROM openjdk:17-jdk-slim

WORKDIR /notification

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} notification-service.jar

ENTRYPOINT ["java", "-jar", "/notification/notification-service.jar"]

EXPOSE 7002