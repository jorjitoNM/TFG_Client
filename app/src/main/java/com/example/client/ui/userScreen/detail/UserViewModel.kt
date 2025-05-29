package com.example.client.ui.userScreen.detail

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.common.NetworkResult
import com.example.client.data.model.UserDTO
import com.example.client.di.IoDispatcher
import com.example.client.domain.useCases.user.LoadProfileImageUseCase
import com.example.client.domain.usecases.note.GetMyNote
import com.example.client.domain.usecases.social.GetNoteSavedUseCase
import com.example.client.domain.usecases.user.GetUserUseCase
import com.example.client.ui.common.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getNoteSavedUseCase: GetNoteSavedUseCase,
    private val getMyNote: GetMyNote,
    private val loadProfileImageUseCase: LoadProfileImageUseCase,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserState())
    val uiState: StateFlow<UserState> = _uiState.asStateFlow()

    fun handleEvent(event: UserEvent) {
        when (event) {
            is UserEvent.LoadUser -> loadUser()
            is UserEvent.AvisoVisto -> avisoVisto()
            is UserEvent.SelectTab -> {
                _uiState.value = _uiState.value.copy(selectedTab = event.tab)
                if (event.tab== UserTab.FAVORITES) {
                    getSavedNotes()

                }else {
                    _uiState.value = _uiState.value.copy(notes = emptyList())
                }
            }

            UserEvent.GetMyNote -> getMyNotes()
            is UserEvent.LoadProfileImage -> loadProfileImage(event.imagesUris)


        }
    }

    private fun loadProfileImage(imagesUris: Uri) {
        viewModelScope.launch(dispatcher) {
            loadProfileImageUseCase.invoke(imagesUris).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(
                                user = _uiState.value.user.let { user ->
                                    UserDTO(
                                        user.id,
                                        user.username,
                                        user.password,
                                        user.email,
                                        user.rol,
                                        user.notes,
                                        user.followers,
                                        user.following,
                                        user.profilePhoto,
                                    )
                                }
                            )
                        }
                    }

                    is NetworkResult.Error -> {
                        _uiState.update {
                            it.copy(
                                aviso = UiEvent.ShowSnackbar(result.message),
                                isLoading = false
                            )
                        }
                    }

                    is NetworkResult.Loading -> {
                        _uiState.update {
                            it.copy(
                                isLoading = true
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = getUserUseCase()) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        user = result.data,
                        selectedTab = UserTab.NOTES
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        aviso = UiEvent.ShowSnackbar(result.message),
                        selectedTab = UserTab.NOTES
                    )
                }
                is NetworkResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    private fun getSavedNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = getMyNote()) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            notes = result.data,
                            isLoading = false
                        )
                    }
                }

                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            aviso = UiEvent.ShowSnackbar(result.message),
                            isLoading = false
                        )
                    }
                }

                is NetworkResult.Loading -> {
                    _uiState.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                }
            }
        }
    }
    private fun getMyNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = getNoteSavedUseCase()) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            notes = result.data,
                            isLoading = false
                        )
                    }
                }

                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            aviso = UiEvent.ShowSnackbar(result.message),
                            isLoading = false
                        )
                    }
                }

                is NetworkResult.Loading -> {
                    _uiState.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                }
            }
        }
    }

    private fun avisoVisto() {
        _uiState.value = _uiState.value.copy(aviso = null)
    }
}
