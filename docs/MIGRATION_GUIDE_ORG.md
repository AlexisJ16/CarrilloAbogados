# 🏢 Guía de Migración a Organización GitHub + Multi-AI Workflow

**Última Actualización**: 14 de Febrero, 2026  
**Fase Proyecto**: FASE 14 - Infraestructura Depurada  
**Organización**: [github.com/Carrillo-Abogados](https://github.com/Carrillo-Abogados)  
**Autor**: Alexis  
**Estado**: 📋 Planificado

---

## 🎯 Objetivo

Migrar el monorepo `AlexisJ16/CarrilloAbogados` a la organización **Carrillo-Abogados** en GitHub, separando cada capa en su propio repositorio para:

1. **Escalar el desarrollo** con 3 IAs trabajando en paralelo
2. **Reducir conflictos** de integración entre capas
3. **Especializar** cada IA en su dominio
4. **Mejorar CI/CD** con pipelines independientes por capa

---

## 📦 Estructura de Repositorios Propuesta

### Repositorios de Código

| Repositorio | Contenido Actual | Propósito |
|-------------|-----------------|-----------|
| **`backend-services`** | `api-gateway/`, `client-service/`, `case-service/`, `payment-service/`, `document-service/`, `calendar-service/`, `notification-service/`, `n8n-integration-service/`, `pom.xml` (parent) | Todos los microservicios Spring Boot |
| **`frontend`** | `frontend/` | Next.js 16 + React 18 + TypeScript |
| **`infrastructure`** | `helm-charts/`, `infrastructure/`, `k8s-manifests/`, `monitoring/`, `compose.yml`, `scripts/`, `.github/workflows/` | DevOps, Docker, K8s, CI/CD, Observabilidad |
| **`automation`** | `automation/` | n8n workflows, documentación marketing |

### Repositorio Central

| Repositorio | Contenido | Propósito |
|-------------|-----------|-----------|
| **`.github`** | Profile README de la org, templates de issues/PRs, políticas de seguridad | Configuración organizacional |
| **`docs`** | `docs/`, `CLAUDE.md`, `PROYECTO_ESTADO.md` | Documentación centralizada, contexto para IAs, estado del proyecto |

---

## 🤖 Distribución de IAs por Repositorio

### Asignación Estratégica

| IA | Repositorio Principal | Repositorio Secundario | Justificación |
|----|----------------------|----------------------|---------------|
| **GitHub Copilot** (VSCode) | `frontend` | `docs` | Excelente en TypeScript/React, autocompletado UI, componentes, y organización de documentación |
| **Claude Code** (Terminal, Opus 4.6) | `backend-services` | `docs` | Superior en arquitectura Java/Spring Boot, refactoring complejo, diseño de APIs, y razonamiento profundo |
| **Gemini CLI** (Terminal, Pro) | `infrastructure` + `automation` | `docs` | Fuerte en DevOps, YAML/HCL, CI/CD, y configuraciones de infraestructura |

### Contexto por IA

Cada repositorio tendrá su propio archivo de contexto para la IA asignada:

```
backend-services/
├── CLAUDE.md              ← Contexto para Claude Code
├── .github/
│   └── copilot-instructions.md  ← Backup para Copilot

frontend/
├── .github/
│   └── copilot-instructions.md  ← Contexto para Copilot
├── CLAUDE.md              ← Backup para Claude Code

infrastructure/
├── GEMINI.md              ← Contexto para Gemini CLI
├── CLAUDE.md              ← Backup para Claude Code

automation/
├── GEMINI.md              ← Contexto para Gemini CLI
├── .claude/               ← Agentes Claude (ya existentes)

docs/
├── CLAUDE.md              ← Contexto maestro (estado del proyecto)
├── .github/
│   └── copilot-instructions.md
```

---

## 🔄 Flujo de Trabajo Multi-AI

### Principios Fundamentales

1. **Una IA principal por repo**: Evitar que dos IAs editen el mismo archivo simultáneamente
2. **Contratos de API como fuente de verdad**: OpenAPI specs en `docs/` como punto de sincronización
3. **Issues como sistema de coordinación**: Usar GitHub Issues + Projects para asignar trabajo
4. **PRs como punto de integración**: Code review cruzado entre repos

### Ciclo de Desarrollo

```
┌─────────────────────────────────────────────────────────┐
│                  GitHub Organization                     │
│                  Carrillo-Abogados                        │
│                                                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌────────┐  │
│  │ backend  │  │ frontend │  │  infra   │  │  docs  │  │
│  │ services │  │          │  │          │  │        │  │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └───┬────┘  │
│       │              │              │             │       │
│  Claude Code    Copilot VSCode  Gemini CLI    Todas      │
│       │              │              │             │       │
│       ▼              ▼              ▼             ▼       │
│  ┌─────────────────────────────────────────────────────┐ │
│  │            GitHub Projects (Kanban Board)            │ │
│  │  Backlog → In Progress → Review → Done              │ │
│  └─────────────────────────────────────────────────────┘ │
│                                                          │
│  ┌─────────────────────────────────────────────────────┐ │
│  │           Contratos API (OpenAPI Specs)              │ │
│  │        docs/api-contracts/*.yaml                    │ │
│  │   Fuente de verdad para sincronización              │ │
│  └─────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

### Protocolo de Sincronización

#### 1. Antes de empezar a trabajar con cualquier IA

```bash
# Siempre sincronizar el repo docs/ primero
cd docs/
git pull origin main

# Leer el PROYECTO_ESTADO.md para contexto actual
# Leer los contratos API para interfaces actualizadas
```

#### 2. Cuando una IA modifica una interfaz (API, evento, schema)

```
1. IA hace el cambio en su repo (ej: Claude modifica un endpoint)
2. IA actualiza el contrato OpenAPI en docs/api-contracts/
3. IA crea un Issue en los repos afectados:
   "🔄 Sync: endpoint X changed in backend-services"
4. Las otras IAs consumen el Issue y adaptan su código
```

#### 3. Al finalizar una sesión de trabajo

```
1. Commit + Push en el repo de trabajo
2. Actualizar docs/PROYECTO_ESTADO.md con lo completado
3. Crear Issues pendientes si hay trabajo cruzado
```

---

## 🛠️ Plan de Migración (Paso a Paso)

### Fase 1: Preparación (Pre-migración)

- [x] Auditoría exhaustiva de documentación
- [x] Limpieza de archivos redundantes y obsoletos  
- [x] Actualización de timestamps y fases en todos los docs
- [ ] Crear repos vacíos en la organización Carrillo-Abogados
- [ ] Configurar GitHub Projects en la organización
- [ ] Definir branch protection rules para cada repo

### Fase 2: Migración de Código

```bash
# 1. Backend Services
# Usar git filter-repo para extraer con historial
git filter-repo --path api-gateway/ --path client-service/ \
  --path case-service/ --path payment-service/ \
  --path document-service/ --path calendar-service/ \
  --path notification-service/ --path n8n-integration-service/ \
  --path pom.xml

# 2. Frontend
git filter-repo --path frontend/

# 3. Infrastructure
git filter-repo --path helm-charts/ --path infrastructure/ \
  --path k8s-manifests/ --path monitoring/ \
  --path compose.yml --path scripts/ \
  --path .github/workflows/

# 4. Automation
git filter-repo --path automation/

# 5. Docs
git filter-repo --path docs/ --path CLAUDE.md \
  --path PROYECTO_ESTADO.md --path README.md
```

### Fase 3: Configuración Post-Migración

- [ ] Configurar CLAUDE.md específico en `backend-services`
- [ ] Configurar copilot-instructions.md en `frontend`
- [ ] Configurar GEMINI.md en `infrastructure` y `automation`
- [ ] Configurar CI/CD independiente por repo
- [ ] Configurar GitHub Container Registry por repo
- [ ] Migrar secrets de GitHub Actions a nivel organización
- [ ] Configurar Dependabot/Snyk por repo

### Fase 4: Validación

- [ ] Build exitoso de cada repo independiente
- [ ] CI/CD pipeline funcionando en cada repo
- [ ] Crear primer Issue cruzado de prueba
- [ ] Verificar que cada IA puede trabajar con su contexto
- [ ] Docker Compose multi-repo funcional

---

## 📝 Archivos de Contexto por IA

### CLAUDE.md (para Claude Code en `backend-services`)

Contendrá:
- Stack tecnológico backend (Java 21, Spring Boot 3.3.13)
- Arquitectura de microservicios (8 servicios, puertos, schemas)
- Convenciones de código Java
- Patrones obligatorios (DTOs, excepciones, validaciones)
- Estructura de paquetes
- Comandos Maven frecuentes
- Lecciones aprendidas (Jackson, NATS, Health Checks)
- Referencia a contratos API en `docs/`

### copilot-instructions.md (para Copilot en `frontend`)

Contendrá:
- Stack frontend (Next.js 16, React 18, TypeScript 5, Tailwind)
- Estructura de carpetas del frontend
- Componentes UI y sistema de diseño
- Integración con backend (URLs, endpoints, tipos)
- Hooks y patrones de data fetching (TanStack Query)
- Variables de entorno
- Referencia a contratos API en `docs/`

### GEMINI.md (para Gemini CLI en `infrastructure`)

Contendrá:
- Stack de infraestructura (Docker, K8s, Helm, Terraform)
- Regla crítica Windows + WSL
- Docker Compose y Jib configuration
- Helm charts structure
- CI/CD pipelines (GitHub Actions)
- Monitoring stack (Grafana LGTM)
- Scripts de deployment
- Referencia a `docs/` para estado del proyecto

---

## ⚠️ Consideraciones Importantes

### Docker Compose Multi-Repo

Para desarrollo local, necesitarás un `docker-compose.yml` que referencie imágenes de múltiples repos. Opciones:

1. **Opción A**: Docker Compose en `infrastructure/` que referencia imágenes pre-built
2. **Opción B**: Script que clona todos los repos y genera compose dinámico
3. **Opción C (recomendada)**: `infrastructure/` tiene un `docker-compose.dev.yml` que usa paths relativos asumiendo que todos los repos están clonados como hermanos:

```
Carrillo-Abogados/          ← Carpeta raíz
├── backend-services/       ← Repo backend
├── frontend/               ← Repo frontend
├── infrastructure/         ← Repo infra (contiene compose)
├── automation/             ← Repo automation
└── docs/                   ← Repo docs
```

### Maven Parent POM

El `pom.xml` padre debe vivir en `backend-services/`. Los módulos se referencian como subdirectorios del mismo repo.

### Branch Strategy

Cada repo mantiene su propio flujo:
- `main` → producción
- `dev` → desarrollo
- Feature branches desde `dev`

### Secrets a Nivel de Organización

Migrar secrets compartidos a nivel de organización GitHub:
- `GHCR_TOKEN`
- `SONAR_TOKEN`
- `SNYK_TOKEN`
- Database credentials
- API keys

---

## 📊 Beneficios Esperados

| Aspecto | Monorepo (Actual) | Multi-repo (Propuesto) |
|---------|-------------------|----------------------|
| **Contexto IA** | 103 archivos .md, info sobrecargada | Contexto específico por dominio |
| **Build Time** | Build completo ~5 min | Build por servicio ~1 min |
| **CI/CD** | Pipeline monolítico | Pipelines independientes |
| **Conflictos** | Frecuentes entre capas | Aislados por dominio |
| **Onboarding** | Abrumador | Gradual por área |
| **IAs paralelas** | 1 IA a la vez | 3 IAs simultáneas |
| **Deploy** | Todo o nada | Deploy independiente |

---

## 🔗 Referencias

- [PROYECTO_ESTADO.md](../PROYECTO_ESTADO.md) - Estado actual del proyecto
- [CLAUDE.md](../CLAUDE.md) - Contexto técnico maestro
- [docs/architecture/ARCHITECTURE.md](architecture/ARCHITECTURE.md) - Arquitectura del sistema
- [.github/copilot-instructions.md](../.github/copilot-instructions.md) - Instrucciones actuales para IAs

---

*Este documento será la guía maestra durante el proceso de migración. Actualizar después de cada fase completada.*
