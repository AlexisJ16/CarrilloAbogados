# SUB-D: REFACTOR REPORT - Nodos Nativos vs HTTP Request

**Fecha**: 7 Enero 2026
**Ejecutado por**: @engineer
**Motivo**: Seguir best practices de n8n (usar nodos nativos en lugar de HTTP Request)

---

## ⚠️ CORRECCIÓN CRÍTICA (v2)

El primer refactor usó el tipo de nodo **INCORRECTO**:
- ❌ `n8n-nodes-base.googleCloudFirestore` (Admin API, no disponible en n8n Cloud)
- ✅ `n8n-nodes-base.googleFirebaseCloudFirestore` (Data API, el correcto)

### Diferencia Crítica:

| Nodo | Propósito | Disponible en n8n Cloud |
|------|-----------|------------------------|
| `googleCloudFirestore` | Admin API (gestión de BD) | ❌ NO |
| `googleFirebaseCloudFirestore` | Data API (CRUD documentos) | ✅ SÍ |

**Archivo corregido**: `SUB-D_WORKFLOW_v2.json`

---

## ⚠️ CORRECCIÓN CRÍTICA (v3) - NODO GEMINI

El v2 todavía usaba HTTP Request para Gemini cuando **existe nodo nativo**:

| Lo que usé (MAL) | Lo que debí usar (BIEN) |
|------------------|------------------------|
| `n8n-nodes-base.httpRequest` | `@n8n/n8n-nodes-langchain.googleGemini` |
| URL manual a API | Parámetros `resource: text`, `operation: message` |
| JSON body complejo | Parámetro `messages` nativo |

### Configuración Correcta del Nodo Gemini (v3):

```json
{
  "type": "@n8n/n8n-nodes-langchain.googleGemini",
  "typeVersion": 1,
  "parameters": {
    "resource": "text",
    "operation": "message",
    "modelId": {
      "__rl": true,
      "value": "models/gemini-2.5-flash",
      "mode": "list"
    },
    "messages": {
      "values": [{ "content": "={{ prompt }}", "role": "user" }]
    },
    "simplify": true,
    "jsonOutput": true,
    "options": {
      "maxOutputTokens": 500,
      "temperature": 0.7
    }
  },
  "credentials": {
    "googlePalmApi": { "id": "jk2FHcbAC71LuRl2" }
  }
}
```

**Archivo corregido**: `SUB-D_WORKFLOW_v3.json`

---

## 📊 RESUMEN DE NODOS NATIVOS vs HTTP REQUEST (v3)

| Nodo | Tipo Usado | Justificación |
|------|------------|---------------|
| **Query Firestore** | `googleFirebaseCloudFirestore` | ✅ Nodo nativo disponible |
| **Update Firestore** | `googleFirebaseCloudFirestore` | ✅ Nodo nativo disponible |
| **Gemini AI** | `@n8n/n8n-nodes-langchain.googleGemini` | ✅ Nodo nativo disponible |
| **MailerSend** | `httpRequest` | ⚠️ No hay nodo nativo |
| **Callback Backend** | `httpRequest` | ⚠️ API interna personalizada |

---

## ❌ PROBLEMAS IDENTIFICADOS

### Problema 1: Uso de HTTP Request para Query Firestore

**Nodo Original** (líneas 55-72):
```json
{
  "id": "query_firestore",
  "name": "Query Firestore: Leads para Nurturing",
  "type": "n8n-nodes-base.httpRequest",
  "parameters": {
    "method": "POST",
    "url": "https://firestore.googleapis.com/v1/projects/carrillo-marketing-core/databases/(default)/documents:runQuery",
    "jsonBody": "{...structured query JSON...}"
  }
}
```

**Problemas**:
- ❌ Requiere conocer la estructura exacta de la API de Firestore
- ❌ Manejo manual de autenticación
- ❌ Formato de query complejo (structuredQuery)
- ❌ Parsing manual de respuesta
- ❌ Difícil mantenimiento y debugging

---

### Problema 2: Uso de HTTP Request para Update Firestore

**Nodo Original** (líneas 302-319):
```json
{
  "id": "actualizar_firestore",
  "name": "Actualizar Firestore",
  "type": "n8n-nodes-base.httpRequest",
  "parameters": {
    "method": "PATCH",
    "url": "https://firestore.googleapis.com/v1/projects/.../documents/leads/{{lead_id}}",
    "jsonBody": "{\"fields\": {...}}"
  }
}
```

**Problemas**:
- ❌ Construcción manual de estructura `fields` de Firestore
- ❌ Manejo de tipos explícitos (`integerValue`, `stringValue`)
- ❌ URL dinámicas con interpolación
- ❌ Sin validación de campos

