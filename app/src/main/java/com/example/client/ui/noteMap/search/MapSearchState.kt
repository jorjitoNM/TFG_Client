package com.example.client.ui.noteMap.search

import com.example.client.domain.model.GooglePlaceUi
import com.example.client.ui.common.UiEvent

data class MapSearchState(
    val searchText: String = "",
    val results: List<GooglePlaceUi> = emptyList(),
    val isLoading: Boolean = false,
    val showEmptyState: Boolean = false,
    val aviso: UiEvent? = null
)