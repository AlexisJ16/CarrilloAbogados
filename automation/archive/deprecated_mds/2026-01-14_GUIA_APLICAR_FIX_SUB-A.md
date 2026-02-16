# 🔧 GUÍA RÁPIDA: Aplicar Fix al Nodo "Mapear Input"

**Workflow**: SUB-A: Lead Intake (v5 - AI POWERED - NATIVE)  
**Nodo**: "0. Mapear Input del Orquestador1"  
**Tiempo estimado**: 3 minutos

---

## 📋 PASOS PARA APLICAR EL FIX

### 1. Abrir el Workflow en n8n Cloud

1. Ir a: https://carrilloabgd.app.n8n.cloud
2. Login con: marketing@carrilloabgd.com
3. Buscar workflow: **"SUB-A: Lead Intake (v5 - AI POWERED - NATIVE)"**
4. Click en el workflow para abrirlo en el editor

### 2. Localizar el Nodo Afectado

1. Buscar el nodo llamado: **"0. Mapear Input del Orquestador1"**
2. Este es el PRIMER nodo del workflow (conectado al trigger)
3. Hacer doble click para abrir sus parámetros

### 3. Reemplazar el Código JavaScript

1. En el editor del nodo, verás un campo "JavaScript Code"
2. **ELIMINAR** todo el código actual
3. **COPIAR** el código del archivo: `automation/workflows/MW1_LEAD_LIFECYCLE/02-spokes/sub-a-lead-intake/FIXED_MAPEAR_INPUT.js`
4. **PEGAR** en el campo del nodo

### 4. Guardar Cambios

1. Click en **"Execute Node"** (para validar sintaxis)
2. Si no hay errores, click en **"Save"** (botón arriba a la derecha)
3. El workflow guardará automáticamente

---

## ✅ VERIFICACIÓN

### Test Manual Rápido

1. En n8n, click en **"Test Workflow"** (botón play arriba)
2. En otro tab, enviar test con curl:

```bash
curl -X POST https://carrilloabgd.app.n8n.cloud/webhook/lead-events-v3 \
  -H "Content-Type: application/json" \
  -d '{
    "event_type": "new_lead",
    "payload": {
      "nombre": "Test Lead Fix",
      "email": "test-fix@example.com",
      "empresa": "Test Company",
      "mensaje": "Test después del fix"
    }
  }'
```

3. Volver a n8n y revisar la ejecución
4. Abrir el nodo "0. Mapear Input del Orquestador1"
5. Verificar que el **OUTPUT** ahora tenga valores:

```json
{
  "nombre": "Test Lead Fix",
  "email": "test-fix@example.com",
  "empresa": "Test Company",
  "mensaje": "Test después del fix"
}
```

### Validar Logs de Consola

En el output del nodo, buscar en los logs:
- ✅ **"Parseado desde campo query (JSON string)"** → Correcto
- ✅ **"Datos extraídos:"** con valores completos → Correcto

Si aparece:
- ❌ **"Fallback: usando raw data"** → Significa que falló el parseo

---

## 🔍 QUÉ CAMBIÓ EXACTAMENTE

### Antes (❌ No funcionaba)

```javascript
const raw = $input.first().json;
let data = {};

// NO BUSCABA EN 'query'
if (raw.payload && typeof raw.payload === 'object') {
  data = raw.payload;
}
// ... otros casos ...
```

### Después (✅ Funciona)

```javascript
const raw = $input.first().json;
let data = {};

// NUEVO: Caso prioritario para 'query'
if (raw.query && typeof raw.query === 'string') {
  try {
    data = JSON.parse(raw.query);
    console.log('✅ Parseado desde campo query (JSON string)');
  } catch(e) {
    console.log('❌ Error parseando query:', e.message);
    data = raw;
  }
}
// ... otros casos (payload, input, body, etc.) ...
```

---

## 🚨 IMPORTANTE

### Por qué los datos llegan en `query`

Cuando el **Orquestador v3.0 (AI Agent)** invoca el workflow SUB-A usando el Tool `@n8n/n8n-nodes-langchain.toolWorkflow` con configuración:

```json
{
  "workflowInputs": {
    "mappingMode": "autoMapInputData"
  }
}
```

n8n automáticamente serializa TODO el contexto del agente en un campo llamado **`query`** como JSON string.

Este es el comportamiento esperado y documentado de n8n cuando usas AI Agent Tools.

---

## 📞 SOPORTE

Si después de aplicar el fix sigues viendo campos vacíos:

1. Verificar que copiaste TODO el código del archivo `FIXED_MAPEAR_INPUT.js`
2. Revisar los logs de consola del nodo (botón "View" en el output)
3. Compartir screenshot de:
   - El INPUT del nodo (pestaña "JSON")
   - El OUTPUT del nodo (pestaña "JSON")
   - Los logs de consola

---

*Guía creada: 2026-01-14*  
*Autor: Claude AI (GitHub Copilot)*
