# SUB-L Content Writer: Migracion a Google Workspace

**Version:** 2.0
**Fecha:** 2026-01-24
**Autor:** Agente Ingeniero
**Estado:** DISEÑO COMPLETO - Pendiente Implementacion

---

## RESUMEN EJECUTIVO

Esta migracion elimina la dependencia de Google Firestore para almacenar borradores de contenido SEO, reemplazandola con una arquitectura 100% Google Workspace que incluye:

- **Google Sheets** para tracking y control de keywords/drafts
- **Google Drive/Docs** para almacenar el contenido editable
- **Gmail** para notificaciones con links directos a los documentos

### Beneficios

| Aspecto | Firestore (v1.0) | Google Workspace (v2.0) |
|---------|------------------|-------------------------|
| UX Edicion | Firebase Console | Google Docs (familiar) |
| Colaboracion | No nativo | Comentarios, sugerencias |
| Mobile | Firebase app | Google Docs app |
| Version History | Manual | Automatico |
| Costo | Firestore reads/writes | Incluido en Workspace |
| Curva aprendizaje | Alta | Minima |

---

## ARQUITECTURA v2.0

```
Google Sheets "Content_Pipeline" (entrada - tab Keywords)
    |
    v
SUB-L Content Writer
    |
    +---> Google Drive: Crea documento en carpeta MW3_Drafts
    |
    +---> Google Docs: Documento con contenido markdown
    |
    +---> Google Sheets "Content_Pipeline" (tab Drafts - tracking)
    |
    +---> Google Sheets (actualiza keyword status)
    |
    +---> Gmail: Notificacion con link al Google Doc
```

---

## CAMBIOS AL WORKFLOW

### NODOS ELIMINADOS (4)

| Nodo Original | Tipo | Razon de Eliminacion |
|---------------|------|----------------------|
| `Query Next Keyword` | googleFirebaseCloudFirestore | Reemplazado por Google Sheets |
| `Save Draft to Firestore` | googleFirebaseCloudFirestore | Reemplazado por Google Drive/Docs |
| `Prepare Update Data` | set | Ya no necesario (flujo simplificado) |
| `Update Keyword Status` | googleFirebaseCloudFirestore | Reemplazado por Google Sheets Update |

### NODOS AGREGADOS (4)

| Nuevo Nodo | Tipo | Proposito |
|------------|------|-----------|
| `Read Next Keyword (Sheets)` | googleSheets | Lee keyword con status="pendiente" |
| `Create Google Doc` | googleDrive | Crea documento con contenido |
| `Prepare Pipeline Data` | set | Prepara datos para Sheets |
| `Add to Content Pipeline (Drafts)` | googleSheets | Registra draft con URL del Doc |
| `Update Keyword Status (Sheets)` | googleSheets | Actualiza status del keyword |

### NODOS MODIFICADOS

| Nodo | Cambio |
|------|--------|
| `Set Variables` | Agrega priority_score, row_number |
| `Parse AI Output` | Agrega campos para Google Workspace |
| `Log Metrics to Sheets` | Agrega columna Google Doc URL |
| `Notify Juan - Success` | **IMPORTANTE**: Agrega link al Google Doc |
| `Notify Juan - Error` | Actualiza referencia a Google Sheets |

### NODOS SIN CAMBIOS (6)

- Execute Workflow Trigger
- Check Keyword Exists
- No Keywords Available
- Content Generator Agent
- Google Gemini Chat Model
- Error Handler
- Log Error to Sheets

---

## FLUJO DE DATOS NUEVO

