# Prompt para Continuar el Desarrollo - Copilot VSCode

**Fecha**: 18 de Diciembre, 2025  
**Propósito**: Usar este prompt en un nuevo chat de GitHub Copilot para continuar el trabajo

---

## 📋 PROMPT PARA COPIAR Y PEGAR

```
## Contexto del Proyecto

Soy Alexis y estoy desarrollando **Carrillo Abogados Legal Tech Platform**, una plataforma cloud-native de gestión legal empresarial con 7 microservicios Spring Boot sobre Kubernetes.

### Propósito Dual
- **Académico**: Proyecto final Plataformas II (entrega 1 diciembre 2025)
- **Empresarial**: Sistema real para bufete de 5 abogados en Cali, Colombia

### Estado Actual (18 Dic 2025)
- ✅ Build SUCCESS en todos los módulos
- ✅ 7 schemas PostgreSQL creados
- ✅ RBAC Kubernetes configurado
- ✅ Health probes con context-path correcto
- ✅ compose.yml y scripts actualizados
- ⏸️ Minikube requiere reinicio (problemas de cgroups en WSL)

---

## ⚠️ CRÍTICO: Entorno Windows + WSL

Mi entorno de desarrollo:
- **SO**: Windows 11
- **WSL**: Ubuntu-24.04 (default)
- **Minikube**: Driver Docker dentro de WSL
- **kubectl/helm**: Instalados en WSL, NO en Windows

### Cómo ejecutar comandos desde PowerShell

**TODOS los comandos de Kubernetes DEBEN ejecutarse a través de WSL:**

```powershell
# ✅ CORRECTO
wsl bash -c "kubectl get pods -n carrillo-dev"
wsl bash -c "minikube status"
wsl bash -c "./scripts/deploy.sh"

# ❌ INCORRECTO
kubectl get pods  # Esto FALLA - Windows kubectl no tiene config de Minikube
```

### Para reiniciar WSL (soluciona problemas de estabilidad)
```powershell
wsl --shutdown
# Esperar 10 segundos, luego:
wsl bash -c "minikube start"
```

---

## 🎯 TAREA PENDIENTE

Después de reiniciar WSL con `wsl --shutdown`, necesito:

1. **Iniciar Minikube**: `wsl bash -c "minikube start"`
2. **Verificar pods**: `wsl bash -c "kubectl get pods -A"`
3. **Crear namespaces si es necesario**: 
   ```
   wsl bash -c "kubectl create namespace carrillo-dev"
   wsl bash -c "kubectl create namespace databases"
   wsl bash -c "kubectl create namespace messaging"
   ```
4. **Desplegar infraestructura**:
   ```
   wsl bash -c "helm install postgresql bitnami/postgresql -n databases --set auth.username=carrillo --set auth.password=carrillo123 --set auth.database=carrillo_legal_tech"
   wsl bash -c "helm install nats nats/nats -n messaging"
   ```
5. **Crear schemas PostgreSQL**:
   ```
   wsl bash -c "kubectl exec -it postgresql-0 -n databases -- psql -U carrillo -d carrillo_legal_tech -c 'CREATE SCHEMA IF NOT EXISTS clients; CREATE SCHEMA IF NOT EXISTS cases; CREATE SCHEMA IF NOT EXISTS documents; CREATE SCHEMA IF NOT EXISTS payments; CREATE SCHEMA IF NOT EXISTS calendar; CREATE SCHEMA IF NOT EXISTS notifications; CREATE SCHEMA IF NOT EXISTS users;'"
   ```
6. **Aplicar RBAC**:
   ```
   wsl bash -c "kubectl apply -f k8s-manifests/rbac/service-discovery-role.yaml -n carrillo-dev"
   ```
7. **Desplegar microservicios**:
   ```
   wsl bash -c "helm upgrade --install carrillo-dev helm-charts/carrillo-abogados/ -n carrillo-dev"
   ```
8. **Verificar despliegue**: `wsl bash -c "kubectl get pods -n carrillo-dev"`

---

## 📁 Archivos de Documentación Importantes

- **CLAUDE.md** - Contexto principal para IAs
- **PROYECTO_ESTADO.md** - Estado actual del proyecto
- **.github/copilot-instructions.md** - Instrucciones específicas para Copilot
- **docs/ai-context/AI_CONTEXT_MASTER.md** - Contexto maestro

---

## 🔧 Servicios del Proyecto

| Servicio | Puerto | Context-Path | Estado |
|----------|--------|--------------|--------|
| api-gateway | 8080 | / | ✅ Activo |
| client-service | 8200 | /client-service | ✅ Activo |
| case-service | 8300 | /case-service | ✅ Activo |
| payment-service | 8400 | / | ✅ Activo |
| document-service | 8500 | / | ⚙️ Skeleton |
| calendar-service | 8600 | / | ⚙️ Skeleton |
| notification-service | 8700 | / | ⚙️ Skeleton |
| n8n-integration-service | 8800 | / | ⚙️ Skeleton |

---

Por favor, ayúdame a:
1. Reiniciar el entorno ejecutando `wsl --shutdown` primero
2. Iniciar Minikube y desplegar toda la infraestructura
3. Verificar que los 7 microservicios estén Running
4. Ejecutar validación final con `./scripts/test.sh`

Recuerda: SIEMPRE usar `wsl bash -c "comando"` para ejecutar comandos de kubectl/minikube/helm.
```

---

## 📝 INSTRUCCIONES DE USO

1. **Cierra este chat** (ya está saturado)
2. **Abre PowerShell como Administrador**
3. **Ejecuta**: `wsl --shutdown`
4. **Espera 10-15 segundos**
5. **Abre un nuevo chat de Copilot en VSCode**
6. **Copia y pega el prompt de arriba**
7. **Continúa el desarrollo**

---

## 🔍 VERIFICACIÓN POST-REINICIO

Después de reiniciar WSL y ejecutar el despliegue, deberías ver:

```
NAME                                                   READY   STATUS    
carrillo-dev-api-gateway-xxx                           1/1     Running   
carrillo-dev-calendar-service-xxx                      1/1     Running   
carrillo-dev-case-service-xxx                          1/1     Running   
carrillo-dev-client-service-xxx                        1/1     Running   
carrillo-dev-document-service-xxx                      1/1     Running   
carrillo-dev-n8n-integration-service-xxx               1/1     Running   
carrillo-dev-notification-service-xxx                  1/1     Running   
```

Y en databases/messaging:
```
postgresql-0   1/1     Running
nats-0         2/2     Running
```
