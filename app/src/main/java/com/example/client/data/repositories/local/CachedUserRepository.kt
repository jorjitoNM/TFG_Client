package com.example.client.data.repositories.local

import com.example.client.common.NetworkResult
import com.example.client.data.local.dao.UserDao
import com.example.client.data.local.entities.toEntity
import com.example.client.data.local.entities.toUserDTO
import com.example.client.data.model.UserDTO
import com.example.client.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CachedUserRepository @Inject constructor(
    private val userDao: UserDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend fun getRecentUsers(userLogged: String) = withContext(dispatcher) {
        try {
            val users = userDao.getRecentUsers(userLogged).map { it.toUserDTO() }
            NetworkResult.Success(users)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }

    suspend fun insertOrUpdateRecentUser(user: UserDTO, userLogged: String) = withContext(dispatcher) {
        try {
            userDao.insertUser(user.toEntity(userLogged = userLogged, timestamp = System.currentTimeMillis()))
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }

    suspend fun deleteRecentUser(username: String, userLogged: String) = withContext(dispatcher) {
        try {
            userDao.deleteUserByUsername(username, userLogged)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }
}

