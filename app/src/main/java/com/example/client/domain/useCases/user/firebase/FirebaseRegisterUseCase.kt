package com.example.client.domain.usecases.user.firebase

import com.example.client.common.NetworkResult
import com.example.client.data.repositories.firebase.FirebaseRepository
import com.example.client.domain.model.user.AuthenticationUser
import javax.inject.Inject

class FirebaseRegisterUseCase @Inject constructor(
    private val firebaseRepository : FirebaseRepository
) {
    suspend fun invoke (authenticationUser: AuthenticationUser) : NetworkResult<Unit> =
        firebaseRepository.register(authenticationUser)
}