package com.example.client.ui.savedNotes

import com.example.client.ui.common.UiEvent
import com.example.client.ui.noteScreen.list.NoteDTO

data class SavedSate(
    val notes: List<NoteDTO> = emptyList(),
    val isLoading: Boolean = false,
    val selectedNoteId: Int = 0,
    val aviso: UiEvent? = null
)