package com.example.client.ui.addNoteScreen

import com.example.client.domain.model.note.Note


sealed class AddEvent {
    object addNote : AddEvent()
    object UiEventDone : AddEvent()
    data class editNote(val note: Note) : AddEvent()
}