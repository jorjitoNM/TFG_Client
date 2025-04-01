package com.example.client.ui.noteScreen.list

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class NoteTypeU {
    @SerialName("CLASSIC") CLASSIC,
    @SerialName("HISTORICAL") HISTORICAL,
    @SerialName("FOOD") FOOD,
    @SerialName("EVENT") EVENT,
    @SerialName("LANDSCAPE") LANDSCAPE,
    @SerialName("CULTURAL") CULTURAL
}
