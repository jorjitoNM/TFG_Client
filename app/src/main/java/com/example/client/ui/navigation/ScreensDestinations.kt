package com.example.client.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object NormalNoteListDestination

@Serializable
data class NormalNoteDetailDestination (val noteId: Int)

@Serializable
object NoteMapDestination

@Serializable
object MapSearchDestination

@Serializable
object UserScreenDestination
@Serializable
object UserSearchDestination




