package com.example.client.domain.usecases

import com.example.client.data.model.NoteDTO
import com.example.client.data.remote.NoteRepository
import com.example.client.domain.model.note.Note
import javax.inject.Inject

class UpdateNoteUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(note: NoteDTO) = noteRepository.updateNote(note)
}