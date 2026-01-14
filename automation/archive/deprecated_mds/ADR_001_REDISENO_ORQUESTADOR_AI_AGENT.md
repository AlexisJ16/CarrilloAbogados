# ADR-001: Rediseño del Orquestador con AI Agent Node

**Estado**: ✅ Aprobado (Pendiente implementación)
**Fecha**: 6 de Enero, 2026
**Autor**: Arquitecto n8n
**Contexto**: MEGA-WORKFLOW #1 - Lead Lifecycle Manager
**Impacto**: 🔴 **CRÍTICO** - Requiere refactorización completa del Orquestador

---

## 📋 CONTEXTO

### Situación Actual

El **Orquestador** (ID: `bva1Kc1USbbITEAw`) utiliza un nodo `Code` para identificar el tipo de evento y decidir qué sub-workflow ejecutar.

```javascript
// Nodo actual "Identify" (Code node)
const eventType = $json.event_type;
let targetWorkflow;

if (eventType === 'new_lead') {
  targetWorkflow = 'RHj1TAqBazxNFriJ'; // SUB-A
} else if (eventType === 'email_opened') {
  targetWorkflow = 'TBD'; // SUB-E
} else {
  throw new Error('Unknown event type');
}

return { targetWorkflow };
```

### Problema Identificado

Este enfoque presenta las siguientes limitaciones:

1. **No escalable**: Cada nuevo tipo de evento requiere modificar código
2. **Frágil**: Payloads inesperados rompen la lógica
3. **Sin contexto**: No puede analizar el contenido más allá del `event_type`
4. **Mantenimiento alto**: Lógica dispersa en múltiples nodos Code
5. **No inteligente**: No maneja ambigüedad ni patrones nuevos

### Evidencia

Según la **metodología Nate Herk** y la **AI Systems Pyramid**:

| Sistema Actual | Clasificación | Limitación |
|----------------|---------------|------------|
| Orquestador con Code | **Nivel 2: Workflow Automation** | Lógica determinista rígida |
| **Debería ser** | **Nivel 4: AI Agent** | Razonamiento flexible con múltiples herramientas |

---

## 🎯 DECISIÓN

### Solución Propuesta

**Rediseñar el Orquestador como un AI Agent (Nivel 4)** que utiliza el nodo `n8n-nodes-langchain.agent` con:

1. **LLM**: Google Gemini 2.5 Pro (mejor integración con ecosistema Google existente)
2. **Tools**: Sub-workflows conectados como herramientas (SUB-A, SUB-D, SUB-E, SUB-F)
3. **Memory**: Window Buffer Memory (últimos 5 eventos)
4. **System Prompt**: Instrucciones claras de routing

### Arquitectura Propuesta

```
ANTES (Nivel 2)
──────────────────────────────────────────────────
[Webhook] → [Code: Identify] → [Switch] → [Execute SUB-A] → [Respond]
              ❌ 30 líneas de lógica rígida

DESPUÉS (Nivel 4)
──────────────────────────────────────────────────
[Webhook] → [AI Agent] → [Respond] → [Logger]
              ↓
         Tools disponibles:
         • SUB-A: Lead Intake
         • SUB-D: Nurturing Engine
         • SUB-E: Engagement Tracker
         • SUB-F: Meeting Scheduler
              ✅ Decisión inteligente basada en contexto
```

---

## 🔍 JUSTIFICACIÓN

### ¿Por qué AI Agent y no AI Workflow?

| Criterio | AI Workflow (Nivel 3) | AI Agent (Nivel 4) | Orquestador |
|----------|----------------------|-------------------|-------------|
| **Secuencia predecible** | ✅ Sí | ❌ No | ❌ **No sabemos qué tool ejecutar hasta analizar payload** |
| **Decisiones dinámicas** | ⚠️ Limitadas | ✅ Flexibles | ✅ **Necesario: payload puede ser ambiguo** |
| **Múltiples herramientas** | ⚠️ Secuenciales | ✅ Dinámicas | ✅ **4+ sub-workflows** |
| **Mantenibilidad** | ✅ Alta | ⚠️ Media | ✅ **System Prompt > Código** |

**Conclusión**: El Orquestador cumple todos los criterios de un **AI Agent**.

### Beneficios Esperados

