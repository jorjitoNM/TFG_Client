package com.example.client.domain.model.google
import com.google.gson.annotations.SerializedName

data class Prediction(
    val description: String,
    @SerializedName("place_id")
    val placeId: String
)