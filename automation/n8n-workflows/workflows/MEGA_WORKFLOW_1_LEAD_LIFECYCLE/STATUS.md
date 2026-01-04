# MEGA-WORKFLOW #1: Lead Lifecycle Manager

## Estado: ✅ ACTIVO Y FUNCIONANDO

**Última actualización:** 2026-01-04 (Verificado con MCP n8n)  
**Versión:** 1.0.0 (Production-Ready)  
**n8n Cloud:** v1.120.4

---

## 🔄 Estado Real (Verificado 2026-01-04)

Datos obtenidos directamente de la API de n8n Cloud mediante MCP:

### Instancia n8n
- **URL**: https://carrilloabgd.app.n8n.cloud
- **Versión**: 1.120.4
- **Usuario**: marketing@carrilloabgd.com
- **Workflows totales**: 4
- **Estado Sistema**: ✅ PRODUCCIÓN ACTIVA

---

## Resumen

Sistema completo de captura y procesamiento de leads para Carrillo Abogados, utilizando IA (Google Gemini 2.5-pro) para análisis y scoring automático.

**Arquitectura**: Hub & Spoke
- 1 Orquestador (Hub) recibe eventos via webhook
- 1 Sub-workflow (Spoke) procesa leads con IA

---

## Workflows en n8n Cloud

### Orquestador (Hub)

| Campo | Valor |
|-------|-------|
| **ID** | `bva1Kc1USbbITEAw` |
| **Nombre** | WORKFLOW A: Lead Lifecycle Manager (Orquestador) |
| **Estado** | ✅ **ACTIVO** (Production) |
| **Webhook** | `https://carrilloabgd.app.n8n.cloud/webhook/lead-events` |
| **Nodos** | 8 (incluye Error Handler) |
| **Última ejecución** | 2026-01-04 (éxito) |
| **Total ejecuciones** | 3+ (validado exitosamente) |
| **Validación** | ✅ 0 errores |

### SUB-A: Lead Intake (Spoke)

| Campo | Valor |
|-------|-------|
| **ID** | `RHj1TAqBazxNFriJ` |
| **Nombre** | SUB-A: Lead Intake (v5 - AI POWERED - NATIVE) |
| **Estado** | ✅ Listo (triggered by Orquestador) |
| **Nodos** | 13 (incluye Error Handler) |
| **IA** | Google Gemini 2.5-pro (análisis + respuesta) |
| **Última ejecución** | 2026-01-04 (éxito) |
| **Total ejecuciones** | 10+ (mejora continua) |
| **Validación** | ✅ 0 errores |

---

## ✅ Correcciones Completadas (4 Enero 2026)

### 1. Error Webhook `onError` - CORREGIDO ✅
- **Problema**: Nodo webhook sin `onError: continueRegularOutput`
- **Solución**: Agregado via n8n MCP
- **Resultado**: Workflow puede activarse correctamente

### 2. Error Handlers Agregados - COMPLETADO ✅

**Orquestador:**
- `Error Handler` (Error Trigger)
- `Preparar Datos Error` (Set node)
- `Notificar Error Email` (Gmail → ingenieria@carrilloabgd.com)

**SUB-A:**
- `Error Handler` (Error Trigger)
- `Preparar Error` (Set node)
- `Notificar Error` (Gmail → ingenieria@carrilloabgd.com)

### 3. Test E2E Exitoso ✅

```json
{
  "success": true,
  "message": "Lead procesado exitosamente por SUB-A (AI Powered)",
  "score": 90,
  "categoria": "HOT",
  "ai_analysis": {
    "normalized_interest": "Marcas",
    "analysis_reason": "Lead de alta calidad con interés específico en servicios de marcas",
    "calculated_score": 90
  }
}
```

---

## Flujo de Datos

```
[Webhook POST] 
    ↓
[Orquestador]
    ├── Webhook Principal Lead Events
    ├── Identify Event (Code)
    ├── Execute SUB-A
    ├── Consolidate Response
    └── Respond to Webhook
    
    Error Handler:
    ├── Error Handler (Error Trigger)
    ├── Preparar Datos Error (Set)
    └── Notificar Error Email (Gmail)
         ↓
    [SUB-A]
        ├── When Executed by Another Workflow
        ├── 0. Mapear Input
        ├── 0.5. Analizar Lead (Gemini IA)
        ├── 1. Validar y Clasificar
        ├── 2. Guardar en Firestore
        ├── 3. Es Lead HOT? (IF)
        │   ├── [HOT] → 4. Notificar Equipo
        │   └── [ALL] → 5. Generar Respuesta (Gemini)
        ├── 6. Enviar Respuesta Lead
        └── FINAL. Resultado
        
        Error Handler:
        ├── Error Handler (Error Trigger)
        ├── Preparar Error (Set)
        └── Notificar Error (Gmail)
```

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

