package com.example.ecofrecuencia

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ecofrecuencia.presentation.ui.BirdScreen
import com.example.ecofrecuencia.presentation.viewmodel.BirdViewModelFactory
import com.example.ecofrecuencia.ui.theme.EcoFrecuenciaTheme

class MainActivity : ComponentActivity() {

    // Variable reactiva para los permisos
    private var permissionsGrantedState = mutableStateOf(false)

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        permissionsGrantedState.value = granted
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Verificar permisos iniciales
        checkInitialPermissions()

        setContent {
            EcoFrecuenciaTheme {
                MainScreen(permissionsGrantedState.value)
            }
        }
    }

    private fun checkInitialPermissions() {
        val fineLocation = androidx.core.content.ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        val coarseLocation = androidx.core.content.ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        permissionsGrantedState.value = fineLocation || coarseLocation
    }

    override fun onDestroy() {
        super.onDestroy()
        (application as EcoApplication).appContainer.audioPlayerHelper.release()
    }

    @Composable
    private fun MainScreen(permissionsGranted: Boolean) {
        val context = LocalContext.current
        val appContainer = (context.applicationContext as EcoApplication).appContainer

        if (permissionsGranted) {
            val factory = remember { BirdViewModelFactory(appContainer.getNearbyBirdsUseCase) }
            val viewModel: com.example.ecofrecuencia.presentation.viewmodel.BirdViewModel = 
                androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)

            LaunchedEffect(Unit) {
                viewModel.fetchBirds()
            }

            BirdScreen(viewModel = viewModel)
        } else {
            PermissionRequestScreen(
                onRequestPermissions = {
                    requestPermissionsLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            )
        }
    }

    @Composable
    private fun PermissionRequestScreen(onRequestPermissions: () -> Unit) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    text = "Permisos de Ubicación Requeridos",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Text(
                    text = "Para escuchar las aves de tu zona, necesitamos acceso a tu ubicación GPS. Esto nos permite encontrar las especies cercanas a ti.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Button(onClick = onRequestPermissions) {
                    Text("Conceder Permisos")
                }
            }
        }
    }
}
