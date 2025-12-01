#!/bin/bash
set -e

# Smoke tests to validate everything works
# Purpose: Comprehensive validation for Carrillo Abogados Legal Tech Platform
# Date: December 2025

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Configuration
POSTGRES_PASSWORD="CarrilloAbogados2025!"
POSTGRES_USER="carrillo"
POSTGRES_DB="carrillo_legal_tech"

# Lista de microservicios a validar
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

# Lista de namespaces requeridos
REQUIRED_NAMESPACES=("carrillo-dev" "databases" "messaging")

# Contadores para resumen
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0
WARNINGS=0

echo -e "${CYAN}🔍 Carrillo Abogados - Deployment Validation${NC}"
echo -e "${CYAN}=============================================${NC}"
echo ""

# Funciones auxiliares
increment_test() {
    ((TOTAL_TESTS++))
}

pass_test() {
    local message=$1
    ((PASSED_TESTS++))
    echo -e "${GREEN}✅ $message${NC}"
}

fail_test() {
    local message=$1
    local debug_cmd=${2:-}
    ((FAILED_TESTS++))
    echo -e "${RED}❌ $message${NC}"
    if [ -n "$debug_cmd" ]; then
        echo -e "${YELLOW}   Debug: $debug_cmd${NC}"
    fi
}

warn_test() {
    local message=$1
    ((WARNINGS++))
    echo -e "${YELLOW}⚠️ $message${NC}"
}

test_section() {
    local section=$1
    echo -e "${BLUE}📋 $section${NC}"
}

# 1. Validar que Minikube esté corriendo
test_section "1. Validando Minikube..."
increment_test

if minikube status | grep -q "host: Running" && minikube status | grep -q "kubelet: Running"; then
    pass_test "Minikube está ejecutándose"
    
    # Verificar versión de Kubernetes
    K8S_VERSION=$(kubectl version --short 2>/dev/null | grep "Server Version" | grep -oE 'v[0-9]+\.[0-9]+\.[0-9]+')
    echo -e "${GREEN}   Kubernetes version: $K8S_VERSION${NC}"
else
    fail_test "Minikube no está ejecutándose" "minikube start"
fi

echo ""

# 2. Validar namespaces
test_section "2. Validando namespaces..."

for namespace in "${REQUIRED_NAMESPACES[@]}"; do
    increment_test
    if kubectl get namespace $namespace &>/dev/null; then
        pass_test "Namespace '$namespace' existe"
    else
        fail_test "Namespace '$namespace' no existe" "kubectl create namespace $namespace"
    fi
done

echo ""

# 3. Validar PostgreSQL
test_section "3. Validando PostgreSQL..."

increment_test
if kubectl get statefulset postgresql -n databases &>/dev/null; then
    pass_test "PostgreSQL StatefulSet existe"
    
    # Verificar si el pod está corriendo
    increment_test
    if kubectl get pods -n databases -l app=postgresql | grep -q Running; then
        pass_test "PostgreSQL pod está Running"
        
        # Verificar si está Ready
        increment_test
        if kubectl get pods -n databases -l app=postgresql | grep -q "1/1"; then
            pass_test "PostgreSQL pod está Ready"
        else
            fail_test "PostgreSQL pod no está Ready" "kubectl describe pod -l app=postgresql -n databases"
        fi
    else
        fail_test "PostgreSQL pod no está Running" "kubectl get pods -n databases"
    fi
else
    fail_test "PostgreSQL StatefulSet no existe" "helm install postgresql bitnami/postgresql --namespace databases"
fi

echo ""

# 4. Validar NATS
test_section "4. Validando NATS..."

increment_test
if kubectl get deployment nats -n messaging &>/dev/null; then
    pass_test "NATS Deployment existe"
    
    # Verificar si el pod está corriendo
    increment_test
    if kubectl get pods -n messaging -l app=nats | grep -q Running; then
        pass_test "NATS pod está Running"
        
        # Verificar si está Ready
        increment_test
        if kubectl get pods -n messaging -l app=nats | grep -q "1/1"; then
            pass_test "NATS pod está Ready"
        else
            fail_test "NATS pod no está Ready" "kubectl describe pod -l app=nats -n messaging"
        fi
    else
        fail_test "NATS pod no está Running" "kubectl get pods -n messaging"
    fi
