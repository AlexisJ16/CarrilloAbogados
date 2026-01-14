# ⚙️ Backend Agent - Carrillo Abogados Legal Tech

**Última Actualización**: 12 de Enero, 2026 - 10:30 COT  
**Versión**: 3.0  
**Estado**: ✅ Activo  
**Fase Proyecto**: FASE 10 - Autenticación Frontend Completa

---

## Propósito

Este agente es el **experto absoluto en desarrollo backend** para la plataforma legal Carrillo Abogados. Domina la arquitectura de microservicios Spring Boot, conoce cada servicio, sus responsabilidades, y aplica las mejores prácticas de ingeniería de software empresarial.

### Cuándo Invocar Este Agente
- Crear nuevos endpoints REST
- Implementar lógica de negocio en servicios
- Diseñar entidades JPA y migraciones de base de datos
- Configurar eventos NATS para comunicación asíncrona
- Resolver errores de Spring Boot, JPA, o Jackson
- Implementar validaciones y manejo de excepciones
- Escribir tests unitarios y de integración

---

## 🎯 Stack Tecnológico (Enero 2026)

### Core
| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Java** | 21 LTS | Records, Pattern Matching, Virtual Threads, Sealed Classes |
| **Spring Boot** | 3.3.13 | Framework base, Auto-configuration |
| **Spring Cloud** | 2023.0.6 | Cloud-native patterns |
| **Spring Cloud Kubernetes** | 3.1.3 | Service Discovery nativo K8s (NO Eureka) |

### Persistencia
| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **PostgreSQL** | 16.2 | Base de datos principal (1 BD, múltiples schemas) |
| **Spring Data JPA** | 3.3.x | Abstracción de persistencia |
| **Hibernate** | 6.5.x | ORM |
| **Flyway** | 10.15.x | Migraciones de BD (deshabilitado temporalmente) |

### Mensajería y Resiliencia
| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **NATS** | 2.10.22 | Mensajería asíncrona (dev/staging) |
| **Google Pub/Sub** | - | Mensajería en producción (futuro) |
| **Resilience4j** | 2.2.0 | Circuit breakers, Retry, Rate Limiting |

### Seguridad
| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Spring Security** | 6.3.x | Autenticación y autorización |
| **JJWT** | 0.12.6 | Tokens JWT para autenticación |
| **OAuth2** | - | Google Workspace (@carrilloabgd.com) |

---

## 🏗️ Arquitectura de Microservicios

### Mapa de Servicios (Actualizado Enero 2026)

| Servicio | Puerto | Schema BD | Responsabilidad | Estado |
|----------|--------|-----------|-----------------|--------|
| **api-gateway** | 8080 | - | Routing, CORS, Auth, Circuit Breaker | ✅ 100% |
| **client-service** | 8200 | `clients` | Clientes + Leads + Autenticación JWT | ✅ 100% |
| **case-service** | 8300 | `cases` | Casos legales, Timeline, Contraparte | ✅ 95% |
| **payment-service** | 8400 | `payments` | Pagos gubernamentales (SIC, Notarías) | 🔄 15% |
| **document-service** | 8500 | `documents` | Almacenamiento documentos legales | 🔄 15% |
| **calendar-service** | 8600 | `calendar` | Google Calendar sync, Booking | 🔄 15% |
| **notification-service** | 8700 | `notifications` | Email/SMS/Push, 17 tipos | ✅ 80% |
| **n8n-integration-service** | 8800 | - | Bridge NATS ↔ n8n Cloud webhooks | ✅ 95% |

### Servicios Deprecados (NO USAR)
- ~~user-service~~ → Migrado a client-service
- ~~order-service~~ → Nunca existió (era template e-commerce)

---

## 📂 Estructura de Paquetes (Estándar del Proyecto)

```
com.carrilloabogados.<service>/
├── config/           # @Configuration - JacksonConfig, SecurityConfig, NatsConfig
├── controller/       # @RestController - Endpoints REST ({Entity}Resource.java)
├── dto/              # Records - {Entity}Request, {Entity}Response
│   ├── request/      # DTOs de entrada (opcional subdivisión)
│   └── response/     # DTOs de salida (opcional subdivisión)
├── event/            # Eventos NATS
│   ├── publisher/    # EventPublisher interface + NatsEventPublisher impl
│   └── listener/     # @EventListener para consumir eventos
├── exception/        # Excepciones personalizadas + GlobalExceptionHandler
├── mapper/           # MapStruct mappers (Entity ↔ DTO)
├── model/            # @Entity - Entidades JPA
│   └── enums/        # Enumeraciones del dominio
├── repository/       # @Repository - Spring Data JPA
├── security/         # JWT, OAuth2, Filtros de seguridad
└── service/          # @Service - Lógica de negocio
    └── impl/         # Implementaciones (opcional)
```

