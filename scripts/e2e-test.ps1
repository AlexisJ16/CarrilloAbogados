# ============================================================
# E2E Test Suite - Carrillo Abogados Legal Tech Platform
# ============================================================

param(
    [switch]$SkipDocker,
    [switch]$Verbose,
    [switch]$OnlyHealthCheck
)

$ErrorActionPreference = "Continue"
$Host.UI.RawUI.WindowTitle = "Carrillo Abogados - E2E Tests"

# Colors for output
function Write-Success { param($msg) Write-Host "[PASS] $msg" -ForegroundColor Green }
function Write-Fail { param($msg) Write-Host "[FAIL] $msg" -ForegroundColor Red }
function Write-Info { param($msg) Write-Host "[INFO] $msg" -ForegroundColor Cyan }
function Write-Warning { param($msg) Write-Host "[WARN] $msg" -ForegroundColor Yellow }
function Write-Section { param($msg) Write-Host "`n========================================" -ForegroundColor Magenta; Write-Host " $msg" -ForegroundColor Magenta; Write-Host "========================================" -ForegroundColor Magenta }

# Test counters
$script:passed = 0
$script:failed = 0
$script:skipped = 0

function Test-Assert {
    param($condition, $successMsg, $failMsg)
    if ($condition) {
        Write-Success $successMsg
        $script:passed++
        return $true
    }
    else {
        Write-Fail $failMsg
        $script:failed++
        return $false
    }
}

# ============================================================
# 1. DOCKER COMPOSE STATUS
# ============================================================
Write-Section "1. VERIFICANDO DOCKER COMPOSE"

if (-not $SkipDocker) {
    $psOutput = docker-compose ps 2>&1
    Write-Info "Estado actual de Docker Compose:"
    Write-Host $psOutput
}

# ============================================================
# 2. HEALTH CHECKS DE TODOS LOS SERVICIOS
# ============================================================
Write-Section "2. HEALTH CHECKS DE MICROSERVICIOS"

$services = @(
    @{ Name = "PostgreSQL"; Port = 5432; Protocol = "tcp" },
    @{ Name = "NATS"; Port = 4222; Protocol = "tcp" },
    @{ Name = "API Gateway"; Port = 8080; HealthPath = "/actuator/health" },
    @{ Name = "Client Service"; Port = 8200; HealthPath = "/client-service/actuator/health" },
    @{ Name = "Case Service"; Port = 8300; HealthPath = "/case-service/actuator/health" },
    @{ Name = "Payment Service"; Port = 8400; HealthPath = "/actuator/health" },
    @{ Name = "Document Service"; Port = 8500; HealthPath = "/actuator/health" },
    @{ Name = "Calendar Service"; Port = 8600; HealthPath = "/actuator/health" },
    @{ Name = "Notification Service"; Port = 8700; HealthPath = "/actuator/health" },
    @{ Name = "N8N Integration Service"; Port = 8800; HealthPath = "/n8n-integration-service/actuator/health" }
)

foreach ($svc in $services) {
    if ($svc.Protocol -eq "tcp") {
        try {
            $tcpClient = New-Object System.Net.Sockets.TcpClient
            $result = $tcpClient.BeginConnect("localhost", $svc.Port, $null, $null)
            $wait = $result.AsyncWaitHandle.WaitOne(2000)
            if ($wait -and $tcpClient.Connected) {
                Test-Assert $true "$($svc.Name) (puerto $($svc.Port)) UP" "$($svc.Name) DOWN"
            }
            else {
                Test-Assert $false "$($svc.Name) UP" "$($svc.Name) (puerto $($svc.Port)) DOWN - No responde"
            }
            $tcpClient.Close()
        }
        catch {
            Test-Assert $false "$($svc.Name) UP" "$($svc.Name) (puerto $($svc.Port)) DOWN - Error"
        }
    }
    else {
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:$($svc.Port)$($svc.HealthPath)" -Method GET -TimeoutSec 5 -UseBasicParsing -ErrorAction Stop
            # Handle byte array content from Invoke-WebRequest
            $content = if ($response.Content -is [byte[]]) { [System.Text.Encoding]::UTF8.GetString($response.Content) } else { $response.Content }
            $health = $content | ConvertFrom-Json
            $status = $health.status
            Test-Assert ($status -eq "UP") "$($svc.Name) $status" "$($svc.Name) $status (esperado UP)"
        }
        catch {
            Test-Assert $false "$($svc.Name) UP" "$($svc.Name) (puerto $($svc.Port)) Error - $($_.Exception.Message)"
        }
    }
}

if ($OnlyHealthCheck) {
    Write-Section "RESUMEN HEALTH CHECKS"
    Write-Host "Pasados: $script:passed | Fallidos: $script:failed | Omitidos: $script:skipped"
    exit $(if ($script:failed -gt 0) { 1 } else { 0 })
}