| Beneficio | Impacto | Métrica |
|-----------|---------|---------|
| **Escalabilidad** | Alto | Agregar nuevos sub-workflows sin modificar código |
| **Robustez** | Alto | Maneja payloads inesperados con razonamiento |
| **Observabilidad** | Alto | `returnIntermediateSteps: true` captura decisiones |
| **Flexibilidad** | Medio | Puede analizar contexto más allá de `event_type` |
| **Mantenimiento** | Alto | Ajustar comportamiento editando System Prompt |

### Costos y Riesgos

| Aspecto | Antes (Code) | Después (AI Agent) | Mitigación |
|---------|--------------|-------------------|------------|
| **Costo por ejecución** | $0 | ~$0.003 USD | Usar GPT-4o-mini ($0.15/1M tokens) |
| **Latencia** | ~100ms | ~2-3 seg | Aceptable para webhook (< 5s timeout) |
| **Confiabilidad** | 100% determinista | ~98% (alucinaciones) | System Prompt detallado + maxIterations=3 |
| **Debugging** | Fácil (código visible) | Requiere logs | Logger en Google Sheets |

**Decisión**: Los beneficios justifican los costos.

---

## 📊 COMPARATIVA TÉCNICA

### Arquitectura Code Node (Actual)

```yaml
Nodos: 8
├─ Webhook Principal
├─ Identify (Code) ❌ 30 líneas de lógica
├─ Router (Switch) ❌ 4 ramas
├─ Execute SUB-A
├─ Execute SUB-D
├─ Execute SUB-E
├─ Execute SUB-F
└─ Respond

Problemas:
- Lógica duplicada en Code + Switch
- Difícil agregar nuevos eventos
- No captura razón de decisión
```

### Arquitectura AI Agent (Propuesta)

```yaml
Nodos: 5
├─ Webhook Principal
├─ AI Agent ✅ Razonamiento flexible
│   Tools:
│   ├─ SUB-A: Lead Intake
│   ├─ SUB-D: Nurturing
│   ├─ SUB-E: Engagement
│   └─ SUB-F: Meeting
├─ Respond
├─ Logger (Google Sheets)
└─ Error Handler

Beneficios:
- 1 solo nodo de decisión
- System Prompt legible
- Captura decision_reason
- Logs detallados
```

---

## 🛠️ DETALLES DE IMPLEMENTACIÓN

### System Prompt (Google Gemini 2.5 Pro)

```markdown
# Rol
Eres el **Lead Lifecycle Orchestrator** de Carrillo Abogados, un bufete legal especializado en Propiedad Intelectual en Colombia.

# Tu Única Función
Identificar el tipo de evento entrante y ejecutar el sub-workflow correspondiente.

# Contexto de Negocio
- Procesamos 300+ leads/mes
- Tiempo de respuesta crítico: < 1 minuto
- Categorías de leads: HOT (≥70 score), WARM (40-69), COLD (<40)

# Herramientas Disponibles

## SUB-A: Lead Intake
**Descripción**: Procesa nuevo lead desde formulario web. Realiza validación, scoring con IA, guardado en Firestore, notificación HOT y respuesta automática.
**Cuándo usar**: Si el payload contiene `event_type: "new_lead"` o menciona "formulario", "contacto", "nuevo lead".

## SUB-D: Nurturing Engine
**Descripción**: Procesa batch de leads para enviar secuencia de nurturing (8-12 emails automatizados).
**Cuándo usar**: Si el payload contiene `event_type: "nurturing_manual_trigger"` o solicitud manual de ejecutar nurturing.

## SUB-E: Engagement Tracker
**Descripción**: Actualiza métricas de engagement (opens/clicks) y recalcula score del lead.
**Cuándo usar**: Si el payload contiene `event_type: "email_opened"` o `"email_clicked"` (webhooks de Mailersend).

## SUB-F: Meeting Scheduler
**Descripción**: Sincroniza reunión agendada en Google Calendar y notifica al equipo.
**Cuándo usar**: Si el payload contiene `event_type: "meeting_booked"` o evento de Google Calendar.

# Reglas de Decisión

1. SIEMPRE analiza el campo `event_type` primero
2. Si `event_type` es ambiguo, analiza el contenido completo del payload
3. Si NO estás seguro, responde con error claro (no adivines)
4. NUNCA ejecutes múltiples tools para un solo evento
5. Ejecuta exactamente 1 tool y retorna resultado

# Output Esperado

Después de ejecutar el tool, SIEMPRE incluye en tu respuesta:
- `tool_used`: Nombre del sub-workflow ejecutado (SUB-A | SUB-D | SUB-E | SUB-F)
- `decision_reason`: 1 frase explicando por qué elegiste ese tool
- `execution_status`: "success" | "error"

# Ejemplo

Input:
{
  "event_type": "new_lead",
  "nombre": "María",
  "email": "maria@test.com"
}

Tu acción:
1. Identificas: event_type = "new_lead"
2. Ejecutas: SUB-A (Lead Intake)
3. Retornas: { "tool_used": "SUB-A", "decision_reason": "Evento de nuevo lead desde formulario web", "execution_status": "success" }
```

