package com.example.client.ui.noteMap.search

sealed class MapSearchEvent {
    data object NavigateBack : MapSearchEvent()
    data object AvisoVisto : MapSearchEvent()
    data class UpdateSearchText(val query: String) : MapSearchEvent()
}