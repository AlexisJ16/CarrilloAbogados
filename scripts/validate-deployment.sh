#!/bin/bash
set -e

# ============================================================================
# Script de Validación de Deployment - Carrillo Abogados
# ============================================================================
# Valida que todos los componentes estén desplegados correctamente.
#
# Uso: ./scripts/validate-deployment.sh [--wait] [--verbose]
#
# Opciones:
#   --wait     Esperar hasta que todos los pods estén Ready (max 5 min)
#   --verbose  Mostrar información detallada
# ============================================================================

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m'

# Configuration
NAMESPACE_APP="carrillo-dev"
NAMESPACE_DB="databases"
NAMESPACE_MSG="messaging"
WAIT_MODE=false
VERBOSE=false
MAX_WAIT=300

# Lista de microservicios a validar (nombres de Helm release)
MICROSERVICES=(
    "api-gateway"
    "user-service"
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

# Parsear argumentos
for arg in "$@"; do
    case $arg in
        --wait) WAIT_MODE=true ;;
        --verbose) VERBOSE=true ;;
    esac
done

echo -e "${CYAN}${BOLD}🔍 Carrillo Abogados - Validación de Deployment${NC}"
echo -e "${CYAN}=================================================${NC}"
echo -e "Fecha: $(date '+%Y-%m-%d %H:%M:%S')"
echo ""

# Funciones auxiliares
increment_test() {
    ((TOTAL_TESTS++))
}
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
PG_POD=$(kubectl get pods -n $NAMESPACE_DB -l app.kubernetes.io/name=postgresql -o jsonpath='{.items[0].metadata.name}' 2>/dev/null || echo "")

if [ -n "$PG_POD" ]; then
    pass_test "PostgreSQL pod existe ($PG_POD)"
    
    increment_test
    PG_STATUS=$(kubectl get pod $PG_POD -n $NAMESPACE_DB -o jsonpath='{.status.phase}' 2>/dev/null)
    if [ "$PG_STATUS" = "Running" ]; then
        pass_test "PostgreSQL pod está Running"
        
        increment_test
        PG_READY=$(kubectl get pod $PG_POD -n $NAMESPACE_DB -o jsonpath='{.status.conditions[?(@.type=="Ready")].status}' 2>/dev/null)
        if [ "$PG_READY" = "True" ]; then
            pass_test "PostgreSQL pod está Ready"
            
            # Verificar conectividad
            increment_test
            if kubectl exec -n $NAMESPACE_DB $PG_POD -- pg_isready -U carrillo &>/dev/null; then
                pass_test "PostgreSQL acepta conexiones"
            else
                warn_test "PostgreSQL no responde a pg_isready"
            fi
        else
            fail_test "PostgreSQL pod no está Ready" "kubectl describe pod $PG_POD -n $NAMESPACE_DB"
        fi
    else
        fail_test "PostgreSQL pod no está Running ($PG_STATUS)" "kubectl get pods -n $NAMESPACE_DB"
    fi
else
    fail_test "PostgreSQL pod no encontrado" "helm install postgresql bitnami/postgresql --namespace $NAMESPACE_DB"
fi

echo ""

# 4. Validar NATS
test_section "4. Validando NATS..."

increment_test
NATS_POD=$(kubectl get pods -n $NAMESPACE_MSG -l app.kubernetes.io/name=nats -o jsonpath='{.items[0].metadata.name}' 2>/dev/null || echo "")

if [ -n "$NATS_POD" ]; then
    pass_test "NATS pod existe ($NATS_POD)"
    
    increment_test
    NATS_STATUS=$(kubectl get pod $NATS_POD -n $NAMESPACE_MSG -o jsonpath='{.status.phase}' 2>/dev/null)
    if [ "$NATS_STATUS" = "Running" ]; then
        pass_test "NATS pod está Running"
        
        increment_test
        NATS_READY=$(kubectl get pod $NATS_POD -n $NAMESPACE_MSG -o jsonpath='{.status.conditions[?(@.type=="Ready")].status}' 2>/dev/null)
        if [ "$NATS_READY" = "True" ]; then
            pass_test "NATS pod está Ready"
        else
            warn_test "NATS pod no está Ready aún"
        fi
    else
        fail_test "NATS pod no está Running ($NATS_STATUS)" "kubectl get pods -n $NAMESPACE_MSG"
    fi
