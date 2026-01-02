# 🏛️ Carrillo Abogados - Legal Tech Platform

<div align="center">

[![Build Status](https://github.com/AlexisJ16/CarrilloAbogados/actions/workflows/ci-cd-pipeline.yml/badge.svg)](https://github.com/AlexisJ16/CarrilloAbogados/actions)
[![Security Scan](https://github.com/AlexisJ16/CarrilloAbogados/actions/workflows/security-scan.yml/badge.svg)](https://github.com/AlexisJ16/CarrilloAbogados/actions)
[![Java](https://img.shields.io/badge/Java-21%20LTS-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.13-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-1.34.0-326CE5?logo=kubernetes&logoColor=white)](https://kubernetes.io/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)](https://docs.docker.com/compose/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-Proprietary-red)](LICENSE)

**Plataforma cloud-native de gestión legal empresarial**

[Documentación](#-documentación) • [Arquitectura](#-arquitectura) • [Quick Start](#-quick-start) • [Estado del Proyecto](#-estado-del-proyecto)

</div>

---

## 🎯 Sobre el Proyecto

**Carrillo Abogados Legal Tech** es una plataforma integral de gestión legal diseñada para el bufete **Carrillo ABGD SAS** de Cali, Colombia. Construida con arquitectura de microservicios sobre Kubernetes, ofrece una solución moderna y escalable para la gestión de casos legales, clientes y automatización de procesos.

### Propósito Dual

| 🎓 Académico | 🏢 Empresarial |
|-------------|----------------|
| Proyecto final curso Plataformas II | Sistema en producción para bufete real |
| Demostración de competencias DevOps | Gestión de 7 abogados + 2 administrativos |
| Arquitectura cloud-native | 5 áreas de práctica legal |

### Funcionalidades Principales

| Módulo | Descripción | Estado |
|--------|-------------|--------|
| 👥 **Clientes** | Gestión completa de clientes y leads | ✅ 100% |
| 📁 **Casos Legales** | Expedientes, timeline, contraparte | ✅ 95% |
| 💰 **Pagos** | Pagos gubernamentales (SIC, Cámara) | 🔄 15% |
| 📄 **Documentos** | Almacenamiento seguro legal | 🔄 15% |
| 📅 **Calendario** | Integración Google Calendar | 🔄 15% |
| 🔔 **Notificaciones** | Email/SMS vía Gmail API | 🔄 15% |
| ⚡ **Workflows** | Automatización con n8n Pro | 🔄 20% |

---

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              KUBERNETES CLUSTER                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│    ┌─────────────────────────────────────────────────────────────────┐     │
│    │                      API GATEWAY (:8080)                         │     │
│    │              Spring Cloud Gateway + OAuth2                       │     │
│    └───────────────────────────┬─────────────────────────────────────┘     │
│                                │                                            │
│    ┌───────────────────────────┼───────────────────────────┐               │
│    │                           │                           │               │
│    ▼                           ▼                           ▼               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │ client-svc   │  │  case-svc    │  │ payment-svc  │  │ document-svc │   │
│  │    :8200     │  │    :8300     │  │    :8400     │  │    :8500     │   │
│  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘   │
│                                                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                      │
│  │ calendar-svc │  │ notif-svc    │  │ n8n-integ    │                      │
│  │    :8600     │  │    :8700     │  │    :8800     │                      │
│  └──────────────┘  └──────────────┘  └──────────────┘                      │
│                                                                             │
│    ┌─────────────────────────────────────────────────────────────────┐     │
│    │                         NATS (:4222)                             │     │
│    │                    Event-Driven Messaging                        │     │
│    └─────────────────────────────────────────────────────────────────┘     │
│                                                                             │
├─────────────────────────────────────────────────────────────────────────────┤
│    ┌─────────────────────────────────────────────────────────────────┐     │
│    │                    PostgreSQL 16 (:5432)                         │     │
│    │     clients │ cases │ payments │ documents │ calendar │ notifs  │     │
│    └─────────────────────────────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Stack Tecnológico

<table>
<tr>
<td>

**Backend**
- Java 21 LTS
- Spring Boot 3.3.13
- Spring Cloud 2023.0.6
- Spring Cloud Kubernetes 3.1.3

</td>
<td>

**Infraestructura**
- Kubernetes 1.34.0
- Docker & Docker Compose
- Helm 3.19.2
- Minikube 1.37.0

</td>
<td>

**Datos & Mensajería**
- PostgreSQL 16.2
- NATS 2.10.22
- Flyway Migrations

</td>
</tr>
<tr>
<td>

**Observabilidad**
- Grafana LGTM Stack
- Prometheus + Loki
- Tempo (Tracing)
- Alertmanager

</td>
<td>

**CI/CD & Seguridad**
- GitHub Actions
- Snyk + SonarCloud
- Trivy Scanner
- OAuth2 (Google)

</td>
<td>

**Integraciones**
- Google Workspace
- n8n Pro (Workflows)
- Gmail API
- Google Calendar

</td>
</tr>
</table>

---

## 🚀 Quick Start

### Opción 1: Docker Compose (Recomendado para desarrollo)

```bash
# Clonar repositorio
git clone https://github.com/AlexisJ16/CarrilloAbogados.git
cd CarrilloAbogados

# Levantar todos los servicios
docker-compose up -d

# Verificar estado (esperar ~60 segundos)
docker-compose ps

# Acceder al API Gateway
curl http://localhost:8080/actuator/health
```

### Opción 2: Kubernetes con Minikube

```bash
# Desde WSL2 (Windows)
wsl

# Navegar al proyecto
cd "/mnt/c/Carrillo Abogados/Repositorios GitHub/CarrilloAbogados"

# Dar permisos y ejecutar
chmod +x scripts/*.sh
./scripts/check.sh    # Verificar prerrequisitos
./scripts/deploy.sh   # Despliegue completo (~15 min)
./scripts/validate.sh # Validar deployment
./scripts/test.sh     # Ejecutar tests
```

### Verificación de Servicios

| Servicio | URL | Health Check |
|----------|-----|--------------|
| API Gateway | http://localhost:8080 | `/actuator/health` |
| Client Service | http://localhost:8080/client-service | `/client-service/actuator/health` |
| Case Service | http://localhost:8080/case-service | `/case-service/actuator/health` |
| Lead API | http://localhost:8080/client-service/api/leads | `GET /api/leads` |

---

## 📊 Estado del Proyecto

<div align="center">

| Fase | Estado | Progreso |
|------|--------|----------|
| **Fase 1**: Arquitectura Base | ✅ Completada | 100% |
| **Fase 2**: DevOps & Observabilidad | ✅ Completada | 100% |
| **Fase 3**: QA & Validación E2E | ✅ Completada | 100% |
| **Fase 4**: Servicios de Negocio | ✅ Completada | 100% |
| **Fase 5**: Frontend MVP | ✅ Completada | 100% |
| **MVP Empresarial** | 📋 Planificado | 27 Mar 2026 |

</div>

### Métricas Actuales

| Métrica | Valor |
|---------|-------|
| 🐳 Contenedores Docker | 10/10 Healthy |
| 🔬 Tests de Seguridad | 66 pasando |
| 📦 Microservicios | 8 activos |
| 🎨 Frontend Routes | 11 páginas |
| 🔐 GitHub Secrets | Configurados |
| 📊 CI/CD Workflows | 4 activos |

### Últimos Commits

```
ca46838 feat(qa): add QA agent + E2E validation complete + compose cleanup
8621788 docs: comprehensive documentation audit and agent enhancement
58ebb3d feat(devops): complete observability stack + security CI/CD
43cd864 feat(security): add comprehensive security tests for client-service
```

---

## 📁 Estructura del Proyecto

```
CarrilloAbogados/
├── 📦 api-gateway/              # Spring Cloud Gateway + OAuth2
├── 📦 client-service/           # Gestión de clientes + Lead API ⭐
├── 📦 case-service/             # Gestión de casos legales
├── 📦 payment-service/          # Pagos gubernamentales
├── 📦 document-service/         # Almacenamiento documentos
├── 📦 calendar-service/         # Google Calendar sync
├── 📦 notification-service/     # Email/SMS notifications
├── 📦 n8n-integration-service/  # Bridge n8n workflows
│
├── 🔧 helm-charts/              # Kubernetes Helm charts
├── 🏗️ infrastructure/           # Terraform + K8s manifests
├── 📊 monitoring/               # Grafana LGTM Stack
├── 🛠️ scripts/                  # Shell scripts utilidades
│
├── 📚 docs/                     # Documentación completa
│   ├── business/                # Modelo de negocio, requerimientos
│   ├── architecture/            # ADRs, arquitectura técnica
│   ├── operations/              # Guías operativas
│   └── ai-context/              # Contexto para IAs
│
├── ⚙️ .github/
│   ├── workflows/               # CI/CD pipelines
│   └── copilot-agents/          # 7 agentes especializados
│
├── 🐳 compose.yml               # Docker Compose (desarrollo)
├── 📋 PROYECTO_ESTADO.md        # Estado actual del proyecto
└── 🤖 CLAUDE.md                 # Contexto para Claude AI
```

---

## 📚 Documentación

### Documentos Clave

| Documento | Descripción |
|-----------|-------------|
| [PROYECTO_ESTADO.md](PROYECTO_ESTADO.md) | Estado actual, hitos, métricas |
| [CLAUDE.md](CLAUDE.md) | Contexto técnico completo para IAs |
| [docs/business/](docs/business/) | Modelo de negocio, requerimientos |
| [docs/architecture/](docs/architecture/) | ADRs, decisiones arquitectónicas |

### Agentes Copilot Disponibles

El proyecto incluye **8 agentes especializados** en `.github/copilot-agents/`:

1. **Backend Agent** - Spring Boot, microservicios
2. **Frontend Agent** - React, Next.js, TypeScript
3. **DevOps Agent** - Docker, K8s, CI/CD
4. **Testing Agent** - JUnit, seguridad, E2E, cobertura
5. **Documentation Agent** - Gestión documental y auditoría
6. **Project Manager Agent** - Tracking, métricas, roadmap
7. **QA & Quality Agent** - Depuración, limpieza de código
8. **Business Product Agent** - Requerimientos, negocio, MVP 🆕

---

## 🔐 Seguridad

| Herramienta | Estado | Dashboard |
|-------------|--------|-----------|
| **Snyk** | ✅ Activo | [app.snyk.io](https://app.snyk.io/org/alexisj16) |
| **SonarCloud** | ✅ Activo | [sonarcloud.io](https://sonarcloud.io/project/overview?id=AlexisJ16_CarrilloAbogados) |
| **Trivy** | ✅ CI/CD | GitHub Actions |

---

## 🛠️ Comandos Útiles

<details>
<summary><b>Docker Compose</b></summary>

```bash
docker-compose up -d          # Levantar servicios
docker-compose ps             # Ver estado
docker-compose logs -f        # Ver logs
docker-compose down           # Detener servicios
```

</details>

<details>
<summary><b>Maven Build</b></summary>

```bash
./mvnw clean package -DskipTests -T 1C   # Build rápido
./mvnw test -pl client-service           # Tests de un servicio
./mvnw clean verify                      # Build + tests
```

</details>

<details>
<summary><b>Kubernetes</b></summary>

```bash
kubectl get pods -n carrillo-dev         # Ver pods
kubectl logs -f deployment/api-gateway   # Ver logs
kubectl port-forward svc/api-gateway 8080:8080  # Port forward
```

</details>

---

## 👥 Equipo

| Rol | Persona |
|-----|---------|
| **Desarrollador** | Alexis |
| **Cliente** | Carrillo ABGD SAS |
| **Ubicación** | Cali, Colombia |

---

## 📄 Licencia

Código propietario - **Carrillo Abogados © 2025**

---

<div align="center">

**[⬆ Volver arriba](#-carrillo-abogados---legal-tech-platform)**

*Última actualización: 3 de Enero, 2026*

</div>
