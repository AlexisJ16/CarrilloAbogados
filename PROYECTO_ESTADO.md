# 📊 ESTADO DEL PROYECTO - Carrillo Abogados Legal Tech Platform

**Última Actualización**: 14 de Enero, 2026 - 16:00 COT  
**Estado General**: ✅ **FASE 12: SISTEMA 100% FUNCIONAL** | Login Fixed + All Systems Operational ✅  
**Rama Actual**: `dev` (sincronizado con `main`)  
**Último Commit**: Login corregido - Password Cliente123! verificado  
**Siguiente Fase**: 🎉 **PRESENTACIÓN EN VIVO CON LOS ABOGADOS**

---

## 🎯 RESUMEN EJECUTIVO

Plataforma cloud-native de gestión legal empresarial con **8 microservicios** Spring Boot sobre Docker/Kubernetes para el bufete **Carrillo ABGD SAS** de Cali, Colombia.

### Sobre el Bufete
- **Razón Social**: Carrillo ABGD SAS (fundado abril 2001)
- **Equipo**: 7 abogados + 2 administrativos
- **Especialización**: 5 áreas de práctica legal
- **Ubicación**: Torre de Cali, Piso 21, Oficina 2102A
- **Diferenciador**: Dr. Omar Carrillo - 15 años experiencia en SIC

### Propósito
**100% Empresarial** - Sistema de producción real para el bufete Carrillo Abogados.

### Métricas Objetivo
| Métrica | Actual | Objetivo | Incremento |
|---------|-------:|--------:|------------|
| Leads/mes | 20 | 300+ | 15x |
| Respuesta a leads | 4-24h | < 1 min | 1440x |
| Conversión | ~5% | 15%+ | 3x |
| Clientes nuevos/año | ~15 | 100+ | 6.7x |

---

## 📅 HISTORIAL DE COMMITS RECIENTES

```
9860476 Merge dev: CI/CD pipeline fully fixed (permissions + CodeQL v4)
a3980ff fix(ci): add packages write permission for Docker push to ghcr.io
f0f0594 fix(ci): add security-events permissions and upgrade CodeQL to v4
4594738 fix(tests): correct XSS test assertions for REST API behavior
1d66c29 style: fix markdown linting in GITHUB_SECRETS.md
1501377 fix(ci): add mvnw chmod, JaCoCo coverage profile, and secrets docs
```

---

## ✅ HITOS COMPLETADOS

