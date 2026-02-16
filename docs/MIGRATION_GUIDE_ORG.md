# Guía de Migración a Organización GitHub + Multi-AI Workflow

**Última Actualización**: 16 de Febrero, 2026  
**Fase Proyecto**: FASE 14 - Infraestructura Depurada  
**Organización**: [github.com/Carrillo-Abogados](https://github.com/Carrillo-Abogados)  
**Autor**: Alexis  
**Estado**: Planificado  
**MVP**: 27 Marzo 2026

---

## 1. Objetivo

Migrar el monorepo `AlexisJ16/CarrilloAbogados` a la organización
**Carrillo-Abogados**, separando cada capa en repositorios
independientes para habilitar desarrollo paralelo con 3 IAs
especializadas.

### Beneficios Esperados

| Aspecto | Monorepo (hoy) | Multi-repo |
|---------|----------------|------------|
| Contexto IA | ~100 MDs, cruzado | Dominio específico |
| Build | ~5 min completo | ~1 min por servicio |
| CI/CD | Pipeline monolítico | Independientes |
| Conflictos | Frecuentes entre capas | Aislados |
| Deploy | Todo o nada | Independiente |
| IAs paralelas | 1 a la vez | 3 simultáneas |

---

## 2. Repositorios Propuestos

### 2.1 Repositorios de Código

| Repositorio | Contenido del Monorepo | IA |
|-------------|------------------------|----|
| `backend-services` | `api-gateway/`, `*-service/`, `pom.xml` | Claude Code |
| `frontend` | `frontend/` | Copilot |
| `infrastructure` | `helm-charts/`, `infrastructure/`, `k8s-manifests/`, `monitoring/`, `compose.yml`, `scripts/`, `.github/workflows/` | Gemini CLI |
| `automation` | `automation/` | Gemini CLI + Juan José |

### 2.2 Repositorios de Soporte

| Repositorio | Contenido | Propósito |
|-------------|-----------|-----------|
| `.github` | Profile README, templates | Config org |
| `docs` | `docs/`, `CLAUDE.md`, `PROYECTO_ESTADO.md` | Doc centralizada |

---

## 3. Asignación de IAs

### 3.1 GitHub Copilot (VSCode)

**Repo principal**: `frontend`  
**Repo secundario**: `docs`

**Fortalezas aprovechadas:**

- Autocompletado superior en TypeScript/JSX
- Excelente en componentes React, hooks, formularios
- Integración nativa con VS Code (inline suggestions)
- Rápido para prototipos UI y refactoring menor

**Limitaciones conocidas:**

- Contexto limitado (~8K tokens por sugerencia)
- No ejecuta comandos ni valida builds
- Menos preciso en lógica de negocio compleja

**Archivo de contexto**: `.github/copilot-instructions.md`

**Responsabilidades:**

- Componentes React (páginas, layouts, formularios)
- Hooks personalizados (TanStack Query, auth)
- Tipos TypeScript sincronizados con contratos API
- Estilos Tailwind CSS
- Tests de componentes

---

### 3.2 Claude Code (Terminal, Opus 4)

**Repo principal**: `backend-services`  
**Repo secundario**: `docs`

**Fortalezas aprovechadas:**

- Razonamiento profundo en arquitectura Java/Spring
- Superior en refactoring complejo multi-archivo
- Excelente comprensión de patrones empresariales
- Capacidad de ejecutar builds/tests y autocorregir
- Contexto amplio (~200K tokens)

**Limitaciones conocidas:**

- Más lento que Copilot para sugerencias inline
- Requiere terminal (no IDE visual)

**Archivo de contexto**: `CLAUDE.md`

**Responsabilidades:**

- Endpoints REST (controllers, services, repos)
- Lógica de negocio (validaciones, reglas)
- Migraciones Flyway
- Tests unitarios y de integración
- Configuración Spring (application.yml, seguridad)
- Eventos NATS entre servicios

---

### 3.3 Gemini CLI (Terminal, Pro)

**Repo principal**: `infrastructure`  
**Repo secundario**: `automation`

**Fortalezas aprovechadas:**

- Fuerte en YAML, HCL, configuración
- Buena comprensión de Docker/Kubernetes
- Rápido para CI/CD pipelines
- Integración nativa con GCP

**Limitaciones conocidas:**

- Menos preciso que Claude en lógica compleja
- Contexto más limitado que Claude

**Archivo de contexto**: `GEMINI.md`

**Responsabilidades:**

- Docker Compose y Dockerfiles (Jib config)
- Helm charts y values
- GitHub Actions workflows
- Terraform para GCP
- Monitoring stack (Prometheus, Grafana, Loki)
- Scripts de deployment

---

### 3.4 Contexto por Repositorio

Cada repo tendrá su archivo de contexto específico:

```
backend-services/
├── CLAUDE.md                        ← Primario
└── .github/copilot-instructions.md  ← Backup

frontend/
├── .github/copilot-instructions.md  ← Primario
└── CLAUDE.md                        ← Backup

infrastructure/
├── GEMINI.md                        ← Primario
└── CLAUDE.md                        ← Backup

automation/
├── GEMINI.md                        ← Primario
└── .claude/                         ← Ya existente

docs/
├── CLAUDE.md                        ← Estado maestro
└── .github/copilot-instructions.md  ← Reglas docs
```

---

## 4. Protocolo de Sincronización

### 4.1 Fuente de Verdad

```
┌──────────────────────────────────────────┐
│       Repo: docs (fuente de verdad)      │
│                                          │
│  PROYECTO_ESTADO.md  ← Estado proyecto   │
│  api-contracts/      ← OpenAPI specs     │
│  CHANGELOG.md        ← Cambios cross-repo│
│                                          │
└──────┬──────────┬──────────┬─────────────┘
       │          │          │
  ┌────▼───┐ ┌───▼────┐ ┌───▼─────────┐
  │backend │ │frontend│ │infra        │
  │services│ │        │ │automation   │
  └────────┘ └────────┘ └─────────────┘
```

### 4.2 Antes de Trabajar con Cualquier IA

```bash
# 1. Actualizar repo docs primero
cd docs/ && git pull origin main

# 2. Leer estado actual
cat PROYECTO_ESTADO.md

# 3. Actualizar el repo de trabajo
cd ../backend-services/ && git pull origin dev
```

### 4.3 Cuando Se Modifica una Interfaz

1. La IA hace el cambio en su repo
2. Actualiza contrato OpenAPI en `docs/api-contracts/`
3. Crea Issue en repos afectados:
   `Sync: endpoint X changed in backend-services`
4. Las IAs de otros repos consumen el Issue

### 4.4 Al Finalizar una Sesión

1. Commit + Push en el repo de trabajo
2. Actualizar `docs/PROYECTO_ESTADO.md`
3. Crear Issues pendientes para trabajo cruzado

---

## 5. Plan de Migración (Paso a Paso)

### Fase 1: Pre-Migración (Completada)

- [x] Auditoría exhaustiva de documentación (103 archivos)
- [x] Limpieza redundantes (11 eliminados)
- [x] Integración rama automation (MW3 SEO)
- [x] Consolidación carpetas duplicadas
- [x] Actualización timestamps y fases
- [x] Eliminación user-service (deprecado FASE 14)
- [x] Remoción archivos locales (.claude, .mcp.json)
- [x] Creación de esta guía

### Fase 2: Crear Repos en Organización

```bash
# Crear repos vacíos con README, .gitignore, License
repos=(
  "backend-services"
  "frontend"
  "infrastructure"
  "automation"
  "docs"
  ".github"
)
```

**Configuración por repo:**

- Branch protection: `main` requiere PR + 1 review
- Default branch: `dev`
- Flujo: `main` ← `staging` ← `dev`

### Fase 3: Migración con git filter-repo

```bash
# Prerequisito
pip install git-filter-repo

# Clonar el monorepo como base
git clone https://github.com/AlexisJ16/CarrilloAbogados.git temp
cd temp

# --- BACKEND SERVICES ---
cp -r temp temp-backend && cd temp-backend
git filter-repo \
  --path api-gateway/ \
  --path client-service/ \
  --path case-service/ \
  --path payment-service/ \
  --path document-service/ \
  --path calendar-service/ \
  --path notification-service/ \
  --path n8n-integration-service/ \
  --path pom.xml \
  --path mvnw --path mvnw.cmd --path .mvn/ \
  --path sonar-project.properties
git remote add origin \
  https://github.com/Carrillo-Abogados/backend-services.git
git push origin --all --tags

# --- FRONTEND ---
cp -r temp temp-frontend && cd temp-frontend
git filter-repo --path frontend/ --path-rename frontend/:
git remote add origin \
  https://github.com/Carrillo-Abogados/frontend.git
git push origin --all --tags

# --- INFRASTRUCTURE ---
cp -r temp temp-infra && cd temp-infra
git filter-repo \
  --path helm-charts/ \
  --path infrastructure/ \
  --path k8s-manifests/ \
  --path monitoring/ \
  --path compose.yml \
  --path scripts/ \
  --path .github/workflows/ \
  --path .env.example \
  --path .gitignore
git remote add origin \
  https://github.com/Carrillo-Abogados/infrastructure.git
git push origin --all --tags

# --- AUTOMATION ---
cp -r temp temp-auto && cd temp-auto
git filter-repo --path automation/ --path-rename automation/:
git remote add origin \
  https://github.com/Carrillo-Abogados/automation.git
git push origin --all --tags

# --- DOCS ---
cp -r temp temp-docs && cd temp-docs
git filter-repo \
  --path docs/ \
  --path CLAUDE.md \
  --path PROYECTO_ESTADO.md \
  --path README.md
git remote add origin \
  https://github.com/Carrillo-Abogados/docs.git
git push origin --all --tags
```

### Fase 4: Configuración Post-Migración

**Por cada repo:**

- [ ] Crear archivo de contexto IA (CLAUDE/GEMINI.md)
- [ ] Crear `.github/copilot-instructions.md` específico
- [ ] Configurar CI/CD pipeline independiente
- [ ] Configurar GitHub Container Registry
- [ ] Configurar Dependabot
- [ ] Crear `dev` como default branch

**A nivel de organización:**

- [ ] Migrar secrets a org-level (GHCR, SONAR, SNYK)
- [ ] Configurar GitHub Projects (Kanban cross-repo)
- [ ] Crear profile README (.github repo)
- [ ] Configurar Snyk/SonarCloud por repo

### Fase 5: Validación Post-Migración

- [ ] `mvn clean verify` exitoso en `backend-services`
- [ ] `npm run build` exitoso en `frontend`
- [ ] CI/CD pipeline verde en cada repo
- [ ] Docker Compose multi-repo funcional
- [ ] Cada IA trabaja con su contexto
- [ ] Issue cruzado de prueba
- [ ] Monorepo original archivado

---

## 6. Docker Compose Multi-Repo

Repos clonados como hermanos en carpeta local:

```
Carrillo-Abogados/
├── backend-services/
├── frontend/
├── infrastructure/    ← tiene docker-compose.dev.yml
├── automation/
└── docs/
```

El `docker-compose.dev.yml` en `infrastructure/` referencia
imágenes construidas con Jib en cada repo:

```yaml
services:
  api-gateway:
    image: carrilloabogados/api-gateway:dev
    # cd ../backend-services && mvn jib:buildTar -pl api-gateway
```

---

## 7. Riesgos y Mitigación

| Riesgo | Prob. | Impacto | Mitigación |
|--------|-------|---------|------------|
| Pérdida historial | Baja | Alto | filter-repo preserva historial |
| API desincronizadas | Media | Alto | OpenAPI en repo docs |
| Overhead coordinación | Media | Medio | GitHub Projects + Issues |
| Builds rotos | Media | Medio | Validación Fase 5 |
| POM roto | Baja | Alto | POM solo incluye servicios |
| Env vars perdidas | Baja | Alto | .env.example en infra |

---

## 8. Rollback Plan

1. Monorepo original NO se elimina (se archiva)
2. Repos nuevos pueden borrarse y recrearse
3. Branches dev y main del monorepo se preservan
4. Se puede repetir filter-repo desde cero

---

## 9. Consideraciones para automation/

La carpeta `automation/` es gestionada por **Juan José Gómez**
(Marketing Tech). Reglas:

1. **No modificar** archivos de automation en la migración
2. El repo tendrá su propio GEMINI.md
3. Juan José mantiene acceso directo al repo
4. PDFs duplicados se mantienen (ref del equipo marketing)
5. Sincronización con backend via webhooks n8n

---

## 10. Timeline Estimado

| Fase | Duración | Prerequisito |
|------|----------|-------------|
| Pre-Migración | Completada | - |
| Crear Repos | 30 min | Acceso admin org |
| git filter-repo | 1-2 horas | Repos creados |
| Config Post-Migración | 2-3 horas | Repos poblados |
| Validación | 1-2 horas | Config completa |
| **Total** | **~1 día** | - |

---

## 11. Referencias

- [PROYECTO_ESTADO.md](../PROYECTO_ESTADO.md)
- [CLAUDE.md](../CLAUDE.md)
- [.github/copilot-instructions.md](../.github/copilot-instructions.md)
- [architecture/ARCHITECTURE.md](architecture/ARCHITECTURE.md)

---

*Guía maestra para la migración. Actualizar tras cada fase.*
