# 🔗 INTEGRACIÓN PORTAL WEB ↔ n8n CLOUD

**Versión**: 1.0  
**Fecha**: 3 de Enero, 2026  
**Estado**: 📋 Diseñado - Listo para Implementación

---

## 📋 RESUMEN EJECUTIVO

Este documento define la **arquitectura de integración** entre:

1. **Portal Web CarrilloAbogados** (Spring Boot Microservices)
2. **Sistema de Automatización MarketingTech** (n8n Cloud)

### Objetivo Principal

Conectar el formulario de contacto del portal web con los workflows de automatización de marketing para:
- **Respuesta automática < 60 segundos** a nuevos leads
- **Scoring automático** con IA (Google Gemini)
- **Notificación HOT leads** al equipo comercial
- **Nurturing automatizado** con secuencia de emails

---

## 🏗️ ARQUITECTURA DE INTEGRACIÓN

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         FLUJO DE INTEGRACIÓN                                │
└─────────────────────────────────────────────────────────────────────────────┘

   PORTAL WEB (CarrilloAbogados)              n8n CLOUD (MarketingTech)
   ══════════════════════════════             ═══════════════════════════

   ┌──────────────────┐
   │   Frontend       │
   │   (Next.js)      │
   │  /contact form   │
   └────────┬─────────┘
            │ POST /api/leads
            ▼
   ┌──────────────────┐
   │  client-service  │
   │  (Puerto 8200)   │
   │  Lead Entity     │
   └────────┬─────────┘
            │ NATS: lead.capturado
            ▼
   ┌──────────────────┐                    ┌──────────────────┐
   │ n8n-integration  │                    │  WORKFLOW A:     │
   │    service       │───HTTP POST───────►│  Lead Lifecycle  │
   │  (Puerto 8800)   │                    │   Manager (HUB)  │
   └────────┬─────────┘                    └────────┬─────────┘
            │                                       │
            │◄──────HTTP 200 (callback)─────────────┤
            │                                       ▼
   ┌────────┴─────────┐                    ┌──────────────────┐
   │ notification     │                    │    SUB-A:        │
   │    service       │◄───────────────────│  Lead Intake &   │
   │  (Email/SMS)     │   webhook/lead-hot │   Enrichment     │
   └──────────────────┘                    └────────┬─────────┘
                                                    │
                                           ┌────────┴────────┐
                                           │                 │
                                           ▼                 ▼
                                   ┌───────────┐     ┌───────────┐
                                   │  Gemini   │     │ Firestore │
                                   │   (IA)    │     │   (BD)    │
                                   └───────────┘     └───────────┘
                                           │
                                           ▼
                                   ┌───────────────┐
                                   │ Gmail (Lead)  │
                                   │ Respuesta <1m │
                                   └───────────────┘
```

---

## 🔌 PUNTOS DE INTEGRACIÓN

### 1. Portal Web → n8n (Outbound)

| Evento | Origen | Destino n8n | Webhook URL |
|--------|--------|-------------|-------------|
| `lead.capturado` | client-service | MW#1 → Orquestador | `https://carrilloabgd.app.n8n.cloud/webhook/lead-events` |
| `cita.agendada` | calendar-service | MW#1 → SUB-F | `https://carrilloabgd.app.n8n.cloud/webhook/meeting-events` |
| `caso.cerrado` | case-service | MW#2 → Orquestador | `https://carrilloabgd.app.n8n.cloud/webhook/case-events` |

### 2. n8n → Portal Web (Inbound)

| Webhook | Origen n8n | Destino | Acción |
|---------|------------|---------|--------|
| `/webhook/lead-scored` | SUB-A | n8n-integration-service | Actualizar score en BD |
| `/webhook/lead-hot` | SUB-A | notification-service | Notificar abogado |
| `/webhook/meeting-confirmed` | SUB-F | calendar-service | Sincronizar cita |

---

## 📦 PAYLOAD DE INTEGRACIÓN

