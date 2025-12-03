# Carrillo Abogados Legal Tech Platform
## Reporte Técnico - Informe de Arquitectura y DevOps

---

## Documento de Referencia

**Título del Proyecto**: Plataforma Cloud-Native de Gestión Legal Empresarial  
**Organización**: Carrillo Abogados, Cali, Colombia  
**Versión del Reporte**: 1.0.0
**Estado del Proyecto**: Fase 3 Completada - Desarrollo Activo

**Versiones Técnicas**:
- **Java Runtime**: 21 LTS
- **Spring Boot**: 3.3.13
- **Spring Cloud**: 2023.0.6
- **Kubernetes**: 1.34.0
- **Helm**: 3.19.2
- **PostgreSQL**: 16
- **NATS**: 2.10

---

## 1. Visión y Objetivos del Proyecto

### 1.1 Visión Estratégica

Carrillo Abogados Legal Tech es una plataforma empresarial diseñada para digitalizar y optimizar los procesos de gestión legal en despachos de abogados. La plataforma implementa una arquitectura cloud-native basada en microservicios, permitiendo escalabilidad, resiliencia y mantenibilidad a través de orquestación en Kubernetes.

### 1.2 Objetivos Arquitectónicos

- **Escalabilidad Horizontal**: Permitir crecimiento de infraestructura sin rediseño
- **Resiliencia**: Tolerancia a fallos mediante aislamiento de servicios
- **Agilidad Operacional**: Despliegues independientes y rápidos
- **Observabilidad**: Visibilidad completa del sistema en tiempo real
- **Seguridad por Diseño**: Implementación de principios Zero Trust
- **Automatización**: Reducción de intervención manual en operaciones

### 1.3 Beneficios Esperados

- Reducción de downtime mediante arquitectura distribuida
- Mejor rendimiento operacional con autoescalado
- Facilidad de mantenimiento y evolución del código
- Conformidad con estándares de seguridad y gobernanza

---

## 2. Arquitectura Cloud-Native

### 2.1 Principios Arquitectónicos

La plataforma se fundamenta en los siguientes principios:

1. **Microservicios Independientes**: Cada componente es autónomo y puede escalarse individualmente
2. **Contenedorización**: Todos los servicios ejecutan en contenedores Docker
3. **Orquestación Kubernetes**: Gestión automática del ciclo de vida de contenedores
4. **Configuración Externalizada**: Separación de configuración del código mediante ConfigMaps y Secrets
5. **Comunicación Asincrónica**: NATS como bus de eventos para desacoplamiento
6. **API-First**: Interfacies REST estandarizadas entre servicios

### 2.2 Modelo de Capas

```
┌─────────────────────────────────────────────────────┐
│                   Capa Presentación                  │
│                  (Cliente Web/Mobile)                 │
└──────────────┬──────────────────────────────────────┘
               │
┌──────────────▼──────────────────────────────────────┐
│              API Gateway (Spring Cloud Gateway)      │
│         - Enrutamiento de peticiones                 │
│         - Autenticación OAuth2                       │
│         - Rate Limiting y Circuit Breaking           │
└──────────────┬──────────────────────────────────────┘
               │
┌──────────────▼──────────────────────────────────────┐
│          Capa de Microservicios (Kubernetes)         │
│   ┌─────────────┐ ┌─────────────┐ ┌──────────────┐ │
│   │   Client    │ │    Case     │ │   Payment    │ │
│   │  Service    │ │   Service   │ │   Service    │ │
│   └─────────────┘ └─────────────┘ └──────────────┘ │
│   ┌─────────────┐ ┌─────────────┐ ┌──────────────┐ │
│   │  Document   │ │  Calendar   │ │ Notification │ │
│   │  Service    │ │   Service   │ │   Service    │ │
│   └─────────────┘ └─────────────┘ └──────────────┘ │
│   ┌──────────────────────────────────────────────┐ │
│   │      N8N Integration Service (Workflows)    │ │
│   └──────────────────────────────────────────────┘ │
└──────────────┬──────────────────────────────────────┘
               │
       ┌───────┴───────┬──────────────┐
       │               │              │
┌──────▼─────┐ ┌──────▼──────┐ ┌────▼─────────┐
│ PostgreSQL  │ │   NATS      │ │  Kubernetes  │
│ (Database)  │ │ (Messaging) │ │ (Secrets)    │
└─────────────┘ └─────────────┘ └──────────────┘
```

