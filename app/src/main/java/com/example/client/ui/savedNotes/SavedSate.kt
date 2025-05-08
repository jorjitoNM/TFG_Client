package com.example.client.ui.savedNotes

import com.example.client.data.model.NoteDTO
import com.example.client.ui.common.UiEvent

data class SavedSate(
    val notes: List<NoteDTO> = emptyList(),
    val isLoading: Boolean = false,
    val selectedNoteId: Int = 0,
    val aviso: UiEvent? = null
)