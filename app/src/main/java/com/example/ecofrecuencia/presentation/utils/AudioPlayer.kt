package com.example.ecofrecuencia.presentation.utils

interface AudioPlayer {
    fun playAudio(url: String, onCompletion: () -> Unit)
    fun stopAudio()
    fun release()
}
