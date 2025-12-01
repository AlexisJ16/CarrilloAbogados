#!/bin/bash
# Ubicación: scripts/build-all-images.sh

# Versión del proyecto (debe coincidir con pom.xml)
VERSION="0.1.0"

# Lista de todos los microservicios
services=(
    "api-gateway"
    "user-service"
    "case-service"
    "client-service"
    "document-service"
    "calendar-service"
    "notification-service"
    "payment-service"
    "order-service"
    "n8n-integration-service"
    "proxy-client"
)

echo "🏗️  Iniciando construcción de imágenes Docker en Minikube..."

# Conectar al Docker de Minikube
eval $(minikube docker-env)

for service in "${services[@]}"; do
    if [ -d "$service" ]; then
        echo "🔹 Procesando $service..."
        
        # LÓGICA HÍBRIDA:
        # Pasamos la variable JAR_FILE con la ruta completa para arreglar los servicios tipo B (document, calendar, etc.)
        # Los servicios tipo A ignorarán esta variable y usarán su ruta hardcodeada (que ya funciona).
        
        docker build \
          -t "carrillo/$service:latest" \
          -f "$service/Dockerfile" \
          --build-arg JAR_FILE="$service/target/$service-v$VERSION.jar" \
          .
          
        if [ $? -eq 0 ]; then
            echo "✅ $service construido exitosamente."
        else
            echo "❌ ERROR construyendo $service. Revisa los logs arriba."
        fi
    else
        echo "⚠️  Directorio $service no encontrado."
    fi
done

echo "📊 Resumen de imágenes construidas:"
docker images | grep carrillo | awk '{print $1 " -> " $2}'
