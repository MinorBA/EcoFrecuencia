package com.example.ecofrecuencia

import android.app.Application
import com.example.ecofrecuencia.di.AppContainer

/**
 * Clase Application principal que inicializa el contenedor de dependencias.
 */
class EcoFrecuenciaApp : Application() {

    companion object {
        lateinit var appContainer: AppContainer
            private set
        
        lateinit var appContext: android.content.Context
    }

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer.getInstance(this)
        appContext = this
    }
}
