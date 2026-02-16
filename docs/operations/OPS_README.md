# OPS README - Carrillo Abogados DevOps

**Última Actualización**: 14 de Febrero, 2026  
**Estado**: FASE 14 - Infraestructura Depurada

## 📋 Índice

1. [Arquitectura de Puertos](#arquitectura-de-puertos)
2. [Scripts de Gestión](#scripts-de-gestión)
3. [Workflows Comunes](#workflows-comunes)
4. [Troubleshooting](#troubleshooting)
5. [Preparación para GCP/GKE](#preparación-para-gcpgke)

---

## 🏗️ Arquitectura de Puertos

### Microservicios

| Servicio | Puerto | Descripción | Estado |
|----------|--------|-------------|--------|
| api-gateway | 8080 | Gateway principal, CORS, Circuit Breaker | ✅ Activo |
| client-service | 8200 | Clientes, Leads, Autenticación JWT | ✅ Activo |
| case-service | 8300 | Gestión de casos legales | ✅ Activo |
| payment-service | 8400 | Procesamiento de pagos gubernamentales | ✅ Activo |
| document-service | 8500 | Gestión de documentos legales | ✅ Activo |
| calendar-service | 8600 | Integración con Google Calendar | ✅ Activo |
| notification-service | 8700 | Notificaciones (Email/SMS/Push) | ✅ Activo |
| n8n-integration-service | 8800 | Bridge con n8n Cloud | ✅ Activo |

### Servicios Eliminados (FASE 14)

| Servicio | Razón |
|----------|-------|
| ~~user-service~~ | Migrado a client-service (código eliminado) |
| ~~order-service~~ | Nunca existió (template e-commerce, eliminado) |
| ~~proxy-client~~ | Nunca existió (eliminado) |

### Infraestructura

| Servicio | Puerto | Namespace |
|----------|--------|-----------|
| PostgreSQL | 5432 | databases |
| NATS | 4222/8222 | messaging |

---

## 🔧 Scripts de Gestión

### 1. check-env.sh

**Propósito**: Validar que el entorno cumple con las versiones requeridas.

**Cuándo usar**: Antes de cualquier deployment o después de actualizar herramientas.

**Uso**:
```bash
./scripts/check-env.sh
```

**Output esperado**:
```
✓ Java 21
✓ Docker 24.x
✓ kubectl 1.31
✓ Helm 3.19
✓ Minikube 1.37
✓ JAVA_HOME configurado
✅ Entorno validado correctamente
```

---

### 2. nuke-environment.sh

**Propósito**: Limpieza total del entorno (Factory Reset).

**Cuándo usar**:
- Cuando el entorno está corrupto
- Antes de recrear desde cero
- Para liberar espacio en disco

**Uso**:
```bash
./scripts/nuke-environment.sh
```

**ADVERTENCIA**: Este script elimina TODO (cluster, imágenes, compilaciones).

---

### 3. dev-up.sh

**Propósito**: Rutina diaria inteligente. Detecta el estado y recupera lo necesario.

**Cuándo usar**:
- Todos los días al iniciar el trabajo
- Después de reiniciar el PC
- Cuando no estás seguro del estado del entorno

**Uso**:
```bash
./scripts/dev-up.sh
```

**Características**:
- ✅ Detecta si Minikube está corriendo
- ✅ Verifica si los namespaces existen
- ✅ Valida si las imágenes Docker están construidas
- ✅ Solo construye lo que falta (no reconstruye todo)
- ✅ Actualiza los deployments si ya existen

**Tiempo de ejecución**:
- Primera vez: ~15 minutos
- Días siguientes: ~2 minutos

---

### 4. deploy-complete.sh

**Propósito**: CI/CD local completo. Deployment desde cero.

**Cuándo usar**:
- Después de cambios mayores en el código
- Para validar el proceso completo de deployment
- En preparación para un release

**Uso**:
```bash
./scripts/deploy-complete.sh
```

**Pasos que ejecuta**:
1. Valida el entorno
2. (Opcional) Limpia el entorno
3. Compila todos los proyectos Java
4. Construye todas las imágenes Docker
5. Despliega infraestructura (PostgreSQL, NATS)
6. Despliega aplicaciones con Helm
7. Ejecuta validaciones de smoke tests

**Tiempo de ejecución**: ~20 minutos

---

### 5. validate-deployment.sh

**Propósito**: Smoke tests para validar que todo funciona.

**Cuándo usar**:
- Después de cualquier deployment
- Para verificar el estado del sistema
- En CI/CD pipelines

**Uso**:
```bash
./scripts/validate-deployment.sh
```

**Validaciones que realiza**:
- ✅ Minikube está corriendo
- ✅ Namespaces existen
- ✅ PostgreSQL está Running y accesible
- ✅ NATS está Running
- ✅ Todos los pods de aplicaciones están Running & Ready
- ✅ API Gateway responde en /actuator/health
- ✅ Conectividad a PostgreSQL funciona

**Output exitoso**:
```
✅ VALIDACIÓN EXITOSA
El deployment está completamente funcional.
```

---

## 🔄 Workflows Comunes

### Workflow 1: Inicio del Día (Rutina Diaria)

```bash
# 1. Arrancar el entorno
./scripts/dev-up.sh

# 2. Validar que todo funciona
./scripts/validate-deployment.sh

# 3. Acceder al API Gateway
kubectl port-forward -n carrillo-dev svc/api-gateway 8080:8080
```

---

### Workflow 2: Después de Cambios en el Código

```bash
# 1. Validar entorno
./scripts/check-env.sh

# 2. Construir solo las imágenes modificadas
eval $(minikube docker-env)
cd <servicio-modificado>
mvn clean package -DskipTests
docker build -t carrilloabogados/<servicio>:v0.1.0 .

# 3. Reiniciar el deployment
kubectl rollout restart deployment <servicio> -n carrillo-dev

# 4. Ver logs
kubectl logs -f -n carrillo-dev -l app=<servicio>
```

---

### Workflow 3: Deployment Completo desde Cero

```bash
# 1. Limpieza opcional (si el entorno está corrupto)
./scripts/nuke-environment.sh

# 2. Deployment completo
./scripts/deploy-complete.sh

# 3. Validar
./scripts/validate-deployment.sh
```

---

### Workflow 4: Debugging de un Pod Fallido

```bash
# 1. Ver estado de pods
kubectl get pods -n carrillo-dev

# 2. Ver logs del pod
kubectl logs -n carrillo-dev <pod-name>

# 3. Ver eventos del pod
kubectl describe pod -n carrillo-dev <pod-name>

# 4. Ejecutar shell dentro del pod
kubectl exec -it -n carrillo-dev <pod-name> -- /bin/sh

# 5. Si el pod está en CrashLoopBackOff, ver logs anteriores
kubectl logs -n carrillo-dev <pod-name> --previous
```

---

## 🚨 Troubleshooting

### Problema: "JAVA_HOME not set"

**Solución**:
```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Para hacerlo permanente:
echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```

---

### Problema: "Docker daemon not responding"

**Solución**:
```bash
# En WSL, asegurarse de que Docker Desktop esté corriendo en Windows
# Luego verificar conectividad:
docker ps

# Si no funciona, reiniciar Docker Desktop desde Windows
```

---

### Problema: "Pod en CrashLoopBackOff"

**Diagnóstico**:
```bash
# Ver logs
kubectl logs -n carrillo-dev <pod-name>

# Ver eventos
kubectl describe pod -n carrillo-dev <pod-name>

# Ver logs del contenedor anterior
kubectl logs -n carrillo-dev <pod-name> --previous
```

**Causas comunes**:
- ❌ La imagen Docker no existe → Ejecutar `./scripts/build-all-images.sh`
- ❌ Falta configuración → Verificar ConfigMaps y Secrets
- ❌ Puerto incorrecto → Verificar `values.yaml` del Helm Chart
- ❌ Health check falla → Verificar que `/actuator/health` esté disponible

---

### Problema: "Helm install failed"

**Solución**:
```bash
# Ver el release fallido
helm list -n carrillo-dev

# Ver el status detallado
helm status carrillo-dev -n carrillo-dev

# Desinstalar y reinstalar
helm uninstall carrillo-dev -n carrillo-dev
helm install carrillo-dev helm-charts/carrillo-abogados/ -n carrillo-dev
```

---

### Problema: "NetworkPolicy bloqueando conexiones"

**Diagnóstico**:
```bash
# Ver NetworkPolicies activas
kubectl get networkpolicies -n carrillo-dev

# Describir una NetworkPolicy específica
kubectl describe networkpolicy <nombre> -n carrillo-dev

# Probar conectividad desde un pod temporal
kubectl run test-pod --rm -it --image=busybox -n carrillo-dev -- sh
wget -O- http://client-service.carrillo-dev.svc.cluster.local:8200/actuator/health
```

---

## ☁️ Preparación para GCP/GKE

### Diferencias entre Minikube y GKE

|Aspecto|Minikube|GKE|
|---|---|---|
|Ingress|NGINX|GCE Load Balancer|
|Storage|hostPath|GCE Persistent Disk|
|Secrets|Kubernetes Secrets|Google Secret Manager|
|Registry|Local Minikube|Google Container Registry (GCR)|
|Node Pools|Single node|Multiple pools con autoscaling|

---

### Checklist de Migración a GKE

#### 1. Imágenes Docker

**Local (Minikube)**:
```bash
eval $(minikube docker-env)
docker build -t carrilloabogados/api-gateway:v0.1.0 .
```

**GKE**:
```bash
# Tag para GCR
docker tag carrilloabogados/api-gateway:v0.1.0 gcr.io/carrillo-legal-tech/api-gateway:v0.1.0

# Push a GCR
docker push gcr.io/carrillo-legal-tech/api-gateway:v0.1.0

# Actualizar values.yaml
image:
  repository: gcr.io/carrillo-legal-tech/api-gateway
  tag: "v0.1.0"
```

---

#### 2. Ingress Controller

**values.yaml para GKE**:
```yaml
ingress:
  enabled: true
  className: "gce"  # Cambiar de "nginx" a "gce"
  annotations:
    kubernetes.io/ingress.class: gce
    kubernetes.io/ingress.global-static-ip-name: carrillo-legal-tech-ip
```

---

#### 3. Secrets Management

**Opción A: Google Secret Manager (recomendado)**

1. Crear secrets en GCP:
```bash
echo -n "CarrilloAbogados2025!" | gcloud secrets create postgresql-password --data-file=-
```

2. Usar External Secrets Operator en Kubernetes

**Opción B: Kubernetes Secrets con encriptación at-rest**
```bash
# Habilitar encriptación en GKE
gcloud container clusters update carrillo-cluster \
  --database-encryption-key=projects/PROJECT_ID/locations/LOCATION/keyRings/KEYRING/cryptoKeys/KEY
```

---

#### 4. Node Pools y Tolerations

**values.yaml para GKE**:
```yaml
nodeSelector:
  cloud.google.com/gke-nodepool: default-pool

tolerations:
  - key: "cloud.google.com/gke-spot"
    operator: "Equal"
    value: "true"
    effect: "NoSchedule"

affinity:
  podAntiAffinity:
    preferredDuringSchedulingIgnoredDuringExecution:
      - weight: 100
        podAffinityTerm:
          topologyKey: kubernetes.io/hostname
```

---

#### 5. Storage Class

**Minikube**:
```yaml
storageClassName: standard  # hostPath
```

**GKE**:
```yaml
storageClassName: standard-rwo  # GCE Persistent Disk
```

---

### Script de Deployment a GKE

Crear `scripts/deploy-to-gke.sh`:
```bash
#!/bin/bash
set -e

PROJECT_ID="carrillo-legal-tech"
CLUSTER_NAME="carrillo-cluster"
REGION="us-central1"

# 1. Configurar gcloud
gcloud config set project $PROJECT_ID

# 2. Conectar a cluster GKE
gcloud container clusters get-credentials $CLUSTER_NAME --region $REGION

# 3. Build y push imágenes a GCR
for service in api-gateway client-service case-service payment-service document-service calendar-service notification-service n8n-integration-service; do
    docker build -t gcr.io/$PROJECT_ID/$service:v0.1.0 ./$service/
    docker push gcr.io/$PROJECT_ID/$service:v0.1.0
done

# 4. Deploy con Helm (usando values específicos de GKE)
helm upgrade --install carrillo-prod helm-charts/carrillo-abogados/ \
  -n carrillo-prod \
  --create-namespace \
  -f helm-charts/carrillo-abogados/values-gke.yaml
```

---

## 📊 Métricas y Monitoreo

### Acceder a Grafana (cuando esté configurado)

```bash
kubectl port-forward -n monitoring svc/grafana 3000:3000
```

URL: http://localhost:3000

---

### Ver métricas de pods

```bash
kubectl top pods -n carrillo-dev
```

---

### Ver logs agregados

```bash
# Logs de todos los pods de un servicio
kubectl logs -n carrillo-dev -l app=client-service --tail=100 -f

# Logs con timestamps
kubectl logs -n carrillo-dev <pod-name> --timestamps=true
```

---

## 🔐 Seguridad

### Rotar Secrets

```bash
# 1. Actualizar el Secret en Kubernetes
kubectl create secret generic postgresql-credentials \
  --from-literal=postgres-password=<NEW_PASSWORD> \
  -n carrillo-dev \
  --dry-run=client -o yaml | kubectl apply -f -

# 2. Reiniciar pods para que usen el nuevo secret
kubectl rollout restart deployment -n carrillo-dev --all
```

---

### Escanear imágenes Docker

```bash
# Usando Trivy
trivy image carrilloabogados/api-gateway:v0.1.0
```

---

## 📞 Contacto y Soporte

Para dudas o problemas, contactar al equipo de DevOps:

- Email: ingenieria@carrilloabgd.com
- Slack: #devops-carrillo