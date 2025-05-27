package com.example.client.ui.login

import com.example.client.domain.model.user.AuthenticationUser

sealed interface LoginScreenEvents {
    data class Login (val authenticationUser: AuthenticationUser) : LoginScreenEvents
    data class UpdateUsername (val newUsername : String) : LoginScreenEvents
    data class UpdatePassword (val newPassword : String) : LoginScreenEvents
    data object EventDone : LoginScreenEvents
}