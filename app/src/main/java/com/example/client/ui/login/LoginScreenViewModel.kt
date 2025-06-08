package com.example.client.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.common.NetworkResult
import com.example.client.data.repositories.SecurePreferencesRepository
import com.example.client.di.IoDispatcher
import com.example.client.domain.model.user.AuthenticationUser
import com.example.client.domain.usecases.authentication.SaveTokenUseCase
import com.example.client.domain.usecases.user.LoginUseCase
import com.example.client.domain.usecases.user.firebase.FirebaseLoginUseCase
import com.example.client.ui.common.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val saveTokenUseCase: SaveTokenUseCase,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val firebaseLoginUseCase: FirebaseLoginUseCase,
    private val securePreferencesRepository: SecurePreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginScreenState())
    val uiState = _uiState.asStateFlow()

    fun handleEvent(event: LoginScreenEvents) {
        when (event) {
            is LoginScreenEvents.Login -> login(event.authenticationUser)
            is LoginScreenEvents.UpdateEmail -> updateEmail(event.newEmail)
            is LoginScreenEvents.UpdatePassword -> updatePassword(event.newPassword)
            is LoginScreenEvents.EventDone -> _uiState.update { it.copy(event = null) }
            LoginScreenEvents.LoginWithBiometrics -> loginWithBiometrics()
        }
    }

    private fun login(authenticationUser: AuthenticationUser) {
        viewModelScope.launch(dispatcher) {
            when (val result = loginUseCase.invoke(authenticationUser)) {
                is NetworkResult.Success -> {
                    saveTokenUseCase.invoke(result.data)
                    securePreferencesRepository.saveCredentials(
                        email = authenticationUser.email,
                        password = authenticationUser.password
                    )
                    when (val authenticationResponse = firebaseLoginUseCase.invoke(authenticationUser)) {
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
                    _uiState.update {
                        it.copy(
                            isValidated = true
                        )
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

    private fun loginWithBiometrics() {
        viewModelScope.launch(dispatcher) {
            val (email, password) = securePreferencesRepository.getCredentials()
            if (!email.isNullOrEmpty() && !password.isNullOrEmpty()) {
                login(AuthenticationUser(email=email, password = password))
            } else {
                _uiState.update {
                    it.copy(event = UiEvent.ShowSnackbar("Stored credentials not found"))
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


    private fun updatePassword(newPassword: String) {
        _uiState.update {
            it.copy(
                authenticationUser = it.authenticationUser.copy(password = newPassword)
            )
        }
    }
}