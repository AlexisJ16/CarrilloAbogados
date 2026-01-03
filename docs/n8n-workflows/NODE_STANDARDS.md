# ESTÁNDARES DE NODOS N8N CLOUD (v1.120+)

> **IMPORTANTE**: Para evitar errores de "Node not installed" o fallos al cargar workflows, utiliza SIEMPRE estos tipos de nodos para funciones de IA en n8n Cloud.

## 1. Google Gemini / PaLM
**Estado**: CRÍTICO ⚠️

- **❌ NO USAR**: `n8n-nodes-base.googleGemini` (Obsoleto/Legacy)
- **❌ NO USAR**: `n8n-nodes-base.googlePalm` (Legacy)
- **✅ USAR**: `@n8n/n8n-nodes-langchain.googleGemini` (Nativo Cloud)

**Configuración Correcta (JSON)**:
```json
{
  "type": "@n8n/n8n-nodes-langchain.googleGemini",
  "typeVersion": 1,
  "parameters": {
    "modelId": { "mode": "list", "value": "gemini-1.5-flash" },
    "messages": {
      "values": [
        {
          "role": "user",
          "content": "={{ 'Tu prompt aquí' }}"
        }
      ]
    }
  }
}
```

## 2. Ejecución de Sub-workflows
**Estado**: ESTABLE ✅

- **✅ USAR**: `n8n-nodes-base.executeWorkflow`
- **⚠️ REQUISITO**: Debe tener la propiedad `workflowId` explícita si es estático, o dinámica mediante expresión.

## 3. Webhooks
**Estado**: ESTABLE ✅

- **✅ USAR**: `n8n-nodes-base.webhook`
- **⚠️ REQUISITO**: Configurar `responseMode: "responseNode"` si esperas devolver datos al final del flujo.

---
**Última actualización**: 19 Dic 2025 - Post-incidente "Node not installed"
