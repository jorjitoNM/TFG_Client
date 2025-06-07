package com.example.client.ui.userScreen

sealed class DetailNavigationEvent {
    data class NavigateToMyNoteDetail(val noteId: Int) : DetailNavigationEvent()
    data class NavigateToNormalNoteDetail(val noteId: Int) : DetailNavigationEvent()
    data object None : DetailNavigationEvent()
}
