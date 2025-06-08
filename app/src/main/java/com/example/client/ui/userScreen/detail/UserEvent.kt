package com.example.client.ui.userScreen.detail

import android.net.Uri

sealed class UserEvent {
    data object AvisoVisto : UserEvent()
    data class SelectTab(val tab: UserTab) : UserEvent()
    data class LikeNote(val noteId: Int) : UserEvent()
    data class FavNote(val noteId: Int) : UserEvent()
    data class DelFavNote(val noteId: Int) : UserEvent()
    data class DelLikeNote(val noteId: Int) : UserEvent()
    data object GetMyNote : UserEvent()
    data class SaveProfileImage (val imageUri : Uri) : UserEvent()
    data object GetFollowing : UserEvent()
    data object GetFollowers : UserEvent()
    data class SelectedNote(val noteId: Int, val isMyNote: Boolean) : UserEvent()
    data object NavigationConsumed : UserEvent()
    data class SaveScrollPosition(val tab: UserTab, val index: Int, val offset: Int) : UserEvent()
    data object LoadUser : UserEvent()

}