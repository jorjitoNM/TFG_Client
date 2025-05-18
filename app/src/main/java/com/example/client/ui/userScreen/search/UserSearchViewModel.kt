package com.example.client.ui.userScreen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.common.NetworkResult
import com.example.client.domain.usecases.user.GetAllUserStartsWithTextUseCase
import com.example.client.ui.common.UiEvent
import com.example.client.ui.userScreen.detail.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSearchViewModel @Inject constructor(
    private val getAllUserStartsWithTextUseCase: GetAllUserStartsWithTextUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(UserSearchState())
    val uiState: StateFlow<UserSearchState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun handleEvent(event: UserSearchEvent) {
        when (event) {
            is UserSearchEvent.UpdateSearchText -> {
                _uiState.value = _uiState.value.copy(searchText = event.text)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(300) // debounce para evitar demasiadas peticiones
                    getAllUserStartsWithText(event.text)
                }
            }
            UserSearchEvent.AvisoVisto -> {
                _uiState.value = _uiState.value.copy(aviso = null)
            }
        }
    }

    private suspend fun getAllUserStartsWithText(text: String) {
        if (text.isBlank()) {
            _uiState.value = _uiState.value.copy(users = emptyList(), showEmptyState = false, isLoading = false)
            return
        }
        _uiState.value = _uiState.value.copy(isLoading = true)
        when (val result = getAllUserStartsWithTextUseCase(text)) {
            is NetworkResult.Success -> {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    users = result.data,
                    showEmptyState = result.data.isEmpty()
                )
            }
            is NetworkResult.Error -> {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    aviso = UiEvent.ShowSnackbar(result.message),
                    showEmptyState = false
                )
            }
            is NetworkResult.Loading -> {
                _uiState.value = _uiState.value.copy(isLoading = true)
            }
        }
    }
}