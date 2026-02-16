# Google Sheets "Keywords_Master" - Specification

**Purpose:** Source of truth para keywords del pipeline SEO
**Owner:** marketing@carrilloabgd.com (Juan)
**Access:** n8n OAuth2 (Editor), Don Omar (Viewer)
**Update Frequency:** Monthly (automated by SUB-K) + Manual edits

---

## SHEET STRUCTURE

### Tab 1: All_Keywords (Main Data)

**Purpose:** Master list de todas las keywords investigadas

#### Column Definitions

| Column | Name | Type | Formula/Validation | Editable | Example |
|--------|------|------|-------------------|----------|---------|
| **A** | keyword_text | Text | - | No (auto from SUB-K) | "cómo registrar marca software colombia" |
| **B** | volume | Number | - | No (auto from SUB-K) | 320 |
| **C** | kd | Number | Range: 0-100 | No (auto from SUB-K) | 22 |
| **D** | cpc | Number | Currency (COP) | No (auto from SUB-K) | 2.50 |
| **E** | priority_score | Number | `=IF(B2="","",ROUND((B2/10)-C2+(D2*5),0))` | No (auto-calculated) | 85 |
| **F** | category | Dropdown | List validation | Yes (Juan edits) | "registro de marca" |
| **G** | intent | Dropdown | List validation | Yes (Juan edits) | "informational" |
| **H** | serp_features | Text | - | No (auto from SUB-K) | "featured_snippet, people_also_ask" |
| **I** | **enabled** | Checkbox | TRUE/FALSE | **Yes (Juan control)** | TRUE |
| **J** | status | Dropdown | List validation | Manual/Auto | "pendiente" |
| **K** | assigned_to | Dropdown | List validation | Yes (Juan assigns) | "" |
| **L** | content_id | Text | Link to Firestore | No (auto from SUB-L) | "draft_005" |
| **M** | published_url | URL | - | No (auto from SUB-M) | "" |
| **N** | notes | Text | - | Yes (Juan comments) | "Revisar competencia" |
| **O** | created_at | DateTime | Auto (SUB-K) | No | 2026-01-23T10:00:00Z |
| **P** | updated_at | DateTime | Auto (SUB-K) | No | 2026-01-23T10:00:00Z |

#### Data Validations

**Column F (category) - Dropdown:**
```
Valores permitidos:
- registro de marca
- propiedad intelectual
- patentes
- derechos de autor
- litigio PI
- contratos estatales
- derecho corporativo
- otro
```

**Column G (intent) - Dropdown:**
```
Valores permitidos:
- informational (usuario busca aprender)
- transactional (usuario quiere contratar)
- navigational (usuario busca sitio específico)
- commercial (usuario compara opciones)
```

**Column I (enabled) - Checkbox:**
```
Tipo: Checkbox
Valores: TRUE o FALSE
Default: TRUE

Función: Juan marca FALSE para keywords que NO quiere sincronizar a Firestore
```

**Column J (status) - Dropdown:**
```
Valores permitidos:
- pendiente (default, keyword esperando ser escrita)
- en_progreso (SUB-L está generando contenido)
- publicado (SUB-M publicó el artículo)
- descartado (Juan decidió no usar esta keyword)
```

**Column K (assigned_to) - Dropdown:**
```
Valores permitidos:
- (vacío) - No asignado
- Juan - Revisión manual requerida
- Gemini - AI generará contenido
- Pending - Esperando decisión
```

#### Conditional Formatting Rules

**Rule 1: Status Color Coding**
```
Condición: status = "publicado"
Formato: Background color #34a853 (verde)

Condición: status = "en_progreso"
Formato: Background color #fbbc04 (amarillo)

Condición: status = "descartado"
Formato: Background color #9e9e9e (gris) + Text strikethrough

Condición: status = "pendiente"
Formato: Background color #ffffff (blanco)
```

**Rule 2: Enabled/Disabled Highlighting**
```
Condición: enabled = FALSE
Formato: Background color #fce8e6 (rojo claro) en toda la fila
```

**Rule 3: High Priority Alert**
```
Condición: priority_score >= 80
Formato: Text bold + Background color #e8f5e9 (verde muy claro)
```

**Rule 4: Low Volume Warning**
```
Condición: volume < 100
Formato: Background color #fff3e0 (naranja claro) en columna B
```

