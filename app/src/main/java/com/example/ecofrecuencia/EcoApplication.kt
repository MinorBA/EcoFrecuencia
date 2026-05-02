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
        Coil.setImageLoader(
            ImageLoader.Builder(this)
                .okHttpClient(
                    OkHttpClient.Builder()
                        .followRedirects(true)
                        .followSslRedirects(true)
                        .addInterceptor { chain ->
                            val request = chain.request().newBuilder()
                                .header("User-Agent", "EcoFrecuencia/1.0")
                                .build()
                            chain.proceed(request)
                        }
                        .build()
                )
                .build()
        )
    }
}
