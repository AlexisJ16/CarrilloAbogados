# MEGA-WORKFLOW #1: Lead Lifecycle Manager

## Estado: ✅ OPERATIVO EN PRODUCCIÓN

**Última actualización:** 2025-12-21  
**Versión:** 1.0.0 (Production)

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
| **Nodos** | 5 (Webhook → Identify → SubA → Consolidate → Respond) |

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
