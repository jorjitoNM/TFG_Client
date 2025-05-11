package com.example.client.domain.useCases

import com.example.client.data.remote.NoteRepository
import javax.inject.Inject

class RateNoteUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(id: Int, rating: Int) = noteRepository.rateNote(id, rating)
}