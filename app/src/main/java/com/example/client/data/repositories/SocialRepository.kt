package com.example.client.data.repositories

import com.example.client.common.NetworkResult
import com.example.client.data.remote.datasource.SocialRemoteDataSource
import com.example.musicapprest.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SocialRepository @Inject constructor(
    private val socialRemoteDataSource: SocialRemoteDataSource,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {

    suspend fun getNotes() = withContext(dispatcher) {
        try {
            socialRemoteDataSource.getNotes()
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }

    suspend fun favNote(id: Int, username: String) = withContext(dispatcher) {
        try {
            socialRemoteDataSource.favNote(id, username)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }
}