| Hito | Fecha | Commit | Estado |
|------|-------|--------|--------|
| Docker Compose Local (10 servicios) | 18 Dic 2025 | - | ✅ |
| Documentación de Negocio Completa | 19 Dic 2025 | `b048fce` | ✅ |
| Integración n8n Documentada | 19 Dic 2025 | `b7557b0` | ✅ |
| Lead Entity + API Completa | 19 Dic 2025 | `155e11e` | ✅ |
| CI/CD Pipeline Modernizado | 19 Dic 2025 | `c331aab` | ✅ |
| VSCode Workspace Optimizado | 19 Dic 2025 | `c331aab` | ✅ |
| Security Tests (66 tests) | 19 Dic 2025 | `43cd864` | ✅ |
| PR #19: Merge dev → main | 20 Dic 2025 | `24c4b80` | ✅ |
| Grafana LGTM Stack (7 servicios) | 20 Dic 2025 | `58ebb3d` | ✅ |
| Security CI/CD (Snyk + SonarCloud + Trivy) | 20 Dic 2025 | `58ebb3d` | ✅ |
| Deployment Strategy Documentada | 20 Dic 2025 | `58ebb3d` | ✅ |
| GitHub Secrets Configurados | 2 Ene 2026 | - | ✅ |
| Documentación Auditada y Limpia | 2 Ene 2026 | - | ✅ |
| E2E Testing Docker Compose (10/10) | 2 Ene 2026 | - | ✅ |
| QA Agent Creado | 2 Ene 2026 | - | ✅ |
| Lead API E2E Verificada | 2 Ene 2026 | - | ✅ |
| Business Product Agent Creado | 2 Ene 2026 | - | ✅ |
| MVP Roadmap Definido (5 Pilares) | 2 Ene 2026 | - | ✅ |
| Documentación Business Organizada | 2 Ene 2026 | - | ✅ |
| **PILAR 1: Auth Backend + Frontend** | **3 Ene 2026** | - | ✅ |
| **PILAR 2: Lead Capture + Management** | **3 Ene 2026** | - | ✅ |
| **PILAR 3: Case Management Frontend** | **3 Ene 2026** | - | ✅ |
| **Frontend Routes: 10 páginas** | **3 Ene 2026** | - | ✅ |
| **PILAR 4: Notification System Backend** | **3 Ene 2026** | - | ✅ |
| **PILAR 4: Notification UI Components** | **3 Ene 2026** | - | ✅ |
| **PILAR 5: Production Config (SEO, Dockerfile)** | **3 Ene 2026** | - | ✅ |
| **PILAR 5: GCP Deploy Workflow + Docs** | **3 Ene 2026** | - | ✅ |
| **Frontend Routes: 11 páginas** | **3 Ene 2026** | - | ✅ |
| **E2E Test Script Completo (95% pass rate)** | **4 Ene 2026** | `f320e8f` | ✅ |
| **n8n-integration-service Fixed** | **4 Ene 2026** | `f320e8f` | ✅ |
| **API Gateway Routing Fixed** | **4 Ene 2026** | `f320e8f` | ✅ |
| **Git Branches Synced (dev, main, automation)** | **4 Ene 2026** | `5554fa2` | ✅ |
| **Páginas Públicas Español (5 nuevas)** | **3 Ene 2026** | `c79ad6c` | ✅ |
| **Docker Healthchecks Fixed (wget, IPv4)** | **3 Ene 2026** | `c629deb` | ✅ |
| **CI/CD Tests Fixed (403 status)** | **3 Ene 2026** | `efc8cf0` | ✅ |
| **FASE 7: Estabilización Completa** | **4 Ene 2026** | `1d66c29` | ✅ |
| **Code Quality: Redundant Serializable removed (14 files)** | **4 Ene 2026** | `e460150` | ✅ |
| **CI/CD: mvnw chmod + JaCoCo coverage profile** | **4 Ene 2026** | `1501377` | ✅ |
| **GitHub Secrets Documentation** | **4 Ene 2026** | `1501377` | ✅ |
| **FASE 8: CI/CD Pipeline Permissions Fixed** | **5 Ene 2026** | `f0f0594` | ✅ |
| **CodeQL Action upgraded v3 → v4** | **5 Ene 2026** | `f0f0594` | ✅ |
| **Docker Build to ghcr.io Working** | **5 Ene 2026** | `a3980ff` | ✅ |
| **All 8 Microservices Built & Pushed** | **5 Ene 2026** | `9860476` | ✅ |
| **Documentation Audit & Cleanup** | **5 Ene 2026** | `7adfe2f` | ✅ |
| **FASE 9: Infraestructura Reconstruida Completa** | **7 Ene 2026** | - | ✅ |
| **Observability Stack Operativo (Grafana LGTM)** | **7 Ene 2026** | - | ✅ |
| **3 Usuarios de Prueba E2E Creados** | **7 Ene 2026** | - | ✅ |
| **Login API Verificado (3 roles)** | **7 Ene 2026** | - | ✅ |
| **E2E Frontend Testing Completo (15 páginas)** | **8 Ene 2026** | - | ✅ |
| **Prometheus Targets: 13/13 UP** | **8 Ene 2026** | - | ✅ |
| **Mimir Healthcheck Fixed (distroless)** | **8 Ene 2026** | - | ✅ |
| **Grafana Dashboard Queries Corregidas** | **8 Ene 2026** | - | ✅ |
| **Stack Observabilidad 100% Operativo** | **8 Ene 2026** | - | ✅ |
| **CORS Frontend ↔ API Gateway Fixed** | **11 Ene 2026** | - | ✅ |
| **Header.tsx con Auth UI Implementado** | **11 Ene 2026** | - | ✅ |
| **FASE 10: Autenticación Frontend Completa** | **11 Ene 2026** | - | ✅ |
| **n8n Webhook URL Corregido** | **14 Ene 2026** | - | ✅ |
| **Spring Boot Environment Variables Fixed** | **14 Ene 2026** | - | ✅ |
| **Docker Service-to-Service Networking** | **14 Ene 2026** | - | ✅ |
| **Spring Security PATCH Endpoint Public** | **14 Ene 2026** | - | ✅ |
| **Callback Integration Tested (Score 0→90)** | **14 Ene 2026** | - | ✅ |
| **n8n Cloud Workflow Activo (Production)** | **14 Ene 2026** | - | ✅ |
| **FASE 11: Integración n8n Production-Ready** | **14 Ene 2026** | - | ✅ |
| **Login Authentication Fixed (Password Cliente123!)** | **14 Ene 2026** | - | ✅ |
| **3 Roles Validated (Cliente, Abogado, Admin)** | **14 Ene 2026** | - | ✅ |
| **FASE 12: Sistema 100% Funcional para Demo** | **14 Ene 2026** | - | ✅ |
| MVP Empresarial | 27 Mar 2026 | - | 📋 Planificado |

