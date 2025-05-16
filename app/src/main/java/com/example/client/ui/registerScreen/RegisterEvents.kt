package com.example.client.ui.registerScreen

import com.example.client.domain.model.user.AuthenticationUser

sealed interface RegisterEvents {

    data class Register (val authenticationUser: AuthenticationUser) : RegisterEvents
    data class UpdateEmail (val newEmail : String) : RegisterEvents
    data class UpdateUsername (val newUsername : String) : RegisterEvents
    data class UpdatePassword (val newPassword : String) : RegisterEvents
}