package com.example.client.domain.model.google

data class Location(
    val name: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val photoUrl: String? = null,
    val rating: Float? = null,
    val userRatingsTotal: Int? = null,
    val openingHours: String? = null,
    val phoneNumber: String? = null,
    val website: String? = null,
    val photos: List<PlacePhoto> = emptyList() 
)