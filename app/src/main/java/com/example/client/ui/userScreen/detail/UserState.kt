package com.example.client.ui.userScreen.detail

import com.example.client.data.model.UserDTO
import com.example.client.ui.common.UiEvent

data class UserState(
    val isLoading: Boolean = false,
    val aviso: UiEvent? = null,
    val user: UserDTO? = null
)