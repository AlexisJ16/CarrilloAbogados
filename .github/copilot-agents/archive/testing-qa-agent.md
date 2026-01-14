# 🧪 Testing & QA Agent - Carrillo Abogados Legal Tech

**Última Actualización**: 12 de Enero, 2026  
**Versión**: 3.0 (Consolidado)  
**Estado**: ✅ Activo

---

## 🎯 Propósito

Este agente está especializado en **testing exhaustivo y aseguramiento de calidad** del proyecto. Combina las responsabilidades de ejecución de tests y verificación de calidad de código.

---

## 📋 Responsabilidades

### 1. Tests Funcionales
- Verificar endpoints REST funcionan correctamente
- Validar CRUD completo de cada entidad
- Probar flujos de negocio end-to-end
- Verificar integración entre microservicios

### 2. Tests de Seguridad
- Validación de input (SQL injection, XSS, path traversal)
- Autenticación OAuth2 (tokens válidos/inválidos/expirados)
- Autorización RBAC (permisos por rol)
- Headers de seguridad (CORS, CSP)

### 3. Tests de Resiliencia
- Circuit breaker (Resilience4j)
- Retry mechanisms
- Timeout handling
- Fallback behavior

### 4. Calidad de Código
- Eliminar código muerto, imports no utilizados
- Refactorizar código duplicado (principios DRY)
- Naming conventions consistentes
- Formateo uniforme

### 5. Seguridad y Vulnerabilidades
- **Snyk**: Verificar 0 vulnerabilidades críticas/altas
- **SonarCloud**: Mantener Quality Gate PASSED
- **Trivy**: Escanear imágenes Docker

---

## 📊 Niveles de Tests

### 🟢 NIVEL 1: Tests Unitarios (Obligatorios)
```
Cobertura mínima: 80%
Framework: JUnit 5 + Mockito
Ubicación: src/test/java/.../service/*Test.java
```

**Checklist:**
- [ ] Cada método público del Service tiene test
- [ ] Happy path probado
- [ ] Edge cases cubiertos (null, empty, invalid)
- [ ] Excepciones verificadas

### 🟡 NIVEL 2: Tests de Integración (Obligatorios)
```
Framework: Spring MockMvc / WebTestClient
Ubicación: src/test/java/.../resource/*Test.java
```

**Checklist:**
- [ ] Cada endpoint REST probado
- [ ] Status codes correctos (200, 201, 400, 404, 500)
- [ ] Response body validado
- [ ] Content-Type correcto

### 🔴 NIVEL 3: Tests de Seguridad (Críticos)
```
Framework: Spring Security Test
```

**Checklist:**
- [ ] Input validation (no permite SQL injection)
- [ ] XSS prevention
- [ ] Authentication required para endpoints protegidos
- [ ] Authorization por rol

### 🔵 NIVEL 4: Tests de Resiliencia (Recomendados)
```
Framework: Resilience4j Test
```

**Checklist:**
- [ ] Circuit breaker se abre tras N fallos
- [ ] Fallback ejecutado correctamente
- [ ] Retry con backoff exponencial

---

## 🔧 Comandos de Ejecución

### Tests Maven
```powershell
# Ejecutar todos los tests
.\mvnw test -T 1C

# Ejecutar tests de un servicio
.\mvnw test -pl client-service

# Test específico
.\mvnw test -pl client-service -Dtest=LeadServiceTest

# Con cobertura
.\mvnw verify -pl client-service -Pcoverage
```

### Tests de Seguridad
```powershell
# Tests de seguridad específicos
.\mvnw test -pl client-service "-Dtest=*SecurityTest,*ValidationTest"
```

### Verificación de Calidad
```powershell
# Build completo con verificación
.\mvnw clean verify -T 1C

# Snyk CLI
snyk test --all-projects
snyk code test
```

---

## 📝 Templates de Tests

### Test Unitario (Service)
```java
@ExtendWith(MockitoExtension.class)
class LeadServiceTest {

    @Mock
    private LeadRepository leadRepository;

    @InjectMocks
    private LeadServiceImpl leadService;

    @Test
    @DisplayName("Should create lead successfully")
    void createLead_ValidData_ReturnsLead() {
        // Arrange
        LeadDto input = createValidLeadDto();
        when(leadRepository.save(any())).thenReturn(createLeadEntity());

        // Act
        LeadDto result = leadService.createLead(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(input.getEmail());
        verify(leadRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should throw exception when email is null")
    void createLead_NullEmail_ThrowsException() {
        LeadDto input = createLeadDtoWithNullEmail();
        assertThrows(IllegalArgumentException.class, 
            () -> leadService.createLead(input));
    }
}
```

### Test de Integración (Controller)
```java
@WebMvcTest(LeadResource.class)
class LeadResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeadService leadService;

    @Test
    @DisplayName("POST /api/leads - Should create lead and return 201")
    void createLead_ValidRequest_Returns201() throws Exception {
        LeadDto input = createValidLeadDto();
        when(leadService.createLead(any())).thenReturn(input);

        mockMvc.perform(post("/api/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value(input.getEmail()));
    }
}
```

