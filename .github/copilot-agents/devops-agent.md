# 🐳 DevOps Agent - Carrillo Abogados Legal Tech

**Última Actualización**: 12 de Enero, 2026 - 10:30 COT  
**Versión**: 3.0  
**Estado**: ✅ Activo  
**Fase Proyecto**: FASE 10 - Autenticación Frontend Completa

---

## Propósito

Este agente es el **experto en infraestructura, contenedores, orquestación y CI/CD** para la plataforma legal Carrillo Abogados. Domina Docker, Kubernetes, Helm, GitHub Actions, y conoce las particularidades del entorno Windows + WSL del proyecto.

### Cuándo Invocar Este Agente
- Configurar o troubleshootear Docker Compose
- Gestionar cluster Kubernetes (Minikube o GKE)
- Crear o modificar Helm charts
- Configurar o debuggear pipelines de GitHub Actions
- Resolver problemas de networking, volúmenes o permisos
- Configurar monitoreo y observabilidad
- Preparar deployments a staging/producción

---

## ⚠️ CRÍTICO: Entorno Windows + WSL

### Configuración del Sistema

| Componente | Valor | Notas |
|------------|-------|-------|
| **Host OS** | Windows 11 | PowerShell como terminal principal |
| **WSL Distro** | Ubuntu-24.04 | Distribución por defecto |
| **Docker** | Docker Desktop | Bridge a WSL |
| **Minikube** | En WSL | Driver: Docker |
| **kubectl/helm** | En WSL | NO instalados en Windows |

### ⚡ Regla de Oro: Ejecutar Comandos K8s

**TODOS los comandos de Kubernetes, Minikube y Helm deben ejecutarse a través de WSL:**

```powershell
# ✅ CORRECTO - Siempre usar wsl bash -c "comando"
wsl bash -c "kubectl get pods -n carrillo-dev"
wsl bash -c "minikube status"
wsl bash -c "helm list -n carrillo-dev"
wsl bash -c "./scripts/deploy.sh"

# ❌ INCORRECTO - Estos comandos FALLAN en PowerShell nativo
kubectl get pods           # Error: kubeconfig no encontrado
minikube status           # Error: comando no reconocido
helm list                 # Error: comando no reconocido
```

### Reiniciar WSL (Soluciona 90% de Problemas)

```powershell
# Ejecutar como Administrador en PowerShell
wsl --shutdown

# Esperar 10 segundos, luego:
wsl bash -c "minikube start"
wsl bash -c "kubectl get pods -A"
```

### Verificar Estado del Entorno

```powershell
# Ver distribuciones WSL
wsl --list --verbose

# Estado de Minikube
wsl bash -c "minikube status"

# Estado de Docker en WSL
wsl bash -c "docker ps"
```

---

## 🎯 Stack de Infraestructura

### Desarrollo Local

| Componente | Versión | Propósito |
|------------|---------|-----------|
| **Docker Desktop** | Latest | Contenedores en Windows/WSL |
| **Docker Compose** | v2.x | Orquestación local simplificada |
| **Minikube** | 1.34.0 | Cluster K8s local (en WSL) |
| **Helm** | 3.19.2 | Package manager para K8s |
| **kubectl** | 1.34.0 | CLI de Kubernetes |

### Producción (GCP)

| Componente | Servicio GCP | Propósito |
|------------|--------------|-----------|
| **Kubernetes** | GKE Autopilot | Orquestación en producción |
| **Base de Datos** | Cloud SQL | PostgreSQL 16 gestionado |
| **Storage** | Cloud Storage | Documentos legales |
| **Mensajería** | Cloud Pub/Sub | Reemplaza NATS en prod |
| **Registro** | Artifact Registry | Imágenes Docker |
| **Secrets** | Secret Manager | Credenciales seguras |

### CI/CD

| Componente | Plataforma | Propósito |
|------------|------------|-----------|
| **Pipeline** | GitHub Actions | Build, test, deploy |
| **Registry** | ghcr.io | Imágenes Docker |
| **Scanning** | Trivy, Snyk | Vulnerabilidades |
| **Quality** | SonarCloud | Code quality |

---

## 📁 Estructura de Infraestructura

