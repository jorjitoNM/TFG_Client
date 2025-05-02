package com.example.client.ui.addNoteScreen

import com.example.client.data.model.NoteDTO
import com.example.client.domain.model.note.Note
import com.example.client.ui.common.UiEvent

data class AddState(
    val note: NoteDTO? = null,
    val isLoading: Boolean = true,
    val uiEvent: UiEvent? = null,
    val aviso: UiEvent? = null,

    )