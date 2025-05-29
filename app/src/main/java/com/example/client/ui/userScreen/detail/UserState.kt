package com.example.client.ui.userScreen.detail

import android.net.Uri
import com.example.client.data.model.NoteDTO
import com.example.client.data.model.UserDTO
import com.example.client.ui.common.UiEvent

data class UserState(
    val isLoading: Boolean = false,
    val aviso: UiEvent? = null,
    val user: UserDTO = UserDTO(),
    val selectedTab: UserTab = UserTab.NOTES,
    val notes: List<NoteDTO> = emptyList(),
    val profileImageUri: Uri? = null
)

enum class UserTab { NOTES, FAVORITES, LIKES }