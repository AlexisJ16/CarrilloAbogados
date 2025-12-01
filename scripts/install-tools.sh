#!/bin/bash
set -e

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Instalador de Herramientas - Carrillo Abogados${NC}"
echo -e "${GREEN}========================================${NC}"

# Cargar versiones desde versions.yaml
VERSIONS_FILE="$(dirname "$0")/../infrastructure/versions.yaml"

# Extraer versiones usando yq
K8S_VERSION=$(yq eval '.kubernetes.version' $VERSIONS_FILE)
KUBECTL_VERSION=$(yq eval '.kubernetes.kubectl' $VERSIONS_FILE)
HELM_VERSION=$(yq eval '.kubernetes.helm' $VERSIONS_FILE)
MINIKUBE_VERSION=$(yq eval '.kubernetes.minikube' $VERSIONS_FILE)

echo -e "${YELLOW}Versiones a instalar:${NC}"
echo "  Kubernetes: $K8S_VERSION"
echo "  kubectl: $KUBECTL_VERSION"
echo "  Helm: $HELM_VERSION"
echo "  Minikube: $MINIKUBE_VERSION"

# Verificar versiones actuales
echo -e "\n${YELLOW}Verificando versiones instaladas...${NC}"

if command -v kubectl &> /dev/null; then
    CURRENT_KUBECTL=$(kubectl version --client -o json 2>/dev/null | jq -r '.clientVersion.gitVersion' | cut -d'v' -f2)
    echo "  kubectl actual: v$CURRENT_KUBECTL"
else
    CURRENT_KUBECTL="no instalado"
fi

# Instalar kubectl si no coincide
if [ "$CURRENT_KUBECTL" != "$KUBECTL_VERSION" ]; then
    echo -e "${GREEN}Instalando kubectl v$KUBECTL_VERSION...${NC}"
    curl -LO "https://dl.k8s.io/release/v${KUBECTL_VERSION}/bin/linux/amd64/kubectl"
    sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
    rm kubectl
    echo -e "${GREEN}✓ kubectl v$KUBECTL_VERSION instalado${NC}"
else
    echo -e "${GREEN}✓ kubectl ya está en la versión correcta${NC}"
fi

# Instalar Helm
if command -v helm &> /dev/null; then
    CURRENT_HELM=$(helm version --short | cut -d'v' -f2 | cut -d'+' -f1)
    echo "  Helm actual: v$CURRENT_HELM"
else
    CURRENT_HELM="no instalado"
fi

if [ "$CURRENT_HELM" != "$HELM_VERSION" ]; then
    echo -e "${GREEN}Instalando Helm v$HELM_VERSION...${NC}"
    curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash
    echo -e "${GREEN}✓ Helm instalado${NC}"
else
    echo -e "${GREEN}✓ Helm ya está en la versión correcta${NC}"
fi

# Instalar Minikube
if command -v minikube &> /dev/null; then
    CURRENT_MINIKUBE=$(minikube version --short | cut -d'v' -f2)
    echo "  Minikube actual: v$CURRENT_MINIKUBE"
else
    CURRENT_MINIKUBE="no instalado"
fi

if [ "$CURRENT_MINIKUBE" != "$MINIKUBE_VERSION" ]; then
    echo -e "${GREEN}Instalando Minikube v$MINIKUBE_VERSION...${NC}"
    curl -LO "https://storage.googleapis.com/minikube/releases/v${MINIKUBE_VERSION}/minikube-linux-amd64"
    sudo install minikube-linux-amd64 /usr/local/bin/minikube
    rm minikube-linux-amd64
    echo -e "${GREEN}✓ Minikube v$MINIKUBE_VERSION instalado${NC}"
else
    echo -e "${GREEN}✓ Minikube ya está en la versión correcta${NC}"
fi

echo -e "\n${GREEN}========================================${NC}"
echo -e "${GREEN}Instalación completada exitosamente!${NC}"
echo -e "${GREEN}========================================${NC}"

# Verificación final
echo -e "\n${YELLOW}Versiones finales instaladas:${NC}"
kubectl version --client --short
helm version --short
minikube version --short