# STATUS: SUB-A Lead Intake (v5 - AI POWERED - NATIVE)

**Workflow ID**: `RHj1TAqBazxNFriJ`
**Última Actualización**: 6 de Enero, 2026
**Estado**: ✅ **CALLBACKS VERIFICADOS Y FUNCIONANDO**

---

## 📊 RESUMEN DEL WORKFLOW

| Campo | Valor |
|-------|-------|
| **Nombre** | SUB-A: Lead Intake (v5 - AI POWERED - NATIVE) |
| **ID** | RHj1TAqBazxNFriJ |
| **Nodos Totales** | 16 |
| **Conexiones Válidas** | 15 |
| **Estado** | Inactivo (sub-workflow, invocado por Orquestador) |
| **Última Ejecución** | 6 Enero 2026, 01:37 AM COT |
| **Tasa de Éxito** | 100% (última sesión) |

---

## ✅ ESTADO ACTUAL (2026-01-06)

### Componentes Funcionales

| Componente | Estado | Verificación |
|------------|--------|--------------|
| **Trigger** | ✅ Funcional | Execute Workflow Trigger configurado |
| **Mapeo Input** | ✅ Funcional | Normaliza datos del orquestador |
| **Análisis IA (Gemini)** | ✅ Funcional | Score 0-100 + categorización HOT/WARM/COLD |
| **Clasificación** | ✅ Funcional | Procesa respuesta IA y enriquece datos |
| **Firestore** | ✅ Funcional | Guarda en `carrillo-marketing-core/leads/leads` |
| **Notificación HOT** | ✅ Funcional | Email a marketing@carrilloabgd.com |
| **Respuesta IA** | ✅ Funcional | Gemini genera email personalizado |
| **Email Lead** | ✅ Funcional | Gmail API envía respuesta |
| **Callback Lead Scored** | ✅ Funcional | POST a webhook externo |
| **Callback HOT Alert** | ✅ Funcional | POST condicional (solo HOT) |
| **Nodo FINAL** | ✅ Funcional | Retorna resultado al Orquestador |

---

## 🔄 ARQUITECTURA DE CALLBACKS (IMPLEMENTADO)

### Flujo Completo Verificado

```
[6. Enviar Respuesta Lead (Gmail)]
    ↓
[FINAL. Resultado del Sub-Workflow]
    ← Retorna al Orquestador

FLUJO PARALELO (desde nodo 1):

[1. Validar y Clasificar]
    ├─→ [2. Guardar en Firestore] (flujo principal)
    │       ↓
    │   [continúa con notificación y emails...]
    │
    └─→ [7. Callback Lead Scored] ✅ (flujo paralelo)
            POST https://eoc4ipe73sd9y75.m.pipedream.net
            Body: {
              lead_id, score, categoria,
              ai_analysis, processed_at
            }
            ↓
        [8. Es Lead HOT (Callback)?] ✅
            IF categoria === "HOT"
            ↓ [TRUE]
        [9. Callback Hot Lead Alert] ✅
            POST https://eoyvly7sjxiim05.m.pipedream.net
            Body: {
              lead_id, score, categoria,
              notified_at, email_sent_to
            }
```

### Callbacks Configurados

#### Callback 1: Lead Scored (SIEMPRE se ejecuta)

**URL**: `https://eoc4ipe73sd9y75.m.pipedream.net` (Pipedream testing)
**Método**: POST
**Content-Type**: application/json

**Payload Enviado**:
```json
{
  "lead_id": "2026-01-06T06:37:23.503Z-carolina-at-techventures.io",
  "score": 95,
  "categoria": "HOT",
  "ai_analysis": {
    "normalized_interest": "Marcas",
    "is_spam": false,
    "analysis_reason": "Lead de alto valor...",
    "calculated_score": 95,
    "category": "HOT"
  },
  "processed_at": "2026-01-06T06:37:23.503Z"
}
```

**Propósito**: Notificar al backend que el lead fue procesado y scored por IA.

---

#### Callback 2: Hot Lead Alert (SOLO si categoria === "HOT")

**URL**: `https://eoyvly7sjxiim05.m.pipedream.net` (Pipedream testing)
**Método**: POST
**Content-Type**: application/json

**Payload Enviado**:
```json
{
  "lead_id": "2026-01-06T06:37:23.503Z-carolina-at-techventures.io",
  "score": 95,
  "categoria": "HOT",
  "notified_at": "2026-01-06T06:37:40.000Z",
  "email_sent_to": "marketing@carrilloabgd.com"
}
```

**Propósito**: Alerta inmediata al backend de que se detectó un lead HOT y el equipo fue notificado.

---

