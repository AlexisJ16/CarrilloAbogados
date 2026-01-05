# 🚀 AI Quick Start - Carrillo Abogados Legal Tech

> **Última actualización**: 5 de Enero, 2026  
> **Fase Actual**: FASE 8 - CI/CD Completamente Funcional

---

## 📌 RESUMEN EN 30 SEGUNDOS

**Proyecto**: Plataforma legal cloud-native para bufete Carrillo Abogados (Cali, Colombia)  
**Stack**: Java 21 + Spring Boot 3.3.13 + PostgreSQL 16 + Kubernetes  
**Estado**: CI/CD funcionando, 8 microservicios activos  
**MVP Target**: 27 Marzo 2026

---

## 🏗️ MICROSERVICIOS

| Servicio | Puerto | Estado |
|----------|--------|--------|
| api-gateway | 8080 | ✅ 100% |
| client-service | 8200 | ✅ 100% |
| case-service | 8300 | ✅ 95% |
| payment-service | 8400 | 🔄 15% |
| document-service | 8500 | 🔄 15% |
| calendar-service | 8600 | 🔄 15% |
| notification-service | 8700 | ✅ 80% |
| n8n-integration-service | 8800 | ✅ 95% |

---

## ⚡ COMANDOS RÁPIDOS

### Docker Compose (Desarrollo Local)
```powershell
docker-compose up -d          # Levantar todo
docker-compose ps             # Ver estado
docker-compose logs -f        # Ver logs
```

### Build Maven
```powershell
.\mvnw clean package -DskipTests -T 1C   # Build rápido paralelo
.\mvnw test -pl client-service           # Tests de un servicio
```

### Kubernetes (via WSL)
```powershell
wsl bash -c "kubectl get pods -n carrillo-dev"
wsl bash -c "minikube status"
```

---

## 📁 DOCUMENTOS PRINCIPALES

| Archivo | Lee primero si... |
|---------|-------------------|
| `CLAUDE.md` | Necesitas contexto técnico general |
| `PROYECTO_ESTADO.md` | Quieres ver estado actual y próximos pasos |
| `.github/copilot-instructions.md` | Vas a desarrollar código |
| `docs/business/MODELO_NEGOCIO.md` | Necesitas entender el cliente |

---

## 🔐 CI/CD (Enero 2026)

- ✅ GitHub Actions: Build + Test (105 tests)
- ✅ Security Scan: Trivy + CodeQL v4
- ✅ Docker: 8 imágenes en ghcr.io/alexisj16/
- ⚠️ Deploy GCP: Pendiente configuración secrets
