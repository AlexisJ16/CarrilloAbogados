# MW#3 ORCHESTRATOR v2.0 - Handoff Summary

**Date:** 2026-01-23
**From:** Agente Arquitecto
**To:** Usuario (Juan) → Agente Ingeniero (for implementation)
**Status:** Design Complete, Ready for Implementation

---

## RESUMEN EJECUTIVO

He diseñado el **Orchestrator v2.0 (AI Agent)** para MW#3 siguiendo el patrón exitoso de MW#1 v3.0.

### Lo Que Se Diseñó

Un **AI Agent Orchestrator** que:
- Coordina la pipeline completa de producción de contenido SEO
- Usa Gemini 2.0 Flash para decidir qué sub-workflow ejecutar
- Registra todas las decisiones en Google Sheets para observabilidad
- Permite control humano via Google Sheets "Keywords_Master"

### Diferencia Clave con MW#1

| Aspecto | MW#1 v3.0 | MW#3 v2.0 |
|---------|-----------|-----------|
| **Tools** | 1 (lead_intake) | 4 (keyword_research, content_writer, publisher, seo_tracker) |
| **Complejidad** | Baja (1 flujo) | Alta (múltiples flujos + validaciones) |
| **Source of Truth** | Firestore only | **Google Sheets + Firestore** |
| **Human Control** | Mínimo | **Alto (Juan edita enabled column)** |

### Innovación Principal: Google Sheets como Source of Truth

**Flujo de datos:**
```
DataForSEO API
    ↓
SUB-K guarda en Google Sheets "Keywords_Master"
    ↓
Juan revisa y marca enabled=TRUE/FALSE manualmente
    ↓
SUB-K sincroniza SOLO enabled=TRUE a Firestore
    ↓
SUB-L lee de Firestore para generar contenido
```

**Beneficios:**
- Juan tiene control total del pipeline desde Google Sheets
- Interface familiar (no necesita aprender Firestore Console)
- Fácil rollback (Google Sheets tiene version history)
- Transparencia total del proceso

---

## ARCHIVOS GENERADOS

| Archivo | Propósito | Páginas |
|---------|-----------|---------|
| **DESIGN_SPEC.md** | Especificación completa del Orchestrator v2.0 | 45 |
| **workflow_diagram.mermaid** | Diagrama de flujo del workflow | 1 |
| **GOOGLE_SHEETS_KEYWORDS_MASTER.md** | Estructura completa del Google Sheet | 18 |
| **CHANGES_REQUIRED_SUB-K.md** | Cambios específicos para SUB-K v2.0 | 12 |
| **HANDOFF_SUMMARY.md** | Este documento | 4 |

**Total:** 80 páginas de documentación

