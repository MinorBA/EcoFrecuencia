package com.example.ecofrecuencia.di

import android.content.Context
import com.example.ecofrecuencia.data.remote.FusedLocationRepository
import com.example.ecofrecuencia.data.remote.HttpUrlConnectionDataSource
import com.example.ecofrecuencia.data.remote.NetworkDataSource
import com.example.ecofrecuencia.data.remote.WikipediaImageDataSource
import com.example.ecofrecuencia.data.repository.BirdRepositoryImpl
import com.example.ecofrecuencia.domain.repository.LocationRepository
import com.example.ecofrecuencia.domain.repository.BirdRepository
import com.example.ecofrecuencia.domain.usecase.GetNearbyBirdsUseCase
import com.example.ecofrecuencia.presentation.utils.AudioPlayer
import com.example.ecofrecuencia.presentation.utils.AudioPlayerHelper
import com.example.ecofrecuencia.presentation.viewmodel.BirdViewModelFactory
import kotlinx.serialization.json.Json

/**
 * Container manual para inyección de dependencias.
 * Proporciona instancias únicas (singleton) de servicios principales.
 */
class AppContainer private constructor(context: Context) {

    private val appContext = context.applicationContext

    companion object {
        @Volatile
        private var instance: AppContainer? = null

        fun getInstance(context: Context): AppContainer {
            return instance ?: synchronized(this) {
                AppContainer(context).also { instance = it }
            }
        }
    }

    // ==================== JSON Serialization ====================
    val jsonFormatter: Json by lazy {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    // ==================== Network Client (Coil & API) ====================
    // Limitamos la concurrencia global para evitar HTTP 429 de Wikimedia
    private val okHttpClient: okhttp3.OkHttpClient by lazy {
        okhttp3.OkHttpClient.Builder()
            .dispatcher(okhttp3.Dispatcher().apply {
                maxRequestsPerHost = 3 // No más de 3 descargas simultáneas a Wikimedia
            })
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "EcoFrecuencia/1.2 (contacto@ecofrecuencia.com; Android)")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    // ==================== Data Sources ====================
    val networkDataSource: NetworkDataSource by lazy {
        HttpUrlConnectionDataSource(jsonFormatter)
    }

    val locationRepository: LocationRepository by lazy {
        FusedLocationRepository(appContext)
    }

    // ==================== Remote Data Sources ====================
    val wikipediaImageDataSource: WikipediaImageDataSource by lazy {
        WikipediaImageDataSource(jsonFormatter)
    }

    // ==================== Repositories ====================
    val birdRepository: BirdRepository by lazy {
        BirdRepositoryImpl(networkDataSource, wikipediaImageDataSource)
    }

    // ==================== Use Cases ====================
    val getNearbyBirdsUseCase: GetNearbyBirdsUseCase by lazy {
        GetNearbyBirdsUseCase(locationRepository, birdRepository)
    }

    // ==================== Factories ====================
    val birdViewModelFactory: BirdViewModelFactory by lazy {
        BirdViewModelFactory(getNearbyBirdsUseCase, audioPlayer)
    }

    // ==================== Utils ====================
    private val audioPlayer: AudioPlayer by lazy {
        AudioPlayerHelper(appContext)
    }
}