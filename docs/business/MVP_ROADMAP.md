# 🚀 MVP ROADMAP - Carrillo Abogados Legal Tech Platform

**Versión**: 1.0  
**Fecha de Creación**: 2 de Enero, 2026  
**Última Actualización**: 16 de Febrero, 2026
**Estado**: ✅ Plan Definido  
**Fecha de Lanzamiento MVP**: **27 de Marzo, 2026**

---

## 📋 RESUMEN EJECUTIVO

Este documento define el **roadmap completo del MVP** para la plataforma Carrillo Abogados Legal Tech. El MVP se enfoca en las **5 funcionalidades críticas** necesarias para ejecutar el plan de marketing y relanzamiento tecnológico de la empresa.

### Objetivo del MVP
> **Capturar leads desde el portal web, clasificarlos automáticamente mediante n8n, y visualizar la información en dashboards para la toma de decisiones, garantizando alta disponibilidad y seguridad.**

### Tiempo hasta MVP: **~12 semanas** (2 Ene - 27 Mar 2026)

---

## 🎯 LAS 5 FUNCIONALIDADES CORE DEL MVP

### Visión General

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    5 PILARES DEL MVP CARRILLO ABOGADOS                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │
│  │   PILAR 1       │  │   PILAR 2       │  │   PILAR 3       │            │
│  │                 │  │                 │  │                 │            │
│  │  AUTENTICACIÓN  │  │  CAPTURA LEADS  │  │  INTEGRACIÓN    │            │
│  │   Y ROLES       │  │  (Formulario)   │  │     N8N         │            │
│  │                 │  │                 │  │                 │            │
│  │  4 tipos usuario│  │  Lead API       │  │  Scoring auto   │            │
│  │  OAuth2 Google  │  │  Notificaciones │  │  Clasificación  │            │
│  │  RBAC por rol   │  │  Almacenamiento │  │  Respuesta IA   │            │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘            │
│                                                                             │
│  ┌─────────────────┐  ┌─────────────────┐                                  │
│  │   PILAR 4       │  │   PILAR 5       │                                  │
│  │                 │  │                 │                                  │
│  │  DASHBOARDS     │  │  PRODUCCIÓN     │                                  │
│  │  INTERACTIVOS   │  │  PROFESIONAL    │                                  │
│  │                 │  │                 │                                  │
│  │  KPIs y métricas│  │  Alta disponib. │                                  │
│  │  Visualización  │  │  Seguridad      │                                  │
│  │  Toma decisiones│  │  SEO + Responsive│                                  │
│  └─────────────────┘  └─────────────────┘                                  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 📊 PILAR 1: Sistema de Autenticación y Roles

### Descripción
Los 4 tipos de usuario deben poder iniciar sesión (excepto Visitante) y acceder únicamente a las funciones que corresponden a su rol.

### Los 4 Tipos de Usuario

| Rol | Autenticación | Acceso | Funciones Principales |
|-----|---------------|--------|----------------------|
| **Visitante** | ❌ No requerida | Portal público | Navegar, formulario contacto, solicitar cita |
| **Cliente** | ✅ Email/OAuth2 | Portal cliente | Ver sus casos, subir documentos, mensajes |
| **Abogado** | ✅ OAuth2 @carrilloabgd.com | Panel interno | Gestionar casos, clientes, calendario |
| **Administrador** | ✅ OAuth2 @carrilloabgd.com | Acceso total | Configuración, usuarios, contenido, workflows |

### Requerimientos Técnicos

| ID | Requerimiento | Microservicio | Prioridad | Estado |
|----|---------------|---------------|-----------|--------|
| AUTH-001 | Login OAuth2 con Google Workspace | api-gateway | MUST | 🔄 30% |
| AUTH-002 | Validar dominio @carrilloabgd.com para abogados | api-gateway | MUST | ⏳ |
| AUTH-003 | Sistema RBAC con 4 roles | client-service | MUST | 🔄 20% |
| AUTH-004 | Persistencia de sesión (JWT) | api-gateway | MUST | ⏳ |
| AUTH-005 | Login de clientes por email | client-service | MUST | ⏳ |
| AUTH-006 | Recuperación de contraseña | notification-service | SHOULD | ⏳ |
| AUTH-007 | Verificación de email | notification-service | SHOULD | ⏳ |

### Implementación

