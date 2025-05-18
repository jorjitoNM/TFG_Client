package com.example.client.ui.userScreen.search

sealed class UserSearchEvent {
    data class UpdateSearchText(val text: String) : UserSearchEvent()
    data object AvisoVisto : UserSearchEvent()
}