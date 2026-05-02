package com.example.ecofrecuencia.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL

class WikipediaImageDataSource {

    suspend fun getImageUrl(scientificName: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                // ── Paso 1: buscar el título real ──────────────────────
                val searchEncoded = URLEncoder.encode(scientificName, "UTF-8")
                val searchUrl = URL("https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=$searchEncoded&format=json&srlimit=1")
                val searchConn = (searchUrl.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("Accept", "application/json")
                    connectTimeout = 10000
                    readTimeout = 10000
                    connect()
                }

                if (searchConn.responseCode != HttpURLConnection.HTTP_OK) return@withContext null


                val searchJson = BufferedReader(InputStreamReader(searchConn.inputStream))
                    .use { it.readText() }

                // Extraer título — busca dentro del array "search":[{"title":"..."}]
                val title = Regex(""""search"\s*:\s*\[\s*\{\s*"ns"\s*:\s*\d+\s*,\s*"title"\s*:\s*"([^"]+)"""")
                    .find(searchJson)?.groupValues?.get(1)


                if (title.isNullOrBlank()) return@withContext null


                val titleEncoded = URLEncoder.encode(title, "UTF-8")
                    .replace("+", "%20")
                    .replace("%27", "'")
                val summaryUrl = URL("https://en.wikipedia.org/api/rest_v1/page/summary/$titleEncoded")
                val summaryConn = (summaryUrl.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("Accept", "application/json")
                    connectTimeout = 10000
                    readTimeout = 10000
                    connect()
                }

                if (summaryConn.responseCode != HttpURLConnection.HTTP_OK) return@withContext null

                val summaryJson = BufferedReader(InputStreamReader(summaryConn.inputStream))
                    .use { it.readText() }

                val thumbnailSection = summaryJson
                    .substringAfter("\"thumbnail\"", "SIN_THUMBNAIL")
                    .take(200)

                val imageUrl = Regex(""""thumbnail".*?"source"\s*:\s*"([^"]+)"""")
                    .find(summaryJson)?.groupValues?.get(1)

                imageUrl

            } catch (e: Exception) {
                null
            }
        }
    }
}