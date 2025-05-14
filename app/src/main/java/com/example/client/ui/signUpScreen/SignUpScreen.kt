package com.example.client.ui.signUpScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SignUpScreen (
    signUpViewModel: SignUpViewModel = hiltViewModel(),
    navigateToApp: () -> Unit,
    showSnackbar: (String) -> Unit,
) {

}

@Composable
fun SignUpScreenContent () {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.weight(0.1f).fillMaxSize().background(Color.Red))
        Row(modifier = Modifier.weight(0.3f).fillMaxSize().background(Color.Blue)) {

        }
        Row(modifier = Modifier.weight(0.2f).fillMaxSize().background(Color.Green)) {

        }
        Row(modifier = Modifier.weight(0.2f).fillMaxSize().background(Color.Cyan)) {

        }
        Row(modifier = Modifier.weight(0.2f).fillMaxSize().background(Color.Magenta)) {

        }
    }
}


@Preview
@Composable
fun SignUpScreenPreview () {
    SignUpScreenContent()
}