#### Formula Cells

**Column E (priority_score):**
```excel
=IF(B2="","",ROUND((B2/10)-C2+(D2*5),0))
```

**Lógica:**
- `(volume / 10)` → Normaliza volumen (ej: 320 → 32 puntos)
- `- kd` → Resta dificultad (ej: -22 puntos)
- `+ (cpc * 5)` → Bonus por alto CPC (ej: 2.5 → +12.5 puntos)
- `ROUND()` → Redondea a entero

**Ejemplo:**
```
volume = 320, kd = 22, cpc = 2.5
priority_score = (320/10) - 22 + (2.5*5)
               = 32 - 22 + 12.5
               = 22.5
               ≈ 23
```

#### Protected Ranges

**Protect from accidental deletion/edit:**

1. **Row 1 (Headers):**
   - Range: `A1:P1`
   - Protection: Locked
   - Reason: Evitar cambios en estructura

2. **DataForSEO Columns (A-H):**
   - Range: `A2:H` (todas las filas)
   - Protection: Locked
   - Reason: Datos automáticos de API, no editar manualmente

3. **Auto-calculated Columns (E, O, P):**
   - Column E: `E2:E` (priority_score)
   - Column O-P: `O2:P` (timestamps)
   - Protection: Locked
   - Reason: Cálculos automáticos

**Allow editing:**
- Column I: `enabled` (Juan control total)
- Column J: `status` (Juan puede actualizar manualmente si necesita)
- Column K: `assigned_to` (Juan asigna keywords)
- Column N: `notes` (Juan agrega comentarios)

---

### Tab 2: Dashboard (Metrics & Analytics)

**Purpose:** Vista ejecutiva del estado del pipeline

#### Metrics Cards

**Card 1: Pipeline Overview**
```
Total Keywords: =COUNTA(All_Keywords!A:A)-1
Enabled: =COUNTIF(All_Keywords!I:I,TRUE)
Disabled: =COUNTIF(All_Keywords!I:I,FALSE)
Pendientes: =COUNTIF(All_Keywords!J:J,"pendiente")
En Progreso: =COUNTIF(All_Keywords!J:J,"en_progreso")
Publicados: =COUNTIF(All_Keywords!J:J,"publicado")
```

**Card 2: Quality Metrics**
```
Avg Priority Score: =AVERAGE(All_Keywords!E:E)
Avg Volume: =AVERAGE(All_Keywords!B:B)
Avg KD: =AVERAGE(All_Keywords!C:C)
High Priority (>=80): =COUNTIF(All_Keywords!E:E,">=80")
```

**Card 3: Category Distribution**
```
Pivot Table:
Rows: category
Values: Count of keyword_text
```

**Card 4: Intent Distribution**
```
Pivot Table:
Rows: intent
Values: Count of keyword_text
```

#### Charts

**Chart 1: Priority Score Distribution (Histogram)**
```
Type: Column chart
X-axis: Priority score buckets (0-20, 21-40, 41-60, 61-80, 81-100)
Y-axis: Count of keywords
```

**Chart 2: Status Breakdown (Pie Chart)**
```
Type: Pie chart
Slices: pendiente, en_progreso, publicado, descartado
Colors: White, Yellow, Green, Gray
```

**Chart 3: Monthly Progress (Line Chart)**
```
Type: Line chart
X-axis: Mes
Y-axis: Count of publicados
Data source: Filter All_Keywords by created_at month
```

---

### Tab 3: Enabled_Queue (Work Queue View)

**Purpose:** Vista filtrada de keywords listas para generar contenido

**Filter Configuration:**
```
Criteria:
- enabled = TRUE
- status = "pendiente"

Sort by:
- priority_score DESC

Display columns:
- keyword_text
- volume
- kd
- priority_score
- category
- notes
```

**This tab shows:** Keywords que SUB-L puede procesar (high to low priority)

---

### Tab 4: Published (Archive View)

**Purpose:** Vista de keywords que ya fueron publicadas

**Filter Configuration:**
```
Criteria:
- status = "publicado"

Sort by:
- created_at DESC (más recientes primero)

Display columns:
- keyword_text
- published_url
- content_id
- volume
- created_at
```

**Use case:** Verificar qué keywords ya fueron convertidas en artículos

