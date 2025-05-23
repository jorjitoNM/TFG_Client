package com.example.client.domain.model.google
import com.google.gson.annotations.SerializedName

data class Photo(
    @SerializedName("photo_reference")
    val photoReference: String
)