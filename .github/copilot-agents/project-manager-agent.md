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

## 📋 Estado Actual del Proyecto

### Microservicios - Progreso

| Servicio | Backend | Tests | Docker | K8s | Total |
|----------|---------|-------|--------|-----|-------|
| api-gateway | ✅ 100% | ✅ | ✅ | ✅ | 100% |
| client-service | ✅ 100% | ✅ 66 | ✅ | ✅ | 100% |
| case-service | ✅ 95% | 🔄 | ✅ | ✅ | 85% |
| payment-service | 🔄 15% | ❌ | ✅ | ✅ | 15% |
| document-service | 🔄 15% | ❌ | ✅ | ✅ | 15% |
| calendar-service | 🔄 15% | ❌ | ✅ | ✅ | 15% |
| notification-service | 🔄 15% | ❌ | ✅ | ✅ | 15% |
| n8n-integration-service | 🔄 20% | ❌ | ✅ | ✅ | 20% |

### Frontend - Progreso

| Componente | Estado | Notas |
|------------|--------|-------|
| Estructura proyecto | ✅ 100% | Next.js 14 + Tailwind configurado |
| Diseño UI/UX | 🔄 30% | HeroSection, ServicesSection creados |
| Formulario contacto | 🔄 50% | Existe ejemplo funcional |
| Panel admin | ❌ 0% | - |
| Portal cliente | ❌ 0% | - |

### Infraestructura - Progreso

| Componente | Local | Staging | Prod |
|------------|-------|---------|------|
| Docker Compose | ✅ 100% | - | - |
| Kubernetes/Minikube | ✅ 100% | ❌ | ❌ |
| CI/CD GitHub Actions | ✅ 100% | ❌ | ❌ |
| Monitoring | ✅ 100% | ❌ | ❌ |
| Security Scan | ✅ 100% | ✅ | ✅ |

---

## 🎯 Prioridades de Negocio (MoSCoW)

### MUST HAVE (MVP - 27 Marzo 2026)

**Backend:**
- [x] CRUD Clientes (client-service)
- [x] CRUD Casos (case-service)
- [x] Captura de Leads (client-service)
- [ ] Gestión de citas (calendar-service)
- [ ] Notificaciones email (notification-service)

**Frontend:**
- [x] Estructura base Next.js
- [ ] Landing page completa
- [ ] Formulario de contacto funcional
- [ ] Booking de citas
- [ ] Autenticación OAuth2

### SHOULD HAVE (Post-MVP)

- Blog con CMS
- Portal de cliente
- SMS notifications
- Gestión de documentos

---

## 📅 Timeline

### Q4 2025 (Diciembre)
- ✅ client-service completo con Lead API
- ✅ case-service 95% completo
- ✅ Tests de seguridad implementados
- ✅ CI/CD configurado
- 🔄 Iniciar desarrollo frontend

### Q1 2026 (Enero-Marzo)
- Completar calendar-service
- Completar notification-service
- Frontend MVP funcional
- OAuth2 integrado
- Deploy a staging

### MVP: 27 Marzo 2026
- Sitio web público funcional
- Formulario de contacto → Lead scoring
- Booking de citas
- Panel básico para abogados

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
