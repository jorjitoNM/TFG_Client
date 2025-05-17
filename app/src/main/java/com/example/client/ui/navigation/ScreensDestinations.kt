package com.example.client.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object NormalNoteListDestination

@Serializable
data class NormalNoteDetailDestination (val noteId: Int)

@Serializable
data class NoteMapDestination(
    val lat: Double? = null,
    val lon: Double? = null
)


@Serializable
object NoteSavedListDestination

@Serializable
object MapSearchDestination