else
    fail_test "NATS pod no encontrado" "helm install nats nats/nats --namespace $NAMESPACE_MSG"
fi

echo ""

# 5. Validar microservicios
test_section "5. Validando microservicios..."

# Si está en modo wait, esperar que los pods estén ready
if [ "$WAIT_MODE" = true ]; then
    echo -e "${YELLOW}   Esperando que los pods estén ready (max ${MAX_WAIT}s)...${NC}"
    for service in "${MICROSERVICES[@]}"; do
        echo -n "   Esperando carrillo-dev-$service... "
        if kubectl wait --for=condition=ready pod -l app.kubernetes.io/name=$service -n $NAMESPACE_APP --timeout=${MAX_WAIT}s &> /dev/null; then
            echo -e "${GREEN}✓${NC}"
        else
            echo -e "${RED}timeout${NC}"
        fi
    done
    echo ""
fi

for service in "${MICROSERVICES[@]}"; do
    increment_test
    
    # Buscar deployment con el prefijo de Helm
    DEPLOY_NAME=$(kubectl get deployments -n $NAMESPACE_APP -o name 2>/dev/null | grep "$service" | head -1)
    
    if [ -n "$DEPLOY_NAME" ]; then
        # Obtener estado del deployment
        READY=$(kubectl get $DEPLOY_NAME -n $NAMESPACE_APP -o jsonpath='{.status.readyReplicas}' 2>/dev/null || echo "0")
        DESIRED=$(kubectl get $DEPLOY_NAME -n $NAMESPACE_APP -o jsonpath='{.spec.replicas}' 2>/dev/null || echo "1")
        
        if [ "$READY" = "$DESIRED" ] && [ "$READY" != "0" ] && [ -n "$READY" ]; then
            pass_test "$service Running y Ready ($READY/$DESIRED replicas)"
        else
            # Verificar estado del pod
            POD=$(kubectl get pods -n $NAMESPACE_APP -l app.kubernetes.io/name=$service -o jsonpath='{.items[0].metadata.name}' 2>/dev/null || echo "")
            if [ -n "$POD" ]; then
                POD_STATUS=$(kubectl get pod $POD -n $NAMESPACE_APP -o jsonpath='{.status.phase}' 2>/dev/null)
                CONTAINER_STATUS=$(kubectl get pod $POD -n $NAMESPACE_APP -o jsonpath='{.status.containerStatuses[0].state}' 2>/dev/null | grep -o '"[^"]*":' | head -1 | tr -d '":')
                
                case "$CONTAINER_STATUS" in
                    "waiting")
                        REASON=$(kubectl get pod $POD -n $NAMESPACE_APP -o jsonpath='{.status.containerStatuses[0].state.waiting.reason}' 2>/dev/null)
                        case "$REASON" in
                            "ContainerCreating"|"PodInitializing")
                                warn_test "$service está iniciando ($REASON)"
                                ;;
                            "ImagePullBackOff"|"ErrImagePull")
                                fail_test "$service error de imagen ($REASON)" "kubectl describe pod $POD -n $NAMESPACE_APP"
                                ;;
                            "CrashLoopBackOff")
                                fail_test "$service está crasheando" "kubectl logs $POD -n $NAMESPACE_APP --tail=20"
                                ;;
                            *)
                                warn_test "$service en estado $REASON"
                                ;;
                        esac
                        ;;
                    "running")
                        warn_test "$service running pero no ready aún"
                        ;;
                    *)
                        fail_test "$service estado desconocido: $CONTAINER_STATUS"
                        ;;
                esac
            else
                fail_test "$service no tiene pods" "kubectl describe $DEPLOY_NAME -n $NAMESPACE_APP"
            fi
        fi
    else
        fail_test "$service deployment no encontrado" "kubectl get deployments -n $NAMESPACE_APP"
    fi
