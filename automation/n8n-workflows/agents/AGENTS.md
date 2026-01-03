# ÍNDICE DE AGENTES ESPECIALIZADOS
## Sistema Multi-Agente para Construcción de Workflows n8n

**Versión**: 1.0  
**Sistema**: n8n Antigravity Agent Ecosystem  
**Cliente**: Carrillo Abogados

---

## 🎭 ROLES Y RESPONSABILIDADES

Este sistema utiliza 5 agentes especializados que trabajan de manera secuencial o paralela según la fase del proyecto.

---

## 📋 LISTA DE AGENTES

### 1. ARQUITECTO (Planner)
**Carpeta**: `./architect/`  
**Fase**: Diseño y Planificación  
**Prioridad**: Alta  

**Responsabilidades**:
- Análisis de requisitos del negocio
- Diseño de arquitectura Hub & Spoke
- Selección de nodos apropiados
- Generación de especificaciones técnicas
- Validación de viabilidad

**Output Principal**:
- `workflow_spec.md` - Especificación completa
- `workflow_diagram.mermaid` - Diagrama visual
- `nodes_required.md` - Lista de nodos necesarios
- `feasibility_analysis.md` - Análisis de viabilidad

**Tools Principales**:
- `n8n:search_nodes`
- `n8n:get_node`
- `n8n:search_templates`
- `n8n:get_template`

**Cuándo Activar**:
- Inicio de nuevo proyecto
- Refactorización mayor
- Cambio significativo de requisitos

---

### 2. INGENIERO (Builder)
**Carpeta**: `./engineer/`  
**Fase**: Implementación  
**Prioridad**: Alta  

**Responsabilidades**:
- Implementación de workflows según spec
- Construcción de nodos y conexiones
- Desarrollo de código JavaScript/Python
- Configuración de credenciales
- Integración de servicios externos

**Output Principal**:
- `workflow.json` - Workflow completo
- `code_snippets/` - Código JavaScript/Python
- `configurations.md` - Configuraciones detalladas
- `integration_notes.md` - Notas de integración

**Tools Principales**:
- `n8n:validate_node`
- `n8n:n8n_create_workflow`
- `n8n:n8n_update_partial_workflow`
- `n8n:validate_workflow`

**Cuándo Activar**:
- Después de aprobación de spec del Arquitecto
- Para modificaciones técnicas
- Desarrollo de nuevas features

---

### 3. QA SPECIALIST (Reviewer)
**Carpeta**: `./qa_specialist/`  
**Fase**: Testing y Validación  
**Prioridad**: Crítica  

**Responsabilidades**:
- Testing exhaustivo de workflows
- Validación de estructura y conexiones
- Verificación de error handling
- Pruebas de integración
- Generación de reportes de bugs

**Output Principal**:
- `test_report.md` - Reporte completo de tests
- `bug_report.md` - Bugs encontrados
- `validation_results.json` - Resultados de validación
- `recommendations.md` - Recomendaciones

**Tools Principales**:
- `n8n:validate_workflow`
- `n8n:n8n_validate_workflow`
- `n8n:n8n_test_workflow`
- `n8n:n8n_executions`

**Cuándo Activar**:
- Después de implementación del Ingeniero
- Antes de despliegue a producción
- Después de cada cambio significativo

---

### 4. OPTIMIZADOR (Performance)
**Carpeta**: `./optimizer/`  
**Fase**: Optimización  
**Prioridad**: Media  

**Responsabilidades**:
- Análisis de performance
- Aplicación de auto-fixes
- Optimización de expresiones
- Refactoring de código
- Mejoras de eficiencia

**Output Principal**:
- `optimization_report.md` - Reporte de optimizaciones
- `performance_analysis.md` - Análisis de performance
- `fixes_applied.md` - Fixes aplicados
- `workflow_improved.json` - Versión mejorada

**Tools Principales**:
- `n8n:n8n_autofix_workflow`
- `n8n:validate_workflow`
- `n8n:n8n_update_partial_workflow`

**Cuándo Activar**:
- Después de validación QA
- Cuando hay issues de performance
- Para mejora continua

---

### 5. VALIDADOR (Deployment)
**Carpeta**: `./validator/`  
**Fase**: Despliegue y Documentación  
**Prioridad**: Crítica  

**Responsabilidades**:
- Validación final pre-producción
- Generación de documentación completa
- Creación de deployment plan
- Configuración de versioning
- Monitoreo post-despliegue

**Output Principal**:
- `deployment_plan.md` - Plan de despliegue
- `final_documentation.md` - Documentación completa
- `deployment_checklist.md` - Checklist verificado
- `version_notes.md` - Notas de versión

**Tools Principales**:
- `n8n:n8n_validate_workflow`
- `n8n:n8n_deploy_template`
- `n8n:n8n_workflow_versions`
- `n8n:n8n_test_workflow`

**Cuándo Activar**:
- Después de optimización
- Antes de deploy a producción
- Para rollback de versiones

---

## 🔄 FLUJO DE TRABAJO RECOMENDADO

### Secuencial (Waterfall)

Para proyectos nuevos o cambios mayores:

