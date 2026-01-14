# 🔐 Secrets Management - Carrillo Abogados

**Última Actualización**: 14 de Enero, 2026

---

## 📋 Resumen

Este documento describe cómo gestionar de forma segura las API keys, tokens y credenciales del proyecto.

---

## 🏗️ Estructura de Secrets

### GitHub Repository Secrets (Configurados)

| Secret | Propósito | Estado |
|--------|-----------|--------|
| `SNYK_TOKEN` | Análisis de vulnerabilidades | ✅ Configurado |
| `SONAR_TOKEN` | Análisis de código SonarCloud | ✅ Configurado |

### GitHub Repository Secrets (Por Configurar)

| Secret | Propósito | Cuándo |
|--------|-----------|--------|
| `N8N_API_KEY` | Integración n8n Cloud | Cuando se use en CI/CD |
| `GCP_PROJECT_ID` | Deploy a GCP | Pre-producción |
| `GCP_SA_KEY` | Service Account GCP | Pre-producción |
| `GOOGLE_CLIENT_ID` | OAuth2 Google Workspace | Producción |
| `GOOGLE_CLIENT_SECRET` | OAuth2 Google Workspace | Producción |

---

## 📁 Archivos de Configuración Local

### Archivos que NUNCA deben commitearse

Estos archivos están en `.gitignore`:

```
.mcp.json              # Configuración MCP con API keys
.claude/settings.local.json  # Configuración Claude local
.env                   # Variables de entorno con secrets
.env.local             # Variables locales
credentials.json       # Credenciales Google
service-account*.json  # Service accounts GCP
*.pem, *.key, *.p12    # Certificados y claves
```

### Plantillas disponibles (sí se commitean)

| Archivo | Propósito |
|---------|-----------|
| `.mcp.json.example` | Template para configuración MCP |
| `.env.example` | Template para variables de entorno |

---

## 🔧 Configuración Local

### 1. Configurar MCP (n8n)

```bash
# Copiar template
cp .mcp.json.example .mcp.json

# Editar con tus credenciales
# Reemplazar "your-n8n-api-key-here" con tu API key real
```

### 2. Configurar Variables de Entorno

```bash
# Copiar template
cp .env.example .env

# Editar con tus valores
# Nunca commitear .env
```

### 3. Verificar que está ignorado

```bash
git status
# .mcp.json y .env NO deben aparecer
```

---

## 🚀 Configuración CI/CD (GitHub Actions)

### Agregar un nuevo secret

1. Ir a: `Settings → Secrets and variables → Actions`
2. Click "New repository secret"
3. Nombre: `NOMBRE_DEL_SECRET` (MAYÚSCULAS con guiones bajos)
4. Valor: El valor del secret
5. Click "Add secret"

### Usar secrets en workflows

```yaml
jobs:
  build:
    steps:
      - name: Use secret
        env:
          MY_SECRET: ${{ secrets.MY_SECRET_NAME }}
        run: echo "Using secret..."
```

---

## 🔑 Obtener API Keys

### n8n Cloud

1. Ir a https://carrilloabgd.app.n8n.cloud
2. Settings → API → Create API Key
3. Copiar el JWT token generado

### Google Cloud Platform (Futuro)

1. Ir a https://console.cloud.google.com
2. APIs & Services → Credentials
3. Create OAuth 2.0 Client ID
4. Descargar `credentials.json`

### Snyk

1. Ir a https://app.snyk.io/account
2. General Settings → Auth Token
3. Copiar token

### SonarCloud

1. Ir a https://sonarcloud.io/account/security
2. Generate Tokens
3. Copiar token

---

## ⚠️ Qué hacer si se expone un secret

1. **Rotar inmediatamente** la credencial comprometida
2. **Revocar** el token anterior en el servicio correspondiente
3. **Verificar** que `.gitignore` incluye el archivo
4. **Limpiar** el historial de git si es necesario:
   ```bash
   git filter-branch --force --index-filter \
     "git rm --cached --ignore-unmatch PATH_TO_FILE" \
     --prune-empty --tag-name-filter cat -- --all
   ```
5. **Forzar push** a todas las ramas afectadas

---

## 📚 Referencias

- [GitHub Encrypted Secrets](https://docs.github.com/en/actions/security-guides/encrypted-secrets)
- [n8n API Documentation](https://docs.n8n.io/api/)
- [Google Cloud IAM](https://cloud.google.com/iam/docs)

---

*Documento de seguridad - Carrillo Abogados Legal Tech*
