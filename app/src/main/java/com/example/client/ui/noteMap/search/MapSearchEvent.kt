package com.example.client.ui.noteMap.search

import com.example.client.domain.model.google.Location

sealed class MapSearchEvent {
    data class UpdateSearchText(val text: String) : MapSearchEvent()
    data object NavigateBack : MapSearchEvent()
    data object AvisoVisto : MapSearchEvent()
    data class ShowSnackbar(val message: String) : MapSearchEvent()

    // Eventos para búsquedas recientes
    data class LoadRecents(val userLogged: String) : MapSearchEvent()
    data class InsertRecent(val location: Location, val userLogged: String) : MapSearchEvent()
    data class DeleteRecent(val id: Int, val userLogged: String) : MapSearchEvent()
}