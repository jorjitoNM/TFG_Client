package com.example.client.domain.model.note

import com.google.gson.annotations.SerializedName

data class NominatimPlace(
    @SerializedName("display_name")
    val displayName: String?,
    val lat: String,
    val lon: String
)