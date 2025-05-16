package com.example.client.ui.login

import com.example.client.domain.model.user.AuthenticationUser
import com.example.client.ui.common.UiEvent

data class LoginScreenState(
    val authenticationUser: AuthenticationUser = AuthenticationUser(),
    val event : UiEvent? = null,
)
