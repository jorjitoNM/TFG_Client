package com.example.client.data.remote.datasource

import com.example.client.data.remote.service.FollowService
import javax.inject.Inject

class FollowRemoteDataSource @Inject constructor(
    private val followService: FollowService
) : BaseApiResponse() {

    suspend fun followUser(username: String) =
        safeApiCall { followService.followUser(username) }

    suspend fun unfollowUser(username: String) =
        safeApiCall { followService.unfollowUser(username) }

    suspend fun getFollowers(username: String) =
        safeApiCall { followService.getFollowers(username) }

    suspend fun getFollowing(username: String) =
        safeApiCall { followService.getFollowing(username) }

    suspend fun getMyFollowing() =
        safeApiCall { followService.getMyFollowing() }

    suspend fun getMyFollowers() =
        safeApiCall { followService.getMyFollowers() }

    suspend fun isFollowing(username: String) =
        safeApiCall { followService.isFollowing(username) }
}
