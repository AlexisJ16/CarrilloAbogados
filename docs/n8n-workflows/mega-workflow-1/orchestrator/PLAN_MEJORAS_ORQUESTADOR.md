# PLAN DE MEJORAS DEL WORKFLOW ORQUESTADOR
## Hoja de Ruta para Implementación de Mejoras Críticas

**Proyecto:** MEGA-WORKFLOW #1 - Lead Lifecycle Manager
**Cliente:** Carrillo Abogados
**Workflow ID actual:** `bva1Kc1USbbITEAw`
**Fecha:** 2026-01-02
**Prioridad:** ALTA

---

## RESUMEN EJECUTIVO

Este plan detalla las mejoras necesarias para hacer el orquestador completamente compatible con la arquitectura Hub & Spoke documentada y prepararlo para la integración de spokes futuros (SUB-D, SUB-E, SUB-F).

**Esfuerzo total estimado:** 7 horas
**Fases:** 3
**Impacto:** CRÍTICO para escalabilidad

---

## FASE 1: CORRECCIONES CRÍTICAS
### Duración: 3 horas | Prioridad: CRÍTICA

### Objetivo
Implementar el Switch node y eliminar limitaciones que bloquean la integración de spokes futuros.

---

### Tarea 1.1: Agregar nodo "Validate Input"
**Duración:** 30 minutos
**Ubicación:** Entre "Webhook Principal" y "Identify"

**Código propuesto:**
```javascript
// NODO: Validate Input
// Tipo: Code (JavaScript)
// Versión: 2

const input = $input.first().json;
const payload = input.body || input;

// VALIDACIÓN DE ESTRUCTURA
if (!payload || typeof payload !== 'object') {
  throw new Error('Invalid payload: must be JSON object');
}

// VALIDACIÓN DE CAMPOS SEGÚN EVENT_TYPE
const eventType = payload.event_type;

if (eventType === 'new_lead' || (!eventType && payload.email)) {
  // Validar campos requeridos para new_lead
  if (!payload.email) {
    throw new Error('Invalid new_lead event: missing required field "email"');
  }

  // Validar formato de email
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(payload.email)) {
    throw new Error(`Invalid email format: ${payload.email}`);
  }
}

if (eventType === 'email_opened' || eventType === 'email_clicked') {
  // Validar campos para eventos de email
  if (!payload.lead_id && !payload.email) {
    throw new Error('Invalid email event: missing lead_id or email');
  }
}

if (eventType === 'meeting_booked') {
  // Validar campos para eventos de reunión
  if (!payload.email) {
    throw new Error('Invalid meeting event: missing email');
  }
}

// PASAR DATOS SIN MODIFICAR
return {
  json: payload
};
```

**Configuración n8n:**
```json
{
  "name": "Validate Input",
  "type": "n8n-nodes-base.code",
  "typeVersion": 2,
  "position": [350, 304],
  "parameters": {
    "jsCode": "... (código de arriba)",
    "mode": "runOnceForAllItems"
  },
  "onError": "continueErrorOutput",
  "notesInFlow": true,
  "notes": "Valida estructura y campos requeridos según event_type"
}
```

**Testing:**
```bash
# Test de validación positiva
curl -X POST https://carrilloabgd.app.n8n.cloud/webhook/lead-events \
  -H "Content-Type: application/json" \
  -d '{"event_type": "new_lead", "email": "test@test.com"}'

# Respuesta esperada: HTTP 200

# Test de validación negativa (email inválido)
curl -X POST https://carrilloabgd.app.n8n.cloud/webhook/lead-events \
  -H "Content-Type: application/json" \
  -d '{"event_type": "new_lead", "email": "not-an-email"}'

# Respuesta esperada: HTTP 400 con mensaje "Invalid email format"
```

---

### Tarea 1.2: Renombrar y Mejorar nodo "Identify"
**Duración:** 30 minutos

**Cambios:**
1. Renombrar de "Identify" a "Classify Event Type"
2. Eliminar hardcoding de workflow ID
3. Mejorar lógica de clasificación

