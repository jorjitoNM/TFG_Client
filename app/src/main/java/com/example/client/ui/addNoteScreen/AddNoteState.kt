package com.example.client.ui.addNoteScreen

import android.location.Location
import com.example.client.data.model.NoteDTO
import com.example.client.ui.common.UiEvent

data class AddNoteState(
    val note: NoteDTO = NoteDTO(),
    val isLoading: Boolean = false,
    val uiEvent: UiEvent? = null,
    val aviso: UiEvent? = null,
    val currentLocation: Location? = null,
    val hasLocationPermission: Boolean = false
)