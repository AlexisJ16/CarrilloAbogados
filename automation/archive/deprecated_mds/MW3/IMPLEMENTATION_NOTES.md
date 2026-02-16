# SUB-L: Content Writer AI - Implementation Notes

**Version:** 1.0
**Implemented:** 2026-01-23
**Status:** IMPLEMENTED - READY FOR TESTING
**Engineer:** Claude Agent (Agente Ingeniero n8n)

---

## 1. RESUMEN DE IMPLEMENTACION

### Workflow Creado
- **Nombre:** SUB-L: Content Writer AI (v1.0)
- **Archivo:** `SUB-L_Content_Writer_v1.json`
- **Nodos Totales:** 16 nodos (incluyendo nodo NoOp para flujo false del IF)
- **Estado Inicial:** INACTIVO (active: false)

### Nodos Implementados

| # | Nodo | Tipo | Estado |
|---|------|------|--------|
| 1 | Execute Workflow Trigger | Trigger | Listo |
| 2 | Query Next Keyword | Firestore Query | Listo |
| 3 | Check Keyword Exists | IF | Listo |
| 4 | No Keywords Available | NoOp | Listo |
| 5 | Set Variables | Set | Listo |
| 6 | Content Generator Agent | AI Agent | Listo |
| 7 | Google Gemini Chat Model | LLM | Listo |
| 8 | Parse AI Output | Code | Listo |
| 9 | Save Draft to Firestore | Firestore Create | Listo |
| 10 | Prepare Update Data | Set | Listo |
| 11 | Update Keyword Status | Firestore Upsert | Listo |
| 12 | Log Metrics to Sheets | Google Sheets Append | Listo |
| 13 | Notify Juan - Success | Gmail Send | Listo |
| 14 | Error Handler | Code | Listo |
| 15 | Log Error to Sheets | Google Sheets Append | Listo |
| 16 | Notify Juan - Error | Gmail Send | Listo |

---

## 2. CREDENCIALES CONFIGURADAS

| Servicio | Credential ID | Nombre |
|----------|---------------|--------|
| Google Gemini API | `jk2FHcbAC71LuRl2` | Google Gemini API |
| Gmail OAuth2 | `l2mMgEf8YUV7HHlK` | Gmail OAuth2 |
| Google Firestore | `AAhdRNGzvsFnYN9O` | Google Firestore account |
| Google Sheets | `googleSheetsOAuth2` | Google Sheets account |

**Nota:** La credencial de Google Sheets necesita verificarse en n8n Cloud. El ID placeholder `googleSheetsOAuth2` debe actualizarse con el ID real.

---

## 3. CONFIGURACION DEL AI AGENT

### Modelo LLM
- **Modelo:** `models/gemini-2.0-flash-exp`
- **Temperatura:** 0.7 (balance creatividad/consistencia)
- **Max Tokens:** 8000 (suficiente para 2500 palabras)

### System Prompt
El system prompt incluye:
- Contexto de experto en PI colombiana con experiencia SIC
- Keyword objetivo y metricas (volumen, categoria)
- Estructura detallada del articulo requerida
- Requisitos de longitud (2000-2500 palabras)
- Audiencia target (PyMEs tecnologicas Colombia)
- Guidelines SEO on-page
- Formato de output estructurado (metadata + markdown)

### Error Handling
- `onError: "continueErrorOutput"` en el AI Agent
- Segunda salida conectada a Error Handler
- Logging de errores a Google Sheets
- Notificacion por email en caso de fallo

---

## 4. FLUJO DE DATOS

### Flujo Principal (Happy Path)
```
Execute Workflow Trigger
    |
    v
Query Next Keyword (Firestore)
    |
    v
Check Keyword Exists (IF)
    |
    v [TRUE]
Set Variables
    |
    v
Content Generator Agent <-- Google Gemini Chat Model
    |
    v [SUCCESS]
Parse AI Output
    |
    v
Save Draft to Firestore
    |
    v
Prepare Update Data
    |
    v
Update Keyword Status
    |
    v
Log Metrics to Sheets
    |
    v
Notify Juan - Success
```

