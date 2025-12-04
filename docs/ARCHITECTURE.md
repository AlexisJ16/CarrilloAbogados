# Arquitectura Carrillo Abogados Legal Tech Platform

## Microservicios

### Core Legal (8 servicios)

1. **api-gateway** (Puerto 8080)
   - Punto de entrada único
   - Enrutamiento a microservicios
   - Sin autenticación (desarrollo)

2. **user-service** (Puerto 8100)
   - Gestión de abogados y empleados
   - Roles y permisos
   - Schema: public (usuarios compartidos)

3. **client-service** (Puerto 8200)
   - Gestión de clientes/personas
   - Información contacto
   - Schema: clients

4. **case-service** (Puerto 8300)
   - Gestión de casos legales
   - Estado de procesos
   - Schema: cases

5. **document-service** (Puerto 8500)
   - Almacenamiento documentos legales
   - Versionado
   - Schema: documents

6. **calendar-service** (Puerto 8600)
   - Audiencias y plazos
   - Recordatorios
   - Schema: calendar

7. **notification-service** (Puerto 8700)
   - Notificaciones email/SMS
   - Alertas de plazos
   - Schema: notifications

8. **n8n-integration-service** (Puerto 8800)
   - Webhooks para n8n
   - Automatizaciones CRM
   - Schema: public

## Infraestructura

- **Base de datos**: PostgreSQL (compartida, schemas separados)
- **Mensajería**: NATS (eventos asíncronos)
- **Orquestación**: Kubernetes/Minikube (dev) → GKE (prod)
- **Observabilidad**: Prometheus + Grafana

## Decisiones Arquitectónicas

- Base de datos compartida con schemas por servicio (pragmático para MVP)
- NATS para eventos asíncronos (casos, documentos, notificaciones)
- Autenticación simple (JWT futuro, sin OAuth2 por ahora)
- 1 réplica en desarrollo, auto-scaling deshabilitado