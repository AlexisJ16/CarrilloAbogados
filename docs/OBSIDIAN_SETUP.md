# 📝 Guía para Obsidian - Carrillo Abogados

**Última Actualización**: 14 de Febrero, 2026

Esta carpeta `docs/` está diseñada para ser compatible con Obsidian como vault de documentación.

## 🔧 Configuración Recomendada

### Abrir como Vault
1. Abre Obsidian
2. Selecciona "Abrir carpeta como vault"
3. Navega a `CarrilloAbogados/docs/`
4. Abre la carpeta

### Plugins Recomendados
- **Obsidian Git**: Sincronización automática con GitHub
- **Dataview**: Queries sobre documentación
- **Tasks**: Gestión de tareas pendientes
- **Kanban**: Tableros de proyecto
- **Mermaid**: Diagramas (ya soportado nativamente)

## 📁 Estructura de Carpetas

```
docs/
├── architecture/    # 🏗️ ADRs y decisiones arquitectónicas
├── business/        # 💼 Modelo de negocio, requerimientos, MVP
├── development/     # 💻 Guías de desarrollo, features, tests
├── operations/      # ⚙️ Deployment, CI/CD, observabilidad
├── security/        # 🔒 Políticas de seguridad
├── archive/         # 📦 Documentos históricos
└── MIGRATION_GUIDE_ORG.md  # 🚀 Guía migración a org
```

## 🏷️ Tags Sugeridos

Usa estos tags para organizar notas:

- `#arquitectura` - Decisiones de diseño
- `#api` - Endpoints y contratos
- `#deployment` - Configuración Kubernetes
- `#seguridad` - Políticas y auditoría
- `#sprint` - Notas de desarrollo
- `#decision-ia` - Decisiones tomadas con IAs
- `#pendiente` - Tareas por completar

## 📋 Templates

### Nueva Decisión Arquitectónica
```markdown
# ADR-XXX: [Título]

**Fecha**: {{date}}
**Estado**: Propuesta | Aceptada | Deprecada

## Contexto
[Describir el problema]

## Decisión
[Describir la solución elegida]

## Consecuencias
- Positivas: ...
- Negativas: ...
```

### Nota de Reunión con IA
```markdown
# 📝 Sesión {{date}}

**IA**: Claude Code | Copilot | Claude Desktop
**Duración**: X horas

## Objetivos
- [ ] Objetivo 1
- [ ] Objetivo 2

## Logros
- Logro 1
- Logro 2

## Decisiones Tomadas
1. [Decisión]

## Próximos Pasos
- [ ] Tarea 1
- [ ] Tarea 2
```

## 🔗 Graph View

Para visualizar conexiones entre documentos:
1. Usa `[[nombre-doc]]` para crear links internos
2. Abre Graph View con `Ctrl+G`
3. Filtra por carpeta o tag

## ⚡ Comandos Rápidos

| Comando | Descripción |
|---------|-------------|
| `Ctrl+O` | Buscar documento |
| `Ctrl+P` | Paleta de comandos |
| `Ctrl+E` | Toggle editor/preview |
| `Ctrl+G` | Graph view |
| `Ctrl+Shift+F` | Buscar en todos los docs |

---

*Esta guía forma parte del proyecto Carrillo Abogados Legal Tech*
