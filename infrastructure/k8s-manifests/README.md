# Kubernetes Manifests - Carrillo Abogados

Manifiestos base de Kubernetes para la plataforma legal tech.

## Estructura

```
k8s-manifests/
├── namespaces/          # Namespaces con ResourceQuotas
├── configmaps/          # Configuraciones de aplicación
├── secrets/             # Templates de secretos (NO commitear valores reales)
├── rbac/               # Roles y permisos
└── network-policies/    # Políticas de red
```

## Uso

### Crear namespaces:
```bash
kubectl apply -f namespaces/
```

### Crear ConfigMaps:
```bash
kubectl apply -f configmaps/
```

### Crear Secrets:
```bash
# 1. Copiar templates
cp secrets/postgresql-secret.yaml.template secrets/postgresql-secret.yaml
cp secrets/oauth2-secret.yaml.template secrets/oauth2-secret.yaml

# 2. Editar y reemplazar valores
nano secrets/postgresql-secret.yaml

# 3. Aplicar (NUNCA commitear estos archivos)
kubectl apply -f secrets/
```

## Verificación

```bash
# Ver namespaces
kubectl get namespaces -l project=carrillo-legal-tech

# Ver ConfigMaps en dev
kubectl get configmaps -n carrillo-dev

# Ver Secrets en dev (sin mostrar valores)
kubectl get secrets -n carrillo-dev
```

## Seguridad

⚠️ **NUNCA commitear archivos con valores reales de secrets**

- Usar siempre `.template` para ejemplos
- Secrets reales solo en cluster, nunca en Git