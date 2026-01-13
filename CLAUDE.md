# CLAUDE.md - Carrillo Abogados Legal Tech Platform

**Última Actualización**: 12 de Enero, 2026  
**Fase Actual**: FASE 10 - Autenticación Frontend Completa  
**Ramas**: `dev` (sincronizado con `main`)  
**Último Test**: 105 tests ✅ (8 Ene 2026)

---

## 📋 RESUMEN DEL PROYECTO

Plataforma cloud-native de gestión legal con **8 microservicios Spring Boot** para el bufete **Carrillo ABGD SAS** de Cali, Colombia.

### Propósito
**100% Empresarial** - Sistema de producción para el bufete Carrillo Abogados (7 abogados + 2 administrativos).

### Fechas Clave
- **MVP Empresarial**: 27 Marzo 2026
- **Estado Actual**: CI/CD Pipeline + Autenticación Frontend funcionando

---

## 🔐 AUTENTICACIÓN (CORREGIDO 11 Ene 2026)

### CORS Configuración
El API Gateway ahora permite requests desde el frontend:
```yaml
# api-gateway/src/main/resources/application.yml
allowed-origins:
  - "${CLIENT_HOST:http://localhost:3000}"
  - "http://localhost:4200"
  - "http://localhost:3000"
```

### Usuarios de Prueba
| Rol | Email | Password |
|-----|-------|----------|
| Cliente | cliente.prueba@example.com | Cliente123! |
| Abogado | abogado.prueba@carrilloabgd.com | Cliente123! |
| Admin | admin.prueba@carrilloabgd.com | Cliente123! |

### Header con Login
El componente `Header.tsx` incluye:
- Botón "Iniciar Sesión" para visitantes
- Menú dropdown con nombre de usuario para autenticados
- Link a dashboard y opción de logout

---

## ⚠️ CRÍTICO: Entorno Windows + WSL

```powershell
# ✅ CORRECTO - Usar wsl bash -c para kubectl/minikube/helm
wsl bash -c "kubectl get pods -n carrillo-dev"
wsl bash -c "minikube status"
wsl bash -c "helm list -n carrillo-dev"

# ❌ INCORRECTO - kubectl directo en PowerShell no funciona
kubectl get pods  # FALLA - Windows kubectl no tiene config de Minikube
```

### Reiniciar WSL (Soluciona Problemas)
```powershell
wsl --shutdown
# Esperar 10 segundos
wsl bash -c "minikube start"
```

---

## 🏗️ ARQUITECTURA DE MICROSERVICIOS

| Servicio | Puerto | Propósito | Estado |
|----------|--------|-----------|--------|
| api-gateway | 8080 | Spring Cloud Gateway + OAuth2 | ✅ 100% |
| client-service | 8200 | Gestión clientes + Lead API | ✅ 100% |
| case-service | 8300 | Gestión casos legales | ✅ 95% |
| payment-service | 8400 | Pagos gubernamentales | 🔄 15% |
| document-service | 8500 | Almacenamiento documentos | 🔄 15% |
| calendar-service | 8600 | Google Calendar sync | 🔄 15% |
| notification-service | 8700 | Email/SMS notifications | ✅ 80% |
| n8n-integration-service | 8800 | Bridge con n8n Cloud | ✅ 95% |

### Servicios Deprecados
- ~~user-service~~ → Migrado a client-service
- ~~order-service~~ → Nunca existió (template e-commerce)

---

## 🔧 STACK TECNOLÓGICO

| Categoría | Tecnología | Versión |
|-----------|------------|---------|
| **Backend** | Java | 21 LTS |
| | Spring Boot | 3.3.13 |
| | Spring Cloud | 2023.0.6 |
| | Spring Cloud Kubernetes | 3.1.3 |
| **Base de Datos** | PostgreSQL | 16.2 |
| **Mensajería** | NATS (dev/staging) | 2.10.22 |
| | Google Pub/Sub (prod) | - |
| **Container** | Docker | 29.x |
| **Orquestación** | Kubernetes | 1.34.0 |
| **Local** | Minikube | Latest |

---

## 🚀 CI/CD PIPELINE (100% Funcional)

### GitHub Actions Workflows

