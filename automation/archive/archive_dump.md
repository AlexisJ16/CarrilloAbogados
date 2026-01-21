# 🔗 Integración Web → n8n → Plataforma Spring Boot

**Documento de Especificación Técnica**
**Última actualización**: 5 de Enero, 2026
**Estado**: ⚠️ EN DESARROLLO - Opción B (Arquitectura Completa)
**Propósito**: Definir contrato de integración para MW#1 Lead Lifecycle

**Responsables**:
- **Marketing Dev (n8n + Frontend)**: Integración webhook n8n + debugging workflows
- **Backend Dev (Spring Boot)**: Eventos NATS + n8n-integration-service

---

## 📋 ESTADO ACTUAL (Verificado 5 Enero 2026)

### ✅ Componentes Funcionales

| Componente | Estado | Evidencia |
|------------|--------|-----------|
| **Frontend `/contacto`** | ✅ Funcional | Envía a `/client-service/api/leads` |
| **client-service API** | ✅ Funcional | Guarda en PostgreSQL schema 'clients' |
| **n8n Webhook** | ✅ Activo | `POST /webhook/lead-events` |
| **n8n Orquestador** | ✅ Activo | 8 nodos, 60% tasa éxito |
| **n8n SUB-A (IA)** | ✅ Funcional | 13 nodos, Gemini 2.5 Pro, 40% tasa éxito |
| **Firestore** | ✅ Operativo | Project: `carrillo-marketing-core` |
| **Gmail API** | ✅ Configurado | Envío emails marketing@carrilloabgd.com |

### ❌ Integraciones Faltantes (BLOQUEADORES)

| Integración | Estado | Bloqueador |
|-------------|--------|------------|
| **Formulario → n8n** | ❌ NO conectado | Frontend apunta a client-service, no a n8n |
| **client-service → NATS** | ❌ NO implementado | No emite evento `lead.capturado` |
| **NATS → n8n-integration-service** | ❌ NO implementado | Service sin listener NATS |
| **n8n-integration-service → n8n** | ❌ NO implementado | No llama webhook n8n |
| **n8n → Plataforma (callbacks)** | ❌ NO implementado | Webhooks inversos faltantes |
| **Alta tasa error n8n** | 🚨 CRÍTICO | 50% ejecuciones fallan (debugging pendiente) |

---

## 📋 Resumen Ejecutivo

Este documento detalla la arquitectura completa de integración entre:
1. **Frontend Next.js** (formulario de contacto)
2. **Plataforma Spring Boot** (8 microservicios)
3. **n8n Cloud** (automatización con IA)

**Objetivo**: Lead capturado en web → Scored con IA → Notificación HOT → BD actualizada en **< 1 minuto**

---

## 🏗️ ARQUITECTURA OBJETIVO (Opción B - Completa)

### Flujo Completo MW#1

```
┌─────────────────────────────────────────────────────────────────┐
│                     FLUJO COMPLETO MW#1                          │
└─────────────────────────────────────────────────────────────────┘

👤 Usuario llena formulario /contacto
   │
   ↓ POST /client-service/api/leads
   │
┌──▼──────────────────────────────────────────┐
│  1. client-service (Spring Boot)            │
│  - Valida datos con Bean Validation        │
│  - Guarda en PostgreSQL schema 'clients'   │
│  - Asigna leadId (UUID)                    │
│  - Estado inicial: NEW                     │
└──┬──────────────────────────────────────────┘
   │
   ↓ natsTemplate.publish("lead.capturado", event)
   │
┌──▼──────────────────────────────────────────┐
│  2. NATS Message Broker                     │
│  - Subject: "lead.capturado"               │
│  - Payload: LeadCapturedEvent              │
└──┬──────────────────────────────────────────┘
   │
   ↓ @NatsListener("lead.capturado")
   │
┌──▼──────────────────────────────────────────┐
│  3. n8n-integration-service                 │
│  - Escucha evento NATS                     │
│  - Transforma a formato n8n                │
│  - Llama webhook n8n                       │
└──┬──────────────────────────────────────────┘
   │
   ↓ POST https://carrilloabgd.app.n8n.cloud/webhook/lead-events
   │
┌──▼──────────────────────────────────────────┐
│  4. n8n WORKFLOW A (Orquestador)            │
│  - Recibe webhook                          │
│  - Identifica event_type: "new_lead"       │
│  - Invoca SUB-A                            │
└──┬──────────────────────────────────────────┘
   │
   ↓ Execute Workflow: SUB-A
   │
┌──▼──────────────────────────────────────────┐
│  5. n8n SUB-A (Lead Intake AI)              │
│  - Gemini 2.5 Pro analiza lead             │
│  - Calcula score (0-100)                   │
│  - Categoría: HOT/WARM/COLD                │
│  - Guarda en Firestore                     │
│  - Si HOT: Email a marketing@              │
│  - Genera respuesta IA                     │
│  - Email automático al lead                │
└──┬──────────────────────────────────────────┘
   │
   ↓ POST /webhook/lead-scored (callback)
   │
┌──▼──────────────────────────────────────────┐
│  6. n8n-integration-service (webhook recv)  │
│  - Recibe score y categoría de n8n         │
│  - Llama client-service API                │
└──┬──────────────────────────────────────────┘
   │
   ↓ PATCH /api/leads/{leadId}
   │
┌──▼──────────────────────────────────────────┐
│  7. client-service actualiza lead           │
│  - lead.score = 85                         │
│  - lead.categoria = "HOT"                  │
│  - lead.estado = "QUALIFIED"               │
└─────────────────────────────────────────────┘
```

