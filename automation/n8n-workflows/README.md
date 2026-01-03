# N8N-ANTIGRAVITY 🚀

Sistema multi-agente de IA para desarrollo autónomo de workflows n8n para **Carrillo Abogados**.

---

## 📋 ¿QUÉ ES ESTO?


**Proyecto**: Automatización de marketing legal  
**Cliente**: Carrillo Abogados (PI, Marcas, Contratación Estatal)  
**Stack**: n8n Cloud + GCP + Claude Code + 5 Subagentes IA  
**Objetivo**: Escalar de 20 → 300+ leads/mes

---

## 🗂️ ESTRUCTURA DEL PROYECTO

```
n8n-antigravity/
├─ .claude/                    # Subagentes IA (architect, engineer, qa, optimizer, validator)
├─ 01-setup/                   # Scripts de configuración (vacío, scripts en raíz)
├─ 02-context/                 # ⚡ CONTEXTO DEL PROYECTO
│  ├─ business/               # DOFA, presupuesto, estrategia
│  └─ technical/              # Arquitectura, n8n MCP guide
├─ 03-agents/                  # Documentación de agentes
├─ 04-workflows/               # 🔧 WORKFLOWS EN DESARROLLO
├─ 05-templates/               # Plantillas reutilizables (vacío)
└─ 06-outputs/                 # ✅ WORKFLOWS PRODUCCIÓN (cuando estén listos)
```

---

## 🤖 SUBAGENTES IA

**Ubicación**: `.claude/agents/`

| Agente | Rol | Output |
|--------|-----|--------|
| **architect.md** | Diseña workflows | specs/, diagramas |
| **engineer.md** | Implementa workflows | artifacts/, JSON |
| **qa-specialist.md** | Testing y validación | testing/, reportes |
| **optimizer.md** | Optimiza performance | artifacts/v2 |
| **validator.md** | Deployment producción | 06-outputs/ |

**Activación**:
```
> /agents                           # Ver todos
> Actúa como el subagente architect  # Usar uno específico
```

**Leer más**: `.claude/README.md`

---

## 📚 CONTEXTO DEL PROYECTO

### Información de Negocio

**Ubicación**: `02-context/business/`

- `DOFA, OBJ, MERCADO.pdf` - Análisis DOFA, objetivos, buyer personas
- `Framework estrategico ABGD.pdf` - Framework estratégico completo
- `PRESUPUESTO_MARKETING_2026_FINAL_v2.docx.pdf` - Budget 70.3M COP

### Documentación Técnica

**Ubicación**: `02-context/technical/`

- `n8n_mcp_guide.md` - ⚡ **IMPORTANTE**: Guía de 35+ tools MCP para n8n
- `arquitectura hub spoke hibrida.pdf` - Patrones de diseño workflows
- `Documento Informativo Automatización...pdf` - Automatización con n8n

**⚠️ Los agentes SIEMPRE leen `n8n_mcp_guide.md` antes de trabajar**

---

## 🔧 WORKFLOWS

### En n8n Cloud

**Última verificación**: 2026-01-03 (via MCP)  
**URL**: https://carrilloabgd.app.n8n.cloud  
**Versión**: v1.120.4

| ID | Nombre | Nodos | Estado | Validación |
|----|--------|-------|--------|------------|
| `bva1Kc1USbbITEAw` | **Orquestador** (Lead Lifecycle) | 5 | ⚪ INACTIVO | ❌ 1 error |
| `RHj1TAqBazxNFriJ` | **SUB-A** (Lead Intake v5 - AI) | 10 | ⚪ INACTIVO | ⚠️ 7 warnings |
| `tpCV4mNjRiuOjeM8` | SUB-A PRUEBA (legacy) | 12 | ⚪ INACTIVO | - |
| `3FG9LxOp09FLC5s4` | My workflow (test) | 2 | ⚪ INACTIVO | - |

```bash
# Verificar estado actual con MCP:
> Dame un listado de los workflows que hay en mi instancia de n8n
> Valida el workflow bva1Kc1USbbITEAw
```

**⚠️ ACCIÓN REQUERIDA**: Ver `workflows/MEGA_WORKFLOW_1_LEAD_LIFECYCLE/ACCION_REQUERIDA.md`

### En Desarrollo Local

**Ubicación**: `04-workflows/MEGA_WORKFLOW_1_LEAD_LIFECYCLE/`

```
MEGA_WORKFLOW_1_LEAD_LIFECYCLE/
├─ 01-orchestrator/        # Hub (orquestador)
│  ├─ specs/              # Diseño del arquitecto
│  └─ artifacts/          # Implementación del ingeniero
│
├─ 02-spokes/             # Sub-workflows
│  ├─ sub-a-lead-intake/
│  │  ├─ artifacts/      # Workflow JSON
│  │  └─ testing/        # Test reports
│  │
│  └─ sub-b-hot-lead-notification/
│     ├─ specs/          # Diseño completo
│     └─ artifacts/      # Implementación parcial
│
└─ artifacts/             # Documentación general
```

