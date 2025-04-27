FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests && ls -lh target/

FROM eclipse-temurin:21-jdk-alpine
EXPOSE 7080
WORKDIR /app
COPY --from=builder /app/target/togh.jar togh.jar
ENTRYPOINT ["java","-Dspring.profiles.active=production", "-jar","togh.jar"]