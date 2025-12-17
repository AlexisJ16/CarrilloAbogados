#!/bin/bash
# ============================================================================
# VALIDATE.SH - Validación del Deployment
# ============================================================================
# Proyecto: Carrillo Abogados Legal Tech Platform
# Uso: ./scripts/validate.sh [--wait]
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
WAIT_MODE=false

# Parsear argumentos
for arg in "$@"; do
    case $arg in
        --wait) WAIT_MODE=true ;;
    esac
done

# Contadores
PASSED=0
FAILED=0

pass() { echo -e "  ${GREEN}✓${NC} $1"; ((PASSED++)); }
fail() { echo -e "  ${RED}✗${NC} $1"; ((FAILED++)); }

echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${CYAN}  🔍 Carrillo Abogados - Validación de Deployment${NC}"
echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

# ============================================================================
# 1. MINIKUBE
# ============================================================================
echo -e "${BLUE}[1/5] Minikube${NC}"
if minikube status 2>/dev/null | grep -q "host: Running"; then
    pass "Cluster running"
else
    fail "Cluster no está running"
fi

# ============================================================================
# 2. NAMESPACES
# ============================================================================
echo -e "${BLUE}[2/5] Namespaces${NC}"
for ns in carrillo-dev databases messaging; do
    if kubectl get namespace $ns &>/dev/null; then
        pass "$ns"
    else
        fail "$ns no existe"
    fi
done

# ============================================================================
# 3. POSTGRESQL
# ============================================================================
echo -e "${BLUE}[3/5] PostgreSQL${NC}"
PG_POD=$(kubectl get pods -n databases -l app.kubernetes.io/name=postgresql -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)
if [ -n "$PG_POD" ]; then
    PG_STATUS=$(kubectl get pod $PG_POD -n databases -o jsonpath='{.status.phase}')
    if [ "$PG_STATUS" = "Running" ]; then
        pass "Pod $PG_POD ($PG_STATUS)"
    else
        fail "Pod $PG_POD ($PG_STATUS)"
    fi
else
    fail "No hay pods de PostgreSQL"
fi

# ============================================================================
# 4. NATS
# ============================================================================
echo -e "${BLUE}[4/5] NATS${NC}"
NATS_POD=$(kubectl get pods -n messaging -l app.kubernetes.io/name=nats -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)
if [ -n "$NATS_POD" ]; then
    NATS_STATUS=$(kubectl get pod $NATS_POD -n messaging -o jsonpath='{.status.phase}')
    if [ "$NATS_STATUS" = "Running" ]; then
        pass "Pod $NATS_POD ($NATS_STATUS)"
    else
        fail "Pod $NATS_POD ($NATS_STATUS)"
    fi
else
    fail "No hay pods de NATS"
fi

# ============================================================================
# 5. MICROSERVICIOS
# ============================================================================
echo -e "${BLUE}[5/5] Microservicios${NC}"

SERVICES=(
    "api-gateway"
    "client-service"
    "case-service"
    "document-service"
    "calendar-service"
    "notification-service"
    "n8n-integration-service"
)

if [ "$WAIT_MODE" = true ]; then
    echo -e "  ${YELLOW}Esperando que los pods estén ready (máx 5 min)...${NC}"
    kubectl wait --for=condition=ready pod -l app.kubernetes.io/instance=carrillo-dev -n $NAMESPACE --timeout=300s 2>/dev/null || true
fi

for svc in "${SERVICES[@]}"; do
    DEPLOY_NAME="carrillo-dev-$svc"
    READY=$(kubectl get deployment $DEPLOY_NAME -n $NAMESPACE -o jsonpath='{.status.readyReplicas}' 2>/dev/null)
    REPLICAS=$(kubectl get deployment $DEPLOY_NAME -n $NAMESPACE -o jsonpath='{.spec.replicas}' 2>/dev/null)
    
    if [ -n "$READY" ] && [ "$READY" -ge 1 ]; then
        pass "$svc ($READY/$REPLICAS ready)"
    elif [ -n "$REPLICAS" ]; then
        fail "$svc (0/$REPLICAS ready)"
    else
        fail "$svc (no deployment)"
    fi
done

# ============================================================================
# RESUMEN
# ============================================================================
echo ""
echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
TOTAL=$((PASSED + FAILED))
if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}  ✅ Validación exitosa: $PASSED/$TOTAL verificaciones pasaron${NC}"
    echo ""
    echo -e "  Siguiente: ${BLUE}./scripts/test.sh${NC}"
else
    echo -e "${RED}  ❌ Validación con errores: $PASSED/$TOTAL pasaron, $FAILED fallaron${NC}"
    echo ""
    echo "  Para debug:"
    echo -e "    ${BLUE}kubectl get pods -n carrillo-dev${NC}"
    echo -e "    ${BLUE}kubectl logs deployment/carrillo-dev-api-gateway -n carrillo-dev${NC}"
fi
echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

exit $FAILED
