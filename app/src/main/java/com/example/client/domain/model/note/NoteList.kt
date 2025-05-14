package com.example.client.domain.model.note

data class NoteList (
    val id : Int = 0,
    val noteType: NoteType = NoteType.CLASSIC,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val likes: Int = 0,
)