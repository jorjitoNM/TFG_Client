package com.example.client.domain.usecases.note

import com.example.client.data.repositories.NoteRepository
import com.example.client.domain.model.note.NoteType
import javax.inject.Inject

class OrderNoteByTypUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(noteType : NoteType) = noteRepository.filterNoteByType(noteType)
}