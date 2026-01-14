# MEGA-WORKFLOW #1: Lead Lifecycle Manager

## Estado: 🚀 EN IMPLEMENTACIÓN ACTIVA - v3.0

**Última actualización:** 2026-01-07
**Versión:** 3.0.0 (En implementación - Metodología Nate Herk)
**Versión en Producción:** 1.0.0 (Funcional pero requiere mejoras)
**Versión en Desarrollo:** 3.0.0 (AI Agent Orchestrator + SUB-D Nurturing)

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

## 🚨 ARQUITECTURA v3.0 - IMPLEMENTACIÓN EN CURSO

### 📋 Resumen Ejecutivo del Rediseño

**Fecha de inicio:** 2026-01-07
**Metodología aplicada:** Nate Herk AI Systems Pyramid
**Aprobación:** ✅ Arquitectura completa por Arquitecto n8n
**Equipo asignado:** @engineer (implementación), @qa-specialist (validación), @optimizer (optimización)

#### Hallazgo Crítico Identificado

El **Orquestador v1.0** usa nodo `Code` para routing estático → **NO escalable** según mejores prácticas Nate Herk para sistemas IA en n8n.

**Consecuencia:** Agregar SUB-E, SUB-F, etc. requiere modificar código manualmente en múltiples nodos → sistema frágil.

#### Solución Aprobada: Opción A - Implementación Completa v3.0

**Alcance total:** 13 horas estimadas
1. **Orquestador v3.0** con AI Agent (4h)
2. **SUB-D: Nurturing Engine** completo (6h)
3. **Actualización SUB-A** con campos nurturing (2h)
4. **Integración y testing** E2E (1h)

**Beneficios clave:**
- ✅ Sistema 100% escalable (agregar sub-workflows = editar prompt, no código)
- ✅ Observabilidad completa (AI Agent registra razonamiento de decisiones)
- ✅ Nurturing automático de leads WARM/COLD (300 emails/mes)
- ✅ Base sólida para futuros sub-workflows (SUB-E, SUB-F)

**Costos:**
- ⚠️ Costo adicional: ~$0.90 USD/mes (300 leads × $0.003/ejecución)
- ⚠️ Latencia: +2-3 segundos por ejecución (AI Agent razonamiento)

### Componentes v3.0

#### 1. Orquestador v3.0 con AI Agent (CRÍTICO)

**Cambio fundamental:**
- ❌ **v1.0:** Nodo `Code` con switch estático
- ✅ **v3.0:** Nodo `AI Agent` con Google Gemini 2.5 Pro

**Funcionalidad:**
- Recibe payload webhook → AI Agent decide qué sub-workflow ejecutar
- Logging automático de decisiones en Google Sheets
- maxIterations=3 para evitar bucles
- Error handling con fallback a routing estático

**Estado:** 🔄 En implementación por @engineer
**Documentos técnicos:**
- [ARQUITECTURA_MW1_V3_NATE_HERK.md](../../automation/n8n-workflows/workflows/MEGA_WORKFLOW_1_LEAD_LIFECYCLE/ARQUITECTURA_MW1_V3_NATE_HERK.md)
- [ADR_001_REDISENO_ORQUESTADOR_AI_AGENT.md](../../automation/n8n-workflows/workflows/MEGA_WORKFLOW_1_LEAD_LIFECYCLE/ADR_001_REDISENO_ORQUESTADOR_AI_AGENT.md)

#### 2. SUB-D: Nurturing Sequence Engine (NUEVO)

**Propósito:** Secuencia automatizada de 12 emails para leads WARM/COLD

**Clasificación:** Nivel 3 - AI Workflow (según Nate Herk)

**Arquitectura (16 nodos totales):**
```
Schedule (6h) → Query Firestore → Loop Leads → Calcular Posición →
  Cargar Template → Gemini Personaliza → Mailersend Envía →
  Actualizar BD → Callback Backend
```

**Templates de email:** 12 posiciones
1. Bienvenida (día 0)
2. Educativo - Por qué proteger marca (día 3)
3. Case Study (día 7)
4. Checklist gratuito (día 10)
5. Urgencia - 3 riesgos (día 14)
6. Autoridad - Dr. Carrillo SIC (día 21)
7. Oferta - Consulta gratis (día 28)
8. Re-engagement (día 35)
9. Tendencias PI 2026 (día 42)
10. Last chance (día 49)
11. Break-up (día 56)
12. Win-back (día 90)

**Servicios externos requeridos:**
- Google Gemini 2.5 Pro (ya configurado: `jk2FHcbAC71LuRl2`)
- Google Firestore (ya configurado: `AAhdRNGzvsFnYN9O`)
- **Mailersend** (NUEVO - requiere configuración):
  - Free tier: 3,000 emails/mes
  - Dominio verificado: carrilloabgd.com
  - Tracking: opens, clicks
  - API Key pendiente