## 🧪 PRUEBAS REALIZADAS (2026-01-06)

### Test 1: Lead HOT (Score 95)

**Payload Entrada**:
```json
{
  "event_type": "new_lead",
  "nombre": "Carolina Gomez",
  "email": "carolina@techventures.io",
  "telefono": "+57 318 999 8877",
  "empresa": "TechVentures Capital",
  "cargo": "Managing Partner",
  "servicio_interes": "Marcas",
  "mensaje": "Fondo de inversión con portfolio de 20 startups..."
}
```

**Resultados**:
- ✅ Gemini Score: 95 (HOT)
- ✅ Firestore: Lead guardado correctamente
- ✅ Email HOT a marketing@carrilloabgd.com: Enviado
- ✅ Email respuesta IA al lead: Enviado
- ✅ Callback 1 (Lead Scored): Ejecutado (404ms)
- ✅ Callback 2 (Hot Alert): Ejecutado (392ms)
- ✅ Datos recibidos en Pipedream: Completos y correctos

---

### Test 2: Lead HOT (Score 95) - Pedro Ramirez

**Payload Entrada**:
```json
{
  "event_type": "new_lead",
  "nombre": "Pedro Ramirez",
  "email": "pedro.ramirez@startuptech.com",
  "telefono": "+57 320 111 2233",
  "empresa": "StartupTech Solutions",
  "cargo": "CTO",
  "servicio_interes": "Patentes",
  "mensaje": "Startup fintech con nueva tecnología blockchain..."
}
```

**Resultados**:
- ✅ Gemini Score: 95 (HOT)
- ✅ Categoría: HOT
- ✅ Ambos callbacks ejecutados correctamente
- ✅ Duración total: ~28 segundos
- ✅ Sin errores

---

## 📋 CONFIGURACIÓN DE NODOS CALLBACKS

### Nodo 7: Callback Lead Scored

```json
{
  "id": "callback_lead_scored",
  "name": "7. Callback Lead Scored",
  "type": "n8n-nodes-base.httpRequest",
  "typeVersion": 4.2,
  "position": [1900, 300],
  "parameters": {
    "url": "https://eoc4ipe73sd9y75.m.pipedream.net",
    "method": "POST",
    "sendHeaders": true,
    "headerParameters": {
      "parameters": [
        {"name": "Content-Type", "value": "application/json"}
      ]
    },
    "sendBody": true,
    "contentType": "json",
    "specifyBody": "json",
    "jsonBody": "={{ JSON.stringify({ lead_id: $('1. Validar y Clasificar1').item.json.lead_id, score: $('1. Validar y Clasificar1').item.json.score, categoria: $('1. Validar y Clasificar1').item.json.categoria, ai_analysis: $('1. Validar y Clasificar1').item.json.ai_analysis, processed_at: $('1. Validar y Clasificar1').item.json.processed_at }) }}",
    "options": {}
  },
  "onError": "continueRegularOutput"
}
```

**Conexión**: Desde nodo "1. Validar y Clasificar1" (flujo paralelo)

---

### Nodo 8: Es Lead HOT (Callback)?

```json
{
  "id": "if_hot_check",
  "name": "8. Es Lead HOT (Callback)?",
  "type": "n8n-nodes-base.if",
  "typeVersion": 2,
  "position": [2100, 300],
  "parameters": {
    "conditions": {
      "options": {
        "caseSensitive": true,
        "leftValue": "",
        "typeValidation": "loose"
      },
      "combinator": "and",
      "conditions": [
        {
          "leftValue": "={{ $('1. Validar y Clasificar1').item.json.categoria }}",
          "rightValue": "HOT",
          "operator": {
            "type": "string",
            "operation": "equals"
          }
        }
      ]
    }
  }
}
```

**Nota**: Usa `typeValidation: "loose"` para evitar problemas de referencia a nodos anteriores.

---

### Nodo 9: Callback Hot Lead Alert

```json
{
  "id": "callback_hot_lead",
  "name": "9. Callback Hot Lead Alert",
  "type": "n8n-nodes-base.httpRequest",
  "typeVersion": 4.2,
  "position": [2350, 200],
  "parameters": {
    "url": "https://eoyvly7sjxiim05.m.pipedream.net",
    "method": "POST",
    "sendHeaders": true,
    "headerParameters": {
      "parameters": [
        {"name": "Content-Type", "value": "application/json"}
      ]
    },
    "sendBody": true,
    "contentType": "json",
    "specifyBody": "json",
    "jsonBody": "={{ JSON.stringify({ lead_id: $('1. Validar y Clasificar1').item.json.lead_id, score: $('1. Validar y Clasificar1').item.json.score, categoria: $('1. Validar y Clasificar1').item.json.categoria, notified_at: new Date().toISOString(), email_sent_to: 'marketing@carrilloabgd.com' }) }}",
    "options": {}
  },
  "onError": "continueRegularOutput"
}
```

