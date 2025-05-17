package com.example.client.data.remote.service


import com.example.client.data.model.UserDTO
import retrofit2.Response
import retrofit2.http.Query

interface UserService {

    suspend fun getUser(@Query("username") username: String): Response<UserDTO>

}