package com.example.client.data.repositories.firebase

import com.example.client.common.NetworkResult
import com.example.client.data.firebase.auth.FirebaseAuthenticator
import com.example.client.domain.model.user.AuthenticationUser
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val firebaseAuthenticator: FirebaseAuthenticator
) {
    suspend fun register(authenticationUser: AuthenticationUser) : NetworkResult<Unit> =
        firebaseAuthenticator.register(authenticationUser)

    suspend fun login(authenticationUser: AuthenticationUser) : NetworkResult<Unit> =
        firebaseAuthenticator.login(authenticationUser)


}