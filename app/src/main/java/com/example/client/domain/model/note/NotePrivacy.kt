package com.example.client.domain.model.note

// Enums for the note
enum class NotePrivacy(val displayName: String) {
    PRIVATE("Private"),
    PUBLIC("Public"),
    FOLLOWERS("Followers");
}
