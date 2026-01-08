# Guía de Operaciones

**Última Actualización**: 11 de Enero, 2026  
**Fase Proyecto**: FASE 10 - Autenticación Frontend Completa

---

## Despliegue Local (Minikube)

### Prerrequisitos
```bash
minikube start --kubernetes-version=v1.34.0 --memory=7168 --cpus=4
kubectl apply -f infrastructure/k8s-manifests/namespaces/
```

### Instalar dependencias

```bash
# NATS
helm install nats nats/nats --namespace messaging

# PostgreSQL
helm install postgresql bitnami/postgresql --namespace databases
```

### Desplegar servicios

```bash
helm install carrillo-dev helm-charts/carrillo-abogados/ \
  --namespace carrillo-dev \
  --create-namespace
```

### Verificar despliegue

```bash
kubectl get pods -n carrillo-dev
kubectl get svc -n carrillo-dev
```

## Monitoreo

### Prometheus

```bash
kubectl port-forward -n monitoring svc/prometheus 9090:9090
```

### Grafana

```bash
kubectl port-forward -n monitoring svc/grafana 3000:3000
```

## Troubleshooting

### Logs de servicio

```bash
kubectl logs -f deployment/api-gateway -n carrillo-dev
```

### Reiniciar servicio

```bash
kubectl rollout restart deployment/api-gateway -n carrillo-dev
```

### Escalar manualmente

```bash
kubectl scale deployment/api-gateway --replicas=5 -n carrillo-dev
```