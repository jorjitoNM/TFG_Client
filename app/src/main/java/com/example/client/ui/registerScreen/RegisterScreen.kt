package com.example.client.ui.registerScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.client.R
import com.example.client.domain.model.user.AuthenticationUser
import com.example.client.ui.startScreen.AuthenticationActionButton

@Composable
fun SignUpScreen (
    registerViewModel: RegisterViewModel = hiltViewModel(),
    navigateToApp: () -> Unit,
    showSnackbar: (String) -> Unit,
) {
    val uiState = registerViewModel.uiState.collectAsStateWithLifecycle()

    SignUpScreenContent(
        authenticationUser = uiState.value.credentialsUser,
        onSignUpClick = { registerViewModel.handleEvent(RegisterEvents.Register(uiState.value.credentialsUser)) },
        onEmailChange = { email -> registerViewModel.handleEvent(RegisterEvents.UpdateEmail(email)) },
        onUsernameChange = { username -> registerViewModel.handleEvent(RegisterEvents.UpdateUsername(username)) },
        onPasswordChange = { password -> registerViewModel.handleEvent(RegisterEvents.UpdatePassword(password)) }
    )
}

@Composable
fun SignUpScreenContent (
    authenticationUser: AuthenticationUser,
    onSignUpClick : (AuthenticationUser) -> Unit,
    onEmailChange : (String) -> Unit,
    onUsernameChange : (String) -> Unit,
    onPasswordChange : (String) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.start_screen_background_big),
            contentScale = ContentScale.FillHeight,
            contentDescription = stringResource(R.string.start_Screen_background),
            modifier = Modifier.fillMaxSize()
        )
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.weight(0.15f).fillMaxSize())
        Row(modifier = Modifier.weight(0.2f).fillMaxSize()) {
            Image(
                painter = painterResource(R.drawable.app_logo_v1),
                modifier = Modifier.fillMaxSize(),
                alignment = Alignment.Center,
                contentDescription = stringResource(R.string.app_logo)
            )
        }
        Spacer(modifier = Modifier.weight(0.05f).fillMaxSize())
        Row(modifier = Modifier.weight(0.3f).fillMaxSize()) {
            RegisterFields (
                modifier = Modifier,
                onEmailChange = onEmailChange,
                onUsernameChange = onUsernameChange,
                onPasswordChange = onPasswordChange,
                authenticationUser = authenticationUser
            )
        }
        Row(modifier = Modifier.weight(0.12f).fillMaxSize()) {
            AuthenticationActionButton(
                onClick = { onSignUpClick(authenticationUser) },
                text = stringResource(R.string.register),
                buttonColors = ButtonColors(
                    containerColor = Color(0xFF8490B2),
                    contentColor = Color(0xFFE2E8F0),
                    disabledContentColor = Color(0xFFA0AEC0),
                    disabledContainerColor = Color(0xFFCBD5E0)
                ),
            )
        }
        Spacer(modifier = Modifier.weight(0.08f).fillMaxSize())
    }
}

@Composable
fun RegisterFields(
    modifier: Modifier,
    onEmailChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    authenticationUser: AuthenticationUser
) {
    TextField(
        value = stringResource(R.string.username),
        onValueChange = onUsernameChange,
        //placeholder = stringResource(R.string.username),
        //colors = TextFieldColors()
    )
}


@Preview
@Composable
fun SignUpScreenPreview () {
    SignUpScreenContent(AuthenticationUser(),{},{},{},{})
}