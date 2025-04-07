package com.example.client.ui.noteMap.list

import com.example.client.data.model.NoteDTO
import com.example.client.ui.common.UiEvent

data class NoteMapState(
    val notes: List<NoteDTO> = emptyList(),
    val isLoading : Boolean = false,
    val aviso: UiEvent? = null
)