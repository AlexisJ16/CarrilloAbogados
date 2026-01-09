# N8N MCP TOOLS - GUÍA TÉCNICA COMPLETA
## Referencia Exhaustiva para Agentes de IA

**Versión**: 1.0  
**Compatible con**: n8n Cloud, n8n MCP Server v1.0  
**Última Actualización**: Diciembre 2024

---

## 📚 TABLA DE CONTENIDOS

1. [Introducción al MCP](#introducción-al-mcp)
2. [Arquitectura y Conexión](#arquitectura-y-conexión)
3. [Tools Disponibles](#tools-disponibles)
4. [Patrones de Uso](#patrones-de-uso)
5. [Casos de Uso Comunes](#casos-de-uso-comunes)
6. [Troubleshooting](#troubleshooting)

---

## 🔌 INTRODUCCIÓN AL MCP

### ¿Qué es MCP?

**Model Context Protocol (MCP)** es un protocolo estandarizado que permite a los modelos de IA (como Claude) interactuar con servicios externos de manera estructurada y segura.

### n8n MCP Server

El servidor MCP de n8n expone todas las capacidades de la API de n8n como tools que Claude puede llamar:

```
Claude (Cliente MCP) 
    ↓
MCP Protocol
    ↓
n8n MCP Server
    ↓
n8n Cloud API
    ↓
Tu Instancia n8n
```

### Configuración Actual

```json
{
  "name": "n8n",
  "url": "carrilloabgd.app.n8n.cloud"
}
```

---

## 🛠️ TOOLS DISPONIBLES

### Categoría 1: BÚSQUEDA Y EXPLORACIÓN

#### `n8n:search_nodes`

**Propósito**: Buscar nodos disponibles por keyword

**Parámetros**:
```typescript
{
  query: string,           // Términos de búsqueda
  limit?: number,          // Máximo resultados (default: 20)
  mode?: "OR"|"AND"|"FUZZY", // Modo búsqueda (default: "OR")
  includeExamples?: boolean // Incluir ejemplos (default: false)
}
```

**Ejemplos de Uso**:

```javascript
// Búsqueda básica
search_nodes("webhook")

// Búsqueda específica (todos los términos)
search_nodes("google sheets create", { mode: "AND" })

// Con ejemplos de configuración
search_nodes("slack", { includeExamples: true, limit: 5 })

// Búsqueda tolerante a errores
search_nodes("firestor", { mode: "FUZZY" })
```

**Cuándo Usar**:
- Al inicio de diseño para encontrar nodos apropiados
- Cuando no sabes el nombre exacto de un nodo
- Para descubrir integraciones disponibles

**Output Ejemplo**:
```json
{
  "results": [
    {
      "name": "Webhook",
      "displayName": "Webhook",
      "description": "Starts workflow when webhook receives HTTP request",
      "type": "n8n-nodes-base.webhook",
      "version": 2.1,
      "category": "Core Nodes"
    }
  ],
  "total": 1
}
```

---

#### `n8n:get_node`

**Propósito**: Obtener información detallada de un nodo específico

**Parámetros**:
```typescript
{
  nodeType: string,        // Tipo de nodo (ej: "n8n-nodes-base.webhook")
  detail?: "minimal"|"standard"|"full", // Nivel detalle (default: "standard")
  mode?: "info"|"docs"|"search_properties"|"versions",
  includeExamples?: boolean,
  includeTypeInfo?: boolean
}
```

**Ejemplos de Uso**:

```javascript
// Info estándar (recomendado)
get_node("n8n-nodes-base.webhook")

// Info completa con ejemplos
get_node("n8n-nodes-base.googleSheets", {
  detail: "full",
  includeExamples: true
})

// Buscar propiedad específica
get_node("n8n-nodes-base.slack", {
  mode: "search_properties",
  propertyQuery: "channel"
})

// Documentación legible
get_node("n8n-nodes-base.code", {
  mode: "docs"
})

// Versiones disponibles
get_node("n8n-nodes-base.httpRequest", {
  mode: "versions"
})
```

**Output Ejemplo (mode="info", detail="standard")**:
```json
{
  "nodeType": "n8n-nodes-base.webhook",
  "version": 2.1,
  "displayName": "Webhook",
  "description": "Starts workflow when webhook receives HTTP request",
  "properties": [
    {
      "displayName": "HTTP Method",
      "name": "httpMethod",
      "type": "options",
      "options": [
        { "name": "GET", "value": "GET" },
        { "name": "POST", "value": "POST" }
      ],
      "default": "GET"
    }
  ],
  "credentials": [
    {
      "name": "httpBasicAuth",
      "required": false
    }
  ]
}
```

---

#### `n8n:search_templates`

**Propósito**: Buscar workflows template existentes

**Parámetros**:
```typescript
{
  searchMode?: "keyword"|"by_nodes"|"by_task"|"by_metadata",
  query?: string,          // Para searchMode="keyword"
  nodeTypes?: string[],    // Para searchMode="by_nodes"
  task?: string,           // Para searchMode="by_task"
  limit?: number           // Máximo resultados (default: 20)
}
```

**Ejemplos de Uso**:

```javascript
// Buscar por keyword
search_templates({
  searchMode: "keyword",
  query: "lead scoring",
  limit: 10
})

// Buscar templates que usen nodos específicos
search_templates({
  searchMode: "by_nodes",
  nodeTypes: [
    "n8n-nodes-base.webhook",
    "n8n-nodes-base.googleSheets"
  ]
})

// Buscar por tipo de tarea
search_templates({
  searchMode: "by_task",
  task: "ai_automation"
})
```

**Tareas Disponibles**:
- `ai_automation`
- `data_sync`
- `webhook_processing`
- `email_automation`
- `slack_integration`
- `data_transformation`
- `file_processing`
- `scheduling`
- `api_integration`
- `database_operations`

---

#### `n8n:get_template`

**Propósito**: Obtener un template específico por ID

**Parámetros**:
```typescript
{
  templateId: number,
  mode?: "nodes_only"|"structure"|"full" // (default: "full")
}
```

**Ejemplos de Uso**:

```javascript
// Template completo (para deployment)
get_template(12345, { mode: "full" })

// Solo estructura (para análisis)
get_template(12345, { mode: "structure" })

// Solo lista de nodos (para quick review)
get_template(12345, { mode: "nodes_only" })
```

---

### Categoría 2: VALIDACIÓN

#### `n8n:validate_node`

**Propósito**: Validar configuración de un nodo ANTES de agregarlo al workflow

**Parámetros**:
```typescript
{
  nodeType: string,
  config: object,
  mode?: "full"|"minimal", // (default: "full")
  profile?: "minimal"|"runtime"|"ai-friendly"|"strict" // (default: "ai-friendly")
}
```

**Ejemplos de Uso**:

```javascript
// Validación completa (RECOMENDADO)
validate_node("n8n-nodes-base.googleSheets", {
  resource: "sheet",
  operation: "append",
  sheetId: "abc123",
  range: "A1:B10"
}, {
  mode: "full",
  profile: "ai-friendly"
})

// Validación mínima (solo campos requeridos)
validate_node("n8n-nodes-base.webhook", {
  httpMethod: "POST",
  path: "lead-intake"
}, {
  mode: "minimal"
})
```

**Output Ejemplo**:
```json
{
  "valid": false,
  "errors": [
    {
      "field": "sheetId",
      "message": "Sheet ID is required",
      "severity": "error"
    }
  ],
  "warnings": [
    {
      "field": "range",
      "message": "Range should be in A1 notation",
      "severity": "warning"
    }
  ],
  "suggestions": [
    {
      "field": "operation",
      "suggestion": "Consider using 'update' instead of 'append' for better performance",
      "reason": "Based on your data structure"
    }
  ]
}
```

---

#### `n8n:validate_workflow`

**Propósito**: Validar workflow completo (estructura, conexiones, expresiones)

**Parámetros**:
```typescript
{
  workflow: {
    nodes: Node[],
    connections: Connections
  },
  options?: {
    validateNodes?: boolean,        // (default: true)
    validateConnections?: boolean,  // (default: true)
    validateExpressions?: boolean,  // (default: true)
    profile?: "minimal"|"runtime"|"ai-friendly"|"strict"
  }
}
```

**Ejemplos de Uso**:

```javascript
// Validación completa (OBLIGATORIO antes de crear)
validate_workflow({
  nodes: [...],
  connections: {...}
}, {
  validateNodes: true,
  validateConnections: true,
  validateExpressions: true,
  profile: "ai-friendly"
})

// Solo validar estructura (quick check)
validate_workflow({
  nodes: [...],
  connections: {...}
}, {
  validateNodes: false,
  validateExpressions: false
})
```

**Output Ejemplo**:
```json
{
  "valid": true,
  "errors": [],
  "warnings": [
    {
      "nodeId": "node_123",
      "message": "Node has no error output configured",
      "suggestion": "Add error handling for production use"
    }
  ],
  "suggestions": [
    {
      "type": "optimization",
      "message": "Consider using webhook response node",
      "impact": "Improves user experience"
    }
  ],
  "statistics": {
    "totalNodes": 15,
    "totalConnections": 18,
    "expressionsUsed": 7,
    "credentialsRequired": 3
  }
}
```

---

### Categoría 3: CONSTRUCCIÓN Y MODIFICACIÓN

#### `n8n:n8n_create_workflow`

**Propósito**: Crear un nuevo workflow en n8n Cloud

**Parámetros**:
```typescript
{
  name: string,
  nodes: Node[],
  connections: Connections,
  settings?: WorkflowSettings
}
```

**IMPORTANTE**: SIEMPRE validar con `validate_workflow` ANTES de llamar este tool.

**Ejemplo Completo**:

```javascript
// PASO 1: Preparar estructura
const workflow = {
  name: "Lead Intake Webhook",
  nodes: [
    {
      id: "webhook_1",
      name: "Webhook Trigger",
      type: "n8n-nodes-base.webhook",
      typeVersion: 2.1,
      position: [250, 300],
      parameters: {
        httpMethod: "POST",
        path: "lead-intake",
        responseMode: "responseNode"
      }
    },
    {
      id: "function_1",
      name: "Process Lead Data",
      type: "n8n-nodes-base.function",
      typeVersion: 1,
      position: [450, 300],
      parameters: {
        functionCode: `
          const lead = $input.all()[0].json;
          return [{
            json: {
              email: lead.email,
              score: calculateScore(lead),
              timestamp: new Date().toISOString()
            }
          }];
        `
      }
    }
  ],
  connections: {
    "webhook_1": {
      "main": [[{ "node": "function_1", "type": "main", "index": 0 }]]
    }
  },
  settings: {
    executionOrder: "v1",
    saveExecutionProgress: true,
    timezone: "America/Bogota"
  }
};

// PASO 2: Validar
const validation = await validate_workflow(workflow);
if (validation.errors.length > 0) {
  console.error("Workflow inválido:", validation.errors);
  return;
}

// PASO 3: Crear
const createdWorkflow = await n8n_create_workflow(
  workflow.name,
  workflow.nodes,
  workflow.connections,
  workflow.settings
);

console.log("Workflow creado:", createdWorkflow.id);
```

---

#### `n8n:n8n_update_partial_workflow`

**Propósito**: Actualizar workflow existente incrementalmente

**Parámetros**:
```typescript
{
  id: string,
  operations: Operation[],
  continueOnError?: boolean,  // (default: false)
  validateOnly?: boolean      // (default: false)
}
```

**Tipos de Operaciones**:

1. **addNode**: Agregar nuevo nodo
2. **removeNode**: Eliminar nodo existente
3. **updateNode**: Modificar nodo existente
4. **moveNode**: Cambiar posición de nodo
5. **enableNode**: Activar nodo deshabilitado
6. **disableNode**: Desactivar nodo
7. **addConnection**: Agregar conexión
8. **removeConnection**: Eliminar conexión
9. **updateSettings**: Modificar settings del workflow
10. **updateName**: Cambiar nombre del workflow
11. **addTag**: Agregar etiqueta
12. **removeTag**: Eliminar etiqueta

**Ejemplos de Uso**:

```javascript
// Agregar nodo nuevo
n8n_update_partial_workflow("workflow_id_123", [
  {
    type: "addNode",
    node: {
      id: "new_node_1",
      name: "Send Email",
      type: "n8n-nodes-base.gmail",
      typeVersion: 2,
      position: [650, 300],
      parameters: {
        resource: "message",
        operation: "send"
      }
    }
  },
  {
    type: "addConnection",
    fromNode: "function_1",
    toNode: "new_node_1",
    outputIndex: 0,
    inputIndex: 0
  }
])

// Actualizar configuración de nodo
n8n_update_partial_workflow("workflow_id_123", [
  {
    type: "updateNode",
    nodeId: "function_1",
    updates: {
      parameters: {
        functionCode: "// Código actualizado..."
      }
    }
  }
])

// Preview mode (no aplica cambios)
n8n_update_partial_workflow("workflow_id_123", operations, {
  validateOnly: true
})
```

---

### Categoría 4: OPTIMIZACIÓN Y FIXES

#### `n8n:n8n_autofix_workflow`

**Propósito**: Corregir automáticamente errores comunes

**Parámetros**:
```typescript
{
  id: string,
  applyFixes?: boolean,  // (default: false - preview)
  fixTypes?: string[],   // Tipos específicos de fixes
  confidenceThreshold?: "high"|"medium"|"low", // (default: "medium")
  maxFixes?: number      // (default: 50)
}
```

**Tipos de Fixes Disponibles**:
- `expression-format`: Corrige formato de expresiones n8n
- `typeversion-correction`: Actualiza typeVersions incorrectas
- `error-output-config`: Configura outputs de error
- `webhook-missing-path`: Agrega paths faltantes en webhooks
- `typeversion-upgrade`: Upgradea a versiones más recientes

**Ejemplos de Uso**:

```javascript
// Preview mode (ver qué se corregiría)
const preview = await n8n_autofix_workflow("workflow_id_123", {
  applyFixes: false
})

console.log("Fixes sugeridos:", preview.suggestedFixes);

// Aplicar todos los fixes
const result = await n8n_autofix_workflow("workflow_id_123", {
  applyFixes: true,
  confidenceThreshold: "high"
})

console.log("Fixes aplicados:", result.appliedFixes);

// Solo fixes específicos
const result = await n8n_autofix_workflow("workflow_id_123", {
  applyFixes: true,
  fixTypes: ["expression-format", "error-output-config"]
})
```

**Output Ejemplo**:
```json
{
  "workflowId": "workflow_id_123",
  "fixesApplied": 5,
  "details": [
    {
      "type": "expression-format",
      "nodeId": "function_1",
      "before": "{{ $json.email }}",
      "after": "={{ $json.email }}",
      "confidence": "high"
    },
    {
      "type": "error-output-config",
      "nodeId": "http_request_1",
      "added": "onError: 'continueErrorOutput'",
      "confidence": "medium"
    }
  ]
}
```

---

### Categoría 5: TESTING Y DESPLIEGUE

#### `n8n:n8n_test_workflow`

**Propósito**: Ejecutar workflow con datos de prueba

**Parámetros**:
```typescript
{
  workflowId: string,
  triggerType?: "webhook"|"form"|"chat",
  data?: object,           // Payload de prueba
  httpMethod?: "GET"|"POST"|"PUT"|"DELETE",
  headers?: object,
  waitForResponse?: boolean, // (default: true)
  timeout?: number          // (default: 120000ms)
}
```

**Ejemplos de Uso**:

```javascript
// Test webhook POST
const result = await n8n_test_workflow({
  workflowId: "workflow_id_123",
  triggerType: "webhook",
  httpMethod: "POST",
  data: {
    email: "test@example.com",
    nombre: "Test Lead",
    empresa: "Test Corp"
  },
  headers: {
    "Content-Type": "application/json"
  }
})

// Test con timeout customizado
const result = await n8n_test_workflow({
  workflowId: "workflow_id_123",
  data: testData,
  timeout: 60000, // 1 minuto
  waitForResponse: true
})
```

**Output Ejemplo**:
```json
{
  "executionId": "exec_123",
  "status": "success",
  "duration": 2.5,
  "output": {
    "finalData": {
      "email": "test@example.com",
      "score": 85,
      "timestamp": "2024-12-16T10:30:00Z"
    }
  },
  "nodeResults": {
    "webhook_1": "success",
    "function_1": "success",
    "gmail_1": "success"
  }
}
```

---

#### `n8n:n8n_deploy_template`

**Propósito**: Desplegar template desde n8n.io directamente

**Parámetros**:
```typescript
{
  templateId: number,
  name?: string,                // Nombre customizado
  autoFix?: boolean,            // (default: true)
  autoUpgradeVersions?: boolean, // (default: true)
  stripCredentials?: boolean    // (default: true)
}
```

**Ejemplos de Uso**:

```javascript
// Deploy template básico
const result = await n8n_deploy_template(12345, {
  name: "Mi Workflow Customizado",
  autoFix: true
})

// Deploy sin auto-fixes (avanzado)
const result = await n8n_deploy_template(12345, {
  autoFix: false,
  autoUpgradeVersions: false
})

console.log("Workflow ID:", result.workflowId);
console.log("Credenciales requeridas:", result.credentialsNeeded);
console.log("Fixes aplicados:", result.fixesApplied);
```

---

### Categoría 6: GESTIÓN Y MANTENIMIENTO

#### `n8n:n8n_get_workflow`

**Propósito**: Obtener workflow existente

**Parámetros**:
```typescript
{
  id: string,
  mode?: "full"|"details"|"structure"|"minimal" // (default: "full")
}
```

**Ejemplos de Uso**:

```javascript
// Workflow completo
const workflow = await n8n_get_workflow("workflow_id_123", { 
  mode: "full" 
})

// Solo metadata
const workflow = await n8n_get_workflow("workflow_id_123", { 
  mode: "minimal" 
})
```

---

#### `n8n:n8n_list_workflows`

**Propósito**: Listar workflows disponibles

**Parámetros**:
```typescript
{
  limit?: number,      // (default: 100)
  cursor?: string,     // Para paginación
  active?: boolean,    // Filtrar por estado
  tags?: string[]      // Filtrar por tags
}
```

**Ejemplos de Uso**:

```javascript
// Listar todos los workflows activos
const workflows = await n8n_list_workflows({
  active: true,
  limit: 50
})

// Filtrar por tags
const workflows = await n8n_list_workflows({
  tags: ["marketing", "automation"]
})
```

---

#### `n8n:n8n_workflow_versions`

**Propósito**: Gestionar versiones de workflows

**Parámetros**:
```typescript
{
  mode: "list"|"get"|"rollback"|"delete"|"prune"|"truncate",
  workflowId?: string,
  versionId?: number,
  maxVersions?: number,  // Para mode="prune"
  confirmTruncate?: boolean // Requerido para mode="truncate"
}
```

**Ejemplos de Uso**:

```javascript
// Listar versiones
const versions = await n8n_workflow_versions({
  mode: "list",
  workflowId: "workflow_id_123"
})

// Rollback a versión anterior
const result = await n8n_workflow_versions({
  mode: "rollback",
  workflowId: "workflow_id_123",
  versionId: 5
})

// Limpiar versiones antiguas (mantener últimas 10)
const result = await n8n_workflow_versions({
  mode: "prune",
  workflowId: "workflow_id_123",
  maxVersions: 10
})
```

---

## 🎯 PATRONES DE USO RECOMENDADOS

### Patrón 1: Descubrimiento → Validación → Construcción

```javascript
// 1. DESCUBRIR nodos necesarios
const nodes = await search_nodes("google sheets webhook", { 
  mode: "AND",
  includeExamples: true 
})

// 2. OBTENER detalles de cada nodo
for (const node of nodes.results) {
  const details = await get_node(node.type, { 
    detail: "full",
    includeExamples: true 
  })
  console.log(`Nodo ${node.displayName}:`, details)
}

// 3. VALIDAR configuración antes de agregar
const validation = await validate_node("n8n-nodes-base.googleSheets", {
  resource: "sheet",
  operation: "append",
  // ... más config
})

if (validation.errors.length > 0) {
  console.error("Nodo inválido:", validation.errors)
  return
}

// 4. CONSTRUIR workflow
const workflow = {
  name: "My Workflow",
  nodes: [...],
  connections: {...}
}

// 5. VALIDAR workflow completo
const workflowValidation = await validate_workflow(workflow)

if (workflowValidation.valid) {
  // 6. CREAR
  const result = await n8n_create_workflow(
    workflow.name,
    workflow.nodes,
    workflow.connections
  )
  console.log("Workflow creado:", result.id)
}
```

---

### Patrón 2: Template → Customize → Deploy

```javascript
// 1. BUSCAR template similar
const templates = await search_templates({
  searchMode: "keyword",
  query: "lead scoring automation"
})

// 2. OBTENER template completo
const template = await get_template(templates.results[0].id, {
  mode: "full"
})

// 3. DESPLEGAR con auto-fixes
const deployed = await n8n_deploy_template(template.id, {
  name: "Lead Scoring - Carrillo Abogados",
  autoFix: true,
  autoUpgradeVersions: true
})

// 4. CUSTOMIZAR usando partial update
await n8n_update_partial_workflow(deployed.workflowId, [
  {
    type: "updateNode",
    nodeId: "webhook_node",
    updates: {
      parameters: {
        path: "carrillo-lead-intake"
      }
    }
  }
])

// 5. TEST
const testResult = await n8n_test_workflow({
  workflowId: deployed.workflowId,
  data: { /* test data */ }
})

console.log("Test result:", testResult.status)
```

---

### Patrón 3: Iterate → Validate → Fix → Optimize

```javascript
// 1. OBTENER workflow actual
const workflow = await n8n_get_workflow("workflow_id_123", { 
  mode: "full" 
})

// 2. VALIDAR
const validation = await n8n_validate_workflow("workflow_id_123")

if (validation.errors.length > 0) {
  // 3. AUTO-FIX primero (preview)
  const fixPreview = await n8n_autofix_workflow("workflow_id_123", {
    applyFixes: false
  })
  
  console.log("Fixes sugeridos:", fixPreview.suggestedFixes)
  
  // 4. APLICAR fixes
  const fixed = await n8n_autofix_workflow("workflow_id_123", {
    applyFixes: true,
    confidenceThreshold: "high"
  })
  
  // 5. RE-VALIDAR
  const revalidation = await n8n_validate_workflow("workflow_id_123")
  
  if (revalidation.valid) {
    console.log("✅ Workflow corregido y validado")
  }
}

// 6. TEST final
await n8n_test_workflow({
  workflowId: "workflow_id_123",
  data: testData
})
```

---

## 🔍 CASOS DE USO COMUNES

### Caso 1: Crear Webhook para Lead Intake

```javascript
// 1. Buscar nodo webhook
const webhookNode = await get_node("n8n-nodes-base.webhook", {
  detail: "full",
  includeExamples: true
})

// 2. Crear workflow
const workflow = {
  name: "Lead Intake Webhook",
  nodes: [
    {
      id: "webhook_trigger",
      name: "Lead Webhook",
      type: "n8n-nodes-base.webhook",
      typeVersion: 2.1,
      position: [250, 300],
      parameters: {
        httpMethod: "POST",
        path: "carrillo-lead-intake",
        responseMode: "responseNode",
        options: {}
      }
    },
    {
      id: "webhook_response",
      name: "Webhook Response",
      type: "n8n-nodes-base.respondToWebhook",
      typeVersion: 1,
      position: [850, 300],
      parameters: {
        respondWith: "json",
        responseBody: "={{ { success: true, message: 'Lead received' } }}"
      }
    }
  ],
  connections: {
    "webhook_trigger": {
      "main": [[{ "node": "webhook_response", "type": "main", "index": 0 }]]
    }
  }
}

// 3. Validar y crear
const validation = await validate_workflow(workflow)
if (validation.valid) {
  const created = await n8n_create_workflow(
    workflow.name,
    workflow.nodes,
    workflow.connections
  )
  console.log("Webhook URL:", created.webhookUrl)
}
```

---

### Caso 2: Integrar con Firestore

```javascript
// 1. Buscar nodo Firestore
const firestoreNode = await get_node("n8n-nodes-base.googleCloudFirestore", {
  detail: "full",
  mode: "docs"
})

// 2. Validar configuración
const validation = await validate_node("n8n-nodes-base.googleCloudFirestore", {
  operation: "create",
  collection: "leads",
  documentId: "={{ $json.email }}",
  data: "={{ $json }}"
})

// 3. Si válido, agregar al workflow
if (validation.valid) {
  await n8n_update_partial_workflow("workflow_id_123", [
    {
      type: "addNode",
      node: {
        id: "firestore_save",
        name: "Save to Firestore",
        type: "n8n-nodes-base.googleCloudFirestore",
        typeVersion: 1,
        position: [650, 300],
        parameters: {
          operation: "create",
          collection: "leads",
          documentId: "={{ $json.email }}",
          data: "={{ $json }}"
        },
        credentials: {
          googleCloudFirestoreApi: {
            id: "your_credential_id",
            name: "Firestore Carrillo"
          }
        }
      }
    }
  ])
}
```

---

### Caso 3: AI Lead Scoring con Google Gemini

```javascript
// 1. Buscar nodos necesarios
const geminiNode = await search_nodes("google gemini", { 
  mode: "FUZZY" 
})

// 2. Crear nodo Code para scoring
const scoringNode = {
  id: "ai_scoring",
  name: "AI Lead Scoring",
  type: "n8n-nodes-base.code",
  typeVersion: 2,
  position: [450, 300],
  parameters: {
    language: "javaScript",
    jsCode: `
      const { GoogleGenerativeAI } = require('@google/generative-ai');
      
      const lead = $input.all()[0].json;
      const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY);
      const model = genAI.getGenerativeModel({ model: 'gemini-pro' });
      
      const prompt = \`
        Analiza este lead y asigna un score de 0-100:
        Email: \${lead.email}
        Nombre: \${lead.nombre}
        Empresa: \${lead.empresa}
        Interés: \${lead.interes}
        
        Responde SOLO con el número del score.
      \`;
      
      const result = await model.generateContent(prompt);
      const score = parseInt(result.response.text());
      
      return [{
        json: {
          ...lead,
          ai_score: score,
          scored_at: new Date().toISOString()
        }
      }];
    `
  }
}

// 3. Validar y agregar
const validation = await validate_node("n8n-nodes-base.code", scoringNode.parameters)
if (validation.valid) {
  await n8n_update_partial_workflow("workflow_id_123", [
    { type: "addNode", node: scoringNode }
  ])
}
```

---

## ⚠️ TROUBLESHOOTING

### Error: "Node validation failed"

**Causa**: Configuración de nodo incorrecta

**Solución**:
```javascript
// 1. Obtener nodo con documentación
const nodeInfo = await get_node(nodeType, { 
  mode: "docs" 
})

// 2. Ver propiedades requeridas
const nodeProps = await get_node(nodeType, { 
  mode: "search_properties",
  propertyQuery: "required"
})

// 3. Validar con profile flexible
const validation = await validate_node(nodeType, config, {
  profile: "ai-friendly"
})
```

---

### Error: "Workflow execution failed"

**Causa**: Error en runtime

**Solución**:
```javascript
// 1. Obtener execution log
const executions = await n8n_executions({
  action: "list",
  workflowId: "workflow_id_123",
  status: "error"
})

// 2. Ver detalles del error
const execution = await n8n_executions({
  action: "get",
  id: executions.results[0].id,
  mode: "full"
})

console.log("Error:", execution.data.resultData.error)
```

---

### Error: "Expression syntax error"

**Causa**: Expresiones n8n mal formateadas

**Solución**:
```javascript
// Usar autofix para corregir expresiones
const fixed = await n8n_autofix_workflow("workflow_id_123", {
  applyFixes: true,
  fixTypes: ["expression-format"]
})

console.log("Expresiones corregidas:", fixed.details)
```

---

## 📊 MEJORES PRÁCTICAS

### 1. Siempre Validar Antes de Crear

```javascript
// ❌ MAL
await n8n_create_workflow(name, nodes, connections)

// ✅ BIEN
const validation = await validate_workflow({ nodes, connections })
if (validation.valid) {
  await n8n_create_workflow(name, nodes, connections)
}
```

### 2. Usar Preview Mode para Fixes

```javascript
// ❌ MAL
await n8n_autofix_workflow(id, { applyFixes: true })

// ✅ BIEN
const preview = await n8n_autofix_workflow(id, { applyFixes: false })
console.log("Fixes sugeridos:", preview)
// Revisar y luego aplicar
await n8n_autofix_workflow(id, { applyFixes: true })
```

### 3. Incluir Examples al Buscar Nodos

```javascript
// ❌ MAL
const nodes = await search_nodes("slack")

// ✅ BIEN
const nodes = await search_nodes("slack", { 
  includeExamples: true 
})
// Aprende de configuraciones reales
```

### 4. Usar Versioning para Safety

```javascript
// Antes de cambios importantes
await n8n_workflow_versions({
  mode: "list",
  workflowId: id
})

// Hacer cambios...

// Si algo sale mal, rollback
await n8n_workflow_versions({
  mode: "rollback",
  workflowId: id,
  versionId: lastGoodVersion
})
```

---

## 📚 RECURSOS ADICIONALES

- [n8n Official Docs](https://docs.n8n.io/)
- [n8n Node Reference](https://docs.n8n.io/integrations/builtin/)
- [n8n Community Templates](https://n8n.io/workflows/)
- [MCP Protocol Spec](https://modelcontextprotocol.io/)

---

**VERSIÓN**: 1.0  
**ÚLTIMA ACTUALIZACIÓN**: Diciembre 16, 2024  
**MANTENIDO POR**: Carrillo Abogados Tech Team