### 2.3 Patrón de Comunicación

**Comunicación Sincrónica**:
- HTTP/REST entre servicios a través del API Gateway
- Service Discovery: Kubernetes DNS nativo (`<servicio>.<namespace>.svc.cluster.local`)
- Resilencia: Circuit Breaker con Resilience4J

**Comunicación Asincrónica**:
- Event Streaming mediante NATS
- Tópicos: `carrillo.events.*`
- Garantía de entrega: At-least-once semantics
- Topología: Fan-out con múltiples suscriptores

---

## 3. Arquitectura de Microservicios

### 3.1 Servicios de Infraestructura

#### 3.1.1 API Gateway
- **Propósito**: Punto de entrada único para todas las peticiones cliente
- **Tecnología**: Spring Cloud Gateway 4.1.5
- **Funcionalidades**:
  - Enrutamiento inteligente basado en rutas HTTP
  - Autenticación OAuth2 con Google Workspace
  - Rate Limiting por cliente
  - Circuit Breaker para tolerancia a fallos
  - Transformación de headers y body
- **Puerto**: 8080
- **Réplicas**: 2 (HA)

#### 3.1.2 Proxy Client
- **Propósito**: Proxy inverso y autenticación centralizada
- **Funcionalidades**:
  - Proxy de peticiones autenticadas
  - Gestión de sesiones
  - Validación de permisos preliminar
- **Puerto**: 8900
- **Réplicas**: 2 (HA)

### 3.2 Servicios de Negocio - Dominio Legal

#### 3.2.1 Client Service
**Responsabilidad**: Gestión integral del ciclo de vida de clientes  
**Port**: 8700  
**Entidades Principales**:
- Cliente (razón social, tipo de cliente, datos de contacto)
- Contactos asociados
- Documentos de identificación
- Historial de interacciones

#### 3.2.2 Case Service
**Responsabilidad**: Orquestación de casos legales y su ciclo de vida  
**Port**: 8300  

#### 3.2.3 Payment Service
**Responsabilidad**: Procesamiento y seguimiento de pagos  
**Port**: 8400  

#### 3.2.4 Document Service
**Responsabilidad**: Gestión y versionado de documentos legales  
**Port**: 8500  

#### 3.2.5 Calendar Service
**Responsabilidad**: Integración con Google Calendar para eventos legales  
**Port**: 8600  

#### 3.2.6 Notification Service
**Responsabilidad**: Entrega multicanal de notificaciones  
**Port**: 8700  

#### 3.2.7 N8N Integration Service
**Responsabilidad**: Orquestación de workflows y automatizaciones  
**Port**: 8800  

---

## 4. Modelo de Datos

**Paradigma**: PostgreSQL 16 compartida con schemas separados por dominio

**Schemas**:
- clients: Datos de clientes
- cases: Gestión de casos legales
- documents: Metadatos de documentos
- payments: Transacciones de pago
- calendar: Eventos y fechas clave
- notifications: Historial de notificaciones
- audit: Log de cambios auditados

---

## 5. Stack Tecnológico

- **Backend**: Spring Boot 3.3.13, Spring Cloud 2023.0.6
- **Database**: PostgreSQL 16
- **Messaging**: NATS 2.10
- **Container**: Docker + Docker Compose
- **Orchestration**: Kubernetes 1.34.0 + Helm 3.19.2
- **Observability**: Prometheus + Grafana + Loki + Micrometer
- **Security**: OAuth2, JWT, TLS 1.3, RBAC, Network Policies

---

## 6. Infraestructura en Kubernetes

### 6.1 Namespaces

Se implementan 3 namespaces aislados:
- `carrillo-dev`: Ambiente de desarrollo (4 CPU, 8Gi RAM)
- `carrillo-staging`: Ambiente staging (8 CPU, 16Gi RAM)
- `carrillo-prod`: Ambiente producción (16 CPU, 32Gi RAM)

### 6.2 Estructura de Manifiestos

```
infrastructure/k8s-manifests/
├── namespaces/
├── configmaps/
├── secrets/
├── rbac/
└── network-policies/
```

### 6.3 Helm Charts