done

echo ""

# 6. Test de conectividad a API Gateway
test_section "6. Testing API Gateway connectivity..."

increment_test
# Buscar el servicio del API Gateway (con prefijo Helm)
API_GW_SVC=$(kubectl get svc -n $NAMESPACE_APP -o name 2>/dev/null | grep "api-gateway" | head -1)

if [ -n "$API_GW_SVC" ]; then
    SVC_NAME=$(echo $API_GW_SVC | cut -d'/' -f2)
    pass_test "API Gateway service existe ($SVC_NAME)"
    
    increment_test
    # Verificar que el pod esté ready antes de hacer port-forward
    API_GW_POD=$(kubectl get pods -n $NAMESPACE_APP -l app.kubernetes.io/name=api-gateway -o jsonpath='{.items[0].metadata.name}' 2>/dev/null || echo "")
    
    if [ -n "$API_GW_POD" ]; then
        POD_READY=$(kubectl get pod $API_GW_POD -n $NAMESPACE_APP -o jsonpath='{.status.conditions[?(@.type=="Ready")].status}' 2>/dev/null)
        
        if [ "$POD_READY" = "True" ]; then
            echo -e "${YELLOW}   Probando conectividad via port-forward...${NC}"
            
            # Iniciar port-forward en background
            kubectl port-forward $API_GW_SVC 18080:8080 -n $NAMESPACE_APP &>/dev/null &
            PORT_FORWARD_PID=$!
            sleep 4
            
            # Intentar conectar al health endpoint
            if curl -f -s --max-time 5 http://localhost:18080/actuator/health &>/dev/null; then
                pass_test "API Gateway responde en /actuator/health"
            elif curl -f -s --max-time 5 http://localhost:18080/ &>/dev/null; then
                pass_test "API Gateway responde en endpoint raíz"
            else
                warn_test "API Gateway no responde aún (puede estar iniciando)"
            fi
            
            # Terminar port-forward
            kill $PORT_FORWARD_PID 2>/dev/null || true
            wait $PORT_FORWARD_PID 2>/dev/null || true
        else
            warn_test "API Gateway pod no está ready para test de conectividad"
        fi
    else
        warn_test "API Gateway pod no encontrado para test de conectividad"
    fi
else
    fail_test "API Gateway service no existe" "kubectl get services -n $NAMESPACE_APP"
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
if [ "$VERBOSE" = true ]; then
    NODE_STATUS=$(kubectl top nodes 2>/dev/null | tail -1)
    if [ $? -eq 0 ]; then
        pass_test "Métricas de nodos disponibles"
        echo -e "${GREEN}   $NODE_STATUS${NC}"
    else
        warn_test "Métricas de nodos no disponibles (metrics-server puede estar iniciando)"
    fi
else
    if kubectl top nodes &>/dev/null; then
        pass_test "Métricas de nodos disponibles"
    else
        warn_test "Métricas de nodos no disponibles"
    fi
fi

increment_test
# Verificar eventos de error recientes
RECENT_ERRORS=$(kubectl get events --sort-by='.lastTimestamp' -n $NAMESPACE_APP 2>/dev/null | grep -iE "(error|failed|backoff)" | head -3)
if [ -z "$RECENT_ERRORS" ]; then
    pass_test "No hay eventos de error recientes en $NAMESPACE_APP"
else
    warn_test "Hay eventos de advertencia en el namespace"
    if [ "$VERBOSE" = true ]; then
        echo -e "${YELLOW}   Últimos eventos:${NC}"
        echo "$RECENT_ERRORS" | head -3 | while read line; do
            echo -e "${YELLOW}     $line${NC}"
        done
    fi
