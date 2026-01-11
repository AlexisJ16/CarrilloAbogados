# MEGA-WORKFLOW #1: Lead Lifecycle Manager

## Estado: ✅ ACTIVO EN PRODUCCION (v3.0) | ORQUESTADOR v1.0 INACTIVO

**Ultima actualizacion:** 2026-01-11 (Verificado con test E2E completo)
**Version Produccion:** 3.0.0 (Orquestador v3.0 AI Agent - ACTIVO)
**Version Legacy:** 1.1.0 (Orquestador v1.0 - INACTIVO)
**n8n Cloud:** v1.120.4
**Webhook URL Produccion:** `https://carrilloabgd.app.n8n.cloud/webhook/lead-events-v3`
**Webhook URL Legacy (v1.0):** `https://carrilloabgd.app.n8n.cloud/webhook/lead-events`

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

**Arquitectura**: Hub & Spoke
- 1 Orquestador (Hub) recibe eventos via webhook
- 1 Sub-workflow (Spoke) procesa leads con IA

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

### SUB-A: Lead Intake (Spoke)

| Campo | Valor |
|-------|-------|
| **ID** | `RHj1TAqBazxNFriJ` |
| **Nombre** | SUB-A: Lead Intake (v5 - AI POWERED - NATIVE) |
| **Estado** | ✅ Listo (triggered by Orquestador) |
| **Nodos** | 16 (incluye Error Handler + Callbacks Backend) |
| **IA** | Google Gemini 2.5-pro (análisis + respuesta) |
| **Última ejecución** | 2026-01-04 (éxito) |
| **Total ejecuciones** | 13+ |
| **Validación** | ✅ 0 nuevos errores (warnings menores pre-existentes) |

---

## Flujo de Datos

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
        ├── 2. Guardar en Firestore
        ├── 3. Es Lead HOT? (IF)
        │   ├── [HOT ≥70] → 4. Notificar Equipo
        │   └── [WARM/COLD] → continúa
        ├── 5. Generar Respuesta (Gemini)
        ├── 6. Enviar Respuesta Lead
        ├── 7. Callback Lead Scored (HTTP → Backend) ← NUEVO
        ├── 8. Es Lead HOT (Callback)? (IF) ← NUEVO
        │   └── [HOT] → 9. Callback Hot Lead Alert ← NUEVO
        ├── FINAL. Resultado
        └── Error Handler → Preparar Error → Notificar Error
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

**Estado Actual** (2026-01-11):
- ✅ Callbacks implementados en SUB-A (nodos 7, 8, 9)
- ✅ Testing con Pipedream exitoso (ambos callbacks reciben datos)
- ⏳ Backend endpoints pendientes de implementación
- ⏳ Variable BACKEND_URL pendiente de configurar en producción

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

| Servicio | Propósito | Credential ID |
|----------|-----------|---------------|
| Google Gemini 2.5-pro | Análisis IA y generación de respuestas | `jk2FHcbAC71LuRl2` |
| Gmail OAuth2 | Envío de emails | `l2mMgEf8YUV7HHlK` |
| Google Firestore | Base de datos de leads | `AAhdRNGzvsFnYN9O` |

---

## Categorización de Leads

| Categoría | Score | Acción |
|-----------|-------|--------|
| 🔥 **HOT** | >= 70 | Email notificación a equipo + respuesta automática |
| 🟡 **WARM** | 40-69 | Respuesta automática |
| 🔵 **COLD** | < 40 | Respuesta automática |

---

## Historial de Cambios

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
| Orquestador JSON | `01-orchestrator/artifacts/ORQUESTADOR_PRODUCTION_2025-12-21.json` |
| SUB-A JSON | `02-spokes/sub-a-lead-intake/artifacts/SUB-A_PRODUCTION_2025-12-21.json` |
| Reporte Debugging | `testing/SESSION_2025-12-21_DEBUGGING_COMPLETO.md` |

---

## Notas Técnicas

### Expresiones n8n
- ❌ NO usar optional chaining (`?.`) - no soportado
- ✅ Usar ternarios: `$json.x ? $json.x : ($json.y ? $json.y.x : '')`

