package com.example.client.data.repositories

import com.example.client.common.NetworkResult
import com.example.client.data.remote.datasource.UserRemoteDataSource
import com.example.client.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val userRemoteDataSource: UserRemoteDataSource
    ) {
    suspend fun getUser() = withContext(dispatcher) {
        try {
            userRemoteDataSource.getUser("user1")
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }
    suspend fun getMyNotes()= withContext(dispatcher) {
        try {
            userRemoteDataSource.getMyNotes("user1")
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }
    suspend fun getAllUserStartsWithText(text: String) = withContext(dispatcher) {
        try {
            userRemoteDataSource.getAllUserStartsWithText(text)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }

}