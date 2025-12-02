FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
ARG JAR_FILE=target/order-service-v0.1.0.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8300
ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]
