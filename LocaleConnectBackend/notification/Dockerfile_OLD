FROM maven:3.8.4-openjdk-17-slim AS builder

WORKDIR /app

COPY . .

RUN mvn clean install -DskipTests -f notification/pom.xml

FROM openjdk:17-jdk-slim

WORKDIR /notification

COPY --from=builder /app/notification/target/notification-1.0-SNAPSHOT.jar /notification/notification-service.jar

ENTRYPOINT ["java", "-jar", "/notification/notification-service.jar"]