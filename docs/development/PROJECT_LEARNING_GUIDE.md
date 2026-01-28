# Guia de Aprendizaje del Proyecto - Carrillo Abogados Legal Tech

**Objetivo**: Dominar este proyecto desde cero, de forma estructurada y con foco en lo critico.

---

## FASE 1: Contexto de Negocio (antes de tocar codigo)

Sin entender el negocio, el codigo no tiene sentido.

### Lecturas obligatorias (en orden):
1. `docs/business/MODELO_NEGOCIO.md` - Que hace Carrillo Abogados, sus 5 areas de practica legal
2. `docs/business/ROLES_USUARIOS.md` - Los 4 tipos de usuario (Visitante, Cliente, Abogado, Admin) y permisos
3. `docs/business/MVP_ROADMAP.md` - Los 5 pilares del MVP, fecha limite: 27 de marzo 2026
4. `docs/business/REQUERIMIENTOS.md` - 150+ requerimientos funcionales y no funcionales

### Dato clave
El sistema busca escalar de **20 leads/mes a 300+** (15x) con respuesta **< 1 minuto** (vs 24h actuales). Todo el diseno gira alrededor de ese objetivo.

---

## FASE 2: Arquitectura General

### Lecturas obligatorias:
1. `CLAUDE.md` (raiz del proyecto) - Resumen ejecutivo: puertos, servicios, stack
2. `docs/architecture/ARCHITECTURE.md` - Vision tecnica completa
3. `docs/architecture/ADR-005-database-strategy.md` - Una sola BD PostgreSQL con 7 schemas separados

### Diagrama de comunicacion
```
Frontend (Next.js :3000)
    -> API Gateway (:8080)
        -> client-service (:8200)    [Leads, Auth, Clientes]
        -> case-service (:8300)      [Casos legales]
        -> notification-service (:8700) [Email/SMS]
        -> n8n-integration-service (:8800) [Bridge con n8n Cloud]
        -> payment-service (:8400)   [Scaffold]
        -> document-service (:8500)  [Scaffold]
        -> calendar-service (:8600)  [Scaffold]
```

### Stack tecnologico
| Capa | Tecnologia |
|------|-----------|
| Frontend | Next.js 14, React 18, TypeScript, Tailwind CSS, TanStack Query |
| Backend | Java 21 LTS, Spring Boot 3.3.13, Spring Cloud 2023.0.6 |
| Base de Datos | PostgreSQL 16 (single DB, 7 schemas) |
| Mensajeria | NATS 2.10 (dev/staging), Google Pub/Sub (prod) |
| Contenedores | Docker, Docker Compose |
| Orquestacion | Kubernetes 1.34.0, Helm Charts |
| CI/CD | GitHub Actions -> ghcr.io |
| Observabilidad | Grafana LGTM (Prometheus, Loki, Tempo, Mimir) |
| Seguridad | OAuth2, JWT, RBAC, Snyk, SonarCloud, Trivy |

---

## FASE 3: El Servicio Principal - `client-service`

Este es el corazon del MVP. Contiene autenticacion, leads y gestion de clientes.

### 3.1 Flujo de autenticacion JWT

Lee estos archivos en orden:

| Orden | Archivo | Que aprendes |
|-------|---------|--------------|
| 1 | `client-service/src/.../config/SecurityConfig.java` | Endpoints publicos vs protegidos, CORS, roles RBAC |
| 2 | `client-service/src/.../security/JwtTokenProvider.java` | Generacion y validacion de tokens JWT |
| 3 | `client-service/src/.../security/JwtAuthenticationFilter.java` | Intercepta cada request HTTP para extraer token del header `Authorization: Bearer <token>` |
| 4 | `client-service/src/.../resource/AuthResource.java` | Endpoints `/api/auth/login`, `/api/auth/register` |

**Permisos por rol:**
```
VISITANTE: POST /api/leads, /api/auth/**, /actuator/**
CLIENTE: GET /api/clients/me
ABOGADO: GET/PUT /api/leads/**, /api/clients/**
ADMIN: Todo, incluyendo DELETE y /api/users/**
```

### 3.2 Flujo de captura de leads (flujo de negocio principal)

| Orden | Archivo | Que aprendes |
|-------|---------|--------------|
| 1 | `client-service/src/.../domain/Lead.java` | Entidad JPA: campos, validaciones, metodos `hasCorpEmail()`, `isCLevel()` |
| 2 | `client-service/src/.../dto/LeadDto.java` | Serializacion hacia el frontend con Jackson |
| 3 | `client-service/src/.../resource/LeadResource.java` | Endpoints REST del CRUD de leads |
| 4 | `client-service/src/.../service/impl/LeadServiceImpl.java` | Logica de negocio: captura, scoring, conversion |
| 5 | `client-service/src/.../repository/LeadRepository.java` | Queries personalizados a PostgreSQL |
| 6 | `client-service/src/.../event/LeadCapturedEvent.java` | Evento emitido a NATS cuando se captura un lead |

