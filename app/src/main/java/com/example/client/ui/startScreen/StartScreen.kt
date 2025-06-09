package com.example.client.ui.startScreen

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.client.R

@SuppressLint("ContextCastToActivity")
@Composable
fun StartScreen(
    navigateToSignUp: () -> Unit,
    navigateToLogin: () -> Unit,
) {
    val context = LocalContext.current

    BackHandler {
        (context as? Activity)?.finish()
    }



    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.start_screen_background_big),
            contentScale = ContentScale.FillHeight,
            contentDescription = stringResource(R.string.start_Screen_background),
            modifier = Modifier.fillMaxSize()
        )
    }


    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.weight(0.2f))
        Row(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxWidth()
        ) {
            LogoAndSlogan(modifier = Modifier.fillMaxSize())
        }
        Spacer(modifier = Modifier.weight(0.1f))
        Row(
            modifier = Modifier
                .weight(0.2f)
                .fillMaxWidth()
        ) {
            AccessButtons(
                modifier = Modifier.fillMaxSize(),
                navigateToLogin = navigateToLogin,
                navigateToSignUp = navigateToSignUp
            )
        }
        Spacer(modifier = Modifier.weight(0.1f))
    }

}

@Composable
fun AccessButtons(
    modifier: Modifier,
    navigateToSignUp: () -> Unit,
    navigateToLogin: () -> Unit,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.5f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(0.1f)
                    .fillMaxSize()
            ) {}
            Column (
                modifier = Modifier
                    .weight(0.8f)
                    .padding(bottom = 64.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    AuthenticationActionButton(
                        onClick = { navigateToSignUp() },
                        ButtonColors(
                            containerColor = Color(0xFF8490B2),
                            contentColor = Color(0xFFE2E8F0),
                            disabledContentColor = Color(0xFFA0AEC0),
                            disabledContainerColor = Color(0xFFCBD5E0)
                        ), stringResource(R.string.register)
                    )
                }
                Row{
                    AuthenticationActionButton(
                        onClick = { navigateToLogin() },
                        ButtonColors(
                            containerColor = Color(0xFF8490B2),
                            contentColor = Color(0xFFE2E8F0),
                            disabledContentColor = Color(0xFFA0AEC0),
                            disabledContainerColor = Color(0xFFCBD5E0)
                        ),
                        stringResource(R.string.login)
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(0.1f)
                    .fillMaxSize()
            ) {}
        }
    }
}

@Composable
fun  AuthenticationActionButton(onClick: () -> Unit, buttonColors: ButtonColors, text: String) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = buttonColors
    ) {
        Text(text = text)
    }
}
@Composable
fun LogoAndSlogan(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxSize(), horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.app_logo_v1),
                contentDescription = stringResource(R.string.app_logo)
            )
        }
        Row(
            modifier = Modifier
                .weight(0.2f)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.app_real_name),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Row(
            modifier = Modifier
                .weight(0.2f)
                .fillMaxSize(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.app_slogan),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.8f)
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Preview

@Composable
fun StartScreenPreview() {
    StartScreen(navigateToLogin = {}, navigateToSignUp = {})
}