```java
// Estructura de roles en el sistema
public enum UserRole {
    VISITOR,      // Sin autenticación - acceso público
    CLIENT,       // Cliente del bufete - acceso limitado
    LAWYER,       // Abogado @carrilloabgd.com - acceso casos
    ADMIN         // Administrador - acceso total
}

// Permisos por rol
VISITOR:  [READ_PUBLIC]
CLIENT:   [READ_OWN_CASES, UPLOAD_DOCUMENTS, SEND_MESSAGES]
LAWYER:   [CRUD_ASSIGNED_CASES, CRUD_ASSIGNED_CLIENTS, MANAGE_CALENDAR]
ADMIN:    [ALL_PERMISSIONS]
```

### Criterios de Aceptación

- [ ] Usuario puede iniciar sesión con Google Workspace
- [ ] Sistema valida dominio @carrilloabgd.com para abogados
- [ ] Cada rol solo ve las funciones que le corresponden
- [ ] Token JWT se refresca automáticamente
- [ ] Logout cierra sesión en todos los dispositivos

---

## 📊 PILAR 2: Captura de Leads (Formulario)

### Descripción
Registrar leads a través del formulario del portal, capturar su información, guardarla y notificar a través de los canales establecidos.

### Flujo de Captura

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        FLUJO DE CAPTURA DE LEAD                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  [1] VISITANTE                                                             │
│      └── Llena formulario de contacto en portal web                        │
│              │                                                              │
│              ▼                                                              │
│  [2] FRONTEND (Next.js)                                                    │
│      ├── Validación client-side (campos requeridos, formato email)         │
│      └── POST /api/leads                                                   │
│              │                                                              │
│              ▼                                                              │
│  [3] API GATEWAY                                                           │
│      └── Enruta a client-service                                           │
│              │                                                              │
│              ▼                                                              │
│  [4] CLIENT-SERVICE                                                        │
│      ├── Valida datos server-side (66 tests de seguridad)                  │
│      ├── Guarda en PostgreSQL (schema: clients, tabla: leads)              │
│      ├── Emite evento NATS: lead.capturado                                 │
│      └── Retorna HTTP 201 Created                                          │
│              │                                                              │
│              ▼                                                              │
│  [5] NOTIFICATION-SERVICE                                                  │
│      ├── Escucha evento NATS                                               │
│      ├── Envía email confirmación al lead                                  │
│      └── Notifica a admin@carrilloabgd.com                                 │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Campos del Formulario

| Campo | Tipo | Requerido | Validación |
|-------|------|-----------|------------|
| Nombre completo | String | ✅ | 2-100 caracteres |
| Email | String | ✅ | Formato email válido |
| Teléfono | String | ❌ | Formato colombiano |
| Empresa | String | ❌ | 2-100 caracteres |
| Cargo | String | ❌ | Texto libre |
| Servicio de interés | Enum | ✅ | Dropdown (5 áreas) |
| Mensaje | String | ✅ | 10-2000 caracteres |
| Acepta política privacidad | Boolean | ✅ | true |

### Servicios de Interés (Dropdown)

1. Derecho Administrativo / Contratación Estatal
2. Derecho de Competencias (Libre Competencia)
3. Derecho Corporativo / Empresarial
4. Derecho de Telecomunicaciones
5. Registro de Marcas / Propiedad Industrial

### Estado Actual de Implementación

| Componente | Estado | Notas |
|------------|--------|-------|
| Lead Entity | ✅ 100% | Modelo completo con score |
| Lead API (CRUD) | ✅ 100% | POST, GET, LIST funcionando |
| Eventos NATS | ✅ 100% | lead.capturado implementado |
| Tests de seguridad | ✅ 100% | 66 tests pasando |
| Frontend formulario | 🔄 10% | Estructura creada |
| Notificaciones | 🔄 15% | Skeleton |

---

## 📊 PILAR 3: Integración n8n (Clasificación Inteligente)

### Descripción
Las integraciones con n8n Cloud permiten clasificar leads automáticamente, calcular scoring, y proporcionar métricas de calidad para la toma de decisiones.

