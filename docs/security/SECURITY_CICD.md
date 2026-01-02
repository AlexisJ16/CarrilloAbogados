# 🔐 Guía de Seguridad - CI/CD Pipeline

**Última Actualización**: 30 de Diciembre, 2025  
**Estado**: ✅ Integrado con GitHub Actions

---

## 📋 Herramientas de Seguridad

| Herramienta | Propósito | Integración |
|-------------|-----------|-------------|
| **Snyk** | Vulnerabilidades en dependencias y código | GitHub Actions + Dashboard |
| **SonarCloud** | Análisis estático de código, code smells | GitHub Actions + Dashboard |
| **Trivy** | Escaneo de contenedores y filesystem | GitHub Actions |
| **OWASP ZAP** | DAST / Penetration testing | Manual / Workflow dispatch |

---

## 🛡️ Snyk

### Dashboard
- **URL**: https://app.snyk.io/org/alexisj16
- **Proyectos**: Todos los pom.xml de microservicios importados

### Configuración en GitHub Actions

El workflow `security-scan.yml` ejecuta:
1. **Snyk Open Source**: Escanea dependencias Maven
2. **Snyk Code**: Analiza código fuente (SAST)

### Secrets Requeridos

```
SNYK_TOKEN: Tu token de API de Snyk
```

Para obtenerlo:
1. Ir a https://app.snyk.io/account
2. Copiar "Auth Token"
3. Agregar en GitHub → Settings → Secrets → Actions → New secret

### Interpretar Resultados

| Severidad | Acción Requerida |
|-----------|------------------|
| **Critical** | Corregir inmediatamente |
| **High** | Corregir antes del próximo release |
| **Medium** | Planificar corrección |
| **Low** | Evaluar si aplica |

---

## 📊 SonarCloud

### Dashboard
- **URL**: https://sonarcloud.io/project/overview?id=AlexisJ16_CarrilloAbogados

### Métricas Clave

| Métrica | Descripción | Objetivo |
|---------|-------------|----------|
| **Security** | Vulnerabilidades de seguridad | 0 |
| **Reliability** | Bugs potenciales | 0 |
| **Maintainability** | Code smells, deuda técnica | A rating |
| **Coverage** | Cobertura de tests | > 80% |
| **Duplications** | Código duplicado | < 3% |

### Secrets Requeridos

```
SONAR_TOKEN: Tu token de SonarCloud
```

Para obtenerlo:
1. Ir a https://sonarcloud.io/account/security
2. Generate Token → Copiar
3. Agregar en GitHub → Settings → Secrets → Actions

### Quality Gates

El proyecto usa el Quality Gate "Sonar way" que requiere:
- 0 bugs nuevos
- 0 vulnerabilidades nuevas
- 0 security hotspots pendientes
- Coverage en código nuevo ≥ 80%
- Duplications en código nuevo < 3%

---

## 🐳 Trivy

### Escaneo Automático

Trivy escanea:
1. **Filesystem**: Vulnerabilidades en código y configs
2. **Containers**: Imágenes Docker antes del push
3. **IaC**: Terraform, Kubernetes manifests

### Resultados

Los resultados se publican automáticamente en:
- GitHub Security → Code scanning alerts

### Severidades Reportadas

Solo se reportan **HIGH** y **CRITICAL** para evitar ruido.

---

## 🕷️ OWASP ZAP (Opcional)

### Uso Manual

OWASP ZAP está configurado pero deshabilitado por defecto. Para ejecutar:

1. Ir a GitHub → Actions → Security Scan
2. Click "Run workflow"
3. Seleccionar rama y ejecutar

### Requisitos

- Tener un ambiente de staging accesible públicamente
- Configurar la URL target en el workflow

---

## 📋 Workflow de Seguridad

```
┌─────────────────────────────────────────────────────────────────┐
│                    SECURITY SCAN PIPELINE                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Trigger: Push to main/staging, Weekly schedule, Manual        │
│                                                                 │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐            │
│  │   Snyk      │  │ SonarCloud  │  │   Trivy     │            │
│  │  Scan       │  │  Analysis   │  │   Scan      │            │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘            │
│         │                │                │                    │
│         └────────────────┼────────────────┘                    │
│                          │                                      │
│                          ▼                                      │
│                  ┌──────────────┐                              │
│                  │   Summary    │                              │
│                  │   Report     │                              │
│                  └──────────────┘                              │
│                                                                 │
│  Results:                                                       │
│  • GitHub Security tab (SARIF uploads)                         │
│  • Snyk Dashboard                                              │
│  • SonarCloud Dashboard                                        │
│  • Job Summary in Actions                                      │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## ⚙️ Configuración Inicial

### 1. Agregar Secrets en GitHub

```bash
# Ir a: GitHub → Settings → Secrets and variables → Actions

SNYK_TOKEN=<tu-token-snyk>
SONAR_TOKEN=<tu-token-sonarcloud>
```

### 2. Verificar Integración

1. Hacer push a main o staging
2. Ir a Actions → ver ejecución de "Security Scan"
3. Verificar resultados en cada dashboard

### 3. Configurar Notificaciones

En Snyk:
1. Settings → Notifications → Activar alertas por email

En SonarCloud:
1. Administration → Notifications → Configurar preferencias

---

## 📊 Estado Actual del Proyecto

### Snyk (según captura)

| Servicio | Critical | High | Medium | Low |
|----------|:--------:|:----:|:------:|:---:|
| api-gateway | 1 | 9 | 4 | 2 |
| n8n-integration-service | 0 | 12 | 5 | 2 |
| notification-service | 0 | 8 | 4 | 1 |
| calendar-service | 0 | 8 | 4 | 1 |
| case-service | 0 | 8 | 4 | 1 |
| document-service | 0 | 8 | 4 | 1 |
| payment-service | 0 | 8 | 4 | 1 |
| user-service | 0 | 8 | 4 | 1 |
| **TOTAL** | **1** | **80** | **83** | **62** |

### SonarCloud (según captura)

| Categoría | Issues | Severidad |
|-----------|:------:|-----------|
| Security | 8 | - |
| Reliability | 0 | - |
| Maintainability | 27 | 12 Blocker, 8 High, 8 Medium, 7 Low |

### Próximos Pasos

1. Resolver el issue **Critical** en api-gateway
2. Reducir los **Blocker** issues en SonarCloud
3. Agregar tests para mejorar coverage
4. Configurar Quality Gate más estricto

---

## 🔗 Enlaces Rápidos

- [Snyk Dashboard](https://app.snyk.io/org/alexisj16)
- [SonarCloud Dashboard](https://sonarcloud.io/project/overview?id=AlexisJ16_CarrilloAbogados)
- [GitHub Security Tab](https://github.com/AlexisJ16/CarrilloAbogados/security)
- [Security Scan Workflow](../.github/workflows/security-scan.yml)

---

*Documentación de Seguridad CI/CD - Carrillo Abogados Legal Tech Platform*