| Workflow | Trigger | Estado |
|----------|---------|--------|
| `ci-cd-pipeline.yml` | push dev/staging/main | ✅ 100% |
| `security-scan.yml` | push main, schedule | ⚠️ Config SonarCloud |
| `deploy-gcp.yml` | push main | ⚠️ Requiere secrets GCP |

### Jobs del CI/CD Pipeline
1. **🔨 Build & Test** (~2 min) - Maven build + 105 tests
2. **🔐 Security Scan** (~1.5 min) - Trivy vulnerabilities
3. **📊 Pipeline Summary** - Resumen de resultados
4. **🐳 Docker Build** (8 servicios) - Push a ghcr.io

### Imágenes Docker Publicadas
```
ghcr.io/alexisj16/api-gateway:dev
ghcr.io/alexisj16/client-service:dev
ghcr.io/alexisj16/case-service:dev
ghcr.io/alexisj16/payment-service:dev
ghcr.io/alexisj16/document-service:dev
ghcr.io/alexisj16/calendar-service:dev
ghcr.io/alexisj16/notification-service:dev
ghcr.io/alexisj16/n8n-integration-service:dev
```

---

## 🗄️ BASE DE DATOS

### PostgreSQL con Schemas Separados
- **Estrategia**: Single PostgreSQL, multiple schemas (ADR-005)
- **Conexión**: `postgresql:5432/carrillo_legal_tech`

| Schema | Servicio |
|--------|----------|
| clients | client-service |
| cases | case-service |
| documents | document-service |
| payments | payment-service |
| calendar | calendar-service |
| notifications | notification-service |

### Conexión en Docker Compose
```bash
docker exec -it postgresql psql -U carrillo -d carrillo_legal_tech
\dn  # Ver schemas
SET search_path TO clients;  # Cambiar schema
\dt  # Ver tablas
```

---

## 📦 DESARROLLO LOCAL

### Comandos Maven
```bash
# Build paralelo sin tests
./mvnw clean package -DskipTests -T 1C

# Build con tests (105 tests)
./mvnw clean verify -T 1C

# Build un servicio específico
./mvnw -pl client-service clean package -DskipTests

# Ejecutar tests de seguridad
./mvnw test -pl client-service "-Dtest=*SecurityTest"
```

### Docker Compose (Desarrollo Local)
```bash
docker-compose up -d        # Levantar todos los servicios
docker-compose ps           # Ver estado
docker-compose logs -f      # Ver logs
docker-compose down         # Detener todo
```

### Verificar Health de Servicios
```powershell
# Windows PowerShell
$ports = @('8080','8200','8300','8400','8500','8600','8700','8800')
foreach ($p in $ports) {
    try { 
        $r = Invoke-WebRequest "http://localhost:$p/actuator/health" -TimeoutSec 2 -UseBasicParsing
        Write-Host "$p : UP" -ForegroundColor Green 
    } catch { 
        Write-Host "$p : DOWN" -ForegroundColor Red 
    }
}
```

---

## 🔐 SEGURIDAD

### Herramientas Configuradas
| Herramienta | Estado | Dashboard |
|-------------|--------|-----------|
| **Snyk** | ✅ Activo | https://app.snyk.io/org/alexisj16 |
| **SonarCloud** | ✅ Activo | https://sonarcloud.io/project/overview?id=AlexisJ16_CarrilloAbogados |
| **Trivy** | ✅ CI/CD | Integrado en pipeline |

### Tests de Seguridad (66 tests)
- `InputValidationSecurityTest` - SQL injection, XSS, Path traversal
- `BeanValidationTest` - Validación de campos DTO

---

## 📊 OBSERVABILIDAD (Grafana LGTM Stack)

| Servicio | Puerto | Propósito | Estado |
|----------|--------|-----------|--------|
| Grafana | 3100 | Dashboards | ✅ Operativo |
| Loki | 3101 | Logs | ✅ Operativo |
| Tempo | 3102 | Tracing | ✅ Operativo |
| Mimir | 3103 | Métricas largo plazo | ✅ Operativo (healthcheck disabled - distroless) |
| Prometheus | 9090 | Métricas | ✅ 13/13 targets UP |
| Alertmanager | 9093 | Alertas | ✅ Operativo |

**Credenciales Grafana**: admin / carrillo2025

