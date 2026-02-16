# MEGA-WORKFLOW #1: Lead Lifecycle Manager

## Estado: ✅ OPERATIVO - Bug Mapeo Resuelto

**Ultima actualizacion:** 2026-01-21 (Bug mapeo RESUELTO, archivos reorganizados)
**Version Produccion:** 3.0.0 (Orquestador v3.0 AI Agent - ACTIVO)
**Version Legacy:** 1.1.0 (Orquestador v1.0 - **DEBE DESACTIVARSE**)
**n8n Cloud:** v1.120.4
**Webhook URL Produccion:** `https://carrilloabgd.app.n8n.cloud/webhook/lead-events-v3`
**Webhook URL Legacy (v1.0):** `https://carrilloabgd.app.n8n.cloud/webhook/lead-events`

### ✅ BUG RESUELTO (21-Ene-2026)

**Workflow Afectado**: SUB-A Lead Intake (v5)
**Nodo Afectado**: "0. Mapear Input del Orquestador1"
**Problema**: Los datos del AI Agent llegaban en campo `query` como JSON string con `payload` anidado, pero el código no lo parseaba correctamente
**Solución**: ✅ **APLICADA EN n8n CLOUD** - Código actualizado para parsear `raw.query` → `parsed.payload`
**Archivo Local**: `02-spokes/sub-a-lead-intake/SUB-A_Lead_Intake_v5.json` (sincronizado con producción)
**Análisis Histórico**: Ver `archive/deprecated_mds/2026-01-14_ANALISIS_ERROR_MAPEO_SUB-A.md`

---

## 🔄 Estado Real (Verificado 2026-01-11)

Datos obtenidos directamente de la API de n8n Cloud mediante MCP y test E2E completo:

### Instancia n8n
- **URL**: https://carrilloabgd.app.n8n.cloud
- **Versión**: 1.120.4
- **Usuario**: marketing@carrilloabgd.com
- **Workflows totales**: 4
- **Estado**: ✅ Orquestador v3.0 ACTIVO en producción, procesando leads con AI Agent

---

## Resumen

Sistema completo de captura y procesamiento de leads para Carrillo Abogados, utilizando IA (Google Gemini 2.5-pro) para análisis y scoring automático.

**Arquitectura**: Hub & Spoke + Batch Processor
- 1 Orquestador (Hub) recibe eventos via webhook
- 1 SUB-A (Spoke) procesa leads con IA (event-driven)
- 1 SUB-D (Spoke) envía secuencia de nurturing (schedule-based)

---

## Workflows en n8n Cloud

### Orquestador v1.0 (Hub - LEGACY)

| Campo | Valor |
|-------|-------|
| **ID** | `bva1Kc1USbbITEAw` |
| **Nombre** | WORKFLOW A: Lead Lifecycle Manager (Orquestador) |
| **Estado** | ⚪ **INACTIVO** (reemplazado por v3.0) |
| **Webhook** | `https://carrilloabgd.app.n8n.cloud/webhook/lead-events` |
| **Nodos** | 8 (Webhook → Identify → SubA → Consolidate → Respond + Error Handler) |
| **Ultima ejecucion** | 2026-01-04 (exito - Score 90 HOT) |
| **Total ejecuciones** | 6+ |
| **Validacion** | ✅ 0 errores |

### Orquestador v3.0 (AI Agent - PRODUCCION)

| Campo | Valor |
|-------|-------|
| **ID** | `68DDbpQzOEIweiBF` |
| **Nombre** | Orquestador v3.0 (AI Agent - Gemini) |
| **Estado** | ✅ **ACTIVO** (producción) |
| **Webhook** | `https://carrilloabgd.app.n8n.cloud/webhook/lead-events-v3` |
| **Nodos** | 10 (Webhook → AI Agent → Respond → Prepare Logger Data → Logger + Error Handler) |
| **LLM** | Google Gemini 2.0 Flash Experimental |
| **Tools** | SUB-A (Lead Intake) |
| **Arquitectura** | AI Agent (Nivel 4 - Metodologia Nate Herk) |
| **Ultima ejecucion** | 2026-01-11 (exito - Score 95 HOT, 38s latency) |
| **Tokens promedio** | 718 total (562 input + 156 output) |
| **Validacion** | ✅ Test E2E completo exitoso |

### SUB-A: Lead Intake (Spoke - Event-Driven)

| Campo | Valor |
|-------|-------|
| **ID** | `RHj1TAqBazxNFriJ` |
| **Nombre** | SUB-A: Lead Intake (v5 - AI POWERED - NATIVE) |
| **Estado** | ✅ Listo (triggered by Orquestador) |
| **Trigger** | Llamado por Orquestador via Tool |
| **Nodos** | 16 (incluye Error Handler + Callbacks Backend) |
| **IA** | Google Gemini 2.5-pro (análisis + respuesta) |
| **Última ejecución** | 2026-01-04 (éxito) |
| **Total ejecuciones** | 13+ |
| **Validación** | ✅ 0 nuevos errores (warnings menores pre-existentes) |
| **Campos Nurturing** | ✅ Agregados 2026-01-11 (5 campos para SUB-D) |

### SUB-D: Nurturing Sequence Engine (Spoke - Schedule-Based)

| Campo | Valor |
|-------|-------|
| **ID** | `PZboUEnAxm5A7Lub` |
| **Nombre** | SUB-D: Nurturing Sequence (v3 - Mailersend) |
| **Estado** | ⚪ **INACTIVO** (pendiente configuración Mailersend) |
| **Trigger** | Schedule cada 6 horas (Cron: `0 */6 * * *`) |
| **Arquitectura** | **INDEPENDIENTE** (no llamado por Orquestador) |
| **Nodos** | 16 (Schedule → Fetch Leads → Process → Send Email → Update Firestore + Error Handler) |
| **IA** | Google Gemini 2.5-pro (personalización de emails) |
| **Email Provider** | Mailersend (pendiente configuración) |
| **Validación** | ✅ 0 errores, 10 warnings menores |
| **Secuencia** | 12 emails distribuidos en 90 días |

---

