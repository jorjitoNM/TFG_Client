package com.example.client.domain.usecases.social

import com.example.client.data.repositories.SocialRepository
import javax.inject.Inject

class DelLikeNoteUseCase @Inject constructor(private val socialRepository: SocialRepository) {
    suspend fun invoke(noteId: Int) = socialRepository.delLikeNote(noteId)
}