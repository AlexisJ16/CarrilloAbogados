# 🚀 PLAN DE TRABAJO - Carrillo Abogados Legal Tech Platform

**Fecha Creación**: 11 de Diciembre, 2025  
**Responsables**: Claude Code (Principal) + GitHub Copilot (Secundario)  
**Versión**: 1.0  
**Estado**: 📋 PLANIFICACIÓN COMPLETADA

---

## 🎯 VISIÓN GENERAL

Este plan de trabajo establece la ruta crítica para llevar el proyecto Carrillo Abogados Legal Tech Platform desde su estado actual (código base limpio post-auditoría) hasta un MVP funcional desplegado localmente, con documentación completa y sistema de trazabilidad robusto.

### Objetivos Principales

1. ✅ **Documentación Completa**: Sistema robusto de documentación técnica y de negocio
2. 🔄 **Trazabilidad IA**: Registro sistemático de decisiones con Claude Code y Copilot
3. 🏗️ **Desarrollo Incremental**: Implementación progresiva de microservicios
4. 🚀 **MVP Deployment**: Plataforma funcional en Minikube (local)
5. 📊 **Preparación Académica**: Entregable para curso Plataformas II (1 dic 2025)

---

## 📅 CRONOGRAMA GENERAL

| Fase | Duración | Fecha Inicio | Fecha Fin | Estado |
|------|----------|--------------|-----------|--------|
| **FASE 0**: Documentación Base | 2-3 días | 11-Dic | 13-Dic | 🔄 En Progreso |
| **FASE 1**: Deployment Local | 2 días | 14-Dic | 15-Dic | 📋 Planificada |
| **FASE 2**: Implementación Core Services | 5 días | 16-Dic | 20-Dic | 📋 Planificada |
| **FASE 3**: Integraciones Externas | 3 días | 21-Dic | 23-Dic | 📋 Planificada |
| **FASE 4**: Testing y Validación | 2 días | 24-Dic | 25-Dic | 📋 Planificada |
| **FASE 5**: Documentación Final y Entrega | 2 días | 26-Dic | 27-Dic | 📋 Planificada |

**Total Estimado**: 16-17 días de trabajo activo

---

## 🔵 FASE 0: DOCUMENTACIÓN BASE (HOY - 13 DIC) 🔄

**Objetivo**: Establecer sistema robusto de documentación que guíe todo el desarrollo.

### DÍA 1 (11 Diciembre) - FUNDAMENTOS 🔴 CRÍTICO

#### Sesión Mañana (3 horas)

1. **[2 horas] Documentación de Modelo de Negocio**
   - [ ] Crear `docs/business/MODELO_NEGOCIO.md`
     - Descripción bufete Carrillo Abogados
     - Servicios legales ofrecidos
     - Estructura organizacional (5 abogados + 2 admin)
     - Objetivos MVP (18 marzo 2026)
   
   - [ ] Crear `docs/business/PROCESOS_LEGALES.md`
     - Flujo: Alta de cliente
     - Flujo: Gestión de caso legal
     - Flujo: Documentación legal
     - Flujo: Pagos gubernamentales
     - Diagramas de proceso (Mermaid)
   
   - [ ] Crear `docs/business/CASOS_USO.md`
     - Casos de uso por rol (Abogado, Admin, Cliente)
     - User stories priorizadas
     - Criterios de aceptación

2. **[1 hora] Sistema de Trazabilidad**
   - [ ] Crear `docs/development/CHANGELOG.md`
     - Formato semántico (MAJOR.MINOR.PATCH)
     - Versión 0.1.0 documentada con correcciones recientes
     - Template para futuras versiones
   
   - [ ] Crear `docs/development/DECISIONES_IA.md`
     - Template de decisiones con IAs
     - Registrar decisiones arquitectónicas pasadas
     - Sistema de identificación (DECISIONID-XXX)

#### Sesión Tarde (2 horas)

3. **[1.5 horas] Roadmap y Planificación**
   - [ ] Crear `docs/project/ROADMAP.md`
     - Roadmap técnico Q4 2025 - Q1 2026
     - Features priorizadas para MVP
     - Dependencias entre features
     - Estimaciones de esfuerzo
   
   - [ ] Crear `docs/project/MILESTONES.md`
     - Milestone 1: Deployment Local (15-Dic)
     - Milestone 2: Core Services (20-Dic)
     - Milestone 3: MVP Funcional (18-Mar-2026)
     - Milestone 4: Entrega Académica (1-Dic-2025)

