package com.example.ecofrecuencia.di

import android.content.Context
import com.example.ecofrecuencia.data.remote.FusedLocationDataSource
import com.example.ecofrecuencia.data.remote.HttpUrlConnectionDataSource
import com.example.ecofrecuencia.data.remote.LocationDataSource
import com.example.ecofrecuencia.data.remote.NetworkDataSource
import com.example.ecofrecuencia.data.repository.BirdRepositoryImpl
import com.example.ecofrecuencia.domain.repository.BirdRepository
import com.example.ecofrecuencia.domain.usecase.GetNearbyBirdsUseCase
import com.example.ecofrecuencia.presentation.viewmodel.BirdViewModel
import com.example.ecofrecuencia.presentation.utils.AudioPlayerHelper
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Container manual para inyección de dependencias.
 * Proporciona instancias únicas (singleton) de servicios principales.
 */
class AppContainer private constructor(private val context: Context) {

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
            prettyPrint = true
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    // ==================== HTTP Client ====================
    val httpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // ==================== Retrofit ====================
    fun createRetrofitInstance(baseUrl: String): Retrofit {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(jsonFormatter.asConverterFactory(contentType))
            .build()
    }

    // ==================== Data Sources ====================
    val networkDataSource: NetworkDataSource by lazy {
        HttpUrlConnectionDataSource(jsonFormatter)
    }

    val locationDataSource: LocationDataSource by lazy {
        FusedLocationDataSource(context)
    }

    // ==================== Repositories ====================
    val birdRepository: BirdRepository by lazy {
        BirdRepositoryImpl(networkDataSource)
    }

    // ==================== Use Cases ====================
    val getNearbyBirdsUseCase: GetNearbyBirdsUseCase by lazy {
        GetNearbyBirdsUseCase(locationDataSource, birdRepository)
    }

    // ==================== ViewModels ====================
    val birdViewModel: BirdViewModel by lazy {
        BirdViewModel(getNearbyBirdsUseCase)
    }

    // ==================== Utils ====================
    val audioPlayerHelper: AudioPlayerHelper by lazy {
        AudioPlayerHelper(context)
    }
}
