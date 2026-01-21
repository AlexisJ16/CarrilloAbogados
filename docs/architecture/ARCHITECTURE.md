# Arquitectura - Carrillo Abogados Legal Tech Platform

**Última Actualización**: 14 de Enero, 2026  
**Estado**: FASE 10 - Autenticación Frontend Completa

## Visión General

Plataforma cloud-native de gestión legal empresarial construida con microservicios sobre Docker/Kubernetes.

## Stack Tecnológico

- Java 21 LTS
- Spring Boot 3.3.13
- Spring Cloud Kubernetes 3.1.3
- PostgreSQL 16
- NATS 2.10
- Kubernetes 1.34.0
- Helm 3.19.2

## Arquitectura de Microservicios

### Infraestructura
- **api-gateway**: Spring Cloud Gateway, enrutamiento, CORS, circuit breaker (port 8080)

### Servicios de Negocio
- **client-service**: Gestión de clientes + Lead API + Autenticación JWT (port 8200)
- **case-service**: Gestión de casos legales, timeline, contraparte (port 8300)
- **payment-service**: Procesamiento de pagos gubernamentales (port 8400)
- **document-service**: Gestión de documentos legales (port 8500)
- **calendar-service**: Integración Google Calendar (port 8600)
- **notification-service**: Email/SMS/Push (port 8700)
- **n8n-integration-service**: Workflows y automatizaciones n8n Cloud (port 8800)

### Servicios Deprecados
- ~~user-service~~: Migrado a client-service (eliminar en próxima versión)
- ~~order-service~~: Nunca existió (era template e-commerce)

## Comunicación

### Sincrónica
- HTTP/REST entre servicios vía API Gateway
- Service Discovery: Kubernetes DNS nativo

### Asincrónica
- NATS para eventos y mensajería
- Topics: carrillo.events.*

## Base de Datos

PostgreSQL compartida con schemas separados:
- clients
- cases
- documents
- payments
- calendar
- notifications

## Seguridad

- RBAC: Roles y permisos por servicio
- Network Policies: Segmentación de tráfico
- OAuth2: Google Workspace integration
- Secrets: Kubernetes Secrets nativos

## Observabilidad

- Metrics: Prometheus + Grafana
- Logging: Loki
- Tracing: Micrometer
- Health checks: Spring Actuator

## Despliegue

### Ambientes
- **dev**: Minikube local
- **staging**: GKE (e2-micro)
- **prod**: GKE Autopilot

### CI/CD
- GitHub Actions
- Helm Charts
- Rolling updates
- Canary deployments (futuro)