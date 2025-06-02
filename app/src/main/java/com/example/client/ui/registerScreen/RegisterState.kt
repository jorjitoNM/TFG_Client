package com.example.client.ui.registerScreen

import com.example.client.domain.model.user.AuthenticationUser
import com.example.client.ui.common.UiEvent

data class RegisterState (
    val authenticationUser : AuthenticationUser = AuthenticationUser(),
    val isRegistered : Boolean = false,
    val event : UiEvent? = null,
    val isLoading : Boolean = false,
)