### 3.3 Migraciones de base de datos
```
client-service/src/main/resources/db/migration/V12__create_leads_table.sql
```
Flyway ejecuta estas migraciones automaticamente al arrancar. Aqui esta la estructura real de tablas, indices, constraints y triggers.

### 3.4 Lead Scoring (Algoritmo de puntuacion)
```
Base: 30 pts
+ Servicio "marca"/"litigio": +20
+ Mensaje > 50 chars: +10
+ Tiene telefono: +10
+ Tiene empresa: +10
+ Email corporativo: +10
+ Cargo C-Level: +20
---
HOT:  >= 70 pts -> Notificacion inmediata
WARM: 40-69 pts -> Nurturing IA
COLD: < 40 pts  -> Respuesta generica
```

---

## FASE 4: Comunicacion entre Servicios (NATS)

El patron es **event-driven**. No hay llamadas HTTP directas entre microservicios internos (con una excepcion para callbacks).

### Flujo de eventos
```
client-service -> NATS (topic: carrillo.events.lead.capturado) -> n8n-integration-service -> n8n Cloud
```

### Archivos clave
| Archivo | Que aprendes |
|---------|--------------|
| `client-service/src/.../config/NatsConfig.java` | Conexion a NATS (condicional con `nats.enabled`) |
| `client-service/src/.../service/impl/NatsEventPublisher.java` | Serializacion de eventos a JSON y publicacion al topic |
| `n8n-integration-service/src/.../listener/NatsEventListener.java` | Suscripcion y consumo de eventos |
| `n8n-integration-service/src/.../service/N8nWebhookService.java` | Reenvio a n8n Cloud via HTTP POST |

### Topics NATS
```
carrillo.events.lead.capturado  - Lead nuevo capturado
carrillo.events.lead.scored     - Lead puntuado por n8n
carrillo.events.lead.hot        - Lead calificado como HOT
```

**Importante**: NATS es opcional. Si `nats.enabled=false`, el servicio funciona sin mensajeria (degradacion graceful).

---

## FASE 5: API Gateway

Lee `api-gateway/src/main/resources/application.yaml` y `application-local.yml`.

### Responsabilidades
- Recibe todas las peticiones del frontend en el puerto 8080
- Las rutea al microservicio correcto basandose en el path
- Maneja CORS (permite `localhost:3000` para el frontend)
- **NO** maneja autenticacion directamente (cada servicio valida su propio JWT)

### Rutas principales
```
/client-service/**              -> http://client-service:8200
/case-service/**                -> http://case-service:8300
/n8n-integration-service/**     -> http://n8n-integration-service:8800
/payment-service/**             -> http://payment-service:8400
/document-service/**            -> http://document-service:8500
/calendar-service/**            -> http://calendar-service:8600
/notification-service/**        -> http://notification-service:8700
```

---

## FASE 6: Frontend (Next.js 14)

### Archivos clave en orden de lectura
| Archivo | Que aprendes |
|---------|--------------|
| `frontend/next.config.js` | Rewrites para proxy API, headers de seguridad |
| `frontend/src/middleware.ts` | Middleware de autenticacion (protege rutas privadas) |
| `frontend/src/app/layout.tsx` | Layout raiz con providers |
| `frontend/src/components/layout/Header.tsx` | Navegacion con estado de autenticacion |
| `frontend/src/app/login/page.tsx` | Flujo de login |
| `frontend/src/app/contacto/page.tsx` | Formulario de captura de leads |

### Estructura de rutas
```
Publicas: /, /nosotros, /servicios, /equipo, /contacto, /blog
Auth: /login, /register
Protegidas: /dashboard, /leads, /cases, /cases/[id], /notifications
```

### Stack frontend
- Next.js 14 (App Router)
- React 18, TypeScript
- Tailwind CSS
- TanStack Query (cache de API)
- Axios (HTTP client)
- React Hook Form + Zod (validacion de formularios)

---

## FASE 7: Infraestructura y DevOps

### Docker Compose (desarrollo local)
```
compose.yml  <- Levanta todo el ecosistema con un comando
```
Define 10+ contenedores: PostgreSQL, NATS, 8 microservicios, frontend. Red: `carrillo-network`.

### CI/CD Pipeline
```
.github/workflows/ci-cd-pipeline.yml
```
4 jobs: Build & Test (Maven + 105 tests) -> Security Scan (Trivy) -> Docker Build (8 imagenes) -> Pipeline Summary.