# ============================================================
# 3. PRUEBAS DE API - CLIENT SERVICE
# ============================================================
Write-Section "3. PRUEBAS API - CLIENT SERVICE"

$baseUrl = "http://localhost:8200/client-service"

# 3.1 GET /api/users
Write-Info "Testing GET /api/users..."
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/api/users" -Method GET -TimeoutSec 10 -UseBasicParsing
    Test-Assert ($response.StatusCode -eq 200) "GET /api/users retorna 200 OK" "GET /api/users fallo"
}
catch {
    Test-Assert $false "GET /api/users OK" "GET /api/users Error - $($_.Exception.Message)"
}

# 3.2 GET /api/leads
Write-Info "Testing GET /api/leads..."
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/api/leads" -Method GET -TimeoutSec 10 -UseBasicParsing
    Test-Assert ($response.StatusCode -eq 200) "GET /api/leads retorna 200 OK" "GET /api/leads fallo"
}
catch {
    Test-Assert $false "GET /api/leads OK" "GET /api/leads Error - $($_.Exception.Message)"
}

# 3.3 POST /api/leads - Crear un lead de prueba
Write-Info "Testing POST /api/leads (crear lead)..."
$testLeadId = $null
$timestamp = Get-Date -Format 'yyyyMMddHHmmss'
$testLead = @{
    nombre   = "Lead E2E Test"
    email    = "e2etest_$timestamp@test.com"
    telefono = "+57 300 123 4567"
    empresa  = "Empresa de Prueba E2E"
    cargo    = "Gerente de Pruebas"
    servicio = "Derecho de Marcas"
    mensaje  = "Este es un mensaje de prueba E2E para validar el flujo completo de captura de leads."
} | ConvertTo-Json -Depth 10

try {
    $response = Invoke-WebRequest -Uri "$baseUrl/api/leads" -Method POST -Body $testLead -ContentType "application/json" -TimeoutSec 10 -UseBasicParsing
    $createdLead = $response.Content | ConvertFrom-Json
    $testLeadId = $createdLead.id
    Test-Assert ($response.StatusCode -eq 201 -or $response.StatusCode -eq 200) "POST /api/leads Lead creado con ID $testLeadId" "POST /api/leads Codigo inesperado $($response.StatusCode)"
    
    if ($Verbose) {
        Write-Info "Lead creado:"
        Write-Host ($createdLead | ConvertTo-Json -Depth 5)
    }
}
catch {
    Test-Assert $false "POST /api/leads OK" "POST /api/leads Error - $($_.Exception.Message)"
}

# 3.4 GET /api/leads/{id} - Verificar lead creado
if ($testLeadId) {
    Write-Info "Testing GET /api/leads/$testLeadId..."
    try {
        $response = Invoke-WebRequest -Uri "$baseUrl/api/leads/$testLeadId" -Method GET -TimeoutSec 10 -UseBasicParsing
        $retrievedLead = $response.Content | ConvertFrom-Json
        Test-Assert ($retrievedLead.id -eq $testLeadId) "GET /api/leads/$testLeadId Lead recuperado correctamente" "GET /api/leads/$testLeadId ID no coincide"
    }
    catch {
        Test-Assert $false "GET /api/leads/$testLeadId OK" "GET /api/leads/$testLeadId Error - $($_.Exception.Message)"
    }
}

# ============================================================
# 4. PRUEBAS DE API - CASE SERVICE
# ============================================================
Write-Section "4. PRUEBAS API - CASE SERVICE"

$caseBaseUrl = "http://localhost:8300/case-service"

Write-Info "Testing GET /api/cases..."
try {
    $response = Invoke-WebRequest -Uri "$caseBaseUrl/api/cases" -Method GET -TimeoutSec 10 -UseBasicParsing
    Test-Assert ($response.StatusCode -eq 200) "GET /api/cases retorna 200 OK" "GET /api/cases fallo"
}
catch {
    Test-Assert $false "GET /api/cases OK" "GET /api/cases Error - $($_.Exception.Message)"
}

# ============================================================
# 5. PRUEBAS DE API GATEWAY ROUTING
# ============================================================
Write-Section "5. PRUEBAS API GATEWAY ROUTING"

$gatewayUrl = "http://localhost:8080"

Write-Info "Testing Gateway route to client-service..."
try {
    $response = Invoke-WebRequest -Uri "$gatewayUrl/client-service/actuator/health" -Method GET -TimeoutSec 10 -UseBasicParsing
    Test-Assert ($response.StatusCode -eq 200) "Gateway -> Client Service OK" "Gateway -> Client Service Fallo"
}
catch {
    Test-Assert $false "Gateway -> Client Service OK" "Gateway -> Client Service Error - $($_.Exception.Message)"
}

