package com.example.client.data.remote.service

import com.example.client.domain.model.google.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlacesService {
    @GET("place/autocomplete/json")
    suspend fun getAutocomplete(
        @Query("input") input: String,
        @Query("key") apiKey: String
    ): Response<AutocompleteResponse>

    @GET("place/details/json")
    suspend fun getPlaceDetails(
        @Query("place_id") placeId: String,
        @Query("fields") fields: String = "name,formatted_address,geometry,photos,rating,user_ratings_total,opening_hours,formatted_phone_number,website",
        @Query("key") apiKey: String
    ): Response<PlaceDetailsResponse>
}