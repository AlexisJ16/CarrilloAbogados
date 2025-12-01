#!/bin/bash

# Colores
RED='\033[0;31m'
YELLOW='\033[1;33m'
GREEN='\033[0;32m'
NC='\033[0m'

echo -e "${RED}⚠️  ADVERTENCIA: Este script eliminará COMPLETAMENTE:${NC}"
echo "  - Cluster de Minikube"
echo "  - Todas las imágenes Docker"
echo "  - Todos los volúmenes Docker"
echo "  - Todos los archivos compilados (target/)"
echo "  - Cache de Helm"
echo "  - Logs temporales"
echo ""
echo -e "${RED}Esta operación es IRREVERSIBLE${NC}"
echo ""
read -p "¿Estás seguro? (escribe 'SI' para continuar): " CONFIRM

if [ "$CONFIRM" != "SI" ]; then
    echo "Operación cancelada."
    exit 0
fi

echo ""
echo "🧹 Iniciando limpieza total..."

# 1. Detener y eliminar Minikube
echo "→ Eliminando cluster Minikube..."
if command -v minikube &> /dev/null; then
    minikube stop 2>/dev/null || echo "  (Minikube ya estaba detenido)"
    minikube delete --all --purge 2>/dev/null || echo "  (Minikube ya estaba limpio)"
    
    # Limpiar archivos de configuración de Minikube
    rm -rf ~/.minikube 2>/dev/null || true
    echo "  ✓ Minikube eliminado completamente"
else
    echo "  ⚠ Minikube no está instalado"
fi

# 2. Limpiar Docker completamente
echo "→ Eliminando contenedores, imágenes y volúmenes Docker..."
if command -v docker &> /dev/null; then
    # Detener todos los contenedores
    docker stop $(docker ps -aq) 2>/dev/null || true
    
    # Eliminar todos los contenedores
    docker rm $(docker ps -aq) 2>/dev/null || true
    
    # Limpiar sistema completo (imágenes, contenedores, volúmenes, networks)
    docker system prune -af --volumes 2>/dev/null || echo "  (Docker ya estaba limpio)"
    
    # Verificar que no queden imágenes de Carrillo Abogados
    docker images | grep carrilloabogados | awk '{print $1":"$2}' | xargs docker rmi 2>/dev/null || true
    
    echo "  ✓ Docker limpio completamente"
else
    echo "  ⚠ Docker no está disponible"
fi

# 3. Limpiar builds de Maven en todo el proyecto
echo "→ Limpiando compilaciones Maven..."
find . -type d -name "target" -exec rm -rf {} + 2>/dev/null || true
find . -name "*.jar" -not -path "./mvnw*" -delete 2>/dev/null || true
echo "  ✓ Compilaciones Maven eliminadas"

# 4. Limpiar cache de Helm
echo "→ Limpiando cache de Helm..."
if command -v helm &> /dev/null; then
    rm -rf ~/.cache/helm 2>/dev/null || true
    rm -rf ~/.config/helm 2>/dev/null || true
    rm -rf ~/.local/share/helm 2>/dev/null || true
    echo "  ✓ Cache de Helm eliminado"
else
    echo "  ⚠ Helm no está instalado"
fi

# 5. Limpiar configuraciones de kubectl
echo "→ Limpiando configuraciones de kubectl..."
if command -v kubectl &> /dev/null; then
    # Hacer backup del config actual
    if [ -f ~/.kube/config ]; then
        cp ~/.kube/config ~/.kube/config.backup.$(date +%Y%m%d_%H%M%S) 2>/dev/null || true
    fi
    
    # Limpiar contextos de desarrollo
    kubectl config delete-context minikube 2>/dev/null || true
    kubectl config delete-cluster minikube 2>/dev/null || true
    kubectl config delete-user minikube 2>/dev/null || true
    
    echo "  ✓ Contextos de desarrollo eliminados"
else
    echo "  ⚠ kubectl no está instalado"
fi

# 6. Limpiar archivos temporales del proyecto
echo "→ Limpiando archivos temporales..."
find . -type f -name "*.log" -delete 2>/dev/null || true
find . -type f -name ".DS_Store" -delete 2>/dev/null || true
find . -type f -name "Thumbs.db" -delete 2>/dev/null || true
find . -type f -name "*.tmp" -delete 2>/dev/null || true
find . -type d -name ".idea" -exec rm -rf {} + 2>/dev/null || true
find . -type d -name "node_modules" -exec rm -rf {} + 2>/dev/null || true

# Limpiar logs de Spring Boot
find . -name "spring.log" -delete 2>/dev/null || true
find . -name "application.log" -delete 2>/dev/null || true

echo "  ✓ Archivos temporales eliminados"

# 7. Limpiar variables de entorno específicas (opcional)
echo "→ Verificando variables de entorno..."
if [ -n "$MINIKUBE_HOME" ]; then
    echo "  ⚠ MINIKUBE_HOME está configurado: $MINIKUBE_HOME"
    echo "    Considera ejecutar: unset MINIKUBE_HOME"
fi

if env | grep -q "DOCKER_HOST.*minikube"; then
    echo "  ⚠ DOCKER_HOST apunta a Minikube"
    echo "    Ejecuta: unset DOCKER_HOST"
fi

echo "  ✓ Variables de entorno verificadas"

# 8. Verificar espacio liberado
echo "→ Calculando espacio liberado..."
if command -v df &> /dev/null; then
    DISK_USAGE=$(df -h . | tail -1 | awk '{print $4}')
    echo "  ℹ️ Espacio disponible: $DISK_USAGE"
fi

echo ""
echo -e "${GREEN}✅ Limpieza completa exitosa!${NC}"
echo ""
echo "El entorno está ahora en estado inicial limpio."
echo ""
echo "Para reconstruir el entorno, ejecuta:"
echo -e "${YELLOW}  ./scripts/dev-up.sh${NC}"
echo ""
echo "Para un deployment completo desde cero:"
echo -e "${YELLOW}  ./scripts/deploy-complete.sh${NC}"