# SUB-K v2.0 - Changes Required for Orchestrator Integration

**Purpose:** Document exact changes needed to upgrade SUB-K from v1.0 to v2.0
**Target:** Make SUB-K compatible with Orchestrator v2.0 AI Agent
**Owner:** Agente Ingeniero (to implement)

---

## OVERVIEW OF CHANGES

| Change | Type | Impact | Priority |
|--------|------|--------|----------|
| Replace trigger | Node replacement | Critical | P0 |
| Add Google Sheets node | New node | Critical | P0 |
| Add "Read Enabled" node | New node | Critical | P0 |
| Modify Firestore node | Configuration change | Critical | P0 |
| Update Gmail notification | Template change | Minor | P1 |

---

## CHANGE 1: Replace Trigger Node

### Current (v1.0)

**Node Type:** `n8n-nodes-base.manualTrigger`

```json
{
  "parameters": {},
  "name": "Manual Trigger",
  "type": "n8n-nodes-base.manualTrigger",
  "typeVersion": 1,
  "position": [240, 300]
}
```

### New (v2.0)

**Node Type:** `n8n-nodes-base.executeWorkflowTrigger`

```json
{
  "parameters": {},
  "name": "Execute Workflow Trigger",
  "type": "n8n-nodes-base.executeWorkflowTrigger",
  "typeVersion": 1,
  "position": [240, 300]
}
```

**Reason:** Orchestrator AI Agent needs to call SUB-K as a Tool. Execute Workflow Trigger allows this.

**Testing:**
- Verify workflow can receive input from Tool node
- Test with manual execution first (n8n UI test button)

---

## CHANGE 2: Add Google Sheets "Save to Keywords_Master"

### Position in Workflow

**Insert AFTER:** Code node "Filter & Calculate Priority"
**Insert BEFORE:** Current Firestore node "Save to keywords_pipeline"

### New Node Configuration

```json
{
  "parameters": {
    "operation": "append",
    "documentId": {
      "__rl": true,
      "mode": "id",
      "value": "[KEYWORDS_MASTER_SHEET_ID]",
      "cachedResultName": "MW3 Content Factory - Keywords Master"
    },
    "sheetName": {
      "__rl": true,
      "mode": "list",
      "value": "gid=0",
      "cachedResultName": "All_Keywords"
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
    },
    "options": {
      "cellFormat": "USER_ENTERED"
    }
  },
  "name": "Save to Keywords_Master Sheet",
  "type": "n8n-nodes-base.googleSheets",
  "typeVersion": 4.7,
  "position": [960, 300],
  "credentials": {
    "googleSheetsOAuth2Api": {
      "id": "EiAQ3c7D8E2fCalN",
      "name": "Google Sheets account"
    }
  },
  "continueOnFail": false
}
```

**Key Points:**
- `priority_score` is left EMPTY because Google Sheet has formula
- `enabled` defaults to TRUE (Juan will manually disable if needed)
- `status` defaults to "pendiente"
- All other metadata fields empty (will be filled by SUB-L/SUB-M later)

**Connection:**
```
[Code: Filter & Calculate Priority]
    │
    ▼
[NEW: Save to Keywords_Master Sheet] ← ADD THIS
    │
    ▼
[Read Enabled Keywords] ← ADD THIS (next change)
    │
    ▼
[Firestore: Sync Enabled Only] ← MODIFY THIS (change 4)
```

---

## CHANGE 3: Add "Read Enabled Keywords" Node

### Purpose

Read back from Google Sheets ONLY keywords with `enabled=TRUE` to sync to Firestore.

### Position in Workflow

**Insert AFTER:** "Save to Keywords_Master Sheet" (Change 2)
**Insert BEFORE:** Firestore node (which will be modified in Change 4)

### New Node Configuration

```json
{
  "parameters": {
    "operation": "read",
    "documentId": {
      "__rl": true,
      "mode": "id",
      "value": "[KEYWORDS_MASTER_SHEET_ID]",
      "cachedResultName": "MW3 Content Factory - Keywords Master"
    },
    "sheetName": {
      "__rl": true,
      "mode": "list",
      "value": "gid=0",
      "cachedResultName": "All_Keywords"
    },
    "options": {
      "range": "A:P",
      "filters": {
        "conditions": [
          {
            "column": "I",
            "condition": "equal",
            "value": "TRUE"
          }
        ]
      }
    }
  },
  "name": "Read Enabled Keywords",
  "type": "n8n-nodes-base.googleSheets",
  "typeVersion": 4.7,
  "position": [1160, 300],
  "credentials": {
    "googleSheetsOAuth2Api": {
      "id": "EiAQ3c7D8E2fCalN",
      "name": "Google Sheets account"
    }
  },
  "continueOnFail": false
}
```

