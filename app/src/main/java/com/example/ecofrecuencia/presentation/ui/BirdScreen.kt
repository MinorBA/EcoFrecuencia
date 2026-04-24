// Archivo: presentation/ui/BirdScreen.kt
package com.example.ecofrecuencia.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ecofrecuencia.presentation.utils.AudioPlayerHelper
import com.example.ecofrecuencia.presentation.viewmodel.BirdUiState
import com.example.ecofrecuencia.presentation.viewmodel.BirdViewModel

@Composable
fun BirdScreen(viewModel: BirdViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val audioPlayerHelper = remember { AudioPlayerHelper(context) }

    LaunchedEffect(Unit) {
        viewModel.fetchBirds()
    }

    when (uiState) {
        is BirdUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is BirdUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = (uiState as BirdUiState.Error).message)
            }
        }
        is BirdUiState.Success -> {
            val birds = (uiState as BirdUiState.Success).birds
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(birds) { bird ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = bird.name, style = MaterialTheme.typography.headlineSmall)
                                Text(text = bird.location, style = MaterialTheme.typography.bodyMedium)
                            }
                            IconButton(onClick = { audioPlayerHelper.playAudio(bird.audioUrl) }) {
                                Text("▶️")
                            }
                        }
                    }
                }
            }
        }
    }
}
