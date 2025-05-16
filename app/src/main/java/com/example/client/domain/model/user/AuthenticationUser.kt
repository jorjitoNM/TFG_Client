package com.example.client.domain.model.user

data class AuthenticationUser(
    val email : String = "",
    val username : String = "",
    val password : String = "",
)