# Carrillo Abogados - AI Agent Instructions

**Última Actualización**: 21 de Enero, 2026  
**Fase**: FASE 13 - Producción Sincronizada  
**MVP**: 27 Marzo 2026

---

## 🎯 Proyecto

Plataforma legal cloud-native con **8 microservicios Spring Boot** sobre Kubernetes para bufete de 7 abogados en Cali, Colombia. Propósito dual: académico (Plataformas II) + producción real.

---

## 🛠️ Stack Tecnológico

| Capa                | Tecnologías                                                      |
| ------------------- | ---------------------------------------------------------------- |
| **Backend**         | Java 21, Spring Boot 3.3.13, Spring Cloud Kubernetes 3.1.3       |
| **Frontend**        | Next.js 14, React 18, TypeScript 5, Tailwind CSS, TanStack Query |
| **Base de Datos**   | PostgreSQL 16.2 (schemas separados por servicio)                 |
| **Mensajería**      | NATS 2.10 (dev/staging), Google Pub/Sub (prod)                   |
| **Infraestructura** | Docker, Minikube (WSL), Helm, GKE Autopilot                      |
| **CI/CD**           | GitHub Actions → ghcr.io                                         |

---

## 📦 Microservicios

| Servicio                | Puerto | Propósito                     |
| ----------------------- | ------ | ----------------------------- |
| api-gateway             | 8080   | Spring Cloud Gateway + OAuth2 |
| client-service          | 8200   | Clientes + Leads + Auth JWT   |
| case-service            | 8300   | Casos legales + Timeline      |
| payment-service         | 8400   | Pagos gubernamentales         |
| document-service        | 8500   | Documentos legales            |
| calendar-service        | 8600   | Google Calendar               |
| notification-service    | 8700   | Email/SMS/Push                |
| n8n-integration-service | 8800   | Bridge n8n Cloud              |

---

## ⚠️ REGLA CRÍTICA: Windows + WSL

**TODOS los comandos de Kubernetes/Minikube/Helm deben ejecutarse vía WSL:**

```powershell
# ✅ CORRECTO
wsl bash -c "kubectl get pods -n carrillo-dev"
wsl bash -c "minikube status"
wsl bash -c "helm list -n carrillo-dev"

# ❌ INCORRECTO - Falla en PowerShell nativo
kubectl get pods
```

**Reiniciar WSL si hay problemas:**

```powershell
wsl --shutdown
wsl bash -c "minikube start"
```

---

## 🏗️ Convenciones del Proyecto

### Nombres de Servicios

- En `application.yml`: MAYÚSCULAS con guiones → `CLIENT-SERVICE`
- En Gateway routes: `lb://CLIENT-SERVICE`
- En URLs: minúsculas → `/client-service/**`

### Estructura de Paquetes

```
com.carrilloabogados.<servicio>/
├── controller/   # REST endpoints
├── service/      # Lógica de negocio
├── repository/   # JPA repositories
├── model/        # Entidades JPA
├── dto/          # Request/Response
└── config/       # Configuraciones
```

### Base de Datos

- **Una PostgreSQL** con schemas separados: `clients`, `cases`, `documents`, `payments`, `calendar`, `notifications`
- Migraciones: Flyway en `src/main/resources/db/migration/`

### Eventos NATS

- Patrón: `carrillo.events.<dominio>.<acción>`
- Ejemplo: `carrillo.events.lead.created`

---

## 🤖 AGENTES ESPECIALIZADOS

Para tareas complejas, **consulta el agente apropiado** en `.github/copilot-agents/`:

| Contexto                            | Agente                     | Qué contiene                                            |
| ----------------------------------- | -------------------------- | ------------------------------------------------------- |
| Código Java, Spring Boot, APIs REST | **backend-agent.md**       | Patrones obligatorios, ejemplos de código, convenciones |
| Código TypeScript, React, Next.js   | **frontend-agent.md**      | Tipos sincronizados, hooks, componentes, diseño         |
| Docker, K8s, Helm, CI/CD, WSL       | **devops-agent.md**        | Comandos, troubleshooting, arquitectura infra           |
| Documentación, READMEs, ADRs        | **documentation-agent.md** | Plantillas, timestamps, auditoría                       |

