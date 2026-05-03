package com.example.ecofrecuencia.domain.model

sealed class DomainException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NetworkException(cause: Throwable) : DomainException("Error de red al conectar con el servidor", cause)
    class LocationException(message: String) : DomainException(message)
    class UnknownException(cause: Throwable) : DomainException("Ha ocurrido un error inesperado", cause)
}