4. **[0.5 horas] Índice Maestro**
   - [ ] Crear `docs/README.md` (índice de toda la documentación)
   - [ ] Actualizar README.md raíz con referencias

**Entregables Día 1**:
- ✅ Modelo de negocio completamente documentado
- ✅ Casos de uso claros y priorizados
- ✅ Sistema de trazabilidad funcionando
- ✅ Roadmap definido hasta MVP

---

### DÍA 2 (12 Diciembre) - DESARROLLO Y APIs 🟡

#### Sesión Mañana (3 horas)

1. **[2 horas] Documentación API**
   - [ ] Crear `docs/api/API_REFERENCE.md`
     - Estructura general de APIs REST
     - Convenciones (HTTP verbs, status codes)
     - Autenticación y autorización
   
   - [ ] Crear `docs/api/ENDPOINTS_BY_SERVICE.md`
     - client-service endpoints
     - case-service endpoints
     - document-service endpoints (spec)
     - calendar-service endpoints (spec)
     - notification-service endpoints (spec)
     - n8n-integration-service endpoints (spec)
   
   - [ ] Crear `docs/api/EVENTS_NATS.md`
     - Topología de eventos: `carrillo.events.*`
     - Eventos por dominio
     - Formato de mensajes
     - Productores y consumidores

2. **[1 hora] Guías de Desarrollo**
   - [ ] Crear `docs/development/CONTRIBUTING.md`
     - Git workflow (GitFlow: main, staging, dev)
     - Branching strategy
     - Pull request process
     - Code review guidelines
   
   - [ ] Crear `docs/development/CODING_STANDARDS.md`
     - Java 21 conventions
     - Spring Boot patterns
     - Package structure: `com.carrilloabogados.<service>`
     - Naming conventions

#### Sesión Tarde (2 horas)

3. **[1.5 horas] Integraciones Externas**
   - [ ] Crear `docs/api/GOOGLE_APIS.md`
     - Google Calendar API setup
     - Gmail API para notifications
     - Google Drive para documents
     - OAuth2 flow (@carrilloabgd.com)
   
   - [ ] Crear `docs/api/N8N_INTEGRATION.md`
     - Arquitectura N8N Pro
     - Webhooks configurables
     - Eventos que disparan workflows
     - Casos de uso: automatizaciones legales

4. **[0.5 horas] Testing**
   - [ ] Crear `docs/development/TESTING_GUIDE.md`
     - Estrategia de testing (unitario, integración, e2e)
     - Testcontainers para PostgreSQL y NATS
     - Cobertura mínima requerida
     - CI/CD integration

**Entregables Día 2**:
- ✅ APIs completamente especificadas
- ✅ Guías de contribución y estándares
- ✅ Integraciones externas documentadas
- ✅ Estrategia de testing definida

---

### DÍA 3 (13 Diciembre) - SEGURIDAD Y ARQUITECTURA 🟢

#### Sesión Mañana (2 horas)

1. **[1.5 horas] Seguridad y Compliance**
   - [ ] Crear `docs/security/SECURITY_POLICY.md`
     - Política de reporte de vulnerabilidades
     - Security best practices
     - Manejo de secrets (Kubernetes Secrets)
   
   - [ ] Crear `docs/security/COMPLIANCE.md`
     - GDPR/LOPD compliance
     - Requisitos legales Colombia
     - Data retention policies
     - Privacidad de datos de clientes
   
   - [ ] Crear `docs/security/AUDIT_LOG.md`
     - Trazabilidad legal requerida
     - Formato de audit logs
     - Retención y acceso

2. **[0.5 horas] ADRs Faltantes**
   - [ ] Crear `docs/architecture/ADR-006-messaging-strategy.md`
     - NATS (dev/staging) vs Google Pub/Sub (prod)
     - Rationale de arquitectura event-driven
   
   - [ ] Crear `docs/architecture/ADR-007-authentication.md`
     - OAuth2 con Google Workspace
     - Por qué @carrilloabgd.com
     - Trazabilidad legal por usuario

#### Sesión Tarde (2 horas)

3. **[1.5 horas] Diagramas de Arquitectura**
   - [ ] Crear `docs/architecture/DIAGRAMS/`
     - `architecture-overview.mmd` (Mermaid)
     - `data-flow.mmd`
     - `deployment-kubernetes.mmd`
     - `oauth2-flow.mmd`
     - `nats-topology.mmd`