## Arquitectura SUB-D: Nurturing Independiente

### Por qué SUB-D es Independiente

A diferencia de SUB-A que es llamado directamente por el Orquestador como un Tool, SUB-D opera como un **proceso batch independiente**:

1. **Trigger Schedule**: Se ejecuta cada 6 horas automáticamente
2. **Busca leads activos**: Consulta Firestore para encontrar todos los leads con `nurturing_status="active"`
3. **Procesa según posición**: Envía el email correspondiente basándose en días transcurridos y posición en secuencia
4. **Actualiza estado**: Modifica campos en Firestore (`emails_sent`, `last_email_sent`, `last_email_position`)

### Integración vía Firestore (5 campos compartidos)

| Campo | SUB-A Escribe | SUB-D Lee/Actualiza | Propósito |
|-------|---------------|---------------------|-----------|
| `nurturing_status` | "active" | Lee para filtrar, actualiza a "completed" | Control de estado del nurturing |
| `lead_captured_at` | timestamp actual | Lee para calcular días transcurridos | Base para decidir qué email enviar |
| `emails_sent` | 0 | Lee e incrementa (+1 por email) | Contador de emails enviados |
| `last_email_sent` | null | Lee y actualiza con timestamp | Fecha último email (evitar duplicados) |
| `last_email_position` | 0 | Lee y actualiza (1-12) | Posición en secuencia de 12 emails |

### Flujo de Datos

```
┌─────────────────────────────────────────────────────────────────┐
│                    MEGA-WORKFLOW #1 ARCHITECTURE                │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│ EVENT-DRIVEN FLOW (Webhook Trigger)                            │
└─────────────────────────────────────────────────────────────────┘

[Webhook POST /webhook/lead-events-v3]
    ↓
[Orquestador v3.0] (AI Agent)
    └─ Tool: SUB-A Lead Intake
         ↓
    [SUB-A] (16 nodos)
         ├─ Analizar Lead (Gemini IA)
         ├─ Guardar en Firestore ← ESCRIBE campos nurturing
         ├─ Notificar Equipo (si HOT)
         ├─ Enviar Respuesta Lead
         └─ Callbacks Backend

┌─────────────────────────────────────────────────────────────────┐
│ SCHEDULE-BASED FLOW (Cron Trigger - cada 6h)                   │
└─────────────────────────────────────────────────────────────────┘

[Schedule Trigger] (cada 6 horas)
    ↓
[SUB-D] (16 nodos - INDEPENDIENTE)
    ├─ 1. Fetch Active Leads (Firestore) ← LEE nurturing_status="active"
    ├─ 2. Split Out (1 lead por iteración)
    ├─ 3. Calcular Días Transcurridos ← LEE lead_captured_at
    ├─ 4. Determinar Email Posición (Code)
    ├─ 5. Ya envió este email? (IF) ← LEE last_email_position
    │   └─ [No enviado] → continúa
    ├─ 6. Cargar Template Email (Code)
    ├─ 7. Personalizar con IA (Gemini)
    ├─ 8. Enviar Email (Mailersend)
    ├─ 9. Actualizar Firestore ← ACTUALIZA emails_sent, last_email_sent, last_email_position
    ├─ 10. Es email final? (IF)
    │   └─ [Email 12] → 11. Marcar Completado ← ACTUALIZA nurturing_status="completed"
    └─ Error Handler → Notificar Error
```

### Secuencia de 12 Emails

| # | Día | Trigger | Objetivo | Template |
|---|-----|---------|----------|----------|
| 1 | 0 | Inmediato (SUB-A) | Bienvenida personalizada | Agradecimiento + próximos pasos |
| 2 | 3 | SUB-D | Educativo | Por qué proteger tu marca |
| 3 | 7 | SUB-D | Social Proof | Case study cliente éxito |
| 4 | 10 | SUB-D | Recurso gratuito | Checklist registro marca |
| 5 | 14 | SUB-D | Urgencia | 3 riesgos de NO registrar |
| 6 | 21 | SUB-D | Autoridad | Dr. Carrillo - 15 años SIC |
| 7 | 28 | SUB-D | Oferta | Consulta gratuita 30min |
| 8 | 35 | SUB-D | Re-engagement | ¿Aún interesado? |
| 9 | 42 | SUB-D | Contenido | Tendencias PI 2026 |
| 10 | 49 | SUB-D | Last chance | Oferta expira |
| 11 | 56 | SUB-D | Break-up | Nos despedimos |
| 12 | 90 | SUB-D | Win-back | Última oportunidad |

### Lógica de Envío (Nodo 4: Determinar Email Posición)

```javascript
// Calcular días transcurridos
const capturedAt = new Date($json.lead_captured_at);
const today = new Date();
const daysElapsed = Math.floor((today - capturedAt) / (1000 * 60 * 60 * 24));

// Mapeo días → posición email
const emailSchedule = [
  { position: 1, day: 0 },   // Bienvenida (SUB-A lo envía)
  { position: 2, day: 3 },
  { position: 3, day: 7 },
  { position: 4, day: 10 },
  { position: 5, day: 14 },
  { position: 6, day: 21 },
  { position: 7, day: 28 },
  { position: 8, day: 35 },
  { position: 9, day: 42 },
  { position: 10, day: 49 },
  { position: 11, day: 56 },
  { position: 12, day: 90 }
];

// Encontrar email correspondiente
const currentEmail = emailSchedule.find(e => e.day === daysElapsed);

// Validar que no se haya enviado ya
if (currentEmail && $json.last_email_position < currentEmail.position) {
  return { shouldSend: true, position: currentEmail.position };
} else {
  return { shouldSend: false };
}
```

---

## Flujo de Datos Completo (v3.0)

