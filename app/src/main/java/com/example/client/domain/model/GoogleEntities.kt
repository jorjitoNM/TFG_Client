package com.example.client.domain.model

import com.google.gson.annotations.SerializedName

data class AutocompleteResponse(
    val predictions: List<Prediction>
)
data class Prediction(
    val description: String,
    @SerializedName("place_id")
    val placeId: String
)
data class PlaceDetailsResponse(
    val result: PlaceResult
)
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

data class Geometry(val location: LatLng)
data class LatLng(val lat: Double, val lng: Double)
data class Photo(
    @SerializedName("photo_reference")
    val photoReference: String
)
data class OpeningHours(
    @SerializedName("open_now")
    val openNow: Boolean?,
    @SerializedName("weekday_text")
    val weekdayText: List<String>?
)
data class GooglePlaceUi(
    val name: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val photoUrl: String?, // principal (puedes dejarlo)
    val rating: Float?,
    val userRatingsTotal: Int?,
    val openingHours: String?,
    val phoneNumber: String?,
    val website: String?,
    val photos: List<PlacePhoto> = emptyList() // Cambia a lista de PlacePhoto
)
data class PlacePhoto(
    val photoReference: String
)