4. **[0.5 horas] Operaciones**
   - [ ] Crear `docs/operations/MONITORING.md`
     - Prometheus metrics por servicio
     - Grafana dashboards diseñados
     - Alerting rules
   
   - [ ] Crear `docs/operations/DISASTER_RECOVERY.md`
     - Backup strategies
     - Recovery procedures
     - RTO/RPO definidos

**Entregables Día 3**:
- ✅ Políticas de seguridad documentadas
- ✅ Compliance legal cubierto
- ✅ ADRs arquitectónicos completos
- ✅ Diagramas visuales de arquitectura

---

## 🟢 FASE 1: DEPLOYMENT LOCAL (14-15 DIC)

**Objetivo**: Plataforma funcional corriendo en Minikube con servicios básicos.

### DÍA 4 (14 Diciembre) - PREPARACIÓN INFRAESTRUCTURA 🔴

#### Sesión Mañana (3 horas)

1. **[1 hora] Validación de Entorno**
   - [ ] Ejecutar `./scripts/check-env.sh`
   - [ ] Validar versiones (Java 21, kubectl, Helm, Minikube)
   - [ ] Verificar Docker y Minikube funcionando
   - [ ] Actualizar DEPLOYMENT_CHECKLIST.md

2. **[2 horas] Construcción de Imágenes**
   - [ ] Build de todos los microservicios:
     ```bash
     ./mvnw clean package -DskipTests -T 1C
     ```
   - [ ] Conectar Docker a Minikube:
     ```bash
     eval $(minikube docker-env)
     ```
   - [ ] Build de imágenes Docker (11 servicios):
     - api-gateway
     - client-service
     - case-service
     - document-service (básico)
     - calendar-service (básico)
     - notification-service (básico)
     - n8n-integration-service (básico)
     - payment-service
     - user-service (legacy temporal)
   - [ ] Verificar imágenes: `docker images | grep carrillo`

#### Sesión Tarde (3 horas)

3. **[1.5 horas] Deployment Kubernetes**
   - [ ] Iniciar Minikube:
     ```bash
     minikube start --kubernetes-version=v1.34.0
     ```
   - [ ] Aplicar namespaces:
     ```bash
     kubectl apply -f infrastructure/k8s-manifests/namespaces/
     ```
   - [ ] Aplicar ConfigMaps:
     ```bash
     kubectl apply -f infrastructure/k8s-manifests/configmaps/
     ```
   - [ ] Crear secrets (desde templates)
   - [ ] Deploy PostgreSQL:
     ```bash
     helm install postgresql bitnami/postgresql -n databases
     ```
   - [ ] Deploy NATS:
     ```bash
     helm install nats nats/nats -n messaging
     ```

4. **[1.5 horas] Deployment Microservicios**
   - [ ] Deploy con Helm:
     ```bash
     helm upgrade --install carrillo-dev \
       helm-charts/carrillo-abogados/ -n carrillo-dev
     ```
   - [ ] Verificar pods:
     ```bash
     kubectl get pods -n carrillo-dev
     ```
   - [ ] Troubleshoot pods en CrashLoopBackOff
   - [ ] Validar health checks

**Entregables Día 4**:
- ✅ Todas las imágenes Docker construidas
- ✅ Infraestructura Kubernetes desplegada
- ✅ Servicios corriendo en Minikube

---

### DÍA 5 (15 Diciembre) - VALIDACIÓN Y TESTING 🟡

#### Sesión Mañana (3 horas)

1. **[2 horas] Validación de Servicios**
   - [ ] Port-forward API Gateway:
     ```bash
     kubectl port-forward svc/api-gateway 8080:8080 -n carrillo-dev
     ```
   - [ ] Validar actuator/health de cada servicio
   - [ ] Validar routes en API Gateway:
     ```bash
     curl http://localhost:8080/actuator/gateway/routes
     ```
   - [ ] Testing manual de endpoints básicos
   - [ ] Validar conexión PostgreSQL
   - [ ] Validar mensajería NATS

2. **[1 hora] Database Setup**
   - [ ] Conectar a PostgreSQL:
     ```bash
     kubectl exec -it postgresql-0 -n databases -- \
       psql -U carrillo -d carrillo_legal_tech
     ```
   - [ ] Verificar schemas:
     ```sql
     \dn
     ```
   - [ ] Aplicar migraciones Flyway
   - [ ] Validar tablas creadas

