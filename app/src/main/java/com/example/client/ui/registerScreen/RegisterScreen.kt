package com.example.client.ui.registerScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.client.R
import com.example.client.domain.model.user.CredentialUser

@Composable
fun SignUpScreen (
    registerViewModel: RegisterViewModel = hiltViewModel(),
    navigateToApp: () -> Unit,
    showSnackbar: (String) -> Unit,
) {
    val uiState = registerViewModel.uiState.collectAsStateWithLifecycle()

    SignUpScreenContent(
        credentialUser = uiState.value.credentialsUser,
        onSignUpClick = { registerViewModel.handleEvent(RegisterEvents.Register(uiState.value.credentialsUser)) },
        onEmailChange = { email -> registerViewModel.handleEvent(RegisterEvents.UpdateEmail(email)) },
        onUsernameChange = { username -> registerViewModel.handleEvent(RegisterEvents.UpdateUsername(username)) },
        onPasswordChange = { password -> registerViewModel.handleEvent(RegisterEvents.UpdatePassword(password)) }
    )
}

@Composable
fun SignUpScreenContent (
    credentialUser: CredentialUser,
    onSignUpClick : (CredentialUser) -> Unit,
    onEmailChange : (String) -> Unit,
    onUsernameChange : (String) -> Unit,
    onPasswordChange : (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.weight(0.1f).fillMaxSize().background(Color.Red))
        Row(modifier = Modifier.weight(0.3f).fillMaxSize().background(Color.Blue)) {
            Image(painter = painterResource(R.drawable.app_logo_v1), modifier = Modifier.fillMaxSize(), alignment = Alignment.Center, contentDescription = stringResource(R.string.app_logo))
        }
        Row(modifier = Modifier.weight(0.2f).fillMaxSize().background(Color.Green)) {

        }
        Row(modifier = Modifier.weight(0.25f).fillMaxSize().background(Color.Cyan)) {

        }
        Row(modifier = Modifier.weight(0.15f).fillMaxSize().background(Color.Magenta)) {

        }
    }
}


@Preview
@Composable
fun SignUpScreenPreview () {
    SignUpScreenContent(CredentialUser(),{},{},{},{})
}