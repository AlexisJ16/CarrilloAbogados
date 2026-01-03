# NODOS REQUERIDOS: MEGA WORKFLOW A

## 1. WORKFLOW ORQUESTADOR
| Nodo | Tipo | Propósito | Configuración Clave |
|------|------|-----------|---------------------|
| **Webhook** | `n8n-nodes-base.webhook` | Entrada principal | `POST`, path: `/lead-events` |
| **AI Agent** | `n8n-nodes-base.aiAgent` | Router Inteligente | Model: Gemini Pro, Prompt: Clasificación |
| **Google Gemini Chat Model** | `n8n-nodes-langchain.lmChatGoogleGemini` | Modelo para el Agente | Credential: Google Gemini API |
| **Switch** | `n8n-nodes-base.switch` | Enrutamiento | Rules basado en `event_type` de la IA |
| **Execute Workflow** | `n8n-nodes-base.executeWorkflow` | Llamar Sub-Workflows | `WaitForSubWorkflow: true` |
| **Respond to Webhook** | `n8n-nodes-base.respondToWebhook` | Respuesta final | Retornar JSON consolidado |

## 2. SUB-WORKFLOW A (V2 Híbrido)
| Nodo | Tipo | Propósito | Configuración Clave |
|------|------|-----------|---------------------|
| **Execute Workflow Trigger** | `n8n-nodes-base.executeWorkflowTrigger` | Trigger interno | Input Schema definido |
| **AI transform** | `n8n-nodes-base.aiAgent` | Normalización | Prompt: "Normalize to [C-Level, Manager...]" |
| **Code** | `n8n-nodes-base.code` | Scoring Determinista | `if (role == 'C-Level') score += 30` |
| **Firestore** | `n8n-nodes-base.googleCloudFirestore` | Base de datos | Collection: `leads` |
| **Gmail** | `n8n-nodes-base.gmail` | Notificaciones | Send Email |