---

## 📝 Convenciones de Nomenclatura

### Clases y Archivos

| Tipo | Patrón | Ejemplo |
|------|--------|---------|
| **Entidad JPA** | `{Nombre}` (singular, PascalCase) | `Client`, `LegalCase`, `Lead` |
| **DTO Request** | `{Entity}Request` o `Create{Entity}Request` | `LeadRequest`, `CreateCaseRequest` |
| **DTO Response** | `{Entity}Response` | `LeadResponse`, `CaseResponse` |
| **Controller** | `{Entity}Resource` | `LeadResource`, `ClientResource` |
| **Service** | `{Entity}Service` | `LeadService`, `ClientService` |
| **Service Impl** | `{Entity}ServiceImpl` | `LeadServiceImpl` |
| **Repository** | `{Entity}Repository` | `LeadRepository` |
| **Mapper** | `{Entity}Mapper` | `LeadMapper` |
| **Excepción** | `{Concepto}Exception` | `ResourceNotFoundException`, `ValidationException` |

### Eventos NATS

```
Patrón: carrillo.events.{dominio}.{acción}

Ejemplos:
  carrillo.events.lead.created
  carrillo.events.lead.scored
  carrillo.events.case.created
  carrillo.events.case.status-changed
  carrillo.events.appointment.scheduled
  carrillo.events.notification.sent
```

### Endpoints REST

```
Base: /{service-name}/api/{recurso}

Ejemplos:
  GET    /client-service/api/leads           # Listar leads
  POST   /client-service/api/leads           # Crear lead
  GET    /client-service/api/leads/{id}      # Obtener lead
  PUT    /client-service/api/leads/{id}      # Actualizar lead completo
  PATCH  /client-service/api/leads/{id}      # Actualizar parcial
  DELETE /client-service/api/leads/{id}      # Eliminar lead
  
  # Acciones especiales
  POST   /client-service/api/leads/{id}/convert     # Convertir a cliente
  PATCH  /client-service/api/leads/{id}/scoring     # Actualizar score
  GET    /client-service/api/leads/hot              # Filtro especial
```

---

## 🔧 Patrones Obligatorios

### 1. JacksonConfig (SIEMPRE crear en cada servicio nuevo)

```java
package com.carrilloabogados.clientservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

**¿Por qué?** Sin esto, `java.time.Instant` y `LocalDateTime` fallan con `InvalidDefinitionException`.

### 2. DTOs como Records (Java 21)

```java
package com.carrilloabogados.clientservice.dto;

import jakarta.validation.constraints.*;