### Configuración del Nodo AI Agent

```json
{
  "node": "n8n-nodes-langchain.agent",
  "name": "AI Agent Orchestrator",
  "type": "toolsAgent",
  "parameters": {
    "promptType": "define",
    "text": "={{ $json }}",
    "systemMessage": "{{ SYSTEM_PROMPT_ARRIBA }}",
    "options": {
      "returnIntermediateSteps": true,
      "maxIterations": 3,
      "humanMessageTemplate": "Analiza este evento y ejecuta el sub-workflow correspondiente:\n\n{{ $json }}"
    }
  },
  "connections": {
    "model": ["Google Gemini 2.5 Pro"],
    "tool": [
      "Execute SUB-A",
      "Execute SUB-D",
      "Execute SUB-E",
      "Execute SUB-F"
    ],
    "memory": ["Window Buffer Memory"]
  }
}
```

### Tools Configuration

Cada sub-workflow se conecta como Execute Workflow Tool:

```json
{
  "node": "n8n-nodes-base.executeWorkflow",
  "name": "SUB-A: Lead Intake",
  "parameters": {
    "workflowId": "RHj1TAqBazxNFriJ",
    "waitForCompletion": true,
    "source": {
      "parameter": "workflowId"
    }
  },
  "description": "Procesa nuevo lead: validación, scoring IA, guardado Firestore, notificación HOT, respuesta automática"
}
```

**Nota crítica**: El campo `description` es usado por el AI Agent para decidir cuándo invocar cada tool.

---

## 📋 PLAN DE MIGRACIÓN

### Fase 0: Preparación (30 min)

1. Crear backup del Orquestador actual
2. Crear Google Sheets para logging
3. Configurar credencial Claude API en n8n Cloud
4. Configurar credencial Google Sheets en n8n

### Fase 1: Crear Orquestador v3.0 (2 horas)

1. Crear nuevo workflow "Orquestador v3.0 (AI Agent)"
2. Copiar nodo Webhook del Orquestador actual
3. Agregar nodo AI Agent con configuración completa
4. Conectar SUB-A como primer tool
5. Configurar System Prompt
6. Configurar Memory (Window Buffer)
7. Testing básico con payload `new_lead`

### Fase 2: Testing y Validación (1 hora)

1. Test caso HOT: Lead con score ≥70
2. Test caso WARM: Lead con score 40-69
3. Test caso error: Payload inválido
4. Test caso ambiguo: event_type no reconocido
5. Verificar logger captura decision_reason
6. Verificar latencia < 5 segundos

### Fase 3: Cutover a Producción (30 min)

1. Desactivar Orquestador actual (bva1Kc1USbbITEAw)
2. Activar Orquestador v3.0
3. Actualizar webhook URL en documentación (si cambió)
4. Monitorear primeras 10 ejecuciones
5. Rollback si tasa de error > 5%

### Rollback Plan

Si el AI Agent falla:
1. Desactivar Orquestador v3.0
2. Re-activar Orquestador actual (bva1Kc1USbbITEAw)
3. Analizar logs de Google Sheets
4. Ajustar System Prompt según errores
5. Re-intentar en 24 horas

---

## 🎯 CRITERIOS DE ÉXITO

### Métricas Técnicas

| Métrica | Target | Medición |
|---------|--------|----------|
| **Precisión de routing** | 100% | Testing manual con 20 payloads variados |
| **Latencia promedio** | < 3 seg | Logger Google Sheets (campo latency_ms) |
| **Tasa de éxito** | > 98% | n8n execution logs |
| **Costo por ejecución** | < $0.005 USD | Logger Google Sheets (campo tokens_used) |