```
[Webhook POST /webhook/lead-events-v3]
    ↓
[Orquestador v3.0] (10 nodos - AI Agent)
    ├── Webhook Principal Lead Events v3
    ├── AI Agent Orchestrator (Gemini 2.0 Flash)
    │   ├── Language Model: Google Gemini
    │   ├── Tool: lead_intake (SUB-A)
    │   └── Memory: Simple Memory (3 context)
    ├── Respond to Webhook (HTTP 200)
    ├── Prepare Logger Data (Set - 8 campos)
    ├── Logger: Google Sheets
    └── Error Handler → Preparar Datos Error → Notificar Error Email
         ↓
    [SUB-A] (16 nodos)
        ├── When Executed by Another Workflow
        ├── 0. Mapear Input
        ├── 0.5. Analizar Lead (Gemini IA)
        ├── 1. Validar y Clasificar
        ├── 2. Guardar en Firestore (+ 5 campos nurturing)
        ├── 3. Es Lead HOT? (IF)
        │   ├── [HOT ≥70] → 4. Notificar Equipo
        │   └── [WARM/COLD] → continúa
        ├── 5. Generar Respuesta (Gemini)
        ├── 6. Enviar Respuesta Lead
        ├── 7. Callback Lead Scored (HTTP → Backend)
        ├── 8. Es Lead HOT (Callback)? (IF)
        │   └── [HOT] → 9. Callback Hot Lead Alert
        ├── FINAL. Resultado
        └── Error Handler → Preparar Error → Notificar Error

[Schedule Trigger cada 6h] (independiente)
    ↓
[SUB-D] (16 nodos)
    ├── 1. Fetch Active Leads (Firestore)
    ├── 2-12. Process & Send Emails
    └── Error Handler
```

---

## Callbacks Backend

SUB-A incluye un sistema de callbacks de dos niveles para notificar al backend de la plataforma Spring Boot sobre el procesamiento del lead:

### Arquitectura de Callbacks

```
SUB-A (n8n)
    ├── 1. Validar y Clasificar (IA) → Score + Categoría
    ├── 2. Guardar en Firestore
    ├── ...
    ├── 7. Callback Lead Scored (HTTP POST) ← SIEMPRE ejecuta
    ├── 8. Es Lead HOT (Callback)? (IF)
    │   └── [HOT] → 9. Callback Hot Lead Alert (HTTP POST)
    └── FINAL. Resultado
         ↓
    [Backend - n8n-integration-service]
         ├── POST /webhook/lead-scored → Actualiza score en PostgreSQL
         └── POST /webhook/lead-hot → Notificación urgente para equipo
```

### Callback 1: Lead Scored (Universal)

**Propósito**: Notificar a la plataforma el score calculado por IA para TODOS los leads (HOT/WARM/COLD)

**Nodo**: 7. Callback Lead Scored
**Tipo**: HTTP Request v4.2
**Método**: POST
**URL Testing**: `https://eoc4ipe73sd9y75.m.pipedream.net`
**URL Producción**: `${BACKEND_URL}/webhook/lead-scored`
**Trigger**: SIEMPRE (se ejecuta para todos los leads)
**Error Handling**: `onError: continueRegularOutput` (no falla el workflow si el callback falla)

**Payload enviado**:
```json
{
  "lead_id": "2026-01-11T02:08:10.022Z-laura.martinez-at-innovatech.com",
  "score": 95,
  "categoria": "HOT",
  "ai_analysis": {
    "normalized_interest": "Marcas",
    "is_spam": false,
    "analysis_reason": "El lead es un decision-maker clave (CEO)...",
    "calculated_score": 95,
    "category": "HOT"
  },
  "processed_at": "2026-01-11T02:08:10.022Z"
}
```

**Acción esperada del backend**:
- Actualizar tabla `leads` en PostgreSQL
- Campos: `score`, `categoria`, `estado = QUALIFIED`
- Endpoint: `PATCH /api/leads/{lead_id}/score`

---

### Callback 2: Hot Lead Alert (Condicional)

**Propósito**: Notificación de urgencia para leads HOT (score ≥70) que requieren atención inmediata del equipo comercial

**Nodo**: 9. Callback Hot Lead Alert
**Tipo**: HTTP Request v4.2
**Método**: POST
**URL Testing**: `https://eoyvly7sjxiim05.m.pipedream.net`
**URL Producción**: `${BACKEND_URL}/webhook/lead-hot`
**Trigger**: SOLO leads HOT (evaluado por nodo 8 IF)
**Error Handling**: `onError: continueRegularOutput`

**Payload enviado**:
```json
{
  "lead_id": "2026-01-11T02:08:10.022Z-laura.martinez-at-innovatech.com",
  "score": 95,
  "categoria": "HOT",
  "notified_at": "2026-01-11T02:08:15.000Z",
  "email_sent_to": "marketing@carrilloabgd.com"
}
```

**Acción esperada del backend**:
- Crear notificación en sistema (dashboard, email, Slack, etc.)
- Alertar al equipo comercial
- Registrar en tabla de notificaciones

---

### Configuración en n8n

**Variable de Entorno**:
- Nombre: `BACKEND_URL`
- Valor Testing: URLs Pipedream individuales
- Valor Producción: `http://n8n-integration-service:8800/n8n-integration-service` (Docker) o `https://api.carrilloabgd.com/n8n-integration-service` (GCP)

**Estado Actual** (2026-01-14):
- ✅ Callbacks implementados en SUB-A (nodos 7, 8, 9)
- ✅ Testing con Pipedream exitoso (ambos callbacks reciben datos)
- ⏳ Backend endpoints pendientes de implementación
- ⏳ Variable BACKEND_URL pendiente de configurar en producción
- 🔴 **PROBLEMA DETECTADO**: Backend envía eventos a `/webhook/lead-events` (v1.0 LEGACY) en lugar de `/webhook/lead-events-v3` (v3.0 ACTIVO)

---

### Testing de Callbacks

**Verificar en Pipedream**:
1. Lead Scored: https://pipedream.com/@username/eoc4ipe73sd9y75 (debe recibir TODOS los leads)
2. Hot Lead: https://pipedream.com/@username/eoyvly7sjxiim05 (solo leads HOT)

**Test E2E exitoso** (2026-01-11):
- Lead: Laura Martinez (Score 95 HOT)
- ✅ Callback 1 recibió payload completo con ai_analysis
- ✅ Callback 2 recibió notificación HOT

---

## Payload Esperado