```
1. Execute Workflow Trigger
   |
   v
2. Read Next Keyword (Sheets)
   - Sheet: Content_Pipeline
   - Tab: Keywords
   - Filter: status = "pendiente"
   - Return: First match only
   |
   v
3. Check Keyword Exists (IF)
   |
   +-- TRUE --> 4. Set Variables
   |
   +-- FALSE --> End (No Keywords Available)
   |
   v
4. Set Variables
   - keyword_id, keyword_text, volume, kd, category
   - priority_score, row_number (NUEVOS)
   - workflow_start
   |
   v
5. Content Generator Agent (Gemini 2.0 Flash)
   - System Prompt con spec de articulo
   - Output: Articulo completo en markdown
   |
   +-- SUCCESS --> 6. Parse AI Output
   |
   +-- ERROR --> Error Handler
   |
   v
6. Parse AI Output
   - Extrae: title, meta_description, slug
   - Calcula: word_count
   - Genera: content_id
   |
   v
7. Create Google Doc
   - Operation: createFromText
   - Title: "DRAFT: {{ title }}"
   - Content: {{ content_markdown }}
   - Folder: MW3_Drafts (env variable)
   - Convert to Google Doc: TRUE
   |
   v
8. Prepare Pipeline Data
   - google_doc_url: https://docs.google.com/document/d/{{ id }}/edit
   - google_doc_id: {{ id }}
   - Todos los campos para Sheets
   |
   v
9. Add to Content Pipeline (Drafts)
   - Sheet: Content_Pipeline
   - Tab: Drafts
   - Operation: Append row
   |
   v
10. Update Keyword Status (Sheets)
    - Sheet: Content_Pipeline
    - Tab: Keywords
    - Operation: Update row where keyword_id matches
    - Set: status = "en_progreso", content_id, updated_at
    |
    v
11. Log Metrics to Sheets
    - Sheet: MW3_ContentWriter_Logs
    - Tab: Logs
    - Incluye: Google Doc URL (NUEVO)
    |
    v
12. Notify Juan - Success
    - Email con LINK DIRECTO al Google Doc
    - Instrucciones de revision en Google Docs
```

---

## ESTRUCTURA GOOGLE SHEETS REQUERIDA

### Sheet 1: "Content_Pipeline"

**Tab "Keywords"** (columnas A-K):

| Columna | Campo | Tipo | Descripcion |
|---------|-------|------|-------------|
| A | keyword_id | string | ID unico (ej: kw_001) |
| B | keyword_text | string | La keyword objetivo |
| C | volume | number | Volumen mensual |
| D | kd | number | Keyword Difficulty (0-100) |
| E | cpc | number | Cost per click |
| F | priority_score | number | Score calculado |
| G | category | string | Categoria del contenido |
| H | intent | string | informational/transactional |
| I | status | string | pendiente/en_progreso/publicado |
| J | content_id | string | ID del draft (si existe) |
| K | updated_at | datetime | Ultima actualizacion |

**Tab "Drafts"** (columnas A-I):

| Columna | Campo | Tipo | Descripcion |
|---------|-------|------|-------------|
| A | content_id | string | ID unico (ej: draft_1706123456789) |
| B | keyword_id | string | FK a Keywords |
| C | title | string | Titulo del articulo |
| D | google_doc_url | string | Link al Google Doc |
| E | word_count | number | Conteo de palabras |
| F | status | string | pending_revision/approved/rejected/published |
| G | created_at | datetime | Fecha creacion |
| H | approved_at | datetime | Fecha aprobacion |
| I | approved_by | string | Nombre del aprobador |

### Sheet 2: "MW3_ContentWriter_Logs"

**Tab "Logs"** (columnas A-I):

| Columna | Campo | Tipo |
|---------|-------|------|
| A | Timestamp | datetime |
| B | Keyword ID | string |
| C | Keyword Text | string |
| D | Content ID | string |
| E | Google Doc URL | string |
| F | Word Count | number |
| G | Status | string |
| H | Result | string |
| I | Duration (ms) | number |

**Tab "Errors"** (columnas A-G):

| Columna | Campo | Tipo |
|---------|-------|------|
| A | Timestamp | datetime |
| B | Keyword ID | string |
| C | Keyword Text | string |
| D | Error Type | string |
| E | Error Message | string |
| F | Execution ID | string |
| G | Resolution Status | string |

---

## VARIABLES DE ENTORNO REQUERIDAS

