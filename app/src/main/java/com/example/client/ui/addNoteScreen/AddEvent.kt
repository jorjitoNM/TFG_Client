package com.example.client.ui.addNoteScreen

import com.example.client.data.model.NoteDTO


sealed class AddEvent {
    object addNote : AddEvent()
    object UiEventDone : AddEvent()
    data class editNote(val note: NoteDTO) : AddEvent()
}