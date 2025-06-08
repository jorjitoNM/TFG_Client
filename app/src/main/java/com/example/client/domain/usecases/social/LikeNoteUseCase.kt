package com.example.client.domain.usecases.social

import com.example.client.common.NetworkResult
import com.example.client.data.repositories.SocialRepository
import javax.inject.Inject

class LikeNoteUseCase @Inject constructor(
    private val socialRepository : SocialRepository
) {
    suspend fun invoke (noteId : Int) : NetworkResult<Unit>
    = socialRepository.likeNote(noteId)
}