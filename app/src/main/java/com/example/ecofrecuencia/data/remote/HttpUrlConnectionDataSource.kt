// Archivo: data/remote/HttpUrlConnectionDataSource.kt
package com.example.ecofrecuencia.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class HttpUrlConnectionDataSource(private val json: Json) : NetworkDataSource {

    override suspend fun getBirdsNearby(lat: Double, lng: Double): Result<BirdResponseDto> {
        return withContext(Dispatchers.IO) {
            try {
                // La API de Xeno-Canto requiere que los parámetros de la consulta estén correctamente formateados.
                // Usamos lat y lon (con un rango aproximado si es necesario, o tal cual)
                // Formato: query=lat:VALUE lon:VALUE
                val query = "lat:$lat lon:$lng"
                val encodedQuery = URLEncoder.encode(query, "UTF-8")
                val urlString = "https://xeno-canto.org/api/2/recordings?query=$encodedQuery"
                
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Accept", "application/json")
                connection.connectTimeout = 15000
                connection.readTimeout = 15000

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val response = reader.use { it.readText() }
                    inputStream.close()

                    val birdResponse = json.decodeFromString<BirdResponseDto>(response)
                    Result.success(birdResponse)
                } else {
                    Result.failure(Exception("Error del servidor: HTTP $responseCode al consultar la ubicación ($lat, $lng)"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
