# AI Coding Agent Instructions - Carrillo Abogados Legal Tech Platform

## Project Overview

Cloud-native legal management platform with 8 Spring Boot microservices on Kubernetes. **Dual purpose**: Academic project (Plataformas II) + Real production system for 5-lawyer firm in Cali, Colombia. Built with zero vendor lock-in using Spring Cloud Kubernetes instead of Eureka/Config Server.

## Critical Architecture Decisions

### Database Strategy (ADR-005)

- **Single PostgreSQL** with separate schemas per service (not databases)
- Schema names: `clients`, `cases`, `documents`, `payments`, `calendar`, `notifications`
- Connect via: `kubectl exec -it postgresql-0 -n databases -- psql -U carrillo -d carrillo_legal_tech`
- Switch schemas: `SET search_path TO clients;`
- Managed by Flyway (check migrations in each service's `src/main/resources/db/migration/`)

### Service Discovery

- **Kubernetes DNS** native (no Eureka)
- Services call via `lb://SERVICE-NAME` in Spring Cloud Gateway
- Check API Gateway routes: [api-gateway/src/main/resources/application.yml](../api-gateway/src/main/resources/application.yml) lines 36-55

### Messaging

- Dev/Staging: **NATS** (`nats://nats.messaging.svc.cluster.local:4222`)
- Production: **Google Pub/Sub** (not yet configured)
- Event-driven architecture for N8N integration

### Authentication

- OAuth2 via Google Workspace (`@carrilloabgd.com`)
- Legal traceability requirement: every action logged with user email
- Configured in API Gateway (not in individual services)

## Core Microservices

| Service                 | Port | Purpose                                         | Status     |
| ----------------------- | ---- | ----------------------------------------------- | ---------- |
| api-gateway             | 8080 | Spring Cloud Gateway + OAuth2                   | Active     |
| client-service          | 8200 | Client management (replacing user-service)      | Active     |
| case-service            | 8300 | Legal case management (replacing order-service) | Active     |
| payment-service         | 8400 | Government payment processing                   | Active     |
| document-service        | 8500 | Legal document storage                          | Active     |
| calendar-service        | 8600 | Google Calendar integration                     | Active     |
| notification-service    | 8700 | Email/SMS via Gmail API                         | Active     |
| n8n-integration-service | 8800 | N8N Pro workflow bridge                         | Active     |
| user-service            | 8100 | Legacy - migrate to client-service              | Deprecated |

## Essential Development Workflows

### Daily Dev Setup

```bash
./scripts/dev-up.sh  # Smart startup - detects state, only rebuilds what's needed
```

This script:

- Starts Minikube if down
- Creates namespaces (`carrillo-dev`, `databases`, `messaging`)
- Deploys PostgreSQL, NATS if missing
- Builds/deploys changed microservices only
- Port-forwards API Gateway to localhost:8080

### Building Services

```bash
# Parallel build all services (fastest)
./mvnw clean verify -T 1C

# Build without tests (quick iteration)
./mvnw clean package -DskipTests -T 1C

# Build single service
cd client-service && ../mvnw clean package -DskipTests
```

### After Code Changes

1. Build service: `./mvnw -pl <service-name> clean package -DskipTests`
2. Connect to Minikube Docker: `eval $(minikube docker-env)` (Windows: `minikube docker-env --shell powershell | Invoke-Expression`)
3. Build image: `docker build -t carrillo/<service>:0.1.0 -f <service>/Dockerfile <service>/`
4. Helm upgrade: `helm upgrade --install carrillo-dev helm-charts/carrillo-abogados/ -n carrillo-dev`

### Kubernetes Commands

```bash
# Check deployment status
kubectl get pods -n carrillo-dev
kubectl logs -f deployment/client-service -n carrillo-dev

# Database access
kubectl port-forward svc/postgresql 5432:5432 -n databases

# NATS testing
kubectl exec -n messaging deployment/nats-box -- nats pub test "Hello"

# Gateway access
kubectl port-forward svc/api-gateway 8080:8080 -n carrillo-dev
```

## Project Conventions

### Package Structure

```
com.carrilloabogados.<service>/
├── controller/     # REST endpoints
├── service/        # Business logic
├── repository/     # JPA repositories
├── model/          # JPA entities
├── dto/            # Request/Response objects
└── config/         # Spring configurations
```

### Application Configuration Pattern

Each service has:

- `application.yml`: Base config with Spring app name, circuit breakers, logging patterns
- `application-dev.yml`: Local development (H2 or Docker PostgreSQL)
- `application-k8s.yml`: Kubernetes config (activated by Helm chart)

**Check service name**: Always uppercase with dashes in `application.yml` (e.g., `CLIENT-SERVICE`), used in Gateway routing

### Docker Images

- Naming: `carrillo/<service-name>:0.1.0`
- Built in Minikube's Docker (use `eval $(minikube docker-env)`)
- Dockerfiles use multi-stage builds with JDK 21
- Base image: `eclipse-temurin:21-jre-jammy`

### Helm Charts

- Umbrella chart: [helm-charts/carrillo-abogados/](../helm-charts/carrillo-abogados/)
- Service flags in [values.yaml](../helm-charts/carrillo-abogados/values.yaml) lines 7-39
- Disable legacy services for new deployments:
  ```yaml
  user-service:
    enabled: false
  ```

## Common Integration Points

### Adding a New Microservice Route

1. Add route in [api-gateway/src/main/resources/application.yml](../api-gateway/src/main/resources/application.yml)
2. Use `lb://<SERVICE-NAME>` format (Kubernetes load balancing)
3. Path predicate: `/service-name/**`
4. Service name MUST match `spring.application.name` in target service

### Database Migrations

- Use Flyway: `src/main/resources/db/migration/V1__initial_schema.sql`
- Naming: `V{version}__{description}.sql`
- Set schema in service's `application-k8s.yml`: `spring.flyway.schemas: <schema_name>`
- View applied migrations: `SELECT * FROM flyway_schema_history;`

### Event Publishing (NATS)

```java
@Autowired
private NatsTemplate natsTemplate;

natsTemplate.publish("carrillo.events.client.created", clientDto);
```

Topic pattern: `carrillo.events.<domain>.<action>`

## ⚠️ CRITICAL: Windows + WSL Development Environment

### Environment Configuration

- **Host OS**: Windows 11
- **WSL Distribution**: Ubuntu-24.04 (default)
- **Minikube**: Runs inside WSL with Docker driver
- **kubectl**: Installed in WSL, NOT in Windows native

### How to Execute Commands from PowerShell

**ALL Kubernetes/Minikube/Helm commands MUST be executed through WSL:**

```powershell
# ✅ CORRECT - Use wsl bash -c "command"
wsl bash -c "kubectl get pods -n carrillo-dev"
wsl bash -c "minikube status"
wsl bash -c "helm list -n carrillo-dev"
wsl bash -c "./scripts/deploy.sh"

# ❌ WRONG - Do NOT run kubectl directly in PowerShell
kubectl get pods  # This fails - Windows kubectl has no config for Minikube
```

### Restarting WSL (Fixes Most Stability Issues)

```powershell
# Run as Administrator in PowerShell:
wsl --shutdown

# Wait 10 seconds, then:
wsl bash -c "minikube start"
wsl bash -c "kubectl get pods -A"
```

### Available WSL Distributions

```powershell
wsl --list  # Shows: Ubuntu-24.04 (Default), docker-desktop
```

### Port Forwarding (Access Services from Windows)

```powershell
# Start port-forward in background
wsl bash -c "kubectl port-forward svc/carrillo-dev-api-gateway 8080:8080 -n carrillo-dev &"

# Then access from Windows browser: http://localhost:8080
```

## Troubleshooting

### Minikube Keeps Stopping

This is usually a cgroups issue in WSL. Solution:

```powershell
wsl --shutdown
# Wait 10 seconds
wsl bash -c "minikube start"
```

### Pod CrashLoopBackOff

```bash
kubectl describe pod <pod-name> -n carrillo-dev
kubectl logs <pod-name> -n carrillo-dev --previous
```

Common causes: Database schema missing, incorrect service name in Gateway

### Service Not Reachable

1. Check pod status: `kubectl get pods -n carrillo-dev`
2. Verify service registration: `kubectl get svc -n carrillo-dev`
3. Check Gateway routes: Access `http://localhost:8080/actuator/gateway/routes`

### Build Failures

- Clean Maven cache: `./mvnw clean`
- Remove target dirs: `find . -name "target" -type d -exec rm -rf {} +`
- Check Java version: `java -version` (must be 21)

### Full Environment Reset

```powershell
wsl --shutdown
wsl bash -c "minikube delete && minikube start"
wsl bash -c "./scripts/deploy.sh"
```

## Tech Stack Reference

- **Java**: 21 LTS (required)
- **Spring Boot**: 3.3.13
- **Spring Cloud**: 2023.0.6
- **Spring Cloud Kubernetes**: 3.1.3 (replaces Eureka)
- **Kubernetes**: 1.34.0
- **PostgreSQL**: 16.2 (Bitnami Helm chart 15.5.38)
- **NATS**: 2.10.22
- **Maven**: 3.8+ (use included `./mvnw`)

## Documentation References

- Architecture deep-dive: [docs/architecture/ARCHITECTURE.md](../docs/architecture/ARCHITECTURE.md)
- Database rationale: [docs/architecture/ADR-005-database-strategy.md](../docs/architecture/ADR-005-database-strategy.md)
- Operations guide: [docs/operations/OPERATIONS.md](../docs/operations/OPERATIONS.md)
- Full context: [CLAUDE.md](../CLAUDE.md) (comprehensive developer reference)
- Project status: [PROYECTO_ESTADO.md](../PROYECTO_ESTADO.md) (current state)

## Git Workflow

- `main`: Production-ready (protected)
- `staging`: Pre-production testing
- `dev`: Active development (current working branch)

Always create feature branches from `dev`, not `main`.

## Recent Fixes Applied (Dec 18, 2025)

1. **PostgreSQL DATEDIFF query** - Changed to PostgreSQL syntax in LegalCaseRepository
2. **Health probes** - Added context-path prefix (/case-service/, /client-service/)
3. **RBAC** - Created service-discovery-role for Kubernetes service discovery
4. **Schemas** - Created 7 PostgreSQL schemas for all services
5. **compose.yml** - Completely rewritten for legal tech (removed e-commerce legacy)
6. **test.sh** - Improved with context-path aware health checks

## Marketing Automation Integration (Dec 19, 2025)

### n8n Cloud Integration

The platform integrates with n8n Cloud for marketing automation through 3 MEGA-WORKFLOWS:

| MEGA-WORKFLOW   | Purpose                          | Workflows | Nodes |
| --------------- | -------------------------------- | --------- | ----- |
| MW#1: Captura   | Lead → Client (<1 min response)  | 7         | 108   |
| MW#2: Retención | Client → Recompra (Flywheel)     | 5         | 72    |
| MW#3: SEO       | Traffic → Lead (Content Factory) | 5         | 60    |

### n8n-integration-service Key Endpoints

**Webhooks to expose (n8n → Platform):**

- `POST /webhook/lead-scored` - Receive lead score from n8n
- `POST /webhook/lead-hot` - Notify lawyer of hot lead (≥70 pts)
- `POST /webhook/upsell-detected` - Create upsell opportunity

**Events to emit (Platform → n8n via NATS):**

- `lead.capturado` → MW#1 SUB-A (scoring)
- `cita.agendada` → MW#1 SUB-F (confirmation)
- `caso.cerrado` → MW#2 (follow-up)
- `cliente.inactivo` → MW#2 (reactivation)

### Lead Scoring (calculated by n8n)

```
Base: 30 pts
+ Service "marca"/"litigio": +20 pts
+ Message > 50 chars: +10 pts
+ Has phone: +10 pts
+ Has company: +10 pts
+ Corporate email: +10 pts
+ C-Level role: +20 pts
────────────────────────────
HOT:  ≥70 pts → Immediate notification
WARM: 40-69 pts → AI nurturing
COLD: <40 pts → Generic response
```

### Business Documentation

- [MODELO_NEGOCIO.md](../docs/business/MODELO_NEGOCIO.md) - Business context & metrics
- [REQUERIMIENTOS.md](../docs/business/REQUERIMIENTOS.md) - 76 functional + 23 non-functional requirements
- [ESTRATEGIA_AUTOMATIZACION.md](../docs/business/ESTRATEGIA_AUTOMATIZACION.md) - n8n integration strategy
- [ARQUITECTURA_FUNCIONAL.md](../docs/business/ARQUITECTURA_FUNCIONAL.md) - Microservice mapping

## 🚨 LESSONS LEARNED - Avoid These Errors (Dec 19, 2025)

### 1. Jackson Instant/LocalDateTime Serialization

**Problem**: `InvalidDefinitionException: Java 8 date/time type 'java.time.Instant' not supported`
**Cause**: Jackson no tiene soporte nativo para java.time.Instant sin configuración explícita
**Solution**: SIEMPRE crear JacksonConfig.java al iniciar cualquier microservicio:

```java
@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
```

### 2. NATS EventPublisher con Nullable Connection

**Problem**: NullPointerException cuando NATS no está disponible
**Cause**: @Autowired falla silenciosamente cuando bean no existe
**Solution**: Usar @Nullable y verificar null antes de operar:

```java
public class NatsEventPublisher implements EventPublisher {
    private final Connection natsConnection;

    public NatsEventPublisher(@Nullable Connection natsConnection) {
        this.natsConnection = natsConnection;
    }

    public void publish(String topic, Object event) {
        if (natsConnection == null) {
            log.warn("NATS not available, skipping event");
            return;
        }
        // publicar evento
    }
}
```

### 3. Flyway + PostgreSQL 16 Incompatibilidad

**Problem**: Flyway 10.10.0 no soporta PostgreSQL 16.11
**Solution temporal**: `spring.flyway.enabled: false` + `hibernate.ddl-auto: update`
**Solution permanente**: Añadir `flyway-database-postgresql` dependency

### 4. Health Check Paths con Context-Path

**Problem**: Health checks fallan con 404
**Cause**: Servicios con context-path `/service-name/` necesitan ruta completa
**Solution**: En Dockerfiles usar:

```dockerfile
HEALTHCHECK --start-period=60s CMD curl -f http://localhost:8200/client-service/actuator/health || exit 1
```

### 5. Docker Compose Variables de Entorno

**Problem**: Servicios no conectan a PostgreSQL/NATS en Docker Compose
**Cause**: Variables de entorno no propagadas al contenedor
**Solution**: SIEMPRE verificar que compose.yml incluya:

```yaml
environment:
  - POSTGRES_HOST=postgresql
  - POSTGRES_PORT=5432
  - NATS_ENABLED=true
  - NATS_SERVER=nats://nats:4222
```

## 📋 CHECKLIST PARA NUEVOS MICROSERVICIOS

Antes de considerar un microservicio "completo", verificar:

### Configuración Básica

- [ ] JacksonConfig.java con JavaTimeModule
- [ ] application.yml con spring.application.name en MAYÚSCULAS
- [ ] application-local.yml para Docker Compose
- [ ] application-k8s.yml para Kubernetes
- [ ] Hibernate ddl-auto: validate (producción) o update (desarrollo)

### Entidades y Persistencia

- [ ] Entities con @Id UUID
- [ ] Repository con queries custom necesarias
- [ ] Migración Flyway V{N}\__create_{entity}\_table.sql

### API REST

- [ ] Resource/Controller con todos los endpoints
- [ ] DTOs con validaciones @NotNull, @Email, @Size
- [ ] Mapper Entity ↔ DTO

### Eventos NATS

- [ ] EventPublisher interface
- [ ] NatsEventPublisher con @Nullable
- [ ] Event classes con datos pre-calculados

### Tests

- [ ] \*ServiceTest.java (unitarios con Mockito)
- [ ] \*ResourceTest.java (integración con MockMvc)
- [ ] E2E manual con curl/Invoke-RestMethod

### Documentación

- [ ] OpenAPI spec exportada
- [ ] Frontend types generados
- [ ] README actualizado

## 🔧 RECOMMENDED TOOLS & EXTENSIONS

### VS Code Extensions (Recomendadas)

- `vscjava.vscode-java-pack` - Java Extension Pack
- `pivotal.vscode-spring-boot` - Spring Boot Tools
- `vscjava.vscode-maven` - Maven for Java
- `ms-azuretools.vscode-docker` - Docker
- `ms-kubernetes-tools.vscode-kubernetes-tools` - Kubernetes
- `redhat.vscode-yaml` - YAML support
- `humao.rest-client` - REST Client (para probar APIs)

### MCPs Disponibles (ya activados)

- **GitHub MCP**: Gestión de repos, PRs, issues
- **Docker MCP**: Gestión de contenedores, imágenes
- **Octopus Deploy MCP**: Para deployments (futuro)

### Tips para Desarrollo Eficiente

1. Usar `docker-compose logs -f service-name` para debugging
2. Mantener PROYECTO_ESTADO.md actualizado después de cada sesión
3. Ejecutar tests antes de commit: `./mvnw test -pl service-name`
4. Usar semantic_search para encontrar patrones existentes en el código
