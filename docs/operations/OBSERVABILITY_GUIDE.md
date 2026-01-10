# 🔍 Guía del Stack de Observabilidad - Carrillo Abogados

**Versión**: 1.0  
**Última Actualización**: Enero 2026  
**Stack**: Grafana LGTM (Loki + Grafana + Tempo + Mimir)

---

## 📋 Resumen del Stack

El stack de observabilidad de Carrillo Abogados está basado en **Grafana LGTM**, una solución moderna y completamente gratuita para monitoreo de aplicaciones cloud-native.

| Componente | Puerto | Propósito | URL Local |
|------------|--------|-----------|-----------|
| **Grafana** | 3100 | Visualización y dashboards | http://localhost:3100 |
| **Prometheus** | 9090 | Recolección de métricas | http://localhost:9090 |
| **Loki** | 3101 | Agregación de logs | http://localhost:3101 |
| **Tempo** | 3102 | Distributed tracing | http://localhost:3102 |
| **Mimir** | 3103 | Almacenamiento largo plazo | http://localhost:3103 |
| **Alertmanager** | 9093 | Gestión de alertas | http://localhost:9093 |

---

## 🚀 Inicio Rápido

### 1. Iniciar el Stack de Observabilidad

```powershell
# Desde la carpeta raíz del proyecto
cd monitoring
docker-compose -f docker-compose.observability.yml up -d
```

### 2. Verificar que todos los servicios estén corriendo

```powershell
docker-compose -f docker-compose.observability.yml ps
```

### 3. Acceder a Grafana

- **URL**: http://localhost:3100
- **Usuario**: `admin`
- **Contraseña**: `carrillo2025`

---

## 📊 Usando Grafana

### Credenciales de Acceso

| Campo | Valor |
|-------|-------|
| **URL** | http://localhost:3100 |
| **Usuario** | admin |
| **Contraseña** | carrillo2025 |

### Datasources Pre-configurados

Al iniciar, Grafana ya tiene configurados los siguientes datasources:

| Datasource | Tipo | Uso |
|------------|------|-----|
| **Prometheus** | Métricas | Consultas de métricas en tiempo real |
| **Mimir** | Métricas | Consultas de métricas históricas (largo plazo) |
| **Loki** | Logs | Agregación y búsqueda de logs |
| **Tempo** | Tracing | Traces distribuidos |
| **Alertmanager** | Alertas | Gestión de alertas activas |

### Consultas Básicas en Grafana

#### Métricas (Prometheus/PromQL)

```promql
# JVM Heap Memory usado por servicio
jvm_memory_used_bytes{area="heap"}

# Requests HTTP por segundo
rate(http_server_requests_seconds_count[5m])

# Latencia P99 de endpoints
histogram_quantile(0.99, rate(http_server_requests_seconds_bucket[5m]))

# CPU usage por contenedor
container_cpu_usage_seconds_total

# Errores HTTP 5xx
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m]))
```

#### Logs (Loki/LogQL)

```logql
# Logs del api-gateway
{container_name="carrillo-api-gateway"}

# Errores en client-service
{container_name="carrillo-client-service"} |= "ERROR"

# Logs con nivel específico
{job="docker"} | json | level = "ERROR"

# Búsqueda de texto
{container_name=~"carrillo-.*"} |= "NullPointerException"
```

---

## 📈 Prometheus - Métricas

### Acceso Directo

- **URL**: http://localhost:9090

### Métricas Disponibles por Microservicio

Cada microservicio expone métricas via Spring Actuator en `/actuator/prometheus`:

| Microservicio | Endpoint de Métricas |
|---------------|---------------------|
| api-gateway | http://localhost:8080/actuator/prometheus |
| client-service | http://localhost:8200/client-service/actuator/prometheus |
| case-service | http://localhost:8300/case-service/actuator/prometheus |
| payment-service | http://localhost:8400/payment-service/actuator/prometheus |
| document-service | http://localhost:8500/document-service/actuator/prometheus |
| calendar-service | http://localhost:8600/calendar-service/actuator/prometheus |
| notification-service | http://localhost:8700/notification-service/actuator/prometheus |
| n8n-integration-service | http://localhost:8800/n8n-integration-service/actuator/prometheus |

