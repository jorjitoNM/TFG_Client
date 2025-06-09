package com.example.client.domain.usecases.note

import com.example.client.data.model.NoteDTO
import com.example.client.data.repositories.NoteRepository
import javax.inject.Inject

class AddNota @Inject constructor(private val notesRepository: NoteRepository) {
    suspend operator fun invoke(note : NoteDTO) = notesRepository.addNote(note)

}