# Carrillo Abogados - Plataforma Legal Tech

## Descripción
Plataforma cloud-native de gestión legal empresarial construida con microservicios sobre Kubernetes.

## Tecnologías
- **Java**: 21 LTS
- **Spring Boot**: 3.3.13
- **Spring Cloud**: 2023.0.3
- **Kubernetes**: 1.34.0
- **Message Queue**: NATS
- **Database**: PostgreSQL 16
- **Orchestration**: Helm Charts

## Microservicios Actuales

### Infraestructura
- `service-discovery`: Eureka Server (migrar a K8s nativo)
- `cloud-config`: Configuración centralizada (migrar a ConfigMaps)
- `api-gateway`: Spring Cloud Gateway
- `proxy-client`: Autenticación y proxy

### Negocio
- `user-service`: Gestión de usuarios (→ client-service)
- `order-service`: Gestión de órdenes (→ case-service)
- `payment-service`: Procesamiento de pagos
- `document-service`: Gestión de documentos legales (WIP)
- `calendar-service`: Integración Google Calendar (WIP)
- `notification-service`: Email/SMS/Push (WIP)
- `n8n-integration-service`: Workflows y automatizaciones (WIP)

## Desarrollo Local

### Prerrequisitos
- Java 21
- Maven 3.8+
- Docker Desktop
- Minikube
- kubectl, Helm

### Compilar
```bash
./mvnw clean verify -T 1C
```

### Ejecutar en Minikube
```bash
# Ver guía en docs/operations/local-setup.md
```

## Estructura del Proyecto

```
├── microservices/          # Código de servicios
├── helm-charts/            # Charts de Kubernetes
├── infrastructure/         # IaC (Terraform, K8s manifests)
├── docs/                   # Documentación
└── scripts/                # Scripts de automatización
```

## Licencia

Propietario - Carrillo Abogados © 2025