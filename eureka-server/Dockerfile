FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY target/eureka-server-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]