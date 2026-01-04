# 🏗️ GUÍA TÉCNICA: MEGA WORKFLOW A (ORQUESTADOR)
## Arquitectura Hub & Spoke para Carrillo Abogados

**Proyecto:** Sistema de Automatización de Marketing y Gestión de Leads  
**Fecha:** 10 de diciembre de 2025  
**Autor:** Asistencia técnica especializada n8n + Claude  
**Destinatario:** Juan Sebastián Mener - Director de Estrategia y Marketing  
**Versión:** 1.0 - Diseño Técnico Definitivo

---

## 📑 TABLA DE CONTENIDOS

1. [Resumen Ejecutivo](#1-resumen-ejecutivo)
2. [Arquitectura Hub & Spoke Pragmático](#2-arquitectura-hub--spoke-pragmático)
3. [Filosofía de Diseño: Cuándo Separar vs. Agrupar](#3-filosofía-de-diseño)
4. [Estructura del Orquestador (Workflow A)](#4-estructura-del-orquestador)
5. [Adaptación de Sub-Workflows Existentes](#5-adaptación-de-sub-workflows)
6. [Implementación Paso a Paso](#6-implementación-paso-a-paso)
7. [Flujo de Datos y Comunicación](#7-flujo-de-datos-y-comunicación)
8. [Testing y Validación](#8-testing-y-validación)
9. [Monitoreo y Debugging](#9-monitoreo-y-debugging)
10. [Roadmap de Expansión](#10-roadmap-de-expansión)

---

## 1. RESUMEN EJECUTIVO

### 1.1 Objetivo Estratégico

Construir el **MEGA WORKFLOW A "Lead Lifecycle Manager"** como núcleo central (HUB) del ecosistema de automatización, permitiendo escalar de 20 leads/mes (manual) a 300+ leads/mes (automatizado) mediante una arquitectura modular Hub & Spoke.

### 1.2 Principio Rector

> **"Agrupa por COHESIÓN funcional, separa por FRAGILIDAD técnica"**

Esta filosofía combina lo mejor de ambos mundos:
- ✅ **Visibilidad de negocio:** 3 MEGA-WORKFLOWs conceptuales (visión estratégica)
- ✅ **Pragmatismo técnico:** 6 sub-workflows modulares (realidad de n8n)

### 1.3 Arquitectura Propuesta (Sweet Spot)

```
┌──────────────────────────────────────────────────────────────┐
│  MEGA-WORKFLOW #1: CAPTURA Y CONVERSIÓN DE LEADS            │
│  (Concepto estratégico - 60% Motor Futuro)                  │
└───────────────────────┬──────────────────────────────────────┘
                        │
        ┌───────────────▼───────────────┐
        │  WORKFLOW A (ORQUESTADOR)     │
        │  "Lead Lifecycle Manager"     │
        │  [HUB CENTRAL]                │
        └─────┬──────┬──────┬──────┬────┘
              │      │      │      │
      ┌───────▼──┐ ┌▼────┐ ┌▼───┐ ┌▼────────┐
      │ SUB-A:   │ │SUB-D││SUB-E│ │ SUB-F:  │
      │ Lead     │ │Nurt││Engag│ │ Meeting │
      │ Intake & │ │uring││Trac │ │Scheduler│
      │Enrichment│ │Seq. ││ker  │ │(Calendly│
      └──────────┘ └─────┘ └─────┘ └─────────┘
        [SPOKE]   [SPOKE] [SPOKE]   [SPOKE]
```

**¿Por qué este diseño?**
- **Cohesión alta:** SUB-A agrupa 3 agentes (Receptor, Enriquecedor, Clasificador) porque SIEMPRE se ejecutan juntos
- **Fragilidad baja:** Separamos Nurturing (SUB-D) porque puede fallar sin detener la captura
- **Complejidad media:** 6 workflows claros vs. 1 monolito gigante o 16 micro-workflows caóticos

---

## 2. ARQUITECTURA HUB & SPOKE PRAGMÁTICO

### 2.1 Comparativa de Enfoques

| Criterio | Monolítico (16 agentes, 1 workflow) | **Hub & Spoke (6 sub-workflows)** ⭐ | Sobre-Modularizado (16 sub-workflows) |
|----------|-------------------------------------|--------------------------------------|----------------------------------------|
| **Complejidad visual** | 🔴 Muy alta (lienzo gigante) | 🟢 Media (6 workflows claros) | 🔴 Alta (demasiados archivos) |
| **Debugging** | 🔴 Difícil (re-ejecutar todo) | 🟢 Fácil (aislar sub-workflow) | 🟡 Media (muchos logs dispersos) |
| **Modificaciones** | 🔴 Riesgoso (tocar 1 nodo afecta todo) | 🟢 Seguro (cambios localizados) | 🟢 Muy seguro |
| **Performance** | 🟢 Rápido (1 ejecución) | 🟡 Media (latencia entre llamadas) | 🔴 Lenta (overhead de red) |
| **Testing** | 🔴 Complejo (mock todo el estado) | 🟢 Simple (test unitario por sub-workflow) | 🟢 Muy simple |
| **Visibilidad de negocio** | 🟢 Clara (ves todo el proceso) | 🟢 Clara (documentación explica flujo) | 🔴 Perdida (no ves el big picture) |
| **Mantenimiento** | 🔴 Alto (cambios riesgosos) | 🟡 Medio | 🔴 Alto (demasiados puntos de falla) |
| **Tiempo implementación** | 🟢 Rápido (1 semana) | 🟡 Medio (2 semanas) | 🔴 Lento (3-4 semanas) |

**Ganador:** Hub & Spoke con 6 sub-workflows (sweet spot)

### 2.2 Reglas de Diseño: ¿Cuándo Separar vs. Agrupar?

#### ✅ SEPARA en sub-workflow si:

1. **Tiene un webhook trigger propio**
   - Ejemplo: Mailersend eventos (SUB-E) → necesita URL pública única
   - Razón: Necesita endpoint dedicado

2. **Puede fallar SIN detener el proceso crítico**
   - Ejemplo: Notificación WhatsApp puede fallar, pero lead ya está guardado
   - Razón: Falla aislada no debe matar el workflow principal

3. **Se ejecuta en horarios diferentes**
   - Ejemplo: Nurturing corre cada 6h (schedule), captura es real-time (webhook)
   - Razón: Mixing triggers en 1 workflow es complejo

#### ⛔ AGRUPA en mismo sub-workflow si:

1. **Siempre se ejecutan en secuencia (no hay lógica condicional compleja)**
   - Ejemplo: Normalizar → Validar → Clasificar (siempre ocurren juntos)
   - Razón: No hay beneficio en separarlos

2. **Comparten contexto (mismo lead)**
   - Ejemplo: Los 3 agentes del SUB-A operan sobre el mismo objeto lead
   - Razón: Pasar datos entre workflows genera latencia

3. **Fallan juntos o pasan juntos (cohesión alta)**
   - Ejemplo: Si no puedes normalizar, tampoco puedes clasificar
   - Razón: Agrupar simplifica el manejo de errores

---

## 3. FILOSOFÍA DE DISEÑO

### 3.1 Los 3 MEGA-WORKFLOWs Conceptuales

Estos son **conceptos estratégicos**, no archivos técnicos. Representan tu visión de negocio:

1. **MEGA-WORKFLOW #1: Captura y Conversión de Leads**
   - Motor Futuro (60% recursos): PyMEs tech, inbound marketing, automation-first
   - Objetivo: 100+ clientes nuevos/año, $350M+ COP ingresos

2. **MEGA-WORKFLOW #2: Relación y Retención de Clientes** *(Futuro Q2 2026)*
   - Gestión de clientes existentes, upsell, cross-sell
   - Automatización de seguimiento post-venta

3. **MEGA-WORKFLOW #3: Operaciones Internas** *(Futuro Q3 2026)*
   - Motor Actual (40% recursos): Contratación estatal, litigios, marketing relacional
   - Automatización de procesos internos

**Clave:** En n8n crearás 6 workflows técnicos, pero en tu documentación estratégica siempre hablas de 3 MEGA-WORKFLOWs.

### 3.2 Los 6 Sub-Workflows Técnicos (MEGA #1)

| ID | Nombre | Trigger | Función | Crítico |
|----|--------|---------|---------|---------|
| **WORKFLOW A** | Lead Lifecycle Manager (Orquestador) | Webhook `/lead-events` | Recibe todos los eventos y enruta a sub-workflows | ⚠️ SÍ |
| **SUB-A** | Lead Intake & Enrichment | Execute Workflow Trigger | Captura, normaliza, clasifica, guarda Firestore | ⚠️ SÍ |
| **SUB-D** | Nurturing Sequence Engine | Execute Workflow Trigger | Envía secuencia de 8-12 emails automatizados | 🟡 NO |
| **SUB-E** | Engagement Tracker | Webhook Mailersend `/email-events` | Captura opens/clicks de emails | 🟡 NO |
| **SUB-F** | Meeting Scheduler | Webhook Calendly `/booking-events` | Sincroniza reuniones agendadas | 🟡 NO |

**Decisión clave:** SUB-A es crítico (si falla, no capturamos el lead). SUB-D puede fallar y el lead ya está guardado.

---

## 4. ESTRUCTURA DEL ORQUESTADOR (WORKFLOW A)

### 4.1 Responsabilidades del Orquestador

El **WORKFLOW A "Lead Lifecycle Manager"** es el HUB central. Sus 3 responsabilidades:

1. **Recibir eventos diversos**
   - Nuevo lead (formulario web)
   - Lead respondió email (Mailersend webhook)
   - Lead abrió email (Mailersend webhook)
   - Lead agendó reunión (Calendly webhook)

2. **Enrutar al sub-workflow correcto**
   - Analizar el `event_type` del payload
   - Decidir qué sub-workflow ejecutar
   - Pasar los datos necesarios

3. **Consolidar respuestas**
   - Recibir resultado del sub-workflow
   - Responder al webhook original
   - Loguear evento en Firestore (opcional)

### 4.2 Arquitectura de Nodos del Orquestador

```
┌──────────────────────────────────────────────────────────┐
│  WORKFLOW A: Lead Lifecycle Manager (Orquestador)        │
└──────────────────────────────────────────────────────────┘

[1. Webhook Principal]  ← Entrada única de todos los eventos
    │
    │ POST a /lead-events
    │ Body: { event_type, payload }
    ▼
[2. Identificar Tipo de Evento]  ← Code node
    │
    │ Analiza: event_type
    │ Extrae: datos relevantes
    │ Enriquece: metadata del orquestador
    ▼
[3. Router por Evento]  ← Switch node
    │
    ├─────► "new_lead" ──────► [4A. Execute SUB-A] ──┐
    │                                                  │
    ├─────► "email_opened" ──► [4E. Execute SUB-E] ──┤
    │                                                  │
    ├─────► "meeting_booked" ─► [4F. Execute SUB-F] ─┤
    │                                                  │
    └─────► "unknown_event" ──► [ERROR Handler] ──────┤
                                                       │
                                                       ▼
                                            [5. Consolidar Respuesta]
                                                       │
                                                       ▼
                                            [6. Responder Webhook]
                                                       │
                                                       ▼
                                            [7. Log a Firestore (opcional)]
```

### 4.3 Código del Orquestador (JSON n8n)

#### Nodo 1: Webhook Principal

```json
{
  "name": "Webhook Principal Lead Events",
  "type": "n8n-nodes-base.webhook",
  "typeVersion": 2.1,
  "position": [240, 300],
  "parameters": {
    "httpMethod": "POST",
    "path": "lead-events",
    "responseMode": "responseNode",
    "options": {}
  },
  "webhookId": "carrillo-lead-lifecycle"
}
```

**Configuración crítica:**
- `path: "lead-events"` → URL será: `https://tu-n8n.app.n8n.cloud/webhook/lead-events`
- `responseMode: "responseNode"` → Permite responder de forma controlada después de procesar

#### Nodo 2: Identificar Tipo de Evento

```json
{
  "name": "2. Identificar Tipo de Evento",
  "type": "n8n-nodes-base.code",
  "typeVersion": 2,
  "position": [460, 300],
  "parameters": {
    "jsCode": "// ORQUESTADOR: Clasificar evento entrante\nconst payload = $input.first().json;\n\n// Determinar event_type\nlet eventType = payload.event_type || 'unknown';\nlet targetWorkflowId = null;\n\n// ROUTING LOGIC\nif (eventType === 'new_lead' || (payload.email && !payload.lead_id)) {\n  eventType = 'new_lead';\n  targetWorkflowId = 'SUB_A_WORKFLOW_ID'; // Reemplazar con ID real de SUB-A\n} \nelse if (eventType === 'email_opened' || payload.mailersend_event === 'email.opened') {\n  eventType = 'email_opened';\n  targetWorkflowId = 'SUB_E_WORKFLOW_ID'; // Reemplazar con ID real de SUB-E\n} \nelse if (eventType === 'meeting_booked' || payload.calendly_event) {\n  eventType = 'meeting_booked';\n  targetWorkflowId = 'SUB_F_WORKFLOW_ID'; // Reemplazar con ID real de SUB-F\n}\n\n// Enriquecer payload\nreturn {\n  json: {\n    ...payload,\n    // Metadata del orquestador\n    event_type: eventType,\n    target_workflow_id: targetWorkflowId,\n    orchestrator_timestamp: new Date().toISOString(),\n    orchestrator_execution_id: $execution.id,\n    // Pasar payload original\n    original_payload: payload\n  }\n};"
  }
}
```

**Lógica clave:**
1. Detecta `event_type` del payload
2. Si no está explícito, infiere del contenido (ej: si tiene `email` y no tiene `lead_id`, es nuevo lead)
3. Asigna el `target_workflow_id` correspondiente
4. Enriquece con metadata del orquestador para trazabilidad

#### Nodo 3: Router por Evento

```json
{
  "name": "3. Router por Evento",
  "type": "n8n-nodes-base.switch",
  "typeVersion": 3.2,
  "position": [680, 300],
  "parameters": {
    "rules": {
      "rules": [
        {
          "name": "Nuevo Lead",
          "conditions": {
            "conditions": [
              {
                "leftValue": "={{ $json.event_type }}",
                "rightValue": "new_lead",
                "operator": {
                  "type": "string",
                  "operation": "equals"
                }
              }
            ]
          }
        },
        {
          "name": "Email Abierto",
          "conditions": {
            "conditions": [
              {
                "leftValue": "={{ $json.event_type }}",
                "rightValue": "email_opened",
                "operator": {
                  "type": "string",
                  "operation": "equals"
                }
              }
            ]
          }
        },
        {
          "name": "Reunión Agendada",
          "conditions": {
            "conditions": [
              {
                "leftValue": "={{ $json.event_type }}",
                "rightValue": "meeting_booked",
                "operator": {
                  "type": "string",
                  "operation": "equals"
                }
              }
            ]
          }
        }
      ]
    },
    "options": {
      "fallbackOutput": 3
    }
  }
}
```

**Salidas del Switch:**
- Output 0 (branch 0): `new_lead` → Ejecuta SUB-A
- Output 1 (branch 1): `email_opened` → Ejecuta SUB-E
- Output 2 (branch 2): `meeting_booked` → Ejecuta SUB-F
- Fallback (branch 3): `unknown_event` → Error Handler

#### Nodo 4A: Execute SUB-A

```json
{
  "name": "4A. Execute SUB-A (Lead Intake)",
  "type": "n8n-nodes-base.executeWorkflow",
  "typeVersion": 1.3,
  "position": [900, 180],
  "parameters": {
    "source": "database",
    "workflowId": {
      "mode": "id",
      "value": "TU_SUB_A_WORKFLOW_ID"
    },
    "mode": "once",
    "workflowInputs": {
      "mappingMode": "defineBelow",
      "value": {
        "nombre": "={{ $json.original_payload.nombre }}",
        "email": "={{ $json.original_payload.email }}",
        "telefono": "={{ $json.original_payload.telefono }}",
        "empresa": "={{ $json.original_payload.empresa }}",
        "cargo": "={{ $json.original_payload.cargo }}",
        "servicio_interes": "={{ $json.original_payload.servicio_interes }}",
        "mensaje": "={{ $json.original_payload.mensaje }}",
        "utm_source": "={{ $json.original_payload.utm_source }}",
        "utm_campaign": "={{ $json.original_payload.utm_campaign }}"
      }
    },
    "options": {
      "waitForSubWorkflow": true
    }
  }
}
```

**Configuración crítica:**
- `source: "database"` → Ejecuta workflow guardado en n8n (no desde archivo)
- `workflowId.value` → **DEBES REEMPLAZAR** con el ID real de tu SUB-A (lo obtienes después de crear SUB-A)
- `mode: "once"` → Ejecuta una vez con todos los items (no loop por cada item)
- `workflowInputs` → Mapea explícitamente qué datos pasar al sub-workflow
- `waitForSubWorkflow: true` → Espera a que SUB-A termine antes de continuar (crítico para obtener respuesta)

#### Nodo 5: Consolidar Respuesta

```json
{
  "name": "5. Consolidar Respuesta",
  "type": "n8n-nodes-base.set",
  "typeVersion": 3.4,
  "position": [1120, 300],
  "parameters": {
    "assignments": {
      "assignments": [
        {
          "id": "success",
          "name": "success",
          "value": "={{ $json.success !== false }}",
          "type": "boolean"
        },
        {
          "id": "event_type",
          "name": "event_type",
          "value": "={{ $('2. Identificar Tipo de Evento').item.json.event_type }}",
          "type": "string"
        },
        {
          "id": "sub_workflow_executed",
          "name": "sub_workflow_executed",
          "value": "={{ $('2. Identificar Tipo de Evento').item.json.target_workflow_id }}",
          "type": "string"
        },
        {
          "id": "result",
          "name": "result",
          "value": "={{ $json }}",
          "type": "object"
        },
        {
          "id": "orchestrator_execution_time_ms",
          "name": "orchestrator_execution_time_ms",
          "value": "={{ $now.diff($('2. Identificar Tipo de Evento').item.json.orchestrator_timestamp).toObject().milliseconds }}",
          "type": "number"
        }
      ]
    }
  }
}
```

**Función:**
- Consolida la respuesta del sub-workflow ejecutado
- Calcula tiempo total de ejecución
- Prepara estructura de respuesta unificada

#### Nodo 6: Responder Webhook

```json
{
  "name": "6. Responder Webhook",
  "type": "n8n-nodes-base.respondToWebhook",
  "typeVersion": 1.1,
  "position": [1340, 300],
  "parameters": {
    "respondWith": "json",
    "responseBody": "={{ { \n  \"success\": $json.success,\n  \"event_type\": $json.event_type,\n  \"sub_workflow_executed\": $json.sub_workflow_executed,\n  \"execution_time_ms\": $json.orchestrator_execution_time_ms,\n  \"result\": $json.result\n} }}",
    "options": {
      "responseCode": "={{ $json.success ? 200 : 500 }}"
    }
  }
}
```

**Respuesta típica:**
```json
{
  "success": true,
  "event_type": "new_lead",
  "sub_workflow_executed": "SUB_A_WORKFLOW_ID",
  "execution_time_ms": 3542,
  "result": {
    "lead_id": "2026-01-15T10:30:00.000Z-sofia-techcorp-com",
    "score": 95,
    "categoria": "HOT"
  }
}
```

---

## 5. ADAPTACIÓN DE SUB-WORKFLOWS

### 5.1 Transformar SUB-A: De Webhook a Execute Workflow Trigger

Tu SUB-A actual comienza con un **Webhook**. Para que funcione en la arquitectura Hub & Spoke, debe comenzar con un **Execute Workflow Trigger**.

#### ANTES (Actual):
```
[Webhook Lead Capture] → [1. Normalizar] → ...
```

#### DESPUÉS (Adaptado):
```
[Execute Workflow Trigger] → [1. Normalizar] → ...
```

#### Pasos de Adaptación:

**1. Reemplazar el nodo Webhook**

```json
{
  "name": "Execute Workflow Trigger SUB-A",
  "type": "n8n-nodes-base.executeWorkflowTrigger",
  "typeVersion": 1.1,
  "position": [240, 300],
  "parameters": {
    "inputSource": "workflowInputs",
    "workflowInputs": {
      "values": [
        {"name": "nombre", "type": "string"},
        {"name": "email", "type": "string"},
        {"name": "telefono", "type": "string"},
        {"name": "empresa", "type": "string"},
        {"name": "cargo", "type": "string"},
        {"name": "servicio_interes", "type": "string"},
        {"name": "mensaje", "type": "string"},
        {"name": "utm_source", "type": "string"},
        {"name": "utm_campaign", "type": "string"}
      ]
    }
  }
}
```

**2. Ajustar referencias en nodos subsecuentes**

ANTES el nodo "1. Normalizar" accedía a:
```javascript
$json.email
```

DESPUÉS debe acceder a:
```javascript
$('Execute Workflow Trigger SUB-A').item.json.email
```

**O mejor, mantener compatibilidad:**

Agregar un nodo "Set" inmediatamente después del Execute Workflow Trigger que reorganice los datos:

```json
{
  "name": "0. Mapear Input del Orquestador",
  "type": "n8n-nodes-base.set",
  "typeVersion": 3.4,
  "position": [380, 300],
  "parameters": {
    "assignments": {
      "assignments": [
        {"id": "nombre", "name": "nombre", "value": "={{ $json.nombre }}", "type": "string"},
        {"id": "email", "name": "email", "value": "={{ $json.email }}", "type": "string"},
        {"id": "telefono", "name": "telefono", "value": "={{ $json.telefono }}", "type": "string"},
        {"id": "empresa", "name": "empresa", "value": "={{ $json.empresa }}", "type": "string"},
        {"id": "cargo", "name": "cargo", "value": "={{ $json.cargo }}", "type": "string"},
        {"id": "servicio_interes", "name": "servicio_interes", "value": "={{ $json.servicio_interes }}", "type": "string"},
        {"id": "mensaje", "name": "mensaje", "value": "={{ $json.mensaje }}", "type": "string"},
        {"id": "utm_source", "name": "utm_source", "value": "={{ $json.utm_source }}", "type": "string"},
        {"id": "utm_campaign", "name": "utm_campaign", "value": "={{ $json.utm_campaign }}", "type": "string"}
      ]
    }
  }
}
```

Esto asegura que el resto del workflow SUB-A funcione sin cambios.

**3. Eliminar nodo "Responder Webhook"**

Los sub-workflows NO responden directamente al webhook. El orquestador lo hace.

REMOVER: Nodo "✅ Responder Éxito"

**4. Asegurar que el último nodo tiene los datos de salida**

El último nodo del SUB-A debe tener la estructura de datos que el orquestador espera:

```json
{
  "name": "FINAL. Resultado del Sub-Workflow",
  "type": "n8n-nodes-base.set",
  "typeVersion": 3.4,
  "position": [2220, 300],
  "parameters": {
    "assignments": {
      "assignments": [
        {
          "id": "success",
          "name": "success",
          "value": "true",
          "type": "boolean"
        },
        {
          "id": "lead_id",
          "name": "lead_id",
          "value": "={{ $json.lead_id }}",
          "type": "string"
        },
        {
          "id": "score",
          "name": "score",
          "value": "={{ $json.score }}",
          "type": "number"
        },
        {
          "id": "categoria",
          "name": "categoria",
          "value": "={{ $json.categoria }}",
          "type": "string"
        },
        {
          "id": "message",
          "name": "message",
          "value": "Lead procesado exitosamente por SUB-A",
          "type": "string"
        }
      ]
    }
  }
}
```

### 5.2 Manejo de Errores en Sub-Workflows

Los sub-workflows DEBEN capturar sus propios errores y retornar una estructura consistente:

```json
{
  "name": "ERROR Handler SUB-A",
  "type": "n8n-nodes-base.set",
  "typeVersion": 3.4,
  "position": [900, 480],
  "parameters": {
    "assignments": {
      "assignments": [
        {
          "id": "success",
          "name": "success",
          "value": "false",
          "type": "boolean"
        },
        {
          "id": "error_message",
          "name": "error_message",
          "value": "={{ $json.error.message || 'Error desconocido en SUB-A' }}",
          "type": "string"
        },
        {
          "id": "error_node",
          "name": "error_node",
          "value": "={{ $json.error.node || 'Unknown' }}",
          "type": "string"
        }
      ]
    }
  }
}
```

**Configuración del workflow para error handling:**

En las "Workflow Settings" del SUB-A:
1. Settings → Error Workflow → "Continue on Error" activado para nodos críticos
2. Conectar nodos que pueden fallar a un "Error Handler" usando la salida de error

---

## 6. IMPLEMENTACIÓN PASO A PASO

### 6.1 Fase 1: Preparación (Día 1)

**Objetivo:** Tener todos los componentes listos para integración

**Tareas:**

1. **Validar que SUB-A funciona standalone**
   ```bash
   # Test SUB-A con su webhook actual
   curl -X POST https://tu-n8n.app.n8n.cloud/webhook/lead-capture-test \
     -H "Content-Type: application/json" \
     -d '{
       "nombre": "Test",
       "email": "test@test.com",
       "empresa": "Test Corp"
     }'
   ```
   ✅ Verificar: Lead se guarda en Firestore, emails se envían

2. **Documentar el Workflow ID de SUB-A**
   - En n8n: Abrir SUB-A
   - En la URL verás: `/workflow/XXXX`
   - Copiar ese ID: `XXXX`
   - Este será el `workflowId` en el orquestador

3. **Crear checklist de credenciales**
   ```
   [ ] Gmail OAuth2 configurado
   [ ] Firestore Service Account configurado
   [ ] Google Gemini API Key configurado
   [ ] Firestore collection "leads" creada
   [ ] Firestore collection "orchestrator_logs" creada (opcional)
   ```

### 6.2 Fase 2: Construcción del Orquestador (Día 2-3)

**Objetivo:** Crear el WORKFLOW A funcional

**Paso 1: Crear nuevo workflow**
1. n8n → Click "+ Add workflow"
2. Nombrar: "WORKFLOW A: Lead Lifecycle Manager (Orquestador)"
3. Agregar tag: "MEGA-WORKFLOW-1"

**Paso 2: Agregar nodos en orden**

Copiar y pegar los nodos en el orden presentado en la sección 4.3:
1. Webhook Principal
2. Identificar Tipo de Evento
3. Router por Evento
4. Execute SUB-A (y futuros SUB-D, SUB-E, SUB-F)
5. Consolidar Respuesta
6. Responder Webhook

**Paso 3: Configurar workflowId de SUB-A**

En el nodo "4A. Execute SUB-A":
```json
"workflowId": {
  "mode": "id",
  "value": "REEMPLAZAR_CON_ID_REAL_DE_SUB_A"
}
```

**Paso 4: Activar el orquestador**
1. Toggle "Active" ON
2. Copiar la URL del webhook: `https://tu-n8n.app.n8n.cloud/webhook/lead-events`

### 6.3 Fase 3: Adaptar SUB-A para Arquitectura Hub & Spoke (Día 3-4)

**Objetivo:** SUB-A debe recibir datos del orquestador, no del formulario web

**Paso 1: Duplicar SUB-A**
- n8n → SUB-A actual → Duplicate
- Renombrar copia: "SUB-A: Lead Intake & Enrichment (v2 - Hub & Spoke)"
- Trabajar en esta copia (no tocar el original hasta validar)

**Paso 2: Reemplazar Webhook por Execute Workflow Trigger**
- Eliminar nodo "Webhook Lead Capture"
- Agregar nodo "Execute Workflow Trigger" con los parámetros de la sección 5.1

**Paso 3: Agregar nodo "0. Mapear Input del Orquestador"**
- Insertar después del Execute Workflow Trigger
- Copiar configuración de la sección 5.1

**Paso 4: Eliminar nodo "Responder Webhook"**
- Los sub-workflows NO responden webhooks

**Paso 5: Agregar nodo final "FINAL. Resultado del Sub-Workflow"**
- Agregar al final del flujo exitoso
- Copiar configuración de la sección 5.1

**Paso 6: Configurar error handling**
- Agregar nodo "ERROR Handler SUB-A"
- Conectar salidas de error de nodos críticos

### 6.4 Fase 4: Testing Integrado (Día 5)

**Test 1: Nuevo Lead a través del Orquestador**

```bash
curl -X POST https://tu-n8n.app.n8n.cloud/webhook/lead-events \
  -H "Content-Type: application/json" \
  -d '{
    "event_type": "new_lead",
    "nombre": "María Rodríguez",
    "email": "maria@techstartup.co",
    "telefono": "+573101234567",
    "empresa": "TechStartup SAS",
    "cargo": "CEO",
    "servicio_interes": "Registro de Marca",
    "mensaje": "Necesitamos proteger nuestra marca de software",
    "utm_source": "google",
    "utm_campaign": "registro-marca-q1-2026"
  }'
```

**Qué validar:**
1. ✅ Orquestador recibe request
2. ✅ Nodo "2. Identificar Tipo de Evento" clasifica como `new_lead`
3. ✅ Router envía a branch 0 (Nuevo Lead)
4. ✅ "4A. Execute SUB-A" se ejecuta
5. ✅ SUB-A procesa el lead (ver ejecución de SUB-A en "Executions")
6. ✅ Lead se guarda en Firestore
7. ✅ Emails se envían (HOT notification + respuesta automática)
8. ✅ Orquestador recibe respuesta de SUB-A
9. ✅ "6. Responder Webhook" devuelve 200 con resultado

**Respuesta esperada:**
```json
{
  "success": true,
  "event_type": "new_lead",
  "sub_workflow_executed": "SUB_A_WORKFLOW_ID",
  "execution_time_ms": 3542,
  "result": {
    "success": true,
    "lead_id": "2026-01-15T10:30:00.000Z-maria-techstartup-co",
    "score": 95,
    "categoria": "HOT",
    "message": "Lead procesado exitosamente por SUB-A"
  }
}
```

**Test 2: Evento desconocido (debe fallar controladamente)**

```bash
curl -X POST https://tu-n8n.app.n8n.cloud/webhook/lead-events \
  -H "Content-Type: application/json" \
  -d '{
    "event_type": "unknown_event_type",
    "some_data": "test"
  }'
```

**Qué validar:**
1. ✅ Orquestador recibe request
2. ✅ Nodo "2. Identificar Tipo de Evento" clasifica como `unknown`
3. ✅ Router envía a fallback (branch 3)
4. ✅ Error handler responde 400
5. ✅ NO se ejecuta ningún sub-workflow

**Test 3: SUB-A falla (simular error en Firestore)**

Desactivar temporalmente credenciales de Firestore en SUB-A, luego enviar request:

```bash
curl -X POST https://tu-n8n.app.n8n.cloud/webhook/lead-events \
  -H "Content-Type: application/json" \
  -d '{
    "event_type": "new_lead",
    "nombre": "Test Error",
    "email": "test@error.com",
    "empresa": "Error Corp"
  }'
```

**Qué validar:**
1. ✅ SUB-A falla en el nodo de Firestore
2. ✅ Error handler de SUB-A captura el error
3. ✅ SUB-A retorna `{"success": false, "error_message": "..."}`
4. ✅ Orquestador recibe el error
5. ✅ Orquestador responde 500 con detalles del error

**Reactivar credenciales después del test**

---

## 7. FLUJO DE DATOS Y COMUNICACIÓN

### 7.1 Estructura de Payloads

#### Entrada al Orquestador

**Caso 1: Nuevo Lead (desde formulario web)**
```json
{
  "event_type": "new_lead",
  "nombre": "Juan Pérez",
  "email": "juan@empresa.com",
  "telefono": "+573001234567",
  "empresa": "Empresa SAS",
  "cargo": "Gerente",
  "servicio_interes": "Registro de Marca",
  "mensaje": "Necesito asesoría sobre...",
  "utm_source": "google",
  "utm_campaign": "marca-q1-2026"
}
```

**Caso 2: Email Abierto (desde Mailersend webhook - futuro SUB-E)**
```json
{
  "event_type": "email_opened",
  "lead_id": "2026-01-15T10:30:00.000Z-juan-empresa-com",
  "email": "juan@empresa.com",
  "mailersend_event": "email.opened",
  "campaign_id": "nurture-sequence-1",
  "opened_at": "2026-01-16T08:45:00.000Z"
}
```

**Caso 3: Reunión Agendada (desde Calendly webhook - futuro SUB-F)**
```json
{
  "event_type": "meeting_booked",
  "lead_id": "2026-01-15T10:30:00.000Z-juan-empresa-com",
  "calendly_event": "invitee.created",
  "meeting_start": "2026-01-20T10:00:00.000Z",
  "meeting_end": "2026-01-20T11:00:00.000Z",
  "invitee_email": "juan@empresa.com"
}
```

#### Salida del Orquestador

**Éxito:**
```json
{
  "success": true,
  "event_type": "new_lead",
  "sub_workflow_executed": "SUB_A_WORKFLOW_ID",
  "execution_time_ms": 3542,
  "result": {
    // Respuesta específica del sub-workflow ejecutado
  }
}
```

**Error:**
```json
{
  "success": false,
  "event_type": "new_lead",
  "sub_workflow_executed": "SUB_A_WORKFLOW_ID",
  "execution_time_ms": 1203,
  "error": {
    "message": "Failed to save to Firestore",
    "node": "4. Guardar en Firestore",
    "details": "..."
  }
}
```

### 7.2 Patrones de Comunicación

#### Patrón 1: Ejecución Sincrónica (Actual)

```
Formulario Web
    │
    ├─> POST al Orquestador
    │       │
    │       ├─> Orquestador ejecuta SUB-A
    │       │       │
    │       │       └─> SUB-A procesa (3-8 seg)
    │       │               └─> Retorna resultado
    │       │
    │       └─> Orquestador responde al formulario
    │
    └─> Usuario ve confirmación
```

**Ventajas:**
- ✅ Simple de implementar
- ✅ Usuario recibe confirmación inmediata
- ✅ Debugging fácil (todo en 1 ejecución)

**Desventajas:**
- ⚠️ Si SUB-A demora >10 seg, el webhook puede timeout
- ⚠️ Formulario web queda esperando

#### Patrón 2: Ejecución Asincrónica (Futuro - Opcional)

```
Formulario Web
    │
    ├─> POST al Orquestador
    │       │
    │       ├─> Orquestador responde INMEDIATO
    │       │   (200: "Request received, processing...")
    │       │
    │       └─> Orquestador ejecuta SUB-A en background
    │               │
    │               └─> SUB-A notifica completion via webhook
    │
    └─> Usuario ve confirmación inmediata
```

**Ventajas:**
- ✅ Sin riesgo de timeout
- ✅ UX más rápida para el usuario

**Desventajas:**
- ⚠️ Más complejo de implementar
- ⚠️ Usuario no sabe si realmente se procesó (necesita email de confirmación)

**Recomendación:** Empezar con Patrón 1 (sincrónico). Cambiar a Patrón 2 solo si experimentas timeouts frecuentes.

---

## 8. TESTING Y VALIDACIÓN

### 8.1 Checklist de Testing Completo

#### Nivel 1: Sub-Workflows Individuales

Antes de integrar, cada sub-workflow debe pasar estos tests:

**SUB-A: Lead Intake & Enrichment**
- [ ] ✅ Recibe input del Execute Workflow Trigger
- [ ] ✅ Normaliza datos correctamente
- [ ] ✅ Valida email (rechaza inválidos)
- [ ] ✅ Clasifica lead (HOT/WARM/COLD)
- [ ] ✅ Guarda en Firestore con todos los campos
- [ ] ✅ Envía email de notificación HOT (si aplica)
- [ ] ✅ Genera email personalizado con Gemini
- [ ] ✅ Envía email de respuesta automática
- [ ] ✅ Retorna estructura de respuesta correcta
- [ ] ✅ Maneja errores (credenciales faltantes, Firestore down, etc.)

#### Nivel 2: Orquestador (Routing)

El orquestador debe enrutar correctamente:

- [ ] ✅ `event_type: "new_lead"` → Ejecuta SUB-A
- [ ] ✅ `event_type: "email_opened"` → Ejecuta SUB-E (futuro)
- [ ] ✅ `event_type: "meeting_booked"` → Ejecuta SUB-F (futuro)
- [ ] ✅ `event_type: "unknown"` → Retorna error 400
- [ ] ✅ Payload sin `event_type` pero con `email` → Infiere `new_lead`

#### Nivel 3: Integración End-to-End

El flujo completo debe funcionar:

- [ ] ✅ Formulario web → Orquestador → SUB-A → Firestore + Emails
- [ ] ✅ Tiempo de respuesta < 10 segundos
- [ ] ✅ Sin errores 500 en logs
- [ ] ✅ Respuesta del webhook es consistente
- [ ] ✅ Logs en Firestore (opcional) se crean correctamente

### 8.2 Escenarios de Prueba

#### Escenario 1: Lead HOT (Camino Feliz)

**Input:**
```json
{
  "event_type": "new_lead",
  "nombre": "Ana Martínez",
  "email": "ana@fintech.co",
  "telefono": "+573101234567",
  "empresa": "Fintech Innovations SAS",
  "cargo": "CEO",
  "servicio_interes": "Registro de Marca",
  "mensaje": "Necesitamos registrar la marca de nuestra app fintech",
  "utm_source": "linkedin",
  "utm_campaign": "startup-q1-2026"
}
```

**Validaciones:**
1. ✅ Orquestador ejecuta SUB-A
2. ✅ SUB-A asigna score 95/100 (HOT)
3. ✅ Se guarda en Firestore
4. ✅ Dr. Carrillo recibe email de notificación HOT
5. ✅ Ana recibe email personalizado por Gemini
6. ✅ Orquestador responde 200

#### Escenario 2: Lead WARM

**Input:**
```json
{
  "event_type": "new_lead",
  "nombre": "Carlos López",
  "email": "carlos@pyme.com",
  "telefono": "",
  "empresa": "PyME Tradicional Ltda",
  "cargo": "Contador",
  "servicio_interes": "Consulta General",
  "mensaje": "Quisiera información",
  "utm_source": "direct",
  "utm_campaign": ""
}
```

**Validaciones:**
1. ✅ Orquestador ejecuta SUB-A
2. ✅ SUB-A asigna score 0/100 (COLD/WARM bajo)
3. ✅ Se guarda en Firestore
4. ✅ Dr. Carrillo NO recibe email (no es HOT)
5. ✅ Carlos recibe email personalizado por Gemini
6. ✅ Orquestador responde 200

#### Escenario 3: Email Inválido (Error Controlado)

**Input:**
```json
{
  "event_type": "new_lead",
  "nombre": "Sin Email",
  "empresa": "Test Corp"
}
```

**Validaciones:**
1. ✅ Orquestador ejecuta SUB-A
2. ✅ SUB-A falla en validación de email
3. ✅ SUB-A retorna `{"success": false, "error_message": "Email inválido"}`
4. ✅ Orquestador responde 400
5. ✅ NO se guarda en Firestore
6. ✅ NO se envían emails

#### Escenario 4: Firestore Down (Error Técnico)

**Setup:** Desactivar credenciales de Firestore temporalmente

**Input:** Cualquier lead válido

**Validaciones:**
1. ✅ Orquestador ejecuta SUB-A
2. ✅ SUB-A falla en el nodo "4. Guardar en Firestore"
3. ✅ Error handler de SUB-A captura el error
4. ✅ SUB-A retorna `{"success": false, "error_message": "Firestore connection failed"}`
5. ✅ Orquestador responde 500 con detalles
6. ✅ Lead NO se guarda (obviamente)
7. ✅ Emails NO se envían (workflow detiene después del error)

**Restaurar credenciales después del test**

### 8.3 Herramientas de Testing

#### Opción 1: cURL (Línea de comandos)

```bash
# Guardar en archivo test_lead_hot.sh
curl -X POST https://tu-n8n.app.n8n.cloud/webhook/lead-events \
  -H "Content-Type: application/json" \
  -d @payloads/lead_hot.json
```

#### Opción 2: Postman / Insomnia

1. Crear colección "Carrillo Abogados - Lead Testing"
2. Agregar requests:
   - "01 - New Lead HOT"
   - "02 - New Lead WARM"
   - "03 - Invalid Email"
   - etc.

#### Opción 3: Script de testing automatizado

```javascript
// test_orchestrator.js
const axios = require('axios');

const ORCHESTRATOR_URL = 'https://tu-n8n.app.n8n.cloud/webhook/lead-events';

const testCases = [
  {
    name: 'Lead HOT',
    payload: {
      event_type: 'new_lead',
      nombre: 'Test HOT',
      email: 'hot@test.com',
      empresa: 'Tech Corp',
      cargo: 'CEO',
      utm_source: 'google'
    },
    expectedScore: 95
  },
  // ... más casos
];

async function runTests() {
  for (const test of testCases) {
    console.log(`Running: ${test.name}`);
    try {
      const response = await axios.post(ORCHESTRATOR_URL, test.payload);
      console.log(`✅ ${test.name}: PASSED`);
      console.log(`   Score: ${response.data.result.score}`);
    } catch (error) {
      console.log(`❌ ${test.name}: FAILED`);
      console.log(`   Error: ${error.message}`);
    }
  }
}

runTests();
```

---

## 9. MONITOREO Y DEBUGGING

### 9.1 Monitoreo en n8n Cloud

#### Dashboard de Ejecuciones

1. **Ver todas las ejecuciones:**
   - Left sidebar → "Executions"
   - Filtrar por workflow (Orquestador, SUB-A, etc.)
   - Filtrar por fecha

2. **Inspeccionar ejecución específica:**
   - Click en una ejecución
   - Ver cada nodo:
     - ✅ Verde: Éxito
     - ❌ Rojo: Error
   - Click en nodo para ver:
     - Input data
     - Output data
     - Execution time

3. **Buscar ejecuciones problemáticas:**
   - Filtrar por "Error"
   - Ordenar por "Execution time" (detectar lentitud)

#### Alertas Proactivas

**Configurar notificación de errores:**

1. Crear "Error Workflow" global:
   - Workflow name: "Global Error Handler"
   - Trigger: Error Trigger
   - Acción: Enviar email a tu inbox con detalles del error

2. Asignar a workflows críticos:
   - Settings del Orquestador → Error Workflow → Seleccionar "Global Error Handler"
   - Settings de SUB-A → Error Workflow → Seleccionar "Global Error Handler"

### 9.2 Logs Estructurados en Firestore (Opcional)

#### Estructura de Collection "orchestrator_logs"

```javascript
{
  "execution_id": "exec_123abc",
  "timestamp": "2026-01-15T10:30:00.000Z",
  "event_type": "new_lead",
  "sub_workflow_executed": "SUB_A_WORKFLOW_ID",
  "execution_time_ms": 3542,
  "success": true,
  "input_payload": { ... },
  "output_result": { ... },
  "error": null
}
```

#### Agregar nodo de logging en el Orquestador

```json
{
  "name": "7. Log a Firestore (opcional)",
  "type": "n8n-nodes-base.googleFirebaseCloudFirestore",
  "typeVersion": 1.1,
  "position": [1560, 300],
  "parameters": {
    "authentication": "serviceAccount",
    "resource": "document",
    "operation": "create",
    "projectId": {
      "mode": "list",
      "value": "carrillo-abogados-prod"
    },
    "database": "(default)",
    "collection": "orchestrator_logs",
    "columns": "execution_id, timestamp, event_type, sub_workflow_executed, execution_time_ms, success, input_payload, output_result"
  },
  "credentials": {
    "googleApi": {
      "id": "GOOGLE_SERVICE_ACCOUNT_ID",
      "name": "Google Cloud Service Account"
    }
  }
}
```

**Beneficios:**
- ✅ Auditoría completa de eventos
- ✅ Debugging histórico (qué pasó con el lead XYZ hace 2 semanas)
- ✅ Métricas de performance (cuánto demora cada sub-workflow)
- ✅ Base para dashboards (Looker Studio)

**Desventaja:**
- ⚠️ Costo adicional de Firestore writes (mínimo)
- ⚠️ Complejidad adicional

**Recomendación:** Implementar solo si necesitas auditoría exhaustiva o estás en fase de debugging intenso.

### 9.3 Debugging de Problemas Comunes

#### Problema 1: "Workflow not found" al ejecutar SUB-A

**Causa:** El `workflowId` en el nodo "Execute SUB-A" es incorrecto

**Solución:**
1. Abrir SUB-A en n8n
2. En la URL, copiar el ID: `/workflow/XXXXX` → `XXXXX` es tu ID
3. Actualizar el nodo "4A. Execute SUB-A":
   ```json
   "workflowId": {
     "value": "XXXXX"  // <- Pegar ID real
   }
   ```

#### Problema 2: SUB-A se ejecuta pero orquestador no recibe respuesta

**Causa:** El nodo "4A. Execute SUB-A" tiene `waitForSubWorkflow: false`

**Solución:**
Cambiar a `true`:
```json
"options": {
  "waitForSubWorkflow": true  // <- IMPORTANTE
}
```

#### Problema 3: Timeout después de 60 segundos

**Causa:** SUB-A demora demasiado (ej: Gemini API lenta)

**Soluciones:**
1. **Corto plazo:** Aumentar timeout del webhook
   ```json
   // En nodo "Webhook Principal"
   "options": {
     "requestTimeout": 120000  // 120 segundos
   }
   ```

2. **Largo plazo:** Cambiar a ejecución asincrónica (Patrón 2 de sección 7.2)

#### Problema 4: Orquestador ejecuta SUB-A múltiples veces para el mismo lead

**Causa:** El formulario web hace retry automático si no recibe respuesta rápida

**Solución:**
Implementar idempotency key:

1. En el formulario web, generar un `request_id` único:
   ```javascript
   const requestId = Date.now() + '-' + Math.random();
   ```

2. Incluir en el payload:
   ```json
   {
     "request_id": "1705315800000-0.123456",
     "event_type": "new_lead",
     ...
   }
   ```

3. En el orquestador, verificar si ya procesamos este `request_id`:
   ```javascript
   // Nodo "Check Duplicate Request" antes del Router
   const requestId = $json.request_id;
   
   // Query Firestore para ver si existe
   // Si existe → return { "success": true, "message": "Already processed" }
   // Si no existe → continuar
   ```

---

## 10. ROADMAP DE EXPANSIÓN

### 10.1 Fase 1: MVP (Semanas 1-2) ✅ EN PROGRESO

**Objetivo:** Sistema funcional con captura y clasificación de leads

**Componentes:**
- ✅ WORKFLOW A (Orquestador) básico
- ✅ SUB-A (Lead Intake & Enrichment)
- ✅ Integración formulario web → Orquestador
- ✅ Testing completo

**Entregables:**
- Orquestador captura leads del formulario
- Leads se guardan en Firestore
- Emails automáticos se envían
- Dashboard básico en n8n Executions

### 10.2 Fase 2: Nurturing Automatizado (Semanas 3-4)

**Objetivo:** Leads reciben secuencia de emails automatizada

**Componentes:**
- 🔄 SUB-D (Nurturing Sequence Engine)
- 🔄 Integración Mailersend
- 🔄 Schedule trigger (corre cada 6h)

**Flujo:**
1. SUB-D corre cada 6h (Schedule trigger)
2. Query Firestore: Buscar leads con `status: "nuevo"` y `last_contact > 24h`
3. Para cada lead:
   - Determinar qué email de la secuencia enviar (Email 1, 2, 3, etc.)
   - Generar contenido con Gemini
   - Enviar via Mailersend
   - Actualizar Firestore: `last_contact: now()`, `emails_sent: +1`

**Diseño del SUB-D:**
```
[Schedule Trigger: Every 6h]
    │
    ▼
[Query Firestore: Get leads to nurture]
    │
    ▼
[Loop through leads]
    │
    ├─> [Determine sequence position]
    ├─> [Generate email content (Gemini)]
    ├─> [Send via Mailersend]
    └─> [Update Firestore]
```

### 10.3 Fase 3: Tracking de Engagement (Semanas 5-6)

**Objetivo:** Saber qué leads abren/clickean emails

**Componentes:**
- 🔄 SUB-E (Engagement Tracker)
- 🔄 Webhook Mailersend `/email-events`
- 🔄 Actualización del Orquestador para enrutar eventos de Mailersend

**Flujo:**
1. Mailersend envía webhook cuando lead abre email
2. Orquestador recibe: `{ "event_type": "email_opened", "lead_id": "...", "campaign_id": "..." }`
3. Router envía a SUB-E
4. SUB-E:
   - Query Firestore: Buscar lead por `lead_id`
   - Actualizar: `email_opens: +1`, `last_engagement: now()`
   - Si es la primera apertura: Incrementar score
   - Si lead pasa de WARM a HOT: Notificar Dr. Carrillo

**Agregar al Orquestador:**

```json
{
  "name": "4E. Execute SUB-E (Engagement)",
  "type": "n8n-nodes-base.executeWorkflow",
  "typeVersion": 1.3,
  "position": [900, 340],
  "parameters": {
    "source": "database",
    "workflowId": {
      "value": "SUB_E_WORKFLOW_ID"
    },
    "workflowInputs": {
      "mappingMode": "defineBelow",
      "value": {
        "lead_id": "={{ $json.original_payload.lead_id }}",
        "event_type": "={{ $json.original_payload.mailersend_event }}",
        "campaign_id": "={{ $json.original_payload.campaign_id }}",
        "timestamp": "={{ $json.original_payload.opened_at }}"
      }
    }
  }
}
```

### 10.4 Fase 4: Agendamiento de Reuniones (Semanas 7-8)

**Objetivo:** Automatizar el proceso cuando un lead agenda reunión con Dr. Carrillo

**Componentes:**
- 🔄 SUB-F (Meeting Scheduler)
- 🔄 Integración Calendly
- 🔄 Webhook Calendly `/booking-events`

**Flujo:**
1. Lead clickea link de Calendly en email
2. Lead agenda reunión
3. Calendly envía webhook al Orquestador
4. Orquestador enruta a SUB-F
5. SUB-F:
   - Query Firestore: Buscar lead por email
   - Actualizar: `status: "reunión_agendada"`, `meeting_date: "..."`, `meeting_link: "..."`
   - Enviar email de recordatorio al lead
   - Notificar Dr. Carrillo por WhatsApp: "Lead HOT [Nombre] agendó reunión [Fecha]"
   - Crear evento en Google Calendar del Dr. Carrillo (opcional)

### 10.5 Fase 5: Analytics y Dashboards (Semanas 9-10)

**Objetivo:** Visualizar métricas del sistema

**Componentes:**
- 🔄 Looker Studio dashboard
- 🔄 Queries complejas en Firestore
- 🔄 Reportes semanales automatizados

**Métricas a trackear:**
- Leads capturados por día/semana/mes
- Tasa de conversión por categoría (HOT/WARM/COLD)
- Tiempo promedio de respuesta
- Open rate de emails
- Click rate de emails
- Tasa de agendamiento de reuniones
- ROI por campaña (utm_campaign)

---

## 11. CONSIDERACIONES FINALES

### 11.1 Costo Operacional Estimado

| Componente | Costo Mensual (300 leads) |
|------------|---------------------------|
| **n8n Cloud** | $0 (Starter tier - hasta 500 ejecuciones/día) |
| **Google Cloud Firestore** | $0 (dentro del tier gratuito) |
| **Gemini API (Google AI Studio)** | ~$4.50 (300 emails × $0.015) |
| **Gmail** | $0 (dentro de límites) |
| **Mailersend** (futuro) | ~$25 (plan Premium 50K emails/mes) |
| **Calendly** (futuro) | $0 (plan gratuito) |
| **TOTAL** | **~$30/mes** |

**Escenario de crecimiento (1000 leads/mes):**
- n8n Cloud: Upgrade a Pro ($50/mes)
- Firestore: ~$10/mes
- Gemini API: ~$15/mes
- Mailersend: ~$45/mes
- **TOTAL: ~$120/mes**

### 11.2 Tiempo de Implementación

**Plan realista (5 horas/semana disponibles):**

| Fase | Duración | Horas Totales |
|------|----------|---------------|
| Fase 1 (MVP) | 2 semanas | 10 horas |
| Fase 2 (Nurturing) | 2 semanas | 10 horas |
| Fase 3 (Tracking) | 2 semanas | 10 horas |
| Fase 4 (Meetings) | 2 semanas | 10 horas |
| Fase 5 (Analytics) | 2 semanas | 10 horas |
| **TOTAL** | **10 semanas** | **50 horas** |

**Plan acelerado (dedicación full-time 1 semana):**
- Fase 1 completa en 1 semana (40 horas)
- Fases 2-5 en las siguientes 4 semanas (10 horas/semana cada una)

### 11.3 Criterios de Éxito

**Semana 2 (MVP):**
- ✅ 100% de leads del formulario se capturan en Firestore
- ✅ 0 errores 500 en últimas 48 horas
- ✅ Tiempo de respuesta < 5 segundos promedio
- ✅ Emails de respuesta automática se envían en <10 segundos

**Mes 1 (Post-lanzamiento):**
- ✅ 90%+ de leads se clasifican correctamente (HOT/WARM/COLD)
- ✅ 100% de leads HOT reciben notificación a Dr. Carrillo
- ✅ Tasa de apertura de emails > 25%

**Mes 3 (Con nurturing):**
- ✅ 50%+ de leads WARM reciben al menos 3 emails de nurturing
- ✅ 10%+ de leads WARM se convierten a HOT
- ✅ 5%+ de leads HOT agendan reunión

---

## ANEXOS

### Anexo A: Glosario de Términos

- **Hub & Spoke:** Arquitectura modular donde un workflow central (hub) distribuye trabajo a workflows especializados (spokes)
- **Orquestador:** Workflow central que recibe eventos y decide qué sub-workflow ejecutar
- **Sub-workflow:** Workflow especializado que ejecuta una función específica
- **Execute Workflow Trigger:** Nodo que permite a un workflow ser llamado por otro workflow
- **Webhook:** Endpoint HTTP que recibe datos de sistemas externos
- **Payload:** Datos enviados en una request HTTP

### Anexo B: Referencias y Recursos

**Documentación Oficial:**
- n8n Docs: https://docs.n8n.io
- Google Firestore: https://firebase.google.com/docs/firestore
- Google Gemini API: https://ai.google.dev/docs
- Mailersend API: https://developers.mailersend.com

**Templates n8n Relevantes:**
- "AI-Powered Employee Database Management via Telegram" (Template #4545)
- "Natural Language Task Management with Todoist" (Template #4186)
- "Route User Requests to Specialized Agents" (Template #4150)

### Anexo C: Contacto y Soporte

**Para asistencia técnica:**
- Documentar el problema con screenshots
- Incluir execution ID de n8n
- Incluir payload que causó el error
- Enviar a: [tu email de soporte]

---

## RESUMEN EJECUTIVO FINAL

Juan, esta guía te proporciona:

1. ✅ **Arquitectura clara:** Hub & Spoke con 6 sub-workflows (sweet spot)
2. ✅ **Código listo para usar:** JSON completo de todos los nodos
3. ✅ **Plan de implementación:** 10 semanas, 50 horas totales
4. ✅ **Testing exhaustivo:** 8 escenarios de prueba documentados
5. ✅ **Roadmap de expansión:** Hasta Fase 5 (Analytics)

**Próximo paso inmediato:**
1. Seguir Fase 1 (secciones 6.1 a 6.4)
2. Validar que SUB-A funciona con Execute Workflow Trigger
3. Crear el Orquestador
4. Ejecutar los 4 tests de la sección 8.2

**Tiempo estimado para MVP funcional:** 2 semanas (10 horas de trabajo efectivo)

¿Listo para empezar? 🚀

