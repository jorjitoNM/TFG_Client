package com.example.client.ui.normalNoteScreen.detail

import com.example.client.data.model.NoteDTO
import com.example.client.domain.model.note.NotePrivacy
import com.example.client.ui.common.UiEvent

data class NoteDetailState(
    val note: NoteDTO? = null,
    val isLoading: Boolean = false,
    val aviso: UiEvent? = null,
    val isImagesLoading: Boolean = false
)
