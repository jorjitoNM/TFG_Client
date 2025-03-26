package com.example.client.ui.common

sealed class UiEvent {
    data object PopBackStack : UiEvent()
    data class ShowSnackbar(
        val message: String,
        val action: String? = null
    ) : UiEvent()
}
