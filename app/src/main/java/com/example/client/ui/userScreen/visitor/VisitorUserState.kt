package com.example.client.ui.userScreen.visitor

import com.example.client.data.model.NoteDTO
import com.example.client.data.model.UserDTO
import com.example.client.ui.common.UiEvent

data class VisitorUserState(
    val user: UserDTO? = null,
    val notes: List<NoteDTO> = emptyList(),
    val isFollowing: Boolean = false,
    val isLoading: Boolean = false,
    val aviso: UiEvent? = null,
    val followers: List<UserDTO> = emptyList(),
    val following: List<UserDTO> = emptyList(),
)