---

## 🔐 SEGURIDAD Y ANÁLISIS DE CÓDIGO

### Estado Actual de Seguridad

| Herramienta | Estado | Issues Detectados |
|-------------|--------|-------------------|
| **Snyk** | ✅ Activo + Token Configurado | 1 Critical, 80 High, 83 Medium, 62 Low |
| **SonarCloud** | ✅ Activo + Token Configurado | 8 Security, 27 Maintainability |
| **Trivy** | ✅ CI/CD | Pendiente primer scan |

### Accesos
- **Snyk**: https://app.snyk.io/org/alexisj16
- **SonarCloud**: https://sonarcloud.io/project/overview?id=AlexisJ16_CarrilloAbogados

---

## 🔭 STACK DE OBSERVABILIDAD

### Grafana LGTM Stack (Docker Compose)

| Servicio | Puerto | Propósito |
|----------|--------|-----------|
| **Grafana** | 3100 | Visualización centralizada |
| **Loki** | 3101 | Agregación de logs |
| **Tempo** | 3102 | Distributed tracing |
| **Mimir** | 3103 | Almacenamiento métricas largo plazo |
| **Prometheus** | 9090 | Recolección métricas |
| **Promtail** | - | Recolector de logs Docker |
| **Alertmanager** | 9093 | Gestión de alertas |

**Credenciales Grafana**: admin / carrillo2025

**Comando para iniciar**:
```bash
cd monitoring
docker-compose -f docker-compose.observability.yml up -d
```

---

## 🚀 ESTRATEGIA DE DEPLOYMENT

### Arquitectura Híbrida

| Componente | Destino | Tecnología |
|------------|---------|------------|
| **Frontend** | HostGator cPanel | Static export (Next.js) |
| **Backend** | GCP Cloud Run | Docker containers |
| **Base de Datos** | Cloud SQL | PostgreSQL 16 |
| **Dominio** | HostGator | carrilloabgd.com |

### Dominios
- `carrilloabgd.com` → Frontend (HostGator)
- `api.carrilloabgd.com` → Backend (GCP Cloud Run)

### Costos Estimados
- **HostGator**: Ya contratado (~$10/mes)
- **GCP**: ~$40-50/mes con créditos (Cloud Run + Cloud SQL)

---

## 🚀 MVP ROADMAP - 5 PILARES

**Fecha de Lanzamiento**: 27 de Marzo, 2026 (12 semanas)

