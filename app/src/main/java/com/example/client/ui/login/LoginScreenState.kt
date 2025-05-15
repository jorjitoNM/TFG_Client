package com.example.client.ui.login

import com.example.client.domain.model.user.CredentialUser
import com.example.client.ui.common.UiEvent

data class LoginScreenState(
    val credentialUser: CredentialUser = CredentialUser(),
    val event : UiEvent? = null,
)
