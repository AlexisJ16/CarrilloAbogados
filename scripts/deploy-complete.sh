#!/bin/bash
set -e

# Complete CI/CD local deployment from scratch
# Purpose: Full deployment pipeline for Carrillo Abogados Legal Tech Platform
# Date: December 2025

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
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

echo -e "${CYAN}🚀 Carrillo Abogados - Complete CI/CD Deployment${NC}"
echo -e "${CYAN}=================================================${NC}"
echo ""

# Función para mostrar progreso
show_progress() {
    local message=$1
    echo -e "${BLUE}📋 $message${NC}"
}

# Función para mostrar éxito
show_success() {
    local message=$1
    echo -e "${GREEN}✅ $message${NC}"
}

# Función para mostrar advertencia
show_warning() {
    local message=$1
    echo -e "${YELLOW}⚠️ $message${NC}"
}

# Función para mostrar error
show_error() {
    local message=$1
    echo -e "${RED}❌ $message${NC}"
}

# Función para esperar confirmación del usuario
confirm_action() {
    local message=$1
    local default=${2:-"n"}
    
    if [ "$default" = "y" ]; then
        local prompt="$message [Y/n]: "
    else
        local prompt="$message [y/N]: "
    fi
    
    read -p "$prompt" -r
    REPLY=${REPLY:-$default}
    
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        return 0
    else
        return 1
    fi
}

# 1. Validar entorno
show_progress "1. Validando entorno de desarrollo..."

# Verificar herramientas requeridas
REQUIRED_TOOLS=("java" "docker" "kubectl" "helm" "minikube" "mvn")
for tool in "${REQUIRED_TOOLS[@]}"; do
    if ! command -v $tool &> /dev/null; then
        show_error "$tool no está instalado o no está en PATH"
        exit 1
    fi
done

# Verificar Java 21
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -ne "21" ]; then
    show_error "Java 21 requerido, encontrado Java $JAVA_VERSION"
    exit 1
fi

# Configurar JAVA_HOME si no está configurado
if [ -z "$JAVA_HOME" ]; then
    if [ -d "/usr/lib/jvm/java-21-openjdk-amd64" ]; then
        export JAVA_HOME="/usr/lib/jvm/java-21-openjdk-amd64"
    elif [ -d "/usr/lib/jvm/temurin-21-jdk-amd64" ]; then
        export JAVA_HOME="/usr/lib/jvm/temurin-21-jdk-amd64"
    else
        show_error "JAVA_HOME no configurado y no se puede auto-detectar"
        exit 1
    fi
    echo -e "${YELLOW}💡 JAVA_HOME configurado automáticamente: $JAVA_HOME${NC}"
fi

# Verificar que Docker esté running
if ! docker ps &> /dev/null; then
    show_error "Docker daemon no responde. Inicia Docker Desktop"
    exit 1
fi

show_success "Entorno validado correctamente"

# 2. Opcional: Limpiar entorno existente
echo ""
show_progress "2. Opciones de limpieza..."

CLEANUP=false
if confirm_action "¿Deseas limpiar el entorno existente completamente? (recomendado para deployment limpio)"; then
    CLEANUP=true
    echo ""
    show_warning "Limpiando entorno existente..."
    
    # Eliminar deployment de Helm si existe
    if helm list -n carrillo-dev | grep -q carrillo-dev; then
        echo -e "${YELLOW}🗑️ Eliminando deployment Helm existente...${NC}"
        helm uninstall carrillo-dev -n carrillo-dev || true
    fi
    
    # Eliminar PostgreSQL y NATS
    echo -e "${YELLOW}🗑️ Eliminando PostgreSQL...${NC}"
    helm uninstall postgresql -n databases || true
    
    echo -e "${YELLOW}🗑️ Eliminando NATS...${NC}"
    helm uninstall nats -n messaging || true
    
    # Esperar que los pods se eliminen
    echo -e "${YELLOW}⏳ Esperando eliminación de pods...${NC}"
    sleep 15
    
    # Eliminar imágenes Docker locales del proyecto
    echo -e "${YELLOW}🗑️ Eliminando imágenes Docker del proyecto...${NC}"
    docker images --format "{{.Repository}}:{{.Tag}}" | grep "carrillo/" | xargs -r docker rmi -f || true
    
    show_success "Entorno limpiado"
