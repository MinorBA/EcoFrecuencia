package com.example.ecofrecuencia.domain.repository

import com.example.ecofrecuencia.domain.model.Bird
import com.example.ecofrecuencia.domain.model.Coordinates

interface BirdRepository {
    suspend fun getNearbyBirds(coordinates: Coordinates): Result<List<Bird>>
}
