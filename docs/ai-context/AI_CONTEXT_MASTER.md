# 🤖 AI Context Master - Carrillo Abogados Legal Tech Platform

**Última Actualización**: 18 de Diciembre, 2024  
**Propósito**: Documento maestro de contexto para todas las IAs que trabajan en el proyecto

---

## 📋 RESUMEN DEL PROYECTO

### Información General
- **Nombre**: Carrillo Abogados Legal Tech Platform
- **Tipo**: Plataforma cloud-native de gestión legal empresarial
- **Arquitectura**: 10 microservicios Spring Boot sobre Kubernetes
- **Propósito Dual**:
  1. **Académico**: Proyecto final curso Plataformas II (entrega 1 diciembre 2025)
  2. **Empresarial**: Sistema real para bufete Carrillo Abogados, Cali, Colombia

### Cliente
- 5 abogados + 2 administrativos
- Dominio: @carrilloabgd.com (Google Workspace)
- MVP objetivo: 18 marzo 2026
- Presupuesto inicial: $0 (escalar según demanda)

---

## 🛠️ STACK TECNOLÓGICO ACTUAL

### Versiones Estables (Diciembre 2024)
| Tecnología | Versión | Notas |
|------------|---------|-------|
| Java | 21 LTS | Requerido |
| Spring Boot | 3.3.13 | LTS estable |
| Spring Cloud | 2023.0.6 | Compatible con Boot 3.3.x |
| springdoc-openapi | 2.6.0 | Compatible con Boot 3.3.x |
| Kubernetes | 1.34.0 | |
| PostgreSQL | 16.2 | Bitnami Helm chart |
| NATS | 2.10.22 | Dev/Staging messaging |

### Microservicios Activos (10)
1. `api-gateway` - Spring Cloud Gateway + OAuth2 (puerto 8080)
2. `client-service` - Gestión de clientes (puerto 8200)
3. `case-service` - Gestión de casos legales (puerto 8300)
4. `payment-service` - Pagos gubernamentales (puerto 8400)
5. `document-service` - Documentos legales (puerto 8500)
6. `calendar-service` - Google Calendar API (puerto 8600)
7. `notification-service` - Email/SMS vía Gmail (puerto 8700)
8. `n8n-integration-service` - Workflows N8N (puerto 8800)
9. `user-service` - Legacy, migrar a client-service (puerto 8100)
10. ~~`order-service`~~ - Eliminado, migrado a case-service

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
- Arquitectura: [docs/architecture/ARCHITECTURE.md](../architecture/ARCHITECTURE.md)
- Base de datos: [docs/architecture/ADR-005-database-strategy.md](../architecture/ADR-005-database-strategy.md)
- Operaciones: [docs/operations/OPERATIONS.md](../operations/OPERATIONS.md)
- Versiones: [docs/development/VERSION_STABILITY.md](../development/VERSION_STABILITY.md)

---

*Generado: 18 de Diciembre, 2024*  
*Mantener actualizado con cada cambio significativo del proyecto*
