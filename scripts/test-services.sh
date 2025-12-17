#!/bin/bash
# ============================================================================
# Script de Pruebas Funcionales - Carrillo Abogados
# ============================================================================
# Ejecuta pruebas funcionales contra los microservicios desplegados.
# Requiere que el API Gateway esté accesible via port-forward.
#
# Uso: ./scripts/test-services.sh [--port PORT] [--skip-port-forward]
#
# Opciones:
#   --port PORT           Puerto local para API Gateway (default: 8080)
#   --skip-port-forward   No iniciar port-forward automáticamente
# ============================================================================

set -e

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m'

# Configuration
NAMESPACE="carrillo-dev"
API_PORT=8080
SKIP_PORT_FORWARD=false
BASE_URL="http://localhost"
PORT_FORWARD_PID=""

# Contadores
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0
SKIPPED_TESTS=0

# Parsear argumentos
while [[ $# -gt 0 ]]; do
    case $1 in
        --port)
            API_PORT="$2"
            shift 2
            ;;
        --skip-port-forward)
            SKIP_PORT_FORWARD=true
            shift
            ;;
        *)
            shift
            ;;
    esac
done

BASE_URL="http://localhost:$API_PORT"

# Funciones de utilidad
print_header() {
    echo -e "\n${BLUE}════════════════════════════════════════════════════════════════${NC}"
    echo -e "${BOLD}${CYAN}  $1${NC}"
    echo -e "${BLUE}════════════════════════════════════════════════════════════════${NC}"
}

print_section() {
    echo -e "\n${YELLOW}▶ $1${NC}"
}

test_pass() {
    echo -e "  ${GREEN}✅ $1${NC}"
    ((PASSED_TESTS++))
    ((TOTAL_TESTS++))
}

test_fail() {
    echo -e "  ${RED}❌ $1${NC}"
    if [ -n "$2" ]; then
        echo -e "     ${RED}Error: $2${NC}"
    fi
    ((FAILED_TESTS++))
    ((TOTAL_TESTS++))
}

test_skip() {
    echo -e "  ${YELLOW}⏭️  $1${NC}"
    ((SKIPPED_TESTS++))
    ((TOTAL_TESTS++))
}

test_info() {
    echo -e "  ${CYAN}ℹ️  $1${NC}"
}

# Función para hacer requests HTTP
http_test() {
    local method=$1
    local endpoint=$2
    local expected_code=$3
    local description=$4
    local data=${5:-}
    
    local url="${BASE_URL}${endpoint}"
    local response
    local http_code
    
    if [ -n "$data" ]; then
        response=$(curl -s -w "\n%{http_code}" -X $method "$url" \
            -H "Content-Type: application/json" \
            -d "$data" \
            --max-time 10 2>/dev/null || echo -e "\n000")
    else
        response=$(curl -s -w "\n%{http_code}" -X $method "$url" \
            --max-time 10 2>/dev/null || echo -e "\n000")
    fi
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$http_code" = "$expected_code" ]; then
        test_pass "$description (HTTP $http_code)"
        return 0
    elif [ "$http_code" = "000" ]; then
        test_fail "$description" "No se pudo conectar al servicio"
        return 1
    else
        test_fail "$description" "Esperado HTTP $expected_code, recibido HTTP $http_code"
        return 1
    fi
}

# Función para cleanup
cleanup() {
    if [ -n "$PORT_FORWARD_PID" ]; then
        echo -e "\n${YELLOW}Cerrando port-forward...${NC}"
        kill $PORT_FORWARD_PID 2>/dev/null || true
        wait $PORT_FORWARD_PID 2>/dev/null || true
    fi
}

trap cleanup EXIT

# ============================================================================
# INICIO DE PRUEBAS
# ============================================================================

print_header "🧪 PRUEBAS FUNCIONALES - CARRILLO ABOGADOS"
echo -e "${CYAN}Fecha: $(date '+%Y-%m-%d %H:%M:%S')${NC}"
echo -e "${CYAN}Base URL: ${BASE_URL}${NC}"

# ============================================================================
# 1. CONFIGURAR PORT-FORWARD
# ============================================================================

