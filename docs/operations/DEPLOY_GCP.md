# 🚀 Guía de Deploy a GCP Cloud Run

**Versión**: 1.0  
**Fecha**: 3 de Enero, 2026  
**Estado**: ✅ Documento de Producción

---

## 📋 Tabla de Contenidos

1. [Arquitectura de Producción](#arquitectura-de-producción)
2. [Requisitos Previos](#requisitos-previos)
3. [Configuración Inicial de GCP](#configuración-inicial-de-gcp)
4. [Configuración de Cloud SQL](#configuración-de-cloud-sql)
5. [Configuración de Artifact Registry](#configuración-de-artifact-registry)
6. [Deploy Manual](#deploy-manual)
7. [Deploy Automático (CI/CD)](#deploy-automático-cicd)
8. [Configuración de Dominios](#configuración-de-dominios)
9. [Monitoreo y Logs](#monitoreo-y-logs)
10. [Troubleshooting](#troubleshooting)

---

## 🏗️ Arquitectura de Producción

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         PRODUCCIÓN - GCP                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    INTERNET / DNS                                    │   │
│  │                                                                      │   │
│  │   app.carrilloabgd.com ──────► Cloud Run (Frontend)                 │   │
│  │   api.carrilloabgd.com ──────► Cloud Run (API Gateway)              │   │
│  │                                                                      │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                │                                            │
│                                ▼                                            │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │                    CLOUD RUN (Servicios)                             │  │
│  │                                                                      │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐              │  │
│  │  │  Frontend    │  │ API Gateway  │  │   Backend    │              │  │
│  │  │  (Next.js)   │  │ (Spring)     │  │  Services    │              │  │
│  │  │  Port 3000   │  │  Port 8080   │  │              │              │  │
│  │  └──────────────┘  └──────────────┘  │ ┌──────────┐ │              │  │
│  │                           │          │ │ client   │ │              │  │
│  │                           │          │ │ case     │ │              │  │
│  │                           └──────────┼─│ notif    │ │              │  │
│  │                                      │ └──────────┘ │              │  │
│  │                                      └──────────────┘              │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│                                │                                            │
│                                ▼                                            │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │                    CLOUD SQL (PostgreSQL 16)                         │  │
│  │                                                                      │  │
│  │   ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐              │  │
│  │   │ clients │  │  cases  │  │  notifs │  │  ...    │              │  │
│  │   │ schema  │  │ schema  │  │ schema  │  │ schemas │              │  │
│  │   └─────────┘  └─────────┘  └─────────┘  └─────────┘              │  │
│  │                                                                      │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Servicios y Recursos

| Servicio | Tipo | Recursos | Costo Estimado |
|----------|------|----------|----------------|
| Frontend | Cloud Run | 512Mi, 1 CPU | ~$10-15/mes |
| API Gateway | Cloud Run | 512Mi, 1 CPU | ~$10-15/mes |
| client-service | Cloud Run | 512Mi, 1 CPU | ~$10-15/mes |
| case-service | Cloud Run | 512Mi, 1 CPU | ~$10-15/mes |
| notification-service | Cloud Run | 256Mi, 1 CPU | ~$5-10/mes |
| Cloud SQL | db-f1-micro | 1 vCPU, 614MB | ~$10-15/mes |
| **Total Estimado** | | | **~$55-85/mes** |

---

## ✅ Requisitos Previos

### 1. Cuenta de GCP
- Cuenta activa con billing habilitado
- Proyecto creado: `carrillo-abogados` (o similar)
- APIs habilitadas (se habilitan automáticamente)

### 2. Herramientas Locales
```bash
# Instalar gcloud CLI
# https://cloud.google.com/sdk/docs/install

# Autenticarse
gcloud auth login
gcloud auth application-default login

# Configurar proyecto
gcloud config set project YOUR_PROJECT_ID
gcloud config set compute/region us-central1
```

### 3. Secrets de GitHub
Configurar en Settings → Secrets and variables → Actions:

| Secret | Descripción | Ejemplo |
|--------|-------------|---------|
| `GCP_PROJECT_ID` | ID del proyecto GCP | `carrillo-abogados-prod` |
| `GCP_SA_KEY` | JSON de Service Account | `{"type": "service_account", ...}` |
| `CLOUD_SQL_HOST` | IP de Cloud SQL | `10.0.0.5` o `/cloudsql/PROJECT:REGION:INSTANCE` |
| `CLOUD_SQL_USER` | Usuario de base de datos | `carrillo` |
| `CLOUD_SQL_PASSWORD` | Contraseña de base de datos | `***` |
| `JWT_SECRET` | Secret para JWT | `CarrilloAbogados...` |

---

## ☁️ Configuración Inicial de GCP

### Paso 1: Crear Proyecto

```bash
# Crear proyecto (si no existe)
gcloud projects create carrillo-abogados-prod --name="Carrillo Abogados"

# Configurar proyecto
gcloud config set project carrillo-abogados-prod

# Habilitar billing (requiere cuenta de facturación)
gcloud billing accounts list
gcloud billing projects link carrillo-abogados-prod --billing-account=BILLING_ACCOUNT_ID
```

### Paso 2: Habilitar APIs

```bash
# Habilitar APIs necesarias
gcloud services enable \
  run.googleapis.com \
  sqladmin.googleapis.com \
  artifactregistry.googleapis.com \
  cloudbuild.googleapis.com \
  secretmanager.googleapis.com
```

### Paso 3: Crear Service Account

```bash
# Crear Service Account para CI/CD
gcloud iam service-accounts create github-actions \
  --display-name="GitHub Actions CI/CD"

# Asignar roles
PROJECT_ID=$(gcloud config get-value project)

gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:github-actions@$PROJECT_ID.iam.gserviceaccount.com" \
  --role="roles/run.admin"

gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:github-actions@$PROJECT_ID.iam.gserviceaccount.com" \
  --role="roles/artifactregistry.writer"

gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:github-actions@$PROJECT_ID.iam.gserviceaccount.com" \
  --role="roles/iam.serviceAccountUser"

# Generar JSON key
gcloud iam service-accounts keys create github-actions-key.json \
  --iam-account=github-actions@$PROJECT_ID.iam.gserviceaccount.com

# El contenido de github-actions-key.json va en GCP_SA_KEY de GitHub Secrets
```

---

## 🗄️ Configuración de Cloud SQL

### Paso 1: Crear Instancia

```bash
# Crear instancia PostgreSQL 16
gcloud sql instances create carrillo-db \
  --database-version=POSTGRES_16 \
  --cpu=1 \
  --memory=614MB \
  --region=us-central1 \
  --root-password=YOUR_ROOT_PASSWORD \
  --storage-type=SSD \
  --storage-size=10GB

# Crear base de datos
gcloud sql databases create carrillo_legal_tech --instance=carrillo-db

# Crear usuario
gcloud sql users create carrillo \
  --instance=carrillo-db \
  --password=YOUR_USER_PASSWORD
```

### Paso 2: Crear Schemas

```bash
# Conectar via Cloud SQL Proxy o IP pública
gcloud sql connect carrillo-db --user=carrillo --database=carrillo_legal_tech

# Ejecutar SQL para crear schemas
CREATE SCHEMA IF NOT EXISTS clients;
CREATE SCHEMA IF NOT EXISTS cases;
CREATE SCHEMA IF NOT EXISTS documents;
CREATE SCHEMA IF NOT EXISTS payments;
CREATE SCHEMA IF NOT EXISTS calendar;
CREATE SCHEMA IF NOT EXISTS notifications;
CREATE SCHEMA IF NOT EXISTS users;
```

### Paso 3: Configurar Conectividad

Para Cloud Run, usar **Cloud SQL Auth Proxy** (recomendado):

```bash
# Agregar conector en Cloud Run
gcloud run services update carrillo-client-service \
  --add-cloudsql-instances=PROJECT_ID:us-central1:carrillo-db \
  --region=us-central1
```

O usar **IP pública** con redes autorizadas (menos seguro pero más simple para desarrollo):

```bash
# Obtener IP de salida de Cloud Run
# Agregar a redes autorizadas de Cloud SQL
gcloud sql instances patch carrillo-db \
  --authorized-networks=0.0.0.0/0  # Temporalmente, después restringir
```

---

## 📦 Configuración de Artifact Registry

```bash
# Crear repositorio de Docker
gcloud artifacts repositories create carrillo-repo \
  --repository-format=docker \
  --location=us-central1 \
  --description="Carrillo Abogados Docker images"

# Configurar Docker para usar Artifact Registry
gcloud auth configure-docker us-central1-docker.pkg.dev
```

---

## 🔧 Deploy Manual

### Frontend

```bash
cd frontend

# Build imagen
docker build \
  --build-arg NEXT_PUBLIC_API_URL=https://api.carrilloabgd.com \
  --build-arg NEXT_PUBLIC_SITE_URL=https://app.carrilloabgd.com \
  -t us-central1-docker.pkg.dev/$PROJECT_ID/carrillo-repo/carrillo-frontend:latest \
  .

# Push imagen
docker push us-central1-docker.pkg.dev/$PROJECT_ID/carrillo-repo/carrillo-frontend:latest

# Deploy a Cloud Run
gcloud run deploy carrillo-frontend \
  --image us-central1-docker.pkg.dev/$PROJECT_ID/carrillo-repo/carrillo-frontend:latest \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --memory 512Mi \
  --cpu 1 \
  --port 3000
```

### Backend (ejemplo client-service)

```bash
cd client-service

# Build JAR
../mvnw package -DskipTests

# Build imagen
docker build -t us-central1-docker.pkg.dev/$PROJECT_ID/carrillo-repo/client-service:latest .

# Push imagen
docker push us-central1-docker.pkg.dev/$PROJECT_ID/carrillo-repo/client-service:latest

# Deploy a Cloud Run
gcloud run deploy carrillo-client-service \
  --image us-central1-docker.pkg.dev/$PROJECT_ID/carrillo-repo/client-service:latest \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --memory 512Mi \
  --cpu 1 \
  --port 8200 \
  --set-env-vars "SPRING_PROFILES_ACTIVE=gcp" \
  --set-env-vars "POSTGRES_HOST=/cloudsql/$PROJECT_ID:us-central1:carrillo-db" \
  --set-env-vars "POSTGRES_DB=carrillo_legal_tech" \
  --set-env-vars "POSTGRES_USER=carrillo" \
  --set-env-vars "POSTGRES_PASSWORD=YOUR_PASSWORD" \
  --add-cloudsql-instances=$PROJECT_ID:us-central1:carrillo-db
```

---

## 🤖 Deploy Automático (CI/CD)

El workflow `.github/workflows/deploy-gcp.yml` automatiza el deploy:

### Trigger Manual

1. Ir a Actions → Deploy to GCP Cloud Run
2. Click "Run workflow"
3. Seleccionar environment: `staging` o `production`
4. Seleccionar servicios a desplegar

### Trigger Automático

Push a `main` despliega automáticamente a staging.

### Verificar Deploy

```bash
# Listar servicios desplegados
gcloud run services list --region=us-central1

# Ver URL de un servicio
gcloud run services describe carrillo-frontend --region=us-central1 --format='value(status.url)'

# Ver logs
gcloud run logs read --service=carrillo-frontend --region=us-central1 --limit=100
```

---

## 🌐 Configuración de Dominios

### Paso 1: Mapear Dominio Personalizado

```bash
# Verificar dominio (si no está verificado)
gcloud domains verify carrilloabgd.com

# Mapear dominio a servicio
gcloud beta run domain-mappings create \
  --service=carrillo-frontend \
  --domain=app.carrilloabgd.com \
  --region=us-central1

gcloud beta run domain-mappings create \
  --service=carrillo-api-gateway \
  --domain=api.carrilloabgd.com \
  --region=us-central1
```

### Paso 2: Configurar DNS en HostGator

En cPanel de HostGator, agregar registros DNS:

| Tipo | Nombre | Valor | TTL |
|------|--------|-------|-----|
| CNAME | app | ghs.googlehosted.com | 3600 |
| CNAME | api | ghs.googlehosted.com | 3600 |

### Paso 3: Verificar SSL

Cloud Run provee SSL automático con certificados gestionados:

```bash
# Verificar estado del certificado
gcloud beta run domain-mappings describe \
  --domain=app.carrilloabgd.com \
  --region=us-central1
```

---

## 📊 Monitoreo y Logs

### Cloud Monitoring

```bash
# Ver métricas
gcloud monitoring dashboards list

# Crear alerta de latencia
gcloud alpha monitoring policies create \
  --display-name="High Latency Alert" \
  --notification-channels=YOUR_CHANNEL_ID \
  --condition-display-name="P99 Latency > 2s" \
  --condition-filter='resource.type="cloud_run_revision" AND metric.type="run.googleapis.com/request_latencies"'
```

### Cloud Logging

```bash
# Ver logs de todos los servicios
gcloud logging read 'resource.type="cloud_run_revision"' --limit=50

# Ver logs de servicio específico
gcloud logging read 'resource.type="cloud_run_revision" AND resource.labels.service_name="carrillo-frontend"' --limit=50

# Exportar logs a BigQuery (opcional)
gcloud logging sinks create carrillo-logs \
  bigquery.googleapis.com/projects/$PROJECT_ID/datasets/carrillo_logs \
  --log-filter='resource.type="cloud_run_revision"'
```

---

## 🔧 Troubleshooting

### Error: "Container failed to start"

```bash
# Ver logs de inicio
gcloud run logs read --service=SERVICE_NAME --region=us-central1 --limit=100

# Verificar puerto expuesto
# Asegurar que PORT env var está configurada o usar puerto hardcoded
```

### Error: "Cloud SQL connection refused"

```bash
# Verificar Cloud SQL Proxy está habilitado
gcloud run services describe SERVICE_NAME --region=us-central1 \
  --format='value(spec.template.metadata.annotations.run.googleapis.com/cloudsql-instances)'

# Verificar usuario y permisos en Cloud SQL
gcloud sql users list --instance=carrillo-db
```

### Error: "503 Service Unavailable"

```bash
# Verificar que hay instancias corriendo
gcloud run services describe SERVICE_NAME --region=us-central1 \
  --format='value(status.conditions)'

# Verificar health checks
curl -v https://SERVICE_URL/actuator/health
```

### Error: "Memory limit exceeded"

```bash
# Aumentar memoria
gcloud run services update SERVICE_NAME \
  --memory=1Gi \
  --region=us-central1
```

---

## 📋 Checklist de Producción

### Pre-Deploy

- [ ] Secrets configurados en GitHub
- [ ] Cloud SQL creado y schemas existentes
- [ ] Artifact Registry creado
- [ ] Service Account con permisos correctos
- [ ] Dominio verificado en GCP

### Post-Deploy

- [ ] Todos los servicios corriendo (`gcloud run services list`)
- [ ] Health checks pasando (`/actuator/health`)
- [ ] Dominios personalizados mapeados
- [ ] SSL funcionando (HTTPS)
- [ ] Logs fluyendo a Cloud Logging
- [ ] Alertas configuradas
- [ ] Backup de Cloud SQL programado

### Validación

```bash
# Test endpoints
curl -s https://app.carrilloabgd.com | head -20
curl -s https://api.carrilloabgd.com/client-service/actuator/health
curl -s https://api.carrilloabgd.com/case-service/actuator/health
```

---

## 💰 Optimización de Costos

### Tips para Reducir Costos

1. **Min instances = 0**: Permite scale-to-zero cuando no hay tráfico
2. **Cloud SQL f1-micro**: Suficiente para inicio, escalar después
3. **Committed use discounts**: Para producción estable
4. **Cleanup de imágenes viejas**: Limpiar Artifact Registry periódicamente

```bash
# Limpiar imágenes antiguas
gcloud artifacts docker images delete \
  us-central1-docker.pkg.dev/$PROJECT_ID/carrillo-repo/carrillo-frontend:old-tag \
  --delete-tags
```

---

*Documento de producción para Carrillo Abogados Legal Tech Platform*