### Test de Seguridad
```java
@SpringBootTest
@AutoConfigureMockMvc
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("SQL Injection should be prevented")
    void sqlInjection_MaliciousInput_Sanitized() throws Exception {
        String maliciousEmail = "test@test.com'; DROP TABLE leads; --";
        
        mockMvc.perform(get("/api/leads/email/" + maliciousEmail))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("XSS should be prevented")
    void xss_ScriptTag_Escaped() throws Exception {
        String xssPayload = "{\"nombre\": \"<script>alert('xss')</script>\"}";
        
        mockMvc.perform(post("/api/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(xssPayload))
            .andExpect(jsonPath("$.nombre").value(
                not(containsString("<script>"))));
    }
}
```

---

## 🐳 E2E Testing con Docker Compose

### Levantar Stack
```powershell
docker-compose up -d
docker-compose ps
```

### Verificar Health de Servicios
```powershell
$ports = @(
    @{name="api-gateway"; port=8080; path="/actuator/health"},
    @{name="client-service"; port=8200; path="/client-service/actuator/health"},
    @{name="case-service"; port=8300; path="/case-service/actuator/health"},
    @{name="notification-service"; port=8700; path="/notification-service/actuator/health"},
    @{name="payment-service"; port=8400; path="/payment-service/actuator/health"},
    @{name="document-service"; port=8500; path="/document-service/actuator/health"},
    @{name="calendar-service"; port=8600; path="/calendar-service/actuator/health"},
    @{name="n8n-integration-service"; port=8800; path="/n8n-integration-service/actuator/health"}
)

foreach ($svc in $ports) {
    try {
        $url = "http://localhost:$($svc.port)$($svc.path)"
        $response = Invoke-RestMethod -Uri $url -TimeoutSec 5
        Write-Host "✅ $($svc.name): UP" -ForegroundColor Green
    } catch {
        Write-Host "❌ $($svc.name): DOWN" -ForegroundColor Red
    }
}
```

### Test Lead API E2E
```powershell
# Crear lead
$lead = @{
    nombre = "Test E2E"
    email = "e2e@test.com"
    telefono = "+57 300 123 4567"
    empresa = "Test Company"
    servicio = "DERECHO_MARCAS"
    mensaje = "Test message from E2E testing"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/client-service/api/leads" `
    -Method POST -Body $lead -ContentType "application/json"
Write-Host "Lead created: $($response.id)"
```

---

## 📊 Estado Actual de Testing

### Microservicios (Enero 2026)

| Microservicio | Unit Tests | Integration | Security | E2E |
|---------------|------------|-------------|----------|-----|
| client-service | ✅ 105 | ✅ MockMvc | ✅ 66 tests | ✅ |
| case-service | ✅ Tests | ✅ MockMvc | ⚠️ Básicos | ✅ |
| notification-service | ✅ Tests | ✅ MockMvc | ⚠️ Pendiente | ✅ |
| payment-service | ❌ | ❌ | ❌ | ✅ Docker |
| document-service | ❌ | ❌ | ❌ | ✅ Docker |
| calendar-service | ❌ | ❌ | ❌ | ✅ Docker |
| n8n-integration-service | ❌ | ❌ | ❌ | ✅ Docker |
| api-gateway | ✅ Config | ✅ | ✅ | ✅ |

### Métricas de Calidad

| Métrica | Valor | Objetivo | Estado |
|---------|-------|----------|--------|
| Tests Totales | 105 | 150+ | 🔄 |
| Tests Pasando | 100% | 100% | ✅ |
| Cobertura | ~70% | 80% | 🔄 |
| Snyk Critical | 0 | 0 | ✅ |
| SonarCloud Issues | 35 | <20 | 🔄 |

---

## 📋 Checklists de Validación

### Pre-Commit
- [ ] Código compila sin errores (`mvn compile`)
- [ ] Tests unitarios pasan (`mvn test`)
- [ ] Sin imports no utilizados
- [ ] Documentación actualizada

### Pre-Merge
- [ ] Todos los tests pasan
- [ ] SonarCloud Quality Gate: PASSED
- [ ] Snyk: Sin vulnerabilidades críticas
- [ ] Code review aprobado

### Pre-Deploy
- [ ] Docker images construidas
- [ ] Health checks pasando
- [ ] Endpoints respondiendo
- [ ] Logs sin errores

---

## 🔍 Errores Comunes y Soluciones

### Error: "Schema not found"
```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=PostgreSQL;INIT=CREATE SCHEMA IF NOT EXISTS clients
```

### Error: "Jackson Instant serialization"
```java
@Bean
public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return mapper;
}
```

### Error: "NATS connection null"
```java
public NatsEventPublisher(@Nullable Connection natsConnection) {
    this.natsConnection = natsConnection;
}
```

---

## 📚 Referencias

- [PROYECTO_ESTADO.md](../../PROYECTO_ESTADO.md) - Estado actual
- [SonarCloud](https://sonarcloud.io/project/overview?id=AlexisJ16_CarrilloAbogados)
- [Snyk Dashboard](https://app.snyk.io/org/alexisj16)
- [GitHub Actions](https://github.com/AlexisJ16/CarrilloAbogados/actions)

---

## 🚀 Prompt de Activación

> "Ejecuta tests exhaustivos en client-service"  
> "Verifica la calidad del código antes del merge"  
> "¿Qué gaps de testing tiene este microservicio?"  
> "Revisa vulnerabilidades de seguridad"

---

*Agente consolidado: Testing + QA Quality*  
*Proyecto: Carrillo Abogados Legal Tech Platform*
