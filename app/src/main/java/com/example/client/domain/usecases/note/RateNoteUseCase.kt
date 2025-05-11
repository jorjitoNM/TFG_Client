package com.example.client.domain.usecases.note

import com.example.client.data.repositories.NoteRepository
import javax.inject.Inject

class RateNoteUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(id: Int, rating: Int) = noteRepository.rateNote(id, rating)
}