package com.example.client.ui.registerScreen

import com.example.client.domain.model.user.CredentialUser

sealed interface RegisterEvents {

    data class Register (val credentialUser: CredentialUser) : RegisterEvents
    data class UpdateEmail (val newEmail : String) : RegisterEvents
    data class UpdateUsername (val newUsername : String) : RegisterEvents
    data class UpdatePassword (val newPassword : String) : RegisterEvents
}