if [ "$SKIP_PORT_FORWARD" = false ]; then
    print_section "1. Configurando acceso al API Gateway"
    
    # Buscar servicio del API Gateway
    API_GW_SVC=$(kubectl get svc -n $NAMESPACE -o name 2>/dev/null | grep "api-gateway" | head -1)
    
    if [ -z "$API_GW_SVC" ]; then
        echo -e "${RED}❌ No se encontró el servicio API Gateway${NC}"
        echo -e "${YELLOW}Ejecute primero: ./scripts/wsl-deploy.sh${NC}"
        exit 1
    fi
    
    SVC_NAME=$(echo $API_GW_SVC | cut -d'/' -f2)
    echo -e "  Servicio encontrado: ${BOLD}$SVC_NAME${NC}"
    
    # Verificar si ya hay algo en el puerto
    if lsof -i:$API_PORT &>/dev/null || netstat -tuln 2>/dev/null | grep -q ":$API_PORT "; then
        echo -e "${YELLOW}  Puerto $API_PORT ya está en uso, intentando conectar...${NC}"
    else
        echo -e "  Iniciando port-forward en puerto $API_PORT..."
        kubectl port-forward $API_GW_SVC $API_PORT:8080 -n $NAMESPACE &>/dev/null &
        PORT_FORWARD_PID=$!
        sleep 3
        
        # Verificar que el port-forward está funcionando
        if ! kill -0 $PORT_FORWARD_PID 2>/dev/null; then
            echo -e "${RED}❌ Port-forward falló${NC}"
            exit 1
        fi
    fi
    
    test_pass "Port-forward configurado en localhost:$API_PORT"
else
    print_section "1. Usando conexión existente"
    test_info "Skip port-forward (--skip-port-forward)"
fi

# Esperar a que el servicio esté listo
echo -e "  Esperando que el API Gateway responda..."
RETRY=0
MAX_RETRY=10
while [ $RETRY -lt $MAX_RETRY ]; do
    if curl -s --max-time 2 "$BASE_URL/actuator/health" &>/dev/null; then
        break
    fi
    ((RETRY++))
    sleep 2
done

if [ $RETRY -eq $MAX_RETRY ]; then
    echo -e "${RED}❌ API Gateway no responde después de $MAX_RETRY intentos${NC}"
    echo -e "${YELLOW}Verifique que los pods estén running: kubectl get pods -n $NAMESPACE${NC}"
    exit 1
fi

echo -e "  ${GREEN}✓ API Gateway respondiendo${NC}"

# ============================================================================
# 2. HEALTH CHECKS - API GATEWAY
# ============================================================================

print_section "2. Health Checks - API Gateway"

http_test GET "/actuator/health" "200" "Health endpoint principal"
http_test GET "/actuator/health/liveness" "200" "Liveness probe"
http_test GET "/actuator/health/readiness" "200" "Readiness probe"
http_test GET "/actuator/info" "200" "Info endpoint"

# ============================================================================
# 3. HEALTH CHECKS - MICROSERVICIOS (via Gateway)
# ============================================================================

print_section "3. Health Checks - Microservicios (via Gateway)"

# Lista de servicios y sus paths
declare -A SERVICES=(
    ["client-service"]="/client-service/actuator/health"
    ["case-service"]="/case-service/actuator/health"
    ["user-service"]="/user-service/actuator/health"
    ["document-service"]="/document-service/actuator/health"
    ["calendar-service"]="/calendar-service/actuator/health"
    ["notification-service"]="/notification-service/actuator/health"
    ["n8n-integration-service"]="/n8n-integration-service/actuator/health"
)

for service in "${!SERVICES[@]}"; do
    endpoint="${SERVICES[$service]}"
    
    # Verificar si el servicio está desplegado
    if kubectl get deployment -n $NAMESPACE 2>/dev/null | grep -q "$service"; then
        http_test GET "$endpoint" "200" "$service health check" || true
    else
        test_skip "$service no desplegado"
    fi
done

# ============================================================================
# 4. PRUEBAS DE ENDPOINTS API
# ============================================================================

