package com.example.client.data.remote.service

import com.example.client.data.remote.security.Token
import com.example.client.domain.model.user.AuthenticationUser
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthenticationService {

    @POST("login")
    suspend fun login (@Body authenticationUser : AuthenticationUser) : Response<Token>

    @POST("register")
    suspend fun register (@Body authenticationUser: AuthenticationUser) : Response<Unit>

    @GET("refresh")
    suspend fun refreshToken(
        @Header("Authorization") token: String,
    ): Response<String>
}