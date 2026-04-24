// Archivo: data/remote/BirdResponseDto.kt
package com.example.ecofrecuencia.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class BirdResponseDto(
    val recordings: List<BirdRecordDto>
)
