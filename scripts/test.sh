#!/bin/bash
# ============================================================================
# TEST.SH - Tests Funcionales de Servicios
# ============================================================================
# Proyecto: Carrillo Abogados Legal Tech Platform
# Uso: ./scripts/test.sh
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
NAMESPACE="carrillo-dev"
GATEWAY_PORT=8080

# Contadores
PASSED=0
FAILED=0

pass() { echo -e "  ${GREEN}✓${NC} $1"; ((PASSED++)); }
fail() { echo -e "  ${RED}✗${NC} $1"; ((FAILED++)); }

cleanup() {
    if [ -n "$PORT_FORWARD_PID" ]; then
        kill $PORT_FORWARD_PID 2>/dev/null || true
    fi
}
trap cleanup EXIT

echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${CYAN}  🧪 Carrillo Abogados - Tests Funcionales${NC}"
echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

# ============================================================================
# 1. PORT-FORWARD AL GATEWAY
# ============================================================================
echo -e "${BLUE}[1/4] Configurando Port-Forward${NC}"

# Verificar si ya hay port-forward activo
if curl -s http://localhost:$GATEWAY_PORT/actuator/health &>/dev/null; then
    pass "Port-forward ya activo en puerto $GATEWAY_PORT"
else
    kubectl port-forward svc/carrillo-dev-api-gateway $GATEWAY_PORT:8080 -n $NAMESPACE &>/dev/null &
    PORT_FORWARD_PID=$!
    sleep 3
    
    if curl -s http://localhost:$GATEWAY_PORT/actuator/health &>/dev/null; then
        pass "Port-forward iniciado (PID: $PORT_FORWARD_PID)"
    else
        fail "No se pudo establecer port-forward"
        exit 1
    fi
fi

# ============================================================================
# 2. HEALTH CHECKS - ACTUATOR
# ============================================================================
echo -e "${BLUE}[2/4] Health Checks (Actuator)${NC}"

SERVICES=(
    "api-gateway:8080"
    "client-service:8200"
    "case-service:8300"
    "document-service:8500"
    "calendar-service:8600"
    "notification-service:8700"
    "n8n-integration-service:8800"
)

for svc_port in "${SERVICES[@]}"; do
    SVC=${svc_port%%:*}
    
    # Intentar acceder al health check del servicio a través del gateway
    if [ "$SVC" = "api-gateway" ]; then
        URL="http://localhost:$GATEWAY_PORT/actuator/health"
    else
        URL="http://localhost:$GATEWAY_PORT/$SVC/actuator/health"
    fi
    
    RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" "$URL" 2>/dev/null || echo "000")
    
    if [ "$RESPONSE" = "200" ]; then
        pass "$SVC (HTTP $RESPONSE)"
    elif [ "$RESPONSE" = "503" ]; then
        fail "$SVC (HTTP $RESPONSE - servicio degradado)"
    else
        fail "$SVC (HTTP $RESPONSE)"
    fi
done

# ============================================================================
# 3. DATABASE CONNECTIVITY
# ============================================================================
echo -e "${BLUE}[3/4] Conectividad Base de Datos${NC}"

PG_POD=$(kubectl get pods -n databases -l app.kubernetes.io/name=postgresql -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)

if [ -n "$PG_POD" ]; then
    # Test de conexión básica
    if kubectl exec -n databases $PG_POD -- psql -U carrillo -d carrillo_legal_tech -c "SELECT 1" &>/dev/null; then
        pass "Conexión a PostgreSQL"
    else
        fail "No se puede conectar a PostgreSQL"
    fi
    
    # Verificar schemas
    SCHEMAS=$(kubectl exec -n databases $PG_POD -- psql -U carrillo -d carrillo_legal_tech -t -c "SELECT schema_name FROM information_schema.schemata WHERE schema_name NOT IN ('pg_catalog', 'information_schema', 'pg_toast')" 2>/dev/null)
    
    if echo "$SCHEMAS" | grep -q "public"; then
        pass "Schema public existe"
    else
        fail "Schema public no encontrado"
    fi
else
    fail "Pod PostgreSQL no encontrado"
fi

# ============================================================================
# 4. NATS MESSAGING
# ============================================================================
echo -e "${BLUE}[4/4] Sistema de Mensajería${NC}"

NATS_POD=$(kubectl get pods -n messaging -l app.kubernetes.io/name=nats -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)

if [ -n "$NATS_POD" ]; then
    # Verificar si NATS está respondiendo
    if kubectl exec -n messaging $NATS_POD -- nats server check connection &>/dev/null; then
        pass "NATS respondiendo"
    else
        fail "NATS no responde"
    fi
else
    fail "Pod NATS no encontrado"
fi

# ============================================================================
# RESUMEN
# ============================================================================
echo ""
echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
TOTAL=$((PASSED + FAILED))
if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}  ✅ Tests exitosos: $PASSED/$TOTAL tests pasaron${NC}"
    echo ""
    echo -e "  API Gateway: ${BLUE}http://localhost:$GATEWAY_PORT${NC}"
    echo -e "  Swagger UI:  ${BLUE}http://localhost:$GATEWAY_PORT/swagger-ui.html${NC}"
else
    echo -e "${YELLOW}  ⚠️  Tests con advertencias: $PASSED/$TOTAL pasaron, $FAILED fallaron${NC}"
    echo ""
    echo "  Algunos servicios pueden estar iniciándose."
    echo -e "  Reintenta en unos minutos: ${BLUE}./scripts/test.sh${NC}"
fi
echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

exit $FAILED
