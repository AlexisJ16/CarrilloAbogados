# 📚 Índice Maestro de Documentación

**Proyecto**: Carrillo Abogados Legal Tech Platform  
**Última Actualización**: 14 de Febrero, 2026  
**Estado**: ✅ FASE 14 - Infraestructura Depurada

---

## 🗂️ ESTRUCTURA DE DOCUMENTACIÓN

```text
docs/
├── 💼 business/         → Documentación de negocio
├── 🏗️ architecture/     → Decisiones arquitectónicas
├── 💻 development/      → Guías de desarrollo
├── ⚙️ operations/       → Deployment y operaciones
└── 🔒 security/         → Políticas de seguridad

⚠️ NOTA: Documentación n8n → Ver carpeta /automation/ (fuente de verdad)
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

---

## 🏗️ ARCHITECTURE (Arquitectura)

Decisiones de arquitectura y diseño técnico.

| Documento | Descripción | Estado |
|-----------|-------------|--------|
| [ARCHITECTURE.md](architecture/ARCHITECTURE.md) | Arquitectura general del sistema | ✅ |
| [ADR-005-database-strategy.md](architecture/ADR-005-database-strategy.md) | Estrategia de base de datos compartida | ✅ |
| [ADR-006-kubernetes-local-strategy.md](architecture/ADR-006-kubernetes-local-strategy.md) | Estrategia Kubernetes local (Minikube) | ✅ |

---

## 💻 DEVELOPMENT (Desarrollo)

Guías y estándares de desarrollo.

| Documento | Descripción | Estado |
|-----------|-------------|--------|
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
| [OPS_README.md](operations/OPS_README.md) | Guía DevOps completa | ✅ |
| [DEPLOY_GCP.md](operations/DEPLOY_GCP.md) | Guía de deploy a GCP Cloud Run | ✅ |
| [DEPLOYMENT.md](operations/DEPLOYMENT.md) | Guía deploy HostGator + GCP | ✅ |
| [OBSERVABILITY_GUIDE.md](operations/OBSERVABILITY_GUIDE.md) | Guía del stack Grafana LGTM | ✅ |
| [GITHUB_SECRETS.md](operations/GITHUB_SECRETS.md) | Configuración de secrets GitHub | ✅ |
| [GITHUB_SECRETS_TEMPLATE.md](operations/GITHUB_SECRETS_TEMPLATE.md) | Template de secrets | ✅ |
| [DEPLOYMENT_CHECKLIST.md](operations/DEPLOYMENT_CHECKLIST.md) | Checklist de deployment | ✅ |

---

## 🔒 SECURITY (Seguridad)

Políticas y configuraciones de seguridad.

| Documento | Descripción | Estado |
|-----------|-------------|--------|
| [SECURITY_CICD.md](security/SECURITY_CICD.md) | Configuración de seguridad en CI/CD | ✅ |
| [SECRETS_MANAGEMENT.md](security/SECRETS_MANAGEMENT.md) | Política de gestión de secrets | ✅ |

---

## 🤖 AUTOMATION (Automatizaciones n8n)

> **⚠️ FUENTE DE VERDAD**: Toda la documentación y workflows de n8n se encuentran en la carpeta `/automation/` en la raíz del proyecto.

**Estructura de Automation:**

```text
automation/
├── README.md                    → Punto de entrada principal
├── docs/
│   ├── 00_INDEX.md             → Índice completo de documentación
│   ├── 01_AGENT_PROTOCOLS.md   → Protocolos para agentes IA
│   ├── business/               → Documentación estratégica
│   └── technical/              → Especificaciones técnicas
│       ├── arquitectura/       → Arquitectura de 3 MEGA-WORKFLOWS
│       ├── n8n_mcp_guide.md    → Guía Model Context Protocol
│       └── NODE_STANDARDS.md   → Estándares de nodos n8n
├── workflows/
│   └── MW1_LEAD_LIFECYCLE/     → Mega-Workflow #1
│       ├── STATUS.md           → Estado actual de desarrollo
│       ├── 01-orchestrator/    → Workflow orquestador
│       └── 02-spokes/          → Sub-workflows especializados
└── archive/                     → Archivos deprecados
```

**Documentos Clave:**

| Documento | Ubicación | Descripción |
|-----------|-----------|-------------|
| **README Principal** | [/automation/README.md](../automation/README.md) | Punto de entrada |
| **Índice Completo** | [/automation/docs/00_INDEX.md](../automation/docs/00_INDEX.md) | Navegación completa |
| **Protocolos Agentes** | [/automation/docs/01_AGENT_PROTOCOLS.md](../automation/docs/01_AGENT_PROTOCOLS.md) | Reglas para IAs |
| **Arquitectura MW1** | [/automation/docs/technical/arquitectura/01_MEGA_WORKFLOW_1_CAPTURA.md](../automation/docs/technical/arquitectura/01_MEGA_WORKFLOW_1_CAPTURA.md) | Lead Lifecycle |
| **Status MW1** | [/automation/workflows/MW1_LEAD_LIFECYCLE/STATUS.md](../automation/workflows/MW1_LEAD_LIFECYCLE/STATUS.md) | Estado actual |

**Workflows Activos:**
- ✅ **MW#1 - Lead Lifecycle**: Captura y gestión de leads (7 sub-workflows)
- 📋 **MW#2 - Retención**: Cliente a recompra (Q2 2026)
- 📋 **MW#3 - SEO Content**: Tráfico a lead (Q2-Q3 2026)

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

*Última actualización: 14 de Febrero, 2026*
