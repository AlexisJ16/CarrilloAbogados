#!/bin/bash
# ============================================================================
# CHECK.SH - Verificación de Prerrequisitos
# ============================================================================
# Proyecto: Carrillo Abogados Legal Tech Platform
# Uso: ./scripts/check.sh
# ============================================================================

set -e

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}  🔍 Carrillo Abogados - Verificación de Prerrequisitos${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

ERRORS=0
WARNINGS=0

check_command() {
    command -v $1 &> /dev/null
}

# 1. Java 21
echo -n "  Java 21 LTS.............. "
if check_command java; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -eq "21" ]; then
        echo -e "${GREEN}✓ Java $JAVA_VERSION${NC}"
    else
        echo -e "${RED}✗ Java $JAVA_VERSION (requiere 21)${NC}"
        ((ERRORS++))
    fi
else
    echo -e "${RED}✗ No instalado${NC}"
    ((ERRORS++))
fi

# 2. Maven
echo -n "  Maven.................... "
if [ -f "./mvnw" ]; then
    echo -e "${GREEN}✓ Maven Wrapper disponible${NC}"
elif check_command mvn; then
    MVN_VERSION=$(mvn --version 2>/dev/null | head -n1 | grep -oP '\d+\.\d+')
    echo -e "${GREEN}✓ Maven $MVN_VERSION${NC}"
else
    echo -e "${RED}✗ No instalado${NC}"
    ((ERRORS++))
fi

# 3. Docker
echo -n "  Docker................... "
if check_command docker; then
    DOCKER_VERSION=$(docker --version | grep -oP '\d+\.\d+' | head -1)
    if docker info &>/dev/null; then
        echo -e "${GREEN}✓ Docker $DOCKER_VERSION${NC}"
    else
        echo -e "${YELLOW}⚠ Docker $DOCKER_VERSION (no running)${NC}"
        ((WARNINGS++))
    fi
else
    echo -e "${RED}✗ No instalado${NC}"
    ((ERRORS++))
fi

# 4. kubectl
echo -n "  kubectl.................. "
if check_command kubectl; then
    KUBECTL_VERSION=$(kubectl version --client -o yaml 2>/dev/null | grep gitVersion | head -1 | grep -oP 'v\d+\.\d+')
    echo -e "${GREEN}✓ kubectl $KUBECTL_VERSION${NC}"
else
    echo -e "${RED}✗ No instalado${NC}"
    ((ERRORS++))
fi

# 5. Minikube
echo -n "  Minikube................. "
if check_command minikube; then
    MINIKUBE_VERSION=$(minikube version --short 2>/dev/null | grep -oP 'v\d+\.\d+')
    echo -e "${GREEN}✓ Minikube $MINIKUBE_VERSION${NC}"
else
    echo -e "${RED}✗ No instalado${NC}"
    ((ERRORS++))
fi

# 6. Helm
echo -n "  Helm..................... "
if check_command helm; then
    HELM_VERSION=$(helm version --short 2>/dev/null | grep -oP 'v\d+\.\d+')
    echo -e "${GREEN}✓ Helm $HELM_VERSION${NC}"
else
    echo -e "${RED}✗ No instalado${NC}"
    ((ERRORS++))
fi

# Resumen
echo ""
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

if [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}  ✅ Entorno listo para desarrollo.${NC}"
    echo ""
    echo -e "  Siguiente: ${BLUE}./scripts/deploy.sh${NC}"
    exit 0
else
    echo -e "${RED}  ❌ Faltan $ERRORS herramienta(s).${NC}"
    echo ""
    echo "  Instalar en Ubuntu/WSL2:"
    echo "    sudo apt install -y openjdk-21-jdk"
    echo "    curl -fsSL https://get.docker.com | sh"
    exit 1
fi