```json
{
  "event_type": "new_lead",
  "nombre": "string (requerido)",
  "email": "string (requerido)",
  "telefono": "string (opcional)",
  "empresa": "string (opcional)",
  "cargo": "string (opcional)",
  "servicio_interes": "string (opcional)",
  "mensaje": "string (opcional)",
  "utm_source": "string (opcional)",
  "utm_campaign": "string (opcional)"
}
```

---

## Servicios Externos

| Servicio | Propósito | Credential ID | Usado por |
|----------|-----------|---------------|-----------|
| Google Gemini 2.5-pro | Análisis IA y generación de respuestas | `jk2FHcbAC71LuRl2` | SUB-A, SUB-D |
| Gmail OAuth2 | Envío de emails | `l2mMgEf8YUV7HHlK` | SUB-A |
| Google Firestore | Base de datos de leads | `AAhdRNGzvsFnYN9O` | SUB-A, SUB-D |
| Mailersend | Email marketing con tracking | Pendiente configurar | SUB-D |

---

## Categorización de Leads

| Categoría | Score | Acción SUB-A | Acción SUB-D |
|-----------|-------|--------------|--------------|
| 🔥 **HOT** | >= 70 | Email notificación a equipo + respuesta automática | Nurturing personalizado 12 emails |
| 🟡 **WARM** | 40-69 | Respuesta automática | Nurturing estándar 12 emails |
| 🔵 **COLD** | < 40 | Respuesta automática | Nurturing básico 12 emails |

---

## Historial de Cambios

### 2026-01-21 - Bug Mapeo RESUELTO + Reorganización Documentación

**Agente**: Orquestador (coordinando Documentation, QA-Specialist, Explore, Engineer)

**Bug Resuelto**:
- ✅ Fix aplicado en n8n Cloud (nodo "0. Mapear Input del Orquestador1")
- ✅ Código ahora parsea `raw.query` → `parsed.payload` correctamente
- ✅ Archivo JSON local sincronizado: `SUB-A_Lead_Intake_v5.json`

**Reorganización según Agent Protocols**:
- ✅ 7 archivos movidos a `archive/deprecated_mds/`:
  - `2026-01-14_ANALISIS_ERROR_MAPEO_SUB-A.md`
  - `2026-01-14_GUIA_APLICAR_FIX_SUB-A.md`
  - `2026-01-14_FIXED_MAPEAR_INPUT_SUB-A.js`
  - `2026-01-14_FIXED_MAPEAR_INPUT_V2_SUB-A.js`
  - `2026-01-14_TEST_REPORT_V1_SUB-A.md`
  - `2026-01-14_VALIDATION_REPORT_QA_SUB-A.md`
  - `2026-01-14_VALIDATION_RESULTS_SUB-A.json`
- ✅ Carpeta `testing/` eliminada (contenido archivado)
- ✅ Estructura conforme a Agent Protocols