Despliegue con Helm chart principal `carrillo-abogados` con valores diferenciados por ambiente.

---

## 7. Prácticas DevOps Implementadas

### 7.1 CI/CD Pipeline

**GitHub Actions**:
- Build automático en push a main/develop
- Tests unitarios e integración
- Scan de seguridad (Trivy + SonarQube)
- Build de imágenes Docker
- Deploy automático en staging

### 7.2 Observabilidad

**Métricas (Prometheus + Grafana)**:
- JVM metrics, HTTP requests, Kubernetes stats
- Dashboards: System Overview, App Metrics, Microservices Health

**Logging (Loki)**:
- Centralización en formato JSON
- Búsqueda eficiente con Grafana

**Tracing (Micrometer)**:
- Rastreo distribuido de requests
- Análisis de latencia

### 7.3 Seguridad

- OAuth2 con Google Workspace
- JWT para autenticación de servicios
- RBAC: ADMIN, LAWYER, CASE_MANAGER, CLIENT_VIEW_ONLY
- Network Policies con Default Deny
- TLS 1.3 en comunicación externa
- Secrets encriptados en Kubernetes

### 7.4 Automatización

Scripts de operación:
- `check-env.sh`: Validar versiones
- `dev-up.sh`: Iniciar entorno local
- `start-minikube.sh`: Setup Minikube
- `deploy-complete.sh`: Despliegue completo
- `validate-deployment.sh`: Validar estado

### 7.5 Disaster Recovery

- PostgreSQL: Backups diarios, retención 30 días
- K8s State: Snapshots con Helm values
- RTO: 5-30 min, RPO: 1 hora - variable según componente

---

## 8. Decisiones Arquitectónicas (ADR)

**ADR-001**: PostgreSQL compartida con schemas separados (vs BD por servicio)
- Simplicidad operacional para equipo pequeño
- ACID garantizado
- Acoplamiento de datos mitigado en futuro

**ADR-002**: Kubernetes + GKE (vs Serverless)
- Control total de infraestructura
- Escalabilidad predecible
- Cumplimiento normativo

**ADR-003**: Comunicación híbrida (REST + NATS)
- REST para operaciones críticas
- NATS para eventos y notificaciones

**ADR-004**: S3-compatible para documentos (MinIO dev, GCS prod)
- Escalabilidad y versionado nativo

---

## 9. Estado Actual

### 9.1 Fases Completadas

**✅ Fase 1**: Transformación e-commerce → legal tech
- Eliminados: product-service, favourite-service, shipping-service
- Creados: document, calendar, notification, n8n-integration
- Build exitoso

**✅ Fase 2**: Infraestructura Kubernetes
- 3 Namespaces con ResourceQuotas
- ConfigMaps + Secrets templates
- Network Policies + RBAC

**✅ Fase 3**: Helm Charts Base
- Chart principal con valores multi-ambiente
- Templates: Deployment, Service, Ingress, HPA

### 9.2 Estado por Componente

| Componente | Estado | Progreso | Notas |
|-----------|--------|----------|--------|
| API Gateway | ✅ Activo | 100% | Enrutamiento, OAuth2 |
| Client Service | ✅ Activo | 100% | CRUD completo |
| Case Service | ✅ Activo | 90% | Core funcional |
| Payment Service | ✅ Activo | 85% | PCI-DSS pendiente |
| Document Service | ⚙️ Dev | 50% | OCR/búsqueda pending |
| Calendar Service | ⚙️ Dev | 40% | Integración en desarrollo |
| Notification Service | ⚙️ Dev | 35% | Multi-channel setup |
| N8N Integration | ⚙️ Dev | 25% | Containerizada |
| Monitoring | ⚙️ Dev | 60% | Prometheus + Grafana OK |
| CI/CD | 🔧 Setup | 50% | GH Actions OK, deployment pending |
| Database | ✅ Ready | 100% | PostgreSQL 16 |

### 9.3 Métricas de Calidad

```
Code Coverage: 65%
Build Time: ~11 segundos (Maven paralelo)
Test Execution: ~45 segundos
Technical Debt: Moderado
```

---

## 10. Fases Futuras

**Fase 4**: Análisis y OCR de Documentos (2-3 semanas)
- Google Cloud Vision API
- Full-text search
- Clasificación automática

