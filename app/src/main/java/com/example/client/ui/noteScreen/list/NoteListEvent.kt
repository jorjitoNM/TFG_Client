package com.example.client.ui.noteScreen.list

sealed class NoteListEvent {
    data object GetNotes : NoteListEvent()
    data class SelectedNote(val noteId: Int) : NoteListEvent()
    data object AvisoVisto : NoteListEvent()
    data class FavNote(val noteId: Int) : NoteListEvent()
   data class ApplyFilter(val asc: Boolean) : NoteListEvent()
}