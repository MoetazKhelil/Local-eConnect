## Prerequisites
- **Java JDK 17**: Required for running Spring Boot. Verify or install from [Oracle](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or [AdoptOpenJDK](https://adoptopenjdk.net/?variant=openjdk17).
- **Maven**: Used for dependency management. Check with `mvn -v` or install from [Maven](https://maven.apache.org/install.html).
- **IDE**: IntelliJ IDEA, Eclipse, or Spring Tool Suite recommended for development.

## Running the Backend with Docker
- Install Docker Desktop.
- **Docker Login**: Run `docker login` and provide the credentials:
    - Username: `localeconnect`
    - Password: `lssa2324app`
- Navigate to `/LocaleConnectBackend` and execute `docker-compose -f docker-compose-test.yml up` to start containers.

## Unit Testing
- Navigate to `/LocaleConnectBackend` and Run `mvn clean test` to execute the unit tests.
