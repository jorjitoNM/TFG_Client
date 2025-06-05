package com.example.client.ui.userScreen.search

import com.example.client.data.model.UserDTO
import com.example.client.ui.common.UiEvent

data class UserSearchState(
    val users: List<UserDTO> = emptyList(),
    val isLoading: Boolean = false,
    val aviso: UiEvent? = null,
    val searchText: String = "",
    val showEmptyState: Boolean = false,
    val userLogged : String? = null,
    val selectedUser : String = ""
)