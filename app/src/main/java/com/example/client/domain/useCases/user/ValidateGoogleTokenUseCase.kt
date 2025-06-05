package com.example.client.domain.useCases.user

import com.example.client.common.NetworkResult
import com.example.client.data.remote.security.Token
import com.example.client.data.repositories.AuthenticationRepository
import javax.inject.Inject

class ValidateGoogleTokenUseCase @Inject constructor(
    private val authRepository: AuthenticationRepository
) {
    suspend operator fun invoke(idToken: String): NetworkResult<Token> {
        return authRepository.validateGoogleToken(idToken)
    }
}
