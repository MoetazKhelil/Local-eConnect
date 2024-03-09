FROM maven:3.8.4-openjdk-17-slim AS builder

WORKDIR /app

COPY . .

RUN mvn clean install -DskipTests -f feed/pom.xml

FROM openjdk:17-jdk-slim

WORKDIR /feed

COPY --from=builder /app/feed/target/feed-1.0-SNAPSHOT.jar /feed/feed-service.jar

ENTRYPOINT ["java", "-jar", "/feed/feed-service.jar"]