### Flujo de Error
```
Content Generator Agent
    |
    v [ERROR]
Error Handler
    |
    v
Log Error to Sheets
    |
    v
Notify Juan - Error
```

### Flujo sin Keywords
```
Check Keyword Exists
    |
    v [FALSE]
No Keywords Available (NoOp)
```

---

## 5. COLECCIONES FIRESTORE

### keywords_pipeline (READ + UPDATE)
```javascript
{
  keyword_id: string,       // ID unico
  keyword_text: string,     // Keyword para contenido
  volume: number,           // Volumen mensual de busqueda
  kd: number,               // Keyword difficulty
  cpc: number,              // Cost per click
  priority_score: number,   // Score de prioridad (0-100)
  category: string,         // Categoria de contenido
  status: string,           // "pendiente" | "en_progreso" | "publicado"
  created_at: timestamp,
  updated_at: timestamp,
  content_id: string        // Referencia al draft generado
}
```

### content_drafts (CREATE)
```javascript
{
  content_id: string,           // "draft_[timestamp]"
  keyword_id: string,           // Referencia a keyword
  title: string,                // Title tag SEO
  meta_description: string,     // Meta description
  slug: string,                 // URL slug
  content_markdown: string,     // Contenido completo en markdown
  word_count: number,           // Conteo de palabras
  status: string,               // "pending_revision" | "approved" | "rejected" | "published"
  reviewer_notes: string,       // Notas del revisor
  published_url: string | null, // URL una vez publicado
  created_at: timestamp,
  approved_at: timestamp | null,
  approved_by: string | null,
  ai_model: string,             // Modelo usado para generacion
  generation_metadata: {
    keyword_text: string,
    category: string,
    volume: number
  }
}
```

---

## 6. GOOGLE SHEETS

### Sheet: MW3_ContentWriter_Logs

**Tab: Logs**
| Columna | Tipo | Descripcion |
|---------|------|-------------|
| Timestamp | String | Fecha/hora de ejecucion |
| Keyword ID | String | ID de la keyword procesada |
| Keyword Text | String | Texto de la keyword |
| Content ID | String | ID del draft generado |
| Word Count | Number | Palabras generadas |
| Status | String | Estado del draft |
| Result | String | SUCCESS / ERROR |
| Duration (ms) | Number | Duracion de ejecucion |

**Tab: Errors**
| Columna | Tipo | Descripcion |
|---------|------|-------------|
| Timestamp | String | Fecha/hora del error |
| Keyword ID | String | ID de la keyword |
| Keyword Text | String | Texto de la keyword |
| Error Type | String | Tipo de error |
| Error Message | String | Mensaje de error |
| Execution ID | String | ID de ejecucion n8n |
| Resolution Status | String | Estado de resolucion |

---

## 7. POSICIONES DE NODOS (Layout Visual)

```
Y=140:  [Prepare Update Data]
Y=300:  [Trigger] -> [Query] -> [IF] -> [Set] -> [Agent] -> [Parse] -> [Save] -> [Update] -> [Log] -> [Email]
Y=480:  [NoOp (false)]
Y=520:  [Gemini Model]   [Error Handler] -> [Log Error] -> [Error Email]
```

X positions:
- 100: Execute Workflow Trigger
- 320: Query Next Keyword
- 540: Check Keyword Exists
- 760: Set Variables / No Keywords Available
- 980: Content Generator Agent / Gemini Model
- 1200: Parse AI Output / Error Handler
- 1420: Save Draft / Log Error
- 1640: Update Keyword / Prepare Update Data / Error Email
- 1860: Log Metrics
- 2080: Notify Success

---

## 8. VALIDACION

