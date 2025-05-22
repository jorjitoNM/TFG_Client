package com.example.client.domain.usecases.user.local

import com.example.client.data.repositories.local.CachedUserRepository
import javax.inject.Inject

class GetCachedUserUseCase @Inject constructor(
    private val repository: CachedUserRepository
) {
    suspend operator fun invoke(username: String) = repository.getUser(username)
}