# 🔐 GitHub Repository Secrets Configuration

**Última Actualización**: Enero 2026  
**Propósito**: Documentar todos los secrets requeridos para los GitHub Actions workflows

---

## 📋 Resumen de Workflows y Secrets

| Workflow | Secret Requerido | Estado |
|----------|------------------|--------|
| `ci-cd-pipeline.yml` | `GITHUB_TOKEN` | ✅ Auto-generado |
| `security-scan.yml` | `SNYK_TOKEN` | ⚠️ Requerido |
| `security-scan.yml` | `SONAR_TOKEN` | ⚠️ Requerido |
| `deploy-gcp.yml` | `GCP_PROJECT_ID` | ⚠️ Requerido |
| `deploy-gcp.yml` | `GCP_SA_KEY` | ⚠️ Requerido |
| `deploy-gcp.yml` | `CLOUD_SQL_HOST` | ⚠️ Requerido |
| `deploy-gcp.yml` | `CLOUD_SQL_USER` | ⚠️ Requerido |
| `deploy-gcp.yml` | `CLOUD_SQL_PASSWORD` | ⚠️ Requerido |

---

## 🛡️ Security Scan Secrets

### SNYK_TOKEN

**Propósito**: Autenticación con Snyk para escaneo de vulnerabilidades

**Cómo obtenerlo**:
1. Ir a [Snyk Dashboard](https://app.snyk.io/account)
2. Click en "Settings" → "Service accounts" o "API token"
3. Copiar el token
4. En GitHub: Settings → Secrets and variables → Actions → New repository secret

**Nombre del secret**: `SNYK_TOKEN`

### SONAR_TOKEN

**Propósito**: Autenticación con SonarCloud para análisis de código

**Cómo obtenerlo**:
1. Ir a [SonarCloud](https://sonarcloud.io/account/security)
2. Generar nuevo token en "Security" → "Generate Tokens"
3. Copiar el token
4. En GitHub: Settings → Secrets and variables → Actions → New repository secret

**Nombre del secret**: `SONAR_TOKEN`

---

## ☁️ GCP Deploy Secrets

### GCP_PROJECT_ID

**Propósito**: ID del proyecto de Google Cloud

**Cómo obtenerlo**:
1. Ir a [Google Cloud Console](https://console.cloud.google.com)
2. Seleccionar el proyecto
3. El ID aparece en la URL y en el selector de proyectos

**Ejemplo**: `carrillo-abogados-prod`

### GCP_SA_KEY

**Propósito**: Service Account key para autenticación con GCP

**Cómo obtenerlo**:
1. En GCP Console, ir a IAM & Admin → Service Accounts
2. Crear o seleccionar service account con permisos:
   - Cloud Run Admin
   - Artifact Registry Writer
   - Cloud SQL Client
3. Generar nueva clave JSON
4. Copiar el contenido JSON completo
5. Pegarlo como secret en GitHub

**Permisos mínimos requeridos**:
```
roles/run.admin
roles/artifactregistry.writer
roles/cloudsql.client
roles/iam.serviceAccountUser
```

### CLOUD_SQL_HOST

**Propósito**: Dirección del servidor Cloud SQL

**Formato**: Para conexión con Cloud SQL Proxy: `/cloudsql/PROJECT:REGION:INSTANCE`  
**Ejemplo**: `carrillo-abogados-prod:us-central1:carrillo-db`

### CLOUD_SQL_USER

**Propósito**: Usuario de la base de datos PostgreSQL

**Valor típico**: `carrillo`

### CLOUD_SQL_PASSWORD

**Propósito**: Contraseña del usuario de base de datos

**Recomendación**: Usar password fuerte de 32+ caracteres

---

## 🔧 Configuración en GitHub

### Pasos para agregar secrets

1. Ir al repositorio en GitHub
2. Click en **Settings** → **Secrets and variables** → **Actions**
3. Click en **New repository secret**
4. Agregar nombre y valor
5. Click en **Add secret**

### Secrets actuales configurados

Para verificar qué secrets están configurados, ir a:
```
https://github.com/AlexisJ16/CarrilloAbogados/settings/secrets/actions
```

---

## ⚠️ Notas de Seguridad

1. **NUNCA** commitear secrets en el código
2. **NUNCA** loggear secrets en los workflows
3. Rotar secrets periódicamente (cada 90 días recomendado)
4. Usar secrets diferentes para staging y production
5. El `GITHUB_TOKEN` se genera automáticamente, no necesita configuración

---

## 📊 Estado Actual

| Secret | Configurado | Verificado |
|--------|-------------|------------|
| SNYK_TOKEN | ✅ | 2 Ene 2026 |
| SONAR_TOKEN | ✅ | 2 Ene 2026 |
| GCP_PROJECT_ID | ❌ | Pendiente |
| GCP_SA_KEY | ❌ | Pendiente |
| CLOUD_SQL_HOST | ❌ | Pendiente |
| CLOUD_SQL_USER | ❌ | Pendiente |
| CLOUD_SQL_PASSWORD | ❌ | Pendiente |

---

## 🔗 Links Útiles

- [Snyk Dashboard](https://app.snyk.io/org/alexisj16)
- [SonarCloud Dashboard](https://sonarcloud.io/project/overview?id=AlexisJ16_CarrilloAbogados)
- [GitHub Actions Secrets Docs](https://docs.github.com/en/actions/security-guides/using-secrets-in-github-actions)
- [GCP Service Accounts](https://cloud.google.com/iam/docs/service-accounts)