---

### Tab 5: Archive (Disabled Keywords)

**Purpose:** Keywords descartadas o disabled

**Filter Configuration:**
```
Criteria:
- enabled = FALSE OR status = "descartado"

Sort by:
- updated_at DESC

Display columns:
- keyword_text
- volume
- kd
- notes (reason for disabling)
- updated_at
```

**Use case:** Historial de keywords descartadas (puede reactivarse later)

---

## SETUP INSTRUCTIONS

### Step 1: Create Google Sheet

1. **Go to:** https://sheets.google.com
2. **Create new sheet** → Name: `MW3 Content Factory - Keywords Master`
3. **Set owner:** marketing@carrilloabgd.com

### Step 2: Configure Tab "All_Keywords"

1. **Create columns A-P** con headers exactos (ver tabla arriba)
2. **Set column widths:**
   - A (keyword_text): 300px
   - B-D (volume, kd, cpc): 80px
   - E (priority_score): 100px
   - F-G (category, intent): 150px
   - H (serp_features): 200px
   - I (enabled): 80px
   - J-K (status, assigned_to): 120px
   - L-M (content_id, published_url): 150px
   - N (notes): 250px
   - O-P (timestamps): 180px

3. **Apply data validations** (ver sección Data Validations arriba)
4. **Apply conditional formatting** (ver sección Conditional Formatting Rules)
5. **Add formula to column E:** `=IF(B2="","",ROUND((B2/10)-C2+(D2*5),0))`
6. **Drag formula down** to row 1000 (pre-populate)
7. **Freeze row 1** (headers) y **freeze column A**
8. **Protect ranges** (ver sección Protected Ranges)

### Step 3: Configure Tab "Dashboard"

1. **Create tab** → Name: `Dashboard`
2. **Add metric cards** usando formulas (ver sección Dashboard arriba)
3. **Create pivot tables:**
   - Category distribution
   - Intent distribution
4. **Create charts:**
   - Priority score histogram
   - Status pie chart
   - Monthly progress line

### Step 4: Configure Filter Views

1. **Tab "Enabled_Queue":**
   - Create filter view: `enabled=TRUE AND status="pendiente"`
   - Sort: `priority_score DESC`
   - Share filter with: marketing@carrilloabgd.com

2. **Tab "Published":**
   - Create filter view: `status="publicado"`
   - Sort: `created_at DESC`

3. **Tab "Archive":**
   - Create filter view: `enabled=FALSE OR status="descartado"`
   - Sort: `updated_at DESC`

### Step 5: Share & Permissions

1. **Share with n8n OAuth2 service account:**
   - Email: (obtener de n8n Cloud credentials)
   - Permission: **Editor**
   - Notify: No

2. **Share with Juan:**
   - Email: marketing@carrilloabgd.com
   - Permission: **Editor**
   - Notify: Yes

3. **Share with Don Omar:**
   - Email: (obtener email)
   - Permission: **Viewer**
   - Notify: Yes

### Step 6: Get Sheet ID

1. **Open sheet URL:**
   ```
   https://docs.google.com/spreadsheets/d/[SHEET_ID]/edit
   ```

2. **Copy SHEET_ID** from URL (between `/d/` and `/edit`)

3. **Save to documentation:**
   - File: `automation/workflows/MW3_SEO_CONTENT_FACTORY/ENV_VARIABLES.md`
   - Variable: `KEYWORDS_MASTER_SHEET_ID=[SHEET_ID]`

4. **Configure in SUB-K workflow:**
   - Node: "Save to Keywords_Master Sheet"
   - Parameter: `documentId.value = [SHEET_ID]`

---

## INTEGRATION WITH n8n

### SUB-K Workflow Integration

**Node: "Save to Keywords_Master Sheet"**

