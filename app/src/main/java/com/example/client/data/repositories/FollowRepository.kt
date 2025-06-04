package com.example.client.data.repositories

import com.example.client.common.NetworkResult
import com.example.client.data.remote.datasource.FollowRemoteDataSource
import com.example.client.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FollowRepository @Inject constructor(
    private val followRemoteDataSource: FollowRemoteDataSource,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend fun followUser(username: String) = withContext(dispatcher) {
        try {
            followRemoteDataSource.followUser(username)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }

    suspend fun unfollowUser(username: String) = withContext(dispatcher) {
        try {
            followRemoteDataSource.unfollowUser(username)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }

    suspend fun getFollowers(username: String) = withContext(dispatcher) {
        try {
            followRemoteDataSource.getFollowers(username)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }

    suspend fun getFollowing(username: String) = withContext(dispatcher) {
        try {
            followRemoteDataSource.getFollowing(username)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }

    suspend fun isFollowing(username: String) = withContext(dispatcher) {
        try {
            followRemoteDataSource.isFollowing(username)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }
}
