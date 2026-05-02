package com.example.ecofrecuencia.data.repository

import com.example.ecofrecuencia.data.remote.BirdRecordDto
import com.example.ecofrecuencia.data.remote.NetworkDataSource
import com.example.ecofrecuencia.data.remote.WikipediaImageDataSource
import com.example.ecofrecuencia.domain.model.Bird
import com.example.ecofrecuencia.domain.repository.BirdRepository

class BirdRepositoryImpl(
    private val networkDataSource: NetworkDataSource,
    private val imageDataSource: WikipediaImageDataSource
) : BirdRepository {

    override suspend fun getNearbyBirds(lat: Double, lng: Double): Result<List<Bird>> {
        return networkDataSource.getBirdsNearby(lat, lng).map { response ->
            response.recordings.map { dto ->
                val scientificName = "${dto.gen} ${dto.sp}".trim()

                val imageUrl = if (scientificName.isNotBlank()) {
                    imageDataSource.getImageUrl(scientificName)
                } else null

                dto.toDomain(imageUrl)
            }
        }
    }

    private fun BirdRecordDto.toDomain(imageUrl: String?): Bird {
        return Bird(
            id = id,
            name = en,
            location = loc,
            audioUrl = file,
            imageUrl = imageUrl
        )
    }
}
