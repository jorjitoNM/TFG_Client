package com.example.client.ui.userScreen.detail

sealed class UserEvent {
    data class LoadUser (val userId: String): UserEvent()
    data object AvisoVisto : UserEvent()
}