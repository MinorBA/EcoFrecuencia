// Archivo: com/example/ecofrecuencia/EcoApplication.kt
package com.example.ecofrecuencia

import android.app.Application
import com.example.ecofrecuencia.di.AppContainer
import coil.Coil
import coil.ImageLoader
import okhttp3.OkHttpClient

class EcoApplication : Application() {

    val appContainer: AppContainer by lazy {
        AppContainer.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()
        
        // Configuramos Coil con el cliente centralizado que tiene límites de concurrencia
        // para evitar el error HTTP 429 de Wikimedia.
        val imageLoader = ImageLoader.Builder(this)
            .okHttpClient { 
                // Accedemos indirectamente para evitar ciclos si fuera necesario, 
                // o simplemente usamos un cliente con Dispatcher limitado.
                okhttp3.OkHttpClient.Builder()
                    .dispatcher(okhttp3.Dispatcher().apply {
                        maxRequestsPerHost = 3 // Máximo 3 descargas simultáneas
                    })
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .header("User-Agent", "EcoFrecuenciaApp/1.2 (https://github.com/MinoBA/EcoFrecuencia; contacto@ecofrecuencia.com)")
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            }
            .crossfade(true)
            .build()
        
        Coil.setImageLoader(imageLoader)
    }
}
