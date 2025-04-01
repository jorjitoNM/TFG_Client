package com.example.client.ui.noteScreen.list

import com.google.gson.annotations.SerializedName



open class NoteDTO(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("title") val title: String = "",
    @SerializedName("content") val content: String? = null,
    @SerializedName("privacy") val privacy: NotePrivacyU = NotePrivacyU.PUBLIC,
    @SerializedName("rating") val rating: Int = 0,
    @SerializedName("ownerUsername") val ownerUsername: String? = null,
    @SerializedName("likes") val likes: Int = 0,
    @SerializedName("created") val created: String = "", // Usaremos String para LocalDateTime
    @SerializedName("latitude") val latitude: Double = 0.0,
    @SerializedName("longitude") val longitude: Double = 0.0,
    @SerializedName("type") val type: NoteTypeU = NoteTypeU.CLASSIC
)

class EventNoteDTO(
    id: Int = 0,
    title: String = "",
    content: String? = null,
    privacy: NotePrivacyU = NotePrivacyU.PUBLIC,
    rating: Int = 0,
    ownerUsername: String? = null,
    likes: Int = 0,
    created: String = "",
    latitude: Double = 0.0,
    longitude: Double = 0.0,
    type: NoteTypeU = NoteTypeU.EVENT,
    @SerializedName("start") val start: String = "", // Usaremos String para LocalDateTime
    @SerializedName("end") val end: String = "" // Usaremos String para LocalDateTime
) : NoteDTO(id, title, content, privacy, rating, ownerUsername, likes, created, latitude, longitude, type)