// Archivo: data/remote/HttpUrlConnectionDataSource.kt
package com.example.ecofrecuencia.data.remote

import com.example.ecofrecuencia.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class HttpUrlConnectionDataSource(private val json: Json) : NetworkDataSource {

    override suspend fun getBirdsNearby(lat: Double, lng: Double): Result<BirdResponseDto> {
        return withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            try {
                // Filtramos por coordenadas y aseguramos que el grupo sea estrictamente aves (birds)
                val query = "lat:$lat lon:$lng grp:birds"
                val encodedQuery = URLEncoder.encode(query, "UTF-8")
                val urlString = "https://www.xeno-canto.org/api/3/recordings?query=$encodedQuery&key=${BuildConfig.XENO_CANTO_KEY}"
                
                val url = URL(urlString)
                connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("Accept", "application/json")
                    connectTimeout = 15000
                    readTimeout = 15000
                }

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val birdResponse = json.decodeFromString<BirdResponseDto>(response)
                    Result.success(birdResponse)
                } else {
                    Result.failure(Exception("Error del servidor: HTTP $responseCode"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            } finally {
                connection?.disconnect()
            }
        }
    }
}
