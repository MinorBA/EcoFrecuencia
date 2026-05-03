package com.example.ecofrecuencia.data.repository

import com.example.ecofrecuencia.data.remote.BirdRecordDto
import com.example.ecofrecuencia.data.remote.NetworkDataSource
import com.example.ecofrecuencia.data.remote.WikipediaImageDataSource
import com.example.ecofrecuencia.domain.model.Bird
import com.example.ecofrecuencia.domain.repository.BirdRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

import com.example.ecofrecuencia.domain.model.Coordinates

class BirdRepositoryImpl(
    private val networkDataSource: NetworkDataSource,
    private val imageDataSource: WikipediaImageDataSource
) : BirdRepository {

    override suspend fun getNearbyBirds(coordinates: Coordinates): Result<List<Bird>> {
        return networkDataSource.getBirdsNearby(coordinates.latitude, coordinates.longitude).map { response ->
            coroutineScope {
                val imageCache = mutableMapOf<String, String?>()
                
                // 1. Filtramos para tener solo una grabación por especie (evita repetidos en la UI)
                val uniqueRecordings = response.recordings
                    .filter { it.gen.isNotBlank() && it.sp.isNotBlank() }
                    .distinctBy { "${it.gen} ${it.sp}" }

                // 2. Procesamos en lotes de 5 para las imágenes
                val birds = uniqueRecordings.chunked(5).flatMapIndexed { index, chunk ->
                    if (index > 0) kotlinx.coroutines.delay(300) // 300ms de pausa entre lotes de 5
                    chunk.map { dto ->
                        async {
                            val scientificName = "${dto.gen} ${dto.sp}".trim()
                            
                            // Verificar caché primero
                            val cachedUrl = synchronized(imageCache) { imageCache[scientificName] }
                            val hasKey = synchronized(imageCache) { imageCache.containsKey(scientificName) }
                            
                            if (hasKey) {
                                return@async dto.toDomain(cachedUrl)
                            }

                            // Si llegamos aquí, buscamos en la red
                            val url = imageDataSource.getImageUrl(scientificName, dto.englishName)
                            
                            synchronized(imageCache) { 
                                imageCache[scientificName] = url 
                            }
                            
                            dto.toDomain(url)
                        }
                    }.awaitAll()
                }
                android.util.Log.d("Repo", "Total de aves procesadas con imágenes: ${birds.size}")
                birds
            }
        }
    }

    private fun BirdRecordDto.toDomain(imageUrl: String?): Bird {
        return Bird(
            id = id,
            name = englishName ?: "Unknown Bird",
            location = location ?: "Unknown Location",
            audioUrl = audioUrl,
            imageUrl = imageUrl
        )
    }
}
