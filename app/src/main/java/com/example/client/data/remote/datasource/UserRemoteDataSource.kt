package com.example.client.data.remote.datasource

import com.example.client.data.remote.service.UserService
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(private val userService: UserService) :
    BaseApiResponse() {


    suspend fun getUser() = safeApiCall { userService.getUser() }

    suspend fun getAllUserStartsWithText(text: String) = safeApiCall { userService.getAllUserStartsWithText(text) }

    suspend fun getMyNotes() = safeApiCall { userService.getMyNotes() }

    suspend fun getLikedNotes() = safeApiCall { userService.getLikedNotes() }

    suspend fun getUserNotes(username: String) = safeApiCall { userService.getUserNotes(username) }

    suspend fun getUserInfo(username: String) = safeApiCall { userService.getUserInfo(username) }

    suspend fun getFirebaseId () = safeApiCall { userService.getFirebaseId() }
}