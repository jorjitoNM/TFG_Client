package com.example.client.ui.noteScreen.list

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class NotePrivacyU {
    @SerialName("PUBLIC") PUBLIC,
    @SerialName("PRIVATE") PRIVATE,
    @SerialName("FOLLOWERS") FOLLOWERS
}