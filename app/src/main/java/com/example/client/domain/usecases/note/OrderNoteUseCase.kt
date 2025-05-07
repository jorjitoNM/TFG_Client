package com.example.client.domain.usecases.note

import com.example.client.data.remote.NoteRepository
import javax.inject.Inject

class OrderNoteUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(asc: Boolean) = noteRepository.orderNote(asc)
}