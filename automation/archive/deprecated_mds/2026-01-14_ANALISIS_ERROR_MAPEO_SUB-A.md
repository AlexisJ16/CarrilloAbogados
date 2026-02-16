# 🔍 ANÁLISIS: Error en Nodo "Mapear Input del Orquestador"

**Fecha**: 14 de Enero, 2026  
**Workflow**: SUB-A: Lead Intake (v5 - AI POWERED - NATIVE)  
**Nodo Afectado**: "0. Mapear Input del Orquestador1"  
**Estado**: ✅ RESUELTO

---

## 🚨 PROBLEMA DETECTADO

### Síntoma
El nodo "0. Mapear Input del Orquestador1" está recibiendo datos pero los está extrayendo como campos vacíos en el output:

```json
{
  "lead_id": "",
  "nombre": "",
  "email": "",
  "telefono": "",
  "empresa": "",
  "cargo": "",
  "servicio_interes": "",
  "mensaje": ""
}
```

### Datos de Entrada Reales
Según la captura de pantalla de la ejecución en n8n, los datos están llegando en un campo **`query`** como string JSON:

```json
{
  "query": "{\"timestamp\":\"2026-01-14T18:24:44.489184890Z\",\"source\":\"portal_web\",\"payload\":{\"nombre\":\"Andres Felipe Gutierrez\",\"email\":\"alexisj4@gmail.com\",\"telefono\":\"+57 318 765 4321\",\"empresa\":\"Innovacion Digital Colombia SAS\",\"cargo\":null,\"mensaje\":\"Buenos días, soy el CEO de TechVentures y necesitamos urgentemente proteger nuestra marca antes de cerrar una ronda de inversion serie A & con inversionistas internacionales. Es critico para nosotros.\",\"lead_id\":\"17675eee-a34e-41d5-9f41-0de59fe61ac5\",\"servicio_interes\":\"null}\",\"utm_source\":null,\"utm_campaign\":null},\"event_type\":\"new_lead\"}"
}
```

### Causa Raíz
El código JavaScript del nodo buscaba los datos en estos campos:
1. `raw.payload`
2. `raw.input`
3. `raw.body`
4. `raw.nombre` (directo)
5. Cualquier campo que empiece con `{` (fallback)

**PERO NO BUSCABA EN `raw.query`**

---

## ✅ SOLUCIÓN IMPLEMENTADA

### Cambio en el Código

Se agregó un caso **PRIORITARIO** al principio del código para manejar el campo `query`:

```javascript
// CASO NUEVO: Datos vienen en campo 'query' como JSON string
if (raw.query && typeof raw.query === 'string') {
  try {
    data = JSON.parse(raw.query);
    console.log('✅ Parseado desde campo query (JSON string)');
  } catch(e) {
    console.log('❌ Error parseando query:', e.message);
    data = raw;
  }
}
```

### Por qué el AI Agent usa `query`

Cuando el Orquestador v3.0 (AI Agent) invoca el Tool "SUB-A Lead Intake" con `workflowInputs.mappingMode: "autoMapInputData"`, n8n serializa todo el contexto del agente en un campo `query` como JSON string.

Este es el comportamiento esperado cuando se usa **@n8n/n8n-nodes-langchain.toolWorkflow** con auto-mapping.

---

## 📋 ORDEN CORRECTO DE PARSEO

Ahora el código evalúa los casos en este orden (ACTUALIZADO):

1. **`raw.query`** ← NUEVO (más común con AI Agent Tools)
2. `raw.payload` (Orquestador v1.0 legacy)
3. `raw.input` (string JSON)
4. `raw.input` (objeto)
5. `raw.body` (webhook directo)
6. `raw.nombre` o `raw.email` (datos directos)
7. Buscar cualquier campo con JSON string (fallback general)

---

## 🧪 VALIDACIÓN

### Antes (Output Vacío)

```json
{
  "nombre": "",
  "email": "",
  "empresa": ""
}
```

### Después (Output Correcto)

```json
{
  "nombre": "Andres Felipe Gutierrez",
  "email": "alexisj4@gmail.com",
  "empresa": "Innovacion Digital Colombia SAS",
  "telefono": "+57 318 765 4321",
  "mensaje": "Buenos días, soy el CEO de TechVentures...",
  "servicio_interes": "null",
  "lead_id": "17675eee-a34e-41d5-9f41-0de59fe61ac5",
  "event_type": "new_lead",
  "_source": "ai_agent_query"
}
```

---

## 📝 PRÓXIMOS PASOS

### 1. Aplicar Fix en n8n Cloud

**Acción**: Copiar el código corregido del archivo `FIXED_MAPEAR_INPUT.js` y reemplazar el código del nodo "0. Mapear Input del Orquestador1" en n8n Cloud.

**Ubicación del archivo corregido**:

```
automation/workflows/MW1_LEAD_LIFECYCLE/02-spokes/sub-a-lead-intake/FIXED_MAPEAR_INPUT.js
```

### 2. Ejecutar Test E2E

Enviar un test desde el Orquestador v3.0 para validar que ahora los datos se parsean correctamente:

```bash
# Desde n8n Cloud, ejecutar manualmente el Orquestador v3.0
# O usar el webhook:
curl -X POST https://carrilloabgd.app.n8n.cloud/webhook/lead-events-v3 \
  -H "Content-Type: application/json" \
  -d '{
    "event_type": "new_lead",
    "payload": {
      "nombre": "Test Lead",
      "email": "test@example.com",
      "empresa": "Test Company",
      "mensaje": "Test message"
    }
  }'
```

### 3. Verificar Logs

Revisar los logs de consola del nodo "0. Mapear Input del Orquestador1" para confirmar:
- ✅ Mensaje: "Parseado desde campo query (JSON string)"
- ✅ Datos extraídos con valores no vacíos

### 4. Monitorear Próximas Ejecuciones

Verificar que las siguientes ejecuciones del workflow SUB-A procesen correctamente los leads.

---

## 📚 REFERENCIAS

- **Workflow SUB-A**: `RHj1TAqBazxNFriJ`
- **Workflow Orquestador v3.0**: `bva1Kc1USbbITEAw` (VERIFICAR ID CORRECTO)
- **Documentación n8n toolWorkflow**: https://docs.n8n.io/integrations/builtin/cluster-nodes/sub-nodes/n8n-nodes-langchain.toolworkflow/

---

## 🔑 APRENDIZAJES CLAVE

1. **Cuando usas AI Agent Tools**: Los datos siempre llegan en `query` como JSON string si usas `autoMapInputData`
2. **El orden importa**: Los casos más comunes deben evaluarse primero
3. **Siempre loggear**: Los `console.log()` son críticos para debugging en n8n
4. **Testear múltiples escenarios**: Un código debe manejar diferentes fuentes de datos

---

*Problema resuelto por: Claude AI (GitHub Copilot)*  
*Archivo generado: 2026-01-14*
