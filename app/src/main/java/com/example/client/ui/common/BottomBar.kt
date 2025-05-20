package com.example.client.ui.common

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.client.ui.navigation.AppDestination
import com.example.client.ui.navigation.AppMainBottomDestination


@Composable
fun BottomBar(
    navController: NavController,
    screens: List<AppDestination>,
    isVisible: Boolean,
) {
    val blue = Color(0xFF1565C0)
    val white = Color.White
    if (isVisible)
        NavigationBar(
            containerColor = blue,
            contentColor = white
        ) {
            val state = navController.currentBackStackEntryAsState()
            val currentDestination = state.value?.destination
            screens.forEach { screen ->
                if (screen is AppMainBottomDestination) {
                    val selected = currentDestination?.route?.substringBefore("/") == screen.route.toString().substringBefore("@").substringBefore("$")
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (selected) screen.iconFilled else screen.icon,
                                contentDescription = null,
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = white,
                            selectedTextColor = white,
                            indicatorColor = white.copy(alpha = 0.2f),
                            unselectedIconColor = white.copy(alpha = 0.6f),
                            unselectedTextColor = white.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
}