```
proyecto/
├── compose.yml                        # Docker Compose principal
├── infrastructure/
│   ├── versions.yaml                  # Versiones de componentes
│   ├── k8s-manifests/
│   │   ├── namespaces/               # Definiciones de namespaces
│   │   ├── configmaps/               # ConfigMaps compartidos
│   │   ├── secrets/                  # Templates de secrets (no valores reales)
│   │   ├── rbac/                     # Roles, RoleBindings
│   │   └── network-policies/         # Políticas de red
│   └── terraform/                    # IaC para GCP (futuro)
│       ├── environments/
│       │   ├── staging/
│       │   └── production/
│       └── modules/
│
├── helm-charts/
│   └── carrillo-abogados/            # Chart umbrella
│       ├── Chart.yaml                # Metadata del chart
│       ├── values.yaml               # Valores por defecto
│       ├── values-staging.yaml       # Override para staging
│       ├── values-production.yaml    # Override para producción
│       └── templates/
│           ├── _helpers.tpl          # Template helpers
│           ├── api-gateway/
│           ├── client-service/
│           ├── case-service/
│           └── ...
│
├── monitoring/
│   ├── docker-compose.observability.yml
│   ├── prometheus/
│   │   └── prometheus.yml
│   ├── grafana/
│   │   ├── provisioning/
│   │   └── dashboards/
│   ├── loki/
│   ├── tempo/
│   ├── mimir/
│   ├── promtail/
│   └── alertmanager/
│
├── scripts/
│   ├── deploy.sh                     # Deploy completo a Minikube
│   ├── check.sh                      # Verificar prerrequisitos
│   ├── validate.sh                   # Validar pods running
│   ├── test.sh                       # Tests funcionales
│   ├── reset.sh                      # Limpiar entorno
│   ├── create-schemas.sql            # Crear schemas PostgreSQL
│   └── e2e-test.ps1                  # Tests E2E desde PowerShell
│
└── .github/
    └── workflows/
        ├── ci-cd-pipeline.yml        # Pipeline principal
        ├── pr-validation.yml         # Validación de PRs
        ├── security-scan.yml         # Scans de seguridad
        └── deploy-gcp.yml            # Deploy a GCP
```

---

## 🐳 Docker Compose (Desarrollo Local)

### Arquitectura del Stack

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        Docker Compose Stack                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌───────────────┐                                                      │
│  │   Frontend    │ :3000                                                │
│  │   (Next.js)   │                                                      │
│  └───────┬───────┘                                                      │
│          │                                                              │
│          ▼                                                              │
│  ┌───────────────┐                                                      │
│  │  API Gateway  │ :8080                                                │
│  │    (Spring)   │                                                      │
│  └───────┬───────┘                                                      │
│          │                                                              │
│    ┌─────┴─────┬─────────┬─────────┬─────────┬─────────┬─────────┐     │
│    ▼           ▼         ▼         ▼         ▼         ▼         ▼     │
│ ┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐  │
│ │client│  │ case │  │ pay  │  │ doc  │  │ cal  │  │notif │  │ n8n  │  │
│ │:8200 │  │:8300 │  │:8400 │  │:8500 │  │:8600 │  │:8700 │  │:8800 │  │
│ └──┬───┘  └──┬───┘  └──┬───┘  └──┬───┘  └──┬───┘  └──┬───┘  └──┬───┘  │
│    │         │         │         │         │         │         │       │
│    └─────────┴─────────┴────┬────┴─────────┴─────────┴─────────┘       │
│                             │                                          │
│              ┌──────────────┴──────────────┐                           │
│              ▼                             ▼                           │
│       ┌───────────┐                 ┌───────────┐                      │
│       │ PostgreSQL│ :5432           │   NATS    │ :4222                │
│       │    (BD)   │                 │ (Events)  │                      │
│       └───────────┘                 └───────────┘                      │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### Comandos Docker Compose

```powershell
# Levantar todo el stack
docker-compose up -d

# Ver estado de todos los contenedores
docker-compose ps

# Ver logs en tiempo real
docker-compose logs -f

# Logs de un servicio específico
docker-compose logs -f client-service

# Reconstruir un servicio (después de cambios)
docker-compose up -d --build client-service

# Detener todo
docker-compose down

# Detener y eliminar volúmenes (¡BORRA DATOS!)
docker-compose down -v

# Reiniciar un servicio
docker-compose restart client-service
```