### Evento: `new_lead` (Portal → n8n)

```json
{
  "event_type": "new_lead",
  "timestamp": "2026-01-03T15:30:00.000Z",
  "source": "portal_web",
  "payload": {
    "lead_id": "550e8400-e29b-41d4-a716-446655440000",
    "nombre": "María García",
    "email": "maria@techstartup.co",
    "telefono": "+573001234567",
    "empresa": "TechStartup SAS",
    "cargo": "CEO",
    "servicio_interes": "Registro de Marca",
    "mensaje": "Necesitamos proteger nuestra marca de software",
    "utm_source": "google",
    "utm_campaign": "registro-marca-q1-2026"
  }
}
```

### Callback: `lead_scored` (n8n → Portal)

```json
{
  "event_type": "lead_scored",
  "timestamp": "2026-01-03T15:30:05.000Z",
  "lead_id": "550e8400-e29b-41d4-a716-446655440000",
  "score": 85,
  "category": "HOT",
  "score_breakdown": {
    "base": 30,
    "servicio": 20,
    "mensaje": 10,
    "telefono": 10,
    "empresa": 10,
    "cargo_clevel": 5
  },
  "processed_at": "2026-01-03T15:30:05.000Z",
  "response_sent": true
}
```

---

## 🧩 IMPLEMENTACIÓN: n8n-integration-service

### Estructura de Paquetes

```
n8n-integration-service/
└── src/main/java/com/carrilloabogados/n8n/
    ├── N8nIntegrationServiceApplication.java
    ├── config/
    │   ├── NatsConfig.java           # Configuración NATS
    │   ├── N8nConfig.java            # URLs webhooks n8n
    │   └── RestTemplateConfig.java   # HTTP client
    ├── controller/
    │   └── WebhookController.java    # Endpoints inbound
    ├── dto/
    │   ├── LeadEventDto.java         # Evento lead capturado
    │   ├── LeadScoredDto.java        # Callback score
    │   └── N8nResponseDto.java       # Respuesta estándar
    ├── listener/
    │   └── NatsEventListener.java    # Escucha eventos NATS
    ├── service/
    │   ├── N8nWebhookService.java    # Envía a n8n
    │   └── LeadSyncService.java      # Sincroniza con BD
    └── model/
        └── WorkflowExecution.java    # Log de ejecuciones
```

### Endpoints del Servicio

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/webhook/lead-scored` | Recibe score calculado por n8n |
| POST | `/webhook/lead-hot` | Recibe alerta de lead HOT |
| POST | `/webhook/meeting-confirmed` | Recibe confirmación de cita |
| GET | `/api/health` | Health check |
| GET | `/api/executions` | Log de ejecuciones (debug) |

---

## 📊 SCORING DE LEADS

El scoring se calcula en **n8n SUB-A** y se sincroniza con el portal:

| Criterio | Puntos | Implementación |
|----------|-------:|----------------|
| Base (lead capturado) | +30 | Automático |
| Servicio "marca" o "litigio" | +20 | `servicio_interes.includes()` |
| Mensaje > 50 caracteres | +10 | `mensaje.length > 50` |
| Tiene teléfono | +10 | `telefono != null` |
| Tiene empresa | +10 | `empresa != null` |
| Email corporativo | +10 | `!email.includes('gmail')` |
| Cargo C-Level | +20 | `cargo.match(/CEO|CTO|CFO|Founder/)` |

### Categorías

| Categoría | Score | Acción n8n | Acción Portal |
|-----------|------:|------------|---------------|
| 🔥 **HOT** | ≥70 | Email IA + Notificación equipo | Notificar abogado |
| 🟡 **WARM** | 40-69 | Email IA | Nurturing sequence |
| ⚪ **COLD** | <40 | Email genérico | Nurturing básico |

---

## ⚙️ CONFIGURACIÓN

### application.yml (n8n-integration-service)

```yaml
n8n:
  cloud:
    base-url: https://carrilloabgd.app.n8n.cloud
    webhooks:
      lead-events: /webhook/lead-events
      meeting-events: /webhook/meeting-events
      case-events: /webhook/case-events
    timeout: 30s
    retry:
      max-attempts: 3
      delay: 1s

