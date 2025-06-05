package com.example.client.domain.usecases.user

import com.example.client.common.NetworkResult
import com.example.client.data.repositories.AuthenticationRepository
import com.example.client.domain.model.user.AuthenticationUser
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend fun invoke (authenticationUser: AuthenticationUser) : NetworkResult<Unit> =
        authenticationRepository.register(authenticationUser)
}