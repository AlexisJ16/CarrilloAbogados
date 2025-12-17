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
- [Prerrequisitos](#-prerrequisitos)
- [Guía de Uso](#-guía-de-uso)
- [Comandos Útiles](#-comandos-útiles)
- [Troubleshooting](#-troubleshooting)
- [Documentación](#-documentación)

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

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| api-gateway | 8080 | Spring Cloud Gateway + OAuth2 |
| client-service | 8200 | Gestión de clientes |
| case-service | 8300 | Gestión de casos legales |
| payment-service | 8400 | Pagos gubernamentales |
| document-service | 8500 | Documentos legales |
| calendar-service | 8600 | Google Calendar |
| notification-service | 8700 | Email/SMS |
| n8n-integration-service | 8800 | Workflows N8N |

### Stack Tecnológico

| Componente | Versión |
|------------|---------|
| Java | 21 LTS |
| Spring Boot | 3.3.13 |
| Spring Cloud | 2023.0.6 |
| Spring Cloud Kubernetes | 3.1.3 |
| PostgreSQL | 16.2 |
| NATS | 2.10.22 |
| Kubernetes | 1.34.0 |
| Helm | 3.19.2 |
| Minikube | 1.37.0 |

---

## 📋 Prerrequisitos

### Windows con WSL2 (Recomendado)

Todos los comandos se ejecutan **dentro de WSL2**. Desde PowerShell:

```powershell
wsl
```

### Herramientas Requeridas

| Herramienta | Versión Mínima | Instalación (Ubuntu/WSL2) |
|-------------|----------------|---------------------------|
| Java JDK | 21 | `sudo apt install openjdk-21-jdk` |
| Maven | 3.8+ | `sudo apt install maven` |
| Docker | 24+ | `curl -fsSL https://get.docker.com \| sh` |
| kubectl | 1.31+ | Ver [docs](https://kubernetes.io/docs/tasks/tools/) |
| Minikube | 1.32+ | Ver [docs](https://minikube.sigs.k8s.io/docs/start/) |
| Helm | 3.14+ | Ver [docs](https://helm.sh/docs/intro/install/) |

---

## 🚀 Guía de Uso

### Flujo de Trabajo

El proyecto incluye 5 scripts que cubren todo el ciclo de desarrollo:

```
┌─────────┐     ┌──────────┐     ┌────────────┐     ┌────────┐
│  CHECK  │ ──► │  DEPLOY  │ ──► │  VALIDATE  │ ──► │  TEST  │
└─────────┘     └──────────┘     └────────────┘     └────────┘
                                                         │
                      ┌─────────┐                        │
                      │  RESET  │ ◄──────────────────────┘
                      └─────────┘       (cuando sea necesario)
```

### Paso 1: Acceder al Proyecto

```bash
# Desde PowerShell, entrar a WSL2
wsl

# Navegar al proyecto
cd "/mnt/c/Carrillo Abogados/Repositorios GitHub/CarrilloAbogados"

# Dar permisos de ejecución (solo primera vez)
chmod +x scripts/*.sh
```

### Paso 2: Verificar Prerrequisitos

```bash
./scripts/check.sh
```

Verifica que todas las herramientas necesarias estén instaladas correctamente.

**Salida esperada:**
```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  🔧 Carrillo Abogados - Verificación de Prerrequisitos
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

[1/6] Java
  ✓ Java 21 instalado

[2/6] Maven
  ✓ Maven 3.8.x instalado

...

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  ✅ Verificación exitosa: 7/7 verificaciones pasaron
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

### Paso 3: Desplegar la Aplicación

```bash
./scripts/deploy.sh
```

Realiza el deployment completo en 7 pasos:
1. Iniciar Docker daemon
2. Iniciar cluster Minikube
3. Crear namespaces (carrillo-dev, databases, messaging)
4. Desplegar PostgreSQL
5. Desplegar NATS
6. Compilar y construir imágenes Docker
7. Desplegar con Helm

**Tiempo estimado:** 10-15 minutos (primera vez)

### Paso 4: Validar el Deployment

```bash
./scripts/validate.sh
```

Verifica que todos los componentes estén corriendo:
- Cluster Minikube
- Namespaces
- PostgreSQL
- NATS
- Todos los microservicios

Opcionalmente, esperar a que los pods estén ready:

```bash
./scripts/validate.sh --wait
```

### Paso 5: Ejecutar Tests

```bash
./scripts/test.sh
```

Ejecuta tests funcionales:
- Health checks de todos los servicios via API Gateway
- Conectividad a PostgreSQL
- Sistema de mensajería NATS

### Paso 6: Acceder a la Aplicación

Después de un deployment exitoso:

```bash
# Port-forward al API Gateway
kubectl port-forward svc/carrillo-dev-api-gateway 8080:8080 -n carrillo-dev
```

Abrir en el navegador: **http://localhost:8080**

### Limpiar el Entorno

Cuando necesites empezar de cero:

```bash
./scripts/reset.sh
```

⚠️ **Advertencia**: Este script elimina completamente:
- Cluster Minikube
- Imágenes Docker del proyecto
- Directorios `target/` de Maven

---

## 💻 Comandos Útiles

### Kubernetes

```bash
# Ver todos los pods
kubectl get pods -n carrillo-dev

# Ver logs de un servicio
kubectl logs -f deployment/carrillo-dev-api-gateway -n carrillo-dev

# Describir un pod con problemas
kubectl describe pod <pod-name> -n carrillo-dev

# Escalar un servicio
kubectl scale deployment/carrillo-dev-api-gateway --replicas=3 -n carrillo-dev
```

### Base de Datos

```bash
# Conectar a PostgreSQL
kubectl exec -it postgresql-0 -n databases -- psql -U carrillo -d carrillo_legal_tech

# Ver schemas
\dn

# Cambiar a un schema
SET search_path TO clients;
\dt
```

### Minikube

```bash
# Estado del cluster
minikube status

# Dashboard de Kubernetes
minikube dashboard

# SSH al nodo
minikube ssh
```

### Maven

```bash
# Compilar todo (paralelo)
./mvnw clean package -DskipTests -T 1C

# Compilar un servicio específico
./mvnw -pl client-service clean package -DskipTests

# Ejecutar tests
./mvnw test
```

---

## 🔧 Troubleshooting

### Pod en CrashLoopBackOff

```bash
# Ver logs del pod que falla
kubectl logs <pod-name> -n carrillo-dev --previous

# Describir el pod
kubectl describe pod <pod-name> -n carrillo-dev
```

**Causas comunes:**
- Base de datos no disponible → Verificar PostgreSQL
- Configuración incorrecta → Verificar ConfigMaps/Secrets
- Falta de recursos → Aumentar memoria de Minikube

### Docker no responde

```bash
# Iniciar Docker daemon
sudo service docker start

# Verificar
docker info
```

### Minikube con problemas

```bash
# Reiniciar completamente
minikube delete
minikube start --kubernetes-version=v1.34.0 --driver=docker --cpus=4 --memory=7168
```

### Puerto 8080 ocupado

```bash
# Encontrar proceso
lsof -i :8080

# Matar proceso
kill -9 <PID>
```

---

## 📚 Documentación

| Documento | Descripción |
|-----------|-------------|
| [CLAUDE.md](CLAUDE.md) | Contexto completo para AI assistants |
| [docs/architecture/](docs/architecture/) | Arquitectura y ADRs |
| [docs/operations/](docs/operations/) | Guías de operaciones |
| [docs/api/](docs/api/) | Documentación de APIs |

---

## 📜 Licencia

Código propietario - Carrillo Abogados © 2025
