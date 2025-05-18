package com.example.client.data.remote.service

import com.example.client.domain.model.note.NominatimPlace
import retrofit2.http.*

fun interface NominatimService {
    @GET("search")
    suspend fun searchPlaces(
        @Query("q") query: String,
        @Query("format") format: String,
        @Query("addressdetails") addressDetails:Int
    ): List<NominatimPlace>
}
