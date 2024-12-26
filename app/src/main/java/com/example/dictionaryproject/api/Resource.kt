package com.example.dictionaryproject.api

sealed class Resource<T>(
    val response: T? = null,
    val message: String? = null
) {
    class Success<T>(response: T) : Resource<T>(response)
    class Error<T>(message: String) : Resource<T>(message = message)
    class Loading<T> : Resource<T>()
    class Empty<T>(response: T? = null) : Resource<T>(response)
}