**Código mejorado:**
```javascript
// NODO: Classify Event Type
// Tipo: Code (JavaScript)
// Versión: 2

const payload = $json;
let eventType = payload.event_type || 'unknown';

// CLASIFICACIÓN INTELIGENTE
if (eventType === 'unknown') {
  // Inferir event_type si no está explícito
  if (payload.email && !payload.lead_id) {
    eventType = 'new_lead';
  } else if (payload.mailersend_event || payload.email_id) {
    if (payload.mailersend_event === 'email.opened') {
      eventType = 'email_opened';
    } else if (payload.mailersend_event === 'email.clicked') {
      eventType = 'email_clicked';
    } else {
      eventType = 'email_event';
    }
  } else if (payload.calendly_event || payload.meeting_start) {
    eventType = 'meeting_booked';
  }
}

// AGREGAR METADATA DEL ORQUESTADOR
return {
  json: {
    ...payload,
    event_type: eventType,
    orchestrator_timestamp: new Date().toISOString(),
    orchestrator_execution_id: $execution.id,
    orchestrator_version: '2.0'
  }
};
```

**Nota:** Ya NO incluye `target_workflow_id` porque el Switch manejará el routing.

---

### Tarea 1.3: Agregar nodo "Switch"
**Duración:** 45 minutos
**Ubicación:** Entre "Classify Event Type" y "Execute SUB-A"

**Configuración del Switch:**

```json
{
  "name": "Route by Event Type",
  "type": "n8n-nodes-base.switch",
  "typeVersion": 3.2,
  "position": [680, 304],
  "parameters": {
    "rules": {
      "rules": [
        {
          "name": "New Lead",
          "output": 0,
          "conditions": {
            "options": {
              "caseSensitive": true,
              "leftValue": "",
              "typeValidation": "strict"
            },
            "conditions": [
              {
                "id": "new_lead_condition",
                "leftValue": "={{ $json.event_type }}",
                "rightValue": "new_lead",
                "operator": {
                  "type": "string",
                  "operation": "equals"
                }
              }
            ],
            "combinator": "and"
          }
        },
        {
          "name": "Email Opened",
          "output": 1,
          "conditions": {
            "options": {
              "caseSensitive": true,
              "leftValue": "",
              "typeValidation": "strict"
            },
            "conditions": [
              {
                "id": "email_opened_condition",
                "leftValue": "={{ $json.event_type }}",
                "rightValue": "email_opened",
                "operator": {
                  "type": "string",
                  "operation": "equals"
                }
              }
            ],
            "combinator": "and"
          }
        },
        {
          "name": "Email Clicked",
          "output": 1,
          "conditions": {
            "options": {
              "caseSensitive": true,
              "leftValue": "",
              "typeValidation": "strict"
            },
            "conditions": [
              {
                "id": "email_clicked_condition",
                "leftValue": "={{ $json.event_type }}",
                "rightValue": "email_clicked",
                "operator": {
                  "type": "string",
                  "operation": "equals"
                }
              }
            ],
            "combinator": "and"
          }
        },
        {
          "name": "Meeting Booked",
          "output": 2,
          "conditions": {
            "options": {
              "caseSensitive": true,
              "leftValue": "",
              "typeValidation": "strict"
            },
            "conditions": [
              {
                "id": "meeting_condition",
                "leftValue": "={{ $json.event_type }}",
                "rightValue": "meeting_booked",
                "operator": {
                  "type": "string",
                  "operation": "equals"
                }
              }
            ],
            "combinator": "and"
          }
        },
        {
          "name": "Nurturing Trigger",
          "output": 3,
          "conditions": {
            "options": {
              "caseSensitive": true,
              "leftValue": "",
              "typeValidation": "strict"
            },
            "conditions": [
              {
                "id": "nurturing_condition",
                "leftValue": "={{ $json.event_type }}",
                "rightValue": "nurturing_trigger",
                "operator": {
                  "type": "string",
                  "operation": "equals"
                }
              }
            ],
            "combinator": "and"
          }
        }
      ]
    },
    "options": {
      "fallbackOutput": 4
    }
  },
  "notesInFlow": true,
  "notes": "Enruta eventos a spokes según event_type"
}
```

**Outputs del Switch:**
- Output 0: `new_lead` → SUB-A
- Output 1: `email_opened` o `email_clicked` → SUB-E (futuro)
- Output 2: `meeting_booked` → SUB-F (futuro)
- Output 3: `nurturing_trigger` → SUB-D (futuro)
- Output 4 (fallback): Eventos desconocidos → Error Handler

---

### Tarea 1.4: Mover nodo "SubA" a rama del Switch
**Duración:** 15 minutos

**Pasos:**
1. Desconectar nodo "SubA" de "Identify"
2. Conectar nodo "SubA" al **Output 0** del Switch (new_lead)
3. Actualizar posición visual para claridad

