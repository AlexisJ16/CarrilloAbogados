#!/bin/bash
# Create PostgreSQL schemas for Carrillo Abogados
kubectl exec postgresql-0 -n databases -- bash -c 'PGPASSWORD=carrillo123 psql -U carrillo -d carrillo_legal_tech -c "CREATE SCHEMA IF NOT EXISTS clients; CREATE SCHEMA IF NOT EXISTS cases; CREATE SCHEMA IF NOT EXISTS documents; CREATE SCHEMA IF NOT EXISTS payments; CREATE SCHEMA IF NOT EXISTS calendar; CREATE SCHEMA IF NOT EXISTS notifications; CREATE SCHEMA IF NOT EXISTS users;"'

echo ""
echo "=== Verificando schemas creados ==="
kubectl exec postgresql-0 -n databases -- bash -c 'PGPASSWORD=carrillo123 psql -U carrillo -d carrillo_legal_tech -c "\dn"'
