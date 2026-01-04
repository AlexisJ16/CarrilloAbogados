# Sesión de Debugging - 21 Diciembre 2025

## Resumen Ejecutivo

**Fecha:** 2025-12-21  
**Duración:** ~2 horas  
**Resultado:** ✅ MEGA-WORKFLOW #1 funcionando al 100%

---

## Estado Inicial

### Orquestador (ID: 7yRMJecj0TaIdinU)
- **Estado:** Corrupto - aparecía vacío en la UI de n8n
- **Problema:** `webhookId` vacío, workflow inutilizable

### SUB-A (ID: RHj1TAqBazxNFriJ)
- **Estado:** 80% tasa de error (8/10 ejecuciones fallidas)
- **Problemas identificados:** 3 errores críticos

---

## Problemas Diagnosticados y Solucionados

### 1. Nodo "0. Mapear Input del Orquestador1" - Mapeo Incorrecto

**Síntoma:** Todos los campos retornaban `null`

**Causa Raíz:** 
- Usaba optional chaining (`?.`) que n8n NO soporta en expresiones
- Expresión: `$json.nombre || $json.body?.nombre || ''`

**Solución Aplicada:**
```javascript
// ANTES (incorrecto)
={{ $json.nombre || $json.body?.nombre || '' }}

// DESPUÉS (correcto)
={{ $json.nombre ? $json.nombre : ($json.body ? $json.body.nombre : '') }}
```

**Campos corregidos:** nombre, email, telefono, empresa, cargo, servicio_interes, mensaje, utm_source, utm_campaign

---

### 2. Nodo "3. Es Lead HOT? (If)1" - Error de Configuración

**Síntoma:** `Cannot read properties of undefined (reading 'caseSensitive')`

**Causa Raíz:** 
- Faltaba la estructura `options` dentro de `conditions`

**Solución Aplicada:**
```json
{
  "conditions": {
    "options": {
      "caseSensitive": true,
      "leftValue": "",
      "typeValidation": "strict"
    }
  }
}
```

---

### 3. Nodo "6. Enviar Respuesta Lead1" - Gmail Mal Configurado

**Síntoma:** `Invalid value for 'operation'`

**Causa Raíz:**
- Faltaba `operation: "send"`
- Faltaban campos `subject` y `message`
- Solo tenía `sendTo` configurado

**Solución Aplicada:**
```json
{
  "operation": "send",
  "sendTo": "={{ $('0. Mapear Input del Orquestador1').item.json.email }}",
  "subject": "=Gracias por contactar a Carrillo Abogados - {{ $('0. Mapear Input del Orquestador1').item.json.nombre }}",
  "message": "={{ $input.first().json.content && $input.first().json.content.parts ? $input.first().json.content.parts[0].text : 'Gracias por contactarnos. Pronto le responderemos.' }}"
}
```

---

### 4. Orquestador Corrupto - Recreación Completa

**Síntoma:** Workflow aparecía vacío en UI, no se podía editar

**Causa Raíz:** 
- Actualizaciones previas corrompieron la estructura
- `webhookId` quedó como string vacío

**Solución Aplicada:**
1. Se eliminó el workflow corrupto (ID: `7yRMJecj0TaIdinU`)
2. Se creó nuevo workflow desde cero (ID: `bva1Kc1USbbITEAw`)

**Nueva estructura del Orquestador:**
```
Webhook → Identify → SubA → Consolidate → Respond
```

---

## Validación Final

### Ejecución de Prueba #93-94

| Workflow | ID | Status | Duración |
|----------|---|--------|----------|
| Orquestador | bva1Kc1USbbITEAw | ✅ SUCCESS | 25s |
| SUB-A | RHj1TAqBazxNFriJ | ✅ SUCCESS | 23s |

### Flujo Completo Ejecutado:

1. **Webhook** recibió POST con datos de lead de prueba
2. **Identify** clasificó como `new_lead` y enrutó a SUB-A
3. **SUB-A** ejecutó:
   - ✅ Mapeo de datos
   - ✅ Análisis IA (Gemini 2.5-pro) → Score: 90, Categoría: HOT
   - ✅ Guardado en Firestore (ID: `RKYlmxKeY5PCExZdJv8T`)
   - ✅ Notificación email al equipo (lead HOT)
   - ✅ Generación respuesta IA
   - ✅ Envío email al lead

### Datos de Prueba Usados:
```json
{
  "event_type": "new_lead",
  "nombre": "Maria Gonzalez",
  "email": "juanjosegomezagudelo1@gmail.com",
  "telefono": "+573101234567",
  "empresa": "TechCorp SAS",
  "cargo": "CEO",
  "servicio_interes": "Registro de Marca",
  "mensaje": "Necesitamos proteger nuestra marca urgentemente"
}
```

### Análisis IA Generado:
```json
{
  "normalized_interest": "Marcas",
  "is_spam": false,
  "analysis_reason": "Lead de alta calidad. Proviene de una CEO, la máxima autoridad en la toma de decisiones. La empresa parece ser del sector tecnológico (TechCorp) y el mensaje expresa una necesidad clara y urgente.",
  "calculated_score": 90,
  "category": "HOT"
}
```

---

## Estado Actual del Sistema

### Workflows Activos

| Workflow | ID | Estado | Webhook |
|----------|---|--------|---------|
| Orquestador | `bva1Kc1USbbITEAw` | ✅ ACTIVO | `/lead-events` |
| SUB-A | `RHj1TAqBazxNFriJ` | ⚪ INACTIVO (trigger por Orquestador) | N/A |

### URLs de Producción

- **Webhook:** `https://carrilloabgd.app.n8n.cloud/webhook/lead-events`
- **Test:** `https://carrilloabgd.app.n8n.cloud/webhook-test/lead-events`

### Credenciales Utilizadas

| Servicio | ID | Nombre |
|----------|---|--------|
| Google Gemini | jk2FHcbAC71LuRl2 | Google Gemini(PaLM) Api account |
| Gmail OAuth2 | l2mMgEf8YUV7HHlK | Gmail account |
| Google Firestore | AAhdRNGzvsFnYN9O | tuto yt |

---

## Lecciones Aprendidas

1. **n8n no soporta optional chaining (`?.`)** en expresiones - usar ternarios
2. **Nodos IF requieren estructura `options` completa** dentro de conditions
3. **Gmail node necesita `operation` explícito** - no se infiere de otros campos
4. **Validar workflows antes de deploy** con `n8n_validate_workflow`
5. **Usar `n8n_autofix_workflow`** para correcciones automáticas cuando sea posible

---

## Herramientas Utilizadas

- **n8n-mcp** v2.30.1 (via VS Code)
- Comandos principales:
  - `n8n_validate_workflow` - Diagnóstico
  - `n8n_update_partial_workflow` - Correcciones
  - `n8n_create_workflow` - Recreación Orquestador
  - `n8n_executions` - Análisis de errores

---

## Próximos Pasos Sugeridos

1. [ ] Monitorear ejecuciones en producción por 24-48h
2. [ ] Configurar alertas de error en n8n
3. [ ] Documentar payload esperado para integraciones externas
4. [ ] Considerar agregar retry logic para llamadas a Gemini API
