package com.example.client.ui.registerScreen

import com.example.client.domain.model.user.AuthenticationUser
import com.example.client.ui.common.UiEvent

data class RegisterState (
    val credentialsUser : AuthenticationUser = AuthenticationUser(),
    val event : UiEvent? = null,
    val isLoading : Boolean = false,
)