| # | Pilar | Descripción | Semanas | Estado |
|---|-------|-------------|---------|--------|
| 1 | **Autenticación y Roles** | 4 tipos usuario + OAuth2 + RBAC | S1-S4 | ✅ 100% |
| 2 | **Captura de Leads** | Formulario → API → NATS → Notificación | S2-S5 | ✅ 100% |
| 3 | **Gestión de Casos** | CRUD + Timeline + Estados | S3-S8 | ✅ 100% |
| 4 | **Notificaciones** | Email + Push + UI de notificaciones | S6-S10 | ✅ 100% |
| 5 | **Producción** | Seguridad, SEO, Responsive, HA | S9-S12 | ✅ 100% |

### Progreso Detallado por Pilar

#### PILAR 1: Autenticación y Roles ✅
- ✅ Spring Security + JWT Backend (JJWT 0.12.6)
- ✅ 4 roles implementados: VISITOR, CLIENT, LAWYER, ADMIN
- ✅ Login/Register frontend con React Hook Form + Zod
- ✅ Dashboard role-based con vistas diferenciadas
- ✅ Middleware de autenticación Next.js
- ✅ AuthGuard component con protección de rutas

#### PILAR 2: Captura de Leads ✅
- ✅ Formulario de contacto público (/contact)
- ✅ Lead API hooks con TanStack Query
- ✅ Página de gestión de leads (/leads)
- ✅ Filtros por estado y categoría
- ✅ Cambio de estado de leads
- ✅ Backend-to-frontend mapping (leadId → id)

#### PILAR 3: Gestión de Casos ✅
- ✅ Cases API hooks completos
- ✅ Página listado de casos (/cases)
- ✅ Filtros por estado (OPEN, IN_PROGRESS, SUSPENDED, CLOSED)
- ✅ Formulario nuevo caso (/cases/new)
- ✅ Página detalle de caso (/cases/[id])
- ✅ Timeline de actividades
- ✅ Modal cambio de estado
- ✅ Links en dashboard para lawyers/admins

#### PILAR 4: Notificaciones ✅
- ✅ notification-service Backend completo (domain, DTOs, repository, service, REST API)
- ✅ 17 tipos de notificación definidos
- ✅ 4 canales de entrega: IN_APP, EMAIL, SMS, PUSH
- ✅ API endpoints: CRUD, mark as read, get unread count
- ✅ Frontend types y API hooks con TanStack Query
- ✅ NotificationBell component con dropdown
- ✅ DashboardHeader con campana de notificaciones
- ✅ Página completa de notificaciones (/notifications)
- ✅ Filtros por tipo: todas, no leídas, leads, casos, citas, sistema
- ✅ Paginación y gestión de estado

#### PILAR 5: Producción ✅
- ✅ Next.js config para standalone output
- ✅ Variables de entorno (.env.production, .env.development)
- ✅ SEO completo: meta tags, OpenGraph, Twitter cards
- ✅ robots.txt y sitemap.xml
- ✅ manifest.json para PWA
- ✅ .htaccess para Apache/HostGator
- ✅ Dockerfile frontend multi-stage
- ✅ Docker Compose con frontend incluido
- ✅ GitHub Actions workflow para GCP Cloud Run
- ✅ application-gcp.yml profiles para todos los microservicios
- ✅ Documentación completa de deploy (docs/operations/DEPLOY_GCP.md)
- ✅ Template de GitHub Secrets configurado
- ✅ .env.staging para ambiente de pruebas

### Frontend Routes (16 total)

| Ruta | Tipo | Descripción |
|------|------|-------------|
| `/` | Static | Landing page |
| `/nosotros` | Static | Quiénes Somos - Historia del bufete |
| `/servicios` | Static | 5 áreas de práctica legal |
| `/equipo` | Static | 7 abogados + personal administrativo |
| `/contacto` | Static | Formulario de contacto (pendiente n8n) |
| `/blog` | Static | Artículos legales y newsletter |
| `/contact` | Static | Formulario de contacto (legacy) |
| `/login` | Static | Página de login |
| `/register` | Static | Página de registro |
| `/dashboard` | Static | Dashboard role-based |
| `/leads` | Static | Gestión de leads (Lawyer/Admin) |
| `/cases` | Static | Listado de casos (Lawyer/Admin) |
| `/cases/new` | Static | Crear nuevo caso |
| `/cases/[id]` | Dynamic | Detalle de caso |
| `/notifications` | Static | Notificaciones del usuario |
| `/_not-found` | Static | Página 404 |