**⚠️ Para conocer el estado real**: Revisar archivos en estas carpetas

---

## 🚀 CÓMO USAR

### Desarrollo de Nuevo Workflow

```
# 1. Diseñar (Arquitecto)
> Actúa como el subagente architect y diseña un workflow para [objetivo]

# 2. Implementar (Ingeniero)
> Actúa como el subagente engineer e implementa según specs en [ruta]

# 3. Validar (QA)
> Usa el subagente qa-specialist para validar el workflow [ID]

# 4. Optimizar (Optimizer)
> Invoca el subagente optimizer para mejorar el workflow [ID]

# 5. Desplegar (Validator)
> Actúa como el subagente validator y prepara para producción
```

### Trabajar con Workflows Existentes

```
# Listar todos
> Dame un listado de los workflows que hay en mi instancia de n8n

# Ver detalles
> Obtén los detalles del workflow [ID]

# Modificar
> Actúa como engineer y agrega [cambio] al workflow [ID]

# Probar
> Ejecuta el workflow [ID] con estos datos: {...}
```

---

## 📖 GUÍAS Y RECURSOS

### Documentación de Agentes

- **Índice completo**: `03-agents/AGENTS.md`
- **Guía de subagentes**: `.claude/README.md`
- **MCP tools n8n**: `02-context/technical/n8n_mcp_guide.md` (⚡ clave)

### Scripts de Configuración

**Ubicación**: Raíz del proyecto

| Script | Para qué |
|--------|----------|
| `diagnose-vertex-status.ps1` | Diagnóstico Vertex AI |
| `switch-to-anthropic-account.ps1` | Cambiar a cuenta Anthropic |
| `fix-claude-code-lock-error.ps1` | Reparar errores lock |

### Guías Técnicas por Workflow

Cada workflow tiene su propia documentación en su carpeta:
- `specs/workflow_spec.md` - Especificación técnica
- `artifacts/implementation_notes.md` - Notas de implementación
- `testing/test_report_*.md` - Reportes de testing

**No buscar aquí**, ir directamente a la carpeta del workflow.

---

## ⚙️ CONFIGURACIÓN

### n8n MCP Server

**Archivo**: `~/.claude/claude_desktop_config.json`

```json
{
  "mcpServers": {
    "n8n": {
      "command": "npx",
      "args": ["-y", "n8n-mcp"],
      "env": {
        "N8N_API_URL": "https://carrilloabgd.app.n8n.cloud",
        "N8N_API_KEY": "[tu-api-key]"
      }
    }
  }
}
```

### Verificar Configuración

```powershell
# Iniciar Claude Code
cd C:\Automatizaciones\n8n-antigravity
claude

# Status debe mostrar:
# MCP servers: n8n ✔
```

---

## 🎯 CONVENCIONES

### Organización de Archivos

**Durante desarrollo** (work in progress):
- Diseño → `04-workflows/[WORKFLOW]/specs/`
- Implementación → `04-workflows/[WORKFLOW]/artifacts/`
- Testing → `04-workflows/[WORKFLOW]/testing/`

**Listo para producción**:
- Todo se mueve a → `06-outputs/production/[WORKFLOW]/`

### Nomenclatura

- `MEGA_WORKFLOW_X` - Workflows principales
- `SUB-X-nombre` - Sub-workflows (spokes)
- `workflow_spec.md` - Especificación
- `workflow_draft_vX.json` - Workflow JSON
- `test_report_vX.md` - Reporte testing

---

## 🔍 COMANDOS RÁPIDOS

```bash
# Ver subagentes disponibles
/agents

# Listar workflows en n8n
> Dame un listado de workflows

# Leer contexto de negocio
> Lee el archivo 02-context/business/DOFA, OBJ, MERCADO.pdf

# Leer guía técnica MCP
> Lee el archivo 02-context/technical/n8n_mcp_guide.md

# Ver estructura de workflow
> Muéstrame la estructura de 04-workflows/MEGA_WORKFLOW_1_LEAD_LIFECYCLE/
```

---

## ⚠️ IMPORTANTE

### Lo que los Agentes DEBEN leer antes de trabajar:

1. **Este README** - Para saber dónde están las cosas
2. **`02-context/technical/n8n_mcp_guide.md`** - Tools MCP disponibles
3. **`02-context/business/`** - Contexto del cliente y objetivos
4. **Carpeta del workflow específico** - Specs, artifacts, testing

### Lo que NO hacer:

❌ Asumir el estado de workflows sin verificar  
❌ Duplicar información que ya existe en carpetas  
❌ Crear archivos fuera de la estructura establecida  
❌ Mover archivos a `06-outputs/` sin validación completa  

---

## 📞 SOPORTE

- **n8n Community**: https://community.n8n.io
- **Claude Code**: https://code.claude.com/docs
- **n8n MCP**: https://github.com/n8n-io/n8n-mcp

---

**Última actualización**: 2024-12-18  
**Versión**: 1.1.0 (corregida - no asumir, solo referenciar)
