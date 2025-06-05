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
            if (it is UiEvent.ShowSnackbar) {
                showSnackbar(it.message)
            }
            loginScreenViewModel.handleEvent(LoginScreenEvents.EventDone)
        }
    }

    LaunchedEffect(uiState.value.isValidated) {
        if (uiState.value.isValidated)
            navigateToApp()
    }

    val oneTapClient = remember { Identity.getSignInClient(context) }

    val signInRequest = remember {
        BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId("TU_CLIENT_ID_AQUI") // poner clientId de consola Google Cloud
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
            val idToken = credential.googleIdToken
            if (idToken != null) {
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                loginScreenViewModel.signInWithGoogle(firebaseCredential)
            } else {
                showSnackbar("Google ID token is null")
            }
        } else {
            showSnackbar("Google Sign-In canceled")
        }
    }

    val onGoogleSignInClick = {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                try {
                    launcher.launch(
                        IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    )
                } catch (e: Exception) {
                    showSnackbar("Google Sign-In failed: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener { e ->
                showSnackbar("Google Sign-In failed: ${e.localizedMessage}")
            }
    }


    LoginScreenContent(
        authenticationUser = uiState.value.authenticationUser,
        onUsernameChange = { username ->
            loginScreenViewModel.handleEvent(
                LoginScreenEvents.UpdateUsername(
                    username
                )
            )
        },
        onPasswordChange = { password ->
            loginScreenViewModel.handleEvent(
                LoginScreenEvents.UpdatePassword(
                    password
                )
            )
        },
        navigateToRegister = navigateToRegister,
        onLoginClick = { loginScreenViewModel.handleEvent(LoginScreenEvents.Login(uiState.value.authenticationUser)) },
        onNavigateBack = onNavigateBack,
        biometricAvailable = biometricAvailable,
        onBiometricAuthenticate = {
            activity?.let { act ->
                val biometricPrompt = createBiometricPrompt(
                    activity = act,
                    onSuccess = {
                        loginScreenViewModel.handleEvent(LoginScreenEvents.LoginWithBiometrics)
                    },
                    onError = { errorMessage ->
                        showSnackbar(errorMessage)
                    }
                )
                biometricPrompt.authenticate(buildPromptInfo())
            } ?: showSnackbar("It's not possible to use biometric authentication in this context")
        },
        onGoogleSignInClick ={ onGoogleSignInClick}
    )

}

@Composable
fun LoginScreenContent(
    authenticationUser: AuthenticationUser,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    navigateToRegister: () -> Unit,
    onLoginClick: () -> Unit,
    onNavigateBack: () -> Unit,
    biometricAvailable: Boolean,
    onBiometricAuthenticate: () -> Unit,
    onGoogleSignInClick: () -> Unit,
) {

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.start_screen_background_big),
            contentScale = ContentScale.FillHeight,
            contentDescription = stringResource(R.string.start_Screen_background),
            modifier = Modifier.fillMaxSize()
        )
        IconButton(onClick = { onNavigateBack() }, modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.navigate_back),
                tint = Color.White
            )
        }
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
            LogoAndMessage(
                modifier = Modifier.fillMaxSize(),
                stringResource(R.string.login_message)
            )
        }
        Spacer(modifier = Modifier.weight(0.05f))
        Row(
            modifier = Modifier
                .weight(0.35f)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LoginFields(
                modifier = Modifier.fillMaxWidth(),
                onUsernameChange = onUsernameChange,
                onPasswordChange = onPasswordChange,
                authenticationUser = authenticationUser
            )
        }
        Row(
            modifier = Modifier
                .weight(0.12f)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AuthenticationActionButton(
                onClick = { onLoginClick() },
                text = stringResource(R.string.login),
                buttonColors = ButtonColors(
                    containerColor = Color(0xFF8490B2),
                    contentColor = Color(0xFFE2E8F0),
                    disabledContentColor = Color(0xFFA0AEC0),
                    disabledContainerColor = Color(0xFFCBD5E0)
                ),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        AuthenticationActionButton(
            onClick = { onGoogleSignInClick() },
            text = stringResource(R.string.sign_in_with_google),
            buttonColors = ButtonColors(
                containerColor = Color(0xFFFFFFFF), // Blanco como fondo
                contentColor = Color(0xFF4285F4),   // Azul Google
                disabledContentColor = Color.Gray,
                disabledContainerColor = Color.LightGray
            )
        )
        if (biometricAvailable) {
            Row(
                modifier = Modifier
                    .weight(0.08f)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.login_with_fingerprint),
                    color = Color(0xFF8490B2),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onBiometricAuthenticate() },
                    textDecoration = TextDecoration.Underline
                )
            }
        }
        Row(
            modifier = Modifier
                .weight(0.08f)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
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
                modifier = Modifier.clickable { navigateToRegister() },
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@Composable
fun LoginFields(
    modifier: Modifier,
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
fun LoginScreenPreview() {
    LoginScreenContent(AuthenticationUser(), {}, {}, {}, {}, {}, true, {},{})
}
