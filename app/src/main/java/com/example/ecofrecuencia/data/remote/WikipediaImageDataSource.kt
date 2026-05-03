package com.example.ecofrecuencia.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import android.util.Log // Importante para debugear

class WikipediaImageDataSource(private val json: Json) {

    /**
     * Busca la imagen de una especie en Wikipedia de forma inteligente.
     * 
     * @param scientificName El nombre científico (ej: "Alouatta palliata").
     * @param commonName El nombre común opcional (ej: "Mantled Howler") como fallback.
     */
    suspend fun getImageUrl(scientificName: String, commonName: String? = null): String? {
        return withContext(Dispatchers.IO) {
            // 1. Intentamos búsqueda por título (más precisa si el nombre es correcto)
            var imageUrl = fetchImageByProperty("titles", scientificName)
            
            // 2. Si falla, intentamos búsqueda flexible (generator=search)
            if (imageUrl == null) {
                imageUrl = fetchImageByProperty("generator=search&gsrlimit=1&gsrsearch", scientificName)
            }

            // 3. Fallback final al nombre común si todo lo anterior falla
            if (imageUrl == null && !commonName.isNullOrBlank() && commonName != "Unknown Bird") {
                Log.d("WikiAPI", "Intentando con nombre común: $commonName")
                imageUrl = fetchImageByProperty("generator=search&gsrlimit=1&gsrsearch", commonName)
            }
            
            imageUrl
        }
    }

    private fun fetchImageByProperty(paramName: String, query: String): String? {
        var connection: HttpURLConnection? = null
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8").replace("+", "%20")
            
            // Wikipedia prefiere formatversion=2 para recibir una lista simple
            val urlString = "https://en.wikipedia.org/w/api.php?action=query&$paramName=$encodedQuery&prop=pageimages&piprop=thumbnail&pithumbsize=600&format=json&formatversion=2&redirects=1"

            val url = URL(urlString)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8000
                readTimeout = 8000
                // User-Agent IDENTIFICABLE Y CONSISTENTE
                setRequestProperty("User-Agent", "EcoFrecuenciaApp/1.2 (https://github.com/MinoBA/EcoFrecuencia; contacto@ecofrecuencia.com)")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("Connection", "close")
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val response = json.decodeFromString<WikipediaResponse>(responseText)
                val source = response.query?.pages?.firstOrNull()?.thumbnail?.source
                
                if (source != null) Log.d("WikiAPI", "✓ Imagen: $source")
                return source
            } else {
                Log.e("WikiAPI", "✗ HTTP $responseCode para '$query'")
            }
        } catch (e: Exception) {
            Log.e("WikiAPI", "! Error: ${e.message}")
        } finally {
            connection?.disconnect()
        }
        return null
    }
}