**Key Points:**
- Column I is the `enabled` column
- Filter: `column="I", condition="equal", value="TRUE"`
- Reads entire range A:P to get all fields
- Only returns rows where enabled=TRUE

**Expected Output:**
```json
[
  {
    "keyword_text": "cómo registrar marca software colombia",
    "volume": 320,
    "kd": 22,
    "cpc": 2.5,
    "priority_score": 85,
    "category": "registro de marca",
    "intent": "informational",
    "serp_features": "featured_snippet, people_also_ask",
    "enabled": "TRUE",
    "status": "pendiente",
    "assigned_to": "",
    "content_id": "",
    "published_url": "",
    "notes": "",
    "created_at": "2026-01-23T10:00:00.000Z",
    "updated_at": "2026-01-23T10:00:00.000Z"
  },
  // ... more keywords with enabled=TRUE
]
```

---

## CHANGE 4: Modify Firestore Node

### Current (v1.0)

**Operation:** `create` (creates all keywords)

**Behavior:** Saves ALL keywords from Code node directly to Firestore

### New (v2.0)

**Operation:** `upsert` (update if exists, insert if new)

**Behavior:** Saves ONLY keywords with enabled=TRUE from Google Sheets

### Updated Configuration

```json
{
  "parameters": {
    "operation": "upsert",
    "collection": "keywords_pipeline",
    "documentId": "={{ 'kw_' + $json.keyword_text.toLowerCase().replace(/\\s+/g, '_').substring(0, 30) }}",
    "dataMode": "defineBelow",
    "fieldsUi": {
      "field": [
        {
          "name": "keyword_id",
          "value": "={{ 'kw_' + $json.keyword_text.toLowerCase().replace(/\\s+/g, '_').substring(0, 30) }}"
        },
        {
          "name": "keyword_text",
          "value": "={{ $json.keyword_text }}"
        },
        {
          "name": "volume",
          "value": "={{ $json.volume }}"
        },
        {
          "name": "kd",
          "value": "={{ $json.kd }}"
        },
        {
          "name": "cpc",
          "value": "={{ $json.cpc }}"
        },
        {
          "name": "priority_score",
          "value": "={{ $json.priority_score }}"
        },
        {
          "name": "category",
          "value": "={{ $json.category }}"
        },
        {
          "name": "intent",
          "value": "={{ $json.intent }}"
        },
        {
          "name": "serp_features",
          "value": "={{ $json.serp_features }}"
        },
        {
          "name": "status",
          "value": "={{ $json.status || 'pendiente' }}"
        },
        {
          "name": "source",
          "value": "google_sheets"
        },
        {
          "name": "enabled",
          "value": "={{ $json.enabled }}"
        },
        {
          "name": "created_at",
          "value": "={{ $json.created_at }}"
        },
        {
          "name": "updated_at",
          "value": "={{ new Date().toISOString() }}"
        }
      ]
    },
    "options": {}
  },
  "name": "Sync to Firestore (Enabled Only)",
  "type": "n8n-nodes-base.googleCloudFirestore",
  "typeVersion": 2,
  "position": [1360, 300],
  "credentials": {
    "googleCloudFirestoreOAuth2Api": {
      "id": "AAhdRNGzvsFnYN9O",
      "name": "Google Cloud Firestore OAuth2"
    }
  },
  "continueOnFail": false
}
```

**Key Changes from v1.0:**
1. Operation: `create` → `upsert`
2. Input: Code node → "Read Enabled Keywords" node
3. Added field: `enabled` (to track in Firestore)
4. Added field: `source` = "google_sheets" (vs "dataforseo_api")
5. Document ID: Uses keyword_text to create unique ID (allows upsert)

**Why upsert?**
- If keyword already exists in Firestore, it updates
- If keyword is new, it creates
- Preserves any manual edits to existing keywords (like status changes by SUB-L)