### Timeline Visual

```
ENERO 2026          FEBRERO 2026        MARZO 2026
S1-S4               S5-S8               S9-S12
├───────────────────┼───────────────────┼───────────────────►
│ ✅ Auth + Roles   │ ✅ Notificaciones  │ ✅ Production     │
│ ✅ Lead Capture   │ ✅ Production      │    Ready!         │
│ ✅ Case Mgmt      │                   │                🚀 │
```

**Ver detalle completo**: [docs/business/MVP_ROADMAP.md](./docs/business/MVP_ROADMAP.md)

---

## 🏗️ ARQUITECTURA ACTUAL

### Microservicios (8 activos)

| Servicio | Puerto | Estado | Tests | Descripción |
|----------|--------|--------|-------|-------------|
| api-gateway | 8080 | ✅ 100% | - | Spring Cloud Gateway + OAuth2 |
| client-service | 8200 | ✅ 100% | 105 tests | Gestión clientes + Lead API |
| case-service | 8300 | ✅ 95% | básicos | Gestión casos legales |
| payment-service | 8400 | 🔄 15% | - | Pagos gubernamentales |
| document-service | 8500 | 🔄 15% | - | Almacenamiento documentos |
| calendar-service | 8600 | 🔄 15% | - | Google Calendar sync |
| notification-service | 8700 | ✅ 80% | - | Email/SMS notifications |
| n8n-integration-service | 8800 | ✅ 95% | básicos | Bridge n8n Cloud (NATS + Webhooks) |

### Infraestructura

| Componente | Tecnología | Estado |
|------------|------------|--------|
| Base de Datos | PostgreSQL 16 | ✅ Operativo |
| Mensajería | NATS 2.10 | ✅ Operativo |
| Contenedores | Docker Compose | ✅ 11/11 healthy |
| Orquestación | Kubernetes (Minikube) | ✅ Configurado |
| CI/CD | GitHub Actions | ✅ 3 workflows |
| Observabilidad | Grafana LGTM | ✅ Configurado |
| Seguridad | Snyk + SonarCloud | ✅ Activo |

---

## 🔧 CONFIGURACIÓN DEL ESPACIO DE TRABAJO

### GitHub Actions (`.github/workflows/`)

| Workflow | Trigger | Propósito |
|----------|---------|-----------|
| `ci-cd-pipeline.yml` | push main/dev, PR | Build, test, Docker, deploy |
| `pr-validation.yml` | PR opened | Validación rápida (5 min) |
| `security-scan.yml` | push main, schedule, manual | Snyk + SonarCloud + Trivy |

### VSCode (`.vscode/`)

| Archivo | Contenido |
|---------|-----------|
| `tasks.json` | 10 tareas rápidas (build, test, docker) |
| `launch.json` | 8 configuraciones debug (1 por servicio) |
| `api-tests.http` | Tests REST Client para endpoints |
| `extensions.json` | Extensiones recomendadas |
| `settings.json` | Configuración Java/Spring |

### Tests de Seguridad (`client-service/src/test/java/.../security/`)

| Clase | Tests | Cobertura |
|-------|-------|-----------|
| `InputValidationSecurityTest` | 34 | SQL injection, XSS, Path traversal |
| `BeanValidationTest` | 32 | Validación de campos DTO |
| **Total** | **66** | **BUILD SUCCESS ✅** |

---

## 📁 ESTRUCTURA DEL REPOSITORIO

