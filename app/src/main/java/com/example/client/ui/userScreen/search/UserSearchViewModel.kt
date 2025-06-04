package com.example.client.ui.userScreen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.common.NetworkResult
import com.example.client.data.model.UserDTO
import com.example.client.domain.usecases.user.GetAllUserStartsWithTextUseCase
import com.example.client.domain.usecases.user.GetUserUseCase
import com.example.client.domain.usecases.user.local.DeleteCachedUserUseCase
import com.example.client.domain.usecases.user.local.GetCachedUsersUseCase
import com.example.client.domain.usecases.user.local.InsertCachedUserUseCase
import com.example.client.ui.common.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSearchViewModel @Inject constructor(
    private val getAllUserStartsWithTextUseCase: GetAllUserStartsWithTextUseCase,
    private val getCachedUsersUseCase: GetCachedUsersUseCase,
    private val insertCachedUserUseCase: InsertCachedUserUseCase,
    private val deleteCachedUserUseCase: DeleteCachedUserUseCase,
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(UserSearchState())
    val uiState: StateFlow<UserSearchState> = _uiState.asStateFlow()

    fun getLoggedUser() {
        viewModelScope.launch {
            val user = getUserUseCase()
            when (user) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(userLogged = user.data.username) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(aviso = UiEvent.ShowSnackbar(user.message)) }
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
    //aqui va el preference
    init {
        loadRecentUsers()
    }


    fun handleEvent(event: UserSearchEvent) {
        when (event) {
            is UserSearchEvent.UpdateSearchText -> {
                _uiState.value = _uiState.value.copy(
                    searchText = event.text,
                )
                 viewModelScope.launch {
                    if (event.text.isBlank()) {
                        loadRecentUsers()
                    } else {
                        getAllUserStartsWithText(event.text)
                    }
                }
            }
            is UserSearchEvent.UserClicked -> addRecentUser(event.user)
            is UserSearchEvent.AvisoVisto -> _uiState.value = _uiState.value.copy(aviso = null)
            is UserSearchEvent.OnDeleteUser -> deleteRecentUser(event.username)

        }
    }

    private fun loadRecentUsers() {
        val loggedUser = _uiState.value.userLogged
        if (loggedUser == null) {
            // Si aún no está disponible, puedes esperar o mostrar un loading
            getLoggedUser() // Lanza la carga si aún no se ha hecho
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = getCachedUsersUseCase.invoke(loggedUser)) {
                is NetworkResult.Success -> _uiState.value = _uiState.value.copy(
                    users = result.data,
                    showEmptyState = result.data.isEmpty(),
                    isLoading = false
                )

                is NetworkResult.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    aviso = UiEvent.ShowSnackbar(result.message),
                    showEmptyState = false
                )

                is NetworkResult.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
            }
        }
    }

    private fun addRecentUser(user: UserDTO) {
        val loggedUser = _uiState.value.userLogged
        if (loggedUser == null) {
            // Si aún no está disponible, puedes esperar o mostrar un loading
            getLoggedUser() // Lanza la carga si aún no se ha hecho
            return
        }
        viewModelScope.launch {
            when (val result = insertCachedUserUseCase.invoke(user, loggedUser)) {
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(aviso = UiEvent.ShowSnackbar(result.message))
                }
                is NetworkResult.Success -> {
                    if (_uiState.value.searchText.isBlank()) {
                        loadRecentUsers()
                    }
                }
                is NetworkResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }


    private fun deleteRecentUser (username:String){
        val loggedUser = _uiState.value.userLogged
        if (loggedUser == null) {
            // Si aún no está disponible, puedes esperar o mostrar un loading
            getLoggedUser() // Lanza la carga si aún no se ha hecho
            return
        }
        viewModelScope.launch {
            when (val result = deleteCachedUserUseCase.invoke(username,loggedUser)) {
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(aviso = UiEvent.ShowSnackbar(result.message))
                }
                is NetworkResult.Success -> {
                    if (_uiState.value.searchText.isBlank()) {
                        loadRecentUsers()
                    }
                }
                is NetworkResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    private suspend fun getAllUserStartsWithText(text: String) {
        if (text.isBlank()) {
            _uiState.value =
                _uiState.value.copy(users = emptyList(), showEmptyState = false, isLoading = false)
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