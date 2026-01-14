# 📚 Índice Maestro de Documentación

**Proyecto**: Carrillo Abogados Legal Tech Platform  
**Última Actualización**: 14 de Enero, 2026  
**Estado**: ✅ FASE 10 - Autenticación Frontend Completa

---

## 🗂️ ESTRUCTURA DE DOCUMENTACIÓN

```text
docs/
├── 💼 business/         → Documentación de negocio
├── 🏗️ architecture/     → Decisiones arquitectónicas
├── 💻 development/      → Guías de desarrollo
├── ⚙️ operations/       → Deployment y operaciones
├── 🔒 security/         → Políticas de seguridad
└── 🔄 n8n-workflows/    → Documentación técnica n8n
```

---

## ⭐ BUSINESS (Documentación de Negocio)

**Documentación fundamental del modelo de negocio y requerimientos.**

| Documento | Descripción | Estado |
|-----------|-------------|--------|
| [MODELO_NEGOCIO.md](business/MODELO_NEGOCIO.md) | Contexto del bufete, 5 áreas de práctica | ✅ |
| [REQUERIMIENTOS.md](business/REQUERIMIENTOS.md) | Requerimientos funcionales (64) y no func (23) | ✅ |
| [ROLES_USUARIOS.md](business/ROLES_USUARIOS.md) | 4 roles: Admin, Abogado, Cliente, Visitante | ✅ |
| [CASOS_USO.md](business/CASOS_USO.md) | Flujos detallados por actor con diagramas | ✅ |
| [ARQUITECTURA_FUNCIONAL.md](business/ARQUITECTURA_FUNCIONAL.md) | Mapeo microservicio → función de negocio | ✅ |
| [MVP_ROADMAP.md](business/MVP_ROADMAP.md) | Roadmap hacia MVP (27 Marzo 2026) | ✅ |
| [ESTRATEGIA_AUTOMATIZACION.md](business/ESTRATEGIA_AUTOMATIZACION.md) | Integración plataforma ↔ n8n (3 workflows) | ✅ |

### Subcarpetas

- `Marketing-N8N/` - Documentación de arquitectura de automatizaciones n8n
- `Documentación_Proporcionada/` - Archivos originales del cliente (briefs)

---

## 🏗️ ARCHITECTURE (Arquitectura)

Decisiones de arquitectura y diseño técnico.

| Documento | Descripción | Estado |
|-----------|-------------|--------|
| [ARCHITECTURE.md](architecture/ARCHITECTURE.md) | Arquitectura general del sistema | ✅ |
| [ADR-005-database-strategy.md](architecture/ADR-005-database-strategy.md) | Estrategia de base de datos compartida | ✅ |
| [ADR-006-kubernetes-local-strategy.md](architecture/ADR-006-kubernetes-local-strategy.md) | Estrategia Kubernetes local (Minikube) | ✅ |
| [INTEGRACION_N8N.md](architecture/INTEGRACION_N8N.md) | Arquitectura de integración Portal ↔ n8n | ✅ |

---

## 💻 DEVELOPMENT (Desarrollo)

Guías y estándares de desarrollo.

| Documento | Descripción | Estado |
|-----------|-------------|--------|
| [ROADMAP.md](development/ROADMAP.md) | Roadmap técnico hacia MVP | ✅ |
| [NEXT_FEATURES.md](development/NEXT_FEATURES.md) | Próximos desarrollos priorizados | ✅ |
| [TEST_USERS.md](development/TEST_USERS.md) | Usuarios de prueba E2E | ✅ |
| [SESSION_CONTEXT.md](development/SESSION_CONTEXT.md) | Contexto entre sesiones de desarrollo | ✅ |
| [GUIA_INTEGRACION_MARKETING.md](development/GUIA_INTEGRACION_MARKETING.md) | Guía para desarrollador de marketing | ✅ |
| [VERSION_STABILITY.md](development/VERSION_STABILITY.md) | Control de versiones estables | ✅ |

---

## ⚙️ OPERATIONS (Operaciones)

Guías de despliegue y operaciones.

| Documento | Descripción | Estado |
|-----------|-------------|--------|
| [DEPLOY_GCP.md](operations/DEPLOY_GCP.md) | Guía de deploy a GCP Cloud Run | ✅ |
| [OPERATIONS.md](operations/OPERATIONS.md) | Guía de operaciones Kubernetes | ✅ |
| [OBSERVABILITY_GUIDE.md](operations/OBSERVABILITY_GUIDE.md) | Guía del stack Grafana LGTM | ✅ |
| [GITHUB_SECRETS.md](operations/GITHUB_SECRETS.md) | Configuración de secrets GitHub | ✅ |
| [DEPLOYMENT_CHECKLIST.md](operations/DEPLOYMENT_CHECKLIST.md) | Checklist de deployment | ✅ |

---

## 🔒 SECURITY (Seguridad)

Políticas y configuraciones de seguridad.

| Documento | Descripción | Estado |
|-----------|-------------|--------|
| [SECURITY_CICD.md](security/SECURITY_CICD.md) | Configuración de seguridad en CI/CD | ✅ |

---

## 🔄 N8N-WORKFLOWS (Automatizaciones)

Documentación técnica de workflows n8n.

| Documento | Descripción | Estado |
|-----------|-------------|--------|
| [README.md](n8n-workflows/README.md) | Índice de workflows n8n | ✅ |
| [NODE_STANDARDS.md](n8n-workflows/NODE_STANDARDS.md) | Estándares de nodos n8n | ✅ |
| [n8n_mcp_guide.md](n8n-workflows/n8n_mcp_guide.md) | Guía de Model Context Protocol | ✅ |
| [mega-workflow-1/](n8n-workflows/mega-workflow-1/) | MW#1: Lead Lifecycle Manager | ✅ |

> **Nota**: El desarrollo activo de workflows n8n está en la carpeta `automation/` (ver PR #32)

---

## 📖 DOCUMENTOS EN RAÍZ DEL PROYECTO

| Documento | Descripción |
|-----------|-------------|
| [PROYECTO_ESTADO.md](../PROYECTO_ESTADO.md) | Estado actual del proyecto |
| [CLAUDE.md](../CLAUDE.md) | Contexto para Claude AI |
| [README.md](../README.md) | README principal del repositorio |

### Instrucciones para IAs

| Documento | Propósito |
|-----------|-----------|
| [.github/copilot-instructions.md](../.github/copilot-instructions.md) | Instrucciones principales para GitHub Copilot |
| [.github/copilot-agents/](../.github/copilot-agents/) | 4 agentes especializados (backend, frontend, devops, docs) |

---

## 🔗 NAVEGACIÓN RÁPIDA

### Para Nuevos Desarrolladores

1. Leer [MODELO_NEGOCIO.md](business/MODELO_NEGOCIO.md) - Entender el negocio
2. Leer [ROLES_USUARIOS.md](business/ROLES_USUARIOS.md) - Entender los usuarios
3. Leer [ARQUITECTURA_FUNCIONAL.md](business/ARQUITECTURA_FUNCIONAL.md) - Entender la arquitectura
4. Leer [../CLAUDE.md](../CLAUDE.md) - Contexto técnico completo

### Para IAs

1. [../CLAUDE.md](../CLAUDE.md) - Contexto técnico maestro
2. [../PROYECTO_ESTADO.md](../PROYECTO_ESTADO.md) - Estado actual
3. [../.github/copilot-instructions.md](../.github/copilot-instructions.md) - Instrucciones de trabajo

---

*Última actualización: 14 de Enero, 2026*
