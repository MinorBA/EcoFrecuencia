// Archivo: data/repository/BirdRepositoryImpl.kt
package com.example.ecofrecuencia.data.repository

import com.example.ecofrecuencia.data.remote.BirdRecordDto
import com.example.ecofrecuencia.data.remote.NetworkDataSource
import com.example.ecofrecuencia.domain.model.Bird
import com.example.ecofrecuencia.domain.repository.BirdRepository

class BirdRepositoryImpl(
    private val networkDataSource: NetworkDataSource
) : BirdRepository {

    override suspend fun getNearbyBirds(lat: Double, lng: Double): Result<List<Bird>> {
        return networkDataSource.getBirdsNearby(lat, lng).map { response ->
            response.recordings.map { it.toDomain() }
        }
    }

    private fun BirdRecordDto.toDomain(): Bird {
        return Bird(
            id = id,
            name = en,
            location = loc,
            audioUrl = file
        )
    }
}