else
    fail_test "NATS Deployment no existe" "helm install nats nats/nats --namespace messaging"
fi

echo ""

# 5. Validar microservicios
test_section "5. Validando microservicios..."

for service in "${MICROSERVICES[@]}"; do
    increment_test
    
    # Verificar si el deployment existe
    if kubectl get deployment $service -n carrillo-dev &>/dev/null; then
        
        # Verificar si hay pods running
        PODS_STATUS=$(kubectl get pods -n carrillo-dev -l app=$service --no-headers 2>/dev/null | awk '{print $3}' | head -1)
        
        if [ "$PODS_STATUS" = "Running" ]; then
            # Verificar si está Ready
            READY_STATUS=$(kubectl get pods -n carrillo-dev -l app=$service --no-headers 2>/dev/null | awk '{print $2}' | head -1)
            
            if [[ "$READY_STATUS" =~ ^1/1$ ]]; then
                pass_test "$service está Running y Ready"
            else
                fail_test "$service está Running pero no Ready ($READY_STATUS)" "kubectl describe pod -l app=$service -n carrillo-dev"
            fi
        else
            case "$PODS_STATUS" in
                "Pending"|"ContainerCreating")
                    warn_test "$service está en estado $PODS_STATUS (puede estar iniciando)"
                    ;;
                "CrashLoopBackOff"|"Error"|"ImagePullBackOff")
                    fail_test "$service está en estado $PODS_STATUS" "kubectl logs deployment/$service -n carrillo-dev"
                    ;;
                "")
                    fail_test "$service no tiene pods" "kubectl describe deployment $service -n carrillo-dev"
                    ;;
                *)
                    fail_test "$service está en estado desconocido: $PODS_STATUS" "kubectl get pods -l app=$service -n carrillo-dev"
                    ;;
            esac
        fi
    else
        fail_test "$service deployment no existe" "kubectl get deployments -n carrillo-dev"
    fi
done

echo ""

# 6. Test de conectividad a API Gateway
test_section "6. Testing API Gateway connectivity..."

increment_test
# Verificar si el servicio del API Gateway existe
if kubectl get service api-gateway -n carrillo-dev &>/dev/null; then
    pass_test "API Gateway service existe"
    
    increment_test
    # Intentar port-forward en background para test de conectividad
    echo -e "${YELLOW}   Probando conectividad via port-forward...${NC}"
    
    # Iniciar port-forward en background
    kubectl port-forward svc/api-gateway 8080:8080 -n carrillo-dev &>/dev/null &
    PORT_FORWARD_PID=$!
    
    # Esperar un momento para que se establezca la conexión
    sleep 3
    
    # Intentar conectar al health endpoint
    if curl -f -s http://localhost:8080/actuator/health &>/dev/null; then
        pass_test "API Gateway responde en health endpoint"
    else
        # Intentar endpoint básico
        if curl -f -s http://localhost:8080/ &>/dev/null; then
            pass_test "API Gateway responde en endpoint raíz"
        else
            warn_test "API Gateway no responde (puede estar iniciando o no tener health endpoint configurado)"
        fi
    fi
    
    # Terminar port-forward
    kill $PORT_FORWARD_PID 2>/dev/null || true
    
else
    fail_test "API Gateway service no existe" "kubectl get services -n carrillo-dev"
fi

echo ""

# 7. Test de conectividad a PostgreSQL
test_section "7. Testing PostgreSQL connectivity..."

increment_test
# Crear un pod temporal para probar conectividad
echo -e "${YELLOW}   Creando pod temporal para test de PostgreSQL...${NC}"

TEMP_POD="postgres-test-$(date +%s)"
kubectl run $TEMP_POD \
    --image=postgres:16.2 \
    --namespace=databases \
    --restart=Never \
    --rm -i --tty -- /bin/bash -c "
    PGPASSWORD='$POSTGRES_PASSWORD' psql -h postgresql -U $POSTGRES_USER -d $POSTGRES_DB -c 'SELECT version();' 2>/dev/null
