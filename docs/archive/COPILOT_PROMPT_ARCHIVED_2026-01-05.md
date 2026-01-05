# 🚀 COPILOT PROMPT - Carrillo Abogados Legal Tech Platform

**Fecha**: 2 de Enero, 2026  
**Versión**: 5.0 (FASE 3: QA Validation Completa)  
**Propósito**: Documento de transición para nuevo chat con contexto completo

---

## 📋 INSTRUCCIONES PARA EL NUEVO CHAT

Copia y pega el siguiente bloque como primer mensaje en un nuevo chat de GitHub Copilot:

---

## 🎯 PROMPT DE INICIO

```
Soy Alexis, desarrollador del proyecto Carrillo Abogados Legal Tech Platform. 
Este es un proyecto de microservicios Spring Boot para un bufete de abogados en Colombia.

## CONTEXTO DEL PROYECTO

### Estado Actual (2 Enero 2026)
- **FASE 3 COMPLETADA**: QA Validation, E2E Tests, Docker 10/10
- **Docker Compose**: 10 contenedores HEALTHY
- **E2E Tests**: 8/8 microservicios respondiendo via API Gateway
- **Lead API**: Verificada (CREATE, GET, LIST funcionando)
- **Tests de Seguridad**: 66 tests pasando
- **CI/CD**: 3 workflows (ci-cd-pipeline, pr-validation, security-scan)
- **Seguridad**: Snyk + SonarCloud + Trivy con tokens configurados
- **Observabilidad**: Grafana LGTM Stack (7 servicios)
- **Último commit**: ca46838 en rama dev

### Arquitectura
- 8 microservicios Spring Boot 3.3.13 + Java 21
- PostgreSQL 16 con schemas separados por servicio
- NATS 2.10 para mensajería asíncrona
- Kubernetes (Minikube local, GKE para producción)
- Spring Cloud Kubernetes (NO Eureka, NO Config Server)

### Microservicios y Estado
| Servicio | Puerto | Estado | Tests |
|----------|--------|--------|-------|
| api-gateway | 8080 | ✅ 100% | - |
| client-service | 8200 | ✅ 100% | 66 security |
| case-service | 8300 | ✅ 95% | básicos |
| payment-service | 8400 | 🔄 15% | - |
| document-service | 8500 | 🔄 15% | - |
| calendar-service | 8600 | 🔄 15% | - |
| notification-service | 8700 | 🔄 15% | - |
| n8n-integration-service | 8800 | 🔄 20% | - |

### Entorno de Desarrollo
- **OS**: Windows 11 con WSL2 (Ubuntu-24.04)
- **Minikube**: Corre DENTRO de WSL (usar `wsl bash -c "kubectl ..."`)
- **Docker Desktop**: v29.1.3 con integración WSL
- **IDE**: VS Code con extensiones Java/Spring configuradas

### Archivos de Contexto Clave
1. `.github/copilot-instructions.md` - Instrucciones detalladas para Copilot
2. `.github/copilot-agents/` - 7 agentes especializados (incluye QA Agent)
3. `PROYECTO_ESTADO.md` - Estado actual del proyecto
4. `CLAUDE.md` - Contexto técnico completo
5. `docs/business/` - Documentación de negocio

### Propósito Dual
1. **Académico**: Proyecto final Plataformas II
2. **Empresarial**: Sistema real para bufete Carrillo Abogados, Cali, Colombia

### Fechas Clave
- **MVP Empresarial**: 27 Marzo 2026

---

Por favor:
1. Lee `.github/copilot-instructions.md`, `CLAUDE.md`, y `PROYECTO_ESTADO.md` para contexto completo
2. Usa `wsl bash -c "..."` para ejecutar comandos de Kubernetes/Minikube
3. Mantén el patrón de desarrollo establecido (tests de seguridad, eventos NATS, etc.)

¿Qué tarea te gustaría que abordemos?
```

---

## 📊 ESTADO DETALLADO DEL PROYECTO

### Commits Recientes

```
ca46838 feat(qa): add QA agent + E2E validation complete + compose cleanup
8621788 docs: comprehensive documentation audit and agent enhancement
58ebb3d feat(devops): complete observability stack + security CI/CD
43cd864 feat(security): add comprehensive security tests for client-service lead API
c331aab ci: modernize CI/CD pipeline + VSCode workspace config
155e11e feat(client-service): Lead API completa con NATS events
```

### Validación E2E Completada ✅

