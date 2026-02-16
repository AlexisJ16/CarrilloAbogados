#!/usr/bin/env pwsh
# ============================================================================
# Carrillo Abogados - Jib Build Script
# Construye imágenes Docker sin Dockerfile usando Google Jib
#
# Uso:
#   .\scripts\jib-build.ps1                    # Construir todos los servicios
#   .\scripts\jib-build.ps1 -Service api-gateway  # Construir uno específico
#   .\scripts\jib-build.ps1 -SkipLoad          # Solo generar tarballs
#
# Última Actualización: 13 de Febrero, 2026 - 00:30 COT
# ============================================================================

param(
    [string]$Service = "",
    [switch]$SkipLoad,
    [switch]$Help
)

if ($Help) {
    Write-Host @"
Carrillo Abogados - Jib Build Script
=====================================
Construye imágenes Docker optimizadas usando Google Jib (sin Dockerfile).

Parámetros:
  -Service <nombre>  Construir solo un servicio específico
  -SkipLoad          Solo generar tarballs, no cargar a Docker daemon
  -Help              Mostrar esta ayuda

Servicios disponibles:
  api-gateway, client-service, case-service, payment-service,
  document-service, calendar-service, notification-service,
  n8n-integration-service

Ejemplos:
  .\scripts\jib-build.ps1                       # Todos los servicios
  .\scripts\jib-build.ps1 -Service api-gateway  # Solo api-gateway
  .\scripts\jib-build.ps1 -SkipLoad             # Solo tarballs
"@
    exit 0
}

$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot

# Buscar Maven
$mavenHome = Get-ChildItem "$env:USERPROFILE\.m2\wrapper\dists" -Recurse -Filter "mvn.cmd" -ErrorAction SilentlyContinue |
Select-Object -First 1 -ExpandProperty DirectoryName |
Split-Path -Parent

if (-not $mavenHome) {
    Write-Host "ERROR: Maven no encontrado. Ejecuta '.\mvnw.cmd --version' primero." -ForegroundColor Red
    exit 1
}

$classworlds = (Get-ChildItem "$mavenHome\boot" -Filter "plexus-classworlds*.jar")[0].FullName

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Carrillo Abogados - Jib Build" -ForegroundColor Cyan
Write-Host " Maven: $mavenHome" -ForegroundColor DarkGray
Write-Host "========================================" -ForegroundColor Cyan

# Construir argumentos Maven
$mvnArgs = @("jib:buildTar", "-DskipTests", "-B")
if ($Service) {
    $mvnArgs += "-pl"
    $mvnArgs += $Service
    Write-Host "`nConstruyendo: $Service" -ForegroundColor Yellow
}
else {
    Write-Host "`nConstruyendo: TODOS los servicios" -ForegroundColor Yellow
}

# Ejecutar Jib buildTar
Push-Location $projectRoot
try {
    $javaArgs = @(
        "-Dclassworlds.conf=$mavenHome\bin\m2.conf",
        "-Dmaven.home=$mavenHome",
        "-Dmaven.multiModuleProjectDirectory=.",
        "-cp", $classworlds,
        "org.codehaus.plexus.classworlds.launcher.Launcher"
    ) + $mvnArgs

    & java @javaArgs
    if ($LASTEXITCODE -ne 0) {
        Write-Host "`nERROR: Jib build failed" -ForegroundColor Red
        exit 1
    }
    Write-Host "`nTarballs generados exitosamente" -ForegroundColor Green

    # Cargar tarballs al daemon Docker
    if (-not $SkipLoad) {
        Write-Host "`nCargando imágenes al daemon Docker..." -ForegroundColor Yellow

        $services = if ($Service) { @($Service) } else {
            @("api-gateway", "client-service", "case-service",
                "payment-service", "document-service", "calendar-service",
                "notification-service", "n8n-integration-service")
        }

        $loaded = 0
        foreach ($svc in $services) {
            $tar = "$svc\target\jib-image.tar"
            if (Test-Path $tar) {
                Write-Host "  Loading $svc..." -NoNewline -ForegroundColor DarkGray
                $result = docker load -i $tar 2>&1
                if ($result -match "Loaded") {
                    Write-Host " OK" -ForegroundColor Green
                    $loaded++
                }
                else {
                    Write-Host " FAILED: $result" -ForegroundColor Red
                }
            }
        }

        Write-Host "`n$loaded/$($services.Count) imágenes cargadas exitosamente" -ForegroundColor Green
        Write-Host "`nPróximo paso: docker compose up -d --pull never" -ForegroundColor Cyan
    }
}
finally {
    Pop-Location
}
