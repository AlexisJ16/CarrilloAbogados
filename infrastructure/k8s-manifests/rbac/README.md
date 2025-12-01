# RBAC - Role-Based Access Control

Control de accesos para microservicios de Carrillo Abogados.

## Service Accounts

- `api-gateway-sa`: API Gateway con permisos de lectura de services/endpoints
- `microservices-sa`: Microservicios con permisos mínimos
- `database-client-sa`: Acceso a secrets de PostgreSQL

## Aplicación

```bash
kubectl apply -f infrastructure/k8s-manifests/rbac/ -n carrillo-dev
```

## Verificación

```bash
kubectl get serviceaccounts -n carrillo-dev
kubectl get roles -n carrillo-dev
kubectl get rolebindings -n carrillo-dev
```