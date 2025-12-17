#!/bin/bash
# Script de deployment rápido para WSL2
# Ejecutar: ./scripts/wsl-deploy.sh

set -e

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${BLUE}🚀 Carrillo Abogados - WSL2 Deployment Script${NC}"
echo "=============================================="

# 1. Verificar Docker
echo -e "\n${YELLOW}[1/8] Verificando Docker...${NC}"
if ! docker info &>/dev/null; then
    echo -e "${YELLOW}Iniciando Docker...${NC}"
    sudo service docker start
    sleep 3
fi
echo -e "${GREEN}✅ Docker running${NC}"

# 2. Iniciar Minikube
echo -e "\n${YELLOW}[2/8] Iniciando Minikube...${NC}"
if minikube status | grep -q "host: Running"; then
    echo -e "${GREEN}✅ Minikube ya está running${NC}"
else
    minikube start \
        --kubernetes-version=v1.34.0 \
        --driver=docker \
        --cpus=4 \
        --memory=7168 \
        --disk-size=30g \
        --addons=ingress,metrics-server
fi

# 3. Configurar Docker de Minikube
echo -e "\n${YELLOW}[3/8] Configurando Docker de Minikube...${NC}"
eval $(minikube docker-env)
echo -e "${GREEN}✅ Docker configurado${NC}"

# 4. Crear namespaces
echo -e "\n${YELLOW}[4/8] Creando namespaces...${NC}"
kubectl apply -f infrastructure/k8s-manifests/namespaces/
kubectl get namespaces | grep -E "(carrillo|databases|messaging)"
echo -e "${GREEN}✅ Namespaces creados${NC}"

# 5. Desplegar PostgreSQL
echo -e "\n${YELLOW}[5/8] Desplegando PostgreSQL...${NC}"
if kubectl get pods -n databases 2>/dev/null | grep -q postgresql; then
    echo -e "${GREEN}✅ PostgreSQL ya existe${NC}"
else
    helm repo add bitnami https://charts.bitnami.com/bitnami 2>/dev/null || true
    helm repo update
    helm install postgresql bitnami/postgresql \
        --namespace databases \
        --set auth.username=carrillo \
        --set auth.password=CarrilloAbogados2025! \
        --set auth.database=carrillo_legal_tech \
        --set primary.persistence.size=8Gi \
        --set primary.resources.requests.memory=512Mi \
        --set primary.resources.requests.cpu=250m
    echo "Esperando PostgreSQL..."
    kubectl wait --for=condition=ready pod -l app.kubernetes.io/name=postgresql -n databases --timeout=300s
fi

# 6. Desplegar NATS
echo -e "\n${YELLOW}[6/8] Desplegando NATS...${NC}"
if kubectl get pods -n messaging 2>/dev/null | grep -q nats; then
    echo -e "${GREEN}✅ NATS ya existe${NC}"
else
    helm repo add nats https://nats-io.github.io/k8s/helm/charts/ 2>/dev/null || true
    helm repo update
    helm install nats nats/nats \
        --namespace messaging \
        --set nats.jetstream.enabled=true \
        --set nats.jetstream.memStorage.enabled=true \
        --set nats.jetstream.memStorage.size=1Gi \
        --set replicaCount=1
    echo "Esperando NATS..."
    kubectl wait --for=condition=ready pod -l app.kubernetes.io/name=nats -n messaging --timeout=120s
fi

# 7. Compilar y construir
echo -e "\n${YELLOW}[7/8] Compilando proyecto...${NC}"
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
./mvnw clean package -DskipTests -T 1C

echo -e "\n${YELLOW}Construyendo imágenes Docker...${NC}"
for service in api-gateway client-service case-service payment-service document-service calendar-service notification-service n8n-integration-service user-service; do
    if [ -d "$service" ] && [ -f "$service/Dockerfile" ]; then
        echo "Building $service..."
        docker build -t carrillo/$service:latest -f $service/Dockerfile $service/ || {
            echo -e "${RED}Error building $service, continuando...${NC}"
        }
    fi
done
echo -e "${GREEN}✅ Imágenes construidas${NC}"

# 8. Desplegar con Helm
echo -e "\n${YELLOW}[8/8] Desplegando microservicios...${NC}"
if helm list -n carrillo-dev | grep -q carrillo-dev; then
    helm upgrade carrillo-dev helm-charts/carrillo-abogados/ -n carrillo-dev
else
    helm install carrillo-dev helm-charts/carrillo-abogados/ -n carrillo-dev
fi

# Resumen
echo -e "\n${GREEN}🎉 ¡Deployment completado!${NC}"
echo "=============================================="
echo ""
echo "📊 Estado del cluster:"
kubectl get pods -A | grep -E "(carrillo-dev|databases|messaging)" | head -15
echo ""
echo "🔗 Para acceder a la aplicación:"
echo "   kubectl port-forward svc/api-gateway 8080:8080 -n carrillo-dev"
echo ""
echo "🌐 Luego abrir: http://localhost:8080"