**Configuración (sin cambios en el nodo):**
```json
{
  "name": "Execute SUB-A (Lead Intake)",
  "type": "n8n-nodes-base.executeWorkflow",
  "typeVersion": 1.2,
  "position": [900, 180],
  "parameters": {
    "workflowId": {
      "__rl": true,
      "value": "RHj1TAqBazxNFriJ",
      "mode": "id"
    },
    "options": {
      "waitForSubWorkflow": true,
      "timeout": 30000
    }
  }
}
```

**Nota:** Se agrega timeout de 30 segundos.

---

### Tarea 1.5: Agregar nodo "Error Handler"
**Duración:** 30 minutos
**Ubicación:** Conectado al Output 4 (fallback) del Switch

**Código del Error Handler:**
```javascript
// NODO: Error Handler
// Tipo: Code (JavaScript)
// Versión: 2

const payload = $json;

// IDENTIFICAR TIPO DE ERROR
let errorType = 'unknown_event_error';
let errorMessage = `Evento no reconocido: ${payload.event_type || 'undefined'}`;
let errorDetails = {
  event_type: payload.event_type || 'undefined',
  payload_keys: Object.keys(payload),
  orchestrator_execution_id: payload.orchestrator_execution_id
};

// RETORNAR ESTRUCTURA DE ERROR CONSISTENTE
return {
  json: {
    success: false,
    event_type: 'error',
    sub_workflow_executed: null,
    orchestrator_execution_id: payload.orchestrator_execution_id || $execution.id,
    orchestrator_timestamp: payload.orchestrator_timestamp || new Date().toISOString(),
    result: null,
    error: {
      type: errorType,
      message: errorMessage,
      details: errorDetails
    }
  }
};
```

**Configuración n8n:**
```json
{
  "name": "Error Handler",
  "type": "n8n-nodes-base.code",
  "typeVersion": 2,
  "position": [900, 480],
  "parameters": {
    "jsCode": "... (código de arriba)",
    "mode": "runOnceForAllItems"
  },
  "notesInFlow": true,
  "notes": "Maneja eventos no reconocidos y errores de validación"
}
```

---

### Tarea 1.6: Actualizar nodo "Consolidate"
**Duración:** 30 minutos

**Objetivo:** Validar respuesta de spokes y enriquecer con metadata.

**Código mejorado:**
```javascript
// NODO: Consolidate Response
// Tipo: Code (JavaScript)
// Versión: 2

const spokeResult = $json;
const classifyNode = $('Classify Event Type').item.json;

// VALIDAR ESTRUCTURA DE RESPUESTA DEL SPOKE
if (typeof spokeResult !== 'object' || spokeResult === null) {
  throw new Error('Spoke returned invalid response: expected object');
}

// VALIDAR CAMPO SUCCESS
const success = spokeResult.success !== false;

// CALCULAR TIEMPO DE EJECUCIÓN
const startTime = new Date(classifyNode.orchestrator_timestamp);
const endTime = new Date();
const executionTimeMs = endTime - startTime;

// CONSTRUIR RESPUESTA CONSOLIDADA
return {
  json: {
    success: success,
    event_type: classifyNode.event_type,
    sub_workflow_executed: spokeResult.sub_workflow_executed || 'unknown',
    execution_time_ms: executionTimeMs,
    orchestrator_execution_id: classifyNode.orchestrator_execution_id,
    orchestrator_timestamp: classifyNode.orchestrator_timestamp,
    orchestrator_version: classifyNode.orchestrator_version || '2.0',
    result: success ? spokeResult : null,
    error: success ? null : {
      type: spokeResult.error_type || 'spoke_execution_error',
      message: spokeResult.error_message || 'Spoke execution failed',
      details: spokeResult.error_details || spokeResult
    }
  }
};
```

**Nota:** Ahora valida estructura de respuesta y calcula latencia.

---

### Tarea 1.7: Actualizar nodo "Respond"
**Duración:** 15 minutos

**Código mejorado:**
```json
{
  "name": "Respond to Webhook",
  "type": "n8n-nodes-base.respondToWebhook",
  "typeVersion": 1.1,
  "position": [1136, 304],
  "parameters": {
    "respondWith": "json",
    "responseBody": "={{ $json }}",
    "options": {
      "responseCode": "={{ $json.success ? 200 : ($json.error.type === 'validation_error' ? 400 : 500) }}"
    }
  }
}
```

**Mejoras:**
- Devuelve TODO el objeto consolidado (incluye metadata)
- HTTP 400 para errores de validación
- HTTP 500 para errores de ejecución

---

### Testing de Fase 1
**Duración:** 15 minutos

