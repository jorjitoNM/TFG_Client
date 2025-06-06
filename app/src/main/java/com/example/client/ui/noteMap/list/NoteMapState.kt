package com.example.client.ui.noteMap.list

import android.location.Location
import com.example.client.data.model.NoteDTO
import com.example.client.domain.model.note.NoteType
import com.example.client.ui.common.UiEvent
import com.google.android.gms.maps.model.LatLng


data class NoteMapState(
    val notes: List<NoteDTO> = emptyList(),
    val isLoading: Boolean = false,
    val aviso: UiEvent? = null,
    val currentSearch: String = "",
    val currentLocation: Location? = null,
    val hasLocationPermission: Boolean = false,
    val cameraLatLng: LatLng? = null,
    val cameraZoom: Float? = null,
    val selectedType: NoteType? = null,
    val noteSelectedId : Int = 0
)
