package com.example.client.domain.usecases.user

import com.example.client.data.repositories.UserRepository
import javax.inject.Inject

class GetAllUserStartsWithTextUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(text: String) = userRepository.getAllUserStartsWithText(text)
}