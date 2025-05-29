package com.example.client.domain.useCases.user

import android.net.Uri
import com.example.client.common.NetworkResult
import com.example.client.data.repositories.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadProfileImageUseCase @Inject constructor(private val userRepository: UserRepository) {
      fun invoke(image:Uri): Flow<NetworkResult<String>> = userRepository.loadProfileImage(image)
}