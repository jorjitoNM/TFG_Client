package com.example.client.ui.registerScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.client.R
import com.example.client.domain.model.user.AuthenticationUser
import com.example.client.ui.common.UiEvent
import com.example.client.ui.startScreen.AuthenticationActionButton

@Composable
fun SignUpScreen (
    registerViewModel: RegisterViewModel = hiltViewModel(),
    navigateToApp: () -> Unit,
    showSnackbar: (String) -> Unit,
    navigateToLogin: () -> Unit,
) {
    val uiState = registerViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.value.event) {
        uiState.value.event?.let {
            if (it is UiEvent.ShowSnackbar) {
                showSnackbar(it.message)
            }
            registerViewModel.handleEvent(RegisterEvents.EventDone)
        }
    }

    LaunchedEffect(uiState.value.isValidated) {
        if (uiState.value.isValidated)
            navigateToApp()
    }

    SignUpScreenContent(
        authenticationUser = uiState.value.authenticationUser,
        onSignUpClick = { registerViewModel.handleEvent(RegisterEvents.Register(uiState.value.authenticationUser)) },
        onEmailChange = { email -> registerViewModel.handleEvent(RegisterEvents.UpdateEmail(email)) },
        onUsernameChange = { username -> registerViewModel.handleEvent(RegisterEvents.UpdateUsername(username)) },
        onPasswordChange = { password -> registerViewModel.handleEvent(RegisterEvents.UpdatePassword(password)) },
        navigateToLogin = navigateToLogin
    )
}

@Composable
fun SignUpScreenContent(
    authenticationUser: AuthenticationUser,
    onSignUpClick: () -> Unit,
    onEmailChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    navigateToLogin: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.start_screen_background_big),
            contentScale = ContentScale.FillHeight,
            contentDescription = stringResource(R.string.start_Screen_background),
            modifier = Modifier.fillMaxSize()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.1f))
        Row(
            modifier = Modifier
                .weight(0.3f)
                .fillMaxWidth()
        ) {
            LogoAndMessage(modifier = Modifier.fillMaxSize(), stringResource(R.string.register_message))
        }
        Spacer(modifier = Modifier.weight(0.05f))
        Row(
            modifier = Modifier
                .weight(0.35f)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            RegisterFields(
                modifier = Modifier.fillMaxWidth(),
                onEmailChange = onEmailChange,
                onUsernameChange = onUsernameChange,
                onPasswordChange = onPasswordChange,
                authenticationUser = authenticationUser
            )
        }
        Row(modifier = Modifier
            .weight(0.12f)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {
            AuthenticationActionButton(
                onClick = { onSignUpClick() },
                text = stringResource(R.string.register),
                buttonColors = ButtonColors(
                    containerColor = Color(0xFF8490B2),
                    contentColor = Color(0xFFE2E8F0),
                    disabledContentColor = Color(0xFFA0AEC0),
                    disabledContainerColor = Color(0xFFCBD5E0)
                ),
            )
        }
        Row(
            modifier = Modifier
                .weight(0.08f)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.already_have_account),
                color = Color.White,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.login),
                color = Color(0xFF8490B2),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { navigateToLogin() },
                textDecoration = TextDecoration.Underline
            )
    }
}
}

@Composable
fun LogoAndMessage (modifier: Modifier = Modifier, message : String) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxSize(), horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.app_logo_v1),
                contentDescription = stringResource(R.string.app_logo)
            )
        }

        Row(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxSize(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.8f)
                ),
                textAlign = TextAlign.Center
            )
        }
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
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = authenticationUser.username,
            onValueChange = onUsernameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.username), color = Color(0xFF8490B2)) },
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color(0xFF8490B2),
                focusedPlaceholderColor = Color(0xFF8490B2),
                unfocusedPlaceholderColor = Color(0xFF8490B2),
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color(0xFF8490B2)
            ),
            placeholder = { Text(stringResource(R.string.username), color = Color(0xFF8490B2)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )

        OutlinedTextField(
            value = authenticationUser.email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.email), color = Color(0xFF8490B2)) },
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color(0xFF8490B2),
                focusedPlaceholderColor = Color(0xFF8490B2),
                unfocusedPlaceholderColor = Color(0xFF8490B2),
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color(0xFF8490B2)
            ),
            placeholder = { Text(stringResource(R.string.email), color = Color(0xFF8490B2)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        OutlinedTextField(
            value = authenticationUser.password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.password), color = Color(0xFF8490B2)) },
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color(0xFF8490B2),
                focusedPlaceholderColor = Color(0xFF8490B2),
                unfocusedPlaceholderColor = Color(0xFF8490B2),
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color(0xFF8490B2)
            ),
            placeholder = { Text(stringResource(R.string.password), color = Color(0xFF8490B2)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            visualTransformation = PasswordVisualTransformation()
        )
    }
}


@Preview
@Composable
fun SignUpScreenPreview () {
    SignUpScreenContent(AuthenticationUser(),{},{},{},{},{})
}