**Estructura final sub-a-lead-intake/**:
```
sub-a-lead-intake/
├── SUB-A_Lead_Intake_v5.json  (sincronizado con producción)
└── test-data/
    └── *.json
```

**Pendientes identificados**:
- ⚠️ Desactivar Orquestador v1.0 legacy (`bva1Kc1USbbITEAw`) manualmente en n8n Cloud
- ⚠️ Verificar backend envía a `/webhook/lead-events-v3` (no v1.0)

---

### 2026-01-11 - SUB-D: Documentación Arquitectura Completa

**Agente**: Documentation
**Operaciones aplicadas**: 1 (actualización STATUS.md)

**Documentación agregada**:
- ✅ Arquitectura SUB-D como workflow independiente (schedule-based)
- ✅ Flujo de datos vía Firestore (5 campos compartidos con SUB-A)
- ✅ Secuencia de 12 emails con días específicos
- ✅ Lógica de envío (código JavaScript)
- ✅ Integración con Mailersend (pendiente configuración)

**Limpieza realizada**:
- ✅ Movidos 6 archivos .md de `testing/` a `archive/deprecated_mds/`:
  - integration_validation_report_v1.md
  - manual_validation_checklist.md
  - README.md
  - test_report_v1.md
  - validation_checklist.md
  - VALIDATION_SUMMARY.md

**Estructura final SUB-D**:
```
sub-d-nurturing/
├── SUB-D_ Workflow-V3.json      # Workflow principal
├── VALIDATION_RESULTS.json      # Resultados validación MCP
├── test-data/                   # Datos de prueba
│   └── *.json
└── testing/                     # Scripts de testing
    ├── test_commands.sh
    └── test_data.json
```

---

### 2026-01-11 - SUB-A: Campos Nurturing Agregados

**Agente**: Engineer
**Operaciones aplicadas**: 2

Se actualizaron dos nodos de SUB-A para soportar la secuencia de nurturing (SUB-D):

**Nodo 1: Validar y Clasificar1** (ID: e627be60-cd86-40ba-8763-beac05bde7c2)
- Agregados 5 campos nuevos al return del codigo JavaScript

**Nodo 2: Guardar en Firestore1** (ID: 5d6463dc-d890-4a48-bbe0-c1067f520993)
- Actualizado parametro `columns` para incluir campos de nurturing

**Campos agregados**:
| Campo | Tipo | Valor Inicial | Uso en SUB-D |
|-------|------|---------------|--------------|
| `nurturing_status` | string | "active" | Control de secuencia |
| `lead_captured_at` | ISO string | fecha actual | Calculo dias en secuencia |
| `emails_sent` | number | 0 | Contador de emails |
| `last_email_sent` | ISO string/null | null | Fecha ultimo envio |
| `last_email_position` | number | 0 | Posicion en secuencia (0-12) |

**Columns Firestore actualizado**:
```
lead_id, nombre, email, empresa, telefono, servicio, mensaje, score, categoria, processed_at, nurturing_status, lead_captured_at, emails_sent, last_email_sent, last_email_position
```

---

### 2026-01-11 - ORQUESTADOR v3.0 ACTIVADO EN PRODUCCION ✅

**Test E2E Completo Exitoso**

- ✅ Orquestador v3.0 ACTIVADO en producción
- ✅ Orquestador v1.0 DESACTIVADO (legacy)
- ✅ Webhook producción cambiado a `/webhook/lead-events-v3`
- ✅ Logger Google Sheets configurado y funcionando
- ✅ Agregado nodo "Prepare Logger Data" (Set node) para estructurar datos
- ✅ Test E2E completo con payload real:
  - Lead: Laura Martinez (InnovaTech Solutions)
  - Score: 95 (HOT)
  - AI Agent identificó correctamente event_type: "new_lead"
  - Ejecutó tool: "lead_intake" (SUB-A)
  - Tokens consumidos: 718 (562 input + 156 output)
  - Latency total: 38.143 segundos
  - Lead ID generado: `2026-01-11T02:08:10.022Z-laura.martinez-at-innovatech.com`

**Configuración final**:
- ✅ Modelo IA: Gemini 2.0 Flash Experimental (no 2.5 Pro)
- ✅ Credencial Gemini renovada (API expirada corregida manualmente)
- ✅ Google Sheets OAuth2 configurado
- ✅ Logger registra 8 campos estructurados: timestamp, event_type, tool_used, decision_reason, execution_status, latency_ms, error_message, output

**Archivos actualizados**:
- Workflow Orquestador v3.0 en n8n Cloud (ID: 68DDbpQzOEIweiBF)
- STATUS.md (este archivo)

---

### 2026-01-07 - ORQUESTADOR v3.0 (AI Agent - Gemini) CREADO

**Metodologia**: Nate Herk AI Systems Pyramid - Nivel 4 (AI Agent)

- NUEVO workflow `Orquestador v3.0 (AI Agent - Gemini)` creado
- **ID**: `68DDbpQzOEIweiBF`
- **Webhook**: `/webhook/lead-events-v3` (testing)
- **Nodos**: 9
- **LLM**: Google Gemini 2.5 Pro (temperature 0.3)
- **Arquitectura**:
  - AI Agent Node con System Prompt completo
  - Tool conectado: SUB-A (Lead Intake)
  - Simple Memory (Window Buffer, 5 eventos)
  - Logger Google Sheets (pendiente configurar)
  - Error Handler + Notificacion Gmail

**Cambios vs v1.0**:
| Aspecto | v1.0 (Actual) | v3.0 (Nuevo) |
|---------|---------------|--------------|
| Logica routing | Code node (30 lineas) | AI Agent |
| Escalabilidad | Modificar codigo | Agregar tools |
| Observabilidad | Limitada | returnIntermediateSteps + Logger |
| Costo por ejecucion | $0 | ~$0.003 USD |
| Latencia | ~100ms | ~2-3 seg |

**Estado v3.0** (2026-01-11): ✅ ACTIVO EN PRODUCCION
1. ✅ Creado Google Sheet `MW1_Orchestrator_Logs`
2. ✅ Configurada credencial Google Sheets OAuth2
3. ✅ Testing manual con payload `new_lead` exitoso
4. ✅ Activado en producción
5. ✅ Agregado nodo "Prepare Logger Data" para estructurar logs

**Archivos generados**:
- `artifacts/ORQUESTADOR_V3_DRAFT.json`
- `artifacts/IMPLEMENTATION_NOTES_ORQUESTADOR_V3.md`
- `artifacts/code_snippets/system_prompt_ai_agent.md`

---

### 2026-01-05 - TAREA 1: Callbacks Backend Agregados ✅
- ✅ Agregados 3 nodos nuevos a SUB-A (ahora 16 nodos total)
- ✅ **Nodo 7: Callback Lead Scored** - HTTP POST a `/webhook/lead-scored` (SIEMPRE ejecuta)
- ✅ **Nodo 8: Es Lead HOT (Callback)?** - IF evalúa categoria === "HOT"
- ✅ **Nodo 9: Callback Hot Lead Alert** - HTTP POST a `/webhook/lead-hot` (solo HOT)
- ✅ Conexiones correctas: 6→7→8→9
- ✅ Variable entorno `BACKEND_URL` configurada (fallback: localhost:8800)
- ✅ Error handling con `onError: continueRegularOutput`
- ⚠️ Warnings menores pre-existentes (typeVersions) - no bloquean

### 2026-01-04 - ACTIVACIÓN PRODUCCIÓN ✅
- ✅ Corregido error webhook `onError: continueRegularOutput`
- ✅ Agregado Error Handler a Orquestador (3 nodos)
- ✅ Agregado Error Handler a SUB-A (3 nodos)
- ✅ Configuradas credenciales Gmail para notificaciones de error
- ✅ Orquestador ACTIVADO en producción
- ✅ Test E2E exitoso (Score 90, categoría HOT)

### 2025-12-21 - Debugging y Reparación Completa
- ✅ Corregido mapeo de datos (eliminado optional chaining `?.`)
- ✅ Corregido nodo IF (agregada estructura `options`)
- ✅ Corregido nodo Gmail (agregado `operation: send`)
- ✅ Recreado Orquestador desde cero (anterior estaba corrupto)
- ✅ Validación E2E exitosa

### 2025-12-17 - Versión Inicial
- Creación de SUB-A con integración Gemini
- Configuración de Firestore

---

## Artifacts

| Archivo | Ubicación |
|---------|-----------|
| Orquestador v3.0 JSON | `01-orchestrator/artifacts/ORQUESTADOR_V3_PRODUCTION_2026-01-11.json` |
| Orquestador v1.0 JSON | `01-orchestrator/artifacts/ORQUESTADOR_PRODUCTION_2025-12-21.json` |
| SUB-A JSON | `02-spokes/sub-a-lead-intake/artifacts/SUB-A_PRODUCTION_2025-12-21.json` |
| SUB-D JSON | `02-spokes/sub-d-nurturing/SUB-D_ Workflow-V3.json` |

---

## Notas Técnicas

### Expresiones n8n
- ❌ NO usar optional chaining (`?.`) - no soportado
- ✅ Usar ternarios: `$json.x ? $json.x : ($json.y ? $json.y.x : '')`

### Nodo IF
- Requiere `options` dentro de `conditions` con estructura completa

### Gmail Node
- Siempre especificar `operation: "send"` explícitamente

### Schedule Node (SUB-D)
- Cron cada 6 horas: `0 */6 * * *` (00:00, 06:00, 12:00, 18:00 UTC)
- Trigger independiente, no llamado por Orquestador

---

## 🔧 Validación n8n (2026-01-11)

### Estado de Validación: ✅ APROBADO

| Workflow | Errores | Warnings | Estado |
|----------|---------|----------|--------|
| Orquestador v3.0 | 0 | 1 (typeVersion) | ✅ Activo |
| Orquestador v1.0 | 0 | 1 (typeVersion) | ⚪ Inactivo |
| SUB-A | 0 | 4 (typeVersions) | ✅ Listo |
| SUB-D | 0 | 10 (typeVersions) | ⚪ Pendiente Mailersend |

### Correcciones Aplicadas (2026-01-04)

| Problema | Solución | Estado |
|----------|----------|--------|
| Webhook sin `onError` | Agregado `continueRegularOutput` | ✅ Corregido |
| Sin Error Handling | Agregado Error Trigger + Set + Gmail | ✅ Corregido |
| Credenciales Gmail | Configuradas en n8n UI | ✅ Activas |

### Warnings Pendientes (P2 - No bloquean)

| Workflow | Nodo | Actual → Recomendada |
|----------|------|----------------------|
| Orquestador v3.0 | Notificar Error Email | 2.1 → 2.2 |
| SUB-A | If Node | 2 → 2.3 |
| SUB-A | Gmail (x2) | 2.1 → 2.2 |
| SUB-A | Notificar Error | 2.1 → 2.2 |
| SUB-D | Multiple nodes | Varios → 2.x |

---

## 🚀 Estado de Acciones

### ✅ P0 - COMPLETADO

1. ✅ **Error Webhook Orquestador** - Corregido `onError: continueRegularOutput`
2. ✅ **Error Handling** - Agregado a ambos workflows (Error Trigger → Set → Gmail)
3. ✅ **Activación Orquestador v3.0** - Activo en producción
4. ✅ **Test E2E v3.0** - Lead procesado exitosamente (Score 95, HOT)
5. ✅ **Campos Nurturing SUB-A** - 5 campos agregados a Firestore
6. ✅ **Arquitectura SUB-D documentada** - STATUS.md actualizado

### 🔴 P0 - BLOQUEADOR CRÍTICO (2026-01-14)

**PROBLEMA IDENTIFICADO**: Backend envía eventos al Orquestador v1.0 (legacy inactivo) en lugar del v3.0 (activo)

**Causa raíz**: `N8nCloudConfig.java` tiene hardcodeado `/webhook/lead-events` (v1.0) pero el Orquestador activo es v3.0 con webhook `/webhook/lead-events-v3`

**Impacto**: 
- ✅ SUB-A se ejecuta correctamente (recibe datos)
- ❌ Orquestador v3.0 NO se ejecuta (no recibe tráfico)
- ⚠️ No hay logs del Orquestador en Google Sheets
- ⚠️ AI Agent no está procesando decisiones

**Solución inmediata**: Actualizar `N8nCloudConfig.java`

### 🟡 P1 - En Progreso

7. **Configurar Mailersend (SUB-D)**
   - ⏳ Crear cuenta Mailersend
   - ⏳ Verificar dominio carrilloabgd.com
   - ⏳ Obtener API Key
   - ⏳ Configurar credencial en n8n Cloud
   - ⏳ Activar SUB-D en producción

8. **Integración Web Completa**
   - ✅ client-service Lead API funcionando
   - ✅ n8n-integration-service webhooks implementados
   - ⏳ Conectar NATS → n8n webhook (pendiente prueba E2E)
   - ⏳ Frontend /contacto → flujo completo

### 🟢 P2 - Mejoras Futuras

9. **Actualizar typeVersions** (warnings, no crítico)
   - Click "Update" en nodos con banner amarillo en n8n UI

10. **Monitoreo**
    - Dashboard Grafana para métricas de leads
    - Alertas ante fallos de workflows

---

## 🔗 Integración con Plataforma Web

### Arquitectura de Conexión

```
┌──────────────────────┐    ┌──────────────────────┐    ┌────────────────────┐
│   Frontend Web       │    │  client-service      │    │  n8n Cloud         │
│   (Formulario)       │    │  (Spring Boot)       │    │                    │
├──────────────────────┤    ├──────────────────────┤    ├────────────────────┤
│ POST /api/leads      │───►│ LeadResource.java    │    │                    │
│                      │    │   └─ NATS Event      │───►│ (pendiente)        │
│                      │    │      "lead.created"  │    │                    │
└──────────────────────┘    └──────────────────────┘    └────────────────────┘
                                      │
                                      ▼
                            ┌──────────────────────┐
                            │ n8n-integration-svc  │
                            │ NatsEventListener    │
                            │   └─ HTTP POST ──────┼───► Webhook lead-events-v3
                            └──────────────────────┘
                                      ▲
                                      │
                            ┌──────────────────────┐
                            │ n8n Callbacks        │
                            │ WebhookController    │
                            │   /webhook/lead-scored
                            │   /webhook/lead-hot  │
                            └──────────────────────┘
