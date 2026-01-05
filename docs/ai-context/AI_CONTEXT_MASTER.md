# 🤖 AI Context Master - Carrillo Abogados Legal Tech Platform

**Última Actualización**: 5 de Enero, 2026  
**Propósito**: Índice de documentación para IAs que trabajan en el proyecto

---

## 📍 DOCUMENTOS DE REFERENCIA

Para obtener contexto completo del proyecto, consultar estos archivos:

| Archivo | Propósito | Prioridad |
|---------|-----------|-----------|
| `CLAUDE.md` | Contexto técnico completo | 🔴 Alta |
| `PROYECTO_ESTADO.md` | Estado actual detallado y hitos | 🔴 Alta |
| `.github/copilot-instructions.md` | Instrucciones de desarrollo | 🔴 Alta |
| `docs/business/MODELO_NEGOCIO.md` | Contexto de negocio del cliente | 🟡 Media |
| `docs/business/REQUERIMIENTOS.md` | Requerimientos funcionales | 🟡 Media |
| `docs/architecture/ARCHITECTURE.md` | Arquitectura técnica | 🟡 Media |

---

## 🎯 ESTADO ACTUAL DEL PROYECTO

**Fase**: FASE 8 - CI/CD Completamente Funcional  
**MVP Target**: 27 Marzo 2026  
**Ramas**: `dev` y `main` sincronizadas

### CI/CD Status
- ✅ Build & Test: 105 tests pasando
- ✅ Security Scan: Trivy + CodeQL v4
- ✅ Docker Build: 8 imágenes en ghcr.io

---

## ⚠️ CRÍTICO: Entorno Windows + WSL

```powershell
# ✅ CORRECTO - Usar wsl bash -c para kubectl/minikube/helm
wsl bash -c "kubectl get pods -n carrillo-dev"

# ❌ INCORRECTO - kubectl directo en PowerShell no funciona
kubectl get pods  # FALLA
```

---

## 📂 ESTRUCTURA DE DOCUMENTACIÓN

```
docs/
├── ai-context/          # Este directorio - índices para IAs
├── architecture/        # ADRs y arquitectura técnica
├── business/            # Modelo de negocio, requerimientos
├── operations/          # Guías de deploy y operaciones
├── development/         # Guías de desarrollo
├── security/            # Documentación de seguridad
└── archive/             # Documentos obsoletos
```

---

## 🔗 REFERENCIAS EXTERNAS

- **Snyk Dashboard**: https://app.snyk.io/org/alexisj16
- **SonarCloud**: https://sonarcloud.io/project/overview?id=AlexisJ16_CarrilloAbogados
- **n8n Cloud**: https://carrilloabgd.app.n8n.cloud
