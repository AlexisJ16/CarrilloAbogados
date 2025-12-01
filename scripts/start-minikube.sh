#!/bin/bash
set -e

# Cargar versión de K8s desde versions.yaml
K8S_VERSION=$(yq eval '.kubernetes.version' ../infrastructure/versions.yaml)

echo "Iniciando Minikube con Kubernetes v$K8S_VERSION..."

# Detener Minikube si está corriendo
minikube delete 2>/dev/null || true

# Iniciar con versión específica
minikube start \
  --kubernetes-version=v${K8S_VERSION} \
  --driver=docker \
  --cpus=4 \
  --memory=7168 \
  --disk-size=30g \
  --addons=ingress,metrics-server,dashboard,storage-provisioner

echo "✓ Minikube iniciado con Kubernetes v$K8S_VERSION"

# Verificar versiones
echo ""
echo "Versiones del cluster:"
kubectl version --short