---

## 📦 ESPECIFICACIÓN DE PAYLOADS

### 1. Frontend → client-service (Actual - No Cambia)

**Endpoint**: `POST /client-service/api/leads`
**Headers**: `Content-Type: application/json`

```json
{
  "nombre": "Juan Pérez",
  "email": "juan@empresa.com",
  "telefono": "+57 300 123 4567",
  "empresa": "Empresa SAS",
  "servicio": "derecho-marcas",
  "mensaje": "Necesito registrar una marca para mi producto"
}
```

**Respuesta**:

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "nombre": "Juan Pérez",
  "email": "juan@empresa.com",
  "telefono": "+57 300 123 4567",
  "empresa": "Empresa SAS",
  "servicio": "derecho-marcas",
  "mensaje": "Necesito registrar una marca",
  "estado": "NEW",
  "score": null,
  "categoria": null,
  "fechaCreacion": "2026-01-05T18:30:00Z"
}
```

---

### 2. client-service → NATS (A IMPLEMENTAR - Backend Dev)

**Subject**: `lead.capturado`
**Message Type**: `LeadCapturedEvent`

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadCapturedEvent implements Serializable {
    private String leadId;
    private String nombre;
    private String email;
    private String telefono;
    private String empresa;
    private String servicio;
    private String mensaje;
    private String source;
    private Instant timestamp;
}
```

**JSON Publicado**:

```json
{
  "leadId": "550e8400-e29b-41d4-a716-446655440000",
  "nombre": "Juan Pérez",
  "email": "juan@empresa.com",
  "telefono": "+57 300 123 4567",
  "empresa": "Empresa SAS",
  "servicio": "derecho-marcas",
  "mensaje": "Necesito registrar una marca",
  "source": "web_contacto",
  "timestamp": "2026-01-05T18:30:00Z"
}
```

---

### 3. n8n-integration-service → n8n Webhook (A IMPLEMENTAR)

**Endpoint**: `POST https://carrilloabgd.app.n8n.cloud/webhook/lead-events`
**Headers**: `Content-Type: application/json`

**Transformación de Campos**:

```
NATS Event          →  n8n Webhook
─────────────────────────────────────
leadId              →  lead_id
servicio            →  servicio_interes
timestamp           →  orchestrator_timestamp
+ event_type: "new_lead"
```

**Payload Enviado**:

```json
{
  "event_type": "new_lead",
  "lead_id": "550e8400-e29b-41d4-a716-446655440000",
  "nombre": "Juan Pérez",
  "email": "juan@empresa.com",
  "telefono": "+57 300 123 4567",
  "empresa": "Empresa SAS",
  "servicio_interes": "derecho-marcas",
  "mensaje": "Necesito registrar una marca",
  "source": "web_contacto",
  "orchestrator_timestamp": "2026-01-05T18:30:00Z"
}
```

**Respuesta de n8n**:

```json
{
  "success": true,
  "result": {
    "lead_id": "550e8400-e29b-41d4-a716-446655440000",
    "score": 85,
    "categoria": "HOT"
  }
}
```

---

### 4. n8n → n8n-integration-service Callbacks (A IMPLEMENTAR)

#### Callback 1: Lead Scored

**Endpoint**: `POST /n8n-integration-service/webhook/lead-scored`
**Headers**: `Content-Type: application/json`

```json
{
  "lead_id": "550e8400-e29b-41d4-a716-446655440000",
  "score": 85,
  "categoria": "HOT",
  "ai_analysis": {
    "normalized_interest": "Marcas",
    "is_spam": false,
    "calculated_score": 85,
    "category": "HOT"
  },
  "processed_at": "2026-01-05T18:30:15Z"
}
```

#### Callback 2: Lead HOT (solo si score ≥70)

**Endpoint**: `POST /n8n-integration-service/webhook/lead-hot`

```json
{
  "lead_id": "550e8400-e29b-41d4-a716-446655440000",
  "score": 85,
  "categoria": "HOT",
  "notified_at": "2026-01-05T18:30:20Z",
  "email_sent_to": "marketing@carrilloabgd.com"
}
```

---

### 5. n8n-integration-service → client-service (A IMPLEMENTAR)

**Endpoint**: `PATCH /client-service/api/leads/{leadId}`

```json
{
  "score": 85,
  "categoria": "HOT",
  "estado": "QUALIFIED"
}
```

---

## 📁 Archivos Clave y Código de Referencia

### client-service (Backend Dev - A IMPLEMENTAR)

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
| client-service | ✅ Ready | Lead API completa |
| n8n-integration-service | ✅ Ready | Bridge implementado |
| NATS | ✅ Ready | Operativo en Docker |
| Orquestador n8n | ⚠️ Inactivo | Requiere corrección |
| SUB-A n8n | ⚠️ Inactivo | Triggered by Orquestador |

### ⏳ Pendientes

1. **Corregir error webhook Orquestador** (ver ACCION_REQUERIDA.md)
2. **Activar workflows en n8n Cloud**
3. **Probar flujo E2E**
4. **Desplegar n8n-integration-service en Cloud Run**

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
