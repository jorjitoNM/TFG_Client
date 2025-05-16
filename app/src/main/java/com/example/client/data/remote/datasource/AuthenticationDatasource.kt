package com.example.client.data.remote.datasource

import com.example.client.common.NetworkResult
import com.example.client.data.remote.service.AuthenticationService
import com.example.client.domain.model.user.AuthenticationUser
import javax.inject.Inject

class AuthenticationDatasource @Inject constructor(
    private val authenticationService: AuthenticationService
) : BaseApiResponse() {

    suspend fun register (authenticationUser: AuthenticationUser) : NetworkResult<Unit> =
        safeApiCall { authenticationService.register(authenticationUser) }
}