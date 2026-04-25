// Archivo: presentation/viewmodel/BirdUiState.kt
package com.example.ecofrecuencia.presentation.viewmodel

import com.example.ecofrecuencia.domain.model.Bird

sealed interface BirdUiState {
    data object Loading : BirdUiState
    data class Success(
        val birds: List<Bird>,
        val playingBirdId: String? = null
    ) : BirdUiState
    data class Error(val message: String) : BirdUiState
}
