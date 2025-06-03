package com.example.client.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.client.data.firebase.auth.FirebaseAuthenticator
import com.example.client.ui.navigation.Navigation
import com.example.client.ui.splashScreen.SplashScreen
import com.example.client.ui.theme.TFGclientTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor (
    private val firebaseAuthenticator: FirebaseAuthenticator
) : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            TFGclientTheme {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashScreen(onFinished = { showSplash = false })
                } else {
                    Navigation(firebaseAuthenticator)
                }
            }
        }
    }
}



