FROM maven:3.8.4-openjdk-17-slim AS builder

WORKDIR /app

COPY . .

RUN mvn clean install -DskipTests -f trip/pom.xml

FROM openjdk:17-jdk-slim

WORKDIR /trip

COPY --from=builder /app/trip/target/trip-1.0-SNAPSHOT.jar /trip/trip-service.jar

ENTRYPOINT ["java", "-jar", "/trip/trip-service.jar"]