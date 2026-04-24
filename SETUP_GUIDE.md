## COMANDOS POWERSHELL PARA CREAR ESTRUCTURA DE DIRECTORIOS

### Opción 1: Crear estructura completa (ejecutar una sola vez)
```powershell
$basePath = "C:\Users\minor\AndroidStudioProjects\EcoFrecuencia\app\src\main\java\com\example\ecofrecuencia"

$directories = @(
    "$basePath\data\remote",
    "$basePath\data\repository",
    "$basePath\domain\model",
    "$basePath\domain\usecase",
    "$basePath\domain\repository",
    "$basePath\presentation\ui",
    "$basePath\presentation\viewmodel",
    "$basePath\di"
)

foreach ($dir in $directories) {
    New-Item -ItemType Directory -Path $dir -Force | Out-Null
}

Write-Host "Estructura de directorios creada exitosamente"
```

### Opción 2: Crear directorios individuales (si necesitas agregar más después)
```powershell
# Ejemplo para agregar un nuevo paquete
$basePath = "C:\Users\minor\AndroidStudioProjects\EcoFrecuencia\app\src\main\java\com\example\ecofrecuencia"
New-Item -ItemType Directory -Path "$basePath\utils" -Force | Out-Null
```

## ESTRUCTURA FINAL DEL PROYECTO

```
com/example/ecofrecuencia/
├── data/
│   ├── remote/              # API services (Retrofit)
│   └── repository/          # Repository implementations
├── domain/
│   ├── model/              # Data models / Entities
│   ├── repository/         # Repository interfaces (contracts)
│   └── usecase/            # Business logic / UseCases
├── presentation/
│   ├── ui/                 # Composables UI components
│   └── viewmodel/          # ViewModels for UI state management
├── di/                     # Dependency Injection container
├── ui/theme/               # Theme & styling (ya existía)
├── MainActivity.kt
├── EcoFrecuenciaApp.kt     # Application class
```

## ARCHIVOS CONFIGURADOS

✅ build.gradle.kts (Module :app)
   - Plugin: kotlin-serialization
   - Kotlin Coroutines: 1.9.1
   - Lifecycle ViewModel: 2.8.6
   - kotlinx-serialization: 1.7.1
   - Retrofit: 2.11.0
   - OkHttp: 4.12.0

✅ build.gradle.kts (Project)
   - Plugin: kotlin-serialization habilitado

✅ AndroidManifest.xml
   - INTERNET
   - ACCESS_NETWORK_STATE
   - ACCESS_FINE_LOCATION
   - ACCESS_COARSE_LOCATION
   - Clase Application: EcoFrecuenciaApp

✅ Inyección de Dependencias (Manual - Sin Hilt)
   - AppContainer.kt: Singleton pattern
   - EcoFrecuenciaApp.kt: Application initialization
   - Soporte nativo para Json, OkHttpClient y Retrofit

## PRÓXIMOS PASOS

1. Sincronizar Gradle en Android Studio (Build → Clean Project)
2. Crear modelos de datos en domain/model/
3. Implementar endpoints en data/remote/ApiService.kt
4. Registrar servicios en AppContainer.kt
5. Crear UseCases en domain/usecase/
6. Implementar repositorios en data/repository/
7. Crear ViewModels en presentation/viewmodel/
8. Desarrollar UI Composables en presentation/ui/