### Arquitectura de Integración

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                   INTEGRACIÓN PLATAFORMA ↔ N8N CLOUD                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  PLATAFORMA WEB                          N8N CLOUD                          │
│  ┌───────────────────────┐               ┌───────────────────────┐         │
│  │                       │               │                       │         │
│  │  client-service       │               │  MW#1: CAPTURA        │         │
│  │  ┌─────────────┐     │    NATS       │  ┌─────────────┐     │         │
│  │  │ Lead API    │──────┼──────────────►│  │ SUB-A       │     │         │
│  │  └─────────────┘     │ lead.capturado │  │ Intake      │     │         │
│  │                       │               │  └──────┬──────┘     │         │
│  │  n8n-integration     │               │         │            │         │
│  │  ┌─────────────┐     │   Webhook     │  ┌──────▼──────┐     │         │
│  │  │ Bridge      │◄─────┼──────────────│  │ SCORING     │     │         │
│  │  └──────┬──────┘     │ /lead-scored  │  │ HOT/WARM/COLD│     │         │
│  │         │            │               │  └──────┬──────┘     │         │
│  │  ┌──────▼──────┐     │               │         │            │         │
│  │  │ Actualizar  │     │               │  ┌──────▼──────┐     │         │
│  │  │ leadScore   │     │               │  │ RESPUESTA IA │     │         │
│  │  │ leadCategory│     │               │  │ (Gemini)    │     │         │
│  │  └─────────────┘     │               │  └─────────────┘     │         │
│  │                       │               │                       │         │
│  └───────────────────────┘               └───────────────────────┘         │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Lead Scoring (Calculado por n8n)

| Criterio | Puntos | Descripción |
|----------|-------:|-------------|
| Base (lead capturado) | +30 | Todos los leads inician aquí |
| Servicio "marca" o "litigio" | +20 | Alta intención comercial |
| Mensaje > 50 caracteres | +10 | Descripción detallada = más interés |
| Tiene teléfono | +10 | Mayor contactabilidad |
| Tiene empresa | +10 | Cliente B2B potencial |
| Email corporativo (no @gmail) | +10 | Profesionalismo |
| Cargo C-Level (CEO, CTO, etc.) | +20 | Poder de decisión |
| **MÁXIMO POSIBLE** | **100** | Lead ideal |

### Categorías y Acciones Automáticas

| Categoría | Score | Color | Acción Automática |
|-----------|------:|-------|-------------------|
| **HOT** 🔥 | ≥70 | Rojo | Notificación inmediata + Email IA personalizado |
| **WARM** 🟡 | 40-69 | Amarillo | Email IA + Secuencia nurturing |
| **COLD** ⚪ | <40 | Gris | Respuesta genérica + Nurturing básico |

### Endpoints de Integración

**Plataforma → n8n:**
```
NATS Event: lead.capturado
Payload: { leadId, nombre, email, telefono, empresa, cargo, servicio, mensaje, createdAt }
```

**n8n → Plataforma:**
```
POST /n8n-integration-service/webhook/lead-scored
Body: { leadId, score, category, aiResponse, processedAt }

POST /n8n-integration-service/webhook/lead-hot
Body: { leadId, urgency, assignedLawyerId }
```

### Estado de Implementación

| Componente | Estado | Responsable |
|------------|--------|-------------|
| NATS events (lead.capturado) | ✅ 100% | Backend |
| n8n-integration-service bridge | 🔄 20% | Backend |
| Webhook /lead-scored | ⏳ Pendiente | Backend |
| MW#1 Orquestador | 🟡 Activo | Marketing |
| SUB-A Intake & Scoring | 🟡 Validado | Marketing |
| SUB-B Hot Notification | 📋 Especificado | Marketing |
| SUB-C Email IA | 📋 Especificado | Marketing |

---

## 📊 PILAR 4: Dashboards Interactivos

### Descripción
Visualización de información clave a través del portal mediante dashboards y tableros interactivos, intuitivos y útiles para la toma de decisiones.

### Dashboard Principal (Administrador)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     DASHBOARD ADMINISTRADOR                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                         MÉTRICAS KPI                                 │   │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐           │   │
│  │  │ LEADS    │  │ HOT      │  │ WARM     │  │ COLD     │           │   │
│  │  │ TOTALES  │  │          │  │          │  │          │           │   │
│  │  │          │  │          │  │          │  │          │           │   │
│  │  │   127    │  │    23    │  │    58    │  │    46    │           │   │
│  │  │  +15%    │  │   +8%    │  │   +12%   │  │   -5%    │           │   │
│  │  └──────────┘  └──────────┘  └──────────┘  └──────────┘           │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────┐  ┌─────────────────────────────────────┐ │
│  │   LEADS POR SERVICIO        │  │   LEADS RECIENTES                   │ │
│  │   (Gráfico de torta)        │  │   ┌─────────────────────────────┐   │ │
│  │                             │  │   │ 🔥 Juan Pérez - Marcas      │   │ │
│  │   🟦 Marcas: 45%            │  │   │    Score: 85 | HOT          │   │ │
│  │   🟨 Corporativo: 25%       │  │   │    Hace 5 minutos           │   │ │
│  │   🟧 Admin: 15%             │  │   ├─────────────────────────────┤   │ │
│  │   🟩 Telecom: 10%           │  │   │ 🟡 María López - Corp       │   │ │
│  │   🟪 Competencias: 5%       │  │   │    Score: 55 | WARM         │   │ │
│  │                             │  │   │    Hace 20 minutos          │   │ │
│  └─────────────────────────────┘  │   └─────────────────────────────┘   │ │
│                                   └─────────────────────────────────────┘ │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    TENDENCIA SEMANAL                                 │   │
│  │   📈 Gráfico de líneas: Leads por día (últimos 7 días)              │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Métricas a Mostrar

