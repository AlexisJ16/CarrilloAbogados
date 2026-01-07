# Notas de Implementacion: Orquestador v3.0 (AI Agent - Gemini)

**Fecha**: 7 de Enero, 2026
**Workflow ID**: `68DDbpQzOEIweiBF`
**Estado**: CREADO - INACTIVO (requiere configuracion manual)
**Arquitectura**: AI Agent (Nivel 4 - Metodologia Nate Herk)

---

## Resumen de Implementacion

| Campo | Valor |
|-------|-------|
| **Workflow ID** | `68DDbpQzOEIweiBF` |
| **Nombre** | Orquestador v3.0 (AI Agent - Gemini) |
| **Nodos Totales** | 9 |
| **Estado** | Inactivo |
| **Webhook URL** | `https://carrilloabgd.app.n8n.cloud/webhook/lead-events-v3` |
| **LLM** | Google Gemini 2.5 Pro |
| **Temperature** | 0.3 (decisiones deterministas) |

---

## Nodos Implementados

### 1. Webhook Principal
- **ID**: `webhook_principal`
- **Tipo**: `n8n-nodes-base.webhook`
- **Version**: 2
- **Path**: `/lead-events-v3`
- **Metodo**: POST
- **Response Mode**: responseNode (espera Respond to Webhook)
- **Validacion**: PASS

### 2. AI Agent Orchestrator
- **ID**: `ai_agent_orchestrator`
- **Tipo**: `@n8n/n8n-nodes-langchain.agent`
- **Version**: 1.7
- **Configuracion**:
  - `promptType`: define
  - `text`: `={{ JSON.stringify($json) }}`
  - `maxIterations`: 3
  - `returnIntermediateSteps`: true
- **Error Handling**:
  - `onError`: continueRegularOutput
  - `retryOnFail`: true
  - `maxTries`: 2
  - `waitBetweenTries`: 3000ms
- **Validacion**: PASS

### 3. Google Gemini 2.5 Pro (Model)
- **ID**: `gemini_model`
- **Tipo**: `@n8n/n8n-nodes-langchain.lmChatGoogleGemini`
- **Version**: 1
- **Model**: `models/gemini-2.5-pro`
- **Options**:
  - `temperature`: 0.3
  - `maxOutputTokens`: 1024
- **Credencial**: `jk2FHcbAC71LuRl2` (Google Gemini Carrillo)
- **Validacion**: PASS

### 4. Tool: SUB-A Lead Intake
- **ID**: `tool_sub_a`
- **Tipo**: `@n8n/n8n-nodes-langchain.toolWorkflow`
- **Version**: 2
- **Workflow Target**: `RHj1TAqBazxNFriJ` (SUB-A v5)
- **Description**: Procesa nuevo lead desde formulario web...
- **Validacion**: PASS

### 5. Simple Memory (Buffer Window)
- **ID**: `memory_buffer`
- **Tipo**: `@n8n/n8n-nodes-langchain.memoryBufferWindow`
- **Version**: 1.3
- **Session Key**: `orchestrator_v3_memory`
- **Context Window**: 5 eventos
- **Validacion**: PASS

### 6. Respond to Webhook
- **ID**: `respond_webhook`
- **Tipo**: `n8n-nodes-base.respondToWebhook`
- **Version**: 1.1
- **Response**: JSON con resultado del AI Agent
- **HTTP Code**: 200
- **Validacion**: PASS

### 7. Logger: Google Sheets
- **ID**: `logger_sheets`
- **Tipo**: `n8n-nodes-base.googleSheets`
- **Version**: 4.5
- **Operation**: append
- **Columnas**:
  - timestamp
  - event_type
  - tool_used
  - decision_reason
  - execution_status
  - latency_ms
  - error_message
- **Continue On Fail**: true
- **Validacion**: PASS
- **NOTA**: Requiere configuracion manual de documentId y sheetName

### 8. Error Trigger
- **ID**: `error_trigger`
- **Tipo**: `n8n-nodes-base.errorTrigger`
- **Version**: 1
- **Validacion**: PASS

### 9. Error Notification (Gmail)
- **ID**: `error_notification`
- **Tipo**: `n8n-nodes-base.gmail`
- **Version**: 2.1
- **Destinatario**: ingenieria@carrilloabgd.com
- **Credencial**: `vr0xHa1bIOD0lWqe` (Gmail Carrillo)
- **Continue On Fail**: true
- **Validacion**: PASS

---

## Conexiones del Workflow

```
[Webhook Principal]
       |
       v
[AI Agent Orchestrator] <-- [Google Gemini 2.5 Pro] (ai_languageModel)
       ^                <-- [Tool: SUB-A Lead Intake] (ai_tool)
       |                <-- [Simple Memory] (ai_memory)
       |
       v
[Respond to Webhook]
       |
       v
[Logger: Google Sheets]

--- Error Flow ---

[Error Trigger]
       |
       v
[Error Notification]
```

---

## System Prompt del AI Agent

El system prompt implementado incluye:

1. **Rol**: Lead Lifecycle Orchestrator de Carrillo Abogados
2. **Contexto de Negocio**: 300+ leads/mes, respuesta < 1 min
3. **Herramientas Disponibles**:
   - SUB-A: Lead Intake (ACTIVO)
   - SUB-D: Nurturing Engine (NO DISPONIBLE)
   - SUB-E: Engagement Tracker (NO DISPONIBLE)
   - SUB-F: Meeting Scheduler (NO DISPONIBLE)
