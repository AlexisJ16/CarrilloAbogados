# Helm Charts - Carrillo Abogados Legal Tech

Helm Charts para despliegue de la plataforma legal tech en Kubernetes.

## Estructura

```
helm-charts/
├── carrillo-abogados/      # Umbrella chart (despliega todo)
├── api-gateway/           # API Gateway chart
└── [futuros charts]/      # Otros microservicios
```

## Instalación

### Prerrequisitos

```bash
# Helm 3.x instalado
helm version

# Kubernetes cluster accesible
kubectl cluster-info

# Namespaces creados
kubectl apply -f ../infrastructure/k8s-manifests/namespaces/
```

### Instalar en Dev (Minikube)

```bash
# 1. Crear secrets (usar templates)
cd ../infrastructure/k8s-manifests/secrets
cp postgresql-secret.yaml.template postgresql-secret.yaml
# Editar postgresql-secret.yaml con valores reales
kubectl apply -f postgresql-secret.yaml -n carrillo-dev

cp oauth2-secret.yaml.template oauth2-secret.yaml
# Editar oauth2-secret.yaml con valores reales
kubectl apply -f oauth2-secret.yaml -n carrillo-dev

# 2. Aplicar ConfigMaps
kubectl apply -f ../infrastructure/k8s-manifests/configmaps/ -n carrillo-dev

# 3. Instalar chart completo
cd helm-charts/
helm install carrillo-dev carrillo-abogados/ \
  --namespace carrillo-dev \
  --create-namespace \
  --values carrillo-abogados/values.yaml

# 4. Verificar despliegue
kubectl get pods -n carrillo-dev
helm list -n carrillo-dev
```

### Instalar solo API Gateway

```bash
helm install api-gateway api-gateway/ \
  --namespace carrillo-dev \
  --create-namespace
```

### Actualizar

```bash
# Actualizar valores
helm upgrade carrillo-dev carrillo-abogados/ \
  --namespace carrillo-dev \
  --values carrillo-abogados/values.yaml

# Ver cambios antes de aplicar
helm diff upgrade carrillo-dev carrillo-abogados/ \
  --namespace carrillo-dev
```

### Desinstalar

```bash
helm uninstall carrillo-dev --namespace carrillo-dev
```

## Desarrollo de Charts

### Validar sintaxis

```bash
# Lint individual chart
helm lint api-gateway/

# Lint umbrella chart
helm lint carrillo-abogados/
```

### Probar renderizado

```bash
# Ver qué YAML se generará
helm template carrillo-dev carrillo-abogados/ \
  --namespace carrillo-dev \
  --values carrillo-abogados/values.yaml
```

### Empaquetar

```bash
# Crear .tgz del chart
helm package api-gateway/
helm package carrillo-abogados/
```

## Ambientes

### Dev (Minikube)

- Namespace: `carrillo-dev`
- Replicas: 2
- Resources: Mínimos

### Staging (GKE)

- Namespace: `carrillo-staging`
- Replicas: 2-3
- Resources: Medios

### Production (GKE)

- Namespace: `carrillo-prod`
- Replicas: 3-10 (HPA)
- Resources: Optimizados