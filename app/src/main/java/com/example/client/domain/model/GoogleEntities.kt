package com.example.client.domain.model

// Autocomplete Response
data class AutocompleteResponse(
    val predictions: List<Prediction>
)
data class Prediction(
    val description: String,
    val place_id: String
)
data class PlaceDetailsResponse(
    val result: PlaceResult
)
data class PlaceResult(
    val name: String?,
    val formatted_address: String?,
    val geometry: Geometry?,
    val photos: List<Photo>?
)
data class Geometry(val location: LatLng)
data class LatLng(val lat: Double, val lng: Double)
data class Photo(val photo_reference: String)
data class GooglePlaceUi(
    val name: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val photoUrl: String?
)