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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecofrecuencia.presentation.ui.BirdScreen
import com.example.ecofrecuencia.presentation.ui.theme.EcoFrecuenciaTheme
import com.example.ecofrecuencia.presentation.viewmodel.BirdViewModel

class MainActivity : ComponentActivity() {

    private var permissionsGrantedState = mutableStateOf(false)

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionsGrantedState.value =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkInitialPermissions()
        setContent {
            EcoFrecuenciaTheme {
                MainScreen(permissionsGrantedState.value)
            }
        }
    }

    private fun checkInitialPermissions() {
        val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        permissionsGrantedState.value =
            fine == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                    coarse == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    @Composable
    private fun MainScreen(permissionsGranted: Boolean) {
        val appContainer = (LocalContext.current.applicationContext as EcoApplication).appContainer

        if (permissionsGranted) {
            val viewModel: BirdViewModel = viewModel(factory = appContainer.birdViewModelFactory)
            BirdScreen(viewModel = viewModel) //  BirdScreen se encarga de llamar fetchBirds()
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
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Para escuchar las aves de tu zona, necesitamos acceso a tu ubicación GPS. Esto nos permite encontrar las especies cercanas a ti.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Button(onClick = onRequestPermissions) {
                    Text("Conceder Permisos")
                }
            }
        }
    }
}