### Kubernetes
```
helm-charts/carrillo-abogados/    <- Helm charts para deploy en K8s
infrastructure/k8s-manifests/     <- Manifests (namespaces, RBAC, network policies)
```

### Monitoring (Grafana LGTM Stack)
```
monitoring/docker-compose.observability.yml
```

| Servicio | Puerto | Proposito |
|----------|--------|-----------|
| Grafana | 3100 | Dashboards (admin/carrillo2025) |
| Prometheus | 9090 | Metricas (13 targets) |
| Loki | 3101 | Logs centralizados |
| Tempo | 3102 | Tracing distribuido |
| Mimir | 3103 | Almacenamiento largo plazo |
| Alertmanager | 9093 | Alertas |

---

## FASE 8: Integracion n8n (Marketing Automation)

Lee `docs/architecture/INTEGRACION_N8N.md` y archivos en `automation/`.

### Flujo completo
```
1. Visitante llena formulario en frontend
2. POST /api/leads -> client-service guarda en PostgreSQL
3. client-service emite evento NATS: lead.capturado
4. n8n-integration-service recibe el evento
5. Reenvia via HTTP POST a n8n Cloud (webhook)
6. n8n Cloud procesa: AI scoring con Gemini
7. n8n Cloud llama callback: PATCH /api/leads/{id}/score
8. PostgreSQL actualizado con score y categoria
9. Si HOT -> notificacion inmediata a los abogados
```

### 3 Mega-Workflows
| Workflow | Proposito | Estado |
|----------|-----------|--------|
| MW#1: Captura | Lead -> Cliente (< 1 min) | 95% |
| MW#2: Retencion | Cliente -> Recompra | Q2 2026 |
| MW#3: SEO | Trafico -> Lead (contenido automatizado) | Q2-Q3 2026 |

---

## CONCEPTOS CRITICOS

### 1. Patron Service -> DTO -> Resource
Presente en TODOS los servicios. Si entiendes `LeadServiceImpl` -> `LeadDto` -> `LeadResource`, entiendes el 80% del backend.

### 2. Spring Security con JWT
Cada request pasa por `JwtAuthenticationFilter`. Los roles (CLIENT, LAWYER, ADMIN) determinan acceso a endpoints.

### 3. NATS como pegamento
No hay llamadas HTTP directas entre microservicios internos, usan eventos asincronos. Excepcion: n8n-integration-service llama a client-service via HTTP para callbacks.

### 4. Flyway para migraciones
Nunca modifiques tablas manualmente. Crea `V{N}__descripcion.sql` en `src/main/resources/db/migration/`.

### 5. Perfiles de Spring
`dev`, `local`, `kubernetes`, `prod` cambian completamente el comportamiento (BD, URLs, timeouts). Siempre verifica cual perfil esta activo.

### 6. Context Paths
Cada servicio tiene su propio context path (ej: `/client-service`, `/case-service`). Las URLs completas son: `http://localhost:8080/client-service/api/leads`.

---

## VALIDACION PRACTICA

Para verificar tu entendimiento, ejecuta estos pasos:

```bash
# 1. Build completo sin tests
./mvnw clean package -DskipTests -T 1C

# 2. Levantar todo el stack
docker-compose up -d

# 3. Verificar servicios UP (puertos 8080, 8200, 8300, 8700, 8800)

# 4. Login
curl -X POST http://localhost:8080/client-service/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin.test@gmail.com","password":"Cliente123!"}'

# 5. Capturar un lead
curl -X POST http://localhost:8080/client-service/api/leads \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Test","email":"test@empresa.com","servicio":"marca","mensaje":"Necesito registrar mi marca"}'

# 6. Ver leads (con token del paso 4)
curl http://localhost:8080/client-service/api/leads \
  -H "Authorization: Bearer <token>"
```

### Usuarios de prueba
| Rol | Email | Password |
|-----|-------|----------|
| Cliente | alexisj4a@gmail.com | Cliente123! |
| Abogado | abogado.test@gmail.com | Cliente123! |
| Admin | admin.test@gmail.com | Cliente123! |

---

## DOCUMENTOS DE REFERENCIA RAPIDA

| Documento | Para que |
|-----------|----------|
| `CLAUDE.md` | Contexto rapido de todo el proyecto |
| `PROYECTO_ESTADO.md` | Estado actual, hitos completados |
| `docs/development/SESSION_CONTEXT.md` | Contexto entre sesiones de desarrollo |
| `docs/operations/OBSERVABILITY_GUIDE.md` | Guia del stack de observabilidad |
| `.github/copilot-instructions.md` | Convenciones de desarrollo |
| `docs/security/SECRETS_MANAGEMENT.md` | Manejo de secretos |
