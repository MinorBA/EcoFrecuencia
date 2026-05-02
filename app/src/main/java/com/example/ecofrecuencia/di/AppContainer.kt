package com.example.ecofrecuencia.di

import android.content.Context
import com.example.ecofrecuencia.data.remote.FusedLocationDataSource
import com.example.ecofrecuencia.data.remote.HttpUrlConnectionDataSource
import com.example.ecofrecuencia.data.remote.NetworkDataSource
import com.example.ecofrecuencia.data.remote.WikipediaImageDataSource
import com.example.ecofrecuencia.data.repository.BirdRepositoryImpl
import com.example.ecofrecuencia.domain.datasource.LocationDataSource
import com.example.ecofrecuencia.domain.repository.BirdRepository
import com.example.ecofrecuencia.domain.usecase.GetNearbyBirdsUseCase
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

    // ==================== Data Sources ====================
    val networkDataSource: NetworkDataSource by lazy {
        HttpUrlConnectionDataSource(jsonFormatter)
    }

    val locationDataSource: LocationDataSource by lazy {
        FusedLocationDataSource(appContext)
    }

    // ==================== Remote Data Sources ====================
    val wikipediaImageDataSource: WikipediaImageDataSource by lazy {
        WikipediaImageDataSource()
    }

    // ==================== Repositories ====================
    val birdRepository: BirdRepository by lazy {
        BirdRepositoryImpl(networkDataSource, wikipediaImageDataSource)
    }

    // ==================== Use Cases ====================
    val getNearbyBirdsUseCase: GetNearbyBirdsUseCase by lazy {
        GetNearbyBirdsUseCase(locationDataSource, birdRepository)
    }

    // ==================== Factories ====================
    val birdViewModelFactory: BirdViewModelFactory by lazy {
        BirdViewModelFactory(getNearbyBirdsUseCase, audioPlayerHelper)
    }

    // ==================== Utils ====================
    val audioPlayerHelper: AudioPlayerHelper by lazy {
        AudioPlayerHelper(appContext)
    }
}