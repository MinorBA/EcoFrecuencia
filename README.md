# EcoFrecuencia 🐦

Aplicación **Android** que usa la ubicación GPS del teléfono para mostrar las **aves y otros animales grabados cerca** del usuario. Cada especie trae su nombre, el lugar de la grabación, una fotografía y su **canto**, que se puede reproducir desde la app.

Se desarrolló para el curso EIF411 Diseño y Programación de Plataformas Móviles de la Universidad Nacional de Costa Rica (2026).

<!-- Capturas: agregar docs/captura-lista.png y docs/captura-detalle.png y mostrarlas aquí con <img src="docs/..." width="260"> -->

## Funcionalidades

- Detecta la ubicación del dispositivo con `FusedLocationProviderClient` y pide los permisos en tiempo de ejecución.
- Consulta la API pública de **Xeno-Canto**, una base mundial de grabaciones de fauna, para obtener las especies grabadas cerca.
- Busca la fotografía de cada especie en la **API de Wikipedia**.
- Reproduce los cantos con `MediaPlayer`.
- Maneja los estados de carga, error y lista vacía en la interfaz.

## Arquitectura

La app sigue **Clean Architecture** en tres capas, con **MVVM** en la presentación:

```
app/src/main/java/com/example/ecofrecuencia/
├── data/            # DTOs, fuentes de datos (Xeno-Canto, Wikipedia, GPS) e implementación del repositorio
├── domain/          # Modelos (Bird, Coordinates), interfaces de repositorio y el caso de uso GetNearbyBirdsUseCase
├── presentation/    # BirdScreen (Jetpack Compose), BirdViewModel con StateFlow y el reproductor de audio
└── di/AppContainer  # Inyección de dependencias manual (singleton)
```

**Flujo:** la UI le pide los datos al `BirdViewModel`. Este ejecuta `GetNearbyBirdsUseCase`, que obtiene la ubicación, consulta Xeno-Canto y Wikipedia y convierte los DTOs en modelos de dominio. Al final se actualiza el `StateFlow` y Compose vuelve a dibujar la pantalla.

## Tecnologías

Kotlin, Jetpack Compose (Material 3), corrutinas, ViewModel, StateFlow, kotlinx.serialization, OkHttp, Google Play Services Location y MediaPlayer. Requiere Android 7.0 o superior (minSdk 24).

## Cómo ejecutarlo

1. Clona el repositorio y ábrelo en Android Studio.
2. Pide una clave gratuita de la API de Xeno-Canto y agrégala a `local.properties`:
   ```properties
   XENO_CANTO_KEY=tu_clave
   ```
3. Ejecuta la app en un dispositivo o emulador con ubicación activada.

## Equipo

- Minor Brenes Aguilar – [@MinorBA](https://github.com/MinorBA): estructura inicial del proyecto, integración con la API de Wikipedia y limpieza del código.
- Jordan Alvarez – [@JordanJazz24](https://github.com/JordanJazz24): interfaz y experiencia de usuario, imágenes y dependencias.