#### Sesión Tarde (2 horas)

3. **[1.5 horas] Smoke Testing**
   - [ ] Crear script de smoke test: `scripts/smoke-test.sh`
   - [ ] Test de cada endpoint crítico
   - [ ] Test de integración básica
   - [ ] Validar logs sin errores

4. **[0.5 horas] Documentación**
   - [ ] Actualizar `docs/operations/OPERATIONS.md`
   - [ ] Documentar troubleshooting encontrado
   - [ ] Actualizar CHANGELOG.md (v0.1.1 - Deployment local exitoso)
   - [ ] Registrar decisiones en DECISIONES_IA.md

**Entregables Día 5**:
- ✅ Plataforma funcionando en Minikube
- ✅ Smoke tests pasando
- ✅ Documentación actualizada

**MILESTONE 1 COMPLETADO**: 🎯 Deployment Local Exitoso

---

## 🟣 FASE 2: IMPLEMENTACIÓN CORE SERVICES (16-20 DIC)

**Objetivo**: Implementar lógica de negocio en microservicios principales.

### DÍA 6-7 (16-17 Diciembre) - CLIENT-SERVICE & CASE-SERVICE 🔴

#### Client-Service (Día 6)

1. **[4 horas] Modelo de Dominio**
   - [ ] Entidades JPA:
     - `Client` (Cliente del bufete)
     - `Contact` (Información de contacto)
     - `Address` (Dirección)
   - [ ] Repositorios JPA
   - [ ] DTOs (Request/Response)
   - [ ] Migraciones Flyway

2. **[2 horas] API REST**
   - [ ] Controllers:
     - POST `/client-service/api/clients` - Crear cliente
     - GET `/client-service/api/clients` - Listar clientes
     - GET `/client-service/api/clients/{id}` - Obtener cliente
     - PUT `/client-service/api/clients/{id}` - Actualizar cliente
     - DELETE `/client-service/api/clients/{id}` - Eliminar cliente
   - [ ] Services (business logic)
   - [ ] Exception handling
   - [ ] Swagger documentation

#### Case-Service (Día 7)

1. **[4 horas] Modelo de Dominio**
   - [ ] Entidades JPA:
     - `LegalCase` (Caso legal)
     - `CaseStatus` (Estado del caso)
     - `CaseTimeline` (Línea temporal)
   - [ ] Relación con Client
   - [ ] Repositorios y DTOs
   - [ ] Migraciones Flyway

2. **[2 horas] API REST**
   - [ ] Controllers CRUD para casos
   - [ ] Endpoints de búsqueda y filtrado
   - [ ] Business logic
   - [ ] Eventos NATS: `carrillo.events.case.created`

**Entregables Día 6-7**:
- ✅ Client-Service funcional con CRUD completo
- ✅ Case-Service funcional con eventos
- ✅ Tests unitarios y de integración

---

### DÍA 8-9 (18-19 Diciembre) - DOCUMENT-SERVICE & CALENDAR-SERVICE 🟡

#### Document-Service (Día 8)

1. **[3 horas] Gestión de Documentos**
   - [ ] Entidades JPA:
     - `Document` (Documento legal)
     - `DocumentType` (Tipo: contrato, demanda, etc.)
     - `DocumentVersion` (Versionado)
   - [ ] Almacenamiento: PostgreSQL (BLOB) + future Google Drive
   - [ ] Upload/download endpoints
   - [ ] Seguridad: validación de tipos MIME

2. **[3 horas] API REST**
   - [ ] Upload de documentos
   - [ ] Metadata management
   - [ ] Asociación documento-caso
   - [ ] Preview generation (básico)

#### Calendar-Service (Día 9)

1. **[2 horas] Modelo de Dominio**
   - [ ] Entidades JPA:
     - `CalendarEvent` (Audiencia, cita, deadline)
     - `EventType`
     - `EventReminder`

2. **[4 horas] Integración Google Calendar**
   - [ ] OAuth2 setup para Google Calendar API
   - [ ] Sync bidireccional (básico)
   - [ ] Crear eventos en Google Calendar
   - [ ] Leer eventos de Google Calendar
   - [ ] Webhooks para updates

**Entregables Día 8-9**:
- ✅ Document-Service con upload/download
- ✅ Calendar-Service integrado con Google

---

