package com.example.ecofrecuencia.data.remote

import kotlinx.serialization.Serializable

/**
 * DTOs para la API de Wikipedia usando formatversion=2.
 * Esto convierte el mapa de "pages" en una lista, facilitando el parseo.
 */
@Serializable
data class WikipediaResponse(
    val query: WikipediaQuery? = null
)

@Serializable
data class WikipediaQuery(
    val pages: List<WikipediaPage>? = null
)

@Serializable
data class WikipediaPage(
    val title: String? = null,
    val thumbnail: WikipediaThumbnail? = null,
    val pageimage: String? = null
)

@Serializable
data class WikipediaThumbnail(
    val source: String? = null,
    val width: Int? = null,
    val height: Int? = null
)