**Conexión**: Desde salida TRUE del nodo 8 (IF HOT)

---

## ⚠️ IMPORTANTE: URLs DE TESTING vs PRODUCCIÓN

### URLs Actuales (TESTING con Pipedream)

| Callback | URL Testing | Propósito |
|----------|-------------|-----------|
| Lead Scored | `https://eoc4ipe73sd9y75.m.pipedream.net` | Validar funcionalidad |
| Hot Lead Alert | `https://eoyvly7sjxiim05.m.pipedream.net` | Validar funcionalidad |

### URLs Producción (PENDIENTE - Backend Dev)

Según [WEB_INTEGRATION.md](../../../WEB_INTEGRATION.md):

| Callback | URL Producción | Estado |
|----------|----------------|--------|
| Lead Scored | `POST /n8n-integration-service/webhook/lead-scored` | 🔴 NO IMPLEMENTADO |
| Hot Lead Alert | `POST /n8n-integration-service/webhook/lead-hot` | 🔴 NO IMPLEMENTADO |

**📌 ACCIÓN REQUERIDA (Backend Dev)**:
1. Implementar endpoints en `n8n-integration-service`
2. Configurar en variable de entorno n8n: `BACKEND_URL`
3. Actualizar URLs en nodos 7 y 9 de SUB-A

---

## 📊 DIFERENCIAS ENTRE CALLBACKS

| Campo | Callback 1 (Scored) | Callback 2 (HOT) |
|-------|---------------------|------------------|
| **Cuándo se ejecuta** | SIEMPRE (todos los leads) | SOLO si categoria === "HOT" |
| **lead_id** | ✅ | ✅ |
| **score** | ✅ | ✅ |
| **categoria** | ✅ | ✅ |
| **ai_analysis** | ✅ (objeto completo) | ❌ |
| **processed_at** | ✅ | ❌ |
| **notified_at** | ❌ | ✅ |
| **email_sent_to** | ❌ | ✅ |
| **Propósito** | Actualizar score en BD | Alerta urgente al equipo |

---

## 🎯 PROPÓSITO DE CADA CALLBACK

### Callback 1: Lead Scored
**Objetivo**: Informar al backend que el lead fue procesado y tiene un score asignado.

**Acción esperada del backend**:
```java
// n8n-integration-service/controller/N8nWebhookController.java
@PostMapping("/webhook/lead-scored")
public ResponseEntity<Void> handleLeadScored(@RequestBody LeadScoredDTO dto) {
    // 1. Llamar a client-service
    clientServiceClient.updateLeadScore(
        dto.getLeadId(),
        dto.getScore(),
        dto.getCategoria()
    );

    // 2. Actualizar estado del lead
    // PATCH /client-service/api/leads/{leadId}
    // Body: { score: 95, categoria: "HOT", estado: "QUALIFIED" }

    return ResponseEntity.ok().build();
}
```

---

### Callback 2: Hot Lead Alert
**Objetivo**: Notificación urgente de que se detectó un lead HOT y el equipo ya fue alertado.

**Acción esperada del backend**:
```java
@PostMapping("/webhook/lead-hot")
public ResponseEntity<Void> handleHotLead(@RequestBody HotLeadDTO dto) {
    // 1. Registrar alerta en log de eventos
    alertService.logHotLeadAlert(dto);

    // 2. (Opcional) Crear tarea en case-service
    // POST /case-service/api/tasks
    // Body: { type: "HOT_LEAD_FOLLOWUP", lead_id: dto.leadId }

    // 3. (Opcional) Notificación adicional (Slack, SMS, etc.)
    notificationService.notifyHotLead(dto);

    return ResponseEntity.ok().build();
}
```

---

## 🔍 LECCIONES APRENDIDAS

### 1. Acceso a Datos de Nodos Anteriores

**Problema**: El nodo IF no podía acceder a `$json.categoria` porque recibía los datos del nodo HTTP Request anterior (que solo retorna `{data: "OK"}`).

**Solución**: Usar referencia explícita al nodo que tiene los datos:
```javascript
$('1. Validar y Clasificar1').item.json.categoria
```

---

### 2. TypeValidation en Nodos IF

**Problema**: Con `typeValidation: "strict"`, el nodo IF fallaba con error "Invalid expression".

**Solución**: Cambiar a `typeValidation: "loose"` permite referencias a nodos no directamente conectados.

---

