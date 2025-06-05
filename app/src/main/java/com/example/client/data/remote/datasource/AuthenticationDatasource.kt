package com.example.client.data.remote.datasource

import com.example.client.common.NetworkResult
import com.example.client.data.remote.security.Token
import com.example.client.data.remote.service.AuthenticationService
import com.example.client.domain.model.user.AuthenticationUser
import javax.inject.Inject

class AuthenticationDatasource @Inject constructor(
    private val authenticationService: AuthenticationService
) : BaseApiResponse() {

    suspend fun register (authenticationUser: AuthenticationUser) : NetworkResult<Unit> =
        safeApiCall { authenticationService.register(authenticationUser) }

    suspend fun login(authenticationUser: AuthenticationUser): NetworkResult<Token> =
        safeApiCall { authenticationService.login(authenticationUser) }

    suspend fun validateGoogleToken(idToken: String): NetworkResult<Token> =
        safeApiCall {
            authenticationService.validateGoogleLogin(mapOf("token" to idToken))
        }

}