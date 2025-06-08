package com.example.client.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.client.ui.navigation.Navigation
import com.example.client.ui.splashScreen.SplashScreen
import com.example.client.ui.theme.TFGclientTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val systemDarkTheme = isSystemInDarkTheme()
            var darkThemeEnabled by rememberSaveable { mutableStateOf(systemDarkTheme) }


            TFGclientTheme(darkTheme = darkThemeEnabled) {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashScreen(onFinished = { showSplash = false })
                } else {
                    Navigation(
                        onToggleTheme = { darkThemeEnabled = it },
                        isDarkTheme = darkThemeEnabled
                    )
                }
            }
        }
    }
}
