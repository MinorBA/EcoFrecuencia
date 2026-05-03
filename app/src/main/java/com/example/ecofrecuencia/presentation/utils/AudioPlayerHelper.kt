// Archivo: presentation/utils/AudioPlayerHelper.kt
package com.example.ecofrecuencia.presentation.utils

import android.content.Context
import android.media.MediaPlayer

class AudioPlayerHelper(private val context: Context) : AudioPlayer {

    private var mediaPlayer: MediaPlayer? = null

    override fun playAudio(url: String, onCompletion: () -> Unit) {
        // Release any existing player
        mediaPlayer?.release()

        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                start()
            }
            setOnCompletionListener {
                onCompletion()
                release()
                mediaPlayer = null
            }
            setOnErrorListener { _, _, _ ->
                onCompletion()
                release()
                mediaPlayer = null
                true
            }
        }
    }

    override fun stopAudio() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun release() {
        stopAudio()
    }
}