**Estado:** ✅ Implementado - Pendiente credenciales Mailersend
**Workflow ID:** `PZboUEnAxm5A7Lub`
**URL n8n:** https://carrilloabgd.app.n8n.cloud/workflow/PZboUEnAxm5A7Lub

**Documentos técnicos:**
- [WIREFRAME_MW1_V3.md](../../automation/n8n-workflows/workflows/MEGA_WORKFLOW_1_LEAD_LIFECYCLE/WIREFRAME_MW1_V3.md)
- [HANDOFF_ENGINEER_SUB_D.md](../../automation/n8n-workflows/workflows/MEGA_WORKFLOW_1_LEAD_LIFECYCLE/HANDOFF_ENGINEER_SUB_D.md)

**Artifacts generados:**
- `artifacts/SUB-D/implementation_notes.md` - Documentación completa
- `artifacts/SUB-D/SUB-D_WORKFLOW.json` - Export JSON
- `artifacts/SUB-D/code_snippets/` - JavaScript (3 archivos)
- `artifacts/SUB-D/MAILERSEND_CONFIG.md` - Guía configuración
- `artifacts/SUB-D/TEST_RESULTS.md` - Resultados testing

**BLOQUEANTES identificados:**
1. ⚠️ **Mailersend NO configurado** - Requiere:
   - Crear cuenta en https://mailersend.com (free tier)
   - Verificar dominio carrilloabgd.com
   - Obtener API Key
   - Configurar credencial en n8n Cloud
2. ⚠️ **Variable BACKEND_URL** - Configurar en n8n Settings > Variables

#### 3. Actualización SUB-A v2.0 (Modificación menor)

**Cambios requeridos en nodo "Guardar en Firestore":**

Agregar campos nuevos:
```javascript
{
  // Campos existentes...
  "status": "nuevo",               // NUEVO
  "emails_sent": 0,                // NUEVO
  "nurturing_position": 0,         // NUEVO
  "next_email_date": "={{ new Date(Date.now() + 3 * 24 * 60 * 60 * 1000).toISOString() }}",  // NUEVO: +3 días
  "created_at": "={{ new Date().toISOString() }}"  // NUEVO
}
```

**Impacto:** Mínimo (1 nodo modificado)
**Dependencia:** Requiere que SUB-D esté implementado primero
**Estado:** ⏳ Pendiente (después de SUB-D completado)

---

## Historial de Cambios

### 2026-01-07 - SUB-D Implementado (v3.0 EN PROGRESO)
- ✅ **SUB-D Completado:** Workflow `PZboUEnAxm5A7Lub` creado con 16 nodos
- ✅ **Arquitectura:** Schedule + Query + Loop + AI Personalización + Mailersend
- ✅ **Artifacts:** 5 documentos generados en `artifacts/SUB-D/`
  - implementation_notes.md (documentación completa)
  - SUB-D_WORKFLOW.json (export JSON)
  - code_snippets/ (3 archivos JavaScript)
  - MAILERSEND_CONFIG.md (guía configuración)
  - TEST_RESULTS.md (resultados testing)
- ⚠️ **Bloqueantes identificados:**
  - Mailersend NO configurado (cuenta, dominio, API Key)
  - Variable BACKEND_URL pendiente en n8n Cloud
- 📊 **STATUS.md actualizado:** Estado v3.0 completo con SUB-D implementado
- 🎯 **Plan de trabajo actualizado:**
  - Fase 1: Orquestador v3.0 con AI Agent (4h) - PENDIENTE
  - Fase 2: SUB-D Nurturing Engine (6h) - ✅ COMPLETADO
  - Fase 3: Actualización SUB-A (2h) - PENDIENTE
  - Fase 4: Integración E2E (1h) - PENDIENTE
- ⏳ **Próximo:** Configurar Mailersend + Validación @qa-specialist

### 2026-01-06 - Diseño Arquitectónico v3.0 Completo
- 📋 Diseño completo aplicando metodología Nate Herk AI Systems Pyramid
- 🎯 Orquestador v3.0: AI Agent con Gemini 2.5 Pro (spec completa)
- 🎯 SUB-D: Nurturing Engine con 12 templates (spec completa)
- 📚 Documentación técnica generada:
  - ARQUITECTURA_MW1_V3_NATE_HERK.md
  - WIREFRAME_MW1_V3.md
  - ADR_001_REDISENO_ORQUESTADOR_AI_AGENT.md
  - HANDOFF_ENGINEER_SUB_D.md
  - RESUMEN_EJECUTIVO_ARQUITECTO.md

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