```json
{
  "parameters": {
    "operation": "append",
    "documentId": {
      "__rl": true,
      "mode": "id",
      "value": "[KEYWORDS_MASTER_SHEET_ID]"
    },
    "sheetName": {
      "__rl": true,
      "mode": "list",
      "value": "gid=0"
    },
    "columns": {
      "mappingMode": "defineBelow",
      "value": {
        "keyword_text": "={{ $json.keyword }}",
        "volume": "={{ $json.volume }}",
        "kd": "={{ $json.kd }}",
        "cpc": "={{ $json.cpc }}",
        "priority_score": "",
        "category": "={{ $json.category }}",
        "intent": "={{ $json.intent }}",
        "serp_features": "={{ $json.serp_features ? $json.serp_features.join(', ') : '' }}",
        "enabled": "TRUE",
        "status": "pendiente",
        "assigned_to": "",
        "content_id": "",
        "published_url": "",
        "notes": "",
        "created_at": "={{ new Date().toISOString() }}",
        "updated_at": "={{ new Date().toISOString() }}"
      }
    }
  }
}
```

**Note:** priority_score se deja vacío porque hay fórmula en Sheet

**Node: "Read Enabled Keywords"**

```json
{
  "parameters": {
    "operation": "read",
    "documentId": {
      "__rl": true,
      "mode": "id",
      "value": "[KEYWORDS_MASTER_SHEET_ID]"
    },
    "sheetName": {
      "__rl": true,
      "mode": "list",
      "value": "gid=0"
    },
    "options": {
      "filters": {
        "conditions": [
          {
            "column": "enabled",
            "condition": "equal",
            "value": "TRUE"
          }
        ]
      }
    }
  }
}
```

**Output:** Array de keywords con enabled=TRUE para sincronizar a Firestore

---

## WORKFLOW FOR JUAN

### Monthly Keyword Review Process

**When:** Después de que SUB-K ejecute (1º del mes)

**Steps:**

1. **Open Google Sheet** "Keywords_Master"
2. **Go to tab "All_Keywords"**
3. **Sort by:** `priority_score DESC` (ya debería estar así)
4. **Review each new keyword:**
   - Read `keyword_text`, `volume`, `kd`, `priority_score`
   - Check if it aligns with Carrillo Abogados services
   - Check if it's not duplicate of existing content
5. **For keywords to DISABLE:**
   - Uncheck `enabled` column (I)
   - Add reason in `notes` column (N)
   - Examples: "Muy genérico", "Ya tenemos artículo similar", "Fuera de expertise"
6. **For keywords to KEEP:**
   - Leave `enabled = TRUE`
   - Optionally edit `category` if SUB-K auto-assigned wrong
   - Optionally edit `intent` if needed
   - Optionally add notes: "Priorizar", "Incluir case study Dr. Carrillo"
7. **Save changes** (auto-saves in Google Sheets)
8. **Next SUB-K run** will sync only enabled=TRUE to Firestore

**Time estimate:** 15-20 min para revisar 30 keywords

### Weekly Content Generation Check

**When:** Después de que SUB-L ejecute (cada lunes)

**Steps:**

1. **Go to tab "Enabled_Queue"**
2. **Check top 5 keywords** (highest priority)
3. **Verify SUB-L picked correct keyword:**
   - Column `status` should change to "en_progreso"
   - Column `content_id` will be populated when draft is done
4. **Review draft in Firestore** (separate process)
5. **If approved:**
   - Change `status` to "aprobado" manually in Sheet
   - This triggers SUB-M (when available)

---

## TROUBLESHOOTING

### Issue: Formula not calculating in column E

**Symptom:** priority_score shows blank or error

**Solution:**
```
1. Check formula syntax: =IF(B2="","",ROUND((B2/10)-C2+(D2*5),0))
2. Verify B2, C2, D2 have numeric values
3. Re-apply formula and drag down
```

### Issue: n8n can't append to sheet

**Symptom:** SUB-K fails with "Permission denied"

**Solution:**
```
1. Verify n8n OAuth2 service account has Editor permission
2. Check Sheet ID is correct in workflow
3. Test manually: Data → Named ranges → Check if writable
```

### Issue: Enabled column not filtering correctly

**Symptom:** Firestore has keywords with enabled=FALSE

**Solution:**
```
1. Check "Read Enabled Keywords" node in SUB-K
2. Verify filter condition: column="enabled", condition="equal", value="TRUE"
3. Test filter manually in Google Sheets
```

---

## CHANGELOG

| Date | Version | Changes |
|------|---------|---------|
| 2026-01-23 | 1.0 | Initial specification for MW#3 Orchestrator v2.0 |

---

**Document Owner:** Juan Jose (marketing@carrilloabgd.com)
**Last Updated:** 2026-01-23
**Status:** Design phase - Ready for implementation