### Targets en Prometheus

Visita http://localhost:9090/targets para ver el estado de todos los targets de scraping.

### Queries Útiles

```promql
# Ver todos los microservicios UP
up{job=~".*-service"}

# Memoria heap usada
jvm_memory_used_bytes{area="heap"} / 1024 / 1024

# Threads activos por servicio
jvm_threads_live_threads

# Conexiones activas a PostgreSQL
hikaricp_connections_active

# Tiempo promedio de respuesta HTTP
rate(http_server_requests_seconds_sum[5m]) / rate(http_server_requests_seconds_count[5m])
```

---

## 📝 Loki - Logs

### Acceso

Loki no tiene UI propia. Usa **Grafana** → **Explore** → Selecciona **Loki** como datasource.

### Labels Disponibles

| Label | Descripción | Ejemplo |
|-------|-------------|---------|
| `container_name` | Nombre del contenedor | `carrillo-client-service` |
| `compose_service` | Nombre del servicio en compose | `client-service` |
| `job` | Job de Promtail | `docker` |

### Queries de Ejemplo

```logql
# Todos los logs de microservicios Carrillo
{container_name=~"carrillo-.*"}

# Solo errores
{container_name=~"carrillo-.*"} |= "ERROR"

# Logs de un request específico (traceId)
{container_name=~"carrillo-.*"} |= "abc123traceId"

# Logs con parseo JSON
{container_name="carrillo-client-service"} | json | level = "ERROR"

# Contar errores por servicio
sum by (container_name) (count_over_time({container_name=~"carrillo-.*"} |= "ERROR" [5m]))
```

---

## 🔗 Tempo - Distributed Tracing

### Acceso

Tempo no tiene UI propia. Usa **Grafana** → **Explore** → Selecciona **Tempo** como datasource.

### Buscar Traces

1. Ir a **Explore** en Grafana
2. Seleccionar **Tempo** como datasource
3. Usar el modo **Search** para buscar por:
   - Service Name
   - Span Name
   - Duration
   - Tags

### Correlación con Logs

Tempo está configurado para correlacionar traces con logs en Loki. Al ver un trace, puedes hacer clic en "Logs for this span" para ver los logs asociados.

### Enviar Traces desde Microservicios

Los microservicios deben estar configurados para enviar traces via OTLP:

```yaml
# application.yml
management:
  tracing:
    sampling:
      probability: 1.0  # 100% sampling para desarrollo
  otlp:
    tracing:
      endpoint: http://tempo:4317
```

---

## 🔔 Alertmanager

### Acceso

- **URL**: http://localhost:9093

### Estado de Alertas

Visita http://localhost:9093/#/alerts para ver alertas activas.

### Configuración

Las alertas se definen en:
- `monitoring/prometheus/alerts/` - Reglas de alertas
- `monitoring/alertmanager/alertmanager.yml` - Configuración de notificaciones

### Ejemplo de Regla de Alerta

```yaml
groups:
  - name: microservices
    rules:
      - alert: ServiceDown
        expr: up{job=~".*-service"} == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Servicio {{ $labels.job }} está caído"
          description: "El servicio {{ $labels.job }} no responde hace más de 1 minuto."
```

---

## 🐳 Comandos Útiles

### Stack de Observabilidad

```powershell
# Iniciar stack
cd monitoring
docker-compose -f docker-compose.observability.yml up -d

# Ver logs del stack
docker-compose -f docker-compose.observability.yml logs -f

# Ver logs de un servicio específico
docker-compose -f docker-compose.observability.yml logs -f prometheus

# Detener stack
docker-compose -f docker-compose.observability.yml down

# Reiniciar un servicio
docker-compose -f docker-compose.observability.yml restart grafana

# Ver estado
docker-compose -f docker-compose.observability.yml ps
```

