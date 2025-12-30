# 🐳 DevOps Agent - Carrillo Abogados Legal Tech

## Propósito

Este agente está especializado en **operaciones de infraestructura, Docker, Kubernetes y CI/CD** para la plataforma. Conoce la configuración de Minikube en WSL, los charts de Helm, y los workflows de GitHub Actions.

---

## 🎯 Stack de Infraestructura

### Local Development
- **Minikube 1.34.0** - Cluster K8s local (driver Docker en WSL)
- **Docker Desktop** - Contenedores en Windows/WSL
- **Docker Compose** - Orquestación local simplificada
- **Helm 3.19.2** - Package manager para K8s

### Cloud (Staging/Prod)
- **GKE Autopilot** - Kubernetes gestionado en GCP
- **Cloud SQL** - PostgreSQL 16 gestionado
- **Cloud Storage** - Almacenamiento de documentos
- **Cloud Pub/Sub** - Mensajería en producción

### CI/CD
- **GitHub Actions** - Pipelines de build y deploy
- **Container Registry** - ghcr.io o GCR

---

## ⚠️ CRÍTICO: Entorno WSL

### Configuración del Sistema
- **Host**: Windows 11
- **WSL**: Ubuntu-24.04 (default)
- **Minikube**: Ejecuta DENTRO de WSL con driver Docker
- **kubectl/helm**: Instalados en WSL, NO en Windows

### Ejecución de Comandos desde PowerShell

**TODOS los comandos K8s deben ejecutarse via WSL:**

```powershell
# ✅ CORRECTO
wsl bash -c "kubectl get pods -n carrillo-dev"
wsl bash -c "minikube status"
wsl bash -c "helm list -n carrillo-dev"

# ❌ INCORRECTO - No funciona
kubectl get pods
minikube status
```

### Reiniciar WSL (Soluciona problemas de estabilidad)
```powershell
wsl --shutdown
# Esperar 10 segundos
wsl bash -c "minikube start"
```

---

## 📁 Estructura de Infraestructura

```
infrastructure/
├── k8s-manifests/
│   ├── namespaces/          # Definiciones de namespaces
│   ├── configmaps/          # ConfigMaps compartidos
│   ├── secrets/             # Templates de secrets
│   ├── rbac/                # Roles y bindings
│   └── network-policies/    # Políticas de red
├── terraform/               # IaC para GCP (futuro)
└── versions.yaml            # Versiones de componentes

helm-charts/
├── carrillo-abogados/       # Chart umbrella
│   ├── Chart.yaml
│   ├── values.yaml          # Configuración por defecto
│   └── templates/
│       ├── api-gateway/
│       ├── client-service/
│       ├── case-service/
│       └── ...
└── README.md
```

---

## 🐳 Docker Compose (Desarrollo Local)

### Comandos Principales
```powershell
# Levantar todos los servicios
docker-compose up -d

# Ver estado
docker-compose ps

# Ver logs de un servicio
docker-compose logs -f client-service

# Reconstruir y levantar
docker-compose up -d --build

# Detener todo
docker-compose down

# Limpiar volúmenes (¡CUIDADO! Borra datos)
docker-compose down -v
```

### Servicios en compose.yml
| Servicio | Puerto Host | Puerto Container |
|----------|-------------|------------------|
| postgresql | 5432 | 5432 |
| nats | 4222, 8222 | 4222, 8222 |
| api-gateway | 8080 | 8080 |
| client-service | 8200 | 8200 |
| case-service | 8300 | 8300 |
| payment-service | 8400 | 8400 |
| document-service | 8500 | 8500 |
| calendar-service | 8600 | 8600 |
| notification-service | 8700 | 8700 |
| n8n-integration-service | 8800 | 8800 |

---

## ☸️ Kubernetes (Minikube)

### Comandos Esenciales
```bash
# Iniciar cluster
minikube start --kubernetes-version=v1.34.0 --memory=7168 --cpus=4

# Verificar estado
minikube status
kubectl get nodes
kubectl get pods -A

# Cambiar a contexto de Docker de Minikube
eval $(minikube docker-env)

# Ver pods del proyecto
kubectl get pods -n carrillo-dev

# Ver logs de un pod
kubectl logs -f deployment/client-service -n carrillo-dev

# Port forward al gateway
kubectl port-forward svc/carrillo-dev-api-gateway 8080:8080 -n carrillo-dev
```

