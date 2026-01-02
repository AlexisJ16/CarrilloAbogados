# 🤖 AI Context Master - Carrillo Abogados Legal Tech Platform

**Última Actualización**: 2 de Enero, 2026  
**Propósito**: Documento maestro de contexto para todas las IAs que trabajan en el proyecto

---

## 📍 DOCUMENTOS DE REFERENCIA PRINCIPALES

Para obtener contexto completo del proyecto, revisar estos archivos en orden de prioridad:

| Archivo | Propósito | Actualización |
|---------|-----------|---------------|
| `PROYECTO_ESTADO.md` | Estado actual detallado | Cada sesión |
| `.github/copilot-instructions.md` | Instrucciones para Copilot | Frecuente |
| `CLAUDE.md` | Contexto técnico para Claude AI | Cambios mayores |
| `.github/copilot-agents/` | 7 agentes especializados | Por dominio |

---

## ⚠️ CRÍTICO: Entorno Windows + WSL

### Configuración del Entorno
- **Host OS**: Windows 11
- **WSL Distribution**: Ubuntu-24.04 (default)
- **Minikube**: Runs inside WSL with Docker driver
- **kubectl/helm**: Installed in WSL, NOT in Windows native

### Cómo Ejecutar Comandos

**TODOS los comandos de Kubernetes/Minikube/Helm DEBEN ejecutarse a través de WSL:**

```powershell
# ✅ CORRECTO - Usar wsl bash -c "comando"
wsl bash -c "kubectl get pods -n carrillo-dev"
wsl bash -c "minikube status"
wsl bash -c "helm list -n carrillo-dev"

# ❌ INCORRECTO - NO ejecutar kubectl directamente
kubectl get pods  # Esto falla - Windows kubectl no tiene config de Minikube
```

### Reiniciar WSL (Soluciona Problemas de Estabilidad)
```powershell
wsl --shutdown
# Esperar 10 segundos, luego:
wsl bash -c "minikube start"
```

---

## 📋 RESUMEN DEL PROYECTO

### Información General
- **Nombre**: Carrillo Abogados Legal Tech Platform
- **Tipo**: Plataforma cloud-native de gestión legal empresarial
- **Arquitectura**: 8 microservicios Spring Boot sobre Kubernetes
- **Propósito Dual**:
  1. **Académico**: Proyecto final curso Plataformas II
  2. **Empresarial**: Sistema real para bufete Carrillo Abogados, Cali, Colombia

### Cliente
- 7 abogados + 2 administrativos
- Dominio: @carrilloabgd.com (Google Workspace)
- MVP objetivo: 27 marzo 2026
- Presupuesto inicial: $0 (escalar según demanda)

---

## 🛠️ STACK TECNOLÓGICO ACTUAL

### Versiones Estables (Enero 2026)
| Tecnología | Versión | Notas |
|------------|---------|-------|
| Java | 21 LTS | Requerido |
| Spring Boot | 3.3.13 | LTS estable |
| Spring Cloud | 2023.0.6 | Compatible con Boot 3.3.x |
| springdoc-openapi | 2.6.0 | Compatible con Boot 3.3.x |
| Kubernetes | 1.34.0 | |
| PostgreSQL | 16.2 | Bitnami Helm chart |
| NATS | 2.10.22 | Dev/Staging messaging |

### Microservicios Activos (8)
| Servicio | Puerto | Context-Path | Estado |
|----------|--------|--------------|--------|
| api-gateway | 8080 | / | ✅ Activo |
| client-service | 8200 | /client-service | ✅ Activo |
| case-service | 8300 | /case-service | ✅ Activo |
| payment-service | 8400 | / | ✅ Activo |
| document-service | 8500 | / | ⚙️ Skeleton |
| calendar-service | 8600 | / | ⚙️ Skeleton |
| notification-service | 8700 | / | ⚙️ Skeleton |
| n8n-integration-service | 8800 | / | ⚙️ Skeleton |

### Servicios Eliminados
- ~~user-service~~ - Migrado a client-service (disabled in Helm)
- ~~order-service~~ - Nunca existió, era template e-commerce

---

## 📁 ESTRUCTURA DEL REPOSITORIO