nats:
  server: nats://nats:4222
  subscriptions:
    - lead.capturado
    - cita.agendada
    - caso.cerrado

spring:
  application:
    name: N8N-INTEGRATION-SERVICE
```

### Variables de Entorno

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `N8N_CLOUD_URL` | URL base n8n Cloud | `https://carrilloabgd.app.n8n.cloud` |
| `N8N_WEBHOOK_SECRET` | Secret para validar callbacks | `${N8N_SECRET}` |
| `NATS_SERVER` | URL servidor NATS | `nats://nats:4222` |

---

## 🧪 PLAN DE TESTING E2E

### Test 1: Flujo Completo Lead → n8n → Respuesta

```powershell
# 1. Crear lead via API
$lead = @{
    nombre = "Test E2E Lead"
    email = "test@empresa.co"
    telefono = "+573001234567"
    empresa = "Empresa Test SAS"
    servicio = "Registro de Marca"
    mensaje = "Necesitamos registrar nuestra marca de software urgentemente"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8200/client-service/api/leads" `
    -Method POST -Body $lead -ContentType "application/json"

# 2. Verificar evento NATS (logs n8n-integration-service)
docker logs carrillo-n8n-integration-service --tail 50

# 3. Verificar ejecución en n8n Cloud
# Dashboard: https://carrilloabgd.app.n8n.cloud/executions

# 4. Verificar email enviado al lead (Gmail)
```

### Test 2: Callback Lead Scored

```powershell
# Simular callback de n8n
$callback = @{
    event_type = "lead_scored"
    lead_id = "550e8400-e29b-41d4-a716-446655440000"
    score = 85
    category = "HOT"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8800/n8n-integration-service/webhook/lead-scored" `
    -Method POST -Body $callback -ContentType "application/json"
```

---

## 📅 ROADMAP DE IMPLEMENTACIÓN

### Fase 1: MVP (Esta semana)

- [ ] Crear DTOs para eventos
- [ ] Implementar NatsEventListener
- [ ] Implementar N8nWebhookService
- [ ] Crear WebhookController
- [ ] Testing local con Docker Compose

### Fase 2: Integración (Siguiente semana)

- [ ] Conectar con n8n Cloud real
- [ ] Implementar callbacks
- [ ] Sincronizar scores con BD
- [ ] Testing E2E completo

### Fase 3: Producción (Pre-MVP)

- [ ] Agregar métricas y logging
- [ ] Configurar alertas de error
- [ ] Documentar troubleshooting

---

## 🔗 REFERENCIAS

### Repositorios

| Proyecto | Ubicación | Rama |
|----------|-----------|------|
| Portal Web | `C:\Carrillo Abogados\Repositorios GitHub\CarrilloAbogados` | `dev` |
| MarketingTech | `C:\GitHub Desktop\MarketingTech` | `Alexis` |

### n8n Cloud

| Recurso | URL |
|---------|-----|
| Dashboard | https://carrilloabgd.app.n8n.cloud |
| Webhook Lead Events | https://carrilloabgd.app.n8n.cloud/webhook/lead-events |
| Orquestador ID | `bva1Kc1USbbITEAw` |
| SUB-A ID | `RHj1TAqBazxNFriJ` |

### Documentación Relacionada

- [ESTRATEGIA_AUTOMATIZACION.md](../business/ESTRATEGIA_AUTOMATIZACION.md)
- [ARQUITECTURA_FUNCIONAL.md](../business/ARQUITECTURA_FUNCIONAL.md)
- [00_ARQUITECTURA_GENERAL.md](MarketingTech/02-context/technical/arquitectura/)

---

*Documento de integración Portal Web ↔ n8n Cloud*
