// Archivo: presentation/utils/AudioPlayerHelper.kt
package com.example.ecofrecuencia.presentation.utils

import android.content.Context
import android.media.MediaPlayer

class AudioPlayerHelper(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    fun playAudio(url: String) {
        // Release any existing player
        mediaPlayer?.release()

        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                start()
            }
            setOnCompletionListener {
                release()
                mediaPlayer = null
            }
            setOnErrorListener { _, _, _ ->
                release()
                mediaPlayer = null
                true
            }
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
