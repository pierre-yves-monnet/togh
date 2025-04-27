FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests && ls -lh target/

FROM eclipse-temurin:21-jdk-alpine
EXPOSE 9081

WORKDIR /app
COPY --from=builder target/togh.jar togh.jar
ENTRYPOINT ["java","-Dspring.profiles.active=production", "-jar","/app/togh.jar"]