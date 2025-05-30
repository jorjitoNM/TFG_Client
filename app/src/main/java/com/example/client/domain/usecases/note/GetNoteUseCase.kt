package com.example.client.domain.useCases.note

import com.example.client.data.repositories.NoteRepository
import javax.inject.Inject

class GetNoteUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(id:Int) = noteRepository.getNote(id)
}