4. **Reglas de Decision**: 6 reglas claras
5. **Output Esperado**: JSON con tool_used, decision_reason, execution_status

---

## Credenciales Requeridas

| Credencial | ID | Estado |
|------------|-----|--------|
| Google Gemini API | `jk2FHcbAC71LuRl2` | CONFIGURADA |
| Gmail OAuth2 | `vr0xHa1bIOD0lWqe` | CONFIGURADA |
| Google Sheets OAuth2 | - | PENDIENTE |

---

## Acciones Requeridas del Usuario

### 1. Crear Google Sheet para Logging

1. Ir a Google Sheets: https://sheets.google.com
2. Crear nueva hoja llamada: `MW1_Orchestrator_Logs`
3. En la primera fila, agregar headers:
   - A1: `timestamp`
   - B1: `event_type`
   - C1: `tool_used`
   - D1: `decision_reason`
   - E1: `execution_status`
   - F1: `latency_ms`
   - G1: `error_message`
4. Copiar el ID del documento de la URL

### 2. Configurar Credencial Google Sheets en n8n

1. Ir a: https://carrilloabgd.app.n8n.cloud
2. Abrir workflow `Orquestador v3.0 (AI Agent - Gemini)`
3. Click en nodo `Logger: Google Sheets`
4. En "Credential to connect with", crear nueva credencial OAuth2
5. Seguir flujo de autorizacion con cuenta @carrilloabgd.com

### 3. Configurar Document ID y Sheet Name

1. En el nodo `Logger: Google Sheets`:
   - Document: Seleccionar `MW1_Orchestrator_Logs`
   - Sheet: Seleccionar `Sheet1` o `Logs`

### 4. Testing Manual

Ejecutar con payload de prueba:

```json
{
  "event_type": "new_lead",
  "nombre": "Test AI Agent",
  "email": "test@techcorp.co",
  "empresa": "Test Corp",
  "servicio_interes": "Registro de Marca",
  "mensaje": "Necesito proteger mi marca urgentemente"
}
```

**Comando curl**:
```bash
curl -X POST https://carrilloabgd.app.n8n.cloud/webhook-test/lead-events-v3 \
  -H "Content-Type: application/json" \
  -d '{
    "event_type": "new_lead",
    "nombre": "Test AI Agent",
    "email": "test@techcorp.co",
    "empresa": "Test Corp",
    "servicio_interes": "Registro de Marca",
    "mensaje": "Necesito proteger mi marca urgentemente"
  }'
```

### 5. Activar Workflow

Solo despues de testing exitoso:
1. Verificar que SUB-A (RHj1TAqBazxNFriJ) este funcionando
2. Activar Orquestador v3.0
3. Monitorear primeras 10 ejecuciones

---

## Diferencias vs Orquestador v1.0 (Actual)

| Aspecto | v1.0 (Actual) | v3.0 (Nuevo) |
|---------|---------------|--------------|
| **Nodos** | 8 | 9 |
| **Logica routing** | Code node (30 lineas) | AI Agent (system prompt) |
| **Escalabilidad** | Requiere modificar codigo | Agregar tools |
| **Observabilidad** | Limitada | returnIntermediateSteps + Logger |
| **Error handling** | Basico | Retry + Error Trigger + Notification |
| **LLM** | Ninguno (logica rígida) | Gemini 2.5 Pro |
| **Costo por ejecucion** | $0 | ~$0.003 USD |
| **Latencia esperada** | ~100ms | ~2-3 seg |

---

## Metricas de Exito

| Metrica | Target | Como medir |
|---------|--------|------------|
| Precision routing | 100% | Testing manual con 20 payloads |
| Latencia promedio | < 3 seg | Logger Google Sheets |
| Tasa de exito | > 98% | n8n execution logs |
| Costo por ejecucion | < $0.005 USD | Logger tokens_used |

---

## Rollback Plan

Si el AI Agent falla consistentemente:

1. Desactivar `Orquestador v3.0` (68DDbpQzOEIweiBF)
2. Re-activar `WORKFLOW A: Lead Lifecycle Manager` (bva1Kc1USbbITEAw)
3. Analizar logs en Google Sheets
4. Ajustar System Prompt segun errores
5. Re-intentar en 24 horas

---

## Proximos Pasos

1. [ ] Usuario: Crear Google Sheet para logging
2. [ ] Usuario: Configurar credencial Google Sheets
3. [ ] Usuario: Testing manual con payload new_lead
4. [ ] Usuario: Verificar Logger captura datos
5. [ ] Usuario: Activar workflow en produccion
6. [ ] Futuro: Implementar SUB-D (Nurturing)
7. [ ] Futuro: Implementar SUB-E (Engagement)
8. [ ] Futuro: Implementar SUB-F (Meeting)

---

## Archivos Generados

| Archivo | Path |
|---------|------|
| Workflow JSON | `artifacts/ORQUESTADOR_V3_DRAFT.json` |
| Notas Implementacion | `artifacts/IMPLEMENTATION_NOTES_ORQUESTADOR_V3.md` |

---

**Implementado por**: Agente Ingeniero n8n
**Metodologia**: Nate Herk AI Systems Pyramid
**Validacion**: Todos los nodos validados individualmente