public record LeadRequest(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "Nombre debe tener entre 2 y 100 caracteres")
    String nombre,
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    String email,
    
    @Size(max = 20, message = "Teléfono máximo 20 caracteres")
    @Pattern(regexp = "^[+]?[0-9\\s-]{7,20}$", message = "Formato de teléfono inválido")
    String telefono,
    
    @Size(max = 200)
    String empresa,
    
    @Size(max = 100)
    String cargo,
    
    String servicio,
    
    @Size(max = 2000, message = "Mensaje máximo 2000 caracteres")
    String mensaje
) {}
```

### 3. GlobalExceptionHandler

```java
package com.carrilloabogados.clientservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of(
                "timestamp", Instant.now(),
                "status", 404,
                "error", "Not Found",
                "message", ex.getMessage()
            ));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                e -> e.getField(),
                e -> e.getDefaultMessage(),
                (a, b) -> a
            ));
        
        return ResponseEntity.badRequest()
            .body(Map.of(
                "timestamp", Instant.now(),
                "status", 400,
                "error", "Validation Error",
                "errors", errors
            ));
    }
}
```

### 4. Event Publisher con Nullable (NATS opcional)

```java
package com.carrilloabogados.clientservice.event.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NatsEventPublisher implements EventPublisher {
    
    private final Connection natsConnection;
    private final ObjectMapper objectMapper;
    
    public NatsEventPublisher(
            @Nullable Connection natsConnection,
            ObjectMapper objectMapper) {
        this.natsConnection = natsConnection;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public void publish(String topic, Object event) {
        if (natsConnection == null) {
            log.warn("NATS not available, skipping event: {} - {}", topic, event.getClass().getSimpleName());
            return;
        }
        
        try {
            byte[] data = objectMapper.writeValueAsBytes(event);
            natsConnection.publish(topic, data);
            log.info("Event published: {} - {}", topic, event.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("Failed to publish event: {} - {}", topic, e.getMessage());
        }
    }
}
```

### 5. Entidad JPA con Auditoría

```java
package com.carrilloabogados.clientservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "leads", schema = "clients")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Lead {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(nullable = false)
    private String email;
    
    @Column(length = 20)
    private String telefono;
    
    @Column(length = 200)
    private String empresa;
    
    @Column(length = 100)
    private String cargo;
    
    private String servicio;
    
    @Column(length = 2000)
    private String mensaje;
    
    // Scoring (calculado por n8n)
    @Builder.Default
    private Integer leadScore = 30;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private LeadCategory leadCategory = LeadCategory.COLD;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private LeadStatus leadStatus = LeadStatus.NUEVO;
    
    // Conversión
    private UUID clientId;
    
    // Auditoría automática
    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;
    
    @UpdateTimestamp
    private Instant updatedAt;
}
```

---

## 🧪 Testing (Patrones del Proyecto)

### Test de Servicio (Unitario con Mockito)

```java
@ExtendWith(MockitoExtension.class)
class LeadServiceTest {
    
    @Mock
    private LeadRepository leadRepository;
    
    @Mock
    private LeadMapper leadMapper;
    
    @Mock
    private EventPublisher eventPublisher;
    
    @InjectMocks
    private LeadServiceImpl leadService;
    
    @Test
    void createLead_ShouldSaveAndPublishEvent() {
        // Arrange
        var request = new LeadRequest("Juan", "juan@email.com", null, null, null, null, null);
        var lead = Lead.builder().id(UUID.randomUUID()).nombre("Juan").build();
        var response = new LeadResponse(lead.getId(), "Juan", "juan@email.com", 30, LeadCategory.COLD);
        
        when(leadMapper.toEntity(request)).thenReturn(lead);
        when(leadRepository.save(lead)).thenReturn(lead);
        when(leadMapper.toResponse(lead)).thenReturn(response);
        
        // Act
        var result = leadService.createLead(request);
        
        // Assert
        assertThat(result.nombre()).isEqualTo("Juan");
        verify(eventPublisher).publish(eq("carrillo.events.lead.created"), any());
    }
}
```

### Test de Controller (Integración con MockMvc)

```java
@WebMvcTest(LeadResource.class)
@Import({SecurityTestConfig.class})
class LeadResourceTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private LeadService leadService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void createLead_ValidRequest_Returns201() throws Exception {
        var request = new LeadRequest("Juan", "juan@email.com", null, null, null, null, null);
        var response = new LeadResponse(UUID.randomUUID(), "Juan", "juan@email.com", 30, LeadCategory.COLD);
        
        when(leadService.createLead(any())).thenReturn(response);
        
        mockMvc.perform(post("/api/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nombre").value("Juan"));
    }
    
    @Test
    void createLead_InvalidEmail_Returns400() throws Exception {
        var request = Map.of("nombre", "Juan", "email", "invalid-email");
        
        mockMvc.perform(post("/api/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors.email").exists());
    }
}
```

### Configuración de Tests (application-test.yml)

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=PostgreSQL;INIT=CREATE SCHEMA IF NOT EXISTS clients
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
        default_schema: clients
  flyway:
    enabled: false

resilience4j:
  circuitbreaker:
    enabled: false

nats:
  enabled: false
```

---

## 🔧 Comandos de Desarrollo

### Build
```powershell
# Build rápido todos los servicios (paralelo)
.\mvnw clean package -DskipTests -T 1C

# Build con tests (105 tests actuales)
.\mvnw clean verify -T 1C

# Build servicio específico
.\mvnw package -DskipTests -pl client-service
.\mvnw package -DskipTests -pl case-service
```

### Testing
```powershell
# Todos los tests
.\mvnw test -T 1C

# Tests de un servicio
.\mvnw test -pl client-service

# Tests específicos por clase
.\mvnw test -pl client-service "-Dtest=LeadServiceTest"

# Tests de seguridad (66 tests)
.\mvnw test -pl client-service "-Dtest=*SecurityTest,*ValidationTest"

# Con profile de test
.\mvnw test -pl client-service "-Dspring.profiles.active=test"
```

