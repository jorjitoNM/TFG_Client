package com.example.client.ui.noteMap.search

sealed class MapSearchEvent {
    data class UpdateSearchText(val text: String) : MapSearchEvent()
    data object NavigateBack : MapSearchEvent()
    data object AvisoVisto : MapSearchEvent()
    data class ShowSnackbar(val message: String) : MapSearchEvent()
}