### DÍA 10 (20 Diciembre) - NOTIFICATION-SERVICE 🟢

1. **[3 horas] Modelo de Dominio**
   - [ ] Entidades:
     - `Notification`
     - `NotificationChannel` (Email, SMS)
     - `NotificationTemplate`
   - [ ] Repositorios y DTOs

2. **[3 horas] Integración Gmail API**
   - [ ] OAuth2 setup para Gmail API
   - [ ] Send email via Gmail API
   - [ ] Templates Thymeleaf
   - [ ] Queue de notificaciones (NATS)
   - [ ] Listener de eventos:
     - `carrillo.events.case.created` → Email de confirmación
     - `carrillo.events.client.created` → Email bienvenida

**Entregables Día 10**:
- ✅ Notification-Service funcional
- ✅ Emails enviándose desde eventos

**MILESTONE 2 COMPLETADO**: 🎯 Core Services Implementados

---

## 🟠 FASE 3: INTEGRACIONES EXTERNAS (21-23 DIC)

### DÍA 11-12 (21-22 Diciembre) - N8N INTEGRATION 🔴

1. **[Día 11 - 4 horas] N8N-Integration-Service**
   - [ ] Endpoints webhook para N8N:
     - POST `/n8n-integration-service/webhooks/trigger`
   - [ ] Publicar eventos a NATS desde webhooks N8N
   - [ ] Consumir eventos desde NATS → trigger N8N webhooks
   - [ ] Autenticación: API Keys

2. **[Día 11 - 2 horas] Documentación N8N**
   - [ ] Workflows ejemplo:
     - Nuevo cliente → Crear carpeta Drive
     - Nuevo caso → Notificar equipo Slack
     - Deadline próximo → Reminder email
   - [ ] Setup N8N Pro (si disponible)

3. **[Día 12 - 6 horas] Payment-Service**
   - [ ] Modelo de dominio:
     - `Payment` (Pago a entidad gubernamental)
     - `PaymentStatus`
     - `PaymentReceipt`
   - [ ] API REST CRUD
   - [ ] Generación de recibos PDF
   - [ ] Asociación pago-caso

**Entregables Día 11-12**:
- ✅ N8N-Integration-Service funcionando
- ✅ Payment-Service implementado

---

### DÍA 13 (23 Diciembre) - OAUTH2 Y SEGURIDAD 🟡

1. **[3 horas] OAuth2 en API Gateway**
   - [ ] Spring Security OAuth2 configuration
   - [ ] Google Workspace (@carrilloabgd.com)
   - [ ] JWT tokens
   - [ ] Roles: ADMIN, ABOGADO, ADMINISTRATIVO

2. **[3 horas] Security Testing**
   - [ ] Validar autenticación funciona
   - [ ] Test de autorización por rol
   - [ ] Audit log implementation
   - [ ] RBAC policies Kubernetes

**Entregables Día 13**:
- ✅ OAuth2 funcional
- ✅ Seguridad implementada

**MILESTONE 3 COMPLETADO**: 🎯 Integraciones Externas Completas

---

## 🔴 FASE 4: TESTING Y VALIDACIÓN (24-25 DIC)

### DÍA 14 (24 Diciembre) - TESTING EXHAUSTIVO 🔴

1. **[3 horas] Tests Automatizados**
   - [ ] Tests unitarios (>70% coverage)
   - [ ] Tests de integración con Testcontainers
   - [ ] Tests e2e principales flujos

2. **[3 horas] Testing Manual**
   - [ ] Flujo completo: Alta cliente → Crear caso → Subir documento → Audiencia calendar
   - [ ] Validar notificaciones
   - [ ] Validar N8N webhooks
   - [ ] Validar OAuth2

**Entregables Día 14**:
- ✅ Suite de tests completa
- ✅ Coverage >70%

---

### DÍA 15 (25 Diciembre) - PERFORMANCE Y MONITORING 🟡

1. **[3 horas] Performance Testing**
   - [ ] Load testing con JMeter/Gatling
   - [ ] Optimizaciones necesarias
   - [ ] Tuning JVM y PostgreSQL

2. **[3 horas] Monitoring Setup**
   - [ ] Prometheus scraping configurado
   - [ ] Grafana dashboards importados
   - [ ] Loki logs centralizados
   - [ ] Alerting rules básicas

**Entregables Día 15**:
- ✅ Performance validado
- ✅ Monitoring operacional

**MILESTONE 4 COMPLETADO**: 🎯 Plataforma Validada

