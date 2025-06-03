package com.example.client.ui.registerScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.R
import com.example.client.common.NetworkResult
import com.example.client.common.StringProvider
import com.example.client.di.IoDispatcher
import com.example.client.domain.model.user.AuthenticationUser
import com.example.client.domain.usecases.user.RegisterUseCase
import com.example.client.domain.usecases.user.firebase.FirebaseRegisterUseCase
import com.example.client.ui.common.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val stringProvider: StringProvider,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val firebaseRegisterUseCase: FirebaseRegisterUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterState())
    val uiState = _uiState.asStateFlow()

    fun handleEvent(event: RegisterEvents) {
        when (event) {
            is RegisterEvents.Register -> register(event.authenticationUser)
            is RegisterEvents.UpdateEmail -> updateEmail(event.newEmail)
            is RegisterEvents.UpdateUsername -> updateUsername(event.newUsername)
            is RegisterEvents.UpdatePassword -> updatePassword(event.newPassword)
            is RegisterEvents.EventDone -> _uiState.update { it.copy(event = null) }
        }
    }

    private fun register(authenticationUser: AuthenticationUser) {
        viewModelScope.launch(dispatcher) {
            when (val result = registerUseCase.invoke(authenticationUser)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            event = UiEvent.ShowSnackbar(stringProvider.getString(R.string.user_registered)),
                            isRegistered = true
                        )
                    }
                    when (val authenticationResponse = firebaseRegisterUseCase.invoke(authenticationUser)) {
                        is NetworkResult.Error -> _uiState.update {
                            it.copy(
                                event = UiEvent.ShowSnackbar(authenticationResponse.message),
                                isLoading = false,
                            )
                        }
                        is NetworkResult.Loading -> _uiState.update {
                            it.copy(
                                isLoading = true
                            )
                        }
                        is NetworkResult.Success -> {}
                    }
                }

                is NetworkResult.Error -> _uiState.update {
                    it.copy(
                        event = UiEvent.ShowSnackbar(result.message),
                        isLoading = false,
                    )
                }

                is NetworkResult.Loading -> _uiState.update {
                    it.copy(
                        isLoading = true
                    )
                }
            }
        }
    }

    private fun updateEmail(newEmail: String) {
        _uiState.update {
            it.copy(
                authenticationUser = it.authenticationUser.copy(email = newEmail)
            )
        }
    }

    private fun updateUsername(newUsername: String) {
        _uiState.update {
            it.copy(
                authenticationUser = it.authenticationUser.copy(username = newUsername)
            )
        }
    }


    private fun updatePassword(newPassword: String) {
        _uiState.update {
            it.copy(
                authenticationUser = it.authenticationUser.copy(password = newPassword)
            )
        }
    }
}