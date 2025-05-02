package com.example.client.domain.useCases

import com.example.client.data.model.NoteDTO
import com.example.client.data.remote.NoteRepository
import javax.inject.Inject

class AddNota @Inject constructor(private val notesRepository: NoteRepository) {
    suspend operator fun invoke(note : NoteDTO, username: String) = notesRepository.addNote(note,username)
}