# Network Policies

Políticas de red para segmentar comunicación entre servicios.

## Políticas

1. `default-deny-all`: Bloquea todo tráfico por defecto
2. `api-gateway-network-policy`: Permite ingress desde Ingress Controller
3. `microservices-network-policy`: Permite tráfico desde API Gateway a servicios
4. `database-network-policy`: Permite acceso a PostgreSQL solo desde carrillo-dev
5. `nats-network-policy`: Permite acceso a NATS solo desde carrillo-dev

## Aplicación

```bash
kubectl apply -f infrastructure/k8s-manifests/network-policies/
```

## Verificación

```bash
kubectl get networkpolicies -n carrillo-dev
kubectl describe networkpolicy api-gateway-network-policy -n carrillo-dev
```

## Testing

```bash
# Probar conectividad desde pod
kubectl run test-pod --rm -it --image=busybox -n carrillo-dev -- sh
wget -O- http://user-service.carrillo-dev.svc.cluster.local:8700/actuator/health
```