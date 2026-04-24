// Archivo: domain/repository/BirdRepository.kt
package com.example.ecofrecuencia.domain.repository

import com.example.ecofrecuencia.domain.model.Bird

interface BirdRepository {
    suspend fun getNearbyBirds(lat: Double, lng: Double): Result<List<Bird>>
}
