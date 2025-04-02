package com.example.client.ui.addNoteScreen


sealed class AddEvent {
    object addNote : AddEvent()
    object UiEventDone : AddEvent()
}