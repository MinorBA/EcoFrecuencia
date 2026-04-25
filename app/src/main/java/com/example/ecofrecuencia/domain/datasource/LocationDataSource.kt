package com.example.ecofrecuencia.domain.datasource

interface LocationDataSource {
    suspend fun getCurrentLocation(): Result<Pair<Double, Double>>
}