### Verificar Conectividad

```powershell
# Verificar que Prometheus puede alcanzar los microservicios
docker exec carrillo-prometheus wget -qO- http://client-service:8200/client-service/actuator/health

# Verificar que Grafana puede alcanzar Prometheus
docker exec carrillo-grafana wget -qO- http://prometheus:9090/-/healthy
```

---

## 🔧 Solución de Problemas

### Problema: Grafana no muestra datos de Prometheus

**Síntoma**: Los dashboards están vacíos o muestran "No data".

**Solución**:
1. Verificar que Prometheus esté corriendo: http://localhost:9090/-/healthy
2. Verificar targets en http://localhost:9090/targets
3. Verificar que los microservicios estén en la misma red Docker

### Problema: Loki no recibe logs

**Síntoma**: No hay logs en Grafana/Loki.

**Solución**:
1. Verificar que Promtail esté corriendo: `docker logs carrillo-promtail`
2. Verificar que el socket Docker esté montado correctamente
3. Verificar la configuración de Promtail: `./promtail/config.yaml`

### Problema: Prometheus no puede scrape-ar microservicios

**Síntoma**: Targets en estado DOWN en Prometheus.

**Solución**:
1. Verificar que los microservicios estén corriendo
2. Verificar que expongan `/actuator/prometheus`
3. Verificar que estén en la misma red Docker (`carrillo-network`)

### Problema: Network isolation

**Síntoma**: El stack de observabilidad no puede comunicarse con los microservicios.

**Solución**: 
El stack de observabilidad está configurado para conectarse a la red externa `carrilloabogados_carrillo-network`. 

```powershell
# Verificar que la red exista
docker network ls | Select-String "carrillo"

# Si no existe, iniciar primero los microservicios
docker-compose up -d
```

---

## 📐 Arquitectura de Redes

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         DOCKER NETWORKS                                  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │            carrilloabogados_carrillo-network                     │   │
│  │                                                                   │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐           │   │
│  │  │api-gateway│ │client-svc │ │case-svc  │ │  ...     │           │   │
│  │  │  :8080   │ │  :8200   │ │  :8300   │ │          │           │   │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘           │   │
│  │       ▲                                                         │   │
│  │       │ scrape metrics                                          │   │
│  │       │                                                         │   │
│  └───────┼─────────────────────────────────────────────────────────┘   │
│          │                                                              │
│          │                                                              │
│  ┌───────┼─────────────────────────────────────────────────────────┐   │
│  │       │       carrillo-observability                             │   │
│  │       │                                                           │   │
│  │  ┌────▼─────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐            │   │
│  │  │Prometheus │ │ Grafana  │ │  Loki    │ │  Tempo   │            │   │
│  │  │  :9090   │ │  :3100   │ │  :3101   │ │  :3102   │            │   │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘            │   │
│  │                                                                   │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐                         │   │
│  │  │  Mimir   │ │Promtail  │ │Alertmgr  │                         │   │
│  │  │  :3103   │ │          │ │  :9093   │                         │   │
│  │  └──────────┘ └──────────┘ └──────────┘                         │   │
│  │                                                                   │   │
│  └───────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 📚 Referencias

- [Grafana Documentation](https://grafana.com/docs/grafana/latest/)
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Loki Documentation](https://grafana.com/docs/loki/latest/)
- [Tempo Documentation](https://grafana.com/docs/tempo/latest/)
- [PromQL Cheat Sheet](https://promlabs.com/promql-cheat-sheet/)
- [LogQL Cheat Sheet](https://grafana.com/docs/loki/latest/query/)

---

*Documento creado para el proyecto Carrillo Abogados Legal Tech Platform*
