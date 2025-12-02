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
        export PATH=$JAVA_HOME/bin:$PATH
    elif [ -d "/usr/lib/jvm/temurin-21-jdk-amd64" ]; then
        export JAVA_HOME="/usr/lib/jvm/temurin-21-jdk-amd64"
        export PATH=$JAVA_HOME/bin:$PATH
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
    if helm list -n carrillo-dev 2>/dev/null | grep -q carrillo-dev; then
        echo -e "${YELLOW}🗑️ Eliminando deployment Helm existente...${NC}"
        helm uninstall carrillo-dev -n carrillo-dev || true
    fi
    
    # Eliminar PostgreSQL y NATS
    if helm list -n databases 2>/dev/null | grep -q postgresql; then
        echo -e "${YELLOW}🗑️ Eliminando PostgreSQL...${NC}"
        helm uninstall postgresql -n databases || true
    fi
    
    if helm list -n messaging 2>/dev/null | grep -q nats; then
        echo -e "${YELLOW}🗑️ Eliminando NATS...${NC}"
        helm uninstall nats -n messaging || true
    fi
    
    # Esperar que los pods se eliminen
    echo -e "${YELLOW}⏳ Esperando eliminación de pods...${NC}"
    sleep 15
    
    # Eliminar imágenes Docker locales del proyecto
    echo -e "${YELLOW}🗑️ Eliminando imágenes Docker del proyecto...${NC}"
    docker images --format "{{.Repository}}:{{.Tag}}" | grep "carrilloabogados/" | xargs -r docker rmi -f 2>/dev/null || true
    
    show_success "Entorno limpiado"
fi

# 3. Iniciar/Verificar Minikube
echo ""
show_progress "3. Configurando Minikube..."

MINIKUBE_RUNNING=false
if minikube status 2>/dev/null | grep -q "host: Running" && minikube status 2>/dev/null | grep -q "kubelet: Running"; then
    MINIKUBE_RUNNING=true
    show_success "Minikube ya está ejecutándose"
else
    echo -e "${YELLOW}🔄 Iniciando Minikube...${NC}"
    minikube start \
        --kubernetes-version=v${K8S_VERSION} \
        --driver=docker \
        --cpus=4 \
        --memory=7168 \
        --disk-size=30g
    
    show_success "Minikube iniciado con Kubernetes v${K8S_VERSION}"
fi

# Habilitar addons necesarios
echo -e "${YELLOW}🔌 Habilitando addons de Minikube...${NC}"

# Lista de addons requeridos
REQUIRED_ADDONS=("storage-provisioner" "default-storageclass" "metrics-server")
OPTIONAL_ADDONS=("dashboard")

# Habilitar addons requeridos
for addon in "${REQUIRED_ADDONS[@]}"; do
    if ! minikube addons list | grep "$addon" | grep -q "enabled"; then
        echo -e "${YELLOW}   Habilitando $addon...${NC}"
        minikube addons enable "$addon"
    else
        echo -e "${GREEN}   ✓ $addon ya está habilitado${NC}"
    fi
done

# Habilitar addons opcionales
for addon in "${OPTIONAL_ADDONS[@]}"; do
    if ! minikube addons list | grep "$addon" | grep -q "enabled"; then
        echo -e "${YELLOW}   Habilitando $addon...${NC}"
        minikube addons enable "$addon" || show_warning "$addon falló (no crítico)"
    else
        echo -e "${GREEN}   ✓ $addon ya está habilitado${NC}"
    fi
done

# Habilitar Ingress con timeout extendido
echo -e "${YELLOW}   Habilitando ingress (puede tardar 2-3 minutos)...${NC}"
if ! minikube addons list | grep "ingress" | grep -q "enabled"; then
    # Intentar habilitar ingress
    minikube addons enable ingress 2>&1 | grep -v "context deadline exceeded" || true
    
    # Verificar que el pod de ingress se está iniciando
    echo -e "${YELLOW}   Esperando a que ingress-nginx esté listo...${NC}"
    for i in {1..60}; do
        if kubectl get pods -n ingress-nginx -l app.kubernetes.io/name=ingress-nginx 2>/dev/null | grep -q "Running"; then
            echo -e "${GREEN}   ✓ Ingress controller está Running${NC}"
            break
        fi
        if [ $i -eq 60 ]; then
            show_warning "Ingress tardó más de lo esperado, pero puede estar iniciándose en segundo plano"
        fi
        sleep 3
    done
else
    echo -e "${GREEN}   ✓ ingress ya está habilitado${NC}"
fi

show_success "Addons de Minikube configurados"

# Conectar al Docker daemon de Minikube
eval $(minikube docker-env)
show_success "Docker environment configurado para Minikube"

# 4. Configurar infraestructura base
echo ""
show_progress "4. Desplegando infraestructura base..."

# Crear namespaces
echo -e "${YELLOW}📁 Creando namespaces...${NC}"
kubectl apply -f infrastructure/k8s-manifests/namespaces/

