# 🔍 Guía de Observabilidad - Grafana LGTM Stack

**Última Actualización**: 5 de Enero, 2026  
**Estado**: ✅ Configurado y Operativo  
**Fase Proyecto**: FASE 8 - CI/CD Completamente Funcional

---

## 📋 Componentes del Stack

| Componente | Puerto | Propósito |
|------------|--------|-----------|
| **Grafana** | 3100 | Visualización y dashboards |
| **Prometheus** | 9090 | Recolección de métricas |
| **Loki** | 3101 | Agregación de logs |
| **Tempo** | 3102 | Distributed tracing |
| **Mimir** | 3103 | Almacenamiento métricas largo plazo |
| **Promtail** | - | Recolector de logs para Loki |
| **Alertmanager** | 9093 | Gestión de alertas |

---

## 🚀 Inicio Rápido

### Levantar el Stack de Observabilidad

```bash
# Desde la raíz del proyecto
cd monitoring
docker-compose -f docker-compose.observability.yml up -d
```

### Verificar que todos los servicios están corriendo

```bash
docker-compose -f docker-compose.observability.yml ps
```

### Acceder a las interfaces

| Servicio | URL | Credenciales |
|----------|-----|--------------|
| **Grafana** | http://localhost:3100 | admin / carrillo2025 |
| **Prometheus** | http://localhost:9090 | - |
| **Alertmanager** | http://localhost:9093 | - |

---

## 📊 Dashboards Disponibles

### 1. Services Overview
- **Ubicación**: Carpeta "Carrillo Abogados" en Grafana
- **Contenido**:
  - Estado UP/DOWN de todos los servicios
  - Request Rate por servicio
  - Response Time P95
  - Logs recientes

### 2. JVM Metrics (Spring Boot)
- Uso de memoria heap
- Garbage Collection
- Threads activos
- Class loading

### 3. Database Metrics
- Conexiones activas a PostgreSQL
- Query latency
- Pool utilization

---

## 📝 Configuración de Microservicios

Para que los microservicios envíen métricas a Prometheus, asegúrate de que tengan:

### Dependencias Maven

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

### application.yml

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus,metrics
  endpoint:
    prometheus:
      enabled: true
  metrics:
    tags:
      application: ${spring.application.name}
```

---

## 🔗 Correlación de Datos

El stack LGTM permite correlacionar:

1. **Métricas → Logs**: Desde una métrica anómala, saltar a los logs del servicio
2. **Logs → Traces**: Desde un log con error, ver el trace completo
3. **Traces → Métricas**: Desde un trace lento, ver métricas del momento

### Configuración de Tracing (OpenTelemetry)

```yaml
# application.yml
management:
  tracing:
    sampling:
      probability: 1.0
  otlp:
    tracing:
      endpoint: http://tempo:4318/v1/traces
```

---

## 🔔 Alertas Configuradas

### Alertas Críticas
- Servicio DOWN por más de 1 minuto
- Error rate > 5% en últimos 5 minutos
- Response time P95 > 2 segundos

### Alertas de Warning
- CPU > 80% por 5 minutos
- Memory > 85% por 5 minutos
- Disk > 90%

### Canales de Notificación
- Email a ingenieria@carrilloabgd.com
- (Futuro) Slack, Discord, PagerDuty

---

## 🛠️ Comandos Útiles

### Ver logs de un servicio específico en Loki

```promql
{service="client-service"} |= "ERROR"
```

### Query de métricas en Prometheus

```promql
# Request rate por servicio
sum(rate(http_server_requests_seconds_count[5m])) by (service)

# Error rate
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m])) by (service)
/ sum(rate(http_server_requests_seconds_count[5m])) by (service) * 100
```

### Detener el stack

```bash
docker-compose -f docker-compose.observability.yml down
```

### Limpiar volúmenes (reset completo)

```bash
docker-compose -f docker-compose.observability.yml down -v
```

---

## 📁 Estructura de Archivos

```
monitoring/
├── docker-compose.observability.yml  # Compose principal
├── grafana/
│   ├── provisioning/
│   │   ├── datasources/
│   │   │   └── datasources.yaml      # Auto-config datasources
│   │   └── dashboards/
│   │       └── dashboards.yaml       # Auto-load dashboards
│   └── dashboards/
│       └── services-overview.json    # Dashboard principal
├── prometheus/
│   └── prometheus.yml                # Config Prometheus
├── loki/
│   └── config.yaml                   # Config Loki
├── tempo/
│   └── config.yaml                   # Config Tempo
├── mimir/
│   └── config.yaml                   # Config Mimir
├── promtail/
│   └── config.yaml                   # Config Promtail
└── alertmanager/
    └── alertmanager.yml              # Config alertas
```

---

## 🔧 Troubleshooting

### Grafana no muestra datos

1. Verificar que Prometheus está corriendo: `curl http://localhost:9090/-/healthy`
2. Verificar que los microservicios exponen métricas: `curl http://localhost:8200/client-service/actuator/prometheus`
3. Revisar logs de Grafana: `docker logs carrillo-grafana`

### Loki no recibe logs

1. Verificar que Promtail está corriendo: `docker logs carrillo-promtail`
2. Verificar la conexión Docker socket
3. Revisar que los contenedores tienen labels correctas

### Tempo no muestra traces

1. Verificar que los microservicios están configurados con OpenTelemetry
2. Verificar conectividad al endpoint OTLP: `curl http://localhost:4318/v1/traces`

---

*Documentación del Stack de Observabilidad - Carrillo Abogados Legal Tech Platform*
