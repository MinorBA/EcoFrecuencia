/**
 * CONFIGURACIÓN INICIAL - ECOFRECUENCIA PROJECT
 * 
 * DEPENDENCIAS INSTALADAS:
 * 
 * ✅ KOTLIN COROUTINES (v1.9.1)
 *    - kotlinx.coroutines.core
 *    - kotlinx.coroutines.android
 *    Uso: Operaciones asincrónicas y manejo de threads
 * 
 * ✅ LIFECYCLE VIEWMODEL KTX (v2.8.6)
 *    - androidx.lifecycle.viewmodel.ktx
 *    Uso: MVVM architecture, estado UI persistente
 * 
 * ✅ KOTLINX SERIALIZATION (v1.7.1)
 *    - org.jetbrains.kotlinx.serialization-json
 *    Uso: Serialización/deserialización JSON sin reflection
 * 
 * ✅ RETROFIT (v2.11.0)
 *    - com.squareup.retrofit2.retrofit
 *    - com.squareup.retrofit2.converter.kotlinx.serialization
 *    Uso: Cliente HTTP type-safe para APIs REST
 * 
 * ✅ OKHTTP (v4.12.0)
 *    - com.squareup.okhttp3.okhttp
 *    - com.squareup.okhttp3.logging-interceptor
 *    Uso: HTTP client con interceptores y logging
 * 
 * PLUGINS HABILITADOS:
 *    - org.jetbrains.kotlin.plugin.serialization (v2.2.10)
 * 
 * PERMISOS ANDROID:
 *    - android.permission.INTERNET
 *    - android.permission.ACCESS_NETWORK_STATE
 *    - android.permission.ACCESS_FINE_LOCATION
 *    - android.permission.ACCESS_COARSE_LOCATION
 * 
 * INYECCIÓN DE DEPENDENCIAS:
 *    Implementación manual (sin Hilt/Dagger)
 *    Ubicación: com.example.ecofrecuencia.di.AppContainer
 *    Pattern: Singleton thread-safe
 *    
 *    Uso en Activities:
 *    ─────────────────────────────────────────
 *    val container = EcoFrecuenciaApp.appContainer
 *    val apiService = container.createRetrofitInstance(...).create(ApiService::class.java)
 *    ─────────────────────────────────────────
 * 
 * CLEAN ARCHITECTURE - ESTRUCTURA:
 * 
 *    📦 data/
 *       └── remote/          → API interfaces (Retrofit)
 *       └── repository/      → Repository implementations
 * 
 *    📦 domain/
 *       └── model/          → Entities / Data Classes
 *       └── repository/     → Repository interfaces (contracts)
 *       └── usecase/        → Business logic layer
 * 
 *    📦 presentation/
 *       └── ui/             → Composable components
 *       └── viewmodel/      → MVVM ViewModel classes
 * 
 *    📦 di/
 *       └── AppContainer.kt → Service locator / DI Container
 * 
 * APPLICATION CLASS:
 *    - EcoFrecuenciaApp.kt (registrado en AndroidManifest.xml)
 *    - Inicializa AppContainer al lanzar la app
 * 
 * VERSIÓN MÍNIMA ANDROID: API 24 (Android 7.0)
 * VERSIÓN DESTINO: API 36 (Android 15)
 * JAVA COMPATIBILITY: 11
 * 
 * GRADLE BUILD TOOLS: 9.1.1
 * KOTLIN VERSION: 2.2.10
 * COMPOSE BOM: 2024.09.00
 */

