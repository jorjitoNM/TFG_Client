package com.example.client.ui.registerScreen

import com.example.client.domain.model.user.CredentialUser
import com.example.client.ui.common.UiEvent

data class RegisterState (
    val credentialsUser : CredentialUser = CredentialUser(),
    val event : UiEvent? = null,
)