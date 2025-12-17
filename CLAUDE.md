# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

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

## COMANDOS DE DESARROLLO ESENCIALES

### Build y Compilación
```bash
# Build completo de todos los microservicios (paralelo)
./mvnw clean verify -T 1C

# Build solo compilación (sin tests)
./mvnw clean package -DskipTests -T 1C

# Build de un microservicio específico
cd client-service && ../mvnw clean package -DskipTests
```

### Desarrollo Local con Minikube

#### Setup inicial completo
```bash
# Usar script automatizado para setup completo desde cero
./scripts/deploy-complete.sh

# O setup smart diario (detecta estado y recupera lo necesario)
./scripts/dev-up.sh
```

#### Comandos Minikube básicos
```bash
# Iniciar Minikube
minikube start --kubernetes-version=v1.34.0
eval $(minikube docker-env)  # Conectar Docker

# Estado del cluster
kubectl get pods -A
kubectl get pods -n carrillo-dev

# Logs de servicios
kubectl logs -f deployment/api-gateway -n carrillo-dev
kubectl logs -f deployment/client-service -n carrillo-dev

# Port-forward para desarrollo
kubectl port-forward svc/api-gateway 8080:8080 -n carrillo-dev
```

### Base de Datos
```bash
# Conectar a PostgreSQL
kubectl exec -it postgresql-0 -n databases -- psql -U carrillo -d carrillo_legal_tech

# Ver esquemas
\dn

# Cambiar a esquema específico
SET search_path TO clients;
\dt
```

### Messaging (NATS)
```bash
# Publicar mensaje de prueba
kubectl exec -n messaging deployment/nats-box -- nats pub test "Hello"

# Verificar conexión
kubectl exec -n messaging deployment/nats -- nats server check connection
```

### Testing
```bash
# Ejecutar todos los tests
./mvnw test

# Tests de un servicio específico
./mvnw -pl client-service test

# Tests de integración (requiere base de datos activa)
./mvnw verify -Dspring.profiles.active=test
```

### Helm y Deployment
```bash
# Instalar/actualizar todas las aplicaciones
helm upgrade --install carrillo-dev helm-charts/carrillo-abogados/ -n carrillo-dev

# Ver deployment
helm list -n carrillo-dev
helm status carrillo-dev -n carrillo-dev

# Eliminar deployment
helm uninstall carrillo-dev -n carrillo-dev
```

### Docker para Desarrollo
```bash
# Ver imágenes creadas
docker images | grep carrillo

# Construir imagen específica después de build
docker build -t carrillo/client-service:latest -f client-service/Dockerfile client-service/

# Limpiar imágenes del proyecto
docker images --format "{{.Repository}}:{{.Tag}}" | grep "carrillo/" | xargs docker rmi -f
```

### Scripts de Utilidad
```bash
# Validar deployment completo
./scripts/validate-deployment.sh

# Limpiar entorno completamente
./scripts/nuke-environment.sh

# Verificar entorno y herramientas
./scripts/check-env.sh
```

## FLUJO DE TRABAJO RECOMENDADO

### Día típico de desarrollo
1. `./scripts/dev-up.sh` - Setup inteligente del entorno
2. `kubectl port-forward svc/api-gateway 8080:8080 -n carrillo-dev` - Acceso al gateway
3. Desarrollar en un microservicio específico
4. `./mvnw -pl client-service clean package -DskipTests` - Build rápido
5. Rebuild imagen Docker si es necesario
6. `helm upgrade carrillo-dev helm-charts/carrillo-abogados/ -n carrillo-dev` - Deploy

### Debugging common issues
```bash
# Ver estado de pods
kubectl get pods -n carrillo-dev -o wide

# Describe pod con problemas
kubectl describe pod <pod-name> -n carrillo-dev

# Ver logs detallados
kubectl logs <pod-name> -n carrillo-dev --previous

# Acceso directo a base de datos para debug
kubectl port-forward svc/postgresql 5432:5432 -n databases
# Luego usar cliente SQL local
```

### CI/CD Local
El proyecto tiene configuración de GitHub Actions para:
- Build automático en PRs
- Deploy a staging/production
- Tests de integración

Para simular CI localmente:
```bash
./scripts/deploy-complete.sh  # Full pipeline simulation
```

## ARQUITECTURA CRÍTICA PARA DESARROLLO

### Microservicios y sus propósitos
- **client-service**: Gestión de clientes (nueva versión de user-service)
- **case-service**: Gestión de casos legales (nueva versión de order-service)
- **api-gateway**: Spring Cloud Gateway + OAuth2 (@carrilloabgd.com)
- **document-service**: Almacenamiento seguro documentos legales
- **calendar-service**: Google Calendar API integration
- **notification-service**: Email/SMS via Gmail API
- **n8n-integration-service**: Bridge con N8N Pro para workflows

### Base de datos compartida
PostgreSQL con schemas separados:
- Schema `clients` para client-service
- Schema `cases` para case-service
- Schema `documents` para document-service
- Schema `notifications` para notification-service

### Messaging
- Dev/Staging: NATS 2.10.22
- Prod: Google Pub/Sub
- Arquitectura basada en eventos para integración N8N

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