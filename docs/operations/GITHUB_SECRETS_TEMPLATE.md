# ===========================================
# GitHub Repository Secrets Template
# ===========================================
# Copy this file and fill in the values
# These should be configured in GitHub Settings → Secrets

# ===========================================
# GCP Configuration
# ===========================================

# GCP Project ID
GCP_PROJECT_ID=carrillo-abogados-prod

# GCP Service Account Key (JSON)
# Generate with: gcloud iam service-accounts keys create key.json --iam-account=github-actions@PROJECT_ID.iam.gserviceaccount.com
# Paste the entire JSON content
GCP_SA_KEY={"type": "service_account", "project_id": "...", ...}

# ===========================================
# Cloud SQL Configuration
# ===========================================

# Cloud SQL Host
# For Cloud SQL Auth Proxy: /cloudsql/PROJECT_ID:REGION:INSTANCE_NAME
# For direct IP: 10.x.x.x
CLOUD_SQL_HOST=/cloudsql/carrillo-abogados-prod:us-central1:carrillo-db

# Cloud SQL User
CLOUD_SQL_USER=carrillo

# Cloud SQL Password
CLOUD_SQL_PASSWORD=your-secure-password

# ===========================================
# Security Configuration
# ===========================================

# JWT Secret for authentication
# Generate with: openssl rand -base64 64
JWT_SECRET=CarrilloAbogadosSecretKey2025PlatformasII-Cali-Colombia-SecureProduction!!

# ===========================================
# Security Scanning
# ===========================================

# Snyk Token
# Get from: https://app.snyk.io/account
SNYK_TOKEN=your-snyk-token

# SonarCloud Token
# Get from: https://sonarcloud.io/account/security
SONAR_TOKEN=your-sonar-token

# ===========================================
# Optional: Email Configuration
# ===========================================

# Gmail App Password for notification-service
GMAIL_USERNAME=notifications@carrilloabgd.com
GMAIL_APP_PASSWORD=your-gmail-app-password