# Aplicar ConfigMaps
echo -e "${YELLOW}⚙️ Aplicando ConfigMaps...${NC}"
kubectl apply -f infrastructure/k8s-manifests/configmaps/ -n carrillo-dev

# Aplicar Secrets
echo -e "${YELLOW}🔑 Aplicando Secrets...${NC}"
kubectl apply -f infrastructure/k8s-manifests/secrets/postgresql-secret.yaml -n carrillo-dev

# Aplicar Network Policies
echo -e "${YELLOW}🌐 Configurando Network Policies...${NC}"
kubectl apply -f infrastructure/k8s-manifests/network-policies/

show_success "Infraestructura base desplegada"

# 5. Configurar repositorios de Helm
echo ""
show_progress "5. Configurando repositorios de Helm..."

echo -e "${YELLOW}📦 Agregando repositorio Bitnami...${NC}"
helm repo add bitnami https://charts.bitnami.com/bitnami 2>/dev/null || true

echo -e "${YELLOW}📦 Agregando repositorio NATS...${NC}"
helm repo add nats https://nats-io.github.io/k8s/helm/charts/ 2>/dev/null || true

echo -e "${YELLOW}🔄 Actualizando repositorios de Helm...${NC}"
helm repo update

show_success "Repositorios de Helm configurados"

# 6. Desplegar PostgreSQL
echo ""
show_progress "6. Desplegando PostgreSQL..."

echo -e "${YELLOW}🗃️ Instalando PostgreSQL...${NC}"
helm upgrade --install postgresql bitnami/postgresql \
    --namespace databases \
    --set auth.username=carrillo \
    --set auth.password=$POSTGRES_PASSWORD \
    --set auth.database=carrillo_legal_tech \
    --set primary.persistence.size=8Gi \
    --set primary.resources.requests.memory=512Mi \
    --set primary.resources.requests.cpu=250m \
    --wait --timeout=5m

echo -e "${YELLOW}⏳ Verificando que PostgreSQL esté ready...${NC}"
kubectl wait --for=condition=ready pod -l app.kubernetes.io/name=postgresql -n databases --timeout=300s

show_success "PostgreSQL desplegado y ready"

# 7. Desplegar NATS
echo ""
show_progress "7. Desplegando NATS..."

echo -e "${YELLOW}📨 Instalando NATS...${NC}"
helm upgrade --install nats nats/nats \
    --namespace messaging \
    --set nats.jetstream.enabled=true \
    --set nats.jetstream.memStorage.enabled=true \
    --set nats.jetstream.memStorage.size=1Gi \
    --set replicaCount=1 \
    --wait --timeout=5m

echo -e "${YELLOW}⏳ Verificando que NATS esté ready...${NC}"
kubectl wait --for=condition=ready pod -l app.kubernetes.io/name=nats -n messaging --timeout=300s

show_success "NATS desplegado y ready"

# 8. Construir todas las aplicaciones
echo ""
show_progress "8. Construyendo aplicaciones..."

echo -e "${YELLOW}🔨 Compilando código fuente con Maven (esto puede tardar 10-15 minutos)...${NC}"
mvn clean package -DskipTests -T 1C

show_success "Compilación completada"

# 9. Construir imágenes Docker
echo ""
show_progress "9. Construyendo imágenes Docker..."

BUILD_ERRORS=0
for service in "${MICROSERVICES[@]}"; do
    if [ -d "$service" ]; then
        echo -e "${YELLOW}🐳 Construyendo imagen para $service...${NC}"
        
        # Verificar que el JAR existe
        JAR_PATH="$service/target/$service-v$PROJECT_VERSION.jar"
        if [ ! -f "$JAR_PATH" ]; then
            show_error "JAR no encontrado: $JAR_PATH"
            BUILD_ERRORS=$((BUILD_ERRORS + 1))
            continue
        fi
        
        # Construir imagen
        docker build \
            -t "carrilloabogados/$service:v$PROJECT_VERSION" \
            -f "$service/Dockerfile" \
            "$service/" 2>&1 | grep -v "^#" || true
        
        if [ ${PIPESTATUS[0]} -eq 0 ]; then
            echo -e "${GREEN}   ✅ $service construido exitosamente${NC}"
        else
            show_error "Error construyendo $service"
            BUILD_ERRORS=$((BUILD_ERRORS + 1))
        fi
    else
        show_warning "Directorio $service no encontrado"
    fi
done

if [ $BUILD_ERRORS -gt 0 ]; then
    show_error "$BUILD_ERRORS imágenes fallaron al construirse"
    exit 1
fi

show_success "Todas las imágenes Docker construidas"

# Listar imágenes creadas
echo -e "${YELLOW}📋 Imágenes creadas:${NC}"
docker images | grep "carrilloabogados" | head -15

# 10. Actualizar dependencias de Helm
echo ""
show_progress "10. Actualizando dependencias de Helm..."

cd helm-charts/carrillo-abogados/
helm dependency update
cd ../..

