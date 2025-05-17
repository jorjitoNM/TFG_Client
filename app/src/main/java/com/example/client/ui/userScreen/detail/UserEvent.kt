package com.example.client.ui.userScreen.detail

sealed class UserEvent {
    data object LoadUser: UserEvent()
    data object AvisoVisto : UserEvent()
}