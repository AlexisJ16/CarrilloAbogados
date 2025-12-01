# Carrillo Abogados - Plataforma Legal Tech

Plataforma cloud-native de gestión legal empresarial para Carrillo Abogados, Cali, Colombia.

## Estado del Proyecto

- **Build Status**: ![Build](https://img.shields.io/badge/build-passing-brightgreen)
- **Version**: 0.1.0
- **Java**: 21 LTS
- **Spring Boot**: 3.3.13
- **Kubernetes**: 1.34.0

## Quick Start

### Prerrequisitos
- JDK 21
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
# Iniciar cluster
minikube start --kubernetes-version=v1.34.0

# Crear namespaces
kubectl apply -f infrastructure/k8s-manifests/namespaces/

# Desplegar
helm install carrillo-dev helm-charts/carrillo-abogados/ -n carrillo-dev
```

## Arquitectura

11 microservicios + API Gateway:

- client-service, case-service, payment-service
- document-service, calendar-service, notification-service
- n8n-integration-service

Ver [Documentación Completa](docs/architecture/ARCHITECTURE.md)

## Desarrollo

Ver [CLAUDE.md](CLAUDE.md) para contexto completo del proyecto.

## Licencia

Propietario - Carrillo Abogados © 2025