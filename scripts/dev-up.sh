#!/bin/bash
set -e

# Smart daily routine that detects state and recovers what's needed
# Purpose: Idempotent startup script for Carrillo Abogados Legal Tech Platform
# Date: December 2025

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
K8S_VERSION="1.34.0"
POSTGRES_PASSWORD="CarrilloAbogados2025!"
PROJECT_VERSION="0.1.0"

# Lista de microservicios
MICROSERVICES=(
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

echo -e "${BLUE}🚀 Carrillo Abogados - Smart Development Startup${NC}"
echo -e "${BLUE}====================================================${NC}"
echo ""

# Función para verificar si un recurso K8s existe
resource_exists() {
    local resource_type=$1
    local resource_name=$2
    local namespace=${3:-}
    
    if [ -n "$namespace" ]; then
        kubectl get $resource_type $resource_name -n $namespace &>/dev/null
    else
        kubectl get $resource_type $resource_name &>/dev/null
    fi
}

# Función para esperar que un pod esté ready
wait_for_pod() {
    local namespace=$1
    local app_label=$2
    local timeout=${3:-300}
    
    echo -e "   ${YELLOW}⏳ Esperando que $app_label esté ready...${NC}"
    kubectl wait --for=condition=ready pod -l app=$app_label -n $namespace --timeout=${timeout}s
    if [ $? -eq 0 ]; then
        echo -e "   ${GREEN}✅ $app_label está ready${NC}"
    else
        echo -e "   ${RED}❌ Timeout esperando $app_label${NC}"
        return 1
    fi
}

# 1. Verificar y iniciar Minikube si es necesario
echo -e "${BLUE}📋 1. Verificando estado de Minikube...${NC}"
if minikube status | grep -q "host: Running" && minikube status | grep -q "kubelet: Running"; then
    echo -e "${GREEN}✅ Minikube ya está ejecutándose${NC}"
else
    echo -e "${YELLOW}🔄 Iniciando Minikube...${NC}"
    minikube start \
        --kubernetes-version=v${K8S_VERSION} \
        --driver=docker \
        --cpus=4 \
        --memory=7168 \
        --disk-size=30g \
        --addons=ingress,metrics-server,dashboard,storage-provisioner
    
    echo -e "${GREEN}✅ Minikube iniciado con Kubernetes v${K8S_VERSION}${NC}"
fi

# Conectar al Docker daemon de Minikube
eval $(minikube docker-env)

# 2. Verificar namespaces
echo -e "${BLUE}📋 2. Verificando namespaces...${NC}"
NAMESPACES=("carrillo-dev" "databases" "messaging")

for ns in "${NAMESPACES[@]}"; do
    if resource_exists namespace $ns; then
        echo -e "${GREEN}✅ Namespace $ns existe${NC}"
    else
        echo -e "${YELLOW}🔧 Creando namespace $ns...${NC}"
        kubectl apply -f infrastructure/k8s-manifests/namespaces/${ns}-namespace.yaml
        echo -e "${GREEN}✅ Namespace $ns creado${NC}"
    fi
done

# 3. Verificar y aplicar RBAC
echo -e "${BLUE}📋 3. Verificando RBAC...${NC}"
RBAC_FILES=(
    "api-gateway-rbac.yaml"
    "database-access-rbac.yaml"
    "microservices-rbac.yaml"
)

for rbac_file in "${RBAC_FILES[@]}"; do
    echo -e "${YELLOW}🔧 Aplicando $rbac_file...${NC}"
    kubectl apply -f infrastructure/k8s-manifests/rbac/$rbac_file
done
echo -e "${GREEN}✅ RBAC configurado${NC}"

# 4. Verificar y aplicar ConfigMaps
echo -e "${BLUE}📋 4. Verificando ConfigMaps...${NC}"
CONFIG_FILES=(
    "application-common-config.yaml"
    "database-config.yaml"
    "nats-config.yaml"
    "api-gateway-config.yaml"
)

for config_file in "${CONFIG_FILES[@]}"; do
    config_name=$(basename $config_file .yaml)
    if resource_exists configmap $config_name carrillo-dev; then
        echo -e "${GREEN}✅ ConfigMap $config_name existe${NC}"
    else
        echo -e "${YELLOW}🔧 Creando ConfigMap $config_name...${NC}"
        kubectl apply -f infrastructure/k8s-manifests/configmaps/$config_file
        echo -e "${GREEN}✅ ConfigMap $config_name creado${NC}"
    fi
done

# 5. Verificar y aplicar Secrets
echo -e "${BLUE}📋 5. Verificando Secrets...${NC}"
if resource_exists secret postgresql-secret databases; then
    echo -e "${GREEN}✅ PostgreSQL secret existe${NC}"
else
    echo -e "${YELLOW}🔧 Creando PostgreSQL secret...${NC}"
    kubectl apply -f infrastructure/k8s-manifests/secrets/postgresql-secret.yaml
    echo -e "${GREEN}✅ PostgreSQL secret creado${NC}"
fi

# 6. Verificar y aplicar Network Policies
echo -e "${BLUE}📋 6. Verificando Network Policies...${NC}"
POLICY_FILES=(
    "default-deny-all.yaml"
    "api-gateway-network-policy.yaml"
    "database-network-policy.yaml"
    "microservices-network-policy.yaml"
    "nats-network-policy.yaml"
)

for policy_file in "${POLICY_FILES[@]}"; do
    echo -e "${YELLOW}🔧 Aplicando $policy_file...${NC}"
    kubectl apply -f infrastructure/k8s-manifests/network-policies/$policy_file
done
echo -e "${GREEN}✅ Network Policies configuradas${NC}"

# 7. Verificar y desplegar PostgreSQL
echo -e "${BLUE}📋 7. Verificando PostgreSQL...${NC}"
if resource_exists statefulset postgresql databases; then
    echo -e "${GREEN}✅ PostgreSQL ya está desplegado${NC}"
    # Verificar si está running
    if kubectl get pods -n databases -l app=postgresql | grep -q Running; then
        echo -e "${GREEN}✅ PostgreSQL está running${NC}"
    else
        echo -e "${YELLOW}⏳ PostgreSQL existe pero no está ready, esperando...${NC}"
        wait_for_pod databases postgresql 300
    fi
else
    echo -e "${YELLOW}🔧 Desplegando PostgreSQL...${NC}"
    helm repo add bitnami https://charts.bitnami.com/bitnami 2>/dev/null || true
    helm repo update
    
    helm install postgresql bitnami/postgresql \
        --namespace databases \
        --set auth.username=carrillo \
        --set auth.password=$POSTGRES_PASSWORD \
        --set auth.database=carrillo_legal_tech \
        --set primary.persistence.size=8Gi \
        --set primary.resources.requests.memory=512Mi \
        --set primary.resources.requests.cpu=250m
    
    wait_for_pod databases postgresql 300
    echo -e "${GREEN}✅ PostgreSQL desplegado y ready${NC}"
fi

# 8. Verificar y desplegar NATS
echo -e "${BLUE}📋 8. Verificando NATS...${NC}"
if resource_exists deployment nats messaging; then
    echo -e "${GREEN}✅ NATS ya está desplegado${NC}"
    # Verificar si está running
    if kubectl get pods -n messaging -l app=nats | grep -q Running; then
        echo -e "${GREEN}✅ NATS está running${NC}"
    else
        echo -e "${YELLOW}⏳ NATS existe pero no está ready, esperando...${NC}"
        wait_for_pod messaging nats 120
    fi
else
    echo -e "${YELLOW}🔧 Desplegando NATS...${NC}"
    helm repo add nats https://nats-io.github.io/k8s/helm/charts/ 2>/dev/null || true
    helm repo update
    
    helm install nats nats/nats \
        --namespace messaging \
        --set nats.jetstream.enabled=true \
        --set nats.jetstream.memStorage.enabled=true \
        --set nats.jetstream.memStorage.size=1Gi \
        --set replicaCount=1
    
    wait_for_pod messaging nats 120
    echo -e "${GREEN}✅ NATS desplegado y ready${NC}"
fi

# 9. Construir imágenes Docker solo si no existen
echo -e "${BLUE}📋 9. Verificando imágenes Docker...${NC}"
IMAGES_TO_BUILD=()

for service in "${MICROSERVICES[@]}"; do
    if docker images --format "{{.Repository}}:{{.Tag}}" | grep -q "carrillo/$service:latest"; then
        echo -e "${GREEN}✅ Imagen carrillo/$service:latest existe${NC}"
    else
        echo -e "${YELLOW}📦 Imagen carrillo/$service:latest no existe, marcada para construcción${NC}"
        IMAGES_TO_BUILD+=("$service")
    fi
done

if [ ${#IMAGES_TO_BUILD[@]} -gt 0 ]; then
    echo -e "${BLUE}🏗️ Construyendo ${#IMAGES_TO_BUILD[@]} imágenes faltantes...${NC}"
    
    # Configurar JAVA_HOME si no está configurado
    if [ -z "$JAVA_HOME" ]; then
        if [ -d "/usr/lib/jvm/java-21-openjdk-amd64" ]; then
            export JAVA_HOME="/usr/lib/jvm/java-21-openjdk-amd64"
        elif [ -d "/usr/lib/jvm/temurin-21-jdk-amd64" ]; then
            export JAVA_HOME="/usr/lib/jvm/temurin-21-jdk-amd64"
        fi
    fi
    
    # Build JARs primero
    echo -e "${YELLOW}🔨 Construyendo JARs...${NC}"
    ./mvnw clean package -DskipTests -T 1C
    
    # Construir solo las imágenes faltantes
    for service in "${IMAGES_TO_BUILD[@]}"; do
        if [ -d "$service" ]; then
            echo -e "${YELLOW}🐳 Construyendo imagen para $service...${NC}"
            docker build \
                -t "carrillo/$service:latest" \
                -f "$service/Dockerfile" \
                --build-arg JAR_FILE="$service/target/$service-v$PROJECT_VERSION.jar" \
                .
            
            if [ $? -eq 0 ]; then
                echo -e "${GREEN}✅ Imagen $service construida${NC}"
            else
                echo -e "${RED}❌ Error construyendo $service${NC}"
                exit 1
            fi
        fi
    done
else
    echo -e "${GREEN}✅ Todas las imágenes Docker existen${NC}"
fi

# 10. Desplegar aplicaciones con Helm
echo -e "${BLUE}📋 10. Desplegando aplicaciones...${NC}"
if helm list -n carrillo-dev | grep -q carrillo-dev; then
    echo -e "${YELLOW}🔄 Actualizando deployment existente...${NC}"
    helm upgrade carrillo-dev helm-charts/carrillo-abogados/ -n carrillo-dev
else
    echo -e "${YELLOW}🚀 Desplegando aplicaciones por primera vez...${NC}"
    helm install carrillo-dev helm-charts/carrillo-abogados/ -n carrillo-dev
fi

# 11. Verificar que todos los pods estén ready
echo -e "${BLUE}📋 11. Verificando estado de aplicaciones...${NC}"
echo -e "${YELLOW}⏳ Esperando que todos los microservicios estén ready...${NC}"

sleep 10  # Dar tiempo para que los pods inicien

for service in "${MICROSERVICES[@]}"; do
    echo -e "${YELLOW}   Verificando $service...${NC}"
    if kubectl get pods -n carrillo-dev -l app=$service | grep -q Running; then
        echo -e "${GREEN}   ✅ $service está running${NC}"
    else
        echo -e "${YELLOW}   ⏳ Esperando $service...${NC}"
        kubectl wait --for=condition=ready pod -l app=$service -n carrillo-dev --timeout=180s 2>/dev/null || {
            echo -e "${YELLOW}   ⚠️ $service tomó más tiempo del esperado, continuando...${NC}"
        }
    fi
done

# 12. Resumen final
echo ""
echo -e "${GREEN}🎉 ¡Entorno de desarrollo listo!${NC}"
echo -e "${BLUE}====================================${NC}"
echo ""
echo "📊 Estado del cluster:"
kubectl get pods -A | grep -E "(carrillo-dev|databases|messaging)"
echo ""
echo "🔗 Comandos útiles:"
echo "   kubectl get pods -n carrillo-dev"
echo "   kubectl logs -f deployment/api-gateway -n carrillo-dev"
echo "   minikube dashboard"
echo "   kubectl port-forward svc/api-gateway 8080:8080 -n carrillo-dev"
echo ""
echo -e "${GREEN}✅ dev-up.sh completado exitosamente${NC}"