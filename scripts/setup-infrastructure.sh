#!/bin/bash
# Full deployment script for Carrillo Abogados Legal Tech Platform
set -e

echo "=== Verificando cluster ==="
kubectl get nodes

echo ""
echo "=== Creando namespaces ==="
kubectl create namespace carrillo-dev 2>/dev/null || echo "carrillo-dev exists"
kubectl create namespace databases 2>/dev/null || echo "databases exists"
kubectl create namespace messaging 2>/dev/null || echo "messaging exists"

echo ""
echo "=== Instalando PostgreSQL ==="
helm install postgresql bitnami/postgresql -n databases \
  --set auth.username=carrillo \
  --set auth.password=carrillo123 \
  --set auth.database=carrillo_legal_tech \
  --set primary.persistence.size=1Gi \
  2>/dev/null || echo "PostgreSQL ya instalado"

echo ""
echo "=== Instalando NATS ==="
helm install nats nats/nats -n messaging \
  --set config.jetstream.enabled=true \
  2>/dev/null || echo "NATS ya instalado"

echo ""
echo "=== Esperando PostgreSQL ==="
kubectl wait --for=condition=ready pod -l app.kubernetes.io/name=postgresql -n databases --timeout=180s

echo ""
echo "=== Creando schemas PostgreSQL ==="
# Using PGPASSWORD env var to avoid password prompt
kubectl exec postgresql-0 -n databases -- bash -c 'PGPASSWORD=carrillo123 psql -U carrillo -d carrillo_legal_tech -c "CREATE SCHEMA IF NOT EXISTS clients; CREATE SCHEMA IF NOT EXISTS cases; CREATE SCHEMA IF NOT EXISTS documents; CREATE SCHEMA IF NOT EXISTS payments; CREATE SCHEMA IF NOT EXISTS calendar; CREATE SCHEMA IF NOT EXISTS notifications; CREATE SCHEMA IF NOT EXISTS users;"'

echo ""
echo "=== Verificando schemas ==="
kubectl exec postgresql-0 -n databases -- bash -c 'PGPASSWORD=carrillo123 psql -U carrillo -d carrillo_legal_tech -c "\\dn"'

echo ""
echo "=== Aplicando RBAC ==="
kubectl apply -f k8s-manifests/rbac/ -n carrillo-dev 2>/dev/null || echo "RBAC aplicado o no existe"

echo ""
echo "=== Estado de pods en databases y messaging ==="
kubectl get pods -n databases
kubectl get pods -n messaging

echo ""
echo "=== Infraestructura lista! ==="