### Métricas de Negocio

| Métrica | Target | Fuente |
|---------|--------|--------|
| **Tiempo respuesta lead** | < 60 seg | End-to-end (Webhook → Email enviado) |
| **Leads procesados sin error** | > 95% | Firestore (leads con processed_at) |
| **Eventos no reconocidos** | < 1% | Logger Google Sheets (decision_reason) |

---

## 🔄 IMPACTO EN OTROS COMPONENTES

### SUB-A, SUB-D, SUB-E, SUB-F

**Cambio requerido**: NINGUNO

Los sub-workflows continúan recibiendo datos del Orquestador de la misma forma (via Execute Workflow). Solo cambia la lógica de routing.

### Webhook URL

**Cambio requerido**: Posiblemente cambio de ID

Si creamos nuevo workflow, el webhook URL puede cambiar:
- Actual: `https://carrilloabgd.app.n8n.cloud/webhook/lead-events`
- Nuevo: `https://carrilloabgd.app.n8n.cloud/webhook/lead-events` (mismo si copiamos webhook)

**Mitigación**: Copiar exactamente el nodo Webhook del Orquestador actual.

### Documentación

**Archivos a actualizar**:
- [STATUS.md](STATUS.md) - Actualizar ID del Orquestador
- [ARQUITECTURA_DATOS_N8N.md](../../ARQUITECTURA_DATOS_N8N.md) - Diagrama de flujo
- [TAREAS_MARKETING_DEV_MW1.md](../../TAREAS_MARKETING_DEV_MW1.md) - Estado de tareas

---

## 📚 REFERENCIAS

| Fuente | Descripción |
|--------|-------------|
| **Nate Herk: AI Agents Are Overused** | Metodología de AI Systems Pyramid |
| **Nate Herk: Orchestrator Pattern** | Parent-Child architecture |
| **n8n Docs: AI Agent Node** | Configuración técnica |
| **ARQUITECTURA_MW1_V3_NATE_HERK.md** | Especificación completa del rediseño |
| **WIREFRAME_MW1_V3.md** | Diagrama visual del flujo |

---

## 🔐 RIESGOS Y MITIGACIONES

### Riesgo 1: Alucinaciones del LLM

**Probabilidad**: Media (5%)
**Impacto**: Alto (ejecuta tool incorrecto)

**Mitigaciones**:
1. System Prompt extremadamente detallado
2. `maxIterations: 3` (evita bucles)
3. Logger captura `decision_reason` para auditar
4. Testing exhaustivo con 20+ payloads variados

### Riesgo 2: Costo inesperado

**Probabilidad**: Baja (< 1%)
**Impacto**: Medio (presupuesto excedido)

**Mitigaciones**:
1. Usar GPT-4o-mini ($0.15/1M tokens) en lugar de Claude ($3/1M)
2. Logger captura `tokens_used` para monitorear
3. Alertas si tokens/día > 10,000

### Riesgo 3: Latencia alta

**Probabilidad**: Baja (< 1%)
**Impacto**: Medio (timeout de webhook)

**Mitigaciones**:
1. AI Agent timeout: 10 segundos
2. Webhook timeout: 30 segundos (configurar en nodo)
3. Error Handler captura timeouts y notifica

### Riesgo 4: Cambio de API de n8n

**Probabilidad**: Baja (< 1%)
**Impacto**: Alto (workflow deja de funcionar)

**Mitigaciones**:
1. Mantener Orquestador actual (backup) por 30 días
2. Suscribirse a changelog de n8n
3. Testing en staging antes de producción

---

## ✅ DECISIÓN FINAL

### Aprobación

**Aprobado por**: Arquitecto n8n
**Fecha**: 6 de Enero, 2026
**Próxima acción**: Implementar Fase 0 (Preparación)

### Stakeholders a Notificar

- [ ] **Marketing Dev**: Responsable de implementación
- [ ] **QA Specialist**: Testing y validación
- [ ] **Backend Dev**: Informar cambio de arquitectura (informativo, no requiere acción)
- [ ] **Orchestrator Agent**: Coordinación cross-domain

---

**Versión**: 1.0
**Última actualización**: 6 de Enero, 2026
**Estado**: ✅ Aprobado para implementación
