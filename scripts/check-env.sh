#!/bin/bash
set -e

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "🔍 Validando entorno de desarrollo..."

EXIT_CODE=0

# Función para comparar versiones
version_compare() {
    local version1=$1
    local version2=$2
    
    if [ "$version1" = "$version2" ]; then
        return 0
    fi
    
    local IFS=.
    local i ver1=($version1) ver2=($version2)
    
    # fill empty fields in ver1 with zeros
    for ((i=${#ver1[@]}; i<${#ver2[@]}; i++)); do
        ver1[i]=0
    done
    
    for ((i=0; i<${#ver1[@]}; i++)); do
        if [[ -z ${ver2[i]} ]]; then
            ver2[i]=0
        fi
        if ((10#${ver1[i]} > 10#${ver2[i]})); then
            return 1
        fi
        if ((10#${ver1[i]} < 10#${ver2[i]})); then
            return 2
        fi
    done
    return 0
}

# Leer versions.yaml
VERSIONS_FILE="infrastructure/versions.yaml"

if [ ! -f "$VERSIONS_FILE" ]; then
    echo -e "${RED}❌ No se encuentra $VERSIONS_FILE${NC}"
    exit 1
fi

# Validar Java
echo -n "Checking Java... "
if java -version &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    EXPECTED_JAVA=21
    if [ "$JAVA_VERSION" -eq "$EXPECTED_JAVA" ]; then
        echo -e "${GREEN}✓ Java $JAVA_VERSION${NC}"
    else
        echo -e "${RED}✗ Java $JAVA_VERSION (esperado: $EXPECTED_JAVA)${NC}"
        echo "  Solución: export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64"
        EXIT_CODE=1
    fi
else
    echo -e "${RED}✗ Java no instalado${NC}"
    EXIT_CODE=1
fi

# Validar Docker
echo -n "Checking Docker... "
if docker --version &> /dev/null; then
    DOCKER_VERSION=$(docker --version | grep -oP '\d+\.\d+\.\d+' | head -1)
    echo -e "${GREEN}✓ Docker $DOCKER_VERSION${NC}"
    
    # Verificar que Docker daemon responde
    if ! docker ps &> /dev/null; then
        echo -e "${RED}✗ Docker daemon no responde${NC}"
        echo "  Solución: Iniciar Docker Desktop"
        EXIT_CODE=1
    fi
else
    echo -e "${RED}✗ Docker no instalado${NC}"
    EXIT_CODE=1
fi

# Validar kubectl
echo -n "Checking kubectl... "
if kubectl version --client &> /dev/null; then
    KUBECTL_VERSION=$(kubectl version --client --output=yaml 2>/dev/null | grep gitVersion | cut -d'"' -f2 | cut -d'v' -f2 | cut -d'.' -f1-2)
    EXPECTED_KUBECTL="1.31"
    if version_compare "$KUBECTL_VERSION" "$EXPECTED_KUBECTL"; then
        echo -e "${GREEN}✓ kubectl v$KUBECTL_VERSION${NC}"
    else
        echo -e "${YELLOW}⚠ kubectl v$KUBECTL_VERSION (esperado: v$EXPECTED_KUBECTL)${NC}"
    fi
else
    echo -e "${RED}✗ kubectl no instalado${NC}"
    EXIT_CODE=1
fi

# Validar Helm
echo -n "Checking Helm... "
if helm version --short &> /dev/null; then
    HELM_VERSION=$(helm version --short | grep -oP 'v\d+\.\d+\.\d+' | head -1)
    EXPECTED_HELM="v3.19"
    if echo "$HELM_VERSION" | grep -q "$EXPECTED_HELM"; then
        echo -e "${GREEN}✓ Helm $HELM_VERSION${NC}"
    else
        echo -e "${YELLOW}⚠ Helm $HELM_VERSION (esperado: $EXPECTED_HELM.x)${NC}"
    fi
else
    echo -e "${RED}✗ Helm no instalado${NC}"
    EXIT_CODE=1
fi

# Validar Minikube
echo -n "Checking Minikube... "
if minikube version &> /dev/null; then
    MINIKUBE_VERSION=$(minikube version | grep -oP 'v\d+\.\d+\.\d+' | head -1)
    EXPECTED_MINIKUBE="v1.37"
    if echo "$MINIKUBE_VERSION" | grep -q "$EXPECTED_MINIKUBE"; then
        echo -e "${GREEN}✓ Minikube $MINIKUBE_VERSION${NC}"
    else
        echo -e "${YELLOW}⚠ Minikube $MINIKUBE_VERSION (esperado: $EXPECTED_MINIKUBE.x)${NC}"
    fi
else
    echo -e "${RED}✗ Minikube no instalado${NC}"
    EXIT_CODE=1
fi

# Validar Maven
echo -n "Checking Maven... "
if mvn --version &> /dev/null; then
    MAVEN_VERSION=$(mvn --version | head -n 1 | grep -oP '\d+\.\d+\.\d+')
    echo -e "${GREEN}✓ Maven $MAVEN_VERSION${NC}"
else
    echo -e "${YELLOW}⚠ Maven no instalado (opcional si usas ./mvnw)${NC}"
fi

# Validar JAVA_HOME
echo -n "Checking JAVA_HOME... "
if [ -n "$JAVA_HOME" ]; then
    echo -e "${GREEN}✓ $JAVA_HOME${NC}"
else
    echo -e "${RED}✗ JAVA_HOME no configurado${NC}"
    echo "  Solución: export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64"
    EXIT_CODE=1
fi

# Validar PowerShell (para WSL)
echo -n "Checking PowerShell integration... "
if command -v powershell.exe &> /dev/null; then
    echo -e "${GREEN}✓ PowerShell disponible${NC}"
elif command -v pwsh &> /dev/null; then
    echo -e "${GREEN}✓ PowerShell Core disponible${NC}"
else
    echo -e "${YELLOW}⚠ PowerShell no disponible (recomendado en WSL)${NC}"
fi

echo ""
if [ ${EXIT_CODE} -eq 0 ]; then
    echo -e "${GREEN}✅ Entorno validado correctamente${NC}"
    echo ""
    echo "Versiones detectadas:"
    echo "  Java: $JAVA_VERSION"
    echo "  Docker: $DOCKER_VERSION"
    echo "  kubectl: v$KUBECTL_VERSION"
    echo "  Helm: $HELM_VERSION"
    echo "  Minikube: $MINIKUBE_VERSION"
    exit 0
else
    echo -e "${RED}❌ Entorno incompleto. Corrige los errores arriba.${NC}"
    echo ""
    echo "Comandos de instalación para WSL/Ubuntu:"
    echo "  sudo apt update && sudo apt install -y openjdk-21-jdk"
    echo "  curl -LO https://storage.googleapis.com/kubernetes-release/release/v1.31.0/bin/linux/amd64/kubectl"
    echo "  curl https://get.helm.sh/helm-v3.19.2-linux-amd64.tar.gz | tar xz"
    echo "  curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64"
    exit 1
fi