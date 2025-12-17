# 🏛️ Carrillo Abogados - Legal Tech Platform

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/AlexisJ16/CarrilloAbogados)
[![Java](https://img.shields.io/badge/Java-21%20LTS-orange)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.13-green)](https://spring.io/projects/spring-boot)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-1.34.0-blue)](https://kubernetes.io/)
[![License](https://img.shields.io/badge/License-Proprietary-red)](LICENSE)

Plataforma cloud-native de gestión legal empresarial construida con microservicios Spring Boot sobre Kubernetes.

---

## 📋 Tabla de Contenidos

- [Descripción](#-descripción)
- [Arquitectura](#-arquitectura)
- [Stack Tecnológico](#-stack-tecnológico)
- [Prerrequisitos](#-prerrequisitos)
- [Guía de Instalación Rápida](#-guía-de-instalación-rápida)
- [Guía de Deployment Completo (WSL2)](#-guía-de-deployment-completo-wsl2)
- [Validación del Deployment](#-validación-del-deployment)
- [Comandos Útiles](#-comandos-útiles)
- [Troubleshooting](#-troubleshooting)
- [Documentación](#-documentación)
- [Licencia](#-licencia)

---

## 📖 Descripción

**Carrillo Abogados Legal Tech** es una plataforma integral de gestión legal diseñada para bufetes de abogados. Permite gestionar:

- 👥 **Clientes** - Registro y seguimiento de clientes
- 📁 **Casos legales** - Gestión completa de expedientes
- 💰 **Pagos** - Procesamiento de pagos gubernamentales
- 📄 **Documentos** - Almacenamiento seguro de documentos legales
- 📅 **Calendario** - Integración con Google Calendar
- 🔔 **Notificaciones** - Email/SMS vía Gmail API
- ⚡ **Workflows** - Automatización con N8N Pro

### Propósito Dual
1. **Académico**: Proyecto final curso Plataformas II (Universidad)
2. **Empresarial**: Sistema real para bufete Carrillo Abogados, Cali, Colombia

---

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────────────────────────┐
│                         KUBERNETES CLUSTER                       │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐                                                 │
│  │ API Gateway │ ◄── OAuth2 + Spring Cloud Gateway               │
│  │   :8080     │                                                 │
│  └──────┬──────┘                                                 │
│         │                                                        │
│  ┌──────┴──────┬───────────────┬───────────────┐                │
│  ▼             ▼               ▼               ▼                │
│ ┌────────┐ ┌────────┐ ┌────────────┐ ┌─────────────┐            │
│ │client  │ │case    │ │payment     │ │document     │            │
│ │service │ │service │ │service     │ │service      │            │
│ │:8200   │ │:8300   │ │:8400       │ │:8500        │            │
│ └────────┘ └────────┘ └────────────┘ └─────────────┘            │
│                                                                  │
│ ┌────────────┐ ┌─────────────────┐ ┌───────────────────┐        │
│ │calendar    │ │notification     │ │n8n-integration    │        │
│ │service     │ │service          │ │service            │        │
│ │:8600       │ │:8700            │ │:8800              │        │
│ └────────────┘ └─────────────────┘ └───────────────────┘        │
│                                                                  │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐              ┌──────────────┐                  │
│  │ PostgreSQL  │              │     NATS     │                  │
│  │   :5432     │              │    :4222     │                  │
│  │ (databases) │              │ (messaging)  │                  │
│  └─────────────┘              └──────────────┘                  │
└─────────────────────────────────────────────────────────────────┘
```

### Microservicios

| Servicio | Puerto | Descripción | Estado |
|----------|--------|-------------|--------|
| api-gateway | 8080 | Spring Cloud Gateway + OAuth2 | ✅ Activo |
| client-service | 8200 | Gestión de clientes | ✅ Activo |
| case-service | 8300 | Gestión de casos legales | ✅ Activo |
| payment-service | 8400 | Pagos gubernamentales | ✅ Activo |
| document-service | 8500 | Documentos legales | ⚙️ Skeleton |
| calendar-service | 8600 | Google Calendar | ⚙️ Skeleton |
| notification-service | 8700 | Email/SMS | ⚙️ Skeleton |
| n8n-integration-service | 8800 | Workflows N8N | ⚙️ Skeleton |

---

## 🛠️ Stack Tecnológico

| Componente | Versión | Propósito |
|------------|---------|-----------|
| Java | 21 LTS | Runtime |
| Spring Boot | 3.3.13 | Framework |
| Spring Cloud | 2023.0.6 | Microservices |
| Spring Cloud Kubernetes | 3.1.3 | Service Discovery |
| PostgreSQL | 16.2 | Base de datos |
| NATS | 2.10.22 | Messaging |
| Kubernetes | 1.34.0 | Orquestación |
| Helm | 3.19.2 | Package Manager |
| Minikube | 1.37.0 | Local K8s |

---

## 📋 Prerrequisitos

### Opción A: Windows con WSL2 (Recomendado)

```powershell
# Verificar WSL2
wsl --version

# Debe mostrar: WSL version 2.x.x
```

### Herramientas Requeridas (en WSL2/Linux)

| Herramienta | Versión Mínima | Verificación |
|-------------|----------------|--------------|
| Java JDK | 21 | `java -version` |
| Maven | 3.8+ | `mvn --version` |
| Docker | 24+ | `docker --version` |
| kubectl | 1.31+ | `kubectl version --client` |
| Minikube | 1.32+ | `minikube version` |
| Helm | 3.14+ | `helm version` |

### Instalación de Herramientas (Ubuntu/WSL2)

```bash
# Java 21
sudo apt update
sudo apt install openjdk-21-jdk -y
echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64' >> ~/.bashrc
source ~/.bashrc

# Docker
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER
newgrp docker

# kubectl
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl

# Minikube
curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
sudo install minikube-linux-amd64 /usr/local/bin/minikube

# Helm
curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash
```

---

## ⚡ Guía de Instalación Rápida

Para usuarios con todas las herramientas instaladas:

### Desde Windows (PowerShell)

```powershell
# 1. Entrar a WSL2
wsl

# El resto de comandos se ejecutan dentro de WSL2
```

### Dentro de WSL2

```bash
# 2. Navegar al proyecto
cd "/mnt/c/Carrillo Abogados/Repositorios GitHub/CarrilloAbogados"

# 3. Dar permisos a scripts
chmod +x scripts/*.sh

# 4. Deployment automático (todo en uno)
./scripts/wsl-deploy.sh

# 5. Acceder a la aplicación (en otra terminal WSL)
kubectl port-forward svc/api-gateway 8080:8080 -n carrillo-dev
# Abrir: http://localhost:8080
```

> ⚠️ **Importante**: Los comandos de Kubernetes deben ejecutarse dentro de una sesión WSL2 persistente, no desde PowerShell directo.

---

## 🚀 Guía de Deployment Completo (WSL2)

Esta guía paso a paso te llevará desde cero hasta tener la plataforma corriendo localmente.

### Paso 1: Acceder a WSL2

```powershell
# Desde PowerShell de Windows
wsl
```

### Paso 2: Navegar al Proyecto

```bash
# El proyecto de Windows está montado en /mnt/c/
cd "/mnt/c/Carrillo Abogados/Repositorios GitHub/CarrilloAbogados"

# Verificar que estás en el directorio correcto
ls -la
# Debe mostrar: pom.xml, CLAUDE.md, api-gateway/, etc.
```

### Paso 3: Verificar Prerrequisitos

```bash
# Ejecutar script de verificación
chmod +x scripts/*.sh
./scripts/check-env.sh
```

Salida esperada:
```
✓ Java 21
✓ Docker 29.x.x
✓ kubectl v1.34.x
✓ Minikube v1.37.x
✓ Helm v3.19.x
✓ Maven 3.8.x
```

### Paso 4: Iniciar Docker

```bash
# Verificar que Docker está corriendo
docker ps

# Si hay error, iniciar Docker daemon
sudo service docker start

# Verificar nuevamente
docker info
```

### Paso 5: Iniciar Minikube

```bash
# Iniciar cluster con recursos suficientes
minikube start \
    --kubernetes-version=v1.34.0 \
    --driver=docker \
    --cpus=4 \
    --memory=7168 \
    --disk-size=30g \
    --addons=ingress,metrics-server,dashboard

# Verificar estado
minikube status
```

Salida esperada:
```
minikube
type: Control Plane
host: Running
kubelet: Running
apiserver: Running
kubeconfig: Configured
```

### Paso 6: Configurar Docker de Minikube

```bash
# Conectar al Docker daemon de Minikube
eval $(minikube docker-env)

# Verificar conexión
docker ps
# Debe mostrar containers de Kubernetes
```

### Paso 7: Crear Namespaces

```bash
# Aplicar namespaces
kubectl apply -f infrastructure/k8s-manifests/namespaces/

# Verificar
kubectl get namespaces
```

Debe mostrar: `carrillo-dev`, `databases`, `messaging`

### Paso 8: Desplegar PostgreSQL

```bash
# Agregar repositorio Bitnami
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update

# Instalar PostgreSQL
helm install postgresql bitnami/postgresql \
    --namespace databases \
    --set auth.username=carrillo \
    --set auth.password=CarrilloAbogados2025! \
    --set auth.database=carrillo_legal_tech \
    --set primary.persistence.size=8Gi \
    --set primary.resources.requests.memory=512Mi \
    --set primary.resources.requests.cpu=250m

# Esperar a que esté ready (2-3 minutos)
kubectl wait --for=condition=ready pod -l app.kubernetes.io/name=postgresql -n databases --timeout=300s

# Verificar
kubectl get pods -n databases
```

### Paso 9: Desplegar NATS

```bash
# Agregar repositorio NATS
helm repo add nats https://nats-io.github.io/k8s/helm/charts/
helm repo update

# Instalar NATS
helm install nats nats/nats \
    --namespace messaging \
    --set nats.jetstream.enabled=true \
    --set nats.jetstream.memStorage.enabled=true \
    --set nats.jetstream.memStorage.size=1Gi \
    --set replicaCount=1

# Esperar a que esté ready
kubectl wait --for=condition=ready pod -l app.kubernetes.io/name=nats -n messaging --timeout=120s

# Verificar
kubectl get pods -n messaging
```

### Paso 10: Compilar el Proyecto

```bash
# Configurar JAVA_HOME si no está configurado
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64

# Compilar todos los módulos (en paralelo)
./mvnw clean package -DskipTests -T 1C
```

Salida esperada:
```
[INFO] BUILD SUCCESS
[INFO] Total time: X min
```

### Paso 11: Construir Imágenes Docker

```bash
# Asegurarse de estar conectado al Docker de Minikube
eval $(minikube docker-env)

# Construir imágenes para cada microservicio
for service in api-gateway client-service case-service payment-service document-service calendar-service notification-service n8n-integration-service user-service; do
    if [ -d "$service" ]; then
        echo "Building $service..."
        docker build -t carrillo/$service:latest -f $service/Dockerfile $service/
    fi
done

# Verificar imágenes creadas
docker images | grep carrillo
```

### Paso 12: Desplegar con Helm

```bash
# Desplegar todos los microservicios
helm install carrillo-dev helm-charts/carrillo-abogados/ --namespace carrillo-dev

# Ver estado del deployment
helm status carrillo-dev -n carrillo-dev

# Esperar a que los pods estén ready (3-5 minutos)
kubectl get pods -n carrillo-dev -w
```

### Paso 13: Verificar Deployment

```bash
# Ver todos los pods
kubectl get pods -A | grep -E "(carrillo-dev|databases|messaging)"

# Ver servicios
kubectl get svc -n carrillo-dev
```

Salida esperada:
```
NAMESPACE      NAME                        READY   STATUS
carrillo-dev   api-gateway-xxx             1/1     Running
carrillo-dev   client-service-xxx          1/1     Running
carrillo-dev   case-service-xxx            1/1     Running
databases      postgresql-0                1/1     Running
messaging      nats-0                      1/1     Running
```

### Paso 14: Acceder a la Aplicación

```bash
# Opción 1: Port-forward (recomendado)
kubectl port-forward svc/api-gateway 8080:8080 -n carrillo-dev

# En otra terminal, probar la conexión
curl http://localhost:8080/actuator/health
```

```bash
# Opción 2: Minikube tunnel (para acceso externo)
minikube tunnel
```

### Paso 15: Acceder al Dashboard

```bash
# Abrir dashboard de Kubernetes
minikube dashboard
```

---

## ✅ Validación del Deployment

### Verificar Health de Servicios

```bash
# API Gateway
curl http://localhost:8080/actuator/health

# Client Service (a través del Gateway)
curl http://localhost:8080/client-service/actuator/health

# Case Service
curl http://localhost:8080/case-service/actuator/health
```

### Verificar Base de Datos

```bash
# Conectar a PostgreSQL
kubectl exec -it postgresql-0 -n databases -- psql -U carrillo -d carrillo_legal_tech

# Dentro de psql:
\dn                    # Listar schemas
\dt                    # Listar tablas
\q                     # Salir
```

### Verificar Logs

```bash
# Logs del API Gateway
kubectl logs -f deployment/api-gateway -n carrillo-dev

# Logs de un servicio específico
kubectl logs -f deployment/client-service -n carrillo-dev
```

### Verificar Rutas del Gateway

```bash
curl http://localhost:8080/actuator/gateway/routes
```

---

## 📝 Comandos Útiles

### Gestión del Cluster

```bash
# Estado de Minikube
minikube status

# Pausar/Reanudar (ahorra recursos)
minikube pause
minikube unpause

# Detener cluster
minikube stop

# Eliminar cluster (reset completo)
minikube delete
```

### Gestión de Pods

```bash
# Ver pods en tiempo real
kubectl get pods -n carrillo-dev -w

# Describir pod (debugging)
kubectl describe pod <pod-name> -n carrillo-dev

# Logs con tail
kubectl logs -f --tail=100 deployment/api-gateway -n carrillo-dev

# Ejecutar comando en pod
kubectl exec -it <pod-name> -n carrillo-dev -- /bin/sh
```

### Gestión de Helm

```bash
# Ver releases instalados
helm list -n carrillo-dev

# Actualizar deployment
helm upgrade carrillo-dev helm-charts/carrillo-abogados/ -n carrillo-dev

# Desinstalar
helm uninstall carrillo-dev -n carrillo-dev
```

### Acceso a Base de Datos

```bash
# Port-forward a PostgreSQL
kubectl port-forward svc/postgresql 5432:5432 -n databases

# Desde otra terminal, conectar con cliente local
psql -h localhost -U carrillo -d carrillo_legal_tech
```

---

## 🔧 Troubleshooting

### Error: "Cannot connect to Docker daemon"

```bash
# Iniciar Docker
sudo service docker start

# Verificar que usuario está en grupo docker
groups
# Si no aparece 'docker':
sudo usermod -aG docker $USER
newgrp docker
```

### Error: "ImagePullBackOff"

```bash
# Verificar que estás usando Docker de Minikube
eval $(minikube docker-env)

# Reconstruir imagen
docker build -t carrillo/<service>:latest -f <service>/Dockerfile <service>/

# Reiniciar pod
kubectl delete pod <pod-name> -n carrillo-dev
```

### Error: "CrashLoopBackOff"

```bash
# Ver logs del pod
kubectl logs <pod-name> -n carrillo-dev --previous

# Causas comunes:
# 1. Database no disponible
# 2. ConfigMap/Secret faltante
# 3. Puerto ya en uso
```

### Error: "Insufficient memory"

```bash
# Detener Minikube
minikube stop

# Reiniciar con más memoria
minikube start --memory=8192
```

### Error: "Connection refused" al hacer port-forward

```bash
# Verificar que el servicio existe
kubectl get svc -n carrillo-dev

# Verificar que el pod está Running
kubectl get pods -n carrillo-dev

# Verificar logs del pod
kubectl logs deployment/api-gateway -n carrillo-dev
```

### Reset Completo

```bash
# Eliminar todo y empezar desde cero
./scripts/nuke-environment.sh

# O manualmente:
minikube delete
docker system prune -a
```

---

## 📚 Documentación

| Documento | Descripción |
|-----------|-------------|
| [docs/README.md](docs/README.md) | Índice maestro de documentación |
| [docs/architecture/ARCHITECTURE.md](docs/architecture/ARCHITECTURE.md) | Arquitectura del sistema |
| [docs/operations/OPERATIONS.md](docs/operations/OPERATIONS.md) | Guía de operaciones |
| [CLAUDE.md](CLAUDE.md) | Contexto para Claude Code |
| [PROYECTO_ESTADO.md](PROYECTO_ESTADO.md) | Estado actual del proyecto |

---

## 👥 Equipo

- **Desarrollador**: Alexis
- **Cliente**: Carrillo Abogados, Cali, Colombia
- **Contacto técnico**: ingenieria@carrilloabgd.com

---

## 📄 Licencia

Propietario © 2024-2025 Carrillo Abogados. Todos los derechos reservados.

---

<p align="center">
  <sub>Built with ❤️ using Spring Boot, Kubernetes, and Cloud-Native technologies</sub>
</p>
