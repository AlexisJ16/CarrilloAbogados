# Deployment Checklist

## Pre-deployment
- [ ] 8 microservicios (no 11)
- [ ] Puertos únicos sin conflictos
- [ ] replicaCount: 1 en todos los Helm charts
- [ ] autoscaling.enabled: false
- [ ] Sin referencias a oauth2-credentials
- [ ] Sin referencias a payment/order/proxy-client
- [ ] API Gateway tiene 7 rutas (no 2)
- [ ] ConfigMaps actualizados (sin payments)

## Compilation
- [ ] mvn clean package exitoso (8 servicios)
- [ ] 8 imágenes Docker construidas
- [ ] Imágenes en Minikube docker-env

## Deployment
- [ ] PostgreSQL ready
- [ ] NATS ready
- [ ] Helm install sin errores
- [ ] 8 pods running
- [ ] Health checks OK

## Validation Commands
```bash
# Verificar servicios running
kubectl get pods -n carrillo-dev

# Verificar puertos únicos
kubectl get services -n carrillo-dev

# Test API Gateway
kubectl port-forward svc/api-gateway 8080:8080 -n carrillo-dev
curl http://localhost:8080/actuator/health

# Verificar logs de servicios
kubectl logs deployment/user-service -n carrillo-dev
kubectl logs deployment/client-service -n carrillo-dev
```