**Test 1: New Lead (camino feliz)**
```bash
curl -X POST https://carrilloabgd.app.n8n.cloud/webhook/lead-events \
  -H "Content-Type: application/json" \
  -d '{
    "event_type": "new_lead",
    "nombre": "Test Usuario",
    "email": "test@test.com",
    "empresa": "Test Corp"
  }'
```

**Validar:**
- Switch enruta a Output 0
- SUB-A se ejecuta
- Respuesta incluye `execution_time_ms`, `orchestrator_execution_id`
- HTTP 200

**Test 2: Evento desconocido**
```bash
curl -X POST https://carrilloabgd.app.n8n.cloud/webhook/lead-events \
  -H "Content-Type: application/json" \
  -d '{"event_type": "evento_inventado", "data": "test"}'
```

**Validar:**
- Switch enruta a Output 4 (fallback)
- Error Handler procesa
- Respuesta incluye `error.type: "unknown_event_error"`
- HTTP 500

**Test 3: Email inválido**
```bash
curl -X POST https://carrilloabgd.app.n8n.cloud/webhook/lead-events \
  -H "Content-Type: application/json" \
  -d '{"event_type": "new_lead", "email": "not-an-email"}'
```

**Validar:**
- Validate Input lanza error
- HTTP 400 con mensaje claro

---

## FASE 2: MEJORAS DE CALIDAD
### Duración: 2 horas | Prioridad: ALTA

### Objetivo
Mejorar robustez, documentación y testing.

---

### Tarea 2.1: Agregar try-catch a nodos Code
**Duración:** 30 minutos

**Aplicar en:**
- Validate Input
- Classify Event Type
- Error Handler
- Consolidate Response

**Patrón:**
```javascript
try {
  // ... lógica actual
} catch (error) {
  return {
    json: {
      success: false,
      error_type: 'internal_error',
      error_message: error.message,
      error_stack: error.stack,
      orchestrator_execution_id: $execution.id
    }
  };
}
```

---

### Tarea 2.2: Agregar comentarios al código
**Duración:** 30 minutos

**Estándar de comentarios:**
```javascript
/**
 * NODO: Validate Input
 * PROPOSITO: Validar estructura y campos requeridos del payload
 * INPUT: Payload del webhook
 * OUTPUT: Payload validado sin modificar
 * ERRORES: Lanza excepción si validación falla
 */

// PASO 1: Extraer payload
const input = $input.first().json;
const payload = input.body || input;

// PASO 2: Validar estructura básica
if (!payload || typeof payload !== 'object') {
  throw new Error('Invalid payload: must be JSON object');
}

// PASO 3: Validar según event_type
// ...
```

---

### Tarea 2.3: Crear colección de Postman
**Duración:** 1 hora

**Estructura de colección:**
```
Carrillo Abogados - Workflow Orquestador
├── 01 - New Lead
│   ├── HOT Lead (score >= 70)
│   ├── WARM Lead (score 40-69)
│   └── COLD Lead (score < 40)
├── 02 - Email Events
│   ├── Email Opened
│   └── Email Clicked
├── 03 - Meeting Events
│   └── Meeting Booked
├── 04 - Error Cases
│   ├── Invalid Email
│   ├── Unknown Event
│   └── Missing Required Fields
└── 05 - Edge Cases
    ├── Empty Payload
    └── Malformed JSON
```

**Variables de entorno:**
```json
{
  "webhook_url": "https://carrilloabgd.app.n8n.cloud/webhook/lead-events",
  "test_email": "test@test.com",
  "test_nombre": "Test Usuario"
}
```

---

## FASE 3: PREPARACION PARA SPOKES FUTUROS
### Duración: 2 horas | Prioridad: MEDIA

### Objetivo
Preparar nodos placeholder para SUB-D, SUB-E, SUB-F.

---

### Tarea 3.1: Agregar nodo "Execute SUB-E (Placeholder)"
**Duración:** 30 minutos
**Ubicación:** Conectado al Output 1 del Switch

**Código placeholder:**
```javascript
// NODO: Execute SUB-E (Engagement Tracker) - PLACEHOLDER
// Tipo: Code (JavaScript)

const payload = $json;

return {
  json: {
    success: false,
    message: 'SUB-E no implementado todavía',
    event_type: payload.event_type,
    lead_id: payload.lead_id || null,
    note: 'Este spoke se implementará en Fase 4'
  }
};
```

