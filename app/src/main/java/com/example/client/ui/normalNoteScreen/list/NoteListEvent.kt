package com.example.client.ui.normalNoteScreen.list

import com.example.client.domain.model.note.NoteType

sealed class NoteListEvent {
    data object GetNotes : NoteListEvent()
    data class SelectedNote(val noteId: Int) : NoteListEvent()
    data object AvisoVisto : NoteListEvent()
    data class FavNote(val noteId: Int) : NoteListEvent()
    data class ApplyFilter(val asc: Boolean) : NoteListEvent()
    data class OrderByType(val type: NoteType) : NoteListEvent()
    data class GetNoteSearch(val title:String):NoteListEvent()
}