**Ubicación:** `C:\CarrilloAbogados\automation\workflows\MW3_SEO_CONTENT_FACTORY\01-orchestrator\`

---

## COMPONENTES DEL ORCHESTRATOR v2.0

### 13 Nodos Totales

1. **Webhook Principal** - Trigger (path: `/webhook/content-factory`)
2. **AI Agent Orchestrator** - Coordinador central con Gemini
3. **Google Gemini 2.0 Flash** - Modelo LLM
4. **Simple Memory** - Contexto de conversación
5. **Tool: SUB-K Keyword Research** - Llama SUB-K
6. **Tool: SUB-L Content Writer** - Llama SUB-L
7. **Tool: SUB-M Publisher** - Placeholder (bloqueado)
8. **Tool: SUB-N SEO Tracker** - Placeholder (pendiente)
9. **Respond to Webhook** - Respuesta HTTP
10. **Prepare Logger Data** - Transformación de datos
11. **Logger: Google Sheets** - Registro de ejecuciones
12. **Error Trigger** - Captura errores
13. **Error Notification** - Gmail de errores

### System Prompt del AI Agent

**Tamaño:** ~2,500 palabras (incluye 4 herramientas + reglas de decisión + ejemplos)

**Características:**
- Define claramente cuándo ejecutar cada Tool
- Incluye validaciones (ej: no ejecutar keyword_research 2 veces/mes)
- Maneja Tools bloqueados/pendientes
- Explica razonamiento en cada decisión

**Ver:** Sección 3.1 de DESIGN_SPEC.md

### Tools Configurados

| Tool | Sub-Workflow | Estado | Prioridad |
|------|--------------|--------|-----------|
| keyword_research | SUB-K | **REQUIERE CAMBIOS** | P0 |
| content_writer | SUB-L | JSON READY | P0 |
| publisher | SUB-M | BLOQUEADO | P2 |
| seo_tracker | SUB-N | PENDIENTE | P2 |

---

## CAMBIOS CRÍTICOS A SUB-K

### SUB-K v1.0 → v2.0

**Cambios requeridos:** 5

1. **Replace trigger:** Manual → Execute Workflow Trigger
2. **Add node:** Google Sheets "Save to Keywords_Master"
3. **Add node:** Google Sheets "Read Enabled Keywords"
4. **Modify node:** Firestore create → upsert (solo enabled=TRUE)
5. **Update node:** Gmail notification template

**Ver:** CHANGES_REQUIRED_SUB-K.md (12 páginas con detalles completos)

**Nodos totales:**
- v1.0: 11 nodos
- v2.0: 13 nodos (+2)

**Tiempo estimado implementación:** 3-4 horas

---

## GOOGLE SHEETS "KEYWORDS_MASTER"

### Estructura Completa Diseñada

**Tabs:** 5
1. **All_Keywords** - Master list (16 columnas A-P)
2. **Dashboard** - Métricas y charts
3. **Enabled_Queue** - Vista filtrada para SUB-L
4. **Published** - Artículos publicados
5. **Archive** - Keywords descartadas

### Columnas Clave

| Column | Name | Editable por Juan | Propósito |
|--------|------|-------------------|-----------|
| A-H | DataForSEO data | ❌ No | Datos de API (auto) |
| I | **enabled** | ✅ **Sí** | **Control del pipeline** |
| J | status | ✅ Sí | Estado del keyword |
| K | assigned_to | ✅ Sí | Asignación |
| N | notes | ✅ Sí | Comentarios de Juan |
| O-P | Timestamps | ❌ No | Auto-generado |

### Fórmula Automática

**Column E (priority_score):**
```excel
=IF(B2="","",ROUND((B2/10)-C2+(D2*5),0))
```

**Lógica:** `(volume / 10) - kd + (cpc * 5)`

### Data Validations & Conditional Formatting

- Dropdowns: category, intent, status, assigned_to
- Checkboxes: enabled
- Color coding: status (verde=publicado, amarillo=en_progreso, gris=descartado)
- Highlighting: enabled=FALSE → fila roja claro

**Ver:** GOOGLE_SHEETS_KEYWORDS_MASTER.md (18 páginas)

---

## LOGGER DE GOOGLE SHEETS

### Sheet: "MW3_Orchestrator_Logs"

**Columnas:** 10 (A-J)

| Column | Data | Purpose |
|--------|------|---------|
| A | timestamp | Cuándo se ejecutó |
| B | event_type | Qué evento disparó |
| C | tool_used | Qué tool se ejecutó |
| D | decision_reason | Por qué el AI decidió eso |
| E | execution_status | success/error |
| F | latency_ms | Tiempo de ejecución |
| G | error_message | Si hubo error |
| H | output_preview | Resultado (200 chars) |
| I | workflow_id | ID del orchestrator |
| J | execution_id | ID de ejecución |

**Uso:**
- Análisis de decisiones del AI Agent
- Debugging cuando algo falla
- Métricas de performance

**Ver:** Sección 7 de DESIGN_SPEC.md

---

## PRÓXIMOS PASOS

### Para Juan (Revisar Diseño)

**Acción:** Revisar sección 9 de DESIGN_SPEC.md (Google Sheets estructura)

**Decisiones pendientes:**
- [ ] Aprobar estructura de Google Sheets "Keywords_Master"
- [ ] Aprobar columnas editables por humano
- [ ] Aprobar flujo: Sheets → Firestore (solo enabled=TRUE)
- [ ] Confirmar que el System Prompt del AI Agent es correcto

**Tiempo estimado:** 20-30 min de lectura

### Para Agente Ingeniero (Implementar)

**Prerequisitos:**
1. Leer DESIGN_SPEC.md completo
2. Leer CHANGES_REQUIRED_SUB-K.md completo
3. Crear Google Sheets "Keywords_Master" según GOOGLE_SHEETS_KEYWORDS_MASTER.md
4. Crear Google Sheets "MW3_Orchestrator_Logs"

**Implementación:**
1. Implementar SUB-K v2.0 primero (cambios en 5 nodos)
2. Implementar Orchestrator v2.0 (13 nodos nuevos)
3. Testing E2E
4. Deploy a n8n Cloud

**Tiempo estimado:** 10 horas total
- SUB-K v2.0: 3-4 horas
- Orchestrator v2.0: 4-5 horas
- Testing: 2-3 horas

**Checklist:** Ver sección 11.1 de DESIGN_SPEC.md

---

## VALIDACIÓN DE VIABILIDAD

### Nodos n8n Verificados

✅ Todos los nodos existen en n8n Cloud:
- `n8n-nodes-base.webhook`
- `@n8n/n8n-nodes-langchain.agent`
- `@n8n/n8n-nodes-langchain.lmChatGoogleGemini`
- `@n8n/n8n-nodes-langchain.memoryBufferWindow`
- `@n8n/n8n-nodes-langchain.toolWorkflow`
- `n8n-nodes-base.googleSheets`
- `n8n-nodes-base.set`
- `n8n-nodes-base.respondToWebhook`
- `n8n-nodes-base.errorTrigger`
- `n8n-nodes-base.gmail`

### Credenciales Disponibles

✅ Todas las credenciales ya configuradas en n8n Cloud:
- Google Gemini API (id: jk2FHcbAC71LuRl2)
- Google Sheets OAuth2 (id: EiAQ3c7D8E2fCalN)
- Google Firestore OAuth2 (id: AAhdRNGzvsFnYN9O)
- Gmail OAuth2 (id: l2mMgEf8YUV7HHlK)

### Dependencias Externas

✅ No hay dependencias bloqueantes:
- SUB-L ya existe (JSON ready)
- SUB-M está marcado como bloqueado (AI Agent lo maneja)
- SUB-N está marcado como pendiente (AI Agent lo maneja)

### Posibles Limitaciones

⚠️ Consideraciones:
- System Prompt es largo (~2,500 palabras) - Puede requerir ajuste de tokens
- Temperature 0.2 puede necesitar tuning basado en calidad de decisiones
- Google Sheets API tiene límite de 100 requests/100 segundos (suficiente para nuestro uso)

---

## DIFERENCIAS CON MW#1 v3.0

### Arquitectura Similar

**Compartido:**
- AI Agent node con Gemini 2.0 Flash
- Tool Workflow nodes
- Google Sheets Logger
- Error handling con Gmail notification

**Nuevo en MW#3:**
- **4 Tools** (vs 1 en MW#1)
- **System Prompt más complejo** (validaciones, tools bloqueados)
- **Google Sheets integration** (Keywords_Master)
- **Two-phase sync** (Sheets → Firestore solo enabled=TRUE)

### System Prompt Comparison

| Aspecto | MW#1 | MW#3 |
|---------|------|------|
| **Tamaño** | ~500 palabras | ~2,500 palabras |
| **Tools** | 1 (lead_intake) | 4 (research, writer, publisher, tracker) |
| **Validations** | Mínimas | Extensas (duplicados, disponibilidad) |
| **Ejemplos** | 1 | 4 |

---

## MÉTRICAS DE DISEÑO

| Métrica | Valor |
|---------|-------|
| Tiempo de diseño | 4 horas |
| Páginas de documentación | 80 |
| Nodos diseñados | 13 (Orchestrator) + 2 nuevos (SUB-K) |
| Diagramas Mermaid | 3 |
| Credenciales requeridas | 4 (todas disponibles) |
| Google Sheets diseñadas | 2 (Keywords_Master, Logs) |
| Tabs en Keywords_Master | 5 |
| Columnas en Keywords_Master | 16 |
| Conditional formatting rules | 4 |
| Data validations | 4 |

---

## CRITERIOS DE ACEPTACIÓN

**El Orchestrator v2.0 debe:**

✅ **Funcionalidad Core:**
- Recibir eventos via webhook
- Analizar event_type con IA (Gemini)
- Ejecutar el Tool correcto basado en contexto
- Responder al webhook con resultado del tool

✅ **Observabilidad:**
- Registrar TODAS las ejecuciones en Google Sheets Logger
- Incluir decision_reason del AI Agent
- Manejar errores sin fallar el workflow

✅ **Validaciones:**
- NO ejecutar keyword_research 2 veces en el mismo mes
- Verificar que existan keywords antes de ejecutar content_writer
- Informar cuando un tool está bloqueado/no disponible

✅ **Integración:**
- SUB-K puede ser llamado como Tool
- SUB-L puede ser llamado como Tool
- SUB-L puede leer keywords desde Firestore

**SUB-K v2.0 debe:**

✅ **Google Sheets Integration:**
- Guardar keywords en "Keywords_Master"
- Sincronizar SOLO keywords con enabled=TRUE a Firestore
- Preservar status existente en Firestore (no sobreescribir)

✅ **Tool Compatibility:**
- Funcionar con Execute Workflow Trigger
- Recibir input del Orchestrator correctamente
- Retornar output estructurado

---

## RIESGOS Y MITIGACIONES

| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|--------------|---------|------------|
| System Prompt muy largo supera límite de tokens | Baja | Medio | Reducir ejemplos, usar referencias |
| AI Agent toma decisiones incorrectas | Media | Alto | Iterar en System Prompt, agregar ejemplos |
| Google Sheets API falla | Baja | Alto | continueOnFail=false, error notification |
| Juan no entiende flujo de Google Sheets | Media | Medio | Documentación clara, training session |
| SUB-K v2.0 rompe SUB-L | Baja | Alto | Testing E2E antes de deploy |

---

## COMANDO PARA ACTIVAR AGENTE INGENIERO

```
Actúa como Agente Ingeniero e implementa el Orchestrator v2.0 de MW#3 según:

