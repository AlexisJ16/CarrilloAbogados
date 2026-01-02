# 🚀 COPILOT PROMPT - Carrillo Abogados Legal Tech Platform

**Fecha**: 2 de Enero, 2026  
**Versión**: 4.0 (Actualizado después de FASE 2: DevOps completa)  
**Propósito**: Documento de transición para nuevo chat con contexto completo

---

## 📋 INSTRUCCIONES PARA EL NUEVO CHAT

### Cómo usar este documento

Copia y pega el siguiente bloque como primer mensaje en un nuevo chat de GitHub Copilot:

---

## 🎯 PROMPT DE INICIO

```
Soy Alexis, desarrollador del proyecto Carrillo Abogados Legal Tech Platform. Este es un proyecto de microservicios Spring Boot para un bufete de abogados en Colombia.

## CONTEXTO DEL PROYECTO

### Estado Actual (2 Enero 2026)
- **FASE 2 COMPLETADA**: DevOps, Observabilidad, Security CI/CD
- **CI/CD**: GitHub Actions con 3 workflows (ci-cd, pr-validation, security-scan)
- **Seguridad**: Snyk + SonarCloud + Trivy integrados con tokens configurados
- **Observabilidad**: Grafana LGTM stack configurado (7 servicios)
- **Docker Compose**: 10 contenedores funcionando (8 microservicios + PostgreSQL + NATS)
- **Último commit**: 09e434e (ramas main y dev sincronizadas)

### Arquitectura
- 8 microservicios Spring Boot 3.3.13 + Java 21
- PostgreSQL 16 con schemas separados por servicio
- NATS 2.10 para mensajería asíncrona
- Kubernetes (Minikube local, GKE para producción)
- Spring Cloud Kubernetes (NO Eureka, NO Config Server)

### Microservicios y Estado
| Servicio | Puerto | Estado |
|----------|--------|--------|
| api-gateway | 8080 | ✅ 100% |
| client-service | 8200 | ✅ 100% (66 tests) |
| case-service | 8300 | ✅ 95% |
| payment-service | 8400 | 🔄 15% |
| document-service | 8500 | 🔄 15% |
| calendar-service | 8600 | 🔄 15% |
| notification-service | 8700 | 🔄 15% |
| n8n-integration-service | 8800 | 🔄 20% |

### Entorno de Desarrollo
- **OS**: Windows 11 con WSL2 (Ubuntu-24.04)
- **Minikube**: Corre DENTRO de WSL (usar `wsl bash -c "kubectl ..."`)
- **Docker**: Desktop con integración WSL
- **IDE**: VS Code con extensiones Java/Spring configuradas

### Archivos de Contexto Importantes
1. `.github/copilot-instructions.md` - Instrucciones detalladas para Copilot
2. `.github/copilot-agents/` - 7 agentes especializados
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
1. Lee los archivos `.github/copilot-instructions.md`, `CLAUDE.md`, y `PROYECTO_ESTADO.md` para contexto completo
2. Usa el comando `wsl bash -c "..."` para ejecutar comandos de Kubernetes/Minikube
3. Mantén el patrón de desarrollo establecido (tests de seguridad, eventos NATS, etc.)

¿Qué tarea te gustaría que abordemos?
```

---

## 📊 ESTADO DETALLADO DEL PROYECTO

### Commits Recientes
```
09e434e ci: add security scanning workflow with Snyk, SonarCloud, and Trivy
ed7af71 chore: add Grafana LGTM stack for comprehensive observability
e8ab5e5 docs: add deployment strategy documentation
43cd864 feat(security): add comprehensive security tests for client-service lead API
c331aab ci: modernize CI/CD pipeline + VSCode workspace config
155e11e feat(client-service): Lead API completa con NATS events y frontend structure
161d190 docs: update AI context files and continuation prompt
```

### CI/CD & Security (GitHub Actions)
```
.github/workflows/
├── ci-cd-pipeline.yml      # Build, test, Docker, Trivy scan, deploy
├── pr-validation.yml       # Validación rápida de PRs
└── security-scan.yml       # Snyk SAST/SCA + SonarCloud analysis
```

### Observabilidad (Grafana LGTM Stack)
```
monitoring/
├── grafana/          # Dashboards y datasources
├── loki/             # Log aggregation
└── prometheus/       # Métricas y alertas
```

**7 Componentes configurados:**
- Grafana, Loki, Prometheus, Tempo, Mimir, Alertmanager, Promtail

