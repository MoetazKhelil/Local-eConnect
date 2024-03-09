FROM maven:3.8.4-openjdk-17-slim AS builder

WORKDIR /app

COPY . .

# speed up Maven JVM a bit
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"

RUN mvn clean install -DskipTests -f meetup/pom.xml

FROM openjdk:17-jdk-slim

WORKDIR /meetup

COPY --from=builder /app/meetup/target/meetup-1.0-SNAPSHOT.jar /meetup/meetup-service.jar

ENTRYPOINT ["java", "-jar", "/meetup/meetup-service.jar"]