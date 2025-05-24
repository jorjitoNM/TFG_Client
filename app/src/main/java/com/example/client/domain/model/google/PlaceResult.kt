package com.example.client.domain.model.google
import com.google.gson.annotations.SerializedName

data class PlaceResult(
    val name: String?,
    @SerializedName("formatted_address")
    val formattedAddress: String?,
    val geometry: Geometry?,
    val photos: List<Photo>?,
    val rating: Float?,
    @SerializedName("user_ratings_total")
    val userRatingsTotal: Int?,
    @SerializedName("opening_hours")
    val openingHours: OpeningHours?,
    @SerializedName("formatted_phone_number")
    val formattedPhoneNumber: String?,
    val website: String?
)
