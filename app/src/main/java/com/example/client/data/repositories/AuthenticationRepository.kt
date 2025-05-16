package com.example.client.data.repositories

import com.example.client.common.NetworkResult
import com.example.client.data.remote.datasource.AuthenticationDatasource
import com.example.client.domain.model.user.AuthenticationUser
import javax.inject.Inject

class AuthenticationRepository @Inject constructor(
    private val authenticationDatasource: AuthenticationDatasource
) {

    suspend fun register (authenticationUser: AuthenticationUser) : NetworkResult<Unit>
    = authenticationDatasource.register(authenticationUser)
}