package com.example.client.data.remote.datasource

import com.example.client.data.remote.service.GooglePlacesService
import javax.inject.Inject

class GooglePlacesRemoteDataSource @Inject constructor(private val googlePlacesService: GooglePlacesService) :
    BaseApiResponse() {
        suspend fun getAutocomplete(input: String, apiKey: String) = safeApiCall { googlePlacesService.getAutocomplete(input, apiKey) }
        suspend fun getPlaceDetails(placeId: String, apiKey: String) = safeApiCall { googlePlacesService.getPlaceDetails(placeId = placeId,  apiKey = apiKey) }
}