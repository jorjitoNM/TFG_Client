package com.example.client.data.remote.service

import com.example.client.domain.model.AutocompleteResponse
import com.example.client.domain.model.PlaceDetailsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlacesService {
    @GET("place/autocomplete/json")
    suspend fun getAutocomplete(
        @Query("input") input: String,
        @Query("key") apiKey: String
    ): AutocompleteResponse

    @GET("place/details/json")
    suspend fun getPlaceDetails(
        @Query("place_id") placeId: String,
        @Query("fields") fields: String = "name,formatted_address,geometry,photos",
        @Query("key") apiKey: String
    ): PlaceDetailsResponse
}