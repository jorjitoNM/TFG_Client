package com.example.client.domain.usecases.social

import com.example.client.data.repositories.UserRepository
import javax.inject.Inject

class GetNoteSavedUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke() = userRepository.getMyNotes()
}