package com.example.client.ui.addNoteScreen

import com.example.client.domain.model.note.Note
import com.example.client.ui.common.UiEvent

data class AddState(
    val note: Note = Note(),
    val isLoading: Boolean = true,
    val uiEvent: UiEvent? = null
)