### 2026-01-04 - ✅ ACTIVACIÓN PRODUCCIÓN
- ✅ Corregido error webhook `onError: continueRegularOutput`
- ✅ Agregados Error Handlers en Orquestador y SUB-A
- ✅ Workflows activados en producción
- ✅ Test E2E exitoso (Score 90, HOT)
- ✅ Sistema completamente operativo

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

### Orquestador - Estado Actual

| Tipo | Nodo | Estado |
|------|------|--------|
| ✅ OK | Webhook Principal | `onError: continueRegularOutput` configurado |
| ✅ OK | Error Handler | Implementado y funcionando |
| ⚠️ Info | Execute Workflow | typeVersion 1.2 (actualización a 1.3 opcional) |
| ⚠️ Info | Respond to Webhook | typeVersion 1.1 (actualización a 1.5 opcional) |

### SUB-A - Estado Actual

| Tipo | Nodo | Estado |
|------|------|--------|
| ✅ OK | Error Handler | Implementado y funcionando |
| ⚠️ Info | IF Node | typeVersion 2 (actualización a 2.3 opcional) |
| ⚠️ Info | Gmail Nodes (x2) | typeVersion 2.1 (actualización a 2.2 opcional) |

**Nota**: Los warnings de typeVersion son actualizaciones menores opcionales que no afectan funcionalidad.

---

## 🚀 Estado de Producción

### ✅ Sistema Completamente Operativo

- [x] Webhook accesible: `https://carrilloabgd.app.n8n.cloud/webhook/lead-events`
- [x] Orquestador ACTIVO sin errores
- [x] SUB-A funcionando correctamente
- [x] Error handling implementado en ambos workflows
- [x] Credenciales verificadas y operativas
- [x] Test E2E pasando exitosamente

### 🟡 Mejoras Opcionales (P2 - No bloquean)

1. **Actualizar typeVersions**
   - Execute Workflow: 1.2 → 1.3
   - Respond to Webhook: 1.1 → 1.5
   - If Node: 2 → 2.3
   - Gmail Nodes: 2.1 → 2.2

2. **Integración Web**
   - Conectar formulario web → NATS → n8n-integration-service → Webhook
   - Probar flujo E2E con datos reales del frontend

3. **Monitoreo**
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

### Estado de Integración

Pasos completados:

1. ✅ Orquestador ACTIVO en n8n Cloud
2. ✅ Webhook verificado y accesible
3. ⬜ Configurar NatsEventListener con URL correcta del webhook
4. ⬜ Desplegar n8n-integration-service en producción
5. ⬜ Probar flujo E2E: Formulario → client-service → NATS → n8n

---

## 📊 Métricas de Ejecución

### Últimas Ejecuciones (Enero 2026)

**Orquestador:**
| Fecha | Estado | Modo | Notas |
|-------|--------|------|-------|
| 2026-01-04 | ✅ Éxito | Test E2E | Score 90, HOT lead |
| 2025-12-22 | ✅ Éxito | Manual | - |
| 2025-12-22 | ✅ Éxito | Manual | - |

**SUB-A:**
| Fecha | Estado | Notas |
|-------|--------|-------|
| 2026-01-04 | ✅ Éxito | Test E2E exitoso con AI analysis |
| 2025-12-22 | ✅ Éxito | Último test antes de activación |

**Tasa de Éxito Actual**: ✅ 100% (últimas 5 ejecuciones)

---

## 🔐 Credenciales Verificadas

| Credencial | ID | Estado | Última verificación |
|------------|------|--------|---------------------|
| Google Gemini API | `jk2FHcbAC71LuRl2` | ✅ Activo | 2026-01-03 |
| Gmail OAuth2 | `l2mMgEf8YUV7HHlK` | ✅ Activo | 2026-01-03 |
| Google Firestore | `AAhdRNGzvsFnYN9O` | ✅ Activo | 2026-01-03 |

---

*Documento actualizado automáticamente con datos de n8n MCP - 2026-01-04*
*Sistema EN PRODUCCIÓN - Workflows ACTIVOS*