print_section "4. Pruebas de Endpoints API"

echo -e "\n  ${BOLD}Client Service:${NC}"
http_test GET "/client-service/api/clients" "200" "Listar clientes" || \
http_test GET "/client-service/api/clients" "401" "Listar clientes (requiere auth)" || \
test_skip "Client API no accesible"

echo -e "\n  ${BOLD}Case Service:${NC}"
http_test GET "/case-service/api/cases" "200" "Listar casos" || \
http_test GET "/case-service/api/cases" "401" "Listar casos (requiere auth)" || \
test_skip "Case API no accesible"

echo -e "\n  ${BOLD}User Service:${NC}"
http_test GET "/user-service/api/users" "200" "Listar usuarios" || \
http_test GET "/user-service/api/users" "401" "Listar usuarios (requiere auth)" || \
test_skip "User API no accesible"

# ============================================================================
# 5. PRUEBAS DE CONECTIVIDAD INTERNA
# ============================================================================

print_section "5. Pruebas de Conectividad Interna"

# Verificar que los servicios pueden comunicarse entre sí
echo -e "  ${CYAN}Verificando DNS interno de Kubernetes...${NC}"

# Test de resolución DNS desde un pod
CLIENT_POD=$(kubectl get pods -n $NAMESPACE -l app.kubernetes.io/name=client-service -o jsonpath='{.items[0].metadata.name}' 2>/dev/null || echo "")

if [ -n "$CLIENT_POD" ]; then
    # Verificar que puede resolver el servicio de PostgreSQL
    if kubectl exec -n $NAMESPACE $CLIENT_POD -- nslookup postgresql.databases.svc.cluster.local &>/dev/null 2>&1; then
        test_pass "DNS: Resolución de PostgreSQL desde client-service"
    else
        test_skip "No se pudo verificar DNS (nslookup no disponible en el contenedor)"
    fi
    
    # Verificar que puede resolver NATS
    if kubectl exec -n $NAMESPACE $CLIENT_POD -- nslookup nats.messaging.svc.cluster.local &>/dev/null 2>&1; then
        test_pass "DNS: Resolución de NATS desde client-service"
    else
        test_skip "No se pudo verificar DNS NATS"
    fi
else
    test_skip "No hay pod de client-service para pruebas de conectividad"
fi

# ============================================================================
# 6. PRUEBAS DE BASE DE DATOS
# ============================================================================

print_section "6. Pruebas de Base de Datos"

PG_POD=$(kubectl get pods -n databases -l app.kubernetes.io/name=postgresql -o jsonpath='{.items[0].metadata.name}' 2>/dev/null || echo "")

if [ -n "$PG_POD" ]; then
    # Verificar que PostgreSQL acepta conexiones
    if kubectl exec -n databases $PG_POD -- pg_isready -U carrillo &>/dev/null; then
        test_pass "PostgreSQL acepta conexiones"
    else
        test_fail "PostgreSQL no acepta conexiones"
    fi
    
    # Verificar que la base de datos existe
    if kubectl exec -n databases $PG_POD -- psql -U carrillo -d carrillo_legal_tech -c "SELECT 1" &>/dev/null; then
        test_pass "Base de datos carrillo_legal_tech accesible"
    else
        test_fail "Base de datos carrillo_legal_tech no accesible"
    fi
    
    # Listar schemas
    SCHEMAS=$(kubectl exec -n databases $PG_POD -- psql -U carrillo -d carrillo_legal_tech -t -c "SELECT schema_name FROM information_schema.schemata WHERE schema_name NOT IN ('pg_catalog', 'information_schema', 'pg_toast');" 2>/dev/null | tr -d ' ' | grep -v '^$' || echo "")
    if [ -n "$SCHEMAS" ]; then
        test_pass "Schemas encontrados: $(echo $SCHEMAS | tr '\n' ', ')"
    else
        test_info "No hay schemas de aplicación creados aún"
    fi
else
    test_skip "PostgreSQL pod no encontrado"
fi

# ============================================================================
# 7. PRUEBAS DE NATS (Messaging)
# ============================================================================

print_section "7. Pruebas de NATS (Messaging)"

