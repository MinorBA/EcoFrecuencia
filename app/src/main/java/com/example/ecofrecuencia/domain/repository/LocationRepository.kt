package com.example.ecofrecuencia.domain.repository

import com.example.ecofrecuencia.domain.model.Coordinates

interface LocationRepository {
    suspend fun getCurrentLocation(): Result<Coordinates>
}