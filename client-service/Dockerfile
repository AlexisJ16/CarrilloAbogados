FROM eclipse-temurin:21-jre-alpine

# Crear usuario no-root para seguridad
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Configurar timezone para Colombia
RUN apk add --no-cache tzdata
ENV TZ=America/Bogota

# Crear directorio de trabajo
WORKDIR /app

# Copiar JAR con permisos correctos
ARG JAR_FILE=target/client-service-v0.2.0.jar
COPY --chown=appuser:appgroup ${JAR_FILE} app.jar

# Cambiar a usuario no-root
USER appuser

# Configurar health check con context-path
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8200/client-service/actuator/health || exit 1

# Exponer puerto
EXPOSE 8200

# Configurar JVM para contenedor con mejores configuraciones
ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
