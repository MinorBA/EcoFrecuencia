package com.example.ecofrecuencia.data.remote

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.ecofrecuencia.domain.repository.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

import com.example.ecofrecuencia.domain.model.Coordinates

import com.example.ecofrecuencia.domain.model.DomainException

class FusedLocationRepository(private val context: Context) : LocationRepository {

    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)

    override suspend fun getCurrentLocation(): Result<Coordinates> = try {
        // 1. Verificar permisos
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Result.failure(DomainException.LocationException("Permisos de ubicación no concedidos"))
        } else {
            // 2. Obtener ubicación con timeout de 5 segundos
            val location = withTimeoutOrNull(5000) {
                fusedLocationClient.lastLocation.await()
            }

            location?.let { 
                Result.success(Coordinates(it.latitude, it.longitude))
            } ?: Result.failure(DomainException.LocationException("No se pudo obtener la ubicación actual. Asegúrate de tener el GPS activado."))
        }
    } catch (e: Exception) {
        Result.failure(DomainException.UnknownException(e))
    }
}
