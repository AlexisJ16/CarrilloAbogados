# 📚 Índice Maestro de Documentación - Carrillo Abogados Legal Tech

**Última actualización**: 11 de Diciembre, 2025  
**Versión del proyecto**: 0.1.0

---

## 🎯 Propósito de Este Documento

Este índice proporciona navegación rápida a toda la documentación del proyecto, organizada por categorías y prioridades.

---

## 📋 DOCUMENTACIÓN PRINCIPAL (RAÍZ)

### Esenciales ⭐⭐⭐

| Documento | Descripción | Audiencia | Estado |
|-----------|-------------|-----------|--------|
| [README.md](../README.md) | Overview del proyecto, quick start | Todos | ✅ Completo |
| [CLAUDE.md](../CLAUDE.md) | Contexto completo para Claude Code | Claude Code | ✅ Completo |
| [.github/copilot-instructions.md](../.github/copilot-instructions.md) | Guía para GitHub Copilot | Copilot | ✅ Completo |

### Estado del Proyecto

| Documento | Descripción | Última Actualización |
|-----------|-------------|---------------------|
| [PROYECTO_ESTADO.md](../PROYECTO_ESTADO.md) | Estado histórico (30-Nov-2024) | ⚠️ Desactualizado |
| [AUDITORIA_DEPURACION_REPORTE.md](AUDITORIA_DEPURACION_REPORTE.md) | Auditoría y correcciones (11-Dic-2025) | ✅ Actualizado |

---

## 📊 ANÁLISIS Y PLANIFICACIÓN

### Documentos Maestros

| Documento | Propósito | Estado |
|-----------|-----------|--------|
| [RESUMEN_EJECUTIVO.md](RESUMEN_EJECUTIVO.md) | Resumen completo del análisis y plan | ✅ Completo |
| [DOCUMENTACION_ANALISIS.md](DOCUMENTACION_ANALISIS.md) | Análisis exhaustivo de gaps documentales | ✅ Completo |
| [PLAN_TRABAJO.md](PLAN_TRABAJO.md) | Plan de 17 días hasta MVP | ✅ Completo |

---

## 🏢 DOCUMENTACIÓN DE NEGOCIO

**Ubicación**: `docs/business/`  
**Estado General**: 🔴 Pendiente de creación

| Documento | Descripción | Prioridad | Estado |
|-----------|-------------|-----------|--------|
| MODELO_NEGOCIO.md | Descripción del bufete y servicios | 🔴 Crítica | 📋 Planificado |
| PROCESOS_LEGALES.md | Flujos de trabajo del bufete | 🔴 Crítica | 📋 Planificado |
| CASOS_USO.md | Casos de uso por rol | 🔴 Crítica | 📋 Planificado |
| ENTIDADES_DOMINIO.md | Modelo de dominio legal | 🟡 Alta | 📋 Planificado |
| GLOSARIO.md | Términos legales | 🟢 Media | 📋 Planificado |

**Fecha estimada de completitud**: 12-Dic-2025

---

## 🔌 DOCUMENTACIÓN DE APIs

**Ubicación**: `docs/api/`  
**Estado General**: 🟡 Básica (solo .gitkeep)

| Documento | Descripción | Prioridad | Estado |
|-----------|-------------|-----------|--------|
| API_REFERENCE.md | Referencia completa de APIs REST | 🟡 Alta | 📋 Planificado |
| ENDPOINTS_BY_SERVICE.md | Endpoints detallados por microservicio | 🟡 Alta | 📋 Planificado |
| EVENTS_NATS.md | Topología de eventos y mensajería | 🟡 Alta | 📋 Planificado |
| GOOGLE_APIS.md | Integración Google Calendar/Gmail | 🟡 Alta | 📋 Planificado |
| N8N_INTEGRATION.md | Workflows y webhooks N8N | 🟢 Media | 📋 Planificado |
| AUTHENTICATION.md | OAuth2 y seguridad | 🟢 Media | 📋 Planificado |

**Fecha estimada de completitud**: 13-Dic-2025

---

## 🏗️ ARQUITECTURA

**Ubicación**: `docs/architecture/`  
**Estado General**: ✅ Buena (60% completo)

| Documento | Descripción | Estado |
|-----------|-------------|--------|
| [ARCHITECTURE.md](architecture/ARCHITECTURE.md) | Overview de arquitectura | ✅ Completo |
| [ADR-005-database-strategy.md](architecture/ADR-005-database-strategy.md) | Decisión: PostgreSQL compartida | ✅ Completo |
| ADR-006-messaging-strategy.md | Decisión: NATS vs Pub/Sub | 📋 Planificado |
| ADR-007-authentication.md | Decisión: OAuth2 Google Workspace | 📋 Planificado |
| DIAGRAMS/ | Diagramas Mermaid de arquitectura | 📋 Planificado |

**Fecha estimada de completitud**: 13-Dic-2025

---

## 💻 DESARROLLO

**Ubicación**: `docs/development/`  
**Estado General**: 🔴 Pendiente de creación