### Resultado de Validacion MCP
- **Total Nodos:** 16
- **Trigger Nodes:** 1
- **Conexiones Validas:** 14

### Warnings Identificados
1. AI Agent no tiene tools conectados - ESPERADO (Fase 0 es prompting simple)
2. Google Sheets sin error handling - Mitigado con `continueOnFail: true`
3. Cadena lineal larga - Aceptable para este workflow

### Expresiones n8n
Todas las expresiones usan el prefijo `=` correcto:
- `={{ $json.keyword_id }}`
- `={{ $now.toISO() }}`
- `={{ $('Set Variables').item.json.keyword_text }}`

---

## 9. PENDIENTES POST-IMPLEMENTACION

### Antes de Desplegar
- [ ] Verificar ID de credencial Google Sheets en n8n Cloud
- [ ] Crear Google Sheet `MW3_ContentWriter_Logs` con tabs Logs y Errors
- [ ] Crear indice en Firestore: `keywords_pipeline` (status ASC, priority_score DESC)
- [ ] Agregar documento de prueba en `keywords_pipeline` con status "pendiente"

### Tareas QA
- [ ] Test E2E con keyword de prueba
- [ ] Verificar que el output de IA sigue estructura esperada
- [ ] Verificar email llega a marketing@carrilloabgd.com
- [ ] Verificar logs se guardan correctamente
- [ ] Test de flujo de error (simular fallo en AI)

---

## 10. ARCHIVOS GENERADOS

```
02-spokes/sub-l-content-writer/
|-- DESIGN_SPEC.md                    # Especificacion de diseno (existente)
|-- SUB-L_Content_Writer_v1.json      # Workflow JSON (NUEVO)
|-- IMPLEMENTATION_NOTES.md           # Este archivo (NUEVO)
|-- test-data/
    |-- sample_keyword.json           # Keyword de prueba (existente)
    |-- sample_ai_output.json         # Output esperado (existente)
```

---

## 11. NOTAS TECNICAS

### Query Firestore
El query de Firestore usa formato JSON estructurado:
```json
{
  "from": [{"collectionId": "keywords_pipeline"}],
  "where": {
    "fieldFilter": {
      "field": {"fieldPath": "status"},
      "op": "EQUAL",
      "value": {"stringValue": "pendiente"}
    }
  },
  "orderBy": [{
    "field": {"fieldPath": "priority_score"},
    "direction": "DESCENDING"
  }],
  "limit": 1
}
```

### Parsing de Output IA
El Code node usa regex para extraer metadata del output:
- `metaRegex = /---\n([\s\S]*?)\n---/` - Extrae bloque de metadata
- `contentRegex = /---\n[\s\S]*?\n---\n([\s\S]+)/` - Extrae contenido

### Word Count
```javascript
const wordCount = content.split(/\s+/).filter(word => word.length > 0).length;
```

---

## 12. HANDOFF A QA

```markdown
## Handoff a QA Specialist

**Workflow Implementado**: SUB-L: Content Writer AI (v1.0)
**Archivo JSON**: SUB-L_Content_Writer_v1.json
**Nodos**: 16
**Estado**: Listo para validacion

**Archivos Generados**:
- SUB-L_Content_Writer_v1.json
- IMPLEMENTATION_NOTES.md (este archivo)

**Proximo Paso**: Importar en n8n Cloud y ejecutar test E2E

**Comando para Usuario**:
> Actua como Agente QA Specialist y valida el workflow SUB-L Content Writer
```

---

## 13. REFERENCIAS

| Documento | Ubicacion |
|-----------|-----------|
| DESIGN_SPEC.md | Misma carpeta |
| MW3 STATUS.md | `../../../STATUS.md` |
| Agent Protocols | `../../../../docs/01_AGENT_PROTOCOLS.md` |

---

**Fin del Documento**
**Estado:** IMPLEMENTED - READY FOR TESTING
**Proximo Paso:** QA Testing en n8n Cloud