**Fase 5**: Automatización Avanzada N8N (4-6 semanas)
- Auto-generación de documentos
- E-sign integration
- Reportes automáticos

**Fase 6**: Seguridad Avanzada (7-9 semanas)
- PCI-DSS certification
- GDPR compliance
- Penetration testing

**Fase 7**: Escalabilidad y Performance (10-12 semanas)
- Redis caching
- Query optimization
- Canary deployments

**Fase 8**: Multi-región y HA Global
- Multi-region GCP
- Database replication
- Global load balancing

---

## 11. Guía de Inicio Rápido

### Desarrollo Local

```bash
git clone https://github.com/AlexisJ16/CarrilloAbogados.git
./scripts/check-env.sh
./scripts/install-tools.sh
./mvnw clean verify -T 1C
./scripts/dev-up.sh
curl http://localhost:8080/health
```

### Minikube

```bash
./scripts/start-minikube.sh
kubectl cluster-info
kubectl apply -f infrastructure/k8s-manifests/namespaces/
./scripts/deploy-complete.sh
kubectl get pods -n carrillo-dev
```

### CI/CD

- Push a `main` → Build + Test + Deploy staging
- Push a `develop` → Build + Test
- PR → Build + Test + Quality gates

---

## 12. Referencias

**Documentación Interna**:
- `docs/architecture/ARCHITECTURE.md`: Arquitectura detallada
- `docs/OPS_README.md`: Guía operacional
- `CLAUDE.md`: Context completo
- `PROYECTO_ESTADO.md`: Status y fases

**Recursos Externos**:
- [Spring Boot 3.3](https://spring.io/projects/spring-boot)
- [Kubernetes](https://kubernetes.io/)
- [Helm](https://helm.sh/)
- [PostgreSQL 16](https://www.postgresql.org/docs/16/)
- [NATS](https://docs.nats.io/)

---

## 13. Apéndices

### Tabla de Versiones

```
Java: 21 LTS
Spring Boot: 3.3.13
Spring Cloud: 2023.0.6
Kubernetes: 1.34.0
Helm: 3.19.2
PostgreSQL: 16
NATS: 2.10
Prometheus: 2.48+
Grafana: 10.2+
Docker: 24.0+
Maven: 3.8+
```

### Puertos de Servicios

```
8080: API Gateway
8300: Case Service
8400: Payment Service
8500: Document Service
8600: Calendar Service
8700: Client/Notification Service
8800: N8N Integration
8900: Proxy Client
5432: PostgreSQL
4222: NATS
9090: Prometheus
3000: Grafana
```

### Checklist Despliegue Producción

- [ ] Tests pasando
- [ ] Code coverage > 70%
- [ ] Security scan sin críticas
- [ ] Load testing exitoso
- [ ] Backup plan documentado
- [ ] Monitoring y alertas
- [ ] Disaster recovery verificado
- [ ] Compliance check completo

---

## 14. Conclusiones

### 14.1 Resumen Ejecutivo

Carrillo Abogados Legal Tech Platform es una arquitectura cloud-native moderna que proporciona:
1. **Escalabilidad**: Crecimiento automático según demanda
2. **Resiliencia**: Tolerancia a fallos
3. **Agilidad**: Despliegues independientes
4. **Observabilidad**: Visibilidad completa
5. **Seguridad**: Múltiples capas de protección

### 14.2 Próximos Pasos

**Corto Plazo** (2 semanas):
- Fase 4 (Document Service OCR)
- PCI-DSS compliance
- Load testing

**Mediano Plazo** (2 meses):
- Fase 5 (N8N Workflows)
- Fase 6 (Seguridad Avanzada)
- Auditoría externa

**Largo Plazo** (6 meses):
- Fase 7 (Escalabilidad)
- Fase 8 (Multi-región)
- ISO 27001, SOC 2

### 14.3 Métricas de Éxito

| Métrica | Meta | Estado |
|---------|------|--------|
| Uptime | 99.95% | En progreso |
| Latencia P99 | < 500ms | En desarrollo |
| Code Coverage | > 70% | 65% |
| Deployment Freq | Daily | Weekly |
| Lead Time Changes | < 1d | 3d |

---

*Este documento es un registro vivo que se actualiza continuamente con los cambios en la arquitectura y operaciones.*