**Configuración:**
```json
{
  "name": "Execute SUB-E (Placeholder)",
  "type": "n8n-nodes-base.code",
  "typeVersion": 2,
  "position": [900, 280],
  "disabled": false,
  "parameters": {
    "jsCode": "... (código de arriba)"
  },
  "notesInFlow": true,
  "notes": "PLACEHOLDER - Implementar cuando SUB-E esté listo"
}
```

---

### Tarea 3.2: Agregar nodo "Execute SUB-F (Placeholder)"
**Duración:** 30 minutos
**Similar a Tarea 3.1**

---

### Tarea 3.3: Agregar nodo "Execute SUB-D (Placeholder)"
**Duración:** 30 minutos
**Similar a Tarea 3.1**

---

### Tarea 3.4: Documentar interfaces de spokes
**Duración:** 30 minutos

**Crear archivo:** `spoke_interfaces.md`

**Contenido:**
```markdown
# Interfaces de Spokes

## SUB-E: Engagement Tracker

### Input esperado
- event_type: "email_opened" | "email_clicked"
- lead_id: string
- email: string
- campaign_id: string
- timestamp: ISO date

### Output esperado
- success: boolean
- lead_id: string
- engagement_updated: boolean
- score_increased: boolean
- category_changed: boolean
- new_category: "HOT" | "WARM" | "COLD" (si cambió)

## SUB-F: Meeting Scheduler

### Input esperado
...

## SUB-D: Nurturing Sequence

### Input esperado
...
```

---

## CHECKLIST DE IMPLEMENTACION

### Antes de empezar
- [ ] Hacer backup del workflow actual (exportar JSON)
- [ ] Confirmar que SUB-A está operativo
- [ ] Tener acceso a n8n Cloud

### Fase 1: Correcciones Críticas
- [ ] Tarea 1.1: Validate Input (30 min)
- [ ] Tarea 1.2: Renombrar Classify (30 min)
- [ ] Tarea 1.3: Agregar Switch (45 min)
- [ ] Tarea 1.4: Mover SubA (15 min)
- [ ] Tarea 1.5: Error Handler (30 min)
- [ ] Tarea 1.6: Consolidate mejorado (30 min)
- [ ] Tarea 1.7: Respond mejorado (15 min)
- [ ] Testing Fase 1 (15 min)

### Fase 2: Mejoras de Calidad
- [ ] Tarea 2.1: Try-catch (30 min)
- [ ] Tarea 2.2: Comentarios (30 min)
- [ ] Tarea 2.3: Colección Postman (1 hora)

### Fase 3: Preparación Futuros Spokes
- [ ] Tarea 3.1: SUB-E Placeholder (30 min)
- [ ] Tarea 3.2: SUB-F Placeholder (30 min)
- [ ] Tarea 3.3: SUB-D Placeholder (30 min)
- [ ] Tarea 3.4: Documentar interfaces (30 min)

### Después de implementar
- [ ] Probar TODOS los escenarios de Postman
- [ ] Actualizar documentación
- [ ] Notificar al equipo
- [ ] Monitorear ejecuciones durante 24h

---

## ROLLBACK PLAN

Si algo sale mal durante la implementación:

1. **Desactivar workflow mejorado**
2. **Reactivar backup del workflow original**
3. **Investigar logs de ejecución**
4. **Identificar qué tarea falló**
5. **Corregir en ambiente de desarrollo**
6. **Re-intentar cuando esté corregido**

**Backup del workflow original:**
- Ubicación: `01-orchestrator/artifacts/ORQUESTADOR_PRODUCTION_2025-12-21.json`
- Restaurar usando: n8n UI → Import from File

---

## CRITERIOS DE EXITO

Al finalizar las 3 fases, el orquestador debe:

1. ✅ Tener nodo Switch funcional con 5 rutas
2. ✅ Validar input del webhook
3. ✅ Manejar errores explícitamente
4. ✅ Retornar metadata completa en respuesta
5. ✅ Tener placeholders para spokes futuros
6. ✅ Pasar todos los tests de Postman
7. ✅ Tener código documentado con comentarios
8. ✅ Ser compatible con arquitectura Hub & Spoke

---

## PROXIMOS PASOS (Post-implementación)

1. **Implementar SUB-D (Nurturing)** - Prioridad ALTA
2. **Configurar Mailersend** - Requerido para SUB-E
3. **Implementar SUB-E (Engagement)** - Prioridad MEDIA
4. **Configurar Calendly** - Requerido para SUB-F
5. **Implementar SUB-F (Meeting)** - Prioridad MEDIA

---

**Documento generado por:** Agente Arquitecto
**Fecha:** 2026-01-02
**Versión:** 1.0
**Estado:** Listo para implementación
