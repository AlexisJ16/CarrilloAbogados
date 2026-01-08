# Agent Protocols & Standards

> [!IMPORTANT]
> This document is the **Law** for all AI Agents working on this project. Follow it strictly to maintain order, clarity, and efficiency.

## 1. Directory Structure

We enforce a strict **Hub & Spoke** folder structure for workflows.

```text
/
├── README.md                # Entry Point
├── docs/                    # Central Documentation
├── workflows/               # ACTIVE n8n Workflows
│   ├── MEGA_WORKFLOW_1.../  # Dedicated folder per Mega Workflow
│   │   ├── 01-orchestrator/ # The Hub
│   │   ├── 02-spokes/       # The Spokes
│   │   ├── artifacts/       # Prompts, schemas, specific docs
│   │   └── testing/         # Test data
│   └── templates/           # Shared templates
└── archive/                 # Deprecated/Reference only
```

## 2. Documentation Standards

### Single Source of Truth
- **Do NOT** duplicate information.
- **Do USE** links and anchors.
- If you change a core concept (e.g., Architecture), update the specific file in `docs/` or `workflows/.../artifacts/`, do not create a new random markdown file.

### Markdown Best Practices
- Start with a clear Title (`#`).
- Use `> [!NOTE]` alerts for important context.
- Keep filenames distinct and descriptive (e.g., `MW1_ORCHESTRATOR_SPEC.md` vs `spec.md`).

## 3. Workflow Management

- **JSON Files**: Never leave loose JSON workflow files in root or random folders. They MUST be in `01-orchestrator`, `02-spokes`, or `templates`.
- **Naming**: Workflows must follow the naming convention: `[MW#]-[SUB#]-[Name]`.

## 4. Maintenance

- **Clean up**: If you create a temp file, delete it.
- **Archive**: If a document is obsolete, move it to `archive/` (don't just delete it if it has historical value).
