package com.example.client.ui.noteScreen.list

import com.example.client.domain.model.note.Note
import com.example.client.ui.common.UiEvent

data class NoteListState(
    val notes: List<NoteDTO> = emptyList(),
    val isLoading: Boolean = false,
    val selectedNoteId: Int = 0,
    val aviso: UiEvent? = null
)