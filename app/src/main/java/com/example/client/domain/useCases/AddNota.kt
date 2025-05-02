package com.example.client.domain.useCases

import com.example.client.data.remote.NoteRepository
import com.example.client.domain.model.note.Note
import javax.inject.Inject

class AddNota @Inject constructor(private val notesRepository: NoteRepository) {
    suspend operator fun invoke(note : Note, username: String) = notesRepository.addNote(note,username)
}