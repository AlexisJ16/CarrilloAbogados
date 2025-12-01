# CLAUDE.md - Carrillo Abogados Legal Tech Platform

## CONTEXTO DEL PROYECTO

### Propósito Dual
1. **Académico**: Proyecto final curso Plataformas II (entrega 1 diciembre 2025)
2. **Empresarial**: Plataforma real para bufete Carrillo Abogados, Cali, Colombia

### Cliente
- 5 abogados + 2 administrativos
- Sin presencia digital actual
- MVP: 18 marzo 2026
- Presupuesto inicial: $0 (escalar según demanda)

## DECISIONES ARQUITECTÓNICAS CRÍTICAS

### Eliminadas (Vendor Lock-in)
- Eureka Server → Kubernetes Service Discovery
- Spring Cloud Config Server → Kubernetes ConfigMaps/Secrets

### Adoptadas (Cloud-Native)
- Spring Cloud Kubernetes Discovery
- Kubernetes ConfigMaps nativos
- NATS (dev/staging) → Google Pub/Sub (prod)
- PostgreSQL compartida con schemas separados

### API Gateway
- Spring Cloud Gateway (MVP)
- Migrar a Kong si escala

### Autenticación
- OAuth2 (cada abogado usa su cuenta @carrilloabgd.com)
- Trazabilidad legal crítica

## MICROSERVICIOS FINALES (11)

### Infraestructura (2)
1. api-gateway - Spring Cloud Gateway + OAuth2
2. proxy-client - Autenticación y proxy

### Negocio Core (7)
3. client-service - Gestión de clientes (adaptado de user-service)
4. case-service - Gestión de casos (adaptado de order-service)
5. payment-service - Pagos a entidades gubernamentales
6. document-service - Almacenamiento seguro documentos legales
7. calendar-service - Google Calendar API integration
8. notification-service - Email/SMS via Gmail API
9. n8n-integration-service - Bridge con N8N Pro para workflows

### Legacy (2 - mantener temporalmente)
10. user-service - Migrar a client-service
11. order-service - Migrar a case-service

## STACK TECNOLÓGICO

### Core
- Java 21 LTS
- Spring Boot 3.3.13
- Spring Cloud 2023.0.6
- Spring Cloud Kubernetes 3.1.3

### Kubernetes
- Version: 1.34.0
- Local: Minikube
- Prod: GKE Autopilot

### Databases
- PostgreSQL 16.2 (Bitnami chart 15.5.38)
- Schemas: clients, cases, documents, payments, calendar, notifications

### Messaging
- Dev/Staging: NATS 2.10.22
- Prod: Google Pub/Sub

### Observability
- Prometheus + Grafana
- Loki (logging)
- Micrometer (tracing)

## ESTRUCTURA DEL REPOSITORIO

```
CarrilloAbogados/
├── microservices/
│   ├── api-gateway/
│   ├── proxy-client/
│   ├── client-service/
│   ├── case-service/
│   ├── payment-service/
│   ├── document-service/
│   ├── calendar-service/
│   ├── notification-service/
│   └── n8n-integration-service/
├── helm-charts/
│   ├── api-gateway/
│   └── carrillo-abogados/ (umbrella)
├── infrastructure/
│   ├── terraform/
│   ├── k8s-manifests/
│   │   ├── namespaces/
│   │   ├── configmaps/
│   │   ├── secrets/
│   │   ├── rbac/
│   │   └── network-policies/
│   └── monitoring/
├── docs/
│   ├── architecture/
│   ├── api/
│   └── operations/
└── scripts/
```

## ESTRATEGIA GIT BRANCHING (GitFlow)

```
main (producción, protegida)
↑
staging (pre-prod)
↑
dev (desarrollo activo)
```

## CONFIGURACIÓN GOOGLE WORKSPACE

- Dominio: @carrilloabgd.com
- Plan: Business Standard ($12/usuario/mes)
- 7 usuarios (5 abogados + 2 admin)
- Host: HostGator con cPanel
- Admin: ingenieria@carrilloabgd.com

## INTEGRACIÓN N8N PRO

- Arquitectura basada en eventos
- Microservicios emiten eventos → NATS → N8N escucha → Workflows
- Ventaja: Abogados modifican workflows sin desarrollador

## COSTOS

### Dev (Minikube): $0
### Staging (GCP $300 créditos): $0 por 12+ meses
- GKE: e2-micro (Always Free)
- Cloud SQL: db-f1-micro (~$7/mes, cubierto)

### Prod inicial: ~$90-130/mes
- GKE Autopilot: ~$40-70/mes
- Cloud SQL: ~$35/mes
- Storage + CDN: ~$15-25/mes

## REQUISITOS ACADÉMICOS

1. Arquitectura (15%)
2. Red y Seguridad (15%)
3. Configuración (10%)
4. CI/CD (15%)
5. Persistencia (10%)
6. Observabilidad (15%)
7. Autoscaling (10%)
8. Documentación (10%)
9. Bonificaciones (+30%)

## COMANDOS IMPORTANTES

### Build
```bash
./mvnw clean verify -T 1C
```

### Minikube
```bash
minikube start --kubernetes-version=v1.34.0
kubectl get pods -A
```

### Helm
```bash
helm install carrillo-dev helm-charts/carrillo-abogados/ -n carrillo-dev
```

### PostgreSQL
```bash
kubectl exec -it postgresql-0 -n databases -- psql -U carrillo -d carrillo_legal_tech
```

### NATS
```bash
kubectl exec -n messaging deployment/nats-box -- nats pub test "Hello"
```

## PRÓXIMOS PASOS

1. Implementar lógica de negocio en servicios nuevos
2. Configurar Google APIs (Calendar, Gmail, Drive)
3. Integrar N8N Pro workflows
4. Deploy a staging en GKE
5. Configurar Prometheus/Grafana dashboards
6. Tests de integración
7. Documentación API (Swagger)
8. Preparar presentación académica

## CONTACTO

- Proyecto: Carrillo Abogados Legal Tech
- Desarrollador: Alexis
- Cliente: Carrillo Abogados, Cali, Colombia
- Entrega académica: 1 diciembre 2025
- MVP empresarial: 18 marzo 2026