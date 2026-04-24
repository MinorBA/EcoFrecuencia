// Archivo: com/example/ecofrecuencia/EcoApplication.kt
package com.example.ecofrecuencia

import android.app.Application
import com.example.ecofrecuencia.di.AppContainer

class EcoApplication : Application() {

    val appContainer: AppContainer by lazy {
        AppContainer.getInstance(this)
    }
}