NATS_POD=$(kubectl get pods -n messaging -l app.kubernetes.io/name=nats -o jsonpath='{.items[0].metadata.name}' 2>/dev/null || echo "")

if [ -n "$NATS_POD" ]; then
    # Verificar que NATS está corriendo
    NATS_READY=$(kubectl get pod $NATS_POD -n messaging -o jsonpath='{.status.conditions[?(@.type=="Ready")].status}' 2>/dev/null)
    if [ "$NATS_READY" = "True" ]; then
        test_pass "NATS servidor está ready"
    else
        test_fail "NATS servidor no está ready"
    fi
    
    # Intentar publicar un mensaje de prueba (si nats-box está disponible)
    NATS_BOX=$(kubectl get pods -n messaging -l app.kubernetes.io/name=nats-box -o jsonpath='{.items[0].metadata.name}' 2>/dev/null || echo "")
    if [ -n "$NATS_BOX" ]; then
        if kubectl exec -n messaging $NATS_BOX -- nats pub test.health "ping" &>/dev/null; then
            test_pass "NATS pub/sub funcionando"
        else
            test_fail "NATS pub/sub no funciona"
        fi
    else
        test_skip "NATS Box no disponible para pruebas de pub/sub"
    fi
else
    test_skip "NATS pod no encontrado"
fi

# ============================================================================
# 8. PRUEBAS DE GATEWAY ROUTES
# ============================================================================

print_section "8. Pruebas de Gateway Routes"

# Verificar que el gateway tiene las rutas configuradas
if curl -s "$BASE_URL/actuator/gateway/routes" &>/dev/null; then
    ROUTES=$(curl -s "$BASE_URL/actuator/gateway/routes" 2>/dev/null)
    
    # Verificar rutas principales
    for route in "CLIENT-SERVICE" "CASE-SERVICE" "USER-SERVICE"; do
        if echo "$ROUTES" | grep -q "$route"; then
            test_pass "Ruta $route configurada"
        else
            test_fail "Ruta $route no encontrada"
        fi
    done
else
    test_skip "Endpoint de rutas del gateway no accesible"
fi

# ============================================================================
# RESUMEN FINAL
# ============================================================================

print_header "📊 RESUMEN DE PRUEBAS FUNCIONALES"

echo -e "\n  Total de pruebas: ${BOLD}$TOTAL_TESTS${NC}"
echo -e "  ${GREEN}✅ Pasadas: $PASSED_TESTS${NC}"
echo -e "  ${RED}❌ Fallidas: $FAILED_TESTS${NC}"
echo -e "  ${YELLOW}⏭️  Omitidas: $SKIPPED_TESTS${NC}"

# Calcular porcentaje (excluyendo omitidas)
EXECUTED=$((TOTAL_TESTS - SKIPPED_TESTS))
if [ $EXECUTED -gt 0 ]; then
    PASS_PERCENT=$((PASSED_TESTS * 100 / EXECUTED))
    echo -e "\n  Porcentaje de éxito: ${BOLD}${PASS_PERCENT}%${NC} (de pruebas ejecutadas)"
fi

echo ""
if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "${GREEN}${BOLD}🎉 ¡TODAS LAS PRUEBAS PASARON!${NC}"
    echo -e "${GREEN}El sistema está funcionando correctamente.${NC}"
    EXIT_CODE=0
else
    echo -e "${RED}${BOLD}⚠️  HAY PRUEBAS FALLIDAS${NC}"
    echo -e "${RED}Revise los errores anteriores para más detalles.${NC}"
    EXIT_CODE=1
fi

echo ""
echo -e "${CYAN}📋 Endpoints disponibles:${NC}"
echo -e "  • API Gateway:        ${BOLD}${BASE_URL}${NC}"
echo -e "  • Health Check:       ${BOLD}${BASE_URL}/actuator/health${NC}"
echo -e "  • Client Service:     ${BOLD}${BASE_URL}/client-service/api/clients${NC}"
echo -e "  • Case Service:       ${BOLD}${BASE_URL}/case-service/api/cases${NC}"

exit $EXIT_CODE
