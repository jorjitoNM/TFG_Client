package com.example.client.ui.noteMap.search

import com.example.client.domain.model.google.Location
import com.example.client.ui.common.UiEvent


data class MapSearchState(
    val searchText: String = "",
    val results: List<Location> = emptyList(),
    val recents: List<Location> = emptyList(),
    val isLoading: Boolean = false,
    val showEmptyState: Boolean = false,
    val aviso: UiEvent? = null,
    val showEmptyStateDelayed: Boolean = false,
    val userLogged: String? = null // <-- Añade esto

)