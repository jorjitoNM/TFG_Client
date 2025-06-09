package com.example.client.domain.usecases.follow

import com.example.client.data.repositories.FollowRepository
import javax.inject.Inject

class GetMyFollowingUseCase @Inject constructor(private val followRepository: FollowRepository) {
    suspend operator fun invoke() = followRepository.getMyFollowing()
}