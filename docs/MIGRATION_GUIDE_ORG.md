# Guia de Migracion a Organizacion GitHub + Multi-AI Workflow

**Version**: 3.0
**Fecha**: 16 de Febrero, 2026
**Organizacion**: [github.com/Carrillo-Abogados](https://github.com/Carrillo-Abogados)
**Autor**: Alexis
**Estado**: En preparacion

---

## 1. Por que migrar

### El problema

El monorepo `AlexisJ16/CarrilloAbogados` contiene ~100 archivos de
documentacion, 8 microservicios, frontend, infra, helm charts,
monitoring, automation y docs de negocio. Cada vez que una IA abre
una sesion, debe procesar todo ese contexto antes de poder producir
codigo util. Esto causa:

- **Saturacion cognitiva**: Las IAs pierden precision al tener
  demasiado contexto cruzado entre dominios
- **Tiempo desperdiciado**: Cada sesion gasta tokens significativos
  solo en comprension antes de producir
- **Interferencia**: Cambios en frontend generan ruido para el
  backend y viceversa
- **Imposibilidad de paralelizar**: Solo una IA puede trabajar a
  la vez sin conflictos

### La solucion

Separar el monorepo en **4 repositorios** bajo una organizacion
GitHub, cada uno con su IA asignada y contexto enfocado.

### Beneficios concretos

| Aspecto | Monorepo (hoy) | Multi-repo |
|---------|----------------|------------|
| Contexto IA | ~100 MDs, todo mezclado | ~10-15 archivos por dominio |
| Tiempo de comprension | 5-10 min por sesion | < 1 min (contexto limpio) |
| Build | ~5 min completo | ~1 min por servicio |
| CI/CD | 1 pipeline monolitico | Pipelines independientes |
| Deploy | Todo o nada | Servicio por servicio |
| Trabajo paralelo | 1 IA a la vez | 3 IAs simultaneas |

---

## 2. Estructura de repositorios (4 repos)

### 2.1 Mapa de repositorios

| Repo | IA asignada | Contenido del monorepo |
|------|-------------|------------------------|
| `backend-services` | Claude Code (Opus) | `api-gateway/`, 7 `*-service/`, `pom.xml`, `mvnw`, `.mvn/` |
| `frontend` | GitHub Copilot (VSCode) | `frontend/` (renombrado a raiz) |
| `platform` | Gemini CLI | `compose.yml`, `helm-charts/`, `infrastructure/`, `monitoring/`, `scripts/`, `.github/workflows/`, `docs/` (operaciones y arquitectura) |
| `automation` | Ninguna (Juan Jose) | `automation/` (renombrado a raiz) |

### 2.2 Por que 4 repos y no 6

La guia anterior proponia repos separados para `docs` y `.github`.
Se eliminan porque:

- **`docs` como repo aparte** crea overhead: cada IA tendria que
  clonar y actualizar un repo extra al finalizar. La documentacion
  relevante vive mejor dentro de cada repo.
- **`.github` org profile** es un archivo README de 20 lineas que
  se crea en 5 minutos post-migracion, no necesita planificacion.
- **`platform`** (renombrado de `infrastructure`) absorbe la
  documentacion transversal (arquitectura, operaciones, estado del
  proyecto) porque es el repo que gestiona el deploy y necesita
  esa visibilidad.

### 2.3 Que lleva cada repo

```
backend-services/                  ← Claude Code
├── api-gateway/
├── client-service/
├── case-service/
├── payment-service/
├── document-service/
├── calendar-service/
├── notification-service/
├── n8n-integration-service/
├── pom.xml                        ← Parent POM (se mantiene intacto)
├── mvnw, mvnw.cmd, .mvn/
├── sonar-project.properties
├── CLAUDE.md                      ← Contexto IA (nuevo, solo backend)
├── .github/
│   ├── workflows/ci.yml           ← Pipeline: build + test + push images
│   └── copilot-instructions.md    ← Backup context
├── .gitignore                     ← Java/Maven
└── README.md

frontend/                          ← GitHub Copilot
├── src/
├── public/
├── package.json
├── next.config.ts
├── tsconfig.json
├── tailwind.config.ts
├── Dockerfile
├── .github/
│   ├── workflows/ci.yml           ← Pipeline: build + push image
│   └── copilot-instructions.md    ← Contexto IA primario
├── CLAUDE.md                      ← Backup context
├── .gitignore                     ← Node/Next.js
└── README.md

platform/                          ← Gemini CLI
├── compose.yml                    ← Docker Compose (solo images, no builds)
├── .env.example
├── helm-charts/
├── k8s-manifests/                 ← Consolidado (no duplicado)
├── monitoring/
├── scripts/
├── docs/
│   ├── architecture/
│   ├── operations/
│   ├── business/
│   └── security/
├── PROYECTO_ESTADO.md             ← Estado del proyecto
├── .github/
│   └── workflows/validate.yml     ← Pipeline: lint YAML, validate compose
├── GEMINI.md                      ← Contexto IA primario
├── CLAUDE.md                      ← Backup context
├── .gitignore
└── README.md

automation/                        ← Juan Jose (sin IA)
├── (contenido actual de automation/ sin cambios)
└── (sin archivos de contexto IA)
```

---

## 3. Asignacion de IAs

### 3.1 Claude Code (Terminal)

**Repo**: `backend-services`
**Archivo de contexto**: `CLAUDE.md`

**Por que Claude para backend:**
- Razonamiento profundo en arquitectura Java/Spring
- Superior en refactoring multi-archivo
- Ejecuta builds/tests y autocorrige
- Contexto amplio (~200K tokens)
- Comprension de patrones empresariales

**Responsabilidades:**
- Endpoints REST (controllers, services, repositories)
- Logica de negocio y validaciones
- Migraciones Flyway
- Tests unitarios e integracion
- Configuracion Spring (application.yml, seguridad)
- Eventos NATS entre servicios

### 3.2 GitHub Copilot (VSCode)

**Repo**: `frontend`
**Archivo de contexto**: `.github/copilot-instructions.md`

**Por que Copilot para frontend:**
- Autocompletado superior en TypeScript/JSX
- Integracion nativa con VSCode (inline)
- Rapido para componentes React y hooks
- Excelente con Tailwind CSS

**Responsabilidades:**
- Componentes React (paginas, layouts, formularios)
- Hooks personalizados (TanStack Query, auth)
- Tipos TypeScript alineados con contratos API
- Estilos Tailwind CSS
- Tests de componentes

### 3.3 Gemini CLI (Terminal)

**Repo**: `platform`
**Archivo de contexto**: `GEMINI.md`

**Por que Gemini para platform:**
- Fuerte en YAML, HCL, configuracion
- Buena comprension Docker/Kubernetes
- Rapido para CI/CD pipelines
- Integracion nativa con GCP

**Responsabilidades:**
- Docker Compose (compose.yml)
- Helm charts y values
- GitHub Actions workflows (todos los repos)
- Terraform para GCP
- Monitoring stack (Prometheus, Grafana, Loki)
- Scripts de deployment y validacion

### 3.4 Repo `automation` (sin IA)

Gestionado por **Juan Jose Gomez** (Marketing Tech).

**Reglas:**
- No incluir archivos de contexto IA
- No asignar ninguna IA
- Juan Jose tiene autonomia total
- Sincronizacion con backend via webhooks n8n
- Cambios cross-repo se comunican via GitHub Issues

---

## 4. Contexto IA por repositorio

Cada repo tendra un archivo de contexto **enfocado y breve** (maximo
150 lineas) que contiene SOLO lo necesario para trabajar en ese repo:

```
backend-services/CLAUDE.md
├── Proposito del repo
├── Stack: Java 21, Spring Boot 3.3.13, PostgreSQL 16, NATS 2.10
├── Estructura de paquetes (packages)
├── Comandos: build, test, jib
├── Variables de entorno
├── Convenciones de codigo
├── Endpoints y puertos por servicio
└── Dependencias criticas y versiones

frontend/.github/copilot-instructions.md
├── Proposito del repo
├── Stack: Next.js 16, React 18, TypeScript 5, Tailwind 3
├── Estructura de carpetas (src/components, src/hooks, etc.)
├── Comandos: dev, build, lint
├── Contratos API (URL base, endpoints principales)
├── Convenciones UI (colores, spacing, componentes)
└── Variables de entorno (.env)

platform/GEMINI.md
├── Proposito del repo
├── Inventario de servicios (nombre, imagen, puerto)
├── Compose: como levantar/detener
├── Helm: estructura de charts
├── Monitoring: stack y dashboards
├── CI/CD: pipelines existentes
├── Scripts disponibles
└── Variables de entorno (.env.example)
```

**Regla critica**: No incluir historiales de sesion, lecciones
aprendidas obsoletas, hitos completados, ni informacion que no sea
directamente util para producir codigo. El contexto debe ser
**operativo, no historico**.

---

## 5. Workflow diario del desarrollador

### 5.1 Setup inicial (una sola vez)

```bash
# Crear carpeta de trabajo
mkdir ~/Carrillo-Abogados && cd ~/Carrillo-Abogados

# Clonar los 4 repos
git clone https://github.com/Carrillo-Abogados/backend-services.git
git clone https://github.com/Carrillo-Abogados/frontend.git
git clone https://github.com/Carrillo-Abogados/platform.git
git clone https://github.com/Carrillo-Abogados/automation.git

# Copiar .env desde template
cp platform/.env.example platform/.env
# Editar platform/.env con credenciales locales
```

### 5.2 Levantar el stack completo

```bash
# 1. Build imagenes backend (desde backend-services/)
cd ~/Carrillo-Abogados/backend-services
mvn clean package jib:buildTar -DskipTests -T 1C
# Cargar imagenes en Docker:
for svc in api-gateway client-service case-service payment-service \
           document-service calendar-service notification-service \
           n8n-integration-service; do
  docker load -i $svc/target/jib-image.tar
done

# 2. Build imagen frontend (desde frontend/)
cd ~/Carrillo-Abogados/frontend
docker build -t carrilloabogados/frontend:dev .

# 3. Levantar stack (desde platform/)
cd ~/Carrillo-Abogados/platform
docker compose up -d --pull never
```

### 5.3 Sesion de trabajo tipica

```
Mañana:
1. Abrir GitHub Projects → ver tablero Kanban → elegir tasks
2. Asignar cada task a un repo (backend/frontend/platform)

Trabajo paralelo (3 terminales):
Terminal 1 → Claude Code en backend-services/
Terminal 2 → VSCode + Copilot en frontend/
Terminal 3 → Gemini CLI en platform/

Al finalizar cada sesion:
1. Commit + push en el repo de trabajo
2. Si hubo cambio de API: crear Issue en repos afectados
3. Mover card en GitHub Projects
```

### 5.4 Cuando se modifica un contrato API

Si el backend cambia un endpoint, el frontend necesita actualizarse:

1. Backend (Claude) modifica el endpoint en `backend-services`
2. Claude documenta el cambio en el commit message
3. El desarrollador crea un GitHub Issue en `frontend`:
   `Sync: endpoint POST /api/leads changed response format`
4. Al abrir Copilot en `frontend`, el Issue da contexto del cambio

No se usa un repo `docs` separado. Los contratos se sincronizan
via Issues de GitHub y las specs OpenAPI auto-generadas por
springdoc en `localhost:{port}/v3/api-docs`.

---

## 6. Coordinacion cross-repo: GitHub Projects

Se reemplaza `PROYECTO_ESTADO.md` como herramienta de tracking por
**GitHub Projects** a nivel de organizacion:

### Tablero Kanban

```
| Backlog | To Do | In Progress | Review | Done |
|---------|-------|-------------|--------|------|
| feat:   | fix:  | [Claude]    |  PR    |      |
| payment | cors  | case-svc    | review |      |
| service | bug   | tests       |        |      |
```

### Etiquetas por repo
- `repo:backend` (azul)
- `repo:frontend` (verde)
- `repo:platform` (naranja)
- `repo:automation` (morado)

### Etiquetas por prioridad
- `P0:critical` - Bloqueador de MVP
- `P1:high` - Necesario para MVP
- `P2:medium` - Mejora importante
- `P3:low` - Deseable

### Ventajas sobre PROYECTO_ESTADO.md
- Visible en tiempo real desde cualquier repo
- Cada card puede vincularse a PRs y commits
- Automatizable con GitHub Actions
- No requiere actualizar un archivo manualmente

> `PROYECTO_ESTADO.md` se mantiene en `platform/` como referencia
> historica pero ya no es la fuente de verdad para tracking diario.

---

## 7. CI/CD independiente por repo

### 7.1 backend-services: ci.yml

```yaml
name: Backend CI
on:
  push:
    branches: [dev, main]
  pull_request:
    branches: [dev, main]

jobs:
  build-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: temurin
          cache: maven
      - run: chmod +x ./mvnw
      - run: ./mvnw clean verify -T 1C -B

  security:
    needs: build-test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: aquasecurity/trivy-action@master
        with:
          scan-type: fs
          severity: HIGH,CRITICAL

  docker:
    needs: [build-test, security]
    if: github.event_name == 'push'
    runs-on: ubuntu-latest
    strategy:
      matrix:
        service:
          - api-gateway
          - client-service
          - case-service
          - payment-service
          - document-service
          - calendar-service
          - notification-service
          - n8n-integration-service
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: temurin
          cache: maven
      - run: chmod +x ./mvnw
      - name: Build with Jib
        run: |
          ./mvnw -pl ${{ matrix.service }} package jib:build \
            -DskipTests -B \
            -Djib.to.image=ghcr.io/${{ github.repository_owner }}/${{ matrix.service }}:${{ github.ref_name }} \
            -Djib.to.auth.username=${{ github.actor }} \
            -Djib.to.auth.password=${{ secrets.GITHUB_TOKEN }}
```

### 7.2 frontend: ci.yml

```yaml
name: Frontend CI
on:
  push:
    branches: [dev, main]
  pull_request:
    branches: [dev, main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: 20
          cache: npm
      - run: npm ci
      - run: npm run lint
      - run: npm run type-check
      - run: npm run build

  docker:
    needs: build
    if: github.event_name == 'push'
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}
      - uses: docker/build-push-action@v5
        with:
          context: .
          push: true
          tags: ghcr.io/${{ github.repository_owner }}/frontend:${{ github.ref_name }}
```

### 7.3 platform: validate.yml

```yaml
name: Platform Validate
on:
  push:
    branches: [dev, main]
  pull_request:
    branches: [dev, main]

jobs:
  validate:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Validate compose
        run: docker compose config -q
      - name: Lint Helm charts
        uses: helm/chart-testing-action@v2
        with:
          command: lint
          chart-dirs: helm-charts
```

---

## 8. Docker Compose multi-repo

### 8.1 compose.yml en platform/

El compose.yml se modifica para **solo referenciar imagenes**
(sin `build:` contexts). Cada imagen se construye en su repo.

```yaml
services:
  # --- INFRAESTRUCTURA ---
  postgresql:
    image: postgres:16-alpine
    # ... (sin cambios)

  nats:
    image: nats:2.10-alpine
    # ... (sin cambios)

  # --- BACKEND (imagenes pre-construidas) ---
  api-gateway:
    image: carrilloabogados/api-gateway:dev
    # NO tiene build: context

  client-service:
    image: carrilloabogados/client-service:dev
    # NO tiene build: context

  # ... (igual para los 6 servicios restantes)

  # --- FRONTEND (imagen pre-construida) ---
  frontend:
    image: carrilloabogados/frontend:dev
    # Antes tenia build: context: ./frontend
    # Ahora es imagen pre-construida
```

### 8.2 Script de build completo: scripts/build-all.sh

```bash
#!/bin/bash
# Ejecutar desde la carpeta padre (~/Carrillo-Abogados/)
set -e

echo "=== Building backend images ==="
cd backend-services
mvn clean package jib:buildTar -DskipTests -T 1C
for svc in api-gateway client-service case-service payment-service \
           document-service calendar-service notification-service \
           n8n-integration-service; do
  docker load -i $svc/target/jib-image.tar
  echo "  Loaded: carrilloabogados/$svc:dev"
done

echo "=== Building frontend image ==="
cd ../frontend
docker build -t carrilloabogados/frontend:dev .

echo "=== Starting stack ==="
cd ../platform
docker compose up -d --pull never

echo "=== Done. Waiting for health checks... ==="
sleep 30
docker compose ps
```

### 8.3 Script PowerShell equivalente: scripts/build-all.ps1

```powershell
# Ejecutar desde la carpeta padre (Carrillo-Abogados/)
$ErrorActionPreference = "Stop"

Write-Host "=== Building backend images ===" -ForegroundColor Cyan
Push-Location backend-services
.\mvnw clean package jib:buildTar -DskipTests "-T" 1C
$services = @("api-gateway","client-service","case-service",
              "payment-service","document-service","calendar-service",
              "notification-service","n8n-integration-service")
foreach ($svc in $services) {
    docker load -i ".\$svc\target\jib-image.tar"
    Write-Host "  Loaded: carrilloabogados/${svc}:dev" -ForegroundColor Green
}
Pop-Location

Write-Host "=== Building frontend image ===" -ForegroundColor Cyan
Push-Location frontend
docker build -t carrilloabogados/frontend:dev .
Pop-Location

Write-Host "=== Starting stack ===" -ForegroundColor Cyan
Push-Location platform
docker compose up -d --pull never
Start-Sleep 30
docker compose ps
Pop-Location
```

---

## 9. Pre-migracion: limpieza obligatoria

Antes de ejecutar `git filter-repo`, el monorepo debe estar limpio.
Estas tareas deben completarse ANTES de la migracion:

### 9.1 Eliminar duplicados

- [x] Eliminar `k8s-manifests/` de la raiz (duplicado de
      `infrastructure/k8s-manifests/` con contenido real)
- [ ] Eliminar `user-service/target/` (directorio local huerfano,
      no git-tracked, borrar manualmente)

### 9.2 Corregir informacion desactualizada

- [x] `copilot-instructions.md`: eliminar mencion a "academico
      (Plataformas II)" - corregido a "100% Empresarial"
- [x] `PROYECTO_ESTADO.md`: seccion "Commits Recientes" eliminada
      (era de enero, desactualizada)
- [x] Verificar que ningun documento referencia `user-service`
      como activo (eliminado de k8s configmaps, network policies,
      helm charts, VERSION_STABILITY.md reactor summary)
- [x] Verificar que ningun documento referencia el repo `docs`
      separado (confirmado: no existe tal referencia)

### 9.3 Consolidar documentacion

- [x] Mover docs operacionales a ubicaciones finales:
      scripts/*.md → docs/operations/ (2 archivos movidos)
- [x] Eliminar archivos obsoletos o redundantes:
      docs/archive/ vaciado (2 historicos eliminados),
      helm-charts user-service eliminado (8 archivos)
- [x] Cada documento con fecha y fase correctas:
      13 archivos actualizados (FASE 10→14, fechas, datos)

### 9.4 Validar codigo

- [ ] `mvn clean verify -T 1C` pasa exitosamente
- [ ] `cd frontend && npm run build` pasa exitosamente
- [ ] `docker compose config -q` no reporta errores
- [ ] No hay archivos con credenciales hardcodeadas

---

## 10. Plan de migracion (paso a paso)

### Fase 1: Limpieza pre-migracion

Completar todas las tareas de la Seccion 9.

### Fase 2: Crear repositorios en la organizacion

```bash
# Desde GitHub CLI (gh)
gh repo create Carrillo-Abogados/backend-services --private
gh repo create Carrillo-Abogados/frontend --private
gh repo create Carrillo-Abogados/platform --private
gh repo create Carrillo-Abogados/automation --private
```

Configuracion por repo:
- Default branch: `dev`
- Branch protection en `main`: requiere PR

### Fase 3: Ejecutar git filter-repo

Ejecutar en WSL o Git Bash (no PowerShell directamente):

```bash
# Prerequisito
pip install git-filter-repo

# Directorio de trabajo temporal
mkdir ~/migration && cd ~/migration

# Clonar monorepo como base
git clone https://github.com/AlexisJ16/CarrilloAbogados.git source
```

#### 3a. Backend Services

```bash
cp -r source backend-services && cd backend-services
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
  --path mvnw \
  --path mvnw.cmd \
  --path .mvn/ \
  --path sonar-project.properties
git remote add origin https://github.com/Carrillo-Abogados/backend-services.git
git push origin --all --tags
cd ..
```

#### 3b. Frontend

```bash
cp -r source frontend && cd frontend
git filter-repo --path frontend/ --path-rename frontend/:
git remote add origin https://github.com/Carrillo-Abogados/frontend.git
git push origin --all --tags
cd ..
```

#### 3c. Platform

```bash
cp -r source platform && cd platform
git filter-repo \
  --path helm-charts/ \
  --path infrastructure/ \
  --path monitoring/ \
  --path compose.yml \
  --path scripts/ \
  --path .env.example \
  --path docs/architecture/ \
  --path docs/operations/ \
  --path docs/business/ \
  --path docs/security/ \
  --path docs/development/ \
  --path PROYECTO_ESTADO.md
git remote add origin https://github.com/Carrillo-Abogados/platform.git
git push origin --all --tags
cd ..
```

#### 3d. Automation

```bash
cp -r source automation && cd automation
git filter-repo --path automation/ --path-rename automation/:
git remote add origin https://github.com/Carrillo-Abogados/automation.git
git push origin --all --tags
cd ..
```

### Fase 4: Configurar cada repositorio

**En cada repo (excepto automation):**

1. Crear archivo de contexto IA enfocado (CLAUDE.md / GEMINI.md /
   copilot-instructions.md) - maximo 150 lineas, solo info operativa
2. Crear `.gitignore` especifico del stack
3. Crear `README.md` con setup rapido
4. Crear `.github/workflows/ci.yml` (ver Seccion 7)
5. Crear `.github/dependabot.yml`
6. Verificar que `dev` es el default branch

**En `platform/`:**

1. Modificar `compose.yml`: eliminar `build: context` del frontend,
   usar solo imagenes
2. Crear `scripts/build-all.sh` y `scripts/build-all.ps1`
3. Mover `.env.example` a la raiz del repo
4. Crear `GEMINI.md` con inventario de servicios

### Fase 5: Configuracion a nivel de organizacion

- [ ] Migrar GitHub Secrets a nivel de org (GITHUB_TOKEN ya es
      automatico, migrar SONAR_TOKEN, SNYK_TOKEN)
- [ ] Crear GitHub Project (Kanban board) a nivel de org
- [ ] Verificar permisos de ghcr.io para la org
- [ ] Crear `.github` repo con profile README (opcional, 5 min)

### Fase 6: Validacion

- [ ] `mvn clean verify -T 1C` pasa en `backend-services`
- [ ] `npm run build` pasa en `frontend`
- [ ] `docker compose config -q` pasa en `platform`
- [ ] CI/CD pipeline verde en `backend-services`
- [ ] CI/CD pipeline verde en `frontend`
- [ ] Stack completo levanta desde `platform/`
- [ ] 11/11 contenedores healthy
- [ ] Health checks en puertos 8080, 8200-8800 responden
- [ ] Monorepo original archivado (read-only)

---

## 11. Riesgos y mitigacion

| Riesgo | Prob. | Impacto | Mitigacion |
|--------|-------|---------|------------|
| Perdida de historial git | Baja | Alto | filter-repo preserva commits por archivo |
| Parent POM roto | Baja | Alto | POM y todos los modulos viajan juntos |
| Compose no levanta | Media | Alto | Validar compose.yml antes de migrar |
| API desincronizada | Media | Medio | Issues cross-repo + springdoc auto-specs |
| Env vars perdidas | Baja | Alto | .env.example verificado pre-migracion |
| CI/CD roto | Media | Medio | Pipelines probados individualmente |

---

## 12. Rollback

1. El monorepo original **no se elimina** - se archiva como read-only
2. Los repos nuevos pueden borrarse y recrearse desde el monorepo
3. Branches `dev` y `main` del monorepo se preservan
4. `git filter-repo` es idempotente: se puede repetir desde cero

---

## 13. Consideraciones para automation/

El repositorio `automation` es **100% independiente** y gestionado
por **Juan Jose Gomez** (Marketing Tech).

**Reglas:**

1. No incluir archivos de contexto IA
2. No asignar ninguna IA para operar sobre este repo
3. No modificar ningun archivo durante la migracion
4. Juan Jose mantiene acceso directo y autonomia completa
5. Sincronizacion con backend via webhooks n8n
6. Cambios cross-repo se comunican via GitHub Issues

---

## 14. Timeline

| Fase | Duracion | Prerequisito |
|------|----------|-------------|
| Limpieza pre-migracion | 2-4 horas | Auditoria completada |
| Crear repos en org | 15 min | Acceso admin a org |
| git filter-repo | 1 hora | Repos creados |
| Configurar repos | 2-3 horas | Repos poblados |
| Config org (secrets, projects) | 1 hora | Repos configurados |
| Validacion completa | 1-2 horas | Todo configurado |
| **Total** | **~1 dia** | - |

---

## 15. Checklist final

```
PRE-MIGRACION
[ ] Auditoria de documentacion completada
[ ] Codigo limpio (build + tests pasan)
[ ] Duplicados eliminados
[ ] Info desactualizada corregida
[ ] Monorepo en estado limpio en branch dev

MIGRACION
[ ] 4 repos creados en org
[ ] filter-repo ejecutado para cada repo
[ ] Todos los repos tienen codigo correcto

POST-MIGRACION
[ ] CLAUDE.md creado en backend-services
[ ] copilot-instructions.md creado en frontend
[ ] GEMINI.md creado en platform
[ ] CI/CD funcionando en backend-services
[ ] CI/CD funcionando en frontend
[ ] compose.yml actualizado en platform (sin build contexts)
[ ] build-all scripts creados en platform
[ ] GitHub Project configurado
[ ] Secrets migrados a org
[ ] Stack completo levanta (11/11 healthy)
[ ] Monorepo archivado

VALIDACION
[ ] Claude Code abre backend-services con contexto limpio
[ ] Copilot abre frontend con contexto limpio
[ ] Gemini CLI abre platform con contexto limpio
[ ] 3 IAs pueden trabajar en paralelo sin interferencia
```

---

*Guia v3.0 - Reescrita 16 Feb 2026*