Configurar en n8n Cloud (Settings > Variables):

| Variable | Valor | Descripcion |
|----------|-------|-------------|
| `MW3_CONTENT_PIPELINE_SHEET_ID` | (ID del Sheet) | Google Sheet "Content_Pipeline" |
| `MW3_CONTENTWRITER_LOGS_SHEET_ID` | (ID del Sheet) | Google Sheet "MW3_ContentWriter_Logs" |
| `MW3_DRAFTS_FOLDER_ID` | (ID de la carpeta) | Carpeta en Google Drive |

### Como obtener los IDs:

1. **Google Sheet ID**: Abrir el Sheet, copiar de la URL:
   ```
   https://docs.google.com/spreadsheets/d/[ESTE_ES_EL_ID]/edit
   ```

2. **Google Drive Folder ID**: Abrir la carpeta, copiar de la URL:
   ```
   https://drive.google.com/drive/folders/[ESTE_ES_EL_ID]
   ```

---

## CREDENCIALES

El workflow usa las siguientes credenciales (ya configuradas en n8n Cloud):

| Servicio | Credential ID | Nombre |
|----------|---------------|--------|
| Google Sheets OAuth2 | EiAQ3c7D8E2fCalN | Google Sheets OAuth2 |
| Google Drive OAuth2 | EiAQ3c7D8E2fCalN | Google Drive OAuth2 |
| Google Gemini API | 7mPlpd3eLy4qngdl | Google Gemini API |
| Gmail OAuth2 | l2mMgEf8YUV7HHlK | Gmail OAuth2 |

**NOTA**: Google Sheets y Google Drive usan la misma credencial OAuth2.

---

## INSTRUCCIONES DE SETUP

### Paso 1: Crear Carpeta en Google Drive