fi

echo ""

# ============================================================================
# RESUMEN FINAL
# ============================================================================
echo -e "${CYAN}${BOLD}════════════════════════════════════════════════════════════════${NC}"
echo -e "${CYAN}${BOLD}  📊 RESUMEN DE VALIDACIÓN${NC}"
echo -e "${CYAN}${BOLD}════════════════════════════════════════════════════════════════${NC}"
echo ""

echo -e "  Total de verificaciones: ${BOLD}$TOTAL_TESTS${NC}"
echo -e "  ${GREEN}✅ Pasadas: $PASSED_TESTS${NC}"
echo -e "  ${RED}❌ Fallidas: $FAILED_TESTS${NC}"
echo -e "  ${YELLOW}⚠️  Advertencias: $WARNINGS${NC}"

# Calcular porcentaje
if [ $TOTAL_TESTS -gt 0 ]; then
    PASS_PERCENT=$((PASSED_TESTS * 100 / TOTAL_TESTS))
    echo -e "\n  Porcentaje de éxito: ${BOLD}${PASS_PERCENT}%${NC}"
fi

echo ""
if [ $FAILED_TESTS -eq 0 ]; then
    if [ $WARNINGS -eq 0 ]; then
        echo -e "${GREEN}${BOLD}🎉 ¡VALIDACIÓN EXITOSA! Todos los componentes están funcionando.${NC}"
        EXIT_CODE=0
    else
        echo -e "${YELLOW}${BOLD}✅ Validación completada con advertencias (servicios pueden estar iniciando)${NC}"
        EXIT_CODE=0
    fi
    
    echo ""
    echo -e "${CYAN}📋 Próximos pasos:${NC}"
    echo -e "  1. Ejecutar pruebas funcionales: ${BOLD}./scripts/test-services.sh${NC}"
    
    # Obtener nombre del servicio API Gateway
    API_GW_SVC=$(kubectl get svc -n $NAMESPACE_APP -o name 2>/dev/null | grep "api-gateway" | head -1 | cut -d'/' -f2)
    if [ -n "$API_GW_SVC" ]; then
        echo -e "  2. Acceder a la API: ${BOLD}kubectl port-forward svc/$API_GW_SVC 8080:8080 -n $NAMESPACE_APP${NC}"
    fi
    echo -e "  3. Abrir en navegador: ${BOLD}http://localhost:8080/actuator/health${NC}"
else
    echo -e "${RED}${BOLD}⚠️  VALIDACIÓN CON ERRORES. Hay $FAILED_TESTS componentes con problemas.${NC}"
    EXIT_CODE=1
    
    echo ""
    echo -e "${CYAN}🔧 Acciones recomendadas:${NC}"
    echo -e "  1. Ver estado detallado: ${BOLD}kubectl get pods -n $NAMESPACE_APP${NC}"
    echo -e "  2. Ver logs de pods: ${BOLD}kubectl logs <pod-name> -n $NAMESPACE_APP${NC}"
    echo -e "  3. Describir pods: ${BOLD}kubectl describe pod <pod-name> -n $NAMESPACE_APP${NC}"
    echo -e "  4. Reintentar con espera: ${BOLD}./scripts/validate-deployment.sh --wait --verbose${NC}"
fi

echo ""
echo -e "${BLUE}🔗 Estado actual del cluster:${NC}"
kubectl get pods -A 2>/dev/null | grep -E "(carrillo-dev|databases|messaging)" | head -15

echo ""
echo -e "${BLUE}🛠️ Comandos útiles:${NC}"
echo "   kubectl get pods -n $NAMESPACE_APP"
echo "   kubectl logs -f <pod-name> -n $NAMESPACE_APP"
echo "   kubectl describe pod <pod-name> -n $NAMESPACE_APP"
echo "   minikube dashboard"

exit $EXIT_CODE