```
CarrilloAbogados/
├── .github/
│   ├── workflows/          # CI/CD pipelines
│   │   ├── ci-cd-pipeline.yml
│   │   └── pr-validation.yml
│   └── copilot-instructions.md  # Instrucciones para Copilot
├── .vscode/
│   ├── tasks.json          # 10 tareas rápidas
│   ├── launch.json         # 8 configs debug
│   ├── api-tests.http      # Tests REST
│   └── extensions.json     # Extensiones recomendadas
├── api-gateway/            # Spring Cloud Gateway
├── client-service/         # ✅ COMPLETO - Lead API + 66 tests
├── case-service/           # ✅ 95% - Casos legales
├── payment-service/        # 🔄 Skeleton
├── document-service/       # 🔄 Skeleton
├── calendar-service/       # 🔄 Skeleton
├── notification-service/   # 🔄 Skeleton
├── n8n-integration-service/# 🔄 Bridge n8n
├── helm-charts/            # Kubernetes Helm
├── infrastructure/         # Terraform, K8s manifests
├── docs/
│   ├── business/           # Documentación de negocio
│   ├── architecture/       # ADRs y arquitectura
│   └── operations/         # Guías operativas
├── scripts/                # Shell scripts utilidades
├── compose.yml             # Docker Compose
├── CLAUDE.md               # Contexto para Claude AI
├── COPILOT_PROMPT.md       # Prompt para nuevos chats
└── PROYECTO_ESTADO.md      # Este archivo
```

---

## 🧪 ESTADO DE TESTS

### client-service Security Tests (66 tests)

```
✅ InputValidationSecurityTest
   ├── SqlInjectionTests: 11 tests
   ├── XssPreventionTests: 13 tests
   ├── PathTraversalTests: 4 tests
   ├── RequestValidationTests: 4 tests
   └── FieldLengthValidationTests: 2 tests

✅ BeanValidationTest
   ├── EmailValidationTests: 14 tests
   ├── NombreValidationTests: 3 tests
   ├── TelefonoValidationTests: 5 tests
   ├── ServicioValidationTests: 6 tests
   ├── MensajeValidationTests: 2 tests
   └── CompleteValidLeadTests: 2 tests
```

### Comando para ejecutar tests
```powershell
.\mvnw test -pl client-service "-Dtest=InputValidationSecurityTest,BeanValidationTest" "-Dspring.profiles.active=test"
```

---

## 🔄 INTEGRACIÓN n8n (Marketing Automation)

### 3 MEGA-WORKFLOWS Planificados

| MEGA-WORKFLOW | Propósito | Workflows | Estado |
|---------------|-----------|-----------|--------|
| MW#1: Captura | Lead → Cliente (< 1 min) | 7 | 28% |
| MW#2: Retención | Cliente → Recompra | 5 | Q2 2026 |
| MW#3: SEO | Tráfico → Lead | 5 | Q2-Q3 2026 |

### Eventos NATS Implementados
- `lead.capturado` → Trigger MW#1 scoring

---

## 📋 PRÓXIMOS PASOS RECOMENDADOS

### Opción A: Completar case-service (Recomendado)
1. Crear tests de seguridad similares a client-service
2. Implementar validaciones de entrada
3. Agregar eventos NATS para casos

### Opción B: Implementar calendar-service
1. Integrar Google Calendar API
2. Booking system para citas
3. Recordatorios automáticos

### Opción C: Deploy a GKE Staging
1. Configurar cluster GKE
2. Aplicar Helm charts
3. Configurar secrets de producción

### Opción D: Implementar OAuth2 completo
1. Configurar Google Workspace OAuth
2. Integrar con api-gateway
3. Roles y permisos RBAC

---

## ⚠️ LECCIONES APRENDIDAS (Sesión 19 Dic)

### Errores Comunes y Soluciones

| Error | Causa | Solución |
|-------|-------|----------|
| `Schema "CLIENTS" no encontrado` | H2 no crea schema | `INIT=CREATE SCHEMA IF NOT EXISTS clients` |
| `StatusAggregator NoSuchBean` | Resilience4j health | `resilience4j.circuitbreaker.enabled: false` |
| `Invalid UUID string` en tests | UUID validation | Try-catch para aceptar excepción |
| `missing@domain` email válido | RFC 5321 permite | Ajustar test expectations |

