// Archivo: presentation/viewmodel/BirdViewModelFactory.kt
package com.example.ecofrecuencia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ecofrecuencia.presentation.utils.AudioPlayer
import com.example.ecofrecuencia.domain.usecase.GetNearbyBirdsUseCase

class BirdViewModelFactory(
    private val getNearbyBirdsUseCase: GetNearbyBirdsUseCase,
    private val audioPlayer: AudioPlayer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BirdViewModel::class.java)) {
            return BirdViewModel(getNearbyBirdsUseCase, audioPlayer) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
