package com.example.client.domain.usecases.user.local

import com.example.client.data.model.UserDTO
import com.example.client.data.repositories.local.CachedUserRepository
import javax.inject.Inject

class InsertCachedUserUseCase @Inject constructor(
    private val repository: CachedUserRepository
) {
    suspend operator fun invoke(user: UserDTO, userLogged: String) = repository.insertOrUpdateRecentUser(user, userLogged)
}