| Métrica | Descripción | Fórmula/Fuente |
|---------|-------------|----------------|
| **Leads Totales** | Total de leads capturados | COUNT(leads) |
| **Leads HOT** | Leads con score ≥ 70 | COUNT(leads WHERE category='HOT') |
| **Leads WARM** | Leads con score 40-69 | COUNT(leads WHERE category='WARM') |
| **Leads COLD** | Leads con score < 40 | COUNT(leads WHERE category='COLD') |
| **Tasa de Conversión** | % leads → clientes | clientes/leads * 100 |
| **Tiempo Respuesta** | Promedio primera respuesta | AVG(responseTime) |
| **Leads por Servicio** | Distribución por área | GROUP BY servicio |
| **Tendencia Diaria** | Leads por día | GROUP BY fecha |

### Componentes Frontend

| Componente | Tecnología | Descripción |
|------------|------------|-------------|
| KPI Cards | React + Tailwind | Tarjetas con métricas principales |
| Pie Chart | Chart.js / Recharts | Distribución por servicio |
| Line Chart | Chart.js / Recharts | Tendencia temporal |
| Lead Table | TanStack Table | Lista de leads con filtros |
| Real-time Updates | WebSocket / Polling | Actualización automática |

---

## 📊 PILAR 5: Producción Profesional

### Descripción
Garantizar un despliegue y estado de producción limpio, con alta disponibilidad, seguridad robusta, optimización SEO, diseño responsivo y las mejores prácticas.

### Checklist de Producción

#### Seguridad
- [ ] HTTPS/TLS en todos los endpoints
- [ ] OAuth2 con Google Workspace
- [ ] RBAC implementado
- [ ] Rate limiting activo
- [ ] Headers de seguridad (CORS, CSP, etc.)
- [ ] Secrets en Kubernetes Secrets (no en código)
- [ ] Escaneo Snyk sin vulnerabilidades críticas
- [ ] SonarCloud sin issues de seguridad

#### Alta Disponibilidad
- [ ] Kubernetes en GKE Autopilot
- [ ] Mínimo 2 réplicas por servicio crítico
- [ ] Health checks configurados
- [ ] Circuit breaker activo
- [ ] Backup diario de PostgreSQL
- [ ] Monitoring con Grafana LGTM

#### SEO
- [ ] Meta tags optimizados
- [ ] Sitemap.xml generado
- [ ] Robots.txt configurado
- [ ] Schema.org markup (LocalBusiness)
- [ ] Open Graph tags
- [ ] Velocidad de carga < 3s (Lighthouse 90+)

#### Responsive Design
- [ ] Mobile-first approach
- [ ] Breakpoints: mobile, tablet, desktop
- [ ] Touch-friendly UI
- [ ] Imágenes optimizadas (WebP)
- [ ] Lazy loading de imágenes

#### Mejores Prácticas
- [ ] Código documentado
- [ ] Tests de seguridad (66+)
- [ ] CI/CD automatizado
- [ ] Logging centralizado (Loki)
- [ ] Tracing distribuido (Tempo)
- [ ] Alertas configuradas

---

## 📅 TIMELINE DEL MVP

### Diagrama de Gantt Simplificado

```
ENERO 2026                FEBRERO 2026              MARZO 2026
┌─────────────────────────┬─────────────────────────┬─────────────────────────┐
│ S1    S2    S3    S4    │ S5    S6    S7    S8    │ S9    S10   S11   S12   │
├─────────────────────────┼─────────────────────────┼─────────────────────────┤
│                         │                         │                         │
│ ████████████            │                         │                         │
│ PILAR 1: Auth + Roles   │                         │                         │
│                         │                         │                         │
│       ████████████████████████████                │                         │
│       PILAR 2: Captura Leads (Frontend)           │                         │
│                         │                         │                         │
│                   ████████████████████            │                         │
│                   PILAR 3: Integración n8n        │                         │
│                         │                         │                         │
│                         │       ████████████████████████                    │
│                         │       PILAR 4: Dashboards                         │
│                         │                         │                         │
│                         │                   ████████████████████████████████│
│                         │                   PILAR 5: Producción             │
│                         │                         │                         │
│                         │                         │                   🚀 MVP│
│                         │                         │                   27 Mar│
└─────────────────────────┴─────────────────────────┴─────────────────────────┘
```