show_success "Dependencias de Helm actualizadas"

# 11. Desplegar aplicaciones
echo ""
show_progress "11. Desplegando aplicaciones..."

echo -e "${YELLOW}🚀 Desplegando con Helm...${NC}"
helm upgrade --install carrillo-dev helm-charts/carrillo-abogados/ \
    -n carrillo-dev \
    --wait --timeout=10m

show_success "Aplicaciones desplegadas"

# 12. Validar deployment
echo ""
show_progress "12. Validando deployment..."

echo -e "${YELLOW}⏳ Esperando que todos los microservicios estén ready...${NC}"
sleep 20

# Verificar cada servicio
FAILED_SERVICES=()
for service in "${MICROSERVICES[@]}"; do
    echo -e "${YELLOW}   Verificando $service...${NC}"
    
    # Verificar que el pod existe
    if ! kubectl get pods -n carrillo-dev -l app=$service 2>/dev/null | grep -q "$service"; then
        echo -e "${RED}   ❌ $service: Pod no encontrado${NC}"
        FAILED_SERVICES+=("$service")
        continue
    fi
    
    # Esperar a que esté ready
    if kubectl wait --for=condition=ready pod -l app=$service -n carrillo-dev --timeout=180s 2>/dev/null; then
        echo -e "${GREEN}   ✅ $service está ready${NC}"
    else
        echo -e "${RED}   ❌ $service falló o tomó demasiado tiempo${NC}"
        FAILED_SERVICES+=("$service")
        
        # Mostrar logs del pod fallido
        POD_NAME=$(kubectl get pods -n carrillo-dev -l app=$service -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)
        if [ -n "$POD_NAME" ]; then
            echo -e "${YELLOW}   📋 Últimas líneas del log:${NC}"
            kubectl logs -n carrillo-dev "$POD_NAME" --tail=10 2>/dev/null || echo "   No se pudieron obtener logs"
        fi
    fi
done

# Validar infraestructura
echo ""
echo -e "${YELLOW}🔍 Validando infraestructura...${NC}"

# PostgreSQL
if kubectl get pods -n databases -l app.kubernetes.io/name=postgresql 2>/dev/null | grep -q "Running"; then
    echo -e "${GREEN}   ✅ PostgreSQL está running${NC}"
else
    echo -e "${RED}   ❌ PostgreSQL no está running${NC}"
fi

# NATS
if kubectl get pods -n messaging -l app.kubernetes.io/name=nats 2>/dev/null | grep -q "Running"; then
    echo -e "${GREEN}   ✅ NATS está running${NC}"
else
    echo -e "${RED}   ❌ NATS no está running${NC}"
fi

# 13. Resumen final
echo ""
echo -e "${CYAN}🎉 Deployment Completo - Resumen${NC}"
echo -e "${CYAN}================================${NC}"
echo ""

if [ ${#FAILED_SERVICES[@]} -eq 0 ]; then
    show_success "¡Todos los servicios desplegados exitosamente!"
else
    show_warning "Algunos servicios fallaron o tardaron demasiado:"
    for service in "${FAILED_SERVICES[@]}"; do
        echo -e "   ${RED}- $service${NC}"
    done
    echo ""
    echo -e "${YELLOW}💡 Para investigar los fallos:${NC}"
    echo "   kubectl get pods -n carrillo-dev"
    echo "   kubectl describe pod <pod-name> -n carrillo-dev"
    echo "   kubectl logs <pod-name> -n carrillo-dev"
fi

echo ""
echo "📊 Estado actual del cluster:"
echo ""
kubectl get pods -n carrillo-dev -o wide 2>/dev/null || kubectl get pods -n carrillo-dev
echo ""
kubectl get pods -n databases
echo ""
kubectl get pods -n messaging

echo ""
echo "🔗 Comandos útiles:"
echo "   # Ver todos los pods"
echo "   kubectl get pods -A"
echo ""
echo "   # Logs de un servicio"
echo "   kubectl logs -f deployment/api-gateway -n carrillo-dev"
echo ""
echo "   # Port-forward para acceder al API Gateway"
echo "   kubectl port-forward svc/api-gateway 8080:8080 -n carrillo-dev"
echo ""
echo "   # Dashboard de Minikube"
echo "   minikube dashboard"
echo ""
echo "   # Validación completa"
echo "   ./scripts/validate-deployment.sh"

echo ""
if [ ${#FAILED_SERVICES[@]} -eq 0 ]; then
    show_success "¡Deployment completo exitoso! 🎉"
    echo ""
    echo "🌐 Para acceder al API Gateway:"
    echo "   kubectl port-forward svc/api-gateway 8080:8080 -n carrillo-dev"
    echo "   Luego visita: http://localhost:8080/actuator/health"
    exit 0
else
    show_warning "Deployment completado con advertencias."
    echo ""
    echo "Por favor, revisa los servicios fallidos antes de continuar."
    exit 1
fi