**Dashboard**: http://localhost:3100/d/carrillo-overview/carrillo-abogados-services-overview

```bash
cd monitoring
docker-compose -f docker-compose.observability.yml up -d
```

---

## 🤖 INTEGRACIÓN n8n (Marketing Automation)

### n8n Cloud
| Campo | Valor |
|-------|-------|
| **URL** | https://carrilloabgd.app.n8n.cloud |
| **Versión** | v1.120.4 |
| **Webhook** | `/webhook/lead-events` |

### MEGA-WORKFLOWS
| Workflow | Propósito | Estado |
|----------|-----------|--------|
| MW#1: Captura | Lead → Cliente (<1 min) | 90% |
| MW#2: Retención | Cliente → Recompra | Q2 2026 |
| MW#3: SEO | Tráfico → Lead | Q2-Q3 2026 |

### Lead Scoring
```
Base: 30 pts
+ Servicio "marca"/"litigio": +20
+ Mensaje > 50 chars: +10
+ Tiene teléfono: +10
+ Tiene empresa: +10
+ Email corporativo: +10
+ Cargo C-Level: +20
───────────────────
HOT:  ≥70 pts → Notificación inmediata
WARM: 40-69 pts → Nurturing IA
COLD: <40 pts → Respuesta genérica
```

---

## 📁 ESTRUCTURA DEL REPOSITORIO

```
CarrilloAbogados/
├── .github/
│   ├── workflows/          # CI/CD pipelines
│   ├── copilot-agents/     # 16 agentes especializados
│   └── copilot-instructions.md
├── api-gateway/            # Spring Cloud Gateway
├── client-service/         # Lead API + Clientes
├── case-service/           # Casos legales
├── payment-service/        # Pagos gubernamentales
├── document-service/       # Almacenamiento docs
├── calendar-service/       # Google Calendar
├── notification-service/   # Email/SMS
├── n8n-integration-service/# Bridge n8n
├── frontend/               # Next.js 14
├── helm-charts/            # Kubernetes Helm
├── infrastructure/         # Terraform, K8s manifests
├── monitoring/             # Grafana LGTM stack
├── docs/                   # Documentación
├── scripts/                # Shell scripts
├── compose.yml             # Docker Compose
├── CLAUDE.md               # Este archivo
└── PROYECTO_ESTADO.md      # Estado detallado
```

---

## 🔀 GIT WORKFLOW

```
main (producción) ← staging (pre-prod) ← dev (desarrollo)
```

- **main**: Protegida, solo merges desde dev/staging
- **dev**: Desarrollo activo, CI/CD completo
- **staging**: Pre-producción (futuro)

### Ramas Actuales
- `dev`: commit `482de04` (desarrollo activo)
- `main`: commit `9860476` (última sincronización)

---

## 📋 DOCUMENTOS DE REFERENCIA

| Documento | Propósito |
|-----------|-----------|
| `PROYECTO_ESTADO.md` | Estado actual, hitos, próximos pasos |
| `docs/development/SESSION_CONTEXT.md` | Contexto entre sesiones de desarrollo |
| `docs/development/TEST_USERS.md` | Usuarios de prueba |
| `docs/operations/OBSERVABILITY_GUIDE.md` | Guía stack observabilidad |
| `.github/copilot-instructions.md` | Instrucciones detalladas para desarrollo |
| `docs/business/` | Modelo de negocio, requerimientos, MVP roadmap |
| `docs/architecture/` | ADRs y arquitectura técnica |
| `docs/operations/` | Guías de operaciones y deploy |

---

## ⚡ LECCIONES APRENDIDAS

### Jackson + LocalDateTime
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

### NATS con @Nullable
```java
public NatsEventPublisher(@Nullable Connection natsConnection) {
    this.natsConnection = natsConnection;
}
```

### Health Checks con Context-Path
```dockerfile
HEALTHCHECK CMD wget -q --spider http://127.0.0.1:8200/client-service/actuator/health || exit 1
```

---

## 📞 CONTACTO

- **Desarrollador**: Alexis
- **Cliente**: Carrillo Abogados, Cali, Colombia
- **Email Admin**: ingenieria@carrilloabgd.com
- **MVP Target**: 27 Marzo 2026
