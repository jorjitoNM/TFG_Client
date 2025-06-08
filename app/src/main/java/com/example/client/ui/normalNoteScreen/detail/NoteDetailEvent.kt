package com.example.client.ui.normalNoteScreen.detail

sealed class NoteDetailEvent {
    data class GetNote(val id: Int) : NoteDetailEvent()
    data object AvisoVisto : NoteDetailEvent()
    data class LoadNoteImages(val id: Int) : NoteDetailEvent()
}
