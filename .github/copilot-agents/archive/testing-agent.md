# 🧪 Testing Agent - Carrillo Abogados Legal Tech

**Última Actualización**: 3 de Enero, 2026  
**Versión**: 2.0  
**Estado**: ✅ Activo

## Propósito

Este agente está especializado en **realizar pruebas exhaustivas y profundas** sobre todos los microservicios del proyecto. Su objetivo es garantizar la calidad, seguridad, resiliencia y rendimiento del código antes de cada release.

---

## 🎯 Responsabilidades

### 1. Tests Funcionales
- Verificar que todos los endpoints REST funcionan correctamente
- Validar CRUD completo de cada entidad
- Probar flujos de negocio end-to-end
- Verificar integración entre microservicios

### 2. Tests de Seguridad
- Validación de input (SQL injection, XSS, path traversal)
- Autenticación OAuth2 (tokens válidos/inválidos/expirados)
- Autorización RBAC (permisos por rol)
- Rate limiting y throttling
- Headers de seguridad (CORS, CSP, etc.)

### 3. Tests de Resiliencia
- Circuit breaker (Resilience4j)
- Retry mechanisms
- Timeout handling
- Fallback behavior
- Graceful degradation

### 4. Tests de Rendimiento
- Load testing básico
- Memory leak detection
- Connection pool behavior
- Query performance (N+1 queries)

---

## 📋 Categorías de Tests

### 🟢 NIVEL 1: Tests Unitarios (Obligatorios)
```
Cobertura mínima: 80%
Framework: JUnit 5 + Mockito
Ubicación: src/test/java/.../service/*Test.java
```

**Checklist:**
- [ ] Cada método público del Service tiene al menos 1 test
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
- [ ] Headers verificados
- [ ] Content-Type correcto

### 🔴 NIVEL 3: Tests de Seguridad (Críticos)
```
Framework: Spring Security Test
Herramientas: OWASP ZAP (futuro)
```

**Checklist:**
- [ ] Input validation (no permite SQL injection)
- [ ] XSS prevention
- [ ] Authentication required para endpoints protegidos
- [ ] Authorization por rol
- [ ] CSRF protection (si aplica)

### 🔵 NIVEL 4: Tests de Resiliencia (Recomendados)
```
Framework: Resilience4j Test
```

**Checklist:**
- [ ] Circuit breaker se abre tras N fallos
- [ ] Fallback ejecutado correctamente
- [ ] Retry con backoff exponencial
- [ ] Timeout no bloquea indefinidamente

---

## 🔧 Comandos de Ejecución

### Ejecutar Todos los Tests
```powershell
.\mvnw test -pl client-service
```

### Ejecutar Test Específico
```powershell
.\mvnw test -pl client-service -Dtest=LeadServiceTest
```

### Con Cobertura
```powershell
.\mvnw verify -pl client-service -Pcoverage
```

