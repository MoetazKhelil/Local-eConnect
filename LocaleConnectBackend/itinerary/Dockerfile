FROM openjdk:17-jdk-slim

WORKDIR /itinerary

COPY ./target/itinerary-1.0-SNAPSHOT.jar /itinerary/itinerary-service.jar

ENTRYPOINT ["java", "-jar", "/itinerary/itinerary-service.jar"]