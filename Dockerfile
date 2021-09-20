
FROM adoptopenjdk/openjdk11
WORKDIR /app
COPY target/togh.jar app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=production", "-jar","/app/app.jar"]