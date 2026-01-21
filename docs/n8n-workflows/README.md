# 🔄 n8n Workflows - Carrillo Abogados

**Última Actualización**: 14 de Enero, 2026  
**Versión**: 1.1.0  
**Estado**: ✅ MW#1 en Producción  
**Fase Proyecto**: FASE 10 - Autenticación Frontend Completa

---

## ⚠️ NOTA: Estructura de Documentación n8n

El proyecto tiene documentación n8n en **DOS ubicaciones**:

| Ubicación | Propósito | Contenido |
|-----------|-----------|-----------|
| `docs/n8n-workflows/` | **Documentación técnica** para desarrolladores | Specs, guías, NODE_STANDARDS |
| `automation/n8n-workflows/` | **Zona de desarrollo n8n** | Workflows JSON, contexto de negocio |

**→ Para desarrollo de workflows n8n, ver: [automation/README.md](../../automation/README.md)**

---

## 📋 Descripción

Esta carpeta contiene todos los workflows de automatización de n8n Cloud para Carrillo Abogados, incluyendo:

- **Workflows JSON** exportados de n8n Cloud (listos para importar)
- **Especificaciones técnicas** de cada workflow
- **Documentación de agentes** especializados
- **Guías técnicas** de integración

---

## 🏗️ Estructura del Proyecto

```
docs/n8n-workflows/
├── README.md                          # Este archivo
├── n8n_mcp_guide.md                   # Guía de Model Context Protocol para n8n
├── NODE_STANDARDS.md                  # Estándares de nodos n8n
│
├── agents/                            # Agentes especializados n8n
│   └── AGENTS.md                      # Índice de 5 agentes (architect, engineer, etc.)
│
└── mega-workflow-1/                   # MW#1: Lead Lifecycle Manager
    ├── STATUS.md                      # Estado actual del MW#1 (PRODUCCIÓN)
    ├── orchestrator/                  # Hub principal
    │   ├── ORQUESTADOR_PRODUCTION_*.json
    │   ├── workflow_spec_v1.md
    │   ├── workflow_diagram.mermaid
    │   ├── nodes_required.md
    │   └── feasibility_analysis.md
    └── spokes/                        # Sub-workflows
        └── sub-a-lead-intake/
            └── SUB-A_PRODUCTION_*.json
```

---

## 🎯 Los 3 MEGA-WORKFLOWS

### MW#1: Lead Lifecycle Manager ✅ ACTIVO
**Propósito**: Lead → Cliente en < 1 minuto  
**Workflows**: 7 sub-workflows  
**Nodos**: ~108 nodos  
**Estado**: En producción desde 21 Dic 2025

| Workflow | ID n8n | Estado |
|----------|--------|--------|
| Orquestador | `bva1Kc1USbbITEAw` | ✅ Activo |
| SUB-A: Lead Intake | `RHj1TAqBazxNFriJ` | ⚪ Triggered |

**Flujo**:
```
[Formulario Web] → [Webhook] → [Orquestador] → [SUB-A] → [Respuesta IA < 1 min]
                                    ↓
                              [Firestore DB]
                                    ↓
                              [Gmail (si HOT)]
```

### MW#2: Client Retention 🔄 Q2 2026
**Propósito**: Cliente → Recompra (Flywheel)  
**Estado**: Planificado

### MW#3: SEO Content Factory 🔄 Q3 2026
**Propósito**: Tráfico → Lead  
**Estado**: Planificado

---

## 🛠️ Cómo Importar Workflows a n8n

### Desde n8n Cloud UI