### Tests de Seguridad Implementados (66 total)

**InputValidationSecurityTest (34 tests):**
- SQL Injection: 11 tests
- XSS Prevention: 13 tests
- Path Traversal: 4 tests
- Request Validation: 4 tests
- Field Length: 2 tests

**BeanValidationTest (32 tests):**
- Email: 14 tests
- Nombre: 3 tests
- Teléfono: 5 tests
- Servicio: 6 tests
- Mensaje: 2 tests
- Complete: 2 tests

### Configuración VSCode (`.vscode/`)
- `tasks.json`: 10 tareas rápidas
- `launch.json`: 8 configuraciones de debug
- `api-tests.http`: Tests REST Client
- `extensions.json`: Extensiones recomendadas

---

## 🎯 PRÓXIMOS PASOS RECOMENDADOS

### Opción A: Completar Security Tests en case-service
Replicar el patrón de tests de seguridad de client-service:
1. Crear `application-test.yml` con H2 y schema
2. Crear `InputValidationSecurityTest.java`
3. Crear `BeanValidationTest.java`
4. Validar que todos los tests pasen

### Opción B: Implementar calendar-service
Prioridad alta para la integración con Google Calendar:
1. Configurar Google Calendar API
2. Implementar booking system
3. Agregar eventos NATS para citas

### Opción C: Deploy a GKE Staging
Preparar el ambiente de staging:
1. Configurar cluster GKE con créditos gratuitos
2. Aplicar Helm charts
3. Configurar secrets de producción
4. Probar CI/CD pipeline completo

### Opción D: Implementar OAuth2
Seguridad para el API Gateway:
1. Configurar Google Workspace OAuth
2. Integrar con api-gateway
3. Implementar RBAC

---

## ⚠️ LECCIONES APRENDIDAS (Importante)

### Errores Comunes y Soluciones

| Error | Solución |
|-------|----------|
| Schema "CLIENTS" no encontrado (H2) | `INIT=CREATE SCHEMA IF NOT EXISTS clients` en URL |
| StatusAggregator NoSuchBean | `resilience4j.circuitbreaker.enabled: false` en tests |
| UUID IllegalArgumentException | Try-catch para aceptar excepción como validación correcta |
| Email sin TLD aceptado | `@Email` de Jakarta acepta `user@domain` (RFC 5321) |

### Configuración Test Profile Esencial
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=PostgreSQL;INIT=CREATE SCHEMA IF NOT EXISTS clients
  jpa:
    properties:
      hibernate:
        default_schema: clients
resilience4j:
  circuitbreaker:
    enabled: false
```

### Comandos Críticos Windows + WSL
```powershell
# Kubernetes/Minikube (SIEMPRE a través de WSL)
wsl bash -c "kubectl get pods -n carrillo-dev"
wsl bash -c "minikube status"

# Maven
.\mvnw clean package -DskipTests -T 1C
.\mvnw test -pl client-service "-Dtest=InputValidationSecurityTest" "-Dspring.profiles.active=test"

# Docker
docker-compose up -d
docker logs carrillo-client-service
```

---

## 📁 ARCHIVOS DE CONTEXTO

El agente debe leer estos archivos para tener contexto completo:

1. **`.github/copilot-instructions.md`** - Instrucciones detalladas del proyecto
2. **`CLAUDE.md`** - Contexto técnico completo para AI
3. **`PROYECTO_ESTADO.md`** - Estado actual del desarrollo
4. **`docs/business/REQUERIMIENTOS.md`** - 76 requerimientos funcionales + 23 no funcionales
5. **`docs/business/ARQUITECTURA_FUNCIONAL.md`** - Mapeo microservicios a funciones
6. **`docs/business/ESTRATEGIA_AUTOMATIZACION.md`** - Integración con n8n

---

## 🔧 STACK TECNOLÓGICO

| Componente | Tecnología | Versión |
|------------|------------|---------|
| Backend | Spring Boot | 3.3.13 |
| Java | OpenJDK | 21 LTS |
| Spring Cloud | Kubernetes | 3.1.3 |
| Database | PostgreSQL | 16.2 |
| Messaging | NATS | 2.10.22 |
| Container | Docker | 24.x |
| Orchestration | Kubernetes | 1.34.0 |
| CI/CD | GitHub Actions | - |
| Local K8s | Minikube | 1.33.x |

---

*Documento de transición - 19 de Diciembre 2025*
