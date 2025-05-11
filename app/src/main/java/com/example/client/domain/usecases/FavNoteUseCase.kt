package com.example.client.domain.useCases

import com.example.client.data.remote.NoteRepository
import javax.inject.Inject

class FavNoteUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(id: Int, username: String) = noteRepository.favNote(id,username)
}