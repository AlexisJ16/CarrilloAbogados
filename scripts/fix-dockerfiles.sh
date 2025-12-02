#!/bin/bash
set -e

echo "🔧 Corrigiendo Dockerfiles para arquitectura cloud-native..."

# Función para crear Dockerfile correcto
create_dockerfile() {
    local service=$1
    local port=$2
    local jar_name="${service}-v0.1.0.jar"
    
    cat > "$service/Dockerfile" << EOF
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
ARG JAR_FILE=target/${jar_name}
COPY \${JAR_FILE} app.jar
EXPOSE ${port}
ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]
EOF
    
    echo "  ✅ $service/Dockerfile corregido"
}

# Crear Dockerfiles correctos
echo "📝 Generando Dockerfiles cloud-native..."

create_dockerfile "api-gateway" "8080"
create_dockerfile "proxy-client" "8900"
create_dockerfile "user-service" "8700"
create_dockerfile "order-service" "8300"
create_dockerfile "payment-service" "8400"
create_dockerfile "client-service" "8700"
create_dockerfile "case-service" "8300"

echo ""
echo "✅ Todos los Dockerfiles corregidos"
echo ""
echo "📋 Verificando archivos..."
for service in api-gateway proxy-client user-service order-service payment-service client-service case-service; do
    if [ -f "$service/Dockerfile" ]; then
        echo "  ✓ $service/Dockerfile existe"
    else
        echo "  ✗ $service/Dockerfile NO EXISTE"
    fi
done

echo ""
echo "🧪 Para verificar que los JARs existen:"
echo "   ls -lh */target/*-v0.1.0.jar"
