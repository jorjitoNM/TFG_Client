package com.example.client.ui.savedNotes

sealed class SavedEvent {
    data object GetNotes : SavedEvent()
    data object AvisoVisto : SavedEvent()
    data class ApplyFilter(val asc: Boolean) : SavedEvent()
}