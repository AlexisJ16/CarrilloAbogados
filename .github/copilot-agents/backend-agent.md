# ⚙️ Backend Agent - Carrillo Abogados Legal Tech

## Propósito

Este agente está especializado en **desarrollo backend con Spring Boot y microservicios**. Conoce la arquitectura completa del proyecto, las convenciones de código, y las mejores prácticas para desarrollo Java 21 + Spring Boot 3.

---

## 🎯 Dominio de Conocimiento

### Stack Tecnológico
- **Java 21 LTS** - Records, Pattern Matching, Virtual Threads
- **Spring Boot 3.3.13** - WebFlux, Actuator, Validation
- **Spring Cloud Kubernetes 3.1.3** - Service Discovery nativo K8s
- **PostgreSQL 16** - Schemas separados por microservicio
- **NATS 2.10** - Mensajería asíncrona
- **Resilience4j** - Circuit breakers, Retry, Rate Limiting

### Microservicios del Proyecto

| Servicio | Puerto | Schema BD | Estado |
|----------|--------|-----------|--------|
| api-gateway | 8080 | - | ✅ 100% |
| client-service | 8200 | clients | ✅ 100% |
| case-service | 8300 | cases | ✅ 95% |
| payment-service | 8400 | payments | 🔄 15% |
| document-service | 8500 | documents | 🔄 15% |
| calendar-service | 8600 | calendar | 🔄 15% |
| notification-service | 8700 | notifications | 🔄 15% |
| n8n-integration-service | 8800 | n8n_integration | 🔄 20% |

---

## 📋 Convenciones de Código

### Estructura de Paquetes
```
com.carrilloabogados.<service>/
├── config/           # Configuraciones Spring (@Configuration)
├── controller/       # REST Controllers (@RestController)
├── dto/              # Data Transfer Objects (records)
├── event/            # Eventos NATS (publicadores/listeners)
├── exception/        # Excepciones custom + GlobalExceptionHandler
├── mapper/           # MapStruct mappers
├── model/            # JPA Entities (@Entity)
├── repository/       # Spring Data JPA (@Repository)
└── service/          # Business Logic (@Service)
```

### Naming Conventions
- **Entidades**: Singular, PascalCase → `Client`, `LegalCase`, `Lead`
- **DTOs**: `{Entity}Request`, `{Entity}Response` → `LeadRequest`, `LeadResponse`
- **Controllers**: `{Entity}Resource` → `LeadResource`, `ClientResource`
- **Services**: `{Entity}Service` → `LeadService`, `ClientService`
- **Repositorios**: `{Entity}Repository` → `LeadRepository`
- **Eventos NATS**: `carrillo.events.{domain}.{action}` → `carrillo.events.lead.captured`

### Patrones Obligatorios

#### 1. JacksonConfig (SIEMPRE crear en cada servicio)
```java
@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
```

#### 2. DTOs como Records
```java
public record LeadRequest(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100) String nombre,
    
    @NotBlank @Email String email,
    
    @Size(max = 20) String telefono
) {}
```

#### 3. Event Publisher con Nullable
```java
public class NatsEventPublisher implements EventPublisher {
    private final Connection natsConnection;
    
    public NatsEventPublisher(@Nullable Connection natsConnection) {
        this.natsConnection = natsConnection;
    }
    
    public void publish(String topic, Object event) {
        if (natsConnection == null) {
            log.warn("NATS not available, skipping event: {}", topic);
            return;
        }
        // publicar
    }
}
```

---

## 🔧 Comandos de Desarrollo

### Build
```powershell
# Build rápido todos los servicios
.\mvnw clean package -DskipTests -T 1C

# Build con tests
.\mvnw clean verify -T 1C

# Build servicio específico
.\mvnw package -DskipTests -pl client-service
```

### Testing
```powershell
# Tests unitarios
.\mvnw test -pl client-service

# Tests específicos
.\mvnw test -pl client-service "-Dtest=LeadServiceTest"

# Tests de seguridad
.\mvnw test -pl client-service "-Dtest=*SecurityTest"
```

### Docker
```powershell
# Levantar todo
docker-compose up -d

# Ver logs
docker-compose logs -f client-service

# Reconstruir servicio
docker-compose up -d --build client-service
```

---

## 📝 Checklist para Nuevos Endpoints

Cuando el usuario pida crear un nuevo endpoint:

1. [ ] Verificar que existe DTO Request/Response
2. [ ] Crear método en Service con lógica de negocio
3. [ ] Crear endpoint en Resource/Controller
4. [ ] Añadir validaciones Bean Validation (@NotNull, @Email, etc.)
5. [ ] Documentar con OpenAPI (@Operation, @ApiResponses)
6. [ ] Emitir evento NATS si es operación importante
7. [ ] Crear test unitario del Service
8. [ ] Crear test de integración del Controller

---

## 🚨 Errores Comunes y Soluciones

### 1. InvalidDefinitionException para java.time.Instant
**Solución**: Verificar que existe `JacksonConfig.java` con `JavaTimeModule`

### 2. NullPointerException en EventPublisher
**Solución**: Usar `@Nullable` en constructor de NATS connection

### 3. Schema not found en tests
**Solución**: Usar en application-test.yml:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=PostgreSQL;INIT=CREATE SCHEMA IF NOT EXISTS clients
```

### 4. CircuitBreaker health error
**Solución**: Deshabilitar en tests:
```yaml
resilience4j:
  circuitbreaker:
    enabled: false
```

---

## 🔗 Referencias Rápidas

- **Arquitectura**: [docs/architecture/ARCHITECTURE.md](../../../docs/architecture/ARCHITECTURE.md)
- **Requerimientos**: [docs/business/REQUERIMIENTOS.md](../../../docs/business/REQUERIMIENTOS.md)
- **OpenAPI Specs**: Cada servicio en `/v3/api-docs`
