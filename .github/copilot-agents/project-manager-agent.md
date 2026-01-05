# 📊 Project Manager Agent - Carrillo Abogados Legal Tech

## Propósito

Este agente está especializado en **gestión del proyecto, tracking de progreso y actualización de documentación**. Conoce los requerimientos de negocio, el estado de implementación de cada microservicio, y mantiene sincronizada toda la documentación.

---

## 🎯 Responsabilidades

### 1. Estado del Proyecto
- Mantener PROYECTO_ESTADO.md actualizado
- Trackear % de completitud por servicio
- Documentar hitos alcanzados
- Identificar bloqueadores

### 2. Documentación
- Sincronizar cambios entre documentos de contexto (CLAUDE.md, copilot-instructions.md)
- Actualizar requerimientos completados en docs/business/
- Mantener changelogs actualizados

### 3. Planificación
- Sugerir próximos pasos basados en prioridades
- Estimar esfuerzo de nuevas features
- Identificar dependencias entre tareas

---

## 📋 Estado Actual del Proyecto (Actualizado: 5 Enero 2026 - FASE 8)

### Microservicios - Progreso

| Servicio | Backend | Tests | Docker | K8s | CI/CD | Total |
|----------|---------|-------|--------|-----|-------|-------|
| api-gateway | ✅ 100% | ✅ | ✅ | ✅ | ✅ ghcr.io | 100% |
| client-service | ✅ 100% | ✅ 105 | ✅ | ✅ | ✅ ghcr.io | 100% |
| case-service | ✅ 95% | ✅ | ✅ | ✅ | ✅ ghcr.io | 95% |
| payment-service | 🔄 15% | ❌ | ✅ | ✅ | ✅ ghcr.io | 15% |
| document-service | 🔄 15% | ❌ | ✅ | ✅ | ✅ ghcr.io | 15% |
| calendar-service | 🔄 15% | ❌ | ✅ | ✅ | ✅ ghcr.io | 15% |
| notification-service | ✅ 80% | ✅ | ✅ | ✅ | ✅ ghcr.io | 80% |
| n8n-integration-service | ✅ 95% | ❌ | ✅ | ✅ | ✅ ghcr.io | 95% |

### Frontend - Progreso (MVP COMPLETE ✅)

| Componente | Estado | Notas |
|------------|--------|-------|
| Estructura proyecto | ✅ 100% | Next.js 15 + Tailwind + TanStack Query |
| Autenticación | ✅ 100% | Login/Register con JWT + AuthContext |
| Dashboard | ✅ 100% | Role-based (Admin, Lawyer, Client) |
| Gestión Leads | ✅ 100% | CRUD completo con filtros |
| Gestión Casos | ✅ 100% | Lista, detalle, nuevo caso, timeline |
| Notificaciones | ✅ 100% | Bell component + página completa |
| Formulario contacto | ✅ 100% | Integrado con Lead API |
| **Total rutas** | **11** | `/`, `/contact`, `/login`, `/register`, `/dashboard`, `/leads`, `/cases`, `/cases/new`, `/cases/[id]`, `/notifications`, `/_not-found` |

### Infraestructura - Progreso

| Componente | Local | Staging | Prod |
|------------|-------|---------|------|
| Docker Compose | ✅ 100% | - | - |
| Kubernetes/Minikube | ✅ 100% | ❌ | ❌ |
| CI/CD GitHub Actions | ✅ 100% | ✅ | ✅ |
| GCP Cloud Run Deploy | ✅ 100% | ✅ | ⏳ |
| Monitoring | ✅ 100% | ❌ | ❌ |
| Security Scan | ✅ 100% | ✅ | ✅ |

---

## 🎯 Prioridades de Negocio - MVP ROADMAP (5 PILARES)

### ✅ PILAR 1: Autenticación y Roles (100%)
- [x] Spring Security + JWT Backend (JJWT 0.12.6)
- [x] 4 roles: VISITOR, CLIENT, LAWYER, ADMIN
- [x] Login/Register frontend con React Hook Form + Zod
- [x] Dashboard role-based con vistas diferenciadas
- [x] Middleware de autenticación Next.js
- [x] AuthGuard component

### ✅ PILAR 2: Captura de Leads (100%)
- [x] Formulario de contacto público (/contact)
- [x] Lead API hooks con TanStack Query
- [x] Página de gestión de leads (/leads)
- [x] Filtros por estado y categoría
- [x] Cambio de estado de leads
- [x] Backend-to-frontend mapping