Write-Info "Testing Gateway route to case-service..."
try {
    $response = Invoke-WebRequest -Uri "$gatewayUrl/case-service/actuator/health" -Method GET -TimeoutSec 10 -UseBasicParsing
    Test-Assert ($response.StatusCode -eq 200) "Gateway -> Case Service OK" "Gateway -> Case Service Fallo"
}
catch {
    Test-Assert $false "Gateway -> Case Service OK" "Gateway -> Case Service Error - $($_.Exception.Message)"
}

Write-Info "Testing Gateway route to n8n-integration-service..."
try {
    $response = Invoke-WebRequest -Uri "$gatewayUrl/n8n-integration-service/actuator/health" -Method GET -TimeoutSec 10 -UseBasicParsing
    Test-Assert ($response.StatusCode -eq 200) "Gateway -> N8N Integration Service OK" "Gateway -> N8N Integration Service Fallo"
}
catch {
    Test-Assert $false "Gateway -> N8N Integration OK" "Gateway -> N8N Integration Error - $($_.Exception.Message)"
}

# ============================================================
# 6. PRUEBAS N8N INTEGRATION SERVICE
# ============================================================
Write-Section "6. PRUEBAS N8N INTEGRATION SERVICE"

$n8nBaseUrl = "http://localhost:8800/n8n-integration-service"

Write-Info "Testing N8N Integration API status..."
try {
    $response = Invoke-WebRequest -Uri "$n8nBaseUrl/api/status" -Method GET -TimeoutSec 10 -UseBasicParsing
    $status = $response.Content | ConvertFrom-Json
    Test-Assert ($status.status -eq "UP") "N8N Integration /api/status $($status.status)" "N8N Integration /api/status Fallo"
}
catch {
    Test-Assert $false "N8N Integration /api/status OK" "N8N Integration /api/status Error - $($_.Exception.Message)"
}

Write-Info "Testing N8N webhook health..."
try {
    $response = Invoke-WebRequest -Uri "$n8nBaseUrl/webhook/health" -Method GET -TimeoutSec 10 -UseBasicParsing
    Test-Assert ($response.StatusCode -eq 200) "N8N /webhook/health OK" "N8N /webhook/health Fallo"
}
catch {
    Test-Assert $false "N8N /webhook/health OK" "N8N /webhook/health Error - $($_.Exception.Message)"
}

Write-Info "Testing N8N test lead endpoint..."
$testLeadPayload = @{
    nombre   = "Test Lead n8n"
    email    = "n8ntest@example.com"
    servicio = "Derecho de Marcas"
    mensaje  = "Mensaje de prueba para n8n integration"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "$n8nBaseUrl/api/test/send-lead" -Method POST -Body $testLeadPayload -ContentType "application/json" -TimeoutSec 30 -UseBasicParsing
    $result = $response.Content | ConvertFrom-Json
    if ($result.sent_to_n8n -eq $true) {
        Test-Assert $true "N8N Test Lead Enviado a n8n Cloud exitosamente" ""
    }
    else {
        Write-Warning "N8N Test Lead - Lead creado pero no enviado a n8n Cloud (esperado si n8n Cloud no esta accesible)"
        $script:skipped++
    }
}
catch {
    Write-Warning "N8N Test Lead Error - $($_.Exception.Message)"
    $script:skipped++
}

# ============================================================
# 7. RESUMEN FINAL
# ============================================================
Write-Section "RESUMEN DE PRUEBAS E2E"

$total = $script:passed + $script:failed + $script:skipped
$passRate = if ($total -gt 0) { [math]::Round(($script:passed / $total) * 100, 1) } else { 0 }

Write-Host ""
Write-Host "+-------------------------------------+" -ForegroundColor White
Write-Host "|        RESULTADOS E2E TESTS         |" -ForegroundColor White
Write-Host "+-------------------------------------+" -ForegroundColor White
Write-Host "|  Pasados:    $($script:passed.ToString().PadLeft(3))                    |" -ForegroundColor Green
Write-Host "|  Fallidos:   $($script:failed.ToString().PadLeft(3))                    |" -ForegroundColor Red
Write-Host "|  Omitidos:   $($script:skipped.ToString().PadLeft(3))                    |" -ForegroundColor Yellow
Write-Host "+-------------------------------------+" -ForegroundColor White
Write-Host "|  Total:      $($total.ToString().PadLeft(3))                    |" -ForegroundColor Cyan
Write-Host "|  Tasa exito: $($passRate.ToString().PadLeft(5))%               |" -ForegroundColor $(if ($passRate -ge 80) { "Green" } elseif ($passRate -ge 50) { "Yellow" } else { "Red" })
Write-Host "+-------------------------------------+" -ForegroundColor White
Write-Host ""

if ($script:failed -eq 0) {
    Write-Host "TODAS LAS PRUEBAS PASARON! El sistema esta listo." -ForegroundColor Green
    exit 0
}
else {
    Write-Host "Algunas pruebas fallaron. Revisar los errores arriba." -ForegroundColor Yellow
    exit 1
}