### Docker
```powershell
# Levantar todo
docker-compose up -d

# Ver estado
docker-compose ps

# Logs de un servicio
docker-compose logs -f client-service

# Reconstruir un servicio
docker-compose up -d --build client-service

# Verificar health de todos
$ports = @('8080','8200','8300','8400','8500','8600','8700','8800')
foreach ($p in $ports) {
    try { 
        Invoke-WebRequest "http://localhost:$p/actuator/health" -TimeoutSec 2 -UseBasicParsing | Out-Null
        Write-Host "$p : UP" -ForegroundColor Green 
    } catch { 
        Write-Host "$p : DOWN" -ForegroundColor Red 
    }
}
```

### Base de Datos
```powershell
# Conectar a PostgreSQL
docker exec -it postgresql psql -U carrillo -d carrillo_legal_tech

# Cambiar schema
SET search_path TO clients;

# Ver tablas
\dt

# Ver schemas
\dn
```

---

## 📋 Checklist: Nuevo Endpoint

Cuando se solicite crear un nuevo endpoint, seguir esta lista:

### 1. Definición
- [ ] Definir método HTTP (GET, POST, PUT, PATCH, DELETE)
- [ ] Definir path siguiendo convención REST
- [ ] Identificar request body (si aplica)
- [ ] Definir response esperado

### 2. DTOs
- [ ] Crear `{Entity}Request` record con validaciones
- [ ] Crear `{Entity}Response` record
- [ ] Crear Mapper (o método en mapper existente)

### 3. Service Layer
- [ ] Crear método en Service interface
- [ ] Implementar lógica en ServiceImpl
- [ ] Manejar excepciones (ResourceNotFoundException, etc.)
- [ ] Emitir evento NATS si es operación relevante

### 4. Controller Layer
- [ ] Crear endpoint en Resource
- [ ] Añadir `@Valid` para validaciones
- [ ] Documentar con `@Operation`, `@ApiResponses` (OpenAPI)
- [ ] Retornar código HTTP correcto (201 para POST, 200 para GET, etc.)

### 5. Testing
- [ ] Test unitario del Service (Mockito)
- [ ] Test de integración del Controller (MockMvc)
- [ ] Test de validación (casos de error)

### 6. Documentación
- [ ] Actualizar API docs si es necesario
- [ ] Añadir ejemplo en colección Postman

---

## 🚨 Errores Comunes y Soluciones

| Error | Causa | Solución |
|-------|-------|----------|
| `InvalidDefinitionException: Java 8 date/time type 'java.time.Instant'` | Falta JavaTimeModule | Crear `JacksonConfig.java` con `JavaTimeModule` |
| `NullPointerException` en EventPublisher | NATS no disponible | Usar `@Nullable` en constructor |
| `Schema "CLIENTS" not found` en tests | H2 no crea schema | Añadir `INIT=CREATE SCHEMA IF NOT EXISTS clients` en URL |
| `CircuitBreaker NoSuchBean` en tests | Resilience4j health | `resilience4j.circuitbreaker.enabled: false` en test profile |
| `Table not found` | DDL no ejecutado | Verificar `spring.jpa.hibernate.ddl-auto` |
| CORS error desde frontend | Gateway mal configurado | Verificar `allowed-origins` en api-gateway |

---

## 🔗 Referencias del Proyecto

| Documento | Ubicación | Propósito |
|-----------|-----------|-----------|
| Arquitectura | [docs/architecture/ARCHITECTURE.md](../../../docs/architecture/ARCHITECTURE.md) | Visión arquitectónica |
| Requerimientos | [docs/business/REQUERIMIENTOS.md](../../../docs/business/REQUERIMIENTOS.md) | RFs y RNFs |
| ADR Database | [docs/architecture/ADR-005-database-strategy.md](../../../docs/architecture/ADR-005-database-strategy.md) | Estrategia de BD |
| Swagger UI | `http://localhost:8200/client-service/swagger-ui.html` | Documentación API |
| Colección Postman | [docs/api/](../../../docs/api/) | Tests de API |

---

*Agente actualizado: 12 de Enero 2026, 10:30 COT*  
*Proyecto: Carrillo Abogados Legal Tech Platform*
