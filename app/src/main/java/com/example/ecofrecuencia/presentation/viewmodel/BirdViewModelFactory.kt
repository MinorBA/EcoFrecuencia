// Archivo: presentation/viewmodel/BirdViewModelFactory.kt
package com.example.ecofrecuencia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ecofrecuencia.domain.usecase.GetNearbyBirdsUseCase

class BirdViewModelFactory(
    private val getNearbyBirdsUseCase: GetNearbyBirdsUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BirdViewModel::class.java)) {
            return BirdViewModel(getNearbyBirdsUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
