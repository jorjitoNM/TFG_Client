package com.example.client.ui.registerScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.R
import com.example.client.common.NetworkResult
import com.example.client.common.StringProvider
import com.example.client.di.IoDispatcher
import com.example.client.domain.model.user.AuthenticationUser
import com.example.client.domain.usecases.user.RegisterUseCase
import com.example.client.ui.common.UiEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val stringProvider: StringProvider,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterState())
    val uiState = _uiState.asStateFlow()

    fun handleEvent(event: RegisterEvents) {
        when (event) {
            is RegisterEvents.Register -> register(event.authenticationUser)
            is RegisterEvents.UpdateEmail -> updateEmail(event.newEmail)
            is RegisterEvents.UpdateUsername -> updateUsername(event.newUsername)
            is RegisterEvents.UpdatePassword -> updatePassword(event.newPassword)
        }
    }

    private fun register(authenticationUser: AuthenticationUser) {
        viewModelScope.launch(dispatcher) {
            when (val result = registerUseCase.invoke(authenticationUser)) {
                is NetworkResult.Success -> _uiState.value =
                    _uiState.value.copy(event = UiEvent.ShowSnackbar(stringProvider.getString(R.string.user_registered)))

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
                credentialsUser = it.credentialsUser.copy(email = newEmail)
            )
        }
    }

    private fun updateUsername(newUsername: String) {
        _uiState.update {
            it.copy(
                credentialsUser = it.credentialsUser.copy(username = newUsername)
            )
        }
    }


    private fun updatePassword(newPassword: String) {
        _uiState.update {
            it.copy(
                credentialsUser = it.credentialsUser.copy(password = newPassword)
            )
        }
    }
}