package com.example.client.ui.login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.client.BuildConfig
import com.example.client.R
import com.example.client.domain.model.user.AuthenticationUser
import com.example.client.domain.useCases.authentication.buildPromptInfo
import com.example.client.domain.useCases.authentication.createBiometricPrompt
import com.example.client.domain.useCases.authentication.isBiometricAvailable
import com.example.client.ui.common.UiEvent
import com.example.client.ui.registerScreen.LogoAndMessage
import com.example.client.ui.startScreen.AuthenticationActionButton
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn



@Composable
fun LoginScreen(
    loginScreenViewModel: LoginScreenViewModel = hiltViewModel(),
    showSnackbar: (String) -> Unit,
    navigateToApp: () -> Unit,
    navigateToRegister: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val uiState = loginScreenViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    val biometricAvailable = remember { isBiometricAvailable(context) }

    LaunchedEffect(uiState.value.event) {
        uiState.value.event?.let {
            if (it is UiEvent.ShowSnackbar) showSnackbar(it.message)
            loginScreenViewModel.handleEvent(LoginScreenEvents.EventDone)
        }
    }


    LaunchedEffect(uiState.value.isValidated) {
        if (uiState.value.isValidated) navigateToApp()
    }


    val oneTapClient = remember { Identity.getSignInClient(context) }

    val signInRequest = remember {
        BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken
                if (idToken != null) {
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    loginScreenViewModel.signInWithGoogle(firebaseCredential)
                } else {
                    showSnackbar("No se obtuvo el ID token de Google.")
                }
            } catch (e: ApiException) {
                showSnackbar("Error al obtener las credenciales: ${e.localizedMessage}")
            }
        } else {
            showSnackbar("Inicio con Google cancelado.")
        }
    }

    val onGoogleSignInClick = {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                try {
                    launcher.launch(IntentSenderRequest.Builder(result.pendingIntent).build())
                } catch (e: Exception) {
                    showSnackbar("Fallo al lanzar One Tap: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener {
                showSnackbar("No se pudo iniciar sesión con Google: ${it.localizedMessage}")
            }
    }

    // === Render UI ===
    LoginScreenContent(
        authenticationUser = uiState.value.authenticationUser,
        onUsernameChange = { loginScreenViewModel.handleEvent(LoginScreenEvents.UpdateUsername(it)) },
        onPasswordChange = { loginScreenViewModel.handleEvent(LoginScreenEvents.UpdatePassword(it)) },
        onLoginClick = { loginScreenViewModel.handleEvent(LoginScreenEvents.Login(uiState.value.authenticationUser)) },
        navigateToRegister = navigateToRegister,
        onNavigateBack = onNavigateBack,
        biometricAvailable = biometricAvailable,
        onBiometricAuthenticate = {
            activity?.let {
                val biometricPrompt = createBiometricPrompt(
                    activity = it,
                    onSuccess = {
                        loginScreenViewModel.handleEvent(LoginScreenEvents.LoginWithBiometrics)
                    },
                    onError = { error -> showSnackbar(error) }
                )
                biometricPrompt.authenticate(buildPromptInfo())
            } ?: showSnackbar("Biometría no disponible.")
        },
        onGoogleSignInClick = {onGoogleSignInClick()}
    )
}


@Composable
fun LoginScreenContent(
    authenticationUser: AuthenticationUser,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    navigateToRegister: () -> Unit,
    onNavigateBack: () -> Unit,
    biometricAvailable: Boolean,
    onBiometricAuthenticate: () -> Unit,
    onGoogleSignInClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.start_screen_background_big),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.navigate_back),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LogoAndMessage(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                message = stringResource(R.string.login_message)
            )

            LoginFields(
                authenticationUser = authenticationUser,
                onUsernameChange = onUsernameChange,
                onPasswordChange = onPasswordChange
            )

            Spacer(modifier = Modifier.height(24.dp))

            AuthenticationActionButton(
                onClick = onLoginClick,
                text = stringResource(R.string.login),
                buttonColors = ButtonColors(
                    containerColor = Color(0xFF8490B2),
                    contentColor = Color(0xFFE2E8F0),
                    disabledContentColor = Color(0xFFA0AEC0),
                    disabledContainerColor = Color(0xFFCBD5E0)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            AuthenticationActionButton(
                onClick = onGoogleSignInClick,
                text = stringResource(R.string.sign_in_with_google),
                buttonColors = ButtonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF4285F4),
                    disabledContentColor = Color.Gray,
                    disabledContainerColor = Color.LightGray
                )
            )

            if (biometricAvailable) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.login_with_fingerprint),
                    color = Color(0xFF8490B2),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onBiometricAuthenticate),
                    textDecoration = TextDecoration.Underline
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.dont_have_account),
                    color = Color.White,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.register),
                    color = Color(0xFF8490B2),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = navigateToRegister),
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}

@Composable
fun LoginFields(
    authenticationUser: AuthenticationUser,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = authenticationUser.username,
            onValueChange = onUsernameChange,
            label = { Text(stringResource(R.string.username)) },
            placeholder = { Text(stringResource(R.string.username)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
            colors = loginFieldColors()
        )

        OutlinedTextField(
            value = authenticationUser.password,
            onValueChange = onPasswordChange,
            label = { Text(stringResource(R.string.password)) },
            placeholder = { Text(stringResource(R.string.password)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth(),
            colors = loginFieldColors()
        )
    }
}

@Composable
private fun loginFieldColors() = TextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = Color.White,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedIndicatorColor = Color.White,
    unfocusedIndicatorColor = Color(0xFF8490B2),
    focusedLabelColor = Color.White,
    unfocusedLabelColor = Color(0xFF8490B2),
    focusedPlaceholderColor = Color(0xFF8490B2),
    unfocusedPlaceholderColor = Color(0xFF8490B2)


)



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreenContent(AuthenticationUser(), {}, {}, {}, {}, {}, true, {},{})
}