**Connection Update:**
```
OLD:
[Code: Filter & Calculate Priority]
    │
    ▼
[Firestore: Save to keywords_pipeline]

NEW:
[Read Enabled Keywords]
    │
    ▼
[Firestore: Sync to Firestore (Enabled Only)]
```

---

## CHANGE 5: Update Gmail Notification

### Current (v1.0) Template

```
Subject: Keywords Research Complete
Body:
Se encontraron X keywords para el pipeline SEO.
Revisa Firestore para ver los resultados.
```

### New (v2.0) Template

```json
{
  "parameters": {
    "sendTo": "marketing@carrilloabgd.com",
    "subject": "=Keywords Research Complete - Revisar Google Sheet",
    "message": "=Hola Juan,\n\nSUB-K ejecutó con éxito la investigación de keywords.\n\nResultados:\n- Total keywords encontradas: {{ $('Code: Filter & Calculate Priority').item.json.total_count }}\n- Keywords guardadas en Google Sheet: {{ $('Save to Keywords_Master Sheet').item.json.rows_appended }}\n- Keywords sincronizadas a Firestore: {{ $('Sync to Firestore (Enabled Only)').item.json.items_synced }}\n\nPróximos pasos:\n1. Abre Google Sheet: https://docs.google.com/spreadsheets/d/[KEYWORDS_MASTER_SHEET_ID]\n2. Revisa keywords en tab 'All_Keywords'\n3. Marca 'enabled=FALSE' en keywords que NO quieres usar\n4. Agrega notas si es necesario\n\nLas keywords con 'enabled=TRUE' serán procesadas por SUB-L (Content Writer) cada lunes.\n\n---\nAutomated by MW#3 SUB-K v2.0",
    "options": {
      "appendAttribution": false
    }
  },
  "name": "Notify Juan - Review Required",
  "type": "n8n-nodes-base.gmail",
  "typeVersion": 2.1,
  "credentials": {
    "gmailOAuth2": {
      "id": "l2mMgEf8YUV7HHlK",
      "name": "Gmail account"
    }
  }
}
```

**Key Changes:**
- Subject mentions "Revisar Google Sheet" (action required)
- Body includes link to Google Sheet
- Body explains what Juan needs to do
- Body shows metrics from each step

---

## UPDATED WORKFLOW DIAGRAM

### v1.0 (Current)

```
[Manual Trigger]
    │
    ▼
[HTTP: DataForSEO Keyword Ideas]
    │
    ▼
[HTTP: DataForSEO Keyword Difficulty]
    │
    ▼
[HTTP: DataForSEO SERP Analysis]
    │
    ▼
[Code: Filter & Calculate Priority]
    │
    ▼
[Firestore: Save to keywords_pipeline]
    │
    ▼
[Gmail: Notify Juan]
```

### v2.0 (New)

```
[Execute Workflow Trigger] ← CHANGE 1
    │
    ▼
[HTTP: DataForSEO Keyword Ideas]
    │
    ▼
[HTTP: DataForSEO Keyword Difficulty]
    │
    ▼
[HTTP: DataForSEO SERP Analysis]
    │
    ▼
[Code: Filter & Calculate Priority]
    │
    ▼
[Save to Keywords_Master Sheet] ← CHANGE 2 (NEW NODE)
    │
    ▼
[Read Enabled Keywords] ← CHANGE 3 (NEW NODE)
    │
    ▼
[Sync to Firestore (Enabled Only)] ← CHANGE 4 (MODIFIED)
    │
    ▼
[Notify Juan - Review Required] ← CHANGE 5 (UPDATED)
```

**Total Nodes:**
- v1.0: 11 nodes
- v2.0: 13 nodes (+2)

---

## VALIDATION CHECKLIST

### Before Deploying v2.0

- [ ] Google Sheet "Keywords_Master" exists and is configured
- [ ] KEYWORDS_MASTER_SHEET_ID is documented
- [ ] n8n OAuth2 has Editor permission on Sheet
- [ ] Credential "Google Sheets account" (id: EiAQ3c7D8E2fCalN) works
- [ ] Credential "Google Cloud Firestore OAuth2" (id: AAhdRNGzvsFnYN9O) works

### After Deploying v2.0

- [ ] Test 1: Manual execution from n8n UI
  - [ ] Verify keywords append to Google Sheet
  - [ ] Verify priority_score formula calculates
  - [ ] Verify only enabled=TRUE sync to Firestore
  - [ ] Verify Gmail notification received