```

### Endpoints del Microservicio

| Servicio | Endpoint | Propósito |
|----------|----------|-----------|
| client-service | `POST /api/leads` | Captura lead desde formulario web |
| n8n-integration-service | `POST /webhook/lead-scored` | n8n envía score calculado |
| n8n-integration-service | `POST /webhook/lead-hot` | n8n notifica lead urgente |
| n8n-integration-service | `GET /webhook/health` | Health check |

### Flujo de Activación

| Paso | Descripción | Estado |
|------|-------------|--------|
| 1 | Activar Orquestador v3.0 en n8n Cloud | ✅ ACTIVO |
| 2 | Verificar webhook accesible | ✅ Funcionando |
| 3 | Configurar NatsEventListener | ✅ Implementado |
| 4 | Desplegar n8n-integration-service | ⏳ Pendiente deploy |
| 5 | Probar flujo E2E completo | ⏳ Pendiente |

### URLs de Producción

| Componente | URL |
|------------|-----|
| **n8n Webhook v3.0** | `https://carrilloabgd.app.n8n.cloud/webhook/lead-events-v3` |
| **n8n Webhook v1.0** | `https://carrilloabgd.app.n8n.cloud/webhook/lead-events` (legacy) |
| **API Gateway** | `https://api.carrilloabgd.com` (pendiente deploy) |
| **Frontend** | `https://carrilloabgd.com/contacto` (pendiente deploy) |

