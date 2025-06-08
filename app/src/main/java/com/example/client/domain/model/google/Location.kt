package com.example.client.domain.model.google

data class Location(
    val id: Int = 0,
    val name: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val userLogged: String = "",
    val photoUrl: String? = null,
    val rating: Float? = null,
    val userRatingsTotal: Int? = null,
    val openingHours: String? = null,
    val phoneNumber: String? = null,
    val website: String? = null,
    val photos: List<PlacePhoto> = emptyList(),
    val openingHoursFull: List<String>? = null, // para weekday_text

)