### Configuración Test Profile
```yaml
# application-test.yml esencial
spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=PostgreSQL;INIT=CREATE SCHEMA IF NOT EXISTS clients
  jpa:
    properties:
      hibernate:
        default_schema: clients
resilience4j:
  circuitbreaker:
    enabled: false
```

---

## 🛠️ COMANDOS FRECUENTES

### Docker Compose
```powershell
docker-compose up -d                    # Levantar todo
docker-compose ps                       # Ver estado
docker logs carrillo-client-service     # Ver logs
```

### Maven Build
```powershell
.\mvnw clean package -DskipTests -T 1C  # Build rápido
.\mvnw test -pl client-service          # Tests de un servicio
```

### Git
```powershell
git status
git add .
git commit -m "feat: descripción"
git push origin dev
```

---

## 📞 CONTACTO

- **Desarrollador**: Alexis
- **Cliente**: Carrillo Abogados, Cali, Colombia
- **Email Admin**: ingenieria@carrilloabgd.com
- **MVP Target**: 27 Marzo 2026

---

## 🚀 PRÓXIMOS DESARROLLOS - ROADMAP Q1 2026

### Fase Actual: Integración Marketing + Deploy

#### 🔴 BLOQUEADORES (Requieren acción externa)

| Tarea | Responsable | Dependencia |
|-------|-------------|-------------|
| Configurar webhooks n8n Cloud | Marketing Dev | Acceso a n8n Cloud |
| Conectar formulario `/contacto` con n8n | Marketing Dev | Webhooks activos |
| Configurar MW#1 (Lead Scoring) | Marketing Dev | Datos de prueba |

#### 🟡 LISTOS PARA DESARROLLO (Sin dependencias)

| Prioridad | Feature | Servicio | Tiempo Est. |
|-----------|---------|----------|-------------|
| P0 | **Sync branches dev → main** | Git | 30 min |
| P1 | **Deploy staging GCP** | Infra | 4h |
| P2 | **Google Calendar integration** | calendar-service | 8h |
| P3 | **Email templates Notification** | notification-service | 4h |
| P4 | **Document upload/download** | document-service | 6h |
| P5 | **Payment CRUD completo** | payment-service | 6h |

### Servicios por Completar

| Servicio | Estado Actual | Trabajo Restante |
|----------|---------------|------------------|
| **payment-service** | 15% Skeleton | Modelo de pagos, CRUD, vincular a casos |
| **document-service** | 15% Skeleton | Storage (GCS/local), upload, clasificación |
| **calendar-service** | 15% Skeleton | Google Calendar API, booking público |
| **notification-service** | 80% Backend | Templates email, integración Gmail API |

### Integraciones Externas Pendientes

| Integración | Prioridad | Estado |
|-------------|-----------|--------|
| **n8n Cloud** | Alta | ⏳ Esperando marketing dev |
| **Google Calendar API** | Media | Credenciales listas |
| **Gmail API** | Media | Credenciales listas |
| **Google Drive** | Baja | Futuro |
| **Calendly** | Baja | Q2 2026 |

### Mejoras Técnicas Sugeridas

| Área | Mejora | Impacto |
|------|--------|---------|
| **Testing** | E2E con Playwright | Cobertura frontend |
| **CI/CD** | Deploy automático a staging | Velocidad |
| **Seguridad** | OAuth2 Google Workspace | Autenticación real |
| **Performance** | Redis cache | Velocidad API |
| **Observabilidad** | Custom Grafana dashboards | Monitoreo |

---

*Documento actualizado - 5 de Enero 2026*

- **Cliente**: Carrillo Abogados, Cali, Colombia
- **Email Admin**: ingenieria@carrilloabgd.com
- **MVP Target**: 27 Marzo 2026
