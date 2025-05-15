package com.example.client.domain.usecases.user

import com.example.client.data.repositories.UserRepository
import com.example.client.domain.model.user.CredentialUser
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val userRepository : UserRepository
) {
    suspend fun invoke (credentialUser: CredentialUser) = userRepository.signUp(credentialUser)
}