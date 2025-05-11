package com.example.client.ui.noteMap.list

sealed class NoteMapEvent {
    data object AvisoVisto : NoteMapEvent()
    data object GetNotes : NoteMapEvent()
    data object GetCurrentLocation : NoteMapEvent()
    data object CheckLocationPermission : NoteMapEvent()
    data class SearchNote(val query: String) : NoteMapEvent()
}