fi

# 3. Iniciar/Verificar Minikube
echo ""
show_progress "3. Configurando Minikube..."

if minikube status | grep -q "host: Running" && minikube status | grep -q "kubelet: Running"; then
    show_success "Minikube ya está ejecutándose"
else
    echo -e "${YELLOW}🔄 Iniciando Minikube...${NC}"
    minikube start \
        --kubernetes-version=v${K8S_VERSION} \
        --driver=docker \
        --cpus=4 \
        --memory=7168 \
        --disk-size=30g \
        --addons=ingress,metrics-server,dashboard,storage-provisioner
    
    show_success "Minikube iniciado con Kubernetes v${K8S_VERSION}"
fi

# Conectar al Docker daemon de Minikube
eval $(minikube docker-env)
show_success "Docker environment configurado para Minikube"

# 4. Configurar infraestructura base
echo ""
show_progress "4. Desplegando infraestructura base..."

# Crear namespaces
echo -e "${YELLOW}📁 Creando namespaces...${NC}"
kubectl apply -f infrastructure/k8s-manifests/namespaces/

# Aplicar RBAC
echo -e "${YELLOW}🔐 Configurando RBAC...${NC}"
kubectl apply -f infrastructure/k8s-manifests/rbac/

# Aplicar ConfigMaps
echo -e "${YELLOW}⚙️ Aplicando ConfigMaps...${NC}"
kubectl apply -f infrastructure/k8s-manifests/configmaps/

# Aplicar Secrets
echo -e "${YELLOW}🔑 Aplicando Secrets...${NC}"
kubectl apply -f infrastructure/k8s-manifests/secrets/

# Aplicar Network Policies
echo -e "${YELLOW}🌐 Configurando Network Policies...${NC}"
kubectl apply -f infrastructure/k8s-manifests/network-policies/

show_success "Infraestructura base desplegada"

# 5. Desplegar PostgreSQL
echo ""
show_progress "5. Desplegando PostgreSQL..."

helm repo add bitnami https://charts.bitnami.com/bitnami 2>/dev/null || true
helm repo update

echo -e "${YELLOW}🗃️ Instalando PostgreSQL...${NC}"
helm install postgresql bitnami/postgresql \
    --namespace databases \
    --set auth.username=carrillo \
    --set auth.password=$POSTGRES_PASSWORD \
    --set auth.database=carrillo_legal_tech \
    --set primary.persistence.size=8Gi \
    --set primary.resources.requests.memory=512Mi \
    --set primary.resources.requests.cpu=250m

echo -e "${YELLOW}⏳ Esperando que PostgreSQL esté ready...${NC}"
kubectl wait --for=condition=ready pod -l app=postgresql -n databases --timeout=300s

show_success "PostgreSQL desplegado y ready"

# 6. Desplegar NATS
echo ""
show_progress "6. Desplegando NATS..."

helm repo add nats https://nats-io.github.io/k8s/helm/charts/ 2>/dev/null || true
helm repo update

echo -e "${YELLOW}📨 Instalando NATS...${NC}"
helm install nats nats/nats \
    --namespace messaging \
    --set nats.jetstream.enabled=true \
    --set nats.jetstream.memStorage.enabled=true \
    --set nats.jetstream.memStorage.size=1Gi \
    --set replicaCount=1

echo -e "${YELLOW}⏳ Esperando que NATS esté ready...${NC}"
kubectl wait --for=condition=ready pod -l app=nats -n messaging --timeout=120s

show_success "NATS desplegado y ready"

# 7. Construir todas las aplicaciones
echo ""
show_progress "7. Construyendo aplicaciones..."

