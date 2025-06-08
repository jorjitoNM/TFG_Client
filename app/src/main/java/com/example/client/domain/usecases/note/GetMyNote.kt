package com.example.client.domain.usecases.note

import com.example.client.data.repositories.UserRepository
import javax.inject.Inject

class GetMyNoteUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke() = userRepository.getMyNotes()
}