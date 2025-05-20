package com.example.client.ui.userScreen.detail

sealed class UserEvent {
    data object LoadUser: UserEvent()
    data object AvisoVisto : UserEvent()
    data class SelectTab(val tab: UserTab) : UserEvent()
    data object GetMyNote : UserEvent()
}