### Namespaces del Proyecto
```yaml
carrillo-dev    # Microservicios de desarrollo
databases       # PostgreSQL
messaging       # NATS
monitoring      # Prometheus, Grafana, Loki
```

### Deploy con Helm
```bash
# Instalar/actualizar
helm upgrade --install carrillo-dev helm-charts/carrillo-abogados/ \
  --namespace carrillo-dev \
  --create-namespace

# Ver estado
helm list -n carrillo-dev
helm status carrillo-dev -n carrillo-dev

# Desinstalar
helm uninstall carrillo-dev -n carrillo-dev
```

---

## 🔄 CI/CD - GitHub Actions

### Workflows Disponibles

#### 1. ci-cd-pipeline.yml
**Trigger**: Push a main/dev, PRs
**Stages**:
1. Build Maven (paralelo por servicio)
2. Run Tests
3. Build Docker Images
4. Push to Registry
5. Deploy to Staging (si main/dev)

#### 2. pr-validation.yml
**Trigger**: PR opened/updated
**Stages**:
1. Lint checks
2. Build verification
3. Unit tests
**Timeout**: 5 minutos

### Secrets Requeridos
```yaml
GHCR_TOKEN          # Token para GitHub Container Registry
GCP_SA_KEY          # Service Account de GCP (base64)
GCP_PROJECT_ID      # ID del proyecto GCP
KUBECONFIG_STAGING  # Kubeconfig para staging
KUBECONFIG_PROD     # Kubeconfig para producción
```

---

## 📋 Scripts del Proyecto

### scripts/check.sh
Verifica prerrequisitos (Java, Docker, kubectl, helm)

### scripts/deploy.sh
Despliegue completo en Minikube

### scripts/validate.sh
Valida que todos los pods estén Running

### scripts/test.sh
Ejecuta tests funcionales contra el cluster

### scripts/reset.sh
Limpia el entorno y permite empezar de cero

### Ejecución desde PowerShell
```powershell
wsl bash -c "./scripts/deploy.sh"
wsl bash -c "./scripts/validate.sh --wait"
```

---

## 🔧 Troubleshooting

### Pod en CrashLoopBackOff
```bash
# Ver eventos
kubectl describe pod <pod-name> -n carrillo-dev

# Ver logs anteriores
kubectl logs <pod-name> -n carrillo-dev --previous
```
**Causas comunes**: 
- Schema de BD no existe
- Variable de entorno faltante
- Puerto ocupado

### ImagePullBackOff
```bash
# Verificar imagen en Minikube
minikube ssh -- docker images | grep carrillo
```
**Solución**: Reconstruir imagen en contexto de Minikube

### Service no accesible
```bash
# Verificar service
kubectl get svc -n carrillo-dev

# Verificar endpoints
kubectl get endpoints <service-name> -n carrillo-dev
```

### Minikube se detiene
```powershell
# Reiniciar WSL
wsl --shutdown
# Esperar 10 segundos
wsl bash -c "minikube start"
```

---

## 📊 Monitoreo

### Prometheus
```bash
kubectl port-forward -n monitoring svc/prometheus 9090:9090
# Acceder: http://localhost:9090
```

### Grafana
```bash
kubectl port-forward -n monitoring svc/grafana 3000:3000
# Acceder: http://localhost:3000 (admin/admin)
```

### Logs (Loki)
Integrado con Grafana, usar "Explore" → Loki

---

## 📝 Checklist de Despliegue

### Nuevo Microservicio
1. [ ] Crear Dockerfile
2. [ ] Añadir al compose.yml
3. [ ] Crear template en helm-charts/carrillo-abogados/templates/
4. [ ] Añadir valores en values.yaml
5. [ ] Actualizar GitHub Actions para incluirlo
6. [ ] Documentar puertos y endpoints

### Release a Staging
1. [ ] Todos los tests pasan
2. [ ] PR aprobado y merged a dev
3. [ ] Pipeline de CI verde
4. [ ] Imágenes Docker construidas
5. [ ] Verificar secrets en GKE
6. [ ] Deploy y validar health checks

---

## 🔗 Referencias

- **Helm Charts**: [helm-charts/README.md](../../helm-charts/README.md)
- **K8s Manifests**: [infrastructure/k8s-manifests/](../../infrastructure/k8s-manifests/)
- **Operations Guide**: [docs/operations/OPERATIONS.md](../../docs/operations/OPERATIONS.md)
