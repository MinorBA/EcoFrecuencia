package com.example.ecofrecuencia.data.remote

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.example.ecofrecuencia.domain.datasource.LocationDataSource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FusedLocationDataSource(private val context: Context) : LocationDataSource {

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    override suspend fun getCurrentLocation(): Result<Pair<Double, Double>> {
        return try {
            // Verificar permisos
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return Result.failure(SecurityException("Location permissions not granted"))
            }

            // Obtener la última ubicación conocida
            val location = suspendCancellableCoroutine<Location?> { continuation ->
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    continuation.resume(location)
                }.addOnFailureListener { exception ->
                    continuation.resume(null)
                }
            }

            if (location != null) {
                Result.success(Pair(location.latitude, location.longitude))
            } else {
                Result.failure(Exception("Unable to get current location. Make sure GPS is on."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