1. Ir a [Google Drive](https://drive.google.com)
2. Crear nueva carpeta: `MW3_Drafts`
3. (Opcional) Crear subcarpeta por ano: `MW3_Drafts/2026`
4. Copiar el Folder ID de la URL
5. Guardar en n8n: `MW3_DRAFTS_FOLDER_ID`

### Paso 2: Crear Google Sheet "Content_Pipeline"

1. Ir a [Google Sheets](https://sheets.google.com)
2. Crear nuevo spreadsheet: "Content_Pipeline"
3. Crear Tab 1: "Keywords" con headers:
   ```
   keyword_id | keyword_text | volume | kd | cpc | priority_score | category | intent | status | content_id | updated_at
   ```
4. Crear Tab 2: "Drafts" con headers:
   ```
   content_id | keyword_id | title | google_doc_url | word_count | status | created_at | approved_at | approved_by
   ```
5. Copiar el Sheet ID de la URL
6. Guardar en n8n: `MW3_CONTENT_PIPELINE_SHEET_ID`

### Paso 3: Crear Google Sheet "MW3_ContentWriter_Logs"

1. Crear nuevo spreadsheet: "MW3_ContentWriter_Logs"
2. Crear Tab 1: "Logs" con headers:
   ```
   Timestamp | Keyword ID | Keyword Text | Content ID | Google Doc URL | Word Count | Status | Result | Duration (ms)
   ```
3. Crear Tab 2: "Errors" con headers:
   ```
   Timestamp | Keyword ID | Keyword Text | Error Type | Error Message | Execution ID | Resolution Status
   ```
4. Copiar el Sheet ID de la URL
5. Guardar en n8n: `MW3_CONTENTWRITER_LOGS_SHEET_ID`

### Paso 4: Configurar Variables en n8n

1. Ir a n8n Cloud > Settings > Variables
2. Agregar las 3 variables:
   - `MW3_CONTENT_PIPELINE_SHEET_ID`
   - `MW3_CONTENTWRITER_LOGS_SHEET_ID`
   - `MW3_DRAFTS_FOLDER_ID`

### Paso 5: Importar Workflow v2.0

**Opcion A: Importar JSON completo (RECOMENDADO)**

1. Ir a n8n Cloud
2. Click "Add workflow" > "Import from file"
3. Seleccionar: `SUB-L_Content_Writer_v2_GoogleWorkspace.json`
4. El workflow se creara inactivo

**Opcion B: Actualizar workflow existente (NO RECOMENDADO)**

Si el workflow ya existe con ID `ZcaEG8VDm1IcG3LF`:
- El workflow actual usa Firestore
- Recomendacion: Crear nuevo workflow v2.0 y desactivar v1.0

### Paso 6: Test Manual

1. Agregar keyword de prueba al Sheet "Keywords":
   ```
   kw_test_001 | como registrar marca software colombia | 320 | 22 | 2.50 | 85 | registro de marca | informational | pendiente | |
   ```

2. Ejecutar workflow manualmente en n8n

3. Verificar:
   - [ ] Google Doc creado en carpeta MW3_Drafts
   - [ ] Fila agregada en tab "Drafts"
   - [ ] Keyword status actualizado a "en_progreso"
   - [ ] Log agregado en MW3_ContentWriter_Logs
   - [ ] Email recibido con link al documento

---

## COMPARATIVA v1.0 vs v2.0

| Aspecto | v1.0 (Firestore) | v2.0 (Google Workspace) |
|---------|------------------|-------------------------|
| Nodos totales | 16 | 17 |
| Credenciales | 3 (Firestore, Gemini, Gmail) | 4 (Sheets, Drive, Gemini, Gmail) |
| Almacenamiento | Firestore content_drafts | Google Docs |
| Tracking | Firestore keywords_pipeline | Google Sheets |
| Edicion | Firebase Console | Google Docs (WYSIWYG) |
| Notificacion | Link a Firebase | Link a Google Doc |
| Costo operativo | Firestore reads/writes | Incluido en Workspace |

---

## ARCHIVOS GENERADOS

| Archivo | Ubicacion | Descripcion |
|---------|-----------|-------------|
| `SUB-L_Content_Writer_v2_GoogleWorkspace.json` | `02-spokes/sub-l-content-writer/` | Workflow completo v2.0 |
| `MIGRATION_TO_WORKSPACE.md` | `02-spokes/sub-l-content-writer/` | Este documento |
| `SUB-L_Content_Writer_v1.json` | `02-spokes/sub-l-content-writer/` | Workflow original (backup) |

---

## ROLLBACK

Si es necesario volver a v1.0:

1. El workflow v1.0 sigue disponible en `SUB-L_Content_Writer_v1.json`
2. Los datos en Firestore no se eliminan
3. Simplemente activar v1.0 y desactivar v2.0

---

## PROXIMOS PASOS

1. [ ] **Juan**: Crear estructura Google Sheets (15 min)
2. [ ] **Juan**: Crear carpeta MW3_Drafts en Drive (2 min)
3. [ ] **Juan**: Configurar variables en n8n (5 min)
4. [ ] **Juan**: Importar workflow v2.0 (2 min)
5. [ ] **Juan**: Test con keyword de prueba (10 min)
6. [ ] **Juan**: Aprobar para produccion

**Tiempo estimado total:** 35 minutos

---

## PREGUNTAS FRECUENTES

**Q: Puedo usar el mismo Sheet que Keywords_Master de SUB-K?**
A: Si, puedes consolidar. La estructura es compatible. Solo asegurate de que el tab "Keywords" tenga todas las columnas requeridas.

**Q: Que pasa si el Google Doc falla al crearse?**
A: El workflow tiene error handling. Se registra en Errors tab y Juan recibe notificacion.

**Q: Puedo editar el contenido directamente en Google Docs?**
A: Si. El contenido se guarda en formato markdown pero es editable en Docs.

**Q: Como apruebo un draft?**
A: En Google Sheets tab "Drafts", cambia la columna "status" de "pending_revision" a "approved".

---

**Documento generado por Agente Ingeniero**
**Fecha:** 2026-01-24
**Workflow ID destino:** Nuevo (importar JSON)
