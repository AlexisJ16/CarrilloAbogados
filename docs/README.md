# 📚 Índice Maestro de Documentación

**Proyecto**: Carrillo Abogados Legal Tech Platform  
**Última Actualización**: 11 de Enero, 2026  
**Estado**: ✅ FASE 10 - Autenticación Frontend Completa

---

## 🗂️ ESTRUCTURA DE DOCUMENTACIÓN

```text
docs/
├── 💼 business/         → Documentación de negocio ⭐ NUEVO
├── 🤖 ai-context/       → Instrucciones para IAs
├── 🏗️ architecture/     → Decisiones arquitectónicas
├── 📡 api/              → Documentación de APIs
├── 💻 development/      → Guías de desarrollo
├── ⚙️ operations/       → Deployment y operaciones
├── 🔒 security/         → Políticas de seguridad
└── 📦 archive/          → Documentos obsoletos
```

---

## ⭐ BUSINESS (Documentación de Negocio)

**Documentación fundamental del modelo de negocio y requerimientos.**

| Documento                                                               | Descripción                                    | Estado |
| ----------------------------------------------------------------------- | ---------------------------------------------- | ------ |
| [MODELO_NEGOCIO.md](business/MODELO_NEGOCIO.md)                         | Contexto del bufete, 5 áreas de práctica       | ✅      |
| [REQUERIMIENTOS.md](business/REQUERIMIENTOS.md)                         | Requerimientos funcionales (64) y no func (23) | ✅      |
| [ROLES_USUARIOS.md](business/ROLES_USUARIOS.md)                         | 4 roles: Admin, Abogado, Cliente, Visitante    | ✅      |
| [CASOS_USO.md](business/CASOS_USO.md)                                   | Flujos detallados por actor con diagramas      | ✅      |
| [ARQUITECTURA_FUNCIONAL.md](business/ARQUITECTURA_FUNCIONAL.md)         | Mapeo microservicio → función de negocio       | ✅      |
| [ESTRATEGIA_AUTOMATIZACION.md](business/ESTRATEGIA_AUTOMATIZACION.md)   | Integración plataforma ↔ n8n (3 workflows)     | ✅      |

### Subcarpetas

- `Analizar_Ya/` - Documentación de marketing y automatizaciones n8n (4 documentos)
- `Documentación_Proporcionada/` - Archivos originales del cliente (briefs, PDFs)

### Archivos de Referencia (PDFs)

- `Brief de requerimientos.pdf` - Cuestionario original
- `Respuestas del Brief de requerimientos.xlsx` - Respuestas del cliente
- `Planeación empresarial para abogados.pdf` - Plan empresarial
- `Estimacion de clientes y presupuesto solo de pauta.pdf` - Presupuesto marketing

---

## 🤖 AI-CONTEXT (Instrucciones para IAs)

Documentos de contexto y memoria para inteligencias artificiales.

| Documento                                                                | Descripción                       | IA Target      |
| ------------------------------------------------------------------------ | --------------------------------- | -------------- |
| [AI_CONTEXT_MASTER.md](ai-context/AI_CONTEXT_MASTER.md)                  | Contexto maestro (resumen)        | Todas          |
| [COPILOT_INSTRUCTIONS.md](ai-context/COPILOT_INSTRUCTIONS.md)            | Instrucciones específicas Copilot | GitHub Copilot |
| [PROMPT_CONTINUACION.md](ai-context/PROMPT_CONTINUACION.md)              | Prompt para continuar sesiones    | Todas          |
| [../CLAUDE.md](../CLAUDE.md)                                             | Contexto completo Claude Code     | Claude Code    |
| [../.github/copilot-instructions.md](../.github/copilot-instructions.md) | Instrucciones GitHub Copilot      | GitHub Copilot |

> **Nota**: CLAUDE.md y copilot-instructions.md permanecen en sus ubicaciones por compatibilidad

---

## 🏗️ ARCHITECTURE (Arquitectura)

Decisiones de arquitectura y diseño técnico.