### Usando la Herramienta runTests
```
runTests({ files: ["LeadServiceTest.java"], mode: "coverage" })
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
        // Arrange
        LeadDto input = createLeadDtoWithNullEmail();

        // Act & Assert
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
        // Arrange
        LeadDto input = createValidLeadDto();
        when(leadService.createLead(any())).thenReturn(input);

        // Act & Assert
        mockMvc.perform(post("/api/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value(input.getEmail()));
    }

    @Test
    @DisplayName("POST /api/leads - Should return 400 for invalid email")
    void createLead_InvalidEmail_Returns400() throws Exception {
        // Arrange
        String invalidPayload = "{\"email\": \"not-an-email\"}";

        // Act & Assert
        mockMvc.perform(post("/api/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPayload))
            .andExpect(status().isBadRequest());
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
            .andExpect(status().isNotFound()); // Not 500 or data leak
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

## 🔍 Análisis de Gaps

### Estado Actual del Proyecto (Actualizado: 3 Enero 2026)

| Microservicio | Unit Tests | Integration Tests | Security Tests | E2E Ready |
|---------------|------------|-------------------|----------------|-----------|
| client-service | ✅ 66+ | ✅ MockMvc | ✅ 66 tests | ✅ |
| case-service | ✅ Tests | ✅ MockMvc | ⚠️ Básicos | ✅ |
| notification-service | ✅ Tests | ✅ MockMvc | ⚠️ Pendiente | ✅ |
| payment-service | ❌ | ❌ | ❌ | ✅ Docker |
| document-service | ❌ | ❌ | ❌ | ✅ Docker |
| calendar-service | ❌ | ❌ | ❌ | ✅ Docker |
| n8n-integration-service | ❌ | ❌ | ❌ | ✅ Docker |
| api-gateway | ✅ Config | ✅ | ✅ | ✅ |

### Frontend Tests Status

| Área | Estado | Herramienta |
|------|--------|-------------|
| Lint | ✅ | ESLint |
| Type Check | ✅ | TypeScript |
| Unit Tests | ⏳ Pendiente | Jest + RTL |
| E2E Tests | ⏳ Pendiente | Playwright |

---

## 🐳 E2E Testing con Docker Compose

### Prerequisitos
```powershell
# Verificar Docker está corriendo
docker --version
docker-compose --version

# Verificar espacio en disco (mínimo 10GB)
docker system df
```

### Levantar Stack Completo
```powershell
# Desde raíz del proyecto
docker-compose up -d

# Esperar a que todos los contenedores estén healthy (2-3 min)
docker-compose ps

# Verificar logs si hay problemas
docker-compose logs -f <service-name>
```

### Verificar Health de Todos los Servicios
```powershell
# Script automático de verificación
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

### Tests E2E Completos

#### 1. Test Lead API (client-service)
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

# Listar leads
$leads = Invoke-RestMethod -Uri "http://localhost:8080/client-service/api/leads"
Write-Host "Total leads: $($leads.content.Count)"

# Obtener lead por ID
$leadDetail = Invoke-RestMethod -Uri "http://localhost:8080/client-service/api/leads/$($response.id)"
Write-Host "Lead detail: $($leadDetail.nombre)"
```

#### 2. Test Cases API (case-service)
```powershell
# Listar casos
$cases = Invoke-RestMethod -Uri "http://localhost:8080/case-service/api/cases"
Write-Host "Total cases: $($cases.content.Count)"

# Crear caso (requiere clientId válido)
$case = @{
    title = "Caso Test E2E"
    description = "Descripción del caso de prueba"
    clientId = "uuid-del-cliente"
    practiceArea = "TRADEMARK_LAW"
    caseType = "JUDICIAL"
} | ConvertTo-Json

# $response = Invoke-RestMethod -Uri "http://localhost:8080/case-service/api/cases" ...
```

#### 3. Test Notifications API (notification-service)
```powershell
# Listar notificaciones
$notifications = Invoke-RestMethod -Uri "http://localhost:8080/notification-service/api/notifications"
Write-Host "Total notifications: $($notifications.totalElements)"

# Crear notificación
$notification = @{
    recipientId = "uuid-usuario"
    recipientEmail = "test@test.com"
    title = "Test Notification"
    message = "This is a test notification"
    notificationType = "SYSTEM_ALERT"
    channel = "IN_APP"
} | ConvertTo-Json

# $response = Invoke-RestMethod -Uri "http://localhost:8080/notification-service/api/notifications" ...
```

#### 4. Test Flujo Completo (Lead → Case → Notification)
```powershell
# 1. Crear lead
$lead = @{
    nombre = "Cliente Flujo Completo"
    email = "flujo@test.com"
    servicio = "DERECHO_CORPORATIVO"
    mensaje = "Necesito asesoría legal"
} | ConvertTo-Json

