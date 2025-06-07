package com.example.client.ui.userScreen.visitor

sealed class VisitorUserEvent {
    data class LoadUser(val username: String) : VisitorUserEvent()
    data class Follow(val username: String) : VisitorUserEvent()
    data class Unfollow(val username: String) : VisitorUserEvent()
    data class FavNote(val noteId: Int) : VisitorUserEvent()
    data class DelFavNote(val noteId: Int) : VisitorUserEvent()
    data class LikeNote(val noteId: Int) : VisitorUserEvent()
    data class DelLikeNote(val noteId: Int) : VisitorUserEvent()
    data class LoadIsFollowing(val username: String) : VisitorUserEvent()
    data object AvisoVisto : VisitorUserEvent()
    data class GetFollowers(val username: String) : VisitorUserEvent()
    data class GetFollowing(val username: String) : VisitorUserEvent()
    data class SelectedNote(val noteId: Int) : VisitorUserEvent()
}