### Cómo Usar los Agentes

1. **Por contexto de archivo**:
   - Editando `*.java` → Consulta `backend-agent.md`
   - Editando `*.tsx` → Consulta `frontend-agent.md`
   - Editando `Dockerfile`, `*.yml` → Consulta `devops-agent.md`
   - Editando `*.md` en `docs/` → Consulta `documentation-agent.md`

2. **Por invocación explícita**:
   ```
   "Siguiendo backend-agent, implementa un nuevo endpoint..."
   "Como devops-agent, resuelve este error de Kubernetes..."
   ```

### Regla de Documentación

> **"Documentación sin fecha es documentación sin valor"**

Todo archivo `.md` modificado debe incluir:

```markdown
**Última Actualización**: DD de Mes, AAAA - HH:MM COT
```

---

## 📂 Documentación Clave

| Documento                                   | Propósito                                      |
| ------------------------------------------- | ---------------------------------------------- |
| [PROYECTO_ESTADO.md](../PROYECTO_ESTADO.md) | Estado actual, hitos, próximos pasos           |
| [CLAUDE.md](../CLAUDE.md)                   | Contexto completo para AI                      |
| [docs/business/](../docs/business/)         | Modelo de negocio, requerimientos, MVP roadmap |
| [docs/architecture/](../docs/architecture/) | ADRs, arquitectura técnica                     |
| [docs/operations/](../docs/operations/)     | Guías de deploy y operaciones                  |

---

## 🔧 Comandos Frecuentes

### Build

```bash
./mvnw clean package -DskipTests -T 1C     # Build rápido paralelo
./mvnw clean verify -T 1C                   # Build + tests
./mvnw -pl client-service package           # Un servicio
```

### Docker Compose (desarrollo local)

```powershell
docker-compose up -d                        # Levantar stack
docker-compose ps                           # Ver estado
docker-compose logs -f client-service       # Logs
```

### Kubernetes (vía WSL)

```powershell
wsl bash -c "kubectl get pods -n carrillo-dev"
wsl bash -c "kubectl logs -f deployment/client-service -n carrillo-dev"
wsl bash -c "helm upgrade --install carrillo-dev helm-charts/carrillo-abogados/ -n carrillo-dev"
```

---

## 🔀 Git Workflow

| Rama      | Propósito                           |
| --------- | ----------------------------------- |
| `main`    | Producción (protegida)              |
| `staging` | Pre-producción                      |
| `dev`     | Desarrollo activo ← **rama actual** |

Crear feature branches desde `dev`, no desde `main`.

---

## 📋 CI/CD

| Workflow             | Trigger               | Estado                  |
| -------------------- | --------------------- | ----------------------- |
| `ci-cd-pipeline.yml` | push dev/staging/main | ✅ Funcional            |
| `security-scan.yml`  | push main, schedule   | ✅ Funcional            |
| `deploy-gcp.yml`     | push main             | ⏳ Requiere secrets GCP |

Imágenes publicadas en: `ghcr.io/alexisj16/<servicio>:dev`

---

## 🔗 Integraciones

### n8n Cloud (Automatizaciones)

> ⚠️ **FUENTE DE VERDAD**: Toda la documentación de n8n está en `/automation/`

- URL: `https://carrilloabgd.app.n8n.cloud`
- Webhook: `/webhook/lead-events`
- 3 MEGA-WORKFLOWS: Captura (MW#1), Retención (MW#2), SEO (MW#3)
- Documentación: [/automation/README.md](../automation/README.md)
- Índice: [/automation/docs/00_INDEX.md](../automation/docs/00_INDEX.md)

### Google Workspace

- Autenticación OAuth2: `@carrilloabgd.com`
- Calendar API, Gmail API integrados

### Rama automation

- **Propósito**: Desarrollo de workflows n8n y documentación de automatización
- **Responsable**: Juan José Gómez Agudelo (Marketing Tech)
- **Integración**: Cambios en `automation/` se integran periódicamente a `dev` vía merge
- **Regla**: Solo modificar contenido dentro de `/automation/` en esta rama

---

_Para contexto detallado, consulta los agentes especializados en `.github/copilot-agents/`_