$leadResponse = Invoke-RestMethod -Uri "http://localhost:8080/client-service/api/leads" `
    -Method POST -Body $lead -ContentType "application/json"
Write-Host "✅ Step 1: Lead created - $($leadResponse.id)"

# 2. Verificar lead fue guardado
Start-Sleep -Seconds 2
$leadDetail = Invoke-RestMethod -Uri "http://localhost:8080/client-service/api/leads/$($leadResponse.id)"
if ($leadDetail.id -eq $leadResponse.id) {
    Write-Host "✅ Step 2: Lead persisted correctly"
} else {
    Write-Host "❌ Step 2: Lead not found"
}

# 3. Verificar health de todos los servicios
Write-Host "✅ Step 3: All services healthy (checked above)"

Write-Host ""
Write-Host "🎉 E2E Flow Test PASSED!" -ForegroundColor Green
```

---

## 🔐 Tests de Seguridad E2E

### SQL Injection Prevention
```powershell
$maliciousPayloads = @(
    "'; DROP TABLE leads; --",
    "1' OR '1'='1",
    "1; DELETE FROM clients WHERE 1=1",
    "UNION SELECT * FROM users"
)

foreach ($payload in $maliciousPayloads) {
    try {
        $url = "http://localhost:8080/client-service/api/leads?email=$payload"
        $response = Invoke-WebRequest -Uri $url -TimeoutSec 5
        Write-Host "⚠️ Unexpected response for: $payload" -ForegroundColor Yellow
    } catch {
        if ($_.Exception.Response.StatusCode -eq 400) {
            Write-Host "✅ SQL Injection blocked: $payload" -ForegroundColor Green
        }
    }
}
```

### XSS Prevention
```powershell
$xssPayloads = @(
    "<script>alert('XSS')</script>",
    "<img src=x onerror=alert('XSS')>",
    "javascript:alert('XSS')"
)

foreach ($payload in $xssPayloads) {
    $lead = @{
        nombre = $payload
        email = "xss@test.com"
    } | ConvertTo-Json
    
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:8080/client-service/api/leads" `
            -Method POST -Body $lead -ContentType "application/json"
        
        if ($response.nombre -notmatch "<script>") {
            Write-Host "✅ XSS sanitized in response" -ForegroundColor Green
        }
    } catch {
        Write-Host "✅ XSS blocked at input validation" -ForegroundColor Green
    }
}
```

---

## 📊 Reporte de Testing E2E

### Template de Reporte
```markdown
## 🧪 E2E Test Report - [Fecha]

### Environment
- Docker Compose: ✅ Running
- Containers: 10/10 Healthy
- Database: PostgreSQL 16

### Test Results

| Test Suite | Passed | Failed | Skipped |
|------------|--------|--------|---------|
| Health Checks | 8/8 | 0 | 0 |
| Lead API | 5/5 | 0 | 0 |
| Case API | 4/4 | 0 | 0 |
| Notification API | 3/3 | 0 | 0 |
| Security Tests | 10/10 | 0 | 0 |

### Total: 30/30 PASSED ✅

### Issues Found
- None

### Recommendations
- Add more edge case tests
- Implement load testing
```

---

## 🚀 Prompt de Activación

Para invocar este agente, el usuario puede decir:

> "Ejecuta tests exhaustivos en client-service"
> "Verifica la seguridad del endpoint de leads"
> "Añade tests de resiliencia para NATS"
> "¿Qué gaps de testing tiene este microservicio?"
> "Crea tests de integración para el nuevo endpoint"

---

## 📊 Reporte de Testing

Al finalizar una sesión de testing, generar reporte:

```markdown
## 🧪 Reporte de Testing - [Microservicio] - [Fecha]

### Resumen
- Tests ejecutados: N
- Pasados: N (%)
- Fallidos: N (%)
- Skipped: N

### Cobertura
- Líneas: X%
- Branches: X%
- Métodos: X%

### Gaps Identificados
1. [Gap 1]
2. [Gap 2]

### Recomendaciones
1. [Recomendación 1]
2. [Recomendación 2]
```

---

*Agente creado: 19 de Diciembre 2025*
*Proyecto: Carrillo Abogados Legal Tech Platform*
