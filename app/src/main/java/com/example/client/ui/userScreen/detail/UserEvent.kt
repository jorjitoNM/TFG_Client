package com.example.client.ui.userScreen.detail

sealed class UserEvent {
    data object LoadUser : UserEvent()
    data object AvisoVisto : UserEvent()
    data class SelectTab(val tab: UserTab) : UserEvent()
    data class LikeNote(val noteId: Int) : UserEvent()
    data class FavNote(val noteId: Int) : UserEvent()
    data class DelFavNote(val noteId: Int) : UserEvent()
    data class DelLikeNote(val noteId: Int) : UserEvent()
    data object GetMyNote : UserEvent()
}