package com.example.client.ui.login

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.client.domain.model.user.CredentialUser

@Composable
fun LoginScreen (
    loginScreenViewModel: LoginScreenViewModel = hiltViewModel(),
    showSnackbar: (String) -> Unit,
    onNavigateToApp : () -> Unit
) {

}

@Composable
fun LoginScreenContent (
    credentialUser: CredentialUser,
) {

}