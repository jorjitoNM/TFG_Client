package com.example.client.data

import com.example.client.domain.model.note.Note
import javax.inject.Inject

class NotesRepository @Inject constructor(
    private val notes: notesDataSource,
) {
    fun addNote(note: Note) = notes.insertNotes(note)
}