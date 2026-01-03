# Script to set Java 21 for this project session
# Usage: . .\scripts\set-java21.ps1

$java21Path = "C:\Program Files\Eclipse Adoptium\jdk-21.0.9.10-hotspot"

if (Test-Path $java21Path) {
    $env:JAVA_HOME = $java21Path
    $env:PATH = "$java21Path\bin;$env:PATH"
    Write-Host "✅ Java 21 configured successfully" -ForegroundColor Green
    Write-Host "   JAVA_HOME: $env:JAVA_HOME" -ForegroundColor Cyan
    java -version
}
else {
    Write-Host "❌ Java 21 not found at: $java21Path" -ForegroundColor Red
    Write-Host "   Please install Eclipse Temurin 21:" -ForegroundColor Yellow
    Write-Host "   winget install EclipseAdoptium.Temurin.21.JDK" -ForegroundColor Yellow
}