---

## 📊 Métricas de Ejecución

### Últimas Ejecuciones (Enero 2026)

**Orquestador v3.0 (AI Agent):**
| Fecha | Estado | Score | Categoría | Latency | Tokens |
|-------|--------|-------|----------|---------|--------|
| 2026-01-11 | ✅ Éxito | 95 | HOT | 38.1s | 718 |

**Orquestador v1.0 (Legacy - Inactivo):**
| Fecha | Estado | Score | Categoría |
|-------|--------|-------|----------|
| 2026-01-04 | ✅ Éxito | 90 | HOT |
| 2026-01-04 | ✅ Éxito | 90 | HOT |
| 2026-01-04 | ✅ Éxito | 90 | HOT |

**SUB-A:**
| Fecha | Estado | Notas |
|-------|--------|-------|
| 2026-01-11 | ✅ Éxito | Lead procesado via AI Agent v3.0 (Score 95 HOT) |
| 2026-01-04 | ✅ Éxito | Lead procesado con Gemini AI |
| 2026-01-04 | ✅ Éxito | Email enviado al equipo (HOT) |
| 2026-01-04 | ✅ Éxito | Guardado en Firestore |

**SUB-D:**
| Fecha | Estado | Notas |
|-------|--------|-------|
| Pendiente | ⚪ No ejecutado | Pendiente configuración Mailersend |

---

## 🔐 Credenciales Verificadas

| Credencial | ID | Estado | Última verificación |
|------------|------|--------|---------------------|
| Google Gemini API | `jk2FHcbAC71LuRl2` | ✅ Activo | 2026-01-11 |
| Gmail OAuth2 | `l2mMgEf8YUV7HHlK` | ✅ Activo | 2026-01-04 |
| Google Firestore | `AAhdRNGzvsFnYN9O` | ✅ Activo | 2026-01-04 |
| Mailersend | Pendiente | ⚠️ No configurado | - |

---

## 🧪 Comando de Prueba Directa

### Webhook Producción (v3.0 - ACTIVO)

```bash
curl -X POST https://carrilloabgd.app.n8n.cloud/webhook/lead-events-v3 \
  -H "Content-Type: application/json" \
  -d '{
    "event_type": "new_lead",
    "nombre": "Test Lead",
    "email": "test@example.com",
    "telefono": "+573001234567",
    "empresa": "Test Company",
    "cargo": "CEO",
    "servicio_interes": "Registro de Marca",
    "mensaje": "Necesito proteger mi marca urgentemente.",
    "utm_source": "test",
    "utm_campaign": "manual"
  }'
```

### Respuesta Esperada (v3.0)

```json
{
  "status": "success",
  "lead_id": "2026-01-11T02:08:10.022Z-laura.martinez-at-innovatech.com",
  "score": 95,
  "categoria": "HOT"
}
```

---

## 🔍 TROUBLESHOOTING

### Problema: SUB-A se ejecuta pero Orquestador no

**Síntoma**: Al enviar formulario desde web, SUB-A registra ejecuciones pero el Orquestador v3.0 no aparece en historial.

**Causa raíz**: El backend (`n8n-integration-service`) está enviando eventos al webhook incorrecto:
- ❌ Enviaba a: `/webhook/lead-events` (Orquestador v1.0 INACTIVO)
- ✅ Debe enviar a: `/webhook/lead-events-v3` (Orquestador v3.0 ACTIVO)

**¿Por qué SUB-A se ejecutaba?**:
El Orquestador v1.0 (aunque inactivo) aún tiene configurado llamar a SUB-A mediante "Execute Workflow". Cuando recibía eventos, los reenviaba a SUB-A sin procesar.

**Solución aplicada** (2026-01-14):
1. ✅ Actualizado `N8nCloudConfig.java`: `leadEvents = "/webhook/lead-events-v3"`
2. ⏳ Rebuild `n8n-integration-service`: `./mvnw clean package -DskipTests -pl n8n-integration-service`
3. ⏳ Restart Docker: `docker-compose restart n8n-integration-service`
4. ⏳ Verificar con curl:
   ```bash
   # Este es el webhook correcto ahora:
   curl -X POST http://localhost:8800/n8n-integration-service/api/test-lead-event
   
   # Debe enviar a:
   https://carrilloabgd.app.n8n.cloud/webhook/lead-events-v3
   ```

**Validación post-fix**:
1. Enviar formulario desde frontend
2. ✅ Verificar ejecución en Orquestador v3.0 (n8n UI → Executions)
3. ✅ Verificar log en Google Sheets `MW1_Orchestrator_Logs`
4. ✅ Verificar ejecución de SUB-A llamada como Tool (no independiente)

---

## 🎯 DIFERENCIAS ORQUESTADOR v1.0 vs v3.0

| Aspecto | v1.0 (INACTIVO) | v3.0 (ACTIVO) |
|---------|----------------|---------------|
| **Webhook URL** | `/webhook/lead-events` | `/webhook/lead-events-v3` |
| **Arquitectura** | Code Node routing | AI Agent + Tools |
| **Cómo llama SUB-A** | Execute Workflow node | AI Agent Tool |
| **Observabilidad** | Sin logs estructurados | Logger en Google Sheets |
| **Estado** | ⚪ Inactivo | ✅ Activo |
| **Última ejecución** | 2026-01-04 | 2026-01-11 |

**⚠️ IMPORTANTE**: Ambos workflows pueden estar activos en n8n Cloud, pero solo v3.0 debe recibir tráfico de producción.

---

### Problema: Ejecuciones Duplicadas (3x el mismo lead)

