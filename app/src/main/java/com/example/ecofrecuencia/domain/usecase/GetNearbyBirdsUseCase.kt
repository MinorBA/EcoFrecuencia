// Archivo: domain/usecase/GetNearbyBirdsUseCase.kt
package com.example.ecofrecuencia.domain.usecase

import com.example.ecofrecuencia.data.remote.LocationDataSource
import com.example.ecofrecuencia.domain.model.Bird
import com.example.ecofrecuencia.domain.repository.BirdRepository

class GetNearbyBirdsUseCase(
    private val locationDataSource: LocationDataSource,
    private val birdRepository: BirdRepository
) {
    suspend operator fun invoke(): Result<List<Bird>> {
        return locationDataSource.getCurrentLocation().fold(
            onSuccess = { (lat, lng) ->
                birdRepository.getNearbyBirds(lat, lng)
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}
