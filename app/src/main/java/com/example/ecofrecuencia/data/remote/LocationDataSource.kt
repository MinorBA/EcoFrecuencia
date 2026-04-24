// Archivo: data/remote/LocationDataSource.kt
package com.example.ecofrecuencia.data.remote

interface LocationDataSource {
    suspend fun getCurrentLocation(): Result<Pair<Double, Double>>
}
