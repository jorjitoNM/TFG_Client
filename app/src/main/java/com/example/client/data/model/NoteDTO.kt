package com.example.client.data.model

import com.example.client.domain.model.note.NotePrivacy
import com.example.client.domain.model.note.NoteType

open class NoteDTO(
    val id: Int = 0,
    val title: String = "",
    val content: String? = null,
    val privacy: NotePrivacy = NotePrivacy.PUBLIC,
    val rating: Int = 0,
    val ownerUsername: String? = null,
    val likes: Int = 0,
    val created: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val type: NoteType = NoteType.CLASSIC
)