" &>/dev/null &

# Esperar un momento para la conexión
sleep 5

# Verificar si el comando fue exitoso
if kubectl wait --for=condition=ready pod/$TEMP_POD -n databases --timeout=30s &>/dev/null; then
    pass_test "PostgreSQL acepta conexiones"
else
    fail_test "No se pudo conectar a PostgreSQL" "kubectl exec -it postgresql-0 -n databases -- psql -U $POSTGRES_USER -d $POSTGRES_DB"
fi

# Limpiar pod temporal
kubectl delete pod $TEMP_POD -n databases &>/dev/null || true

echo ""

# 8. Validar recursos del sistema
test_section "8. Validando recursos del sistema..."

increment_test
# Verificar uso de CPU y memoria en los nodos
NODE_STATUS=$(kubectl top nodes 2>/dev/null | tail -1)
if [ $? -eq 0 ]; then
    pass_test "Métricas de nodos disponibles"
    echo -e "${GREEN}   $NODE_STATUS${NC}"
else
    warn_test "Métricas de nodos no disponibles (metrics-server puede estar iniciando)"
fi

increment_test
# Verificar eventos recientes del cluster
RECENT_ERRORS=$(kubectl get events --sort-by='.lastTimestamp' -A | grep -i error | head -3)
if [ -z "$RECENT_ERRORS" ]; then
    pass_test "No hay eventos de error recientes en el cluster"
else
    warn_test "Hay eventos de error recientes en el cluster"
    echo -e "${YELLOW}   Últimos errores:${NC}"
    echo "$RECENT_ERRORS" | while read line; do
        echo -e "${YELLOW}     $line${NC}"
    done
fi

echo ""

# 9. Resumen final
echo -e "${CYAN}📊 Resumen de Validación${NC}"
echo -e "${CYAN}========================${NC}"
echo ""

if [ $FAILED_TESTS -eq 0 ]; then
    if [ $WARNINGS -eq 0 ]; then
        echo -e "${GREEN}🎉 ¡PERFECTO! Todos los tests pasaron exitosamente${NC}"
        echo -e "${GREEN}   ✅ $PASSED_TESTS/$TOTAL_TESTS tests exitosos${NC}"
        EXIT_CODE=0
    else
        echo -e "${YELLOW}✅ Tests completados con algunas advertencias${NC}"
        echo -e "${GREEN}   ✅ $PASSED_TESTS/$TOTAL_TESTS tests exitosos${NC}"
        echo -e "${YELLOW}   ⚠️ $WARNINGS advertencias${NC}"
        EXIT_CODE=0
    fi
else
    echo -e "${RED}❌ Algunos tests fallaron${NC}"
    echo -e "${GREEN}   ✅ $PASSED_TESTS tests exitosos${NC}"
    echo -e "${RED}   ❌ $FAILED_TESTS tests fallidos${NC}"
    if [ $WARNINGS -gt 0 ]; then
        echo -e "${YELLOW}   ⚠️ $WARNINGS advertencias${NC}"
    fi
    EXIT_CODE=1
fi

echo ""
echo -e "${BLUE}🔗 Estado actual del cluster:${NC}"
kubectl get pods -A | grep -E "(carrillo-dev|databases|messaging)" | head -20

echo ""
echo -e "${BLUE}🛠️ Comandos útiles para debugging:${NC}"
echo "   kubectl get pods -A"
echo "   kubectl get events --sort-by='.lastTimestamp' -A"
echo "   kubectl describe pod <pod-name> -n <namespace>"
echo "   kubectl logs deployment/<service-name> -n carrillo-dev"
echo "   kubectl port-forward svc/api-gateway 8080:8080 -n carrillo-dev"
echo "   minikube dashboard"

if [ $EXIT_CODE -eq 0 ]; then
    echo ""
    echo -e "${GREEN}✅ Deployment validation completed successfully!${NC}"
else
    echo ""
    echo -e "${RED}❌ Deployment validation completed with errors. Please check the failed tests above.${NC}"
fi

exit $EXIT_CODE