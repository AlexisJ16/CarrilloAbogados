# Agent Protocols & Standards

> [!IMPORTANT]
> This document is the **Law** for all AI Agents working on this project. Follow it strictly to maintain order, clarity, and efficiency.

**Last Updated**: January 10, 2026

---

## 1. Directory Structure

We enforce a strict **Hub & Spoke** folder structure for workflows.

```text
automation/
├── README.md                     # Entry Point
├── docs/                         # Central Documentation
│   ├── 00_INDEX.md              # Documentation index
│   ├── 01_AGENT_PROTOCOLS.md    # This file
│   ├── business/                # Business context (PDFs, strategies)
│   └── technical/               # Technical specs (MCP guide, standards)
│       └── arquitectura/        # MW architecture docs (1 per MW)
├── agents/                       # Agent definitions (future)
├── workflows/                    # ACTIVE n8n Workflows
│   ├── MW1_LEAD_LIFECYCLE/      # Mega Workflow 1
│   │   ├── STATUS.md            # ← ONLY status file allowed
│   │   ├── 01-orchestrator/     # The Hub (JSON only)
│   │   └── 02-spokes/           # The Spokes
│   │       └── sub-X-name/      # Each spoke
│   │           ├── WORKFLOW.json
│   │           └── test-data/   # Test data (JSON only)
│   └── templates/               # Shared templates
└── archive/                     # Deprecated/Reference only
    └── deprecated_mds/          # Old markdown files
```

---

## 2. Documentation Standards

### 🚨 CRITICAL: No .md Proliferation

| ✅ ALLOWED | ❌ FORBIDDEN |
|------------|--------------|
| 1 STATUS.md per MW folder | ACCION_REQUERIDA.md |
| Docs in `docs/` folder | HANDOFF_*.md |
| Architecture in `docs/technical/arquitectura/` | RESUMEN_*.md |
| | CLAUDE.md in subfolders |
| | WIREFRAME_*.md |
| | QA_REPORT.md, TEST_RESULTS.md |

### Single Source of Truth
- **Do NOT** duplicate information.
- **Do NOT** create new .md files in workflow folders.
- **Do USE** links and anchors.
- If you need to document something, update `STATUS.md` or the appropriate file in `docs/`.

### Markdown Best Practices
- Start with a clear Title (`#`).
- Use `> [!NOTE]` alerts for important context.
- Keep filenames distinct and descriptive.

---

## 3. Workflow Management

### File Structure per Spoke
```text
sub-X-name/
├── WORKFLOW.json      # The workflow (required)
└── test-data/         # Test data folder (optional)
    └── sample.json
```

### Rules
- **JSON Files**: Must be in `01-orchestrator/`, `02-spokes/sub-X/`, or `templates/`.
- **No loose files**: Never leave JSON or MD files in random folders.
- **Naming**: Use descriptive names: `SUB-A_LEAD_INTAKE.json`, not `workflow.json`.
- **Code snippets**: Go INSIDE the workflow JSON, not in separate files.

---

## 4. Maintenance

- **Clean up**: If you create a temp file, delete it immediately after use.
- **Archive**: Move obsolete documents to `archive/deprecated_mds/`.
- **No empty folders**: Delete folders that become empty after reorganization.
- **STATUS.md only**: All status updates, action items, and notes go in the MW's STATUS.md.

---

## 5. Agent Behavior

When working on automation tasks:

1. **Before creating a file**: Check if it already exists or can be added to STATUS.md
2. **Before creating a folder**: Check if the structure already exists
3. **After completing work**: Clean up any temp files created
4. **Documentation updates**: Go to `docs/` folder, not workflow folders