```
1. ARQUITECTO
   ↓ (genera spec)
2. INGENIERO
   ↓ (implementa)
3. QA SPECIALIST
   ↓ (valida)
4. OPTIMIZADOR
   ↓ (optimiza)
5. VALIDADOR
   ↓ (despliega)
```

### Paralelo (Agile)

Para iteraciones rápidas:

```
ARQUITECTO → genera spec
    ↓
    ├── INGENIERO (implementa Spoke A)
    │       ↓
    │   QA SPECIALIST → valida
    │       ↓
    │   OPTIMIZADOR → optimiza
    │
    ├── INGENIERO (implementa Spoke B)
    │       ↓
    │   QA SPECIALIST → valida
    │       ↓
    │   OPTIMIZADOR → optimiza
    │
    └── ... (más spokes en paralelo)
    
    ↓ (cuando todos listos)
VALIDADOR → integra y despliega todo
```

---

## 📁 ESTRUCTURA DE CARPETAS DE AGENTES

Cada agente tiene su propia carpeta con:

```
agente/
├── AGENT_PROMPT.md          # Prompt completo del agente
├── TOOLS_GUIDE.md           # Guía de tools específicos
├── EXAMPLES.md              # Ejemplos de uso
├── TEMPLATES/               # Templates específicos
│   ├── output_template.md
│   └── report_template.md
└── RESOURCES/               # Recursos adicionales
    ├── best_practices.md
    └── common_patterns.md
```

---

## 🚀 GUÍA DE ACTIVACIÓN RÁPIDA

### Para Claude Code (Terminal)

```bash
# 1. Navegar al proyecto
cd C:\Automatizaciones\n8n-antigravity

# 2. Activar agente específico
# Reemplaza [AGENTE] con: architect, engineer, qa_specialist, optimizer, validator

# Ejemplo: Activar Arquitecto
cat ./03-agents/architect/AGENT_PROMPT.md

# 3. Proporcionar contexto del proyecto
cat ./02-context/business/strategic_goals.md
cat ./02-context/technical/architecture.md

# 4. Ejecutar tarea
# [El agente leerá su prompt y ejecutará su rol]
```

### Para Google Antigravity (Agent Manager)

```
1. Crear nuevo Workspace
2. Agregar estos files al contexto:
   - ./CLAUDE_CODE_INSTRUCTIONS.md
   - ./03-agents/[AGENTE]/AGENT_PROMPT.md
   - ./02-context/* (todos los archivos)
3. Prompt inicial: "Actúa como Agente [NOMBRE] y [TAREA]"
4. El agente trabajará de forma autónoma
```

---

## 🔐 PERMISOS Y ACCESOS

### Agentes con Acceso de Escritura a n8n

- ✅ INGENIERO: Puede crear/modificar workflows
- ✅ OPTIMIZADOR: Puede aplicar fixes automáticos
- ✅ VALIDADOR: Puede desplegar a producción

### Agentes Solo Lectura

- 👁️ ARQUITECTO: Solo consulta y diseña
- 👁️ QA SPECIALIST: Solo valida y reporta

---

## 📊 MÉTRICAS DE ÉXITO

### ARQUITECTO
- ✅ Spec completa y clara
- ✅ Diagrama visual generado
- ✅ Viabilidad confirmada

### INGENIERO
- ✅ Workflow funcional creado
- ✅ Todos los nodos validados
- ✅ Código limpio y documentado

### QA SPECIALIST
- ✅ 0 errores críticos
- ✅ Tests pasados 100%
- ✅ Reporte detallado generado

### OPTIMIZADOR
- ✅ Performance mejorada >20%
- ✅ Expresiones optimizadas
- ✅ Código refactorizado

### VALIDADOR
- ✅ Workflow desplegado exitosamente
- ✅ Documentación completa
- ✅ Versión registrada

---

## 🔄 CICLO DE MEJORA CONTINUA

Después de cada proyecto:

1. **Retrospectiva**: ¿Qué funcionó bien?
2. **Lecciones Aprendidas**: Documentar en `./RESOURCES/lessons_learned.md`
3. **Templates Actualizados**: Mejorar templates basados en experiencia
4. **Best Practices**: Actualizar guías de mejores prácticas

---

## 📞 SOPORTE

Para issues con agentes específicos:

- **Problemas de Diseño** → Revisar carpeta `./architect/`
- **Errores de Implementación** → Revisar carpeta `./engineer/`
- **Fallas de Validación** → Revisar carpeta `./qa_specialist/`
- **Issues de Performance** → Revisar carpeta `./optimizer/`
- **Problemas de Despliegue** → Revisar carpeta `./validator/`

---

## 📄 DOCUMENTACIÓN RELACIONADA

- [README Principal](../README.md)
- [Instrucciones Claude Code](../CLAUDE_CODE_INSTRUCTIONS.md)
- [Guía n8n MCP](../02-context/technical/n8n_mcp_guide.md)
- [Arquitectura Hub & Spoke](../02-context/technical/architecture.md)

---

**VERSIÓN**: 1.0  
**ÚLTIMA ACTUALIZACIÓN**: Diciembre 16, 2024  
**PRÓXIMA REVISIÓN**: Enero 2026