echo -e "${YELLOW}🔨 Compilando código fuente con Maven...${NC}"
./mvnw clean package -DskipTests -T 1C

show_success "Compilación completada"

# 8. Construir imágenes Docker
echo ""
show_progress "8. Construyendo imágenes Docker..."

for service in "${MICROSERVICES[@]}"; do
    if [ -d "$service" ]; then
        echo -e "${YELLOW}🐳 Construyendo imagen para $service...${NC}"
        docker build \
            -t "carrillo/$service:latest" \
            -f "$service/Dockerfile" \
            --build-arg JAR_FILE="$service/target/$service-v$PROJECT_VERSION.jar" \
            .
        
        if [ $? -eq 0 ]; then
            echo -e "${GREEN}   ✅ $service construido exitosamente${NC}"
        else
            show_error "Error construyendo $service"
            exit 1
        fi
    else
        show_warning "Directorio $service no encontrado"
    fi
done

show_success "Todas las imágenes Docker construidas"

# 9. Desplegar aplicaciones
echo ""
show_progress "9. Desplegando aplicaciones..."

echo -e "${YELLOW}🚀 Desplegando con Helm...${NC}"
helm install carrillo-dev helm-charts/carrillo-abogados/ -n carrillo-dev

show_success "Aplicaciones desplegadas"

# 10. Validar deployment
echo ""
show_progress "10. Validando deployment..."

echo -e "${YELLOW}⏳ Esperando que todos los microservicios estén ready...${NC}"
sleep 15

# Verificar cada servicio
FAILED_SERVICES=()
for service in "${MICROSERVICES[@]}"; do
    echo -e "${YELLOW}   Verificando $service...${NC}"
    if kubectl wait --for=condition=ready pod -l app=$service -n carrillo-dev --timeout=180s 2>/dev/null; then
        echo -e "${GREEN}   ✅ $service está ready${NC}"
    else
        echo -e "${RED}   ❌ $service falló o tomó demasiado tiempo${NC}"
        FAILED_SERVICES+=("$service")
    fi
done

# Validar infraestructura
echo -e "${YELLOW}🔍 Validando infraestructura...${NC}"

# PostgreSQL
if kubectl get pods -n databases -l app=postgresql | grep -q Running; then
    echo -e "${GREEN}   ✅ PostgreSQL está running${NC}"
else
    echo -e "${RED}   ❌ PostgreSQL no está running${NC}"
fi

# NATS
if kubectl get pods -n messaging -l app=nats | grep -q Running; then
    echo -e "${GREEN}   ✅ NATS está running${NC}"
else
    echo -e "${RED}   ❌ NATS no está running${NC}"
fi

# 11. Resumen final
echo ""
echo -e "${CYAN}🎉 Deployment Completo - Resumen${NC}"
echo -e "${CYAN}================================${NC}"
echo ""

if [ ${#FAILED_SERVICES[@]} -eq 0 ]; then
    show_success "¡Todos los servicios desplegados exitosamente!"
else
    show_warning "Algunos servicios fallaron:"
    for service in "${FAILED_SERVICES[@]}"; do
        echo -e "   ${RED}- $service${NC}"
    done
fi

echo ""
echo "📊 Estado actual del cluster:"
kubectl get pods -A | grep -E "(carrillo-dev|databases|messaging)"

echo ""
echo "🔗 Comandos útiles:"
echo "   kubectl get pods -n carrillo-dev"
echo "   kubectl logs -f deployment/api-gateway -n carrillo-dev"
echo "   kubectl port-forward svc/api-gateway 8080:8080 -n carrillo-dev"
echo "   minikube dashboard"

echo ""
echo "🧪 Para validar el deployment completo:"
echo "   ./scripts/validate-deployment.sh"

echo ""
if [ ${#FAILED_SERVICES[@]} -eq 0 ]; then
    show_success "¡Deploy completo exitoso!"
    exit 0
else
    show_warning "Deploy completado con advertencias. Revisa los servicios fallidos."
    exit 1
fi