### 3. Flujo Paralelo vs Secuencial

**Arquitectura Implementada**: Los callbacks corren en PARALELO al flujo principal (Firestore → Notificación → Email).

**Ventaja**: Si un callback falla, el flujo principal continúa y el lead se procesa correctamente.

**Configuración Clave**:
- Nodo 1 tiene 2 salidas paralelas
- Salida 1: → Nodo 2 (flujo principal)
- Salida 2: → Nodo 7 (callbacks)

---

### 4. Optimización de Payloads

**Cambio Importante**: Reduci los datos enviados en callbacks para enviar SOLO lo necesario.

**Antes (v1)**:
```json
{
  "leadId": "...",
  "email": "carolina@techventures.io",
  "nombre": "Carolina Gomez",
  "empresa": "TechVentures Capital",
  "telefono": "+57 318 999 8877",
  "score": 95,
  "categoria": "HOT",
  "servicio_interes": "Marcas",
  "mensaje": "Fondo de inversión...",
  "ai_analysis": {...}
}
```

**Después (v2 - Actual)**:
```json
{
  "lead_id": "...",
  "score": 95,
  "categoria": "HOT",
  "ai_analysis": {...},
  "processed_at": "2026-01-06T06:37:23.503Z"
}
```

**Razón**: El backend YA tiene los datos del lead (nombre, email, empresa, etc.). Solo necesita el score y análisis IA.

---

## 📅 PRÓXIMOS PASOS

### BLOQUEADO - Esperando Backend Dev

| Tarea | Responsable | Bloqueador |
|-------|-------------|------------|
| Implementar endpoint `/webhook/lead-scored` | Backend Dev | n8n-integration-service sin endpoints |
| Implementar endpoint `/webhook/lead-hot` | Backend Dev | n8n-integration-service sin endpoints |
| Configurar variable `BACKEND_URL` en n8n | Marketing Dev | Endpoints deben existir primero |
| Actualizar URLs en nodos 7 y 9 | Marketing Dev | Endpoints deben existir primero |
| Testing E2E completo | Ambos | Toda la cadena debe funcionar |

---

## ✅ VALIDACIÓN TÉCNICA

### Estructura de Nodos (16 total)

```
Triggers (1):
  └─ When Executed by Another Workflow

Procesamiento Principal (8):
  ├─ 0. Mapear Input del Orquestador1
  ├─ 0.5. Analizar Lead (IA)
  ├─ 1. Validar y Clasificar1
  ├─ 2. Guardar en Firestore1
  ├─ 3. Es Lead HOT? (If)1
  ├─ 4. Notificar Equipo (HOT)1
  ├─ 5. Generar Respuesta (Gemini)
  └─ 6. Enviar Respuesta Lead1

Callbacks (3):
  ├─ 7. Callback Lead Scored
  ├─ 8. Es Lead HOT (Callback)?
  └─ 9. Callback Hot Lead Alert

Resultado (1):
  └─ FINAL. Resultado del Sub-Workflow1

Error Handling (3):
  ├─ Error Handler
  ├─ Preparar Error
  └─ Notificar Error
```

### Conexiones (15 total)

- Flujo Principal: 10 conexiones
- Flujo Callbacks: 3 conexiones
- Error Handling: 2 conexiones

---

## 📞 HANDOFF PARA BACKEND DEV

### Datos Necesarios del Backend

Para completar la integración, necesitamos:

1. **URL Base del servicio n8n-integration-service**:
   - Dev: `http://localhost:8800`
   - Staging: `https://staging-api.carrilloabgd.com/n8n-integration-service`
   - Prod: `https://api.carrilloabgd.com/n8n-integration-service`

2. **Confirmación de endpoints implementados**:
   - `POST /webhook/lead-scored`
   - `POST /webhook/lead-hot`

3. **Estructura esperada de DTOs** (verificar con WEB_INTEGRATION.md):
   - ✅ Ya documentada en sección "Callbacks Configurados"

---

## 📄 REFERENCIAS

- [WEB_INTEGRATION.md](../../../WEB_INTEGRATION.md) - Especificación completa de integración
- [BACKEND_DEV_TASKS.md](../../../BACKEND_DEV_TASKS.md) - Tareas pendientes backend
- [ORQUESTADOR_PLAN_MW1_COMPLETO.md](../../../ORQUESTADOR_PLAN_MW1_COMPLETO.md) - Plan completo MW#1

---

**Última Actualización**: 6 de Enero, 2026 - 02:00 AM COT
**Estado**: ✅ Callbacks funcionando correctamente con webhooks de testing (Pipedream)
**Pendiente**: Integración con endpoints reales del backend (n8n-integration-service)