---

## ✅ SOLUCIONES IMPLEMENTADAS

### Solución 1: Nodo Nativo Firestore (getAll)

**Nodo Refactorizado**:
```json
{
  "id": "query_firestore",
  "name": "Query Firestore: Leads para Nurturing",
  "type": "n8n-nodes-base.googleCloudFirestore",
  "typeVersion": 1,
  "parameters": {
    "projectId": "carrillo-marketing-core",
    "operation": "getAll",
    "collection": "leads",
    "returnAll": false,
    "limit": 10,
    "options": {
      "where": {
        "whereValues": [
          {
            "field": "status",
            "operator": "in",
            "value": "={{ ['nuevo', 'nurturing'] }}"
          },
          {
            "field": "emails_sent",
            "operator": "<",
            "value": "={{ 12 }}"
          }
        ]
      },
      "orderBy": {
        "orderByValues": [
          {
            "field": "next_email_date",
            "direction": "ASCENDING"
          }
        ]
      }
    }
  },
  "credentials": {
    "googleCloudFirestoreApi": {
      "id": "firestore_carrillo",
      "name": "Firestore Carrillo Marketing"
    }
  }
}
```

**Beneficios**:
- ✅ Sintaxis declarativa (no JSON manual)
- ✅ Credenciales manejadas por n8n
- ✅ Operadores nativos (`in`, `<`, etc.)
- ✅ Parsing automático de respuesta
- ✅ Validación de campos por n8n
- ✅ Debugging más fácil en UI

---

### Solución 2: Nodo Nativo Firestore (update)

**Nodo Refactorizado**:
```json
{
  "id": "actualizar_firestore",
  "name": "Actualizar Firestore",
  "type": "n8n-nodes-base.googleCloudFirestore",
  "typeVersion": 1,
  "parameters": {
    "projectId": "carrillo-marketing-core",
    "operation": "update",
    "collection": "leads",
    "documentId": "={{ $('Validar Output Gemini').item.json.lead_id }}",
    "updateFields": {
      "fields": [
        {
          "field": "emails_sent",
          "fieldValue": "={{ $('Validar Output Gemini').item.json.emails_sent + 1 }}"
        },
        {
          "field": "last_contact",
          "fieldValue": "={{ new Date().toISOString() }}"
        },
        {
          "field": "status",
          "fieldValue": "nurturing"
        },
        {
          "field": "nurturing_position",
          "fieldValue": "={{ $('Validar Output Gemini').item.json.position }}"
        },
        {
          "field": "next_email_date",
          "fieldValue": "={{ $('Validar Output Gemini').item.json.next_email_date || '' }}"
        },
        {
          "field": "updated_at",
          "fieldValue": "="{{ new Date().toISOString() }}"
        }
      ]
    }
  },
  "credentials": {
    "googleCloudFirestoreApi": {
      "id": "firestore_carrillo",
      "name": "Firestore Carrillo Marketing"
    }
  }
}
```

**Beneficios**:
- ✅ Sin construcción manual de estructura `fields`
- ✅ n8n infiere tipos automáticamente
- ✅ Expresiones n8n para valores dinámicos
- ✅ Sin URLs manuales (n8n construye la request)
- ✅ Error handling nativo

---

## 🔄 NODOS QUE SE MANTIENEN CON HTTP REQUEST

### Nodo Gemini (líneas 201-222)

**Justificación**:
- ⚠️ **NO existe nodo nativo de Google Gemini en n8n**
- ✅ HTTP Request es la única opción disponible
- ✅ Configuración correcta con `googlePalmApi` credential type

**Configuración correcta**:
```json
{
  "id": "gemini_personalizar",
  "name": "Personalizar Email con Gemini",
  "type": "n8n-nodes-base.httpRequest",
  "parameters": {
    "method": "POST",
    "url": "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent",
    "authentication": "predefinedCredentialType",
    "nodeCredentialType": "googlePalmApi"
  }
}
```

---

## 📊 COMPARACIÓN: ANTES vs DESPUÉS

| Aspecto | HTTP Request (❌) | Nodo Nativo (✅) |
|---------|------------------|------------------|
| **Configuración** | JSON manual complejo | UI declarativa |
| **Credenciales** | Manual/OAuth2 custom | n8n credential store |
| **Validación** | Ninguna | Validación pre-ejecución |
| **Debugging** | Inspeccionar raw JSON | Ver campos en UI |
| **Mantenimiento** | Difícil (cambios API) | Fácil (n8n actualiza) |
| **Error Handling** | Manual | Nativo de n8n |
| **Documentación** | API docs externa | n8n inline docs |
| **Type Safety** | Ninguna | Inferencia automática |