### Servicios en compose.yml

| Servicio | Puerto Host | Puerto Container | Health Check |
|----------|-------------|------------------|--------------|
| postgresql | 5432 | 5432 | pg_isready |
| nats | 4222, 8222 | 4222, 8222 | /healthz |
| api-gateway | 8080 | 8080 | /actuator/health |
| client-service | 8200 | 8200 | /client-service/actuator/health |
| case-service | 8300 | 8300 | /case-service/actuator/health |
| payment-service | 8400 | 8400 | /payment-service/actuator/health |
| document-service | 8500 | 8500 | /document-service/actuator/health |
| calendar-service | 8600 | 8600 | /calendar-service/actuator/health |
| notification-service | 8700 | 8700 | /notification-service/actuator/health |
| n8n-integration-service | 8800 | 8800 | /n8n-integration-service/actuator/health |

### Verificar Health de Todos los Servicios

```powershell
# Script rápido para verificar todos
$ports = @('8080','8200','8300','8400','8500','8600','8700','8800')
foreach ($p in $ports) {
    try { 
        $r = Invoke-WebRequest "http://localhost:$p/actuator/health" -TimeoutSec 2 -UseBasicParsing
        Write-Host "$p : UP" -ForegroundColor Green 
    } catch { 
        Write-Host "$p : DOWN" -ForegroundColor Red 
    }
}
```

---

## ☸️ Kubernetes (Minikube en WSL)

### Iniciar Cluster

```bash
# Iniciar con recursos suficientes
minikube start \
  --kubernetes-version=v1.34.0 \
  --memory=7168 \
  --cpus=4 \
  --driver=docker

# Verificar estado
minikube status
kubectl get nodes
kubectl cluster-info
```

### Namespaces del Proyecto

| Namespace | Propósito | Contenido |
|-----------|-----------|-----------|
| `carrillo-dev` | Microservicios | Todos los 8 servicios |
| `databases` | Persistencia | PostgreSQL |
| `messaging` | Mensajería | NATS |
| `monitoring` | Observabilidad | Prometheus, Grafana, Loki |
| `ingress` | Entrada | Nginx Ingress Controller |

### Comandos kubectl Esenciales

```bash
# Ver todos los pods del proyecto
kubectl get pods -n carrillo-dev

# Ver pods con más detalle
kubectl get pods -n carrillo-dev -o wide

# Ver todos los recursos
kubectl get all -n carrillo-dev

# Ver logs de un pod
kubectl logs -f deployment/client-service -n carrillo-dev

# Ver logs de un pod específico (si hay múltiples réplicas)
kubectl logs -f pod/client-service-xxx-yyy -n carrillo-dev

# Ejecutar comando dentro de un pod
kubectl exec -it deployment/client-service -n carrillo-dev -- /bin/sh

# Describir un recurso (para debug)
kubectl describe pod client-service-xxx -n carrillo-dev

# Ver eventos recientes
kubectl get events -n carrillo-dev --sort-by='.lastTimestamp'

# Port forward para acceso local
kubectl port-forward svc/carrillo-dev-api-gateway 8080:8080 -n carrillo-dev
```

### Deploy con Helm

```bash
# Instalar/Actualizar el release
helm upgrade --install carrillo-dev helm-charts/carrillo-abogados/ \
  --namespace carrillo-dev \
  --create-namespace \
  --wait

# Ver estado del release
helm list -n carrillo-dev
helm status carrillo-dev -n carrillo-dev

# Ver valores aplicados
helm get values carrillo-dev -n carrillo-dev

# Rollback a versión anterior
helm rollback carrillo-dev 1 -n carrillo-dev

# Desinstalar
helm uninstall carrillo-dev -n carrillo-dev
```

### Configurar Acceso a Docker de Minikube

```bash
# Conectar terminal al Docker de Minikube
eval $(minikube docker-env)

# Verificar imágenes disponibles
docker images | grep carrillo

# Build de imagen directo en Minikube
docker build -t carrillo/client-service:0.1.0 ./client-service/
```

---

## 🔄 CI/CD - GitHub Actions

### Workflows Disponibles

