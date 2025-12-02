#!/bin/bash
set -e

echo "🏗️ Reorganizando arquitectura de Helm Charts..."

cd helm-charts/carrillo-abogados

# PASO 1: Mover api-gateway al lugar correcto
echo "📦 Moviendo api-gateway al umbrella chart..."
if [ -d "../api-gateway" ]; then
    mv ../api-gateway charts/
    echo "  ✅ api-gateway movido a charts/"
else
    echo "  ⚠️ api-gateway ya está en charts/ o no existe"
fi

# PASO 2: Limpiar Chart.yaml del umbrella
echo "🧹 Limpiando Chart.yaml..."
cat > Chart.yaml << 'EOF'
apiVersion: v2
name: carrillo-abogados
description: Carrillo Abogados Legal Tech Platform - Complete Microservices Suite
type: application
version: 0.1.0
appVersion: "0.1.0"
keywords:
  - legal-tech
  - microservices
  - spring-boot
  - kubernetes
maintainers:
  - name: Carrillo Abogados DevOps Team
    email: devops@carrilloabogados.com
EOF

echo "  ✅ Chart.yaml simplificado"

# PASO 3: Crear .helmignore para evitar problemas con Git
echo "📝 Creando .helmignore..."
cat > .helmignore << 'EOF'
# Git files
.git/
.gitignore
.gitattributes

# CI/CD
.github/
.gitlab-ci.yml

# IDE
.idea/
.vscode/
*.iml

# OS
.DS_Store
Thumbs.db

# Temp files
*.tmp
*.temp
*.log

# Documentation
README.md
NOTES.md
EOF

echo "  ✅ .helmignore creado"

# PASO 4: Verificar estructura
echo ""
echo "🔍 Verificando estructura de charts..."
EXPECTED_CHARTS=(
    "api-gateway"
    "proxy-client"
    "user-service"
    "order-service"
    "payment-service"
    "client-service"
    "case-service"
    "document-service"
    "calendar-service"
    "notification-service"
    "n8n-integration-service"
)

MISSING_CHARTS=()
for chart in "${EXPECTED_CHARTS[@]}"; do
    if [ -d "charts/$chart" ]; then
        echo "  ✓ $chart"
    else
        echo "  ✗ $chart FALTA"
        MISSING_CHARTS+=("$chart")
    fi
done

if [ ${#MISSING_CHARTS[@]} -gt 0 ]; then
    echo ""
    echo "⚠️ ADVERTENCIA: Faltan ${#MISSING_CHARTS[@]} charts"
    echo "Charts faltantes: ${MISSING_CHARTS[*]}"
    exit 1
fi

# PASO 5: Limpiar dependencias antiguas
echo ""
echo "🧹 Limpiando dependencias antiguas..."
rm -rf charts/*.tgz
rm -f Chart.lock

echo "  ✅ Limpieza completada"

# PASO 6: Actualizar values.yaml global
echo ""
echo "📝 Actualizando values.yaml global..."
cat > values.yaml << 'EOF'
# Global configuration for all microservices
global:
  imageRegistry: ""
  imagePullPolicy: IfNotPresent
  storageClass: "standard"
  environment: development

# Service enablement flags
api-gateway:
  enabled: true
  replicaCount: 1

proxy-client:
  enabled: true
  replicaCount: 1

user-service:
  enabled: true
  replicaCount: 1

order-service:
  enabled: true
  replicaCount: 1

payment-service:
  enabled: true
  replicaCount: 1

client-service:
  enabled: true
  replicaCount: 1

case-service:
  enabled: true
  replicaCount: 1

document-service:
  enabled: true
  replicaCount: 1

calendar-service:
  enabled: true
  replicaCount: 1

notification-service:
  enabled: true
  replicaCount: 1

n8n-integration-service:
  enabled: true
  replicaCount: 1
EOF

echo "  ✅ values.yaml actualizado"

echo ""
echo "✅ Reorganización completada exitosamente!"
echo ""
echo "📊 Resumen:"
echo "   - 11 charts en charts/"
echo "   - Chart.yaml simplificado"
echo "   - .helmignore creado"
echo "   - Dependencias limpias"
echo ""
echo "🎯 Siguiente paso:"
echo "   Volver a la raíz del proyecto y ejecutar deployment"
