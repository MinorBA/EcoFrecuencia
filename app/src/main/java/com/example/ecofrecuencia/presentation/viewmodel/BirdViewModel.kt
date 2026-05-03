// Archivo: presentation/viewmodel/BirdViewModel.kt
package com.example.ecofrecuencia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecofrecuencia.domain.usecase.GetNearbyBirdsUseCase
import com.example.ecofrecuencia.presentation.utils.AudioPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BirdViewModel(
    private val getNearbyBirdsUseCase: GetNearbyBirdsUseCase,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow<BirdUiState>(BirdUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun fetchBirds() {
        viewModelScope.launch {
            _uiState.value = BirdUiState.Loading
            getNearbyBirdsUseCase().fold(
                onSuccess = { birds ->
                    _uiState.value = BirdUiState.Success(birds)
                },
                onFailure = { error ->
                    _uiState.value = BirdUiState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }

    fun playBirdAudio(birdId: String, url: String) {
        val currentState = _uiState.value
        if (currentState is BirdUiState.Success) {
            if (currentState.playingBirdId == birdId) {
                stopAudio()
            } else {
                audioPlayer.playAudio(url) {
                    // Al completar el audio, reseteamos el estado de reproducción
                    _uiState.update { state ->
                        (state as? BirdUiState.Success)?.copy(playingBirdId = null) ?: state
                    }
                }
                _uiState.update { state ->
                    (state as? BirdUiState.Success)?.copy(playingBirdId = birdId) ?: state
                }
            }
        }
    }

    fun stopAudio() {
        audioPlayer.stopAudio()
        _uiState.update { state ->
            (state as? BirdUiState.Success)?.copy(playingBirdId = null) ?: state
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}