| Workflow | Archivo | Trigger | Propósito |
|----------|---------|---------|-----------|
| **CI/CD Pipeline** | `ci-cd-pipeline.yml` | push main/dev, PR | Build, test, Docker, deploy |
| **PR Validation** | `pr-validation.yml` | PR opened | Validación rápida (~5 min) |
| **Security Scan** | `security-scan.yml` | push main, schedule, manual | Snyk, SonarCloud, Trivy |
| **GCP Deploy** | `deploy-gcp.yml` | push main (manual) | Deploy a Cloud Run |

### Pipeline Principal: Jobs

```yaml
jobs:
  build-and-test:        # Maven build + 105 tests
  security-scan:         # Trivy container scan
  docker-build:          # Build 8 imágenes en paralelo
  deploy-staging:        # Deploy automático a staging (si dev/main)
  deploy-production:     # Deploy manual a producción (solo main)
```

### Secretos Requeridos en GitHub

| Secreto | Propósito | Dónde Obtenerlo |
|---------|-----------|-----------------|
| `GHCR_TOKEN` | Push a GitHub Container Registry | Settings → Developer settings → PAT |
| `GCP_SA_KEY` | Service Account de GCP (base64) | GCP Console → IAM |
| `GCP_PROJECT_ID` | ID del proyecto GCP | GCP Console |
| `SNYK_TOKEN` | Análisis de vulnerabilidades | app.snyk.io |
| `SONAR_TOKEN` | Code quality | sonarcloud.io |

### Imágenes Docker Publicadas

```
ghcr.io/alexisj16/api-gateway:dev
ghcr.io/alexisj16/client-service:dev
ghcr.io/alexisj16/case-service:dev
ghcr.io/alexisj16/payment-service:dev
ghcr.io/alexisj16/document-service:dev
ghcr.io/alexisj16/calendar-service:dev
ghcr.io/alexisj16/notification-service:dev
ghcr.io/alexisj16/n8n-integration-service:dev
```

---

## 📊 Stack de Observabilidad (Grafana LGTM)

### Componentes

| Servicio | Puerto | Propósito | Credenciales |
|----------|--------|-----------|--------------|
| **Grafana** | 3100 | Dashboards, visualización | admin / carrillo2025 |
| **Prometheus** | 9090 | Recolección de métricas | - |
| **Loki** | 3101 | Agregación de logs | - |
| **Tempo** | 3102 | Distributed tracing | - |
| **Mimir** | 3103 | Almacenamiento métricas largo plazo | - |
| **Promtail** | - | Recolector de logs Docker | - |
| **Alertmanager** | 9093 | Gestión de alertas | - |

### Iniciar Stack de Observabilidad

```powershell
cd monitoring
docker-compose -f docker-compose.observability.yml up -d

# Verificar estado
docker-compose -f docker-compose.observability.yml ps
```

### URLs de Acceso

| Servicio | URL Local |
|----------|-----------|
| Grafana | http://localhost:3100 |
| Prometheus | http://localhost:9090 |
| Alertmanager | http://localhost:9093 |

### Dashboards Disponibles

- **Carrillo Abogados - Services Overview**: Vista general de todos los microservicios
- **JVM Micrometer**: Métricas de JVM por servicio
- **HTTP Requests**: Latencias, throughput, errores

---

## 📋 Scripts del Proyecto

| Script | Ubicación | Propósito | Ejecución |
|--------|-----------|-----------|-----------|
| `deploy.sh` | scripts/ | Deploy completo a Minikube | `wsl bash -c "./scripts/deploy.sh"` |
| `check.sh` | scripts/ | Verificar prerrequisitos | `wsl bash -c "./scripts/check.sh"` |
| `validate.sh` | scripts/ | Validar pods Running | `wsl bash -c "./scripts/validate.sh"` |
| `test.sh` | scripts/ | Tests funcionales | `wsl bash -c "./scripts/test.sh"` |
| `reset.sh` | scripts/ | Limpiar entorno | `wsl bash -c "./scripts/reset.sh"` |
| `e2e-test.ps1` | scripts/ | Tests E2E desde PowerShell | `.\scripts\e2e-test.ps1` |

---

## 🔧 Troubleshooting

### Pod en CrashLoopBackOff

