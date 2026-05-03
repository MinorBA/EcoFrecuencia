// Archivo: data/remote/BirdRecordDto.kt
package com.example.ecofrecuencia.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BirdRecordDto(
    val id: String,
    @SerialName("en") val englishName: String? = "Unknown Bird",
    @SerialName("loc") val location: String? = "Unknown Location",
    @SerialName("file") val audioUrl: String,
    val gen: String = "",
    val sp: String = ""
)
