// Archivo: domain/usecase/GetNearbyBirdsUseCase.kt
package com.example.ecofrecuencia.domain.usecase

import com.example.ecofrecuencia.domain.repository.LocationRepository
import com.example.ecofrecuencia.domain.model.Bird
import com.example.ecofrecuencia.domain.repository.BirdRepository

class GetNearbyBirdsUseCase(
    private val locationRepository: LocationRepository,
    private val birdRepository: BirdRepository
) {
    suspend operator fun invoke(): Result<List<Bird>> {
        return locationRepository.getCurrentLocation().fold(
            onSuccess = { coordinates ->
                birdRepository.getNearbyBirds(coordinates)
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}
