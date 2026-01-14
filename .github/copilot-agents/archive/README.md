# 📦 Archived Agents

**Fecha de Archivo**: 12 de Enero, 2026

---

## Propósito

Esta carpeta contiene agentes que fueron **archivados** por una de las siguientes razones:

1. **Dependencia de herramientas no disponibles** (MCP tools de n8n)
2. **Consolidación** (fusionados con otros agentes)
3. **Bajo valor para IA** (procedimientos manuales)

---

## Agentes Archivados

### Por Dependencia de MCP Tools n8n

Estos agentes requieren herramientas MCP de n8n que actualmente no están disponibles en el entorno:

| Agente | Herramientas Requeridas |
|--------|------------------------|
| `orchestrator.md` | `mcp_n8n_n8n_list_workflows`, `mcp_n8n_n8n_validate_workflow` |
| `architect.md` | `mcp__n8n__list_nodes`, `mcp__n8n__validate_node` |
| `engineer.md` | `n8n:n8n_create_workflow`, `n8n:n8n_update_workflow` |
| `qa-specialist.md` | `mcp_n8n_n8n_test_workflow`, `mcp_n8n_n8n_get_executions` |
| `optimizer.md` | `mcp_n8n_n8n_analyze_performance` |
| `validator.md` | `mcp_n8n_n8n_export_workflow`, `mcp_n8n_n8n_import_workflow` |

**Reactivación**: Cuando las herramientas MCP de n8n estén disponibles, estos agentes pueden moverse de vuelta a la carpeta principal.

---

### Por Consolidación

Estos agentes fueron fusionados en nuevos agentes más completos:

| Agente Original | Nuevo Agente | Razón |
|-----------------|--------------|-------|
| `testing-agent.md` | `testing-qa-agent.md` | Funciones superpuestas con qa-quality-agent |
| `qa-quality-agent.md` | `testing-qa-agent.md` | Ambos cubrían tests y calidad |
| `project-manager-agent.md` | `project-context-agent.md` | Ambos manejaban estado del proyecto |
| `business-product-agent.md` | `project-context-agent.md` | Contexto de negocio integrado |

---

### Por Bajo Valor para IA

| Agente | Razón |
|--------|-------|
| `branch-sync-agent.md` | Contiene procedimientos manuales de git que no requieren asistencia de IA |

---

## ¿Cómo Reactivar un Agente?

1. Verificar que las herramientas requeridas estén disponibles
2. Mover el archivo a la carpeta padre (`..`)
3. Actualizar `INDEX.md` para incluirlo en la lista de agentes activos

```powershell
# Ejemplo: Reactivar orchestrator.md
Move-Item -Path "orchestrator.md" -Destination "..\orchestrator.md"
```

---

*Documentación de archivo - Carrillo Abogados Legal Tech*
