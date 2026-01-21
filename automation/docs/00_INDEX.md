# Documentation Index

> Welcome to the central documentation hub for the Carrillo Abogados Automation Project.

**Last Updated**: January 10, 2026

---

## 🔑 Key Documents

### Core Standards
| Document | Description |
|----------|-------------|
| [**AGENT PROTOCOLS**](01_AGENT_PROTOCOLS.md) | 🛑 **READ FIRST**. Rules for all agents |
| [README](../README.md) | Project entry point |

### Business Context
| Document | Description |
|----------|-------------|
| [WEB_INTEGRATION](business/WEB_INTEGRATION.md) | Web ↔ n8n integration specs |
| [DOFA, OBJ, MERCADO](business/) | Strategic business docs (PDF) |

### Technical Specs
| Document | Description |
|----------|-------------|
| [n8n MCP Guide](technical/n8n_mcp_guide.md) | How to use n8n MCP tools |
| [NODE_STANDARDS](technical/NODE_STANDARDS.md) | n8n node coding standards |
| [BACKEND_DEV_TASKS](technical/BACKEND_DEV_TASKS.md) | Backend integration tasks |

### Architecture (per MW)
| Document | Description |
|----------|-------------|
| [00_ARQUITECTURA_GENERAL](technical/arquitectura/00_ARQUITECTURA_GENERAL.md) | High-level system design |
| [01_MEGA_WORKFLOW_1](technical/arquitectura/01_MEGA_WORKFLOW_1.md) | MW1 Lead Lifecycle architecture |
| [02_MEGA_WORKFLOW_2](technical/arquitectura/02_MEGA_WORKFLOW_2.md) | MW2 Retention (future) |
| [03_MEGA_WORKFLOW_3](technical/arquitectura/03_MEGA_WORKFLOW_3.md) | MW3 SEO Content (future) |

---

## 📂 Workflows

### MW1: Lead Lifecycle
| Resource | Location |
|----------|----------|
| **Status & Progress** | [STATUS.md](../workflows/MW1_LEAD_LIFECYCLE/STATUS.md) |
| **Orchestrator** | [01-orchestrator/](../workflows/MW1_LEAD_LIFECYCLE/01-orchestrator/) |
| **Spokes** | [02-spokes/](../workflows/MW1_LEAD_LIFECYCLE/02-spokes/) |

---

## 🗄️ Archive

Deprecated documents are in [archive/deprecated_mds/](../archive/deprecated_mds/).

---

## 📋 Quick Reference

### Where to put things

| Content Type | Location |
|--------------|----------|
| Workflow JSON | `workflows/MW#/01-orchestrator/` or `02-spokes/sub-X/` |
| Status updates | `workflows/MW#/STATUS.md` |
| Architecture docs | `docs/technical/arquitectura/` |
| Business docs | `docs/business/` |
| Technical guides | `docs/technical/` |
| Obsolete files | `archive/deprecated_mds/` |