- [ ] Test 2: Called by Orchestrator as Tool
  - [ ] Orchestrator can execute SUB-K successfully
  - [ ] Input payload passes correctly
  - [ ] Output returns to Orchestrator

- [ ] Test 3: Human workflow
  - [ ] Juan can open Google Sheet
  - [ ] Juan can edit enabled column
  - [ ] Re-running SUB-K respects enabled=FALSE (doesn't sync)

---

## ROLLBACK PLAN

If v2.0 fails in production:

1. **Immediate rollback:**
   - Deactivate SUB-K v2.0 workflow in n8n Cloud
   - Reactivate SUB-K v1.0 (if available)

2. **Manual fallback:**
   - Keywords go directly to Firestore (v1.0 behavior)
   - Juan edits in Firestore Console (less user-friendly)

3. **Investigation:**
   - Check logs in Google Sheets Logger (Orchestrator)
   - Check execution logs in n8n UI
   - Verify Google Sheets permissions

4. **Fix and redeploy:**
   - Correct issue
   - Test in separate workflow first
   - Deploy to production when validated

---

## TESTING SCENARIOS

### Scenario 1: Normal Execution

**Input:** Orchestrator calls SUB-K with event_type="monthly_keyword_research"

**Expected:**
- 30 keywords appended to Google Sheet
- 30 keywords synced to Firestore (all enabled=TRUE by default)
- Gmail sent to Juan
- Orchestrator receives success response

### Scenario 2: Juan Disables Some Keywords

**Setup:**
1. SUB-K executes, adds 30 keywords to Sheet
2. Juan marks 5 keywords as enabled=FALSE
3. SUB-K executes again (next month)

**Expected:**
- New 30 keywords appended to Sheet (total 60 rows)
- Only 55 keywords in Firestore (30 old + 30 new - 5 disabled)
- Disabled keywords NOT in Firestore

### Scenario 3: Keyword Already Exists in Firestore

**Setup:**
1. Keyword "registro de marca" already in Firestore with status="en_progreso"
2. SUB-K finds same keyword again

**Expected:**
- Google Sheet: New row appended (duplicate OK in Sheet)
- Firestore: Existing document UPDATED (upsert preserves status="en_progreso")
- Status NOT overwritten to "pendiente"

### Scenario 4: Google Sheets API Fails

**Simulate:** Remove OAuth2 permission temporarily

**Expected:**
- Node "Save to Keywords_Master Sheet" fails
- Error caught (continueOnFail=false)
- Workflow stops, doesn't proceed to Firestore
- Orchestrator receives error response
- Error notification sent via Gmail

---

## MIGRATION NOTES

### Data Migration

**No migration needed** because:
- Existing keywords in Firestore remain untouched
- New v2.0 only adds `enabled` and `source` fields
- Old keywords will work with SUB-L (reads from Firestore)

**Optional cleanup:**
- Add `enabled: true` and `source: "legacy"` to existing Firestore keywords via script

### Backwards Compatibility

**SUB-L (Content Writer):**
- No changes needed
- Still reads from Firestore `keywords_pipeline`
- Field `enabled` is optional (defaults to true if missing)

**Orchestrator:**
- Calls SUB-K as Tool (Execute Workflow Trigger)
- Input/output format unchanged

---

## CHANGELOG

| Date | Version | Changes |
|------|---------|---------|
| 2026-01-23 | 2.0 | Google Sheets integration, enabled/disabled control, upsert to Firestore |
| 2026-01-22 | 1.0 | Initial version (Manual Trigger, direct to Firestore) |

---

**Implementation Checklist for Agente Ingeniero:**

1. [ ] Read this document completely
2. [ ] Read GOOGLE_SHEETS_KEYWORDS_MASTER.md for Sheet structure
3. [ ] Backup SUB-K v1.0 JSON (archive as SUB-K_v1.0_backup.json)
4. [ ] Implement changes 1-5 in new workflow
5. [ ] Test each node individually
6. [ ] Test complete workflow end-to-end
7. [ ] Import to n8n Cloud
8. [ ] Test integration with Orchestrator
9. [ ] Document final workflow ID
10. [ ] Update STATUS.md

**Estimated Time:** 3-4 hours (including testing)

---

**Document Owner:** Agente Arquitecto
**Implementer:** Agente Ingeniero
**Date:** 2026-01-23
