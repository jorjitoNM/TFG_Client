package com.example.client.ui.noteMap.search

import com.example.client.ui.common.UiEvent

data class MapSearchState (
    val searchText: String = "",
    val aviso: UiEvent? = null
)