| Documento                                                                  | Descripción                            | Estado |
| -------------------------------------------------------------------------- | -------------------------------------- | ------ |
| [ARCHITECTURE.md](architecture/ARCHITECTURE.md)                            | Arquitectura general del sistema       | ✅      |
| [ADR-005-database-strategy.md](architecture/ADR-005-database-strategy.md)  | Estrategia de base de datos compartida | ✅      |

---

## 📡 API (Documentación de APIs)

Documentación de endpoints y contratos de API.

| Documento                | Descripción                           | Estado |
| ------------------------ | ------------------------------------- | ------ |
| [openapi/](api/openapi/) | Especificaciones OpenAPI por servicio | ⏳      |

> **Nota**: Las APIs están documentadas con Swagger/OpenAPI en cada microservicio en `/swagger-ui.html`

---

## 💻 DEVELOPMENT (Desarrollo)

Guías y estándares de desarrollo.

| Documento                                                | Descripción                   | Estado |
| -------------------------------------------------------- | ----------------------------- | ------ |
| [VERSION_STABILITY.md](development/VERSION_STABILITY.md) | Control de versiones estables | ✅      |

---

## ⚙️ OPERATIONS (Operaciones)

Guías de despliegue y operaciones.

| Documento                                 | Descripción                    | Estado |
| ----------------------------------------- | ------------------------------ | ------ |
| [OPERATIONS.md](operations/OPERATIONS.md) | Guía de operaciones Kubernetes | ✅      |
| [OPS_README.md](operations/OPS_README.md) | Comandos operacionales rápidos | ✅      |

---

## 🔒 SECURITY (Seguridad)

Políticas y configuraciones de seguridad.

| Documento   | Descripción            | Estado |
| ----------- | ---------------------- | ------ |
| *Por crear* | Políticas de seguridad | ⏳      |

---

## 📦 ARCHIVE (Documentos Obsoletos)

Documentos archivados para referencia histórica.

| Documento                       | Razón de Archivo                  | Fecha       |
| ------------------------------- | --------------------------------- | ----------- |
| PLAN_TRABAJO.md                 | Plan obsoleto (11 Dic 2025)       | 19 Dic 2025 |
| RESUMEN_EJECUTIVO.md            | Superado por documentación actual | 19 Dic 2025 |
| DOCUMENTACION_ANALISIS.md       | Análisis completado               | 19 Dic 2025 |
| AUDITORIA_DEPURACION_REPORTE.md | Auditoría antigua (11 Dic 2025)   | 19 Dic 2025 |

---

## 📖 OTROS DOCUMENTOS EN RAÍZ

| Documento                                      | Descripción                                       |
| ---------------------------------------------- | ------------------------------------------------- |
| [../PROYECTO_ESTADO.md](../PROYECTO_ESTADO.md) | Estado actual del proyecto (mantener actualizado) |
| [../CLAUDE.md](../CLAUDE.md)                   | Contexto para Claude Code                         |
| [../COPILOT_PROMPT.md](../COPILOT_PROMPT.md)   | Prompt inicial para GitHub Copilot                |
| [../README.md](../README.md)                   | README principal del repositorio                  |

---

## 🔗 NAVEGACIÓN RÁPIDA

### Para Nuevos Desarrolladores

1. Leer [MODELO_NEGOCIO.md](business/MODELO_NEGOCIO.md) - Entender el negocio
2. Leer [ROLES_USUARIOS.md](business/ROLES_USUARIOS.md) - Entender los usuarios
3. Leer [ARQUITECTURA_FUNCIONAL.md](business/ARQUITECTURA_FUNCIONAL.md) - Entender la arquitectura
4. Leer [../CLAUDE.md](../CLAUDE.md) - Contexto técnico completo

### Para IAs

1. [AI_CONTEXT_MASTER.md](ai-context/AI_CONTEXT_MASTER.md) - Resumen ejecutivo
2. [../PROYECTO_ESTADO.md](../PROYECTO_ESTADO.md) - Estado actual
3. Documentación de negocio en `business/`

---

*Última actualización: 2 de Enero, 2026*