---

## 📘 FASE 5: DOCUMENTACIÓN FINAL Y ENTREGA (26-27 DIC)

### DÍA 16 (26 Diciembre) - DOCUMENTACIÓN FINAL 🔴

1. **[4 horas] Documentación Técnica**
   - [ ] Actualizar README.md raíz
   - [ ] Actualizar CLAUDE.md con nuevos features
   - [ ] Actualizar .github/copilot-instructions.md
   - [ ] Completar API_REFERENCE.md con Swagger exports

2. **[2 horas] Documentación Académica**
   - [ ] Crear `docs/project/ACADEMIC_DELIVERABLES.md`
   - [ ] Checklist requisitos Plataformas II
   - [ ] Screenshots y diagramas
   - [ ] Video demo (5 min)

**Entregables Día 16**:
- ✅ Documentación técnica completa
- ✅ Material académico listo

---

### DÍA 17 (27 Diciembre) - REVISIÓN Y ENTREGA 🟢

1. **[3 horas] Revisión Final**
   - [ ] Code review completo
   - [ ] Validar todos los checklists
   - [ ] Testing de regresión
   - [ ] Cleanup código

2. **[2 horas] Deployment Final**
   - [ ] Tag release v0.2.0
   - [ ] Deploy final en Minikube
   - [ ] Backup de documentación
   - [ ] Push a GitHub

3. **[1 hora] Preparación Entrega**
   - [ ] Zip del proyecto
   - [ ] README de entrega
   - [ ] Instrucciones deployment

**Entregables Día 17**:
- ✅ MVP Completo
- ✅ Entrega académica lista

**MILESTONE 5 COMPLETADO**: 🎯 MVP Entregado

---

## 📊 MÉTRICAS DE ÉXITO

### Técnicas

- [ ] 11 microservicios funcionando
- [ ] >70% test coverage
- [ ] <500ms response time p95
- [ ] 0 critical vulnerabilities
- [ ] 100% health checks passing

### Funcionales

- [ ] Alta de cliente funcional
- [ ] Creación de caso funcional
- [ ] Upload de documentos funcional
- [ ] Calendar sync funcional
- [ ] Notificaciones por email funcionales
- [ ] N8N workflows funcionando

### Documentación

- [ ] 90%+ documentación completa
- [ ] Todas las decisiones registradas
- [ ] API completamente documentada
- [ ] Runbooks operacionales listos

---

## 🚨 RIESGOS Y MITIGACIONES

| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|--------------|---------|------------|
| Google APIs no autorizan | Media | Alto | Mock inicial, real después |
| N8N Pro no disponible | Baja | Medio | Usar N8N self-hosted |
| Performance insuficiente | Media | Medio | Optimización incremental |
| Tests de integración lentos | Alta | Bajo | Parallel execution |
| Tiempo insuficiente | Media | Alto | Priorizar MVP features |

---

## 📞 PRÓXIMOS PASOS INMEDIATOS (HOY)

### Orden de Ejecución

1. ✅ **[COMPLETADO]** Análisis de documentación (DOCUMENTACION_ANALISIS.md)
2. ✅ **[COMPLETADO]** Plan de trabajo (este documento)
3. 🔄 **[AHORA]** Iniciar Fase 0 - Día 1:
   - Crear `docs/business/MODELO_NEGOCIO.md`
   - Crear `docs/business/PROCESOS_LEGALES.md`
   - Crear `docs/business/CASOS_USO.md`
   - Crear `docs/development/CHANGELOG.md`
   - Crear `docs/development/DECISIONES_IA.md`

---

## ✅ CRITERIOS DE COMPLETITUD

**Un entregable está completo cuando**:

- [ ] Código implementado y funcionando
- [ ] Tests unitarios >70% coverage
- [ ] Tests de integración pasando
- [ ] Documentación actualizada
- [ ] Decisión registrada en DECISIONES_IA.md
- [ ] CHANGELOG.md actualizado
- [ ] Code review aprobado
- [ ] Merge a `dev` branch

---

**Estado**: 📋 PLAN APROBADO Y LISTO PARA EJECUCIÓN  
**Inicio Oficial**: 11 de Diciembre, 2025  
**Próxima Acción**: Crear documentación de modelo de negocio

---

*Generado por GitHub Copilot en colaboración con Claude Code*  
*Versión 1.0 - 11 de Diciembre, 2025*
