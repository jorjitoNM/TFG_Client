package com.example.client.data.remote.datasource

import com.example.client.data.remote.service.UserService
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(private val userService: UserService) :
    BaseApiResponse() {


    suspend fun getUser(username: String) = safeApiCall {
        userService.getUser(username)
    }
    suspend fun getAllUserStartsWithText(text: String) = safeApiCall { userService.getAllUserStartsWithText(text) }

    suspend fun getMyNotes(username: String) = safeApiCall { userService.getMyNotes(username) }
}