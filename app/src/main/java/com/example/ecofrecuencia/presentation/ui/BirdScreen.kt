// Archivo: presentation/ui/BirdScreen.kt
package com.example.ecofrecuencia.presentation.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ecofrecuencia.domain.model.Bird
import com.example.ecofrecuencia.presentation.viewmodel.BirdUiState
import com.example.ecofrecuencia.presentation.viewmodel.BirdViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirdScreen(viewModel: BirdViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    
    // Estado persistente del scroll y pop-up para evitar reinicios
    val listState = rememberLazyListState()
    var selectedBirdForImage by remember { mutableStateOf<Bird?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchBirds()
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "EcoFrecuencia",
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            val state = uiState
            AnimatedVisibility(
                visible = state is BirdUiState.Success && state.playingBirdId != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                if (state is BirdUiState.Success) {
                    val playingBird = state.birds.find { it.id == state.playingBirdId }
                    playingBird?.let { bird ->
                        ModernBottomPlayer(
                            bird = bird,
                            onStop = { viewModel.stopAudio() }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Usamos la CLASE del estado como llave para que la animación solo ocurra
            // al cambiar entre Loading, Success o Error, pero NO al cambiar de canción.
            // Esto soluciona el problema de que la lista vuelva al inicio.
            AnimatedContent(
                targetState = uiState::class,
                label = "MainContentTransition"
            ) { _ ->
                val currentState = uiState
                when (currentState) {
                    is BirdUiState.Loading -> LoadingShimmerList()
                    is BirdUiState.Error -> ErrorScreen(currentState.message) { viewModel.fetchBirds() }
                    is BirdUiState.Success -> {
                        BirdList(
                            birds = currentState.birds,
                            playingBirdId = currentState.playingBirdId,
                            listState = listState,
                            onPlay = { bird -> viewModel.playBirdAudio(bird.id, bird.audioUrl) },
                            onImageClick = { bird -> selectedBirdForImage = bird }
                        )
                    }
                }
            }
        }
    }

    // Diálogo de imagen en grande (Pop-up)
    selectedBirdForImage?.let { bird ->
        BirdImagePopup(bird = bird, onDismiss = { selectedBirdForImage = null })
    }
}

@Composable
fun BirdList(
    birds: List<Bird>,
    playingBirdId: String?,
    listState: LazyListState,
    onPlay: (Bird) -> Unit,
    onImageClick: (Bird) -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(birds, key = { it.id }) { bird ->
            BirdItem(
                bird = bird,
                isPlaying = bird.id == playingBirdId,
                onPlay = { onPlay(bird) },
                onImageClick = { onImageClick(bird) }
            )
        }
    }
}

@Composable
fun BirdItem(
    bird: Bird,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onImageClick: () -> Unit
) {
    val animatedBgColor by animateColorAsState(
        targetValue = if (isPlaying) 
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f) 
        else 
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        label = "BgColor"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = animatedBgColor),
        onClick = onPlay 
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen con click independiente para el Pop-up
            Box(
                modifier = Modifier
                    .size(85.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onImageClick() },
                contentAlignment = Alignment.Center
            ) {
                if (!bird.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(bird.imageUrl.replace("http://", "https://"))
                            .crossfade(true)
                            .build(),
                        contentDescription = bird.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Rounded.MusicNote, null, modifier = Modifier.size(32.dp))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = bird.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = bird.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            if (!isPlaying) {
                FilledIconButton(
                    onClick = onPlay,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Rounded.PlayArrow, null)
                }
            }
        }
    }
}

@Composable
fun ModernBottomPlayer(
    bird: Bird,
    onStop: () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 12.dp)
            .navigationBarsPadding(),
        tonalElevation = 12.dp,
        shadowElevation = 8.dp,
        color = Color(0xFF212121), // Fondo oscuro para el reproductor
        shape = RoundedCornerShape(28.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Miniatura en el reproductor
            AsyncImage(
                model = bird.imageUrl?.replace("http://", "https://"),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp))
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "REPRODUCIENDO",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFADB5FF),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = bird.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            IconButton(
                onClick = onStop,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFFB3261E), // Botón rojo
                    contentColor = Color.White
                ),
                modifier = Modifier.size(44.dp)
            ) {
                Icon(Icons.Rounded.Close, null, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
fun BirdImagePopup(bird: Bird, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(bird.imageUrl?.replace("http://", "https://"))
                        .crossfade(true)
                        .build(),
                    contentDescription = bird.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.FillWidth
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = bird.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = bird.location,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
                ) {
                    Text("Cerrar", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun LoadingShimmerList() {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        repeat(5) {
            Box(modifier = Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(20.dp)).background(Color.LightGray.copy(alpha = 0.2f)))
        }
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.Warning, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
        Text(message, textAlign = TextAlign.Center)
        Button(onClick = onRetry) { Text("Reintentar") }
    }
}
