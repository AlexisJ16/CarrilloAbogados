FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
ARG JAR_FILE=target/client-service-v0.2.0.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8700
ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]
