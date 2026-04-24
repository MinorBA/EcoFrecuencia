// Archivo: data/remote/NetworkDataSource.kt
package com.example.ecofrecuencia.data.remote

interface NetworkDataSource {
    suspend fun getBirdsNearby(lat: Double, lng: Double): Result<BirdResponseDto>
}