| Documento | Descripción | Prioridad | Estado |
|-----------|-------------|-----------|--------|
| CHANGELOG.md | Registro de cambios versionado | 🔴 Crítica | 📋 Planificado |
| DECISIONES_IA.md | Log de decisiones con Claude/Copilot | 🔴 Crítica | 📋 Planificado |
| CONTRIBUTING.md | Guía de contribución | 🟡 Alta | 📋 Planificado |
| CODING_STANDARDS.md | Estándares de código Java/Spring | 🟡 Alta | 📋 Planificado |
| TESTING_GUIDE.md | Estrategia de testing | 🟡 Alta | 📋 Planificado |
| SPRINT_LOG.md | Registro de sprints | 🟢 Media | 📋 Planificado |

**Fecha estimada de completitud**: 12-13-Dic-2025

---

## 🔧 OPERACIONES

**Ubicación**: `docs/operations/`  
**Estado General**: ✅ Buena (50% completo)

| Documento | Descripción | Estado |
|-----------|-------------|--------|
| [OPERATIONS.md](operations/OPERATIONS.md) | Guía operacional | ✅ Completo |
| [OPS_README.md](OPS_README.md) | Scripts y workflows | ✅ Completo |
| [DEPLOYMENT_CHECKLIST.md](DEPLOYMENT_CHECKLIST.md) | Checklist de deployment | ⚠️ Desactualizado |
| MONITORING.md | Prometheus/Grafana setup | 📋 Planificado |
| DISASTER_RECOVERY.md | Backup y recovery | 📋 Planificado |
| RUNBOOKS/ | Procedimientos operacionales | 📋 Planificado |

**Fecha estimada de completitud**: 13-Dic-2025

---

## 🔒 SEGURIDAD

**Ubicación**: `docs/security/`  
**Estado General**: 🔴 No existe

| Documento | Descripción | Prioridad | Estado |
|-----------|-------------|-----------|--------|
| SECURITY_POLICY.md | Política de seguridad | 🟡 Alta | 📋 Planificado |
| COMPLIANCE.md | GDPR y compliance legal | 🟡 Alta | 📋 Planificado |
| AUDIT_LOG.md | Trazabilidad legal | 🟢 Media | 📋 Planificado |
| VULNERABILITY_MANAGEMENT.md | Gestión de vulnerabilidades | 🟢 Media | 📋 Planificado |

**Fecha estimada de completitud**: 13-Dic-2025

---

## 📁 GESTIÓN DE PROYECTO

**Ubicación**: `docs/project/`  
**Estado General**: 🔴 No existe

| Documento | Descripción | Prioridad | Estado |
|-----------|-------------|-----------|--------|
| ROADMAP.md | Roadmap hasta MVP | 🔴 Crítica | 📋 Planificado |
| MILESTONES.md | Hitos y entregas | 🔴 Crítica | 📋 Planificado |
| REQUIREMENTS.md | Requisitos del proyecto | 🟡 Alta | 📋 Planificado |
| ACADEMIC_DELIVERABLES.md | Entregables académicos | 🟡 Alta | 📋 Planificado |

**Fecha estimada de completitud**: 12-Dic-2025

---

## 📊 MÉTRICAS DE COMPLETITUD

### Por Categoría

| Categoría | Docs Existentes | Docs Requeridos | % Completitud | Prioridad |
|-----------|----------------|-----------------|---------------|-----------|
| **Principal** | 3/3 | 3/3 | 100% | ✅ |
| **Negocio** | 0/5 | 5/5 | 0% | 🔴 |
| **API** | 0/6 | 6/6 | 0% | 🟡 |
| **Arquitectura** | 2/5 | 5/5 | 40% | 🟡 |
| **Desarrollo** | 0/6 | 6/6 | 0% | 🔴 |
| **Operaciones** | 3/6 | 6/6 | 50% | 🟢 |
| **Seguridad** | 0/4 | 4/4 | 0% | 🟡 |
| **Proyecto** | 0/4 | 4/4 | 0% | 🔴 |

### General

- **Documentos existentes**: 8 / 39
- **Completitud total**: 21%
- **Meta Fase 0**: 90%+
- **Meta Final**: 100%

---

## 🎯 PRIORIDADES DE CREACIÓN

### 🔴 FASE 0 - DÍA 1 (11-Dic) - CRÍTICO

1. business/MODELO_NEGOCIO.md
2. business/PROCESOS_LEGALES.md
3. business/CASOS_USO.md
4. development/CHANGELOG.md
5. development/DECISIONES_IA.md
6. project/ROADMAP.md
7. project/MILESTONES.md

### 🟡 FASE 0 - DÍA 2 (12-Dic) - ALTO

8. api/API_REFERENCE.md
9. api/ENDPOINTS_BY_SERVICE.md
10. api/EVENTS_NATS.md
11. api/GOOGLE_APIS.md
12. api/N8N_INTEGRATION.md
13. development/CONTRIBUTING.md
14. development/CODING_STANDARDS.md
15. development/TESTING_GUIDE.md