```
CarrilloAbogados/
├── api-gateway/                    # Spring Cloud Gateway
├── client-service/                 # Gestión clientes
├── case-service/                   # Casos legales
├── payment-service/                # Pagos
├── document-service/               # Documentos
├── calendar-service/               # Google Calendar
├── notification-service/           # Notificaciones
├── n8n-integration-service/        # Workflows
├── user-service/                   # Legacy
├── helm-charts/carrillo-abogados/  # Umbrella Helm chart
├── infrastructure/
│   ├── k8s-manifests/              # Kubernetes manifests
│   └── terraform/                  # IaC (futuro)
├── docs/
│   ├── ai-context/                 # Instrucciones para IAs
│   ├── architecture/               # Decisiones arquitectónicas
│   ├── api/                        # Documentación API
│   ├── development/                # Guías desarrollo
│   ├── operations/                 # Operaciones/deployment
│   ├── security/                   # Políticas seguridad
│   └── tracking/                   # Trazabilidad proyecto
├── scripts/                        # Scripts de utilidad
├── CLAUDE.md                       # Contexto principal Claude
├── PROYECTO_ESTADO.md              # Estado actual
└── pom.xml                         # Maven parent POM
```

---

## 🔧 DECISIONES ARQUITECTÓNICAS CLAVE

### Cloud-Native (Sin Vendor Lock-in)
- ❌ Eureka Server → ✅ Kubernetes Service Discovery
- ❌ Spring Cloud Config → ✅ Kubernetes ConfigMaps/Secrets
- ❌ Ribbon → ✅ Spring Cloud LoadBalancer

### Base de Datos
- PostgreSQL compartida con schemas separados por servicio
- Schemas: `clients`, `cases`, `documents`, `payments`, `calendar`, `notifications`
- Flyway para migraciones

### Messaging
- Dev/Staging: NATS
- Producción: Google Pub/Sub (planeado)
- Arquitectura event-driven para integración N8N

### Autenticación
- OAuth2 via Google Workspace (@carrilloabgd.com)
- Trazabilidad legal: cada acción registrada con email de usuario

---

## 🎯 DIRECTRICES PARA IAs

### Al Modificar Código
1. **Verificar versiones**: Usar siempre Spring Boot 3.3.13, Cloud 2023.0.6
2. **No agregar Eureka/Config Server**: Usar Kubernetes nativo
3. **Respetar naming**: `SERVICE-NAME` en mayúsculas para `spring.application.name`
4. **Multi-módulo Maven**: Todos los servicios heredan del parent POM

### Al Crear Documentación
1. **Ubicar correctamente**: Usar la estructura de docs/
2. **Actualizar índice**: Mantener docs/README.md actualizado
3. **Formato**: Markdown con emojis para claridad visual

### Al Modificar Configuración Kubernetes
1. **Namespace**: `carrillo-dev` para desarrollo
2. **Helm charts**: Umbrella chart en `helm-charts/carrillo-abogados/`
3. **Secretos**: Templates sin valores reales en Git

---

## 📄 DOCUMENTOS DE CONTEXTO POR IA

| IA | Documento Principal | Ubicación |
|----|---------------------|-----------|
| Claude Code | CLAUDE.md | Raíz del proyecto |
| GitHub Copilot | copilot-instructions.md | .github/ |
| Claude Desktop | AI_CONTEXT_MASTER.md | docs/ai-context/ |

---

## 🔗 REFERENCIAS RÁPIDAS

### Comandos Esenciales
```bash
# Build completo
./mvnw clean verify -T 1C

# Minikube
minikube start --kubernetes-version=v1.34.0
eval $(minikube docker-env)

# Helm
helm upgrade --install carrillo-dev helm-charts/carrillo-abogados/ -n carrillo-dev

# Logs
kubectl logs -f deployment/api-gateway -n carrillo-dev
```

### Documentación Clave
- **NUEVO** Modelo de Negocio: [docs/business/MODELO_NEGOCIO.md](../business/MODELO_NEGOCIO.md)
- **NUEVO** Requerimientos: [docs/business/REQUERIMIENTOS.md](../business/REQUERIMIENTOS.md)
- **NUEVO** Roles: [docs/business/ROLES_USUARIOS.md](../business/ROLES_USUARIOS.md)
- **NUEVO** Casos de Uso: [docs/business/CASOS_USO.md](../business/CASOS_USO.md)
- **NUEVO** Arquitectura Funcional: [docs/business/ARQUITECTURA_FUNCIONAL.md](../business/ARQUITECTURA_FUNCIONAL.md)
- Arquitectura Técnica: [docs/architecture/ARCHITECTURE.md](../architecture/ARCHITECTURE.md)
- Base de datos: [docs/architecture/ADR-005-database-strategy.md](../architecture/ADR-005-database-strategy.md)
- Operaciones: [docs/operations/OPERATIONS.md](../operations/OPERATIONS.md)
- Versiones: [docs/development/VERSION_STABILITY.md](../development/VERSION_STABILITY.md)

---

*Generado: 19 de Diciembre, 2025*  
*Mantener actualizado con cada cambio significativo del proyecto*
