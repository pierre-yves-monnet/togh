FROM eclipse-temurin:21-jdk-alpine
EXPOSE 9081

WORKDIR /app
COPY target/togh.jar togh.jar
ENTRYPOINT ["java","-Dspring.profiles.active=production", "-jar","/app/togh.jar"]