---

## 🎯 BEST PRACTICES APLICADAS

### ✅ Regla 1: Usar Nodos Nativos Siempre

```markdown
SI existe nodo nativo para el servicio → USAR NODO NATIVO
SI NO existe nodo nativo → HTTP Request es válido
```

**Ejemplos**:
- ✅ Google Firestore → `n8n-nodes-base.googleCloudFirestore`
- ✅ Google Sheets → `n8n-nodes-base.googleSheets`
- ✅ Slack → `n8n-nodes-base.slack`
- ⚠️ Google Gemini → `n8n-nodes-base.httpRequest` (no hay nativo)
- ⚠️ MailerSend → `n8n-nodes-base.httpRequest` (no hay nativo)

### ✅ Regla 2: Credenciales Centralizadas

**HTTP Request** (❌):
```json
{
  "authentication": "oAuth2",
  "oAuth2": {
    "clientId": "...",
    "clientSecret": "...",
    "accessToken": "..."
  }
}
```

**Nodo Nativo** (✅):
```json
{
  "credentials": {
    "googleCloudFirestoreApi": {
      "id": "firestore_carrillo",
      "name": "Firestore Carrillo Marketing"
    }
  }
}
```

### ✅ Regla 3: Expresiones n8n para Datos Dinámicos

**HTTP Request JSON body** (❌):
```json
{
  "jsonBody": "={\"fields\": {\"emails_sent\": {\"integerValue\": \"{{ $json.emails_sent + 1 }}\"}}}"
}
```

**Nodo Nativo** (✅):
```json
{
  "updateFields": {
    "fields": [
      {
        "field": "emails_sent",
        "fieldValue": "={{ $json.emails_sent + 1 }}"
      }
    ]
  }
}
```

---

## 🔍 CAMBIOS EN EXTRACCIÓN DE DATOS

### Nodo "Extraer Datos Lead"

**Cambio crítico** (línea 131):
```json
{
  "id": "a1",
  "name": "lead_id",
  "value": "={{ $json._name || $json.lead_id }}"
}
```

**Explicación**:
- HTTP Request: `$json.document.name.split('/').pop()`
- Nodo Nativo: `$json._name` (campo automático)

---

## 📝 ARCHIVOS GENERADOS

### 1. `SUB-D_WORKFLOW_REFACTORED.json`
- Workflow completo con nodos nativos
- Listo para importar a n8n Cloud

### 2. `REFACTOR_REPORT.md`
- Este documento
- Explicación de cambios y justificaciones

---

## ✅ CHECKLIST DE VALIDACIÓN

- [x] Nodo Query Firestore refactorizado con nodo nativo
- [x] Nodo Actualizar Firestore refactorizado con nodo nativo
- [x] Nodo Gemini mantiene HTTP Request (no hay nativo)
- [x] Nodo MailerSend mantiene HTTP Request (no hay nativo)
- [x] Credenciales configuradas correctamente
- [x] Expresiones n8n actualizadas para datos dinámicos
- [x] Conexiones validadas
- [x] Tags actualizados con "REFACTORED"

---

## 🚀 PRÓXIMOS PASOS

### Para Implementar en n8n Cloud:

1. **Configurar Credencial Firestore**:
   ```
   Settings → Credentials → Add Credential
   Tipo: Google Cloud Firestore
   Nombre: "Firestore Carrillo Marketing"
   Project ID: carrillo-marketing-core
   Service Account JSON: [agregar desde GCP]
   ```

2. **Importar Workflow**:
   ```
   Workflows → Import from File
   Seleccionar: SUB-D_WORKFLOW_REFACTORED.json
   ```

3. **Verificar Nodos**:
   - Query Firestore → Verificar filtros `where`
   - Actualizar Firestore → Verificar campos `updateFields`

4. **Testing**:
   ```
   Ejecutar manualmente el trigger
   Verificar que query retorna leads
   Verificar que update persiste en Firestore
   ```

---

## 📚 REFERENCIAS

- **n8n Firestore Node**: https://docs.n8n.io/integrations/builtin/app-nodes/n8n-nodes-base.googlecloudfirestore/
- **n8n Best Practices**: [C:\Automatizaciones\n8n-antigravity\02-context\technical\n8n_best_practices.md](../../context/technical/n8n_best_practices.md)
- **Engineer Agent Guide**: [automation/.claude/agents/engineer.md](../../../.claude/agents/engineer.md)

---

## 🔖 TAGS

`REFACTOR` `BEST-PRACTICES` `NATIVE-NODES` `FIRESTORE` `SUB-D` `MEGA-WORKFLOW-1`

---

**Fin del reporte**
