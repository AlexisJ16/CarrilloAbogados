#!/bin/bash
# ============================================================================
# RESET.SH - Limpieza Total del Entorno
# ============================================================================
# Proyecto: Carrillo Abogados Legal Tech Platform
# Uso: ./scripts/reset.sh
# ============================================================================

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${RED}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${RED}  ⚠️  RESET COMPLETO DEL ENTORNO${NC}"
echo -e "${RED}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo "  Este script eliminará:"
echo "    • Cluster de Minikube"
echo "    • Imágenes Docker del proyecto"
echo "    • Archivos compilados (target/)"
echo ""
echo -e "${RED}  Esta operación es IRREVERSIBLE${NC}"
echo ""
read -p "  ¿Continuar? (escribe 'SI' para confirmar): " CONFIRM

if [ "$CONFIRM" != "SI" ]; then
    echo -e "${YELLOW}  Operación cancelada.${NC}"
    exit 0
fi

echo ""
echo -e "${BLUE}[1/4] Eliminando cluster Minikube...${NC}"
if command -v minikube &> /dev/null; then
    minikube stop 2>/dev/null || true
    minikube delete --all --purge 2>/dev/null || true
    rm -rf ~/.minikube 2>/dev/null || true
    echo -e "${GREEN}  ✓ Minikube eliminado${NC}"
else
    echo -e "${YELLOW}  ⚠ Minikube no instalado${NC}"
fi

echo -e "${BLUE}[2/4] Limpiando imágenes Docker...${NC}"
if command -v docker &> /dev/null && docker info &>/dev/null; then
    # Solo eliminar imágenes del proyecto
    docker images --format "{{.Repository}}:{{.Tag}}" | grep "carrilloabogados/" | xargs -r docker rmi -f 2>/dev/null || true
    # Limpiar imágenes huérfanas
    docker image prune -f 2>/dev/null || true
    echo -e "${GREEN}  ✓ Imágenes limpiadas${NC}"
else
    echo -e "${YELLOW}  ⚠ Docker no disponible${NC}"
fi

echo -e "${BLUE}[3/4] Limpiando compilaciones Maven...${NC}"
find . -type d -name "target" -exec rm -rf {} + 2>/dev/null || true
echo -e "${GREEN}  ✓ Directorios target eliminados${NC}"

echo -e "${BLUE}[4/4] Limpiando contextos kubectl...${NC}"
if command -v kubectl &> /dev/null; then
    kubectl config delete-context minikube 2>/dev/null || true
    kubectl config delete-cluster minikube 2>/dev/null || true
    echo -e "${GREEN}  ✓ Contextos limpiados${NC}"
fi

echo ""
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}  ✅ Limpieza completada${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo -e "  Para redesplegar: ${BLUE}./scripts/deploy.sh${NC}"
echo ""
