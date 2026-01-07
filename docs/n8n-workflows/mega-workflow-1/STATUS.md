# MEGA-WORKFLOW #1: Lead Lifecycle Manager

## Estado: 🟡 EN REDISEÑO ARQUITECTÓNICO

**Última actualización:** 2026-01-06
**Versión:** 2.0.0 (En desarrollo - Metodología Nate Herk)
**Versión en Producción:** 1.0.0 (Funcional pero requiere mejoras)

---

## 🔄 Verificar Estado Real

```bash
# Antes de modificar, SIEMPRE verificar con MCP que este documento esté sincronizado:
> Obtén el workflow bva1Kc1USbbITEAw y valídalo
> Obtén el workflow RHj1TAqBazxNFriJ y valídalo
> Lista las últimas 5 ejecuciones
```

**⚠️ Si hay diferencias entre esta documentación y n8n Cloud, actualizar este archivo.**

---

## Resumen

Sistema completo de captura y procesamiento de leads para Carrillo Abogados, utilizando IA (Google Gemini 2.5-pro) para análisis y scoring automático.

---

## Workflows en Producción

### Orquestador (Hub)

| Campo | Valor |
|-------|-------|
| **ID** | `bva1Kc1USbbITEAw` |
| **Nombre** | WORKFLOW A: Lead Lifecycle Manager (Orquestador) |
| **Estado** | ✅ ACTIVO |
| **Webhook** | `https://carrilloabgd.app.n8n.cloud/webhook/lead-events` |
| **Nodos** | 8 (Webhook → Identify → SubA → Consolidate → Respond + Error Handler) |
| **Arquitectura** | ⚠️ Code-based routing (v1.0) → 🎯 AI Agent routing (v2.0 planeado) |

### SUB-A: Lead Intake (Spoke)

| Campo | Valor |
|-------|-------|
| **ID** | `RHj1TAqBazxNFriJ` |
| **Nombre** | SUB-A: Lead Intake (v5 - AI POWERED - NATIVE) |
| **Estado** | ⚪ INACTIVO (triggered by Orquestador) |
| **Nodos** | 10 |
| **IA** | Google Gemini 2.5-pro (análisis + respuesta) |

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

## 🚨 REDISEÑO EN CURSO - v2.0.0

### Cambios Arquitectónicos Planeados

**Metodología aplicada:** Nate Herk AI Systems Pyramid

#### 1. Orquestador v2.0 (CRÍTICO)

**Problema identificado:**
- Actual usa nodo `Code` para routing → lógica rígida, no escalable
- Cada nuevo sub-workflow requiere modificar código manualmente

**Solución v2.0:**
- Reemplazar con **AI Agent Node** (Google Gemini 2.5 Pro)
- Routing inteligente basado en razonamiento
- Agregar nuevos workflows = solo editar System Prompt
- Observabilidad completa (logs de decisiones)

**Estado:** 📋 Diseño completo - Pendiente implementación
**Documentos:**
- [ARQUITECTURA_MW1_V3_NATE_HERK.md](../../automation/n8n-workflows/workflows/MEGA_WORKFLOW_1_LEAD_LIFECYCLE/ARQUITECTURA_MW1_V3_NATE_HERK.md)
- [ADR_001_REDISENO_ORQUESTADOR_AI_AGENT.md](../../automation/n8n-workflows/workflows/MEGA_WORKFLOW_1_LEAD_LIFECYCLE/ADR_001_REDISENO_ORQUESTADOR_AI_AGENT.md)

#### 2. SUB-D: Nurturing Sequence Engine (NUEVO)

**Propósito:** Secuencia automatizada de 8-12 emails para leads WARM/COLD

**Arquitectura:** AI Workflow (Nivel 3 - Gemini para personalización)

**Componentes:**
- Schedule Trigger (cada 6 horas)
- Query Firestore (leads para nurturing)
- Calcular posición en secuencia (1-12)
- Gemini personaliza contenido
- Mailersend envía con tracking
- Callback a backend

**Estimado:** 16 nodos total
**Estado:** 📋 Diseño completo - Pendiente implementación
**Documentos:**
- [WIREFRAME_MW1_V3.md](../../automation/n8n-workflows/workflows/MEGA_WORKFLOW_1_LEAD_LIFECYCLE/WIREFRAME_MW1_V3.md)
- [HANDOFF_ENGINEER_SUB_D.md](../../automation/n8n-workflows/workflows/MEGA_WORKFLOW_1_LEAD_LIFECYCLE/HANDOFF_ENGINEER_SUB_D.md)

#### 3. Actualización SUB-A (Modificación menor)

**Cambios requeridos:**
- Agregar campos Firestore: `status`, `emails_sent`, `nurturing_position`, `next_email_date`, `created_at`
- Calcular `next_email_date = now + 3 días` al guardar lead

**Estado:** ⏳ Pendiente después de SUB-D

---

## Historial de Cambios

### 2026-01-06 - Rediseño Arquitectónico v2.0 (En Curso)
- 📋 Diseño completo aplicando metodología Nate Herk
- 🎯 Orquestador v2.0: AI Agent con Gemini 2.5 Pro
- 🎯 SUB-D: Nurturing Engine con 12 templates
- 📚 Documentación técnica completa generada
- ⏳ Pendiente aprobación e implementación

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
