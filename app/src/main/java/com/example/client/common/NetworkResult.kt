package com.example.client.common

sealed class NetworkResult<T> {
    class Success<T>(val data: T) : NetworkResult<T>()

    class Error<T>(val message: String) : NetworkResult<T>()

    class Loading<T> : NetworkResult<T>()

    inline fun <R> map(transform: (data: T) -> R): NetworkResult<R> =
        when (this) {
            is Error -> Error(message)
            is Success -> Success(transform(data))
            is Loading -> Loading()
        }

    inline fun <R> then(transform: (data: T) -> NetworkResult<R>): NetworkResult<R> =
        when (this) {
            is Error -> Error(message)
            is Success -> transform(data)
            is Loading -> Loading()
        }
}