package com.example.client.domain.usecases.follow

import com.example.client.data.repositories.FollowRepository
import javax.inject.Inject

class UnfollowUserUseCase @Inject constructor(
    private val repository: FollowRepository
) {
    suspend operator fun invoke(username: String) = repository.unfollowUser(username)
}
