package com.example.client.domain.usecases.social

import com.example.client.data.SocialRepository
import java.util.UUID
import javax.inject.Inject

class LikeNoteUseCase @Inject constructor(private val socialRepository: SocialRepository) {
    suspend fun invoke (noteId : Int) = socialRepository.likeNote(noteId, UUID.randomUUID())
}