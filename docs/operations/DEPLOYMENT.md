# ☁️ Guía de Deployment - HostGator cPanel + GCP

**Última Actualización**: 16 de Febrero, 2026
**Estado**: 📋 Estrategia Definida
**Fase Proyecto**: FASE 14 - Infraestructura Depurada
**Imágenes Docker**: 8 servicios construidos con Google Jib, publicados en ghcr.io

---

## 📋 Resumen de Arquitectura de Deployment

Debido a las limitaciones del plan de hosting en HostGator (sin Node.js Selector, sin SSH), adoptamos una arquitectura **híbrida**:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        ARQUITECTURA DE DEPLOYMENT                           │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│    HOSTGATOR (cPanel)                    GCP (Cloud Run / GKE)             │
│    ┌─────────────────────┐              ┌─────────────────────────┐        │
│    │                     │              │                         │        │
│    │   FRONTEND          │   API Calls  │   BACKEND               │        │
│    │   (Static HTML/JS)  │ ────────────►│   (Spring Boot APIs)    │        │
│    │                     │              │                         │        │
│    │   • Next.js Export  │              │   • api-gateway         │        │
│    │   • /public_html    │              │   • client-service      │        │
│    │                     │              │   • case-service        │        │
│    │   carrilloabgd.com  │              │   • ... otros servicios │        │
│    │                     │              │                         │        │
│    └─────────────────────┘              │   api.carrilloabgd.com  │        │
│                                         └─────────────────────────┘        │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 🌐 Frontend en HostGator (cPanel)

### Limitaciones Identificadas

| Característica | Estado |
|---------------|--------|
| Node.js Selector | ❌ No disponible |
| SSH Access | ❌ No disponible |
| Archivos estáticos | ✅ Soportado |
| PHP | ✅ Disponible |
| FTP/SFTP | ✅ Disponible |
| Git Version Control | ⚠️ Solo pull, sin build |

### Estrategia: Static Export

Next.js puede generar un export estático (HTML/CSS/JS) que funciona en cualquier servidor web.

#### Configuración de Next.js

```javascript
// next.config.js
const nextConfig = {
  output: 'export',  // Genera archivos estáticos
  trailingSlash: true,  // URLs terminan en /
  images: {
    unoptimized: true,  // Imágenes sin optimización server-side
  },
  // API URL del backend en GCP
  env: {
    NEXT_PUBLIC_API_URL: 'https://api.carrilloabgd.com',
  },
};
```

#### Comando de Build

```bash
cd frontend
npm run build
# Genera carpeta 'out/' con archivos estáticos
```

### Proceso de Deployment

#### Opción A: FTP Manual

1. Ejecutar `npm run build` localmente
2. Conectar via FTP (FileZilla recomendado)
   - Host: ftp.carrilloabgd.com
   - Usuario: Tu usuario cPanel
   - Puerto: 21
3. Subir contenido de `out/` a `public_html/`

#### Opción B: Script Automatizado

```bash
# scripts/deploy-frontend.sh
#!/bin/bash
set -e

echo "🔨 Building frontend..."
cd frontend
npm run build

echo "📦 Deploying to HostGator..."
# Requiere lftp instalado
lftp -c "
  open -u $FTP_USER,$FTP_PASS ftp.carrilloabgd.com
  mirror -R --delete --verbose out/ public_html/
  bye
"

echo "✅ Deployment complete!"
```

### Configuración de .htaccess

Para que las rutas de Next.js funcionen correctamente:

```apache
# public_html/.htaccess
RewriteEngine On
RewriteBase /

# Handle client-side routing
RewriteCond %{REQUEST_FILENAME} !-f
RewriteCond %{REQUEST_FILENAME} !-d
RewriteRule ^(.*)$ /$1.html [L]

# Fallback to index.html for SPA behavior
RewriteCond %{REQUEST_FILENAME} !-f
RewriteCond %{REQUEST_FILENAME} !-d
RewriteRule . /index.html [L]

# Security headers
Header set X-Content-Type-Options "nosniff"
Header set X-Frame-Options "DENY"
Header set X-XSS-Protection "1; mode=block"
Header set Referrer-Policy "strict-origin-when-cross-origin"

# Cache static assets
<FilesMatch "\.(js|css|jpg|jpeg|png|gif|ico|svg|woff|woff2)$">
  Header set Cache-Control "max-age=31536000, public"
</FilesMatch>

# No cache HTML
<FilesMatch "\.html$">
  Header set Cache-Control "no-cache, no-store, must-revalidate"
</FilesMatch>
```

---

## ☁️ Backend en GCP

### Opciones de Deployment

| Servicio GCP | Costo Estimado | Complejidad | Recomendado |
|--------------|----------------|-------------|-------------|
| **Cloud Run** | ~$20-40/mes | Baja | ✅ MVP |
| **GKE Autopilot** | ~$70-100/mes | Media | Producción |
| **Compute Engine** | ~$25-50/mes | Alta | No recomendado |

### Cloud Run (Recomendado para MVP)

Cloud Run es serverless, paga solo por uso, y escala automáticamente a 0.

#### Dockerfile optimizado para Cloud Run

```dockerfile
# Dockerfile.cloudrun
FROM eclipse-temurin:21-jre-alpine

ENV SPRING_PROFILES_ACTIVE=prod
ENV PORT=8080

WORKDIR /app
COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]
```

