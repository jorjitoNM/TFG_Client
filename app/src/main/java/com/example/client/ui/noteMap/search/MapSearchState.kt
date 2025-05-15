package com.example.client.ui.noteMap.search

import com.example.client.domain.model.note.NominatimPlace
import com.example.client.ui.common.UiEvent

data class MapSearchState (
    val searchText: String = "",
    val results: List<NominatimPlace> = emptyList(),
    val aviso: UiEvent? = null
)