### 🟢 FASE 0 - DÍA 3 (13-Dic) - MEDIO

16. security/SECURITY_POLICY.md
17. security/COMPLIANCE.md
18. security/AUDIT_LOG.md
19. architecture/ADR-006-messaging-strategy.md
20. architecture/ADR-007-authentication.md
21. architecture/DIAGRAMS/
22. operations/MONITORING.md
23. operations/DISASTER_RECOVERY.md

---

## 🔍 NAVEGACIÓN RÁPIDA

### Por Audiencia

**Para Desarrolladores**:
- [CLAUDE.md](../CLAUDE.md) - Contexto completo
- [.github/copilot-instructions.md](../.github/copilot-instructions.md) - Guía AI
- [development/CONTRIBUTING.md](development/CONTRIBUTING.md) - (Pendiente)
- [development/CODING_STANDARDS.md](development/CODING_STANDARDS.md) - (Pendiente)
- [api/API_REFERENCE.md](api/API_REFERENCE.md) - (Pendiente)

**Para DevOps**:
- [OPS_README.md](OPS_README.md) - Scripts y workflows
- [operations/OPERATIONS.md](operations/OPERATIONS.md) - Guía operacional
- [DEPLOYMENT_CHECKLIST.md](DEPLOYMENT_CHECKLIST.md) - Checklist

**Para Product Owner**:
- [business/MODELO_NEGOCIO.md](business/MODELO_NEGOCIO.md) - (Pendiente)
- [business/CASOS_USO.md](business/CASOS_USO.md) - (Pendiente)
- [project/ROADMAP.md](project/ROADMAP.md) - (Pendiente)
- [project/MILESTONES.md](project/MILESTONES.md) - (Pendiente)

**Para Arquitectos**:
- [architecture/ARCHITECTURE.md](architecture/ARCHITECTURE.md) - Overview
- [architecture/ADR-005-database-strategy.md](architecture/ADR-005-database-strategy.md) - ADR ejemplo
- [AUDITORIA_DEPURACION_REPORTE.md](AUDITORIA_DEPURACION_REPORTE.md) - Correcciones

**Para Seguridad/Compliance**:
- [security/SECURITY_POLICY.md](security/SECURITY_POLICY.md) - (Pendiente)
- [security/COMPLIANCE.md](security/COMPLIANCE.md) - (Pendiente)
- [security/AUDIT_LOG.md](security/AUDIT_LOG.md) - (Pendiente)

---

## 📝 CONVENCIONES DE DOCUMENTACIÓN

### Formato

- **Markdown** para todos los documentos
- **Mermaid** para diagramas
- **Emojis** para categorización visual
- **Tablas** para información estructurada

### Estados de Documentos

- ✅ **Completo**: Documento finalizado y actualizado
- ⚠️ **Desactualizado**: Existe pero necesita actualización
- 📋 **Planificado**: Identificado y planificado
- 🔄 **En Progreso**: Actualmente en desarrollo
- 🔴 **Bloqueado**: Requiere input externo

### Prioridades

- 🔴 **Crítica**: Bloquea desarrollo
- 🟡 **Alta**: Importante para siguiente fase
- 🟢 **Media**: Deseable pero no bloqueante
- ⚪ **Baja**: Nice to have

---

## 🔄 PROCESO DE ACTUALIZACIÓN

### Cuándo actualizar este índice

1. **Al crear un nuevo documento**: Agregarlo a la tabla correspondiente
2. **Al completar un documento**: Cambiar estado de 📋 a ✅
3. **Al encontrar documentación desactualizada**: Marcar con ⚠️
4. **Al final de cada fase**: Recalcular métricas de completitud

### Responsables

- **Claude Code**: Creación de documentación técnica
- **GitHub Copilot**: Organización y mantenimiento del índice
- **Desarrollador**: Review y validación

---

## 📞 CONTACTO Y SOPORTE

**Para preguntas sobre documentación**:
- Ver [RESUMEN_EJECUTIVO.md](RESUMEN_EJECUTIVO.md)
- Consultar [PLAN_TRABAJO.md](PLAN_TRABAJO.md)
- Revisar [DOCUMENTACION_ANALISIS.md](DOCUMENTACION_ANALISIS.md)

**Para reportar documentación faltante**:
- Crear issue en GitHub con label `documentation`
- Registrar en [development/DECISIONES_IA.md](development/DECISIONES_IA.md)

---

## 🎯 OBJETIVOS

**Meta a Corto Plazo (13-Dic-2025)**:
- Completitud documental: 90%+
- Todas las categorías críticas al 100%

**Meta a Mediano Plazo (27-Dic-2025)**:
- Completitud documental: 100%
- Sistema de actualización continua funcionando
- Documentación académica lista

**Meta a Largo Plazo (18-Mar-2026)**:
- Documentación enterprise-ready
- Onboarding automatizado
- Knowledge base completa

---

**Última actualización**: 11-Dic-2025  
**Próxima revisión**: 12-Dic-2025  
**Mantenido por**: GitHub Copilot + Claude Code