### Detalle por Semana

#### Fase 1: Foundation (Semanas 1-4) - Enero 2026

| Semana | Fechas | Entregables | Responsable |
|--------|--------|-------------|-------------|
| S1 | 6-10 Ene | OAuth2 básico en api-gateway | Backend |
| S2 | 13-17 Ene | Sistema RBAC, roles en BD | Backend |
| S3 | 20-24 Ene | Frontend: Landing page | Frontend |
| S4 | 27-31 Ene | Frontend: Formulario contacto | Frontend |

#### Fase 2: Integration (Semanas 5-8) - Febrero 2026

| Semana | Fechas | Entregables | Responsable |
|--------|--------|-------------|-------------|
| S5 | 3-7 Feb | n8n bridge NATS completo | Backend |
| S6 | 10-14 Feb | Webhooks callback n8n → plataforma | Backend |
| S7 | 17-21 Feb | MW#1 SUB-A, SUB-B, SUB-C completos | Marketing |
| S8 | 24-28 Feb | Testing E2E formulario → n8n → respuesta | QA |

#### Fase 3: MVP Launch (Semanas 9-12) - Marzo 2026

| Semana | Fechas | Entregables | Responsable |
|--------|--------|-------------|-------------|
| S9 | 3-7 Mar | Dashboard de leads | Frontend |
| S10 | 10-14 Mar | Notificaciones email funcionales | Backend |
| S11 | 17-21 Mar | Deploy a GKE + DNS | DevOps |
| S12 | 24-27 Mar | Testing final + Lanzamiento | Todos |

---

## ✅ CRITERIOS DE ÉXITO DEL MVP

### Métricas de Lanzamiento

| Métrica | Objetivo Mínimo | Objetivo Ideal |
|---------|-----------------|----------------|
| Uptime | 99% | 99.5% |
| Tiempo de respuesta API | < 500ms | < 200ms |
| Lighthouse Score | 80+ | 90+ |
| Vulnerabilidades críticas | 0 | 0 |
| Tests pasando | 100% | 100% |
| Tiempo respuesta a leads | < 5 min | < 1 min |

### Funcionalidades Verificadas

- [ ] Visitante puede llenar formulario de contacto
- [ ] Lead se guarda en base de datos
- [ ] n8n recibe evento y calcula score
- [ ] Lead HOT notifica a admin inmediatamente
- [ ] Dashboard muestra leads clasificados
- [ ] Administrador puede ver métricas
- [ ] Abogado puede ver solo sus casos
- [ ] Cliente puede ver solo su información
- [ ] Sistema disponible 24/7

---

## 📋 POST-MVP: Funcionalidades Futuras

### Q2 2026 (Abril - Junio)
- Portal de clientes completo
- Booking de citas (calendar-service)
- Gestión de documentos
- MW#2: Retención y Reactivación

### Q3 2026 (Julio - Septiembre)
- Blog/CMS integrado
- MW#3: Fábrica de Contenido SEO
- Analytics avanzados
- Chat en vivo

### Q4 2026 (Octubre - Diciembre)
- App móvil (React Native)
- Firma electrónica
- WhatsApp Business
- AI Chatbot

---

## 🔗 DOCUMENTOS RELACIONADOS

| Documento | Descripción |
|-----------|-------------|
| [REQUERIMIENTOS.md](./REQUERIMIENTOS.md) | Requerimientos funcionales detallados |
| [ROLES_USUARIOS.md](./ROLES_USUARIOS.md) | Definición de los 4 roles |
| [ESTRATEGIA_AUTOMATIZACION.md](./ESTRATEGIA_AUTOMATIZACION.md) | Integración n8n |
| [MODELO_NEGOCIO.md](./MODELO_NEGOCIO.md) | Contexto empresarial |
| [ARQUITECTURA_FUNCIONAL.md](./ARQUITECTURA_FUNCIONAL.md) | Mapeo a microservicios |

---

*Documento gestionado por el Business Product Agent - Versión 1.0*
