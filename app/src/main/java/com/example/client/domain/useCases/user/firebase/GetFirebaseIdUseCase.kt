package com.example.client.domain.usecases.user.firebase

import com.example.client.common.NetworkResult
import com.example.client.data.remote.service.UserService
import com.example.client.data.repositories.UserRepository
import javax.inject.Inject

class GetFirebaseIdUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun invoke () : NetworkResult<UserService.FirebaseIdResponse> =
        userRepository.getFirebaseId()
}