1. Abrir [n8n Cloud](https://carrilloabgd.app.n8n.cloud)
2. Ir a **Workflows** → **Import from File**
3. Seleccionar el archivo `.json` correspondiente
4. Revisar y activar

### Usando n8n MCP (Model Context Protocol)

```bash
# Desde Claude Code con n8n MCP activado:
> Importa el workflow desde docs/n8n-workflows/mega-workflow-1/orchestrator/ORQUESTADOR_PRODUCTION_2025-12-21.json
```

Ver [n8n_mcp_guide.md](./n8n_mcp_guide.md) para más detalles.

---

## 🤖 Sistema de 5 Agentes n8n

El sistema utiliza **5 agentes especializados** para construir y mantener workflows:

| Agente | Rol | Fase |
|--------|-----|------|
| 🏗️ **Arquitecto** | Diseño y planificación | 1. Diseño |
| 👷 **Ingeniero** | Implementación | 2. Build |
| 🧪 **QA Specialist** | Testing y validación | 3. Test |
| ⚡ **Optimizador** | Performance | 4. Optimización |
| ✅ **Validador** | Deployment | 5. Deploy |

Ver [agents/AGENTS.md](./agents/AGENTS.md) para documentación completa.

---

## 📊 Lead Scoring (MW#1)

El scoring es calculado por IA (Google Gemini 2.5-pro):

| Criterio | Puntos |
|----------|--------|
| Base | 30 pts |
| Servicio "marca" o "litigio" | +20 pts |
| Mensaje > 50 caracteres | +10 pts |
| Tiene teléfono | +10 pts |
| Tiene empresa | +10 pts |
| Email corporativo | +10 pts |
| Cargo C-Level | +20 pts |

**Categorías**:
- 🔥 **HOT** (≥70 pts): Notificación inmediata + respuesta IA
- 🟡 **WARM** (40-69 pts): Respuesta IA automática
- 🔵 **COLD** (<40 pts): Respuesta genérica

---

## 🔗 Integración con Plataforma Web

### Eventos NATS → n8n

| Evento | Webhook n8n | MW |
|--------|-------------|-----|
| `lead.capturado` | `/webhook/lead-events` | MW#1 |
| `cita.agendada` | `/webhook/meeting-events` | MW#1 |
| `caso.cerrado` | `/webhook/case-events` | MW#2 |
| `cliente.inactivo` | `/webhook/client-events` | MW#2 |

### n8n-integration-service

El microservicio `n8n-integration-service` (puerto 8800) actúa como bridge bidireccional:

- **Escucha NATS** → Envía a webhooks n8n
- **Recibe callbacks n8n** → Ejecuta acciones en plataforma

Ver [docs/business/ESTRATEGIA_AUTOMATIZACION.md](../business/ESTRATEGIA_AUTOMATIZACION.md) para arquitectura completa.

---

## 📝 Notas Técnicas Importantes

### ⚠️ Expresiones n8n

```javascript
// ❌ NO usar optional chaining (no soportado)
$json.contact?.email

// ✅ Usar ternarios
$json.contact ? $json.contact.email : ''
```

### ⚠️ Nodo IF

```json
// ✅ Siempre incluir "options" en conditions
{
  "conditions": {
    "options": { "caseSensitive": true, "leftValue": "", "typeValidation": "strict" },
    "conditions": [ ... ]
  }
}
```

### ⚠️ Nodo Gmail

```json
// ✅ Siempre especificar operation
{
  "operation": "send"
}
```

---

## 🔄 Sincronización con MarketingTech

Este contenido fue importado del repositorio externo:
- **Origen**: `C:\GitHub Desktop\MarketingTech`
- **Fecha**: 4 Enero 2026
- **Rama**: `automation` (desarrollo marketing)

Para mantener sincronizado:
1. Ejecutar el **Branch Sync Agent** (`.github/copilot-agents/branch-sync-agent.md`)
2. Revisar cambios en MarketingTech
3. Copiar archivos actualizados a esta carpeta

---

## 📚 Documentos Relacionados

- [ESTRATEGIA_AUTOMATIZACION.md](../business/ESTRATEGIA_AUTOMATIZACION.md) - Estrategia completa
- [ARQUITECTURA_FUNCIONAL.md](../business/ARQUITECTURA_FUNCIONAL.md) - Mapeo a microservicios
- [branch-sync-agent.md](../../.github/copilot-agents/branch-sync-agent.md) - Agente de sincronización

---

**Mantenido por**: Equipo de Desarrollo + Marketing  
**Contacto**: ingenieria@carrilloabgd.com
