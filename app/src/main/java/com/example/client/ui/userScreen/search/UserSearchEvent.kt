package com.example.client.ui.userScreen.search

import com.example.client.data.model.UserDTO

sealed class UserSearchEvent {
    data class UpdateSearchText(val text: String) : UserSearchEvent()
    data class UserClicked(val user: UserDTO) : UserSearchEvent()
    data object AvisoVisto : UserSearchEvent()
    data class OnDeleteUser(val username: String) : UserSearchEvent()
    data class UserSelected (val username: String) : UserSearchEvent()
}