1. Especificación completa: automation/workflows/MW3_SEO_CONTENT_FACTORY/01-orchestrator/DESIGN_SPEC.md
2. Cambios a SUB-K: automation/workflows/MW3_SEO_CONTENT_FACTORY/01-orchestrator/CHANGES_REQUIRED_SUB-K.md
3. Google Sheets setup: automation/workflows/MW3_SEO_CONTENT_FACTORY/01-orchestrator/GOOGLE_SHEETS_KEYWORDS_MASTER.md

Prerequisitos:
- Crear Google Sheets "Keywords_Master" y "MW3_Orchestrator_Logs" primero
- Implementar SUB-K v2.0 antes del Orchestrator
- Testing completo antes de deploy

Output esperado:
- JSON files para import a n8n Cloud
- Documentation de workflow IDs
- Test results
```

---

## PREGUNTAS PARA JUAN

Antes de pasar a implementación, necesito confirmación en:

1. **Google Sheets estructura:**
   - ¿Apruebas las 16 columnas de Keywords_Master?
   - ¿Te parece claro qué columnas puedes editar (I, J, K, N)?

2. **Flujo de trabajo humano:**
   - ¿Te sientes cómodo editando enabled=TRUE/FALSE en Google Sheets?
   - ¿Necesitas training o es suficiente la documentación?

3. **System Prompt del AI Agent:**
   - ¿Las reglas de decisión tienen sentido para ti?
   - ¿Hay algún caso de uso que no esté cubierto?

4. **Timeline:**
   - ¿Cuándo necesitas esto en producción?
   - ¿Hay alguna prioridad (ej: SUB-K antes que Orchestrator)?

---

## CONCLUSIÓN

El diseño está **100% completo** y **listo para implementación**.

**Documentación generada:**
- ✅ Especificación técnica completa (45 páginas)
- ✅ Diagramas de flujo (Mermaid)
- ✅ Google Sheets structures (18 páginas)
- ✅ Cambios a SUB-K (12 páginas)
- ✅ Handoff summary (este doc)

**Próximo paso:** Aprobación de Juan → Handoff a Agente Ingeniero

**Estimated delivery:** 10 horas de implementación + 2-3 horas de testing = **2 días de trabajo**

---

**Diseñado por:** Agente Arquitecto
**Fecha:** 2026-01-23
**Status:** Awaiting user approval
**Next Agent:** Agente Ingeniero (for implementation)
