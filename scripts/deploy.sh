#!/bin/bash
# ============================================================================
# DEPLOY.SH - Despliegue Completo en Kubernetes
# ============================================================================
# Proyecto: Carrillo Abogados Legal Tech Platform
# Uso: ./scripts/deploy.sh
# ============================================================================

set -e

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
CYAN='\033[0;36m'
NC='\033[0m'

# Configuración
VERSION="0.2.0"
NAMESPACE="carrillo-dev"
POSTGRES_PASSWORD="CarrilloAbogados2025!"

# Microservicios a desplegar
SERVICES=(
    "api-gateway"
    "client-service"
    "case-service"
    "payment-service"
    "document-service"
    "calendar-service"
    "notification-service"
    "n8n-integration-service"
)

echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${CYAN}  🚀 Carrillo Abogados - Despliegue en Kubernetes${NC}"
echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

# ============================================================================
# PASO 1: Docker
# ============================================================================
echo -e "${BLUE}[1/7] Verificando Docker...${NC}"
if ! docker info &>/dev/null; then
    echo -e "${YELLOW}  Iniciando Docker...${NC}"
    sudo service docker start
    sleep 3
fi
echo -e "${GREEN}  ✓ Docker running${NC}"

# ============================================================================
# PASO 2: Minikube
# ============================================================================
echo -e "${BLUE}[2/7] Iniciando Minikube...${NC}"
if minikube status 2>/dev/null | grep -q "host: Running"; then
    echo -e "${GREEN}  ✓ Minikube ya está running${NC}"
else
    minikube start \
        --kubernetes-version=v1.34.0 \
        --driver=docker \
        --cpus=4 \
        --memory=6144 \
        --disk-size=20g
    echo -e "${GREEN}  ✓ Minikube iniciado${NC}"
fi

# Configurar Docker de Minikube
eval $(minikube docker-env)

# ============================================================================
# PASO 3: Namespaces
# ============================================================================
echo -e "${BLUE}[3/7] Creando namespaces...${NC}"
kubectl create namespace carrillo-dev --dry-run=client -o yaml | kubectl apply -f -
kubectl create namespace databases --dry-run=client -o yaml | kubectl apply -f -
kubectl create namespace messaging --dry-run=client -o yaml | kubectl apply -f -
echo -e "${GREEN}  ✓ Namespaces creados${NC}"

# ============================================================================
# PASO 4: PostgreSQL
# ============================================================================
echo -e "${BLUE}[4/7] Desplegando PostgreSQL...${NC}"
if kubectl get pods -n databases 2>/dev/null | grep -q postgresql; then
    echo -e "${GREEN}  ✓ PostgreSQL ya existe${NC}"
else
    helm repo add bitnami https://charts.bitnami.com/bitnami 2>/dev/null || true
    helm repo update bitnami
    helm install postgresql bitnami/postgresql \
        --namespace databases \
        --set auth.username=carrillo \
        --set auth.password=$POSTGRES_PASSWORD \
        --set auth.database=carrillo_legal_tech \
        --set primary.persistence.size=5Gi \
        --set primary.resources.requests.memory=256Mi \
        --set primary.resources.requests.cpu=100m \
        --wait --timeout=5m
    echo -e "${GREEN}  ✓ PostgreSQL desplegado${NC}"
fi

# ============================================================================
# PASO 5: NATS
# ============================================================================
echo -e "${BLUE}[5/7] Desplegando NATS...${NC}"
if kubectl get pods -n messaging 2>/dev/null | grep -q nats; then
    echo -e "${GREEN}  ✓ NATS ya existe${NC}"
else
    helm repo add nats https://nats-io.github.io/k8s/helm/charts/ 2>/dev/null || true
    helm repo update nats
    helm install nats nats/nats \
        --namespace messaging \
        --set nats.jetstream.enabled=true \
        --set nats.jetstream.memStorage.size=256Mi \
        --set replicaCount=1 \
        --wait --timeout=3m
    echo -e "${GREEN}  ✓ NATS desplegado${NC}"
fi

# ============================================================================
# PASO 6: Build
# ============================================================================
echo -e "${BLUE}[6/7] Compilando y construyendo imágenes...${NC}"

# Compilar con Maven
export JAVA_HOME=${JAVA_HOME:-/usr/lib/jvm/java-21-openjdk-amd64}
echo -e "  Compilando proyecto..."
./mvnw clean package -DskipTests -T 1C -q

# Construir imágenes Docker
echo -e "  Construyendo imágenes Docker..."
for service in "${SERVICES[@]}"; do
    if [ -d "$service" ] && [ -f "$service/Dockerfile" ]; then
        docker build -t carrilloabogados/$service:v$VERSION -f $service/Dockerfile $service/ -q
        echo -e "    ${GREEN}✓${NC} $service"
    fi
done
echo -e "${GREEN}  ✓ Build completado${NC}"

# ============================================================================
# PASO 7: Helm Deploy
# ============================================================================
echo -e "${BLUE}[7/7] Desplegando microservicios...${NC}"

# Aplicar ConfigMaps y Secrets
kubectl apply -f k8s-manifests/configmaps/ 2>/dev/null || true
kubectl apply -f k8s-manifests/secrets/ 2>/dev/null || true

# Desplegar con Helm
if helm list -n $NAMESPACE 2>/dev/null | grep -q carrillo-dev; then
    helm upgrade carrillo-dev helm-charts/carrillo-abogados/ -n $NAMESPACE
else
    helm install carrillo-dev helm-charts/carrillo-abogados/ -n $NAMESPACE
fi
echo -e "${GREEN}  ✓ Microservicios desplegados${NC}"

# ============================================================================
# RESUMEN
# ============================================================================
echo ""
echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}  🎉 ¡Despliegue completado!${NC}"
echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo "  Próximos pasos:"
echo ""
echo -e "  1. Verificar pods:     ${BLUE}kubectl get pods -n carrillo-dev${NC}"
echo -e "  2. Validar deploy:     ${BLUE}./scripts/validate.sh${NC}"
echo -e "  3. Acceder a la app:   ${BLUE}kubectl port-forward svc/carrillo-dev-api-gateway 8080:8080 -n carrillo-dev${NC}"
echo -e "  4. Probar servicios:   ${BLUE}./scripts/test.sh${NC}"
echo ""