#### Deployment a Cloud Run

```bash
# Autenticar con GCP
gcloud auth login
gcloud config set project carrillo-abogados

# Build y push imagen
gcloud builds submit --tag gcr.io/carrillo-abogados/api-gateway

# Deploy a Cloud Run
gcloud run deploy api-gateway \
  --image gcr.io/carrillo-abogados/api-gateway \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --memory 512Mi \
  --cpu 1 \
  --min-instances 0 \
  --max-instances 3
```

### Configuración de CORS

Para que el frontend en HostGator pueda llamar al backend en GCP:

```java
// ApiGatewayApplication.java
@Bean
public CorsWebFilter corsWebFilter() {
    CorsConfiguration config = new CorsConfiguration();
    config.addAllowedOrigin("https://carrilloabgd.com");
    config.addAllowedOrigin("https://www.carrilloabgd.com");
    config.addAllowedMethod("*");
    config.addAllowedHeader("*");
    config.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    
    return new CorsWebFilter(source);
}
```

### Variables de Entorno Producción

```yaml
# Cloud Run environment variables
DATABASE_URL: postgresql://user:pass@/carrillo_legal_tech?host=/cloudsql/PROJECT:REGION:INSTANCE
NATS_URL: nats://nats.carrilloabgd.com:4222
GOOGLE_APPLICATION_CREDENTIALS: /secrets/gcp-credentials.json
JWT_SECRET: ${JWT_SECRET}
```

---

## 🔧 GitHub Actions para Deployment

### Workflow de Deployment Automático

```yaml
# .github/workflows/deploy-production.yml
name: 🚀 Deploy to Production

on:
  push:
    branches: [main]
  workflow_dispatch:

jobs:
  deploy-frontend:
    name: 📦 Deploy Frontend to HostGator
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v4
    
    - uses: actions/setup-node@v4
      with:
        node-version: '20'
        cache: 'npm'
        cache-dependency-path: frontend/package-lock.json
    
    - name: Install dependencies
      run: cd frontend && npm ci
    
    - name: Build static export
      run: cd frontend && npm run build
      env:
        NEXT_PUBLIC_API_URL: https://api.carrilloabgd.com
    
    - name: Deploy via FTP
      uses: SamKirkland/FTP-Deploy-Action@v4.3.5
      with:
        server: ftp.carrilloabgd.com
        username: ${{ secrets.FTP_USERNAME }}
        password: ${{ secrets.FTP_PASSWORD }}
        local-dir: ./frontend/out/
        server-dir: /public_html/

  deploy-backend:
    name: ☁️ Deploy Backend to Cloud Run
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v4
    
    - uses: google-github-actions/auth@v2
      with:
        credentials_json: ${{ secrets.GCP_SA_KEY }}
    
    - uses: google-github-actions/setup-gcloud@v2
    
    - name: Build and Deploy to Cloud Run
      run: |
        gcloud builds submit --config cloudbuild.yaml
```

---

## 📊 Estimación de Costos GCP

Con los $300 de crédito disponibles:

| Servicio | Uso Estimado | Costo/Mes |
|----------|--------------|-----------|
| Cloud Run (8 servicios) | ~100k requests/mes | ~$30 |
| Cloud SQL (PostgreSQL) | db-f1-micro | ~$10 |
| Cloud Storage | 10GB | ~$0.26 |
| Cloud Build | 120 min/mes | Gratis |
| Cloud DNS | 1 zona | ~$0.20 |
| **Total Estimado** | | **~$40-50/mes** |

**Duración del crédito**: ~6-7 meses de operación

---

## 📋 Checklist de Deployment

### Pre-Deployment

- [ ] Configurar dominio api.carrilloabgd.com apuntando a Cloud Run
- [ ] Configurar SSL/TLS en Cloud Run
- [ ] Crear secrets en GitHub Actions
- [ ] Configurar Cloud SQL con PostgreSQL
- [ ] Migrar base de datos con Flyway

### Frontend (HostGator)

- [ ] Modificar next.config.js para static export
- [ ] Configurar NEXT_PUBLIC_API_URL
- [ ] Crear .htaccess para SPA routing
- [ ] Subir primera versión vía FTP
- [ ] Verificar todas las rutas funcionen

### Backend (GCP)

- [ ] Crear proyecto en GCP
- [ ] Habilitar APIs necesarias (Cloud Run, Cloud Build, Cloud SQL)
- [ ] Configurar Service Account
- [ ] Deploy cada microservicio
- [ ] Configurar custom domain

### Post-Deployment

- [ ] Verificar conectividad frontend → backend
- [ ] Probar formulario de contacto
- [ ] Verificar CORS funciona correctamente
- [ ] Configurar monitoring en GCP
- [ ] Configurar alertas

---

## 🔗 Enlaces Útiles

- [GCP Console](https://console.cloud.google.com/)
- [Cloud Run Documentation](https://cloud.google.com/run/docs)
- [HostGator cPanel](https://carrilloabgd.com:2083)
- [Next.js Static Export](https://nextjs.org/docs/app/building-your-application/deploying/static-exports)

---

*Documentación de Deployment - Carrillo Abogados Legal Tech Platform*