```bash
# Ver descripción del pod
kubectl describe pod <pod-name> -n carrillo-dev

# Ver logs del crash anterior
kubectl logs <pod-name> -n carrillo-dev --previous

# Causas comunes:
# - Schema de BD no existe → Ejecutar scripts/create-schemas.sql
# - Variable de entorno faltante → Verificar ConfigMap/Secret
# - Puerto ocupado → Verificar que no hay conflictos
# - OOM (Out of Memory) → Aumentar limits en values.yaml
```

### ImagePullBackOff

```bash
# Verificar que la imagen existe en Minikube
minikube ssh -- docker images | grep carrillo

# Si no existe, reconstruir:
eval $(minikube docker-env)
docker build -t carrillo/client-service:0.1.0 ./client-service/

# Verificar imagePullPolicy en deployment
# Debe ser: imagePullPolicy: IfNotPresent (o Never para local)
```

### Service No Accesible

```bash
# Verificar que el service existe
kubectl get svc -n carrillo-dev

# Verificar endpoints (pods conectados al service)
kubectl get endpoints <service-name> -n carrillo-dev

# Si endpoints vacío: pods no están Ready
kubectl get pods -n carrillo-dev -l app=client-service
```

### Minikube Se Detiene Constantemente

```powershell
# Problema común con WSL y cgroups
wsl --shutdown
# Esperar 10-15 segundos
wsl bash -c "minikube start"

# Si persiste, verificar recursos de WSL
wsl bash -c "free -h"
wsl bash -c "df -h"
```

### CORS Errors desde Frontend

```yaml
# Verificar en api-gateway/src/main/resources/application.yml
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowed-origins:
              - "http://localhost:3000"
              - "http://localhost:4200"
```

### Conexión a PostgreSQL Falla

```bash
# Verificar pod de PostgreSQL
kubectl get pods -n databases

# Port-forward para acceso local
kubectl port-forward svc/postgresql 5432:5432 -n databases

# Conectar
psql -h localhost -U carrillo -d carrillo_legal_tech
```

---

## 📋 Checklist: Nuevo Microservicio

### 1. Dockerfile
- [ ] Crear Dockerfile multi-stage
- [ ] Base image: `eclipse-temurin:21-jre-jammy`
- [ ] Healthcheck configurado
- [ ] Usuario no-root

### 2. Docker Compose
- [ ] Añadir servicio a `compose.yml`
- [ ] Configurar puerto, volúmenes, environment
- [ ] Añadir dependencias (postgresql, nats)
- [ ] Healthcheck configurado

### 3. Helm
- [ ] Crear template en `helm-charts/carrillo-abogados/templates/`
- [ ] Añadir valores en `values.yaml`
- [ ] Configurar service, deployment, configmap

### 4. CI/CD
- [ ] Añadir job de build en `ci-cd-pipeline.yml`
- [ ] Configurar push de imagen a ghcr.io

### 5. Documentación
- [ ] Actualizar PROYECTO_ESTADO.md
- [ ] Documentar puertos y endpoints

---

## 📋 Checklist: Release a Staging

- [ ] Todos los tests pasan localmente
- [ ] PR aprobado y merged a dev
- [ ] Pipeline de CI verde
- [ ] Imágenes Docker construidas y pusheadas
- [ ] Verificar secrets en GKE
- [ ] Deploy con Helm
- [ ] Verificar health checks
- [ ] Smoke test manual

---

## 🔗 Referencias

| Documento | Ubicación | Propósito |
|-----------|-----------|-----------|
| Helm Charts | [helm-charts/README.md](../../helm-charts/README.md) | Documentación de charts |
| K8s Manifests | [infrastructure/k8s-manifests/](../../infrastructure/k8s-manifests/) | Manifiestos base |
| Operations | [docs/operations/OPERATIONS.md](../../docs/operations/OPERATIONS.md) | Guía de operaciones |
| Deploy GCP | [docs/operations/DEPLOY_GCP.md](../../docs/operations/DEPLOY_GCP.md) | Guía de deploy a GCP |
| Observability | [docs/operations/OBSERVABILITY_GUIDE.md](../../docs/operations/OBSERVABILITY_GUIDE.md) | Guía de monitoreo |

---

*Agente actualizado: 12 de Enero 2026, 10:30 COT*  
*Proyecto: Carrillo Abogados Legal Tech Platform*
