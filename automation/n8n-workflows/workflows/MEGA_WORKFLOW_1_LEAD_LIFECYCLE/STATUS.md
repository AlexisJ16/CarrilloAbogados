# MEGA-WORKFLOW #1: Lead Lifecycle Manager

## Estado: ⚠️ INACTIVO - PENDIENTE ACTIVACIÓN

**Última actualización:** 2026-01-03 (Verificado con MCP n8n)  
**Versión:** 1.0.0 (Production-Ready)  
**n8n Cloud:** v1.120.4

---

## 🔄 Estado Real (Verificado 2026-01-03)

Datos obtenidos directamente de la API de n8n Cloud mediante MCP:

### Instancia n8n
- **URL**: https://carrilloabgd.app.n8n.cloud
- **Versión**: 1.120.4
- **Usuario**: marketing@carrilloabgd.com
- **Workflows totales**: 4

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
| **Estado** | ⚪ INACTIVO (requiere activación manual) |
| **Webhook** | `https://carrilloabgd.app.n8n.cloud/webhook/lead-events` |
| **Nodos** | 5 (Webhook → Identify → SubA → Consolidate → Respond) |
| **Última ejecución** | 2025-12-22 (éxito) |
| **Total ejecuciones** | 3 (2 éxitos, 1 error) |
| **Validación** | ⚠️ 1 error, 5 warnings |

### SUB-A: Lead Intake (Spoke)

| Campo | Valor |
|-------|-------|
| **ID** | `RHj1TAqBazxNFriJ` |
| **Nombre** | SUB-A: Lead Intake (v5 - AI POWERED - NATIVE) |
| **Estado** | ⚪ INACTIVO (triggered by Orquestador) |
| **Nodos** | 10 |
| **IA** | Google Gemini 2.5-pro (análisis + respuesta) |
| **Última ejecución** | 2025-12-22 (éxito) |
| **Total ejecuciones** | 10 (4 éxitos, 6 errores) |
| **Validación** | ✅ Válido (7 warnings menores) |

---

## Flujo de Datos

```
[Webhook POST] 
    ↓
[Orquestador]
    ├── Identify Event (Code)
    ├── Execute SUB-A
    ├── Consolidate Response
    └── Respond to Webhook
         ↓
    [SUB-A]
        ├── 0. Mapear Input
        ├── 0.5. Analizar Lead (Gemini IA)
        ├── 1. Validar y Clasificar
        ├── 2. Guardar en Firestore
        ├── 3. Es Lead HOT? (IF)
        │   ├── [HOT] → 4. Notificar Equipo
        │   └── [ALL] → 5. Generar Respuesta (Gemini)
        ├── 6. Enviar Respuesta Lead
        └── FINAL. Resultado
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

## 🔧 Validación n8n (2026-01-03)

### Orquestador - Errores Detectados

| Tipo | Nodo | Problema | Solución |
|------|------|----------|----------|
| ❌ **ERROR** | Webhook Principal | `responseNode` mode sin `onError` configurado | Agregar `"onError": "continueRegularOutput"` |
| ⚠️ Warning | Execute Workflow | typeVersion 1.2 → 1.3 disponible | Actualizar nodo |
| ⚠️ Warning | Respond to Webhook | typeVersion 1.1 → 1.5 disponible | Actualizar nodo |
| ⚠️ Warning | General | Sin error handling global | Agregar nodos Error Trigger |

### SUB-A - Warnings Detectados

| Tipo | Nodo | Problema | Solución |
|------|------|----------|----------|
| ⚠️ Warning | IF Node | typeVersion 2 → 2.3 disponible | Actualizar nodo |
| ⚠️ Warning | Gmail Nodes (x2) | typeVersion 2.1 → 2.2 disponible | Actualizar nodos |
| ⚠️ Warning | IF Node | Sin `onError` configurado | Agregar manejo de errores |
| ⚠️ Warning | General | Sin error handling global | Agregar workflow Error Trigger |

---

## 🚀 Acciones Requeridas (Prioridad)

### 🔴 P0 - Crítico (Antes de activar)

1. **Corregir error Webhook Orquestador**
   ```javascript
   // En nodo "Webhook Principal Lead Events" agregar:
   "parameters": {
     "httpMethod": "POST",
     "path": "lead-events",
     "responseMode": "responseNode",
     "onError": "continueRegularOutput"  // <- AGREGAR
   }
   ```

2. **Actualizar typeVersions**
   - Execute Workflow: 1.2 → 1.3
   - Respond to Webhook: 1.1 → 1.5
   - If Node: 2 → 2.3

### 🟡 P1 - Importante (Post-activación)

3. **Agregar Error Handling**
   - Añadir nodo "Error Trigger" en ambos workflows
   - Configurar notificación Slack/Email ante errores

4. **Mejorar tasa de éxito SUB-A**
   - Actual: 40% (4/10 éxitos)
   - Revisar logs de errores
   - Agregar validación de payload más robusta

### 🟢 P2 - Mejoras (Futuro)

5. **Integración Web**
   - Conectar formulario web → NATS → n8n-integration-service → Webhook
   - Probar flujo E2E con datos reales

6. **Monitoreo**
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

Para que la integración funcione completamente:

1. ⬜ Activar Orquestador en n8n Cloud
2. ⬜ Verificar webhook accesible: `curl -X POST https://carrilloabgd.app.n8n.cloud/webhook/lead-events`
3. ⬜ Configurar NatsEventListener con URL correcta del webhook
4. ⬜ Desplegar n8n-integration-service en producción
5. ⬜ Probar flujo E2E: Formulario → client-service → NATS → n8n

---

## 📊 Métricas de Ejecución

### Últimas Ejecuciones (Diciembre 2025)

**Orquestador:**
| Fecha | Estado | Duración | Modo |
|-------|--------|----------|------|
| 2025-12-22 | ✅ Éxito | - | Manual |
| 2025-12-22 | ✅ Éxito | - | Manual |
| 2025-12-22 | ❌ Error | - | Manual |

**SUB-A:**
| Fecha | Estado | Notas |
|-------|--------|-------|
| 2025-12-22 | ✅ Éxito | Último test exitoso |
| 2025-12-17-21 | Mixto | 4 éxitos, 6 errores (debugging) |

---

## 🔐 Credenciales Verificadas

| Credencial | ID | Estado | Última verificación |
|------------|------|--------|---------------------|
| Google Gemini API | `jk2FHcbAC71LuRl2` | ✅ Activo | 2026-01-03 |
| Gmail OAuth2 | `l2mMgEf8YUV7HHlK` | ✅ Activo | 2026-01-03 |
| Google Firestore | `AAhdRNGzvsFnYN9O` | ✅ Activo | 2026-01-03 |

---

*Documento actualizado automáticamente con datos de n8n MCP - 2026-01-03*