**Síntoma**: El mismo lead aparece procesado 3 veces en n8n con timestamps casi idénticos.

**Causa raíz**: El backend (`N8nWebhookService.java`) tiene configurado un **mecanismo de retry automático**:

```java
public static class Retry {
    private int maxAttempts = 3;  // 🔴 ESTO causa las 3 ejecuciones
    private int delayMillis = 1000;
}
```

**¿Por qué reintenta 3 veces?**

El código del backend tiene esta lógica en `sendWithRetry()`:

1. **Intento 1**: Envía evento a n8n → n8n responde 200 OK → ❌ Backend NO detecta el éxito correctamente
2. **Espera 1 segundo** (delayMillis = 1000)
3. **Intento 2**: Reenvía el mismo evento → n8n responde 200 OK → ❌ Backend NO detecta el éxito
4. **Espera 1 segundo**
5. **Intento 3**: Reenvía el mismo evento → n8n responde 200 OK → ❌ Backend NO detecta el éxito
6. **Fallo después de 3 intentos**

**¿Por qué no detecta el éxito?**

Posibles causas:
- n8n Cloud responde **antes** de que el webhook termine de ejecutarse
- El backend verifica `response.getStatusCode().is2xxSuccessful()` pero n8n puede estar respondiendo con un redirect o código no estándar
- Timeout muy corto (30 segundos) vs latencia del workflow (38+ segundos)

**Soluciones**:

**Opción 1: Desactivar reintentos** (recomendado para desarrollo)
```yaml
# application.yml
n8n:
  cloud:
    retry:
      max-attempts: 1  # Sin reintentos
```

**Opción 2: Aumentar timeout y validar respuesta**
```java
// N8nCloudConfig.java
private int timeoutSeconds = 60;  // De 30s a 60s

// N8nWebhookService.java - Mejorar detección de éxito
if (response.getStatusCode().is2xxSuccessful() || 
    response.getStatusCode().is3xxRedirection()) {
    log.info("Successfully sent event to n8n");
    return true;  // 🔴 Asegurar que retorna true
}
```

**Opción 3: Idempotencia en n8n** (solución a largo plazo)
- Agregar nodo en Orquestador que verifique si el lead_id ya existe en Firestore
- Si existe, retornar 200 OK sin procesar
- Implementar caché de 5 minutos con Redis

**Solución inmediata aplicada** (2026-01-14):
Desactivar reintentos cambiando `maxAttempts` de 3 a 1 en `N8nCloudConfig.java`

---

### Problema: AI Agent alcanza límite de 3 iteraciones (Max iterations reached)

**Síntoma**: Error en n8n "Max iterations (3) reached. The agent could not complete the task within the allowed number of iterations."

**Causa raíz REAL** (14 Ene 2026 - 13:15): El Tool **SÍ está llamando a SUB-A** correctamente, pero **SUB-A está fallando con validación de email**:

```json
{
  "response": "There was an error: \"Invalid email address (item 0)\""
}
```

**Flujo del error real**:
1. AI Agent recibe payload del webhook ✅
2. AI Agent ejecuta tool `lead_intake` ✅
3. Tool llama a SUB-A **con payload completo** ✅
4. **SUB-A valida el email** ❌ FALLA aquí
5. SUB-A retorna error "Invalid email address"
6. AI Agent reintenta (iteración 2) → mismo error
7. AI Agent reintenta (iteración 3) → mismo error
8. AI Agent agota iteraciones → "Max iterations reached"

**Problema específico**:
SUB-A tiene un nodo de validación de email que rechaza:
- `alexisj4a@gmail.com` → ❌ "Invalid email address"
- `verify@production.test` → ❌ "Invalid email address"

**Posibles causas del error de validación**:
1. El nodo Gmail en SUB-A tiene validación estricta de email
2. El formato del email en el payload no coincide con lo que espera SUB-A
3. Hay un nodo de validación de email que está mal configurado
4. El campo `email` está llegando con espacios extra o encoding especial

**Solución**:
Necesitas revisar SUB-A (workflow ID: `RHj1TAqBazxNFriJ`) en n8n Cloud y buscar:
- Nodo de validación de email
- Nodo Gmail que pueda estar validando
- Expresiones que verifiquen el formato del email
- Configuración incorrecta que rechace emails válidos como `@gmail.com`
  "success": true,
  "lead_id": "2026-01-11T02:08:10.022Z-test-at-example.com",
  "score": 85,
  "categoria": "HOT",
  "ai_analysis": {
    "normalized_interest": "Marcas",
    "is_spam": false,
    "analysis_reason": "Lead con necesidad específica y urgente...",
    "calculated_score": 85,
    "category": "HOT"
  },
  "message": "Lead procesado exitosamente por SUB-A (AI Powered via AI Agent)"
}
```

### Webhook Legacy (v1.0 - INACTIVO)

```bash
curl -X POST https://carrilloabgd.app.n8n.cloud/webhook/lead-events \
  -H "Content-Type: application/json" \
  -d '{ "event_type": "new_lead", ... }'
```

---

## 📋 Bloqueantes Actuales

### SUB-D Nurturing (P1 - Alta Prioridad)

| Bloqueante | Descripción | Acción Requerida | Owner |
|------------|-------------|------------------|-------|
| **Mailersend Account** | Servicio de email marketing no configurado | Crear cuenta + verificar dominio | Usuario |
| **Mailersend API Key** | Credencial pendiente en n8n Cloud | Obtener API Key y configurar | Usuario |
| **Variable BACKEND_URL** | Falta configurar en n8n Settings | Definir URL producción/staging | Usuario |

### Integración Web Completa (P1 - Media Prioridad)

| Bloqueante | Descripción | Acción Requerida | Owner |
|------------|-------------|------------------|-------|
| **NATS → n8n Bridge** | Test E2E pendiente | Probar flujo completo desde formulario | DevOps |
| **Frontend /contacto** | Integración con API Gateway | Conectar formulario a endpoint | Frontend Dev |

---

*Documento actualizado con arquitectura SUB-D completa - 2026-01-11 - MW#1 v3.0 en producción*
