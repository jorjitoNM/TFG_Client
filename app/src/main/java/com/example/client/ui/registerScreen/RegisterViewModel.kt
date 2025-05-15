package com.example.client.ui.registerScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.domain.model.user.CredentialUser
import com.example.client.domain.usecases.user.RegisterUseCase
import com.example.musicapprest.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterState())
    val uiState = _uiState.asStateFlow()

    fun handleEvent(event: RegisterEvents) {
        when (event) {
            is RegisterEvents.Register -> signUp(event.credentialUser)
            is RegisterEvents.UpdateEmail -> updateEmail(event.newEmail)
            is RegisterEvents.UpdateUsername -> updateUsername(event.newUsername)
            is RegisterEvents.UpdatePassword -> updatePassword(event.newPassword)
        }
    }

    private fun signUp(credentialUser: CredentialUser) {
        viewModelScope.launch(dispatcher) {
            when (val result = registerUseCase.invoke(credentialUser)) {

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