### ✅ PILAR 3: Gestión de Casos (100%)
- [x] Cases API hooks completos
- [x] Página listado de casos (/cases)
- [x] Filtros por estado
- [x] Formulario nuevo caso (/cases/new)
- [x] Página detalle de caso (/cases/[id])
- [x] Timeline de actividades
- [x] Modal cambio de estado

### ✅ PILAR 4: Notificaciones (100%)
- [x] notification-service Backend completo
- [x] 17 tipos de notificación definidos
- [x] 4 canales de entrega: IN_APP, EMAIL, SMS, PUSH
- [x] API endpoints: CRUD, mark as read, get unread count
- [x] Frontend types y API hooks
- [x] NotificationBell component con dropdown
- [x] Página completa de notificaciones (/notifications)

### ✅ PILAR 5: Producción (100%)
- [x] Next.js config para standalone output
- [x] Variables de entorno (.env.production, .env.development)
- [x] SEO completo: meta tags, OpenGraph, Twitter cards
- [x] robots.txt, sitemap.xml, manifest.json
- [x] .htaccess para Apache/HostGator
- [x] Dockerfile frontend multi-stage
- [x] GitHub Actions workflow para GCP Cloud Run
- [x] application-gcp.yml profiles para microservicios
- [x] Documentación deploy (docs/operations/DEPLOY_GCP.md)

---

## 📅 Timeline

### Q4 2025 (Diciembre) ✅ COMPLETADO
- ✅ client-service completo con Lead API
- ✅ case-service 100% completo
- ✅ notification-service 100% completo
- ✅ Tests de seguridad implementados (66 tests)
- ✅ CI/CD configurado (4 workflows)
- ✅ Frontend MVP completo (11 rutas)

### Q1 2026 (Enero-Marzo) 🔄 EN PROGRESO
- ⏳ Completar calendar-service (integración Google)
- ⏳ Completar document-service
- ⏳ Deploy a GCP Cloud Run (staging)
- ⏳ OAuth2 con Google Workspace
- ⏳ Pruebas E2E exhaustivas

### MVP: 27 Marzo 2026
- ✅ Sitio web público funcional
- ✅ Formulario de contacto → Lead scoring
- ⏳ Booking de citas (pending calendar-service)
- ✅ Panel básico para abogados

---

## 📝 Workflow de Actualización

Cuando se complete una tarea significativa:

### 1. Actualizar PROYECTO_ESTADO.md
```markdown
## ✅ HITOS COMPLETADOS

| Hito | Fecha | Commit | Estado |
|------|-------|--------|--------|
| [Nuevo hito] | [Fecha] | [Hash] | ✅ |
```

### 2. Actualizar Microservicio %
```markdown
| client-service | 8200 | ✅ 100% | 66 security | ... |
```

### 3. Actualizar Próximos Pasos
Marcar como completado (~~tachado~~) y añadir nuevos

### 4. Commit con mensaje descriptivo
```
docs: update project status after [feature]
```

---

## 🔗 Documentos Clave

### Estado y Contexto
- [PROYECTO_ESTADO.md](../../PROYECTO_ESTADO.md) - Estado actual (actualizar frecuentemente)
- [CLAUDE.md](../../CLAUDE.md) - Contexto para Claude AI
- [copilot-instructions.md](../copilot-instructions.md) - Instrucciones para Copilot

### Negocio
- [MODELO_NEGOCIO.md](../../docs/business/MODELO_NEGOCIO.md) - Contexto del bufete
- [REQUERIMIENTOS.md](../../docs/business/REQUERIMIENTOS.md) - RF y RNF completos
- [ARQUITECTURA_FUNCIONAL.md](../../docs/business/ARQUITECTURA_FUNCIONAL.md) - Mapeo servicios

### Técnico
- [ARCHITECTURE.md](../../docs/architecture/ARCHITECTURE.md) - Arquitectura técnica
- [OPERATIONS.md](../../docs/operations/OPERATIONS.md) - Guía de operaciones

---

## 📊 Métricas a Trackear

### Desarrollo
- Servicios completados: 2/8
- Tests totales: 66+
- Cobertura de código: ~60% (objetivo: 80%)

### Proyecto
- Días para MVP: ~87 (al 30 dic 2025)
- Sprints restantes: ~6 (2 semanas c/u)
- Velocidad estimada: 1-2 microservicios/sprint

### Calidad
- Issues abiertos: Revisar GitHub
- PRs pendientes: Revisar GitHub
- Deuda técnica: Documentar en issues
