package com.example.client.domain.usecases.social

import com.example.client.data.repositories.SocialRepository
import javax.inject.Inject

class FavNoteUseCase @Inject constructor(private val socialRepository: SocialRepository) {
    suspend operator fun invoke(id: Int, username: String) = socialRepository.favNote(id, username)
}