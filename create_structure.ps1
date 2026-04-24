# Script PowerShell para crear estructura de directorios de EcoFrecuencia
# Ejecutar desde: C:\Users\minor\AndroidStudioProjects\EcoFrecuencia\

param(
    [string]$BasePath = "app/src/main/java/com/example/ecofrecuencia"
)

$directories = @(
    "$BasePath/data/remote",
    "$BasePath/data/repository",
    "$BasePath/domain/model",
    "$BasePath/domain/usecase",
    "$BasePath/domain/repository",
    "$BasePath/presentation/ui",
    "$BasePath/presentation/viewmodel",
    "$BasePath/di"
)

Write-Host "═══════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "  Creando estructura de directorios - EcoFrecuencia" -ForegroundColor Cyan
Write-Host "═══════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

foreach ($dir in $directories) {
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
        Write-Host "✅ Creado: $dir" -ForegroundColor Green
    } else {
        Write-Host "ℹ️  Ya existe: $dir" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "═══════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "  ✅ Estructura de directorios completada" -ForegroundColor Green
Write-Host "═══════════════════════════════════════════════════════" -ForegroundColor Cyan

