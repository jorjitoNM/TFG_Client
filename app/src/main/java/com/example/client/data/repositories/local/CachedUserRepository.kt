package com.example.client.data.repositories.local

import com.example.client.common.NetworkResult
import com.example.client.data.local.dao.UserDao
import com.example.client.data.local.entities.UserEntity
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
){
    suspend fun getUsers() = withContext(dispatcher) {
        try {
           val users = userDao.getUsers().map { it.toUserDTO() }
            NetworkResult.Success(users)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }

    suspend fun getUser (username : String) = withContext(dispatcher) {
        try {
            val user = userDao.getUser(username).toUserDTO()
            NetworkResult.Success(user)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }

    suspend fun insertUser (user : UserDTO) =withContext(dispatcher) {
        try {
            userDao.insertUser(user.toEntity())
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }
}