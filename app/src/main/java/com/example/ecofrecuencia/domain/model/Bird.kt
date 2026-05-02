// Archivo: domain/model/Bird.kt
package com.example.ecofrecuencia.domain.model

data class Bird(
    val id: String,
    val name: String,
    val location: String,
    val audioUrl: String,
    val imageUrl: String? = null
)
