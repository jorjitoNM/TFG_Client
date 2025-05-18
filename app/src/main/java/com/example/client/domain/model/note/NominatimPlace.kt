package com.example.client.domain.model.note

import com.google.gson.annotations.SerializedName

data class NominatimPlace(
    @SerializedName("display_name")
    val displayName: String?,
    @SerializedName("lat")
    val latitude: String?,
    @SerializedName("lon")
    val longitude: String?,
    @SerializedName("type")
    val placeType: String?
)