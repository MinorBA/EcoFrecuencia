// Archivo: data/remote/BirdRecordDto.kt
package com.example.ecofrecuencia.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BirdRecordDto(
    val id: String,
    val en: String, // Nombre en inglés
    val loc: String, // Lugar
    val file: String, // URL del audio
    val gen: String = "", // Género
    val sp: String = "" // especie

)
