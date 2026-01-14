# 🤖 Carrillo Abogados Automation

> [!IMPORTANT]
> This is the root directory for the Carrillo Abogados Automation Project. All agents and developers must start here.

**Last Updated**: January 2026

---

## 📂 Project Structure

This project follows a strict Hub & Spoke architecture.

- **[docs/](docs/)**: Central documentation.
    - [**00_INDEX.md**](docs/00_INDEX.md): Start here to find everything.
    - [**01_AGENT_PROTOCOLS.md**](docs/01_AGENT_PROTOCOLS.md): **MANDATORY** reading for AI Agents.
    - `business/`: Strategic documents.
    - `technical/`: Technical specifications.

- **[workflows/](workflows/)**: Active n8n Workflows.
    - `MEGA_WORKFLOW_1_LEAD_LIFECYCLE/`: Lead Capture & Management.
        - `01-orchestrator/`
        - `02-spokes/`
    - `templates/`: Shared templates.

- **[archive/](archive/)**: Deprecated or reference files.

## 🚀 Quick Start

1. **Read the Protocols**: [Agent Protocols](docs/01_AGENT_PROTOCOLS.md)
2. **Explore Workflows**: Go to `workflows/` to find specific Mega Workflows.
3. **Check Status**: See `workflows/MEGA_WORKFLOW_.../STATUS.md` for active development status.

---
*Managed by n8n-antigravity Agents*
