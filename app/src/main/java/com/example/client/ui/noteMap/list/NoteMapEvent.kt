package com.example.client.ui.noteMap.list

sealed class NoteMapEvent {
    object AvisoVisto : NoteMapEvent()
    object GetNotes : NoteMapEvent()
}