package com.example.client.domain.usecases.note

import com.example.client.data.model.NoteDTO
import com.example.client.data.repositories.NoteRepository
import javax.inject.Inject

class UpdateNoteUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(note: NoteDTO) = noteRepository.updateNote(note)
}