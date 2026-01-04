# 🔗 Integración Plataforma Web ↔ n8n Cloud

**Última actualización**: 2026-01-03  
**Estado**: ✅ IMPLEMENTADO (Pendiente activar n8n workflows)

---

## 📋 Resumen

Este documento detalla cómo conectar la plataforma web de Carrillo Abogados con los workflows de n8n Cloud para automatización de leads.

---

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        FLUJO DE INTEGRACIÓN                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐                  │
│  │   FRONTEND   │    │  API GATEWAY │    │ CLIENT-SVC   │                  │
│  │  (Next.js)   │───►│  (port 8080) │───►│ (port 8200)  │                  │
│  │              │    │              │    │              │                  │
│  │ Formulario   │    │   /api/*     │    │ POST /leads  │                  │
│  │ de Contacto  │    │              │    │              │                  │
│  └──────────────┘    └──────────────┘    └──────┬───────┘                  │
│                                                 │                           │
│                                                 │ NATS Event                │
│                                                 │ "carrillo.events.lead.*"  │
│                                                 ▼                           │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │                           NATS (Messaging)                            │  │
│  └──────────────────────────────────────────────┬───────────────────────┘  │
│                                                 │                           │
│                                                 ▼                           │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │                    n8n-integration-service (port 8800)                │  │
│  │                                                                       │  │
│  │  NatsEventListener.java                                               │  │
│  │  ├─ Suscribe: carrillo.events.lead.created                           │  │
│  │  ├─ Transforma evento a formato n8n                                  │  │
│  │  └─ HTTP POST → n8n webhook                                          │  │
│  └──────────────────────────────────────────────┬───────────────────────┘  │
│                                                 │                           │
│                                                 │ HTTP POST                 │
│                                                 ▼                           │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │                         n8n Cloud                                     │  │
│  │                                                                       │  │
│  │  Webhook: https://carrilloabgd.app.n8n.cloud/webhook/lead-events     │  │
│  │           │                                                           │  │
│  │           ▼                                                           │  │
│  │  ┌─────────────────┐      ┌─────────────────┐                        │  │
│  │  │  Orquestador    │ ───► │    SUB-A        │                        │  │
│  │  │  (5 nodos)      │      │  (10 nodos)     │                        │  │
│  │  │                 │      │  - AI Scoring   │                        │  │
│  │  │  ID: bva1...    │      │  - Firestore    │                        │  │
│  │  └─────────────────┘      │  - Gmail        │                        │  │
│  │                           └────────┬────────┘                        │  │
│  └────────────────────────────────────┼─────────────────────────────────┘  │
│                                       │                                     │
│                                       │ Callback HTTP                       │
│                                       ▼                                     │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │                    n8n-integration-service                            │  │
│  │                                                                       │  │
│  │  WebhookController.java                                               │  │
│  │  ├─ POST /webhook/lead-scored  → Actualizar score en BD              │  │
│  │  ├─ POST /webhook/lead-hot     → Notificar abogado urgente           │  │
│  │  └─ POST /webhook/meeting-confirmed → Sincronizar calendario         │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 📁 Archivos Clave

### client-service (Origen de eventos)

| Archivo | Propósito |
|---------|-----------|
| `LeadResource.java` | REST API para leads, emite eventos NATS |
| `LeadService.java` | Lógica de negocio de leads |
| `NatsEventPublisher.java` | Publica eventos a NATS |

### n8n-integration-service (Bridge)

| Archivo | Propósito |
|---------|-----------|
| `NatsEventListener.java` | Escucha NATS, envía a n8n webhooks |
| `WebhookController.java` | Recibe callbacks de n8n |
| `N8nWebhookService.java` | Cliente HTTP para n8n |

---

## 🔧 Configuración Requerida

### 1. Variables de Entorno

**n8n-integration-service** (`application.yml`):

```yaml
n8n:
  webhooks:
    lead-events: https://carrilloabgd.app.n8n.cloud/webhook/lead-events
    # Solo funciona cuando el workflow está ACTIVO

nats:
  server: nats://nats:4222
  subjects:
    - carrillo.events.lead.created
    - carrillo.events.case.closed
    - carrillo.events.appointment.scheduled
```

### 2. Eventos NATS

| Evento | Origen | Payload |
|--------|--------|---------|
| `carrillo.events.lead.created` | client-service | `{nombre, email, telefono, empresa, servicio, mensaje}` |
| `carrillo.events.case.closed` | case-service | `{caseId, clientId, resolution}` |
| `carrillo.events.appointment.scheduled` | calendar-service | `{appointmentId, clientEmail, date}` |

### 3. Webhooks n8n → Plataforma

| Endpoint | Método | Payload Esperado |
|----------|--------|------------------|
| `/webhook/lead-scored` | POST | `{leadId, score, category, analysisNotes}` |
| `/webhook/lead-hot` | POST | `{leadId, urgency, assignedTo}` |
| `/webhook/meeting-confirmed` | POST | `{meetingId, clientEmail, confirmedDate}` |

---

## ✅ Checklist de Integración

### Pre-requisitos

- [ ] Orquestador activo en n8n Cloud
- [ ] n8n-integration-service desplegado
- [ ] NATS operativo
- [ ] client-service emitiendo eventos

### Prueba E2E

```bash
# 1. Crear lead via API
curl -X POST http://localhost:8200/client-service/api/leads \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Test E2E",
    "email": "test@empresa.com",
    "telefono": "3001234567",
    "empresa": "Test Corp",
    "servicio": "marca",
    "mensaje": "Prueba de integración completa"
  }'

# 2. Verificar evento NATS (usar nats-box)
nats sub "carrillo.events.lead.*"

# 3. Verificar ejecución en n8n Cloud
# Dashboard → Executions → Filtrar por Orquestador

# 4. Verificar callback recibido
# Logs de n8n-integration-service
```

---

## 🚨 Estado Actual

### ✅ Componentes Listos

| Componente | Estado | Notas |
|------------|--------|-------|
| client-service | ✅ Ready | Lead API completa + endpoints para scoring |
| n8n-integration-service | ✅ Ready | Bridge implementado con callbacks reales |
| WebhookController | ✅ Ready | `/lead-scored` y `/lead-hot` implementados |
| Frontend /contacto | ✅ Ready | Envía a `/api/client-service/api/leads` |
| next.config.js | ✅ Ready | Rewrites configurados para API Gateway |
| NATS | ✅ Ready | Operativo en Docker |
| Orquestador n8n | ⚠️ Inactivo | Requiere corrección |
| SUB-A n8n | ⚠️ Inactivo | Triggered by Orquestador |

### ⏳ Pendientes

1. **Corregir error webhook Orquestador** (ver ACCION_REQUERIDA.md)
2. **Activar workflows en n8n Cloud**
3. **Probar flujo E2E con Docker Compose**

---

## 📊 Métricas Esperadas

| Métrica | Objetivo | Medición |
|---------|----------|----------|
| Tiempo respuesta lead | < 60 segundos | n8n → Email enviado |
| Tasa éxito workflow | > 95% | n8n Executions dashboard |
| Score accuracy | > 80% | Validación manual mensual |

---

## 🔗 Referencias

- [STATUS.md](./workflows/MEGA_WORKFLOW_1_LEAD_LIFECYCLE/STATUS.md) - Estado detallado workflows
- [ACCION_REQUERIDA.md](./workflows/MEGA_WORKFLOW_1_LEAD_LIFECYCLE/ACCION_REQUERIDA.md) - Acciones pendientes
- [n8n_mcp_guide.md](./02-context/technical/n8n_mcp_guide.md) - Guía MCP tools

---

*Documento parte de la integración CarrilloAbogados + n8n-antigravity*
