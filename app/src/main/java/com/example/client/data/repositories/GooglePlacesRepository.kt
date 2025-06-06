package com.example.client.data.repositories

import com.example.client.BuildConfig
import com.example.client.common.NetworkResult
import com.example.client.data.remote.datasource.GooglePlacesRemoteDataSource
import com.example.client.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GooglePlacesRepository @Inject constructor(
    private val googlePlacesRemoteDataSource: GooglePlacesRemoteDataSource,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend fun getPlacesDetails(placeId: String) = withContext(dispatcher) {
        try {
            googlePlacesRemoteDataSource.getPlaceDetails(placeId, BuildConfig.GOOGLE_PLACES_API_KEY)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }

    suspend fun getPlacesAutocomplete(input: String) = withContext(dispatcher) {
        try {
            googlePlacesRemoteDataSource.getAutocomplete(input, BuildConfig.GOOGLE_PLACES_API_KEY)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }
}