**Docker Compose (10/10 Healthy):**
```
✅ carrillo-api-gateway          (8080)
✅ carrillo-client-service       (8200)
✅ carrillo-case-service         (8300)
✅ carrillo-payment-service      (8400)
✅ carrillo-document-service     (8500)
✅ carrillo-calendar-service     (8600)
✅ carrillo-notification-service (8700)
✅ carrillo-n8n-integration      (8800)
✅ carrillo-postgresql           (5432)
✅ carrillo-nats                 (4222)
```

**Health Endpoints via API Gateway (8/8):**
- `/client-service/actuator/health` → UP ✅
- `/case-service/actuator/health` → UP ✅
- `/payment-service/actuator/health` → UP ✅
- `/document-service/actuator/health` → UP ✅
- `/calendar-service/actuator/health` → UP ✅
- `/notification-service/actuator/health` → UP ✅
- `/n8n-integration-service/actuator/health` → UP ✅

**Lead API (client-service):**
- `POST /api/leads` → 201 Created ✅
- `GET /api/leads/{id}` → 200 OK ✅
- `GET /api/leads` → 200 OK (5 leads) ✅

### CI/CD & Security (GitHub Actions)

```
.github/workflows/
├── ci-cd-pipeline.yml      # Build, test, Docker, Trivy, deploy
├── pr-validation.yml       # Validación rápida de PRs
└── security-scan.yml       # Snyk + SonarCloud + Trivy
```

### Agentes Copilot Disponibles (7)

```
.github/copilot-agents/
├── INDEX.md                   # Índice de agentes
├── backend-agent.md           # Spring Boot, Java
├── frontend-agent.md          # React, Next.js
├── devops-agent.md            # Docker, K8s, CI/CD
├── testing-agent.md           # JUnit, seguridad
├── documentation-agent.md     # Docs maintenance
├── project-manager-agent.md   # Tracking
└── qa-quality-agent.md        # QA & depuración 🆕
```

### Tests de Seguridad (66 total)

**InputValidationSecurityTest (34):**
- SQL Injection: 11 tests
- XSS Prevention: 13 tests
- Path Traversal: 4 tests
- Request Validation: 4 tests
- Field Length: 2 tests

**BeanValidationTest (32):**
- Email: 14 tests
- Nombre: 3 tests
- Teléfono: 5 tests
- Servicio: 6 tests
- Mensaje: 2 tests
- Complete: 2 tests

---

## 🎯 PRÓXIMOS PASOS RECOMENDADOS

### Opción A: Completar case-service (Recomendado)
1. Crear tests de seguridad similares a client-service
2. Implementar validaciones de entrada
3. Agregar eventos NATS para casos

### Opción B: Implementar calendar-service
1. Integrar Google Calendar API
2. Booking system para citas
3. Recordatorios automáticos

### Opción C: Deploy a GKE Staging
1. Configurar cluster GKE
2. Aplicar Helm charts
3. Configurar secrets de producción

### Opción D: Implementar OAuth2 completo
1. Configurar Google Workspace OAuth
2. Integrar con api-gateway
3. Roles y permisos RBAC

### Opción E: Reducir vulnerabilidades Snyk
- 1 Critical, 80 High issues detectados
- Actualizar dependencias vulnerables
- Aplicar fixes recomendados

---

## 🔧 COMANDOS FRECUENTES

### Docker Compose
```powershell
docker-compose up -d                    # Levantar todo
docker-compose ps                       # Ver estado
docker logs carrillo-client-service     # Ver logs
```

### Maven Build
```powershell
.\mvnw clean package -DskipTests -T 1C  # Build rápido
.\mvnw test -pl client-service          # Tests de un servicio
```

### Kubernetes (via WSL)
```powershell
wsl bash -c "kubectl get pods -n carrillo-dev"
wsl bash -c "minikube status"
wsl bash -c "./scripts/deploy.sh"
```

### Git
```powershell
git status
git add .
git commit -m "feat: descripción"
git push origin dev
```

---

## 📚 DOCUMENTACIÓN CLAVE

| Documento | Descripción |
|-----------|-------------|
| [PROYECTO_ESTADO.md](PROYECTO_ESTADO.md) | Estado actual, hitos, métricas |
| [CLAUDE.md](CLAUDE.md) | Contexto técnico para Claude AI |
| [docs/business/](docs/business/) | Modelo de negocio, requerimientos |
| [docs/architecture/](docs/architecture/) | ADRs, decisiones arquitectónicas |
| [.github/copilot-agents/](/.github/copilot-agents/) | 7 agentes especializados |

---

*Documento actualizado: 2 de Enero, 2026 - 07:00 COT*
