package com.example.client.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.common.NetworkResult
import com.example.client.data.remote.security.Token
import com.example.client.data.repositories.SecurePreferencesRepository
import com.example.client.di.IoDispatcher
import com.example.client.domain.model.user.AuthenticationUser
import com.example.client.domain.useCases.authentication.SaveTokenUseCase
import com.example.client.domain.useCases.user.ValidateGoogleTokenUseCase
import com.example.client.domain.usecases.user.LoginUseCase
import com.example.client.ui.common.UiEvent
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val saveTokenUseCase: SaveTokenUseCase,
    private val securePreferencesRepository: SecurePreferencesRepository,
    private val auth: FirebaseAuth,
    private val validateGoogleTokenUseCase: ValidateGoogleTokenUseCase,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginScreenState())
    val uiState = _uiState.asStateFlow()

    fun handleEvent(event: LoginScreenEvents) {
        when (event) {
            is LoginScreenEvents.Login -> login(event.authenticationUser)
            is LoginScreenEvents.UpdateUsername -> updateUsername(event.newUsername)
            is LoginScreenEvents.UpdatePassword -> updatePassword(event.newPassword)
            is LoginScreenEvents.EventDone -> _uiState.update { it.copy(event = null) }
            LoginScreenEvents.LoginWithBiometrics -> loginWithBiometrics()
        }
    }

    fun signInWithGoogle(googleCredential: AuthCredential) {
        viewModelScope.launch(dispatcher) {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val authResult = auth.signInWithCredential(googleCredential).await()
                val user = authResult.user

                user?.let { firebaseUser ->
                    val idToken = firebaseUser.getIdToken(false).await().token

                    idToken?.let { token ->
                        when (val result = validateGoogleTokenUseCase(token)) {
                            is NetworkResult.Success -> {
                                saveTokenUseCase(result.data)
                                _uiState.update {
                                    it.copy(
                                        isValidated = true,
                                        isLoading = false,
                                    )
                                }
                            }

                            is NetworkResult.Error -> {
                                _uiState.update {
                                    it.copy(
                                        event = UiEvent.ShowSnackbar(result.message),
                                        isLoading = false,
                                    )
                                }
                            }

                            is NetworkResult.Loading -> _uiState.update {
                                it.copy(
                                    isLoading = true
                                )
                            }
                        }
                    } ?: run {
                        _uiState.update {
                            it.copy(event = UiEvent.ShowSnackbar("Failed to get ID token"))
                        }
                    }
                } ?: run {
                    _uiState.update {
                        it.copy(event = UiEvent.ShowSnackbar("Google sign-in failed"))
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(event = UiEvent.ShowSnackbar(e.message ?: "Google sign-in error"))
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun login(authenticationUser: AuthenticationUser) {
        viewModelScope.launch(dispatcher) {
            when (val result = loginUseCase.invoke(authenticationUser)) {

                is NetworkResult.Success -> {

                    saveTokenUseCase.invoke(result.data)
                    securePreferencesRepository.saveCredentials(
                        username = authenticationUser.username,
                        password = authenticationUser.password
                    )
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
            val (username, password) = securePreferencesRepository.getCredentials()
            if (!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
                login(AuthenticationUser(username = username, password = password))
            } else {
                _uiState.update {
                    it.copy(event = UiEvent.ShowSnackbar("Stored credentials not found"))
                }
            }
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