### Nodo IF
- Requiere `options` dentro de `conditions` con estructura completa

### Gmail Node
- Siempre especificar `operation: "send"` explícitamente

---

## 🔧 Validación n8n (2026-01-04)

### Estado de Validación: ✅ APROBADO

| Workflow | Errores | Warnings | Estado |
|----------|---------|----------|--------|
| Orquestador | 0 | 1 (typeVersion) | ✅ Activo |
| SUB-A | 0 | 4 (typeVersions) | ✅ Listo |

### Correcciones Aplicadas (2026-01-04)

| Problema | Solución | Estado |
|----------|----------|--------|
| Webhook sin `onError` | Agregado `continueRegularOutput` | ✅ Corregido |
| Sin Error Handling | Agregado Error Trigger + Set + Gmail | ✅ Corregido |
| Credenciales Gmail | Configuradas en n8n UI | ✅ Activas |

### Warnings Pendientes (P2 - No bloquean)

| Workflow | Nodo | Actual → Recomendada |
|----------|------|----------------------|
| Orquestador | Notificar Error Email | 2.1 → 2.2 |
| SUB-A | If Node | 2 → 2.3 |
| SUB-A | Gmail (x2) | 2.1 → 2.2 |
| SUB-A | Notificar Error | 2.1 → 2.2 |

---

## 🚀 Estado de Acciones

### ✅ P0 - COMPLETADO

1. ✅ **Error Webhook Orquestador** - Corregido `onError: continueRegularOutput`
2. ✅ **Error Handling** - Agregado a ambos workflows (Error Trigger → Set → Gmail)
3. ✅ **Activación** - Orquestador activo en producción
4. ✅ **Test E2E** - Lead procesado exitosamente (Score 90, HOT)

### 🟡 P1 - En Progreso

5. **Integración Web Completa**
   - ✅ client-service Lead API funcionando
   - ✅ n8n-integration-service webhooks implementados
   - ⏳ Conectar NATS → n8n webhook (pendiente prueba E2E)
   - ⏳ Frontend /contacto → flujo completo

### 🟢 P2 - Mejoras Futuras

6. **Actualizar typeVersions** (warnings, no crítico)
   - Click "Update" en nodos con banner amarillo en n8n UI

7. **Monitoreo**
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
                            │   └─ HTTP POST ──────┼───► Webhook lead-events
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
| 1 | Activar Orquestador en n8n Cloud | ✅ ACTIVO |
| 2 | Verificar webhook accesible | ✅ Funcionando |
| 3 | Configurar NatsEventListener | ✅ Implementado |
| 4 | Desplegar n8n-integration-service | ⏳ Pendiente deploy |
| 5 | Probar flujo E2E completo | ⏳ Pendiente |

### URLs de Producción

| Componente | URL |
|------------|-----|
| **n8n Webhook** | `https://carrilloabgd.app.n8n.cloud/webhook/lead-events` |
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

---

## 🔐 Credenciales Verificadas

| Credencial | ID | Estado | Última verificación |
|------------|------|--------|---------------------|
| Google Gemini API | `jk2FHcbAC71LuRl2` | ✅ Activo | 2026-01-04 |
| Gmail OAuth2 | `l2mMgEf8YUV7HHlK` | ✅ Activo | 2026-01-04 |
| Google Firestore | `AAhdRNGzvsFnYN9O` | ✅ Activo | 2026-01-04 |

---

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
  "success": true,
  "lead_id": "2026-01-11T02:08:10.022Z-laura.martinez-at-innovatech.com",
  "score": 95,
  "categoria": "HOT",
  "ai_analysis": {
    "normalized_interest": "Marcas",
    "is_spam": false,
    "analysis_reason": "El lead es un decision-maker clave (CEO) con una necesidad específica y urgente de registro de marca...",
    "calculated_score": 95,
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

*Documento actualizado con datos reales de test E2E - 2026-01-11 - Orquestador v3.0 AI Agent en producción*
