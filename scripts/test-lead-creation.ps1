# Test Lead Creation Script
$uri = "http://localhost:8200/client-service/api/leads"

$body = @{
    nombre   = "Test Usuario Demo"
    email    = "demo.test@empresa.com"
    telefono = "+573001234567"
    empresa  = "Empresa Demo SAS"
    cargo    = "CEO"
    servicio = "derecho-marcas"
    mensaje  = "Necesito registrar mi marca con alta prioridad. Tengo una empresa grande y necesito hacer esto rapido."
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri $uri -Method POST -Body $body -ContentType "application/json"
    Write-Host "✅ Lead creado exitosamente:" -ForegroundColor Green
    Write-Host "Lead ID: $($response.leadId)"
    Write-Host "Nombre: $($response.nombre)"
    Write-Host "Email: $($response.email)"
    Write-Host "Score: $($response.leadScore)"
    Write-Host "Category: $($response.leadCategory)"
}
catch {
    Write-Host "❌ Error al crear lead